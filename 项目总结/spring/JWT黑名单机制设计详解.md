# JWT 黑名单机制设计详解

---

## 一、业务背景与需求分析

### 1.1 问题场景

在使用 JWT（JSON Web Token）进行身份认证时，存在一个天然的缺陷：**JWT 本身是无状态的，一旦签发就无法主动撤销**。

**典型问题场景：**

| 场景 | 问题描述 | 影响范围 |
|------|----------|----------|
| **用户主动登出** | 用户点击"退出登录"，但旧 token 仍然有效 | 用户隐私安全 |
| **密码泄露/账户被盗** | 攻击者获取到 token 后可持续访问系统 | 账户安全 |
| **权限变更** | 用户权限被降级，但旧 token 仍保留原有权限 | 数据安全 |
| **多端登录控制** | 用户在新设备登录，希望旧设备失效 | 用户体验 |

### 1.2 业务需求

基于以上场景，我们需要实现一个 **token 黑名单机制**，具备以下特性：

- **实时生效**：登出后立即禁止使用旧 token
- **高性能**：不影响正常请求的响应速度
- **自动清理**：过期 token 自动从黑名单中移除
- **分布式支持**：多实例部署时黑名单数据共享

---

## 二、技术方案设计

### 2.1 核心设计思路

**方案对比：**

| 方案 | 实现方式 | 优点 | 缺点 |
|------|----------|------|------|
| **状态化存储** | 在 Redis 中维护黑名单 | 实时生效、支持分布式 | 需要 Redis 依赖 |
| **短有效期 + 刷新** | 设置短 token 有效期，频繁刷新 | 无状态、简单 | 用户体验差、刷新复杂度高 |
| **数据库查询** | 每次请求查询数据库 | 数据持久化 | 性能差、不适合高并发 |

**选择：状态化存储（Redis）**

### 2.2 架构设计

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        请求处理流程                                   │
└─────────────────────────────────────────────────────────────────────────┘

                    用户请求
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                     JwtAuthenticationFilter                           │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐   │
│  │ 提取 Token  │──▶│ 验证签名    │──▶│ 检查黑名单（Redis）       │   │
│  └─────────────┘    └─────────────┘    └─────────────────────────┘   │
│                                                      │               │
│                                        ┌─────────────┴─────────────┐ │
│                                        ▼                           ▼ │
│                                   在黑名单                     不在黑名单   │
│                                        │                           │   │
│                                        ▼                           ▼   │
│                                返回 402 错误                  继续处理请求   │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.3 数据结构设计

**Redis Key 设计：**

```
moodnote:auth:blacklist:{token}
```

| 组成部分 | 说明 | 示例 |
|----------|------|------|
| `moodnote` | 项目前缀 | 避免 key 冲突 |
| `auth` | 模块名 | 认证模块 |
| `blacklist` | 功能标识 | 黑名单 |
| `{token}` | JWT Token 值 | 唯一标识 |

**Value 设计：**

```java
redisUtil.set(blackKey, "1", expireTime, TimeUnit.HOURS);
```

- **值内容**：`"1"`（占位符，表示存在）
- **过期时间**：与 JWT 过期时间一致（如 24 小时）

**设计意图：**
- 只关心 token 是否存在，不关心具体值
- 简单的字符串值占用内存极少
- Redis 自动清理过期 key，无需手动维护

---

## 三、核心代码实现

### 3.1 登出时加入黑名单

**AuthServiceImpl.java - logout 方法：**

```java
@Override
public Result<Void> logout() {
    // 1. 从请求中提取 token
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
        return Result.error(MessageConstant.LOGOUT_FAILED);
    }
    
    String token = extractTokenFromRequest(attributes.getRequest());
    if (token == null) {
        return Result.error(MessageConstant.UNAUTHORIZED);
    }
    
    try {
        // 2. 将 token 加入黑名单
        String blackKey = RedisKeyConstant.getBlacklistKey(token);
        redisUtil.set(blackKey, "1", DataConstant.TOKEN_EXPIRE_TIME, TimeUnit.HOURS);
        
        // 3. 删除用户的 token 记录（可选，用于多端控制）
        Long userId = jwtTokenUtil.extractUserId(token);
        if (userId != null) {
            redisUtil.delete(RedisKeyConstant.getUserTokenKey(userId));
        }
        
        log.info("用户退出登录成功，userId: {}", userId);
        return Result.success(MessageConstant.LOGOUT_SUCCESS);
        
    } catch (Exception e) {
        log.error("退出登录失败: {}", e.getMessage());
        return Result.error(MessageConstant.LOGOUT_FAILED);
    }
}
```

### 3.2 请求时检查黑名单

**JwtAuthenticationFilter.java - doFilter 方法：**

```java
@Override
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    
    // ... 白名单检查、OPTIONS 处理 ...
    
    // 获取 token
    String token = extractToken(request);
    if (token == null) {
        sendUnauthorizedResponse(response, MessageConstant.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
        return;
    }
    
    try {
        // 验证 token 有效性
        if (!jwtTokenUtil.validateToken(token)) {
            sendUnauthorizedResponse(response, MessageConstant.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
            return;
        }
        
        // 检查黑名单（核心逻辑）
        String blackKey = RedisKeyConstant.getBlacklistKey(token);
        if (Boolean.TRUE.equals(redisUtil.hasKey(blackKey))) {
            sendUnauthorizedResponse(response, MessageConstant.TOKEN_BLACKLIST, ErrorCode.TOKEN_BLACKLIST);
            return;
        }
        
        // 用户信息存入请求属性
        String userName = jwtTokenUtil.extractUsername(token);
        Long userId = jwtTokenUtil.extractUserId(token);
        request.setAttribute("userName", userName);
        request.setAttribute("userId", userId);
        
        chain.doFilter(request, response);
        
    } catch (Exception e) {
        log.error("Token 验证异常: {}", e.getMessage());
        sendUnauthorizedResponse(response, MessageConstant.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
    }
}
```

### 3.3 Redis Key 常量定义

**RedisKeyConstant.java：**

```java
public class RedisKeyConstant {
    
    private static final String PROJECT_PREFIX = "moodnote";
    private static final String AUTH_MODULE = "auth";
    private static final String BLACKLIST = "blacklist";
    
    /**
     * 获取黑名单 key
     * @param token JWT token
     * @return moodnote:auth:blacklist:{token}
     */
    public static String getBlacklistKey(String token) {
        return PROJECT_PREFIX + ":" + AUTH_MODULE + ":" + BLACKLIST + ":" + token;
    }
    
    /**
     * 获取用户 token 记录 key
     * @param userId 用户 ID
     * @return moodnote:auth:token:{userId}
     */
    public static String getUserTokenKey(Long userId) {
        return PROJECT_PREFIX + ":" + AUTH_MODULE + ":token:" + userId;
    }
}
```

---

## 四、业务场景分析

### 4.1 用户登出流程

```
用户操作          后端处理                      Redis 变化
─────────────────────────────────────────────────────────────────
点击退出登录    → 提取请求中的 token            ────────────────
               → 生成黑名单 key                ────────────────
               → redis.set(blackKey, "1")      key: moodnote:auth:blacklist:xxx
                                               value: "1"
                                               ttl: 24h
               → 删除用户 token 记录           删除 key: moodnote:auth:token:{userId}
               → 返回成功响应                  ────────────────
```

### 4.2 禁用旧 token 验证流程

```
请求发起          Filter 处理                   处理结果
─────────────────────────────────────────────────────────────────
携带旧 token    → 提取 token                   ────────────────
               → 验证 JWT 签名（通过）          ────────────────
               → 检查黑名单（命中）             ────────────────
               → 返回 402 错误                 {"code":402,"message":"用户已登出，请重新登录"}
```

### 4.3 多端登录控制

**场景**：用户在手机和电脑同时登录，希望登出后两端都失效

**实现策略**：

```java
// 登录时存储用户的当前 token
redisUtil.set(RedisKeyConstant.getUserTokenKey(userId), token, expireTime, TimeUnit.HOURS);

// 登出时删除用户的 token 记录（可选）
redisUtil.delete(RedisKeyConstant.getUserTokenKey(userId));
```

**优点**：
- 可通过 `userId` 查询用户当前的 token
- 支持强制下线（删除 `token:{userId}` 后，用户需重新登录）

---

## 五、性能优化与注意事项

### 5.1 性能考虑

| 优化点 | 说明 | 实现方式 |
|--------|------|----------|
| **内存占用** | 黑名单只存储 key，value 为短字符串 | 使用 `"1"` 作为占位符 |
| **过期策略** | 自动清理过期记录，避免内存泄漏 | 设置与 JWT 相同的过期时间 |
| **查询效率** | `hasKey` 是 O(1) 操作 | Redis 的核心优势 |
| **批量操作** | 避免频繁操作 Redis | 单次请求只做一次 `hasKey` 查询 |

### 5.2 安全注意事项

| 风险点 | 风险描述 | 解决方案 |
|--------|----------|----------|
| **Token 泄露** | token 被截获后仍可使用 | HTTPS + 短有效期 + 黑名单 |
| **Redis 宕机** | 黑名单不可用 | 配置 Redis 哨兵/集群 |
| **内存溢出** | 黑名单过多导致内存不足 | 设置合理过期时间 + 监控告警 |
| **并发问题** | 同时登出多次可能出现数据不一致 | Redis 单线程特性保证原子性 |

### 5.3 监控与运维

**监控指标**：

| 指标 | 说明 | 告警阈值 |
|------|------|----------|
| **黑名单大小** | 当前黑名单记录数 | > 10000 |
| **查询命中率** | 请求命中黑名单的比例 | > 10% |
| **Redis 内存** | Redis 内存使用量 | > 80% |
| **响应时间** | 黑名单查询耗时 | > 10ms |

---

## 六、总结

### 6.1 设计要点

1. **核心思想**：通过 Redis 存储已失效的 token，实现实时禁用
2. **数据结构**：`moodnote:auth:blacklist:{token} → "1"`
3. **过期策略**：与 JWT 过期时间一致，自动清理
4. **查询方式**：使用 `hasKey` 判断存在性，O(1) 复杂度

### 6.2 适用场景

- 用户主动登出
- 密码重置后禁用旧 token
- 权限变更时强制重新认证
- 多端登录控制

### 6.3 扩展建议

- **分布式部署**：使用 Redis 集群保证高可用
- **多租户场景**：在 key 中加入租户 ID
- **审计日志**：记录 token 加入黑名单的时间和原因
- **定期清理**：定时任务清理过期记录（可选，Redis 会自动清理）

---

**参考资料**：
- [JWT 官方文档](https://jwt.io/)
- [Redis 官方文档](https://redis.io/)
- [Spring Security JWT 集成指南](https://spring.io/projects/spring-security)
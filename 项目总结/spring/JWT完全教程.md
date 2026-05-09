# JWT 完全教程

## 一、JWT 概述

### 1.1 什么是 JWT

JWT（JSON Web Token）是一个开放标准（[RFC 7519](https://tools.ietf.org/html/rfc7519)），它定义了一种紧凑且自包含的方式，使用 JSON 对象在各方之间安全地传输信息。此信息可以被验证和信任，因为它是数字签名的。

**JWT 特点：**

- **紧凑（Compact）**：体积小，可通过 URL、POST 参数或 HTTP 头部发送
- **自包含（Self-contained）**：包含所有必要的用户信息，无需查询数据库
- **可验证**：使用数字签名确保数据完整性

---

### 1.2 JWT 的应用场景

| 场景 | 说明 |
|------|------|
| **授权（Authorization）** | 最常见场景，用户登录后，后续请求携带 JWT 访问资源 |
| **信息交换（Information Exchange）** | 安全地在各方之间传输信息，可验证发送者身份 |
| **单点登录（SSO）** | 在不同域之间实现单点登录 |

---

## 二、认证方式对比

### 2.1 Cookie + Session 认证

**工作流程：**

```
1. 用户登录 → 服务器创建 Session → Session 存入服务器 → Session ID 存入 Cookie
2. 用户请求 → 携带 Cookie（含 Session ID） → 服务器查询 Session → 验证身份
```

**优点：**
- 传统成熟，易于理解
- 服务器端可随时注销会话

**缺点：**
- 服务器需要存储 Session，占用内存
- 分布式部署需要 Session 共享
- 移动端适配困难
- CSRF 攻击风险

---

### 2.2 Token 认证

**工作流程：**

```
1. 用户登录 → 服务器生成 Token → 返回给客户端
2. 用户请求 → 携带 Token（通常在 Authorization Header）→ 服务器验证 Token → 验证身份
```

**优点：**
- 无状态，服务器无需存储
- 易于扩展，支持分布式
- 移动端友好
- 天然防止 CSRF

**缺点：**
- Token 无法主动撤销（除非使用黑名单）
- Token 包含敏感信息需加密（若使用 JWE）

---

### 2.3 三种方式对比

| 特性 | Cookie + Session | Token | JWT |
|------|-----------------|-------|-----|
| 服务器存储 | 需要 | 不需要 | 不需要 |
| 扩展性 | 差（需 Session 共享） | 好 | 好 |
| 移动端支持 | 一般 | 好 | 好 |
| 信息自包含 | 否 | 可选 | 是 |
| 安全性 | 中等 | 中等 | 高（签名） |
| CSRF 防护 | 需要 | 不需要 | 不需要 |

---

## 三、JWT 结构详解

JWT 由三个部分组成，用点（`.`）分隔：

```
xxxxx.yyyyy.zzzzz
│    │     │
│    │     └─ Signature（签名）
│    └─────── Payload（有效载荷）
└──────────── Header（头部）
```

### 3.1 Header（头部）

头部通常包含两部分：
- `typ`：令牌类型，通常为 `JWT`
- `alg`：签名算法，如 `HS256`、`RS256`

**示例：**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

然后这个 JSON 会被 **Base64Url** 编码，形成 JWT 的第一部分。

---

### 3.2 Payload（有效载荷）

有效载荷包含声明（Claims），声明是关于实体（通常是用户）和附加数据的声明。

#### 声明类型

| 类型 | 说明 | 示例 |
|------|------|------|
| **注册声明（Registered）** | 预定义声明，推荐使用 | `iss`, `exp`, `sub`, `aud` |
| **公共声明（Public）** | 可自定义，但需在 IANA 注册 | 自定义字段 |
| **私有声明（Private）** | 自定义声明，仅在双方间使用 | `userId`, `role` |

#### 常用注册声明

| 声明 | 全称 | 说明 |
|------|------|------|
| `iss` | Issuer | 签发者 |
| `exp` | Expiration Time | 过期时间 |
| `sub` | Subject | 主题（通常是用户 ID） |
| `aud` | Audience | 受众 |
| `nbf` | Not Before | 生效时间 |
| `iat` | Issued At | 签发时间 |
| `jti` | JWT ID | JWT 唯一标识 |

**示例：**
```json
{
  "sub": "1234567890",
  "username": "john_doe",
  "role": "USER",
  "iat": 1516239022,
  "exp": 1516242622
}
```

这个 JSON 同样会被 **Base64Url** 编码，形成 JWT 的第二部分。

⚠️ **重要**：Header 和 Payload 只是 Base64 编码，不是加密！任何人都可以解码查看内容，因此**不要在 JWT 中存放敏感信息**（如密码）。

---

### 3.3 Signature（签名）

签名用于验证消息在传输过程中没有被篡改，并且验证发送者身份。

**签名创建过程：**

```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

**签名的作用：**
- 验证消息未被篡改
- 验证发送者身份（使用私钥签名时）

---

### 3.4 完整 JWT 示例

将三部分组合在一起：

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

你可以使用 [jwt.io](https://jwt.io/#debugger-io) 来解码、验证和生成 JWT。

---

## 四、JWT 工作流程

### 4.1 认证流程

```
┌─────────────────────────────────────────────────────────────┐
│                     JWT 认证流程                             │
└─────────────────────────────────────────────────────────────┘

1. 用户登录
   ┌─────────────┐         ┌─────────────┐
   │   客户端    │────────>│   服务器    │
   │  (username)│  请求   │  (验证用户)  │
   │  password) │         └──────┬──────┘
   └─────────────┘                │
                                  │ 验证成功
                                  │
2. 生成 JWT
                                  │
                         ┌─────────┴─────────┐
                         │  Header           │
                         │  Payload          │
                         │  Signature        │
                         └─────────┬─────────┘
                                  │ JWT
                                  ▼
   ┌─────────────┐         ┌─────────────┐
   │   客户端    │<────────│   服务器    │
   │  (存储 JWT) │  响应   │  (返回 JWT) │
   └─────────────┘         └─────────────┘

3. 请求受保护资源
   ┌─────────────┐         ┌─────────────┐
   │   客户端    │────────>│   服务器    │
   │  (携带 JWT)│  请求   │  (验证 JWT)  │
   └─────────────┘         └──────┬──────┘
                                  │
                                  │ 验证通过
                                  │
   ┌─────────────┐         ┌─────────────┐
   │   客户端    │<────────│   服务器    │
   │  (获取资源) │  响应   │  (返回资源)  │
   └─────────────┘         └─────────────┘
```

---

### 4.2 JWT 的存储位置

| 存储位置 | 优点 | 缺点 | 安全性 |
|---------|------|------|--------|
| **LocalStorage** | 可跨标签页，容量大 | XSS 攻击可窃取 | 中等 |
| **SessionStorage** | 仅当前标签页有效 | XSS 攻击可窃取 | 中等 |
| **HttpOnly Cookie** | 防止 XSS 窃取 | 需防 CSRF | 高 |
| **内存变量** | 最安全 | 刷新页面丢失 | 最高 |

**推荐方案：**
- Web 端：使用 HttpOnly Cookie + SameSite 属性
- 移动端：存储在安全存储区域（如 iOS Keychain、Android Keystore）

---

### 4.3 JWT 的传输方式

**方式一：Authorization Header（推荐）**

```http
GET /api/user HTTP/1.1
Host: example.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**方式二：Cookie**

```http
GET /api/user HTTP/1.1
Host: example.com
Cookie: jwt_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 五、在 Spring Boot 中使用 JWT

### 5.1 项目集成

#### 添加依赖

项目已集成 JJWT（Java JWT）库：

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

### 5.2 JWT 工具类实现

```java
package com.moodnote.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成 JWT Token
     */
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, username);
    }

    /**
     * 创建 Token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 从 Token 中提取用户名
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * 从 Token 中提取用户 ID
     */
    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 提取过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    /**
     * 提取 Claims
     */
    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 Token 是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 验证 Token 是否有效
     */
    public Boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

---

### 5.3 JWT 认证过滤器

```java
package com.moodnote.filter;

import com.moodnote.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 从请求头获取 Token
        String authHeader = request.getHeader(AUTH_HEADER);
        
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            token = authHeader.substring(TOKEN_PREFIX.length());
            try {
                username = jwtTokenUtil.extractUsername(token);
            } catch (Exception e) {
                logger.warn("Token 解析失败: " + e.getMessage());
            }
        }

        // 验证 Token 并设置认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtTokenUtil.validateToken(token, username)) {
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

### 5.4 Spring Security 配置

```java
package com.moodnote.config;

import com.moodnote.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（JWT 不需要）
            .csrf(AbstractHttpConfigurer::disable)
            
            // 无状态会话
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  // 认证接口放行
                .requestMatchers("/swagger-ui/**").permitAll() // Swagger 放行
                .requestMatchers("/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()  // 其他请求需要认证
            )
            
            // 添加 JWT 过滤器
            .addFilterBefore(
                jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

### 5.5 配置文件

```yaml
# application.yml
jwt:
  secret: your-256-bit-secret-key-at-least-32-characters-long
  expiration: 86400000  # 24 小时（毫秒）
  header: Authorization
  prefix: Bearer
```

---

### 5.6 登录接口实现

```java
package com.moodnote.controller;

import com.moodnote.dto.LoginDTO;
import com.moodnote.service.AuthService;
import com.moodnote.util.JwtTokenUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginDTO loginDTO) {
        // 1. 验证用户名密码
        authService.login(loginDTO);
        
        // 2. 生成 JWT Token
        Long userId = 123L; // 从数据库获取
        String token = jwtTokenUtil.generateToken(loginDTO.getUsername(), userId);
        
        // 3. 返回 Token
        return Result.success(new LoginResponse(token, "Bearer"));
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String tokenType;

        public LoginResponse(String token, String tokenType) {
            this.token = token;
            this.tokenType = tokenType;
        }
    }
}
```

---

## 六、JWT 最佳实践

### 6.1 安全建议

| 建议 | 说明 |
|------|------|
| **使用强密钥** | HS256 至少 256 位（32 字符） |
| **设置过期时间** | exp 声明必须设置，建议短期 Token |
| **使用 HTTPS** | 防止 Token 在传输中被窃取 |
| **不要存敏感信息** | Header 和 Payload 只是 Base64 编码 |
| **验证签名** | 不要只解码，必须验证签名 |
| **考虑 Token 刷新** | 使用刷新令牌机制（Refresh Token） |

---

### 6.2 刷新令牌机制

为了解决 JWT 无法主动撤销的问题，可以使用双令牌机制：

```
Access Token（访问令牌）：短期有效（如 15 分钟），用于 API 访问
Refresh Token（刷新令牌）：长期有效（如 7 天），用于刷新 Access Token
```

**流程：**

```
1. 用户登录 → 返回 Access Token + Refresh Token
2. Access Token 过期 → 使用 Refresh Token 获取新的 Access Token
3. Refresh Token 过期 → 需要重新登录
```

**优点：**
- 提高安全性（Access Token 短期有效）
- 减少用户登录频率

---

### 6.3 Token 撤销方案

虽然 JWT 是无状态的，但某些场景需要主动撤销 Token：

| 方案 | 实现方式 | 优点 | 缺点 |
|------|---------|------|------|
| **黑名单机制** | 将撤销的 Token 存入 Redis，请求时检查 | 简单易实现 | 需要存储 |
| **修改用户密钥** | 重新签发密钥，旧 Token 全部失效 | 彻底 | 影响其他用户 |
| **缩短有效期** | 使用短期 Token + Refresh Token | 无需存储 | 用户体验稍差 |

---

## 七、JWT 在项目中的作用和地位

### 7.1 在 MoodNote 项目中的角色

```
┌─────────────────────────────────────────────────────────────┐
│                    MoodNote 认证架构                        │
└─────────────────────────────────────────────────────────────┘

                    ┌───────────────┐
                    │   前端应用    │
                    └───────┬───────┘
                            │
                    ┌───────▼───────┐
                    │   登录/注册   │
                    └───────┬───────┘
                            │
                    ┌───────▼───────┐
                    │  验证凭证     │
                    └───────┬───────┘
                            │
                    ┌───────▼───────┐
                    │  生成 JWT    │
                    └───────┬───────┘
                            │
                    ┌───────▼───────┐
                    │  返回 Token  │
                    └───────┬───────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
   ┌────▼────┐        ┌─────▼─────┐      ┌─────▼─────┐
   │ 访问笔记 │        │ 分享笔记  │      │ 用户设置  │
   └────┬────┘        └─────┬─────┘      └─────┬─────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                    ┌───────▼───────┐
                    │ JWT 过滤器    │
                    └───────┬───────┘
                            │
                    ┌───────▼───────┐
                    │  验证 Token   │
                    └───────┬───────┘
                            │
                    ┌───────▼───────┐
                    │  执行业务逻辑  │
                    └───────────────┘
```

---

### 7.2 JWT 的优势在项目中的体现

| 优势 | MoodNote 项目中的应用 |
|------|----------------------|
| **无状态** | 支持多实例部署，无需 Session 共享 |
| **移动端友好** | 支持手机 App、小程序等多端登录 |
| **扩展性好** | 未来可支持 SSO 单点登录 |
| **性能优化** | 减少数据库查询（无需查询 Session） |

---

## 八、常见问题

### 8.1 JWT 相关问题

**Q1: JWT 和 Session 哪个更好？**

A: 取决于场景：
- 传统 Web 应用 → Session 足够
- 前后端分离、微服务、移动端 → JWT 更合适

**Q2: JWT 可以被篡改吗？**

A: 不能。因为签名是基于 Header + Payload + 密钥生成的，篡改任何部分都会导致签名验证失败。

**Q3: JWT 被盗用怎么办？**

A: 
- 使用 HTTPS 防止传输中被窃取
- 设置合理的过期时间
- 考虑使用 Refresh Token 机制
- 检测异常行为（如 IP 变化）

---

### 8.2 开发常见问题

**Q1: Token 解析失败**

```java
// 常见错误：签名验证失败
io.jsonwebtoken.SignatureException: JWT signature does not match locally computed signature
```

**解决方案：**
- 检查密钥是否一致
- 确认算法是否匹配
- 验证 Token 是否完整

**Q2: Token 已过期**

```java
io.jsonwebtoken.ExpiredJwtException: JWT expired at 2024-01-01T00:00:00Z
```

**解决方案：**
- 实现刷新令牌机制
- 前端检测到过期自动刷新

---

## 九、总结

### 9.1 核心要点回顾

1. **JWT 结构**：Header + Payload + Signature
2. **认证方式**：无状态，服务器无需存储
3. **安全事项**：签名验证、过期时间、HTTPS
4. **最佳实践**：双令牌机制、合理设置有效期
5. **项目集成**：配合 Spring Security 使用

### 9.2 学习资源

| 资源 | 链接 |
|------|------|
| JWT 官方网站 | [jwt.io](https://jwt.io) |
| RFC 7519 规范 | [tools.ietf.org/html/rfc7519](https://tools.ietf.org/html/rfc7519) |
| JJWT GitHub | [github.com/jwtk/jjwt](https://github.com/jwtk/jjwt) |

---

**更新时间**：2026-05-09  
**适用版本**：Spring Boot 3.x + JJWT 0.12.x

# Spring Security 与请求拦截学习指南

## 一、核心概念概述

### 1.1 请求拦截的三种方式

在 Spring Boot 项目中，请求拦截主要有三种方式：

| 组件 | 作用 | 执行时机 | 技术层面 |
|------|------|----------|----------|
| **Filter（过滤器）** | 过滤请求，处理 HTTP 层面的拦截 | 在 DispatcherServlet 之前 | Servlet 规范 |
| **Interceptor（拦截器）** | 拦截 Controller 方法调用 | 在 DispatcherServlet 之后 | Spring MVC |
| **AOP（切面）** | 面向切面编程，可拦截任意方法 | 方法执行前后 | Spring AOP |

### 1.2 Spring Security 核心架构

```
请求 → SecurityFilterChain → Filter1 → Filter2 → ... → DispatcherServlet → Controller
```

## 二、学习资源推荐

### 2.1 官方文档

| 资源 | 链接 | 说明 |
|------|------|------|
| Spring Security 官方文档 | [docs.spring.io/spring-security](https://docs.spring.io/spring-security/reference/index.html) | 最权威的学习资料 |
| Spring Security Samples | [github.com/spring-projects/spring-security](https://github.com/spring-projects/spring-security/tree/main/samples) | 官方示例项目 |

### 2.2 基础入门

| 标题 | 链接 | 难度 |
|------|------|------|
| Spring Security 从入门到精通 | [CSDN 系列文章](https://blog.csdn.net/qq_39654127/category_9272641.html) | 入门 |
| Spring Security 详解（一）：基本概念和入门配置 | [掘金](https://juejin.cn/post/6844903688222406663) | 入门 |
| Spring Boot Security 入门教程 | [知乎](https://zhuanlan.zhihu.com/p/317482922) | 入门 |

### 2.3 过滤器与拦截器

| 标题 | 链接 | 重点内容 |
|------|------|----------|
| Spring Boot 过滤器和拦截器详解与区别 | [CSDN](https://blog.csdn.net/weixin_43888891/article/details/124534434) | Filter vs Interceptor |
| Spring MVC 拦截器详解 | [博客园](https://www.cnblogs.com/lenve/p/11492623.html) | 拦截器配置与使用 |
| Spring Filter 过滤器详解 | [简书](https://www.jianshu.com/p/952a42a78916) | Filter 实现原理 |

### 2.4 JWT 认证

| 标题 | 链接 | 内容特点 |
|------|------|----------|
| Spring Security + JWT 实现无状态认证 | [掘金](https://juejin.cn/post/6844904092756813832) | 完整实现教程 |
| Spring Boot + JWT 实现登录认证 | [CSDN](https://blog.csdn.net/qq_34101364/article/details/105927671) | 代码示例详细 |
| JWT 入门到精通 | [阮一峰博客](https://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html) | 原理讲解清晰 |

### 2.5 进阶内容

| 标题 | 链接 | 难度 |
|------|------|------|
| Spring Security 权限控制详解 | [博客园](https://www.cnblogs.com/crazymakercircle/p/14085565.html) | 进阶 |
| Spring Security FilterChain 工作原理解析 | [掘金](https://juejin.cn/post/7023694742541365261) | 进阶 |
| Spring Security 源码分析 | [CSDN](https://blog.csdn.net/qq_34446485/category_10251279.html) | 高级 |

### 2.6 视频教程

| 平台 | 推荐课程 | 特点 |
|------|----------|------|
| B 站 | Spring Security 实战教程 | 实战项目驱动 |
| 慕课网 | Spring Security 从入门到实战 | 体系化学习 |
| YouTube | Spring Security Tutorial | 英文原版 |

## 三、学习路径建议

### 阶段一：基础入门（1-2 周）

1. **理解核心概念**
   - 认证（Authentication）vs 授权（Authorization）
   - Session vs Token 认证方式

2. **环境搭建**
   - 创建 Spring Boot 项目
   - 引入 Spring Security 依赖
   - 配置简单的用户名密码认证

3. **实践练习**
   - 实现基于内存的用户认证
   - 配置静态资源放行
   - 自定义登录页面

### 阶段二：核心进阶（2-3 周）

1. **用户数据持久化**
   - 集成数据库存储用户信息
   - 实现 UserDetailsService

2. **密码加密**
   - BCryptPasswordEncoder 使用
   - 密码加密原理

3. **权限控制**
   - 基于角色的访问控制（RBAC）
   - @PreAuthorize、@Secured 注解使用

### 阶段三：实战应用（2-3 周）

1. **JWT 认证实现**
   - JWT Token 生成与解析
   - Token 过滤器实现
   - Token 刷新机制

2. **安全配置优化**
   - CORS 跨域配置
   - CSRF 防护
   - 会话管理

### 阶段四：高级拓展（按需学习）

1. **OAuth2.0**
   - OAuth2.0 协议理解
   - Spring Security OAuth2 配置
   - 第三方登录集成

2. **SSO 单点登录**
   - SSO 原理
   - Spring Security SAML 集成

## 四、重点概念速记

### 4.1 Filter vs Interceptor vs AOP

```
请求流程：
┌─────────────────────────────────────────────────────────────┐
│  HTTP Request                                              │
└─────────────────┬─────────────────────────────────────────┘
                  ▼
┌─────────────────────────────────────────────────────────────┐
│  Filter（Servlet 层）                                       │
│  - 处理 HTTP 请求/响应                                      │
│  - 可修改 request/response                                 │
└─────────────────┬─────────────────────────────────────────┘
                  ▼
┌─────────────────────────────────────────────────────────────┐
│  DispatcherServlet                                         │
└─────────────────┬─────────────────────────────────────────┘
                  ▼
┌─────────────────────────────────────────────────────────────┐
│  Interceptor（Spring MVC 层）                               │
│  - 拦截 Controller 方法                                     │
│  - 可获取 HandlerMethod                                    │
└─────────────────┬─────────────────────────────────────────┘
                  ▼
┌─────────────────────────────────────────────────────────────┐
│  Controller                                                │
│  └── AOP（方法层）                                          │
│      - 可拦截任意 Spring Bean 方法                           │
│      - 基于切面表达式                                        │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 Spring Security 过滤器链

| 过滤器 | 作用 |
|--------|------|
| `SecurityContextPersistenceFilter` | 管理 SecurityContext |
| `UsernamePasswordAuthenticationFilter` | 处理表单登录 |
| `BasicAuthenticationFilter` | 处理 HTTP Basic 认证 |
| `SessionManagementFilter` | 会话管理 |
| `ExceptionTranslationFilter` | 异常处理 |
| `FilterSecurityInterceptor` | 最终权限检查 |

### 4.3 常用注解

| 注解 | 作用 | 使用位置 |
|------|------|----------|
| `@EnableWebSecurity` | 启用 Web 安全配置 | 配置类 |
| `@PreAuthorize` | 方法执行前权限检查 | 方法 |
| `@PostAuthorize` | 方法执行后权限检查 | 方法 |
| `@Secured` | 角色检查 | 方法 |
| `@PermitAll` | 允许所有访问 | 方法/类 |
| `@DenyAll` | 拒绝所有访问 | 方法/类 |

## 五、实战代码模板

### 5.1 SecurityConfig 示例

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### 5.2 JWT 过滤器示例

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null && jwtTokenUtil.validateToken(token)) {
            String username = jwtTokenUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
```

## 六、常见问题

### 6.1 401 Unauthorized

**原因**：请求没有携带有效凭证或凭证无效

**解决方案**：
- 检查请求头是否包含 `Authorization: Bearer <token>`
- 验证 Token 是否过期
- 检查 Token 签名是否正确

### 6.2 403 Forbidden

**原因**：用户已认证，但没有足够的权限

**解决方案**：
- 检查 `@PreAuthorize` 注解配置
- 验证用户角色是否正确
- 检查权限表达式是否正确

### 6.3 CORS 跨域问题

**原因**：浏览器同源策略限制

**解决方案**：
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

## 七、总结

学习 Spring Security 和请求拦截需要循序渐进：

1. **理解基础概念**：Filter、Interceptor、AOP 的区别
2. **掌握核心配置**：SecurityFilterChain、UserDetailsService
3. **实践 JWT 认证**：Token 生成、验证、刷新
4. **关注安全细节**：密码加密、CSRF、CORS

推荐通过官方文档 + 实战项目的方式学习，理论结合实践才能真正掌握。

---

**更新时间**：2026-05-07  
**适用版本**：Spring Boot 3.x + Spring Security 6.x
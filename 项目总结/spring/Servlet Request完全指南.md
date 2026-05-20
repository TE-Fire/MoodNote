# Servlet Request 完全指南

## 概述

Servlet Request（`HttpServletRequest`）是 Servlet API 中最核心的接口之一，它封装了客户端 HTTP 请求的所有信息，包括请求参数、请求头、Cookie、Session 等。在 Java Web 开发中，几乎每个请求都会用到 Request 对象，它是连接浏览器与服务器的桥梁。

---

## 一、Request 的作用与生命周期

### 1.1 Request 的核心作用

Request 对象在 Web 开发中扮演着以下关键角色：

```
┌─────────────────────────────────────────────────────────────┐
│                   HTTP 请求处理流程                          │
├─────────────────────────────────────────────────────────────┤
│                                                            │
│  浏览器 (Client)                                            │
│       │                                                    │
│       │ 1. 发送 HTTP 请求                                   │
│       ▼                                                    │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              Web 服务器 (Tomcat)                     │  │
│  │                                                     │  │
│  │  2. 解析 HTTP 报文，创建 Request 对象                │  │
│  │  3. 创建 Response 对象                              │  │
│  │  4. 调用 Servlet 的 service() 方法                  │  │
│  └─────────────────────────────────────────────────────┘  │
│       │                                                    │
│       │ 5. Servlet 处理请求                                │
│       ▼                                                    │
│  ┌─────────────────────────────────────────────────────┐  │
│  │           HttpServletRequest 对象                     │  │
│  │  - 获取请求参数                                      │  │
│  │  - 读取请求头                                        │  │
│  │  - 访问 Cookie/Session                               │  │
│  │  - 读取请求体                                        │  │
│  │  - 请求转发                                          │  │
│  └─────────────────────────────────────────────────────┘  │
│       │                                                    │
│       │ 6. 返回 HTTP 响应                                  │
│       ▼                                                    │
│  浏览器                                                    │
│                                                            │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Request 的生命周期

Request 对象的生命周期非常短暂：

```
1. 请求到达 → Tomcat 创建 HttpServletRequest 实例
2. 请求处理 → Servlet/Controller 中使用
3. 响应返回 → Request 对象销毁，内存释放
```

**关键点**：每个请求对应一个独立的 Request 对象，请求结束后立即销毁，因此它是**线程安全**的。

---

## 二、Request 包含的信息

### 2.1 HTTP 请求报文结构

在理解 Request 对象之前，先看一下 HTTP 请求报文的结构：

```
GET /api/diary/list?pageNum=1&pageSize=10 HTTP/1.1
Host: localhost:8080
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json
Cookie: JSESSIONID=abc123; token=xyz789

{"keyword":"心情","moodType":1}
```

HTTP 请求由三部分组成：

| 部分 | 说明 | 示例 |
|------|------|------|
| **请求行** | 请求方法、URI、协议版本 | `GET /api/diary/list?pageNum=1 HTTP/1.1` |
| **请求头** | 键值对，包含元数据 | `Host: localhost:8080` |
| **请求体** | POST/PUT 请求的内容（可选） | `{"keyword":"心情"}` |

### 2.2 Request 对象的信息分类

HttpServletRequest 对象将上述 HTTP 报文封装成 Java 对象，主要包含以下信息：

```
HttpServletRequest
│
├─ 请求行信息
│   ├─ 请求方法 (GET/POST/PUT/DELETE)
│   ├─ 请求 URI
│   ├─ 请求 URL
│   ├─ Context Path
│   ├─ Servlet Path
│   ├─ Query String
│   └─ 协议版本
│
├─ 请求头信息
│   ├─ Host
│   ├─ User-Agent
│   ├─ Accept
│   ├─ Content-Type
│   ├─ Authorization
│   ├─ Referer
│   └─ Cookie
│
├─ 请求参数
│   ├─ URL 参数 (Query String)
│   ├─ 表单参数 (Form Data)
│   └─ 路径参数 (Path Variable)
│
├─ 请求体
│   ├─ JSON 数据
│   ├─ 表单数据
│   └─ 文件上传
│
├─ 客户端信息
│   ├─ 客户端 IP
│   ├─ 客户端端口
│   ├─ 远程主机名
│   └─ 本地地址
│
├─ 会话信息
│   ├─ Session
│   ├─ Cookie
│   └─ 认证信息
│
└─ 请求属性
    ├─ Servlet 容器设置的属性
    ├─ 过滤器设置的属性
    └─ 开发者自定义属性
```

---

## 三、HttpServletRequest 接口详解

### 3.1 接口继承关系

```
ServletRequest (接口)
      ↑
      │
HttpServletRequest (接口)
      ↑
      │
  具体实现类 (Tomcat/Jetty 等容器提供)
```

### 3.2 核心接口说明

#### ServletRequest（基础接口）

定义了与协议无关的请求方法：

| 方法 | 说明 |
|------|------|
| `getParameter()` | 获取请求参数 |
| `getAttribute()` | 获取请求属性 |
| `setAttribute()` | 设置请求属性 |
| `getInputStream()` | 获取请求输入流 |
| `getReader()` | 获取请求字符流 |
| `getRemoteAddr()` | 获取客户端 IP |
| `getServerName()` | 获取服务器名称 |
| `getServerPort()` | 获取服务器端口 |

#### HttpServletRequest（HTTP 专用接口）

继承自 ServletRequest，增加了 HTTP 协议相关的方法：

| 方法 | 说明 |
|------|------|
| `getMethod()` | 获取请求方法 (GET/POST) |
| `getRequestURI()` | 获取请求 URI |
| `getRequestURL()` | 获取请求 URL |
| `getHeader()` | 获取请求头 |
| `getCookies()` | 获取 Cookie |
| `getSession()` | 获取 Session |
| `getContextPath()` | 获取上下文路径 |
| `getServletPath()` | 获取 Servlet 路径 |
| `getPathInfo()` | 获取路径信息 |
| `getQueryString()` | 获取查询字符串 |

---

## 四、常用 API 详解

### 4.1 获取请求行信息

```java
@GetMapping("/request-info")
public void getRequestInfo(HttpServletRequest request) {
    // 1. 请求方法
    String method = request.getMethod();
    System.out.println("Method: " + method); // GET, POST, PUT, DELETE, etc.
    
    // 2. 请求 URI (不含协议、主机名、端口)
    String uri = request.getRequestURI();
    System.out.println("URI: " + uri); // /api/diary/list
    
    // 3. 请求 URL (完整地址)
    StringBuffer url = request.getRequestURL();
    System.out.println("URL: " + url); // http://localhost:8080/api/diary/list
    
    // 4. Context Path (应用根路径)
    String contextPath = request.getContextPath();
    System.out.println("Context Path: " + contextPath); // "" 或 /your-app-name
    
    // 5. Servlet Path (Servlet 映射路径)
    String servletPath = request.getServletPath();
    System.out.println("Servlet Path: " + servletPath); // /api/diary/list
    
    // 6. Query String (查询参数)
    String queryString = request.getQueryString();
    System.out.println("Query String: " + queryString); // pageNum=1&pageSize=10
    
    // 7. 协议版本
    String protocol = request.getProtocol();
    System.out.println("Protocol: " + protocol); // HTTP/1.1
    
    // 8. 完整路径信息
    String pathInfo = request.getPathInfo();
    System.out.println("Path Info: " + pathInfo); // 可能为 null
}
```

### 4.2 获取请求参数

#### 方式一：getParameter() - 单个参数

```java
// 获取单个参数
String keyword = request.getParameter("keyword");
String pageNum = request.getParameter("pageNum");
String pageSize = request.getParameter("pageSize");

// 参数类型转换
Integer pageNumInt = null;
if (pageNum != null) {
    pageNumInt = Integer.parseInt(pageNum);
}
```

#### 方式二：getParameterMap() - 所有参数

```java
// 获取所有参数（返回 Map<String, String[]>）
Map<String, String[]> paramMap = request.getParameterMap();
for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
    String key = entry.getKey();
    String[] values = entry.getValue();
    System.out.println(key + " = " + Arrays.toString(values));
}
```

#### 方式三：getParameterValues() - 同名多值

```java
// 获取同名参数的多个值（如 checkbox）
String[] tagIds = request.getParameterValues("tagIds");
if (tagIds != null) {
    for (String tagId : tagIds) {
        System.out.println("Tag ID: " + tagId);
    }
}
```

#### 方式四：getParameterNames() - 枚举所有参数名

```java
// 遍历所有参数名
Enumeration<String> paramNames = request.getParameterNames();
while (paramNames.hasMoreElements()) {
    String paramName = paramNames.nextElement();
    String paramValue = request.getParameter(paramName);
    System.out.println(paramName + " = " + paramValue);
}
```

### 4.3 获取请求头

```java
@GetMapping("/headers")
public void getHeaders(HttpServletRequest request) {
    // 1. 获取单个请求头
    String userAgent = request.getHeader("User-Agent");
    String contentType = request.getHeader("Content-Type");
    String authorization = request.getHeader("Authorization");
    String referer = request.getHeader("Referer");
    
    System.out.println("User-Agent: " + userAgent);
    System.out.println("Content-Type: " + contentType);
    System.out.println("Authorization: " + authorization);
    System.out.println("Referer: " + referer);
    
    // 2. 获取请求头的值（可能有多个值）
    Enumeration<String> headerValues = request.getHeaders("Accept");
    while (headerValues.hasMoreElements()) {
        System.out.println("Accept: " + headerValues.nextElement());
    }
    
    // 3. 获取所有请求头名称
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        String headerValue = request.getHeader(headerName);
        System.out.println(headerName + " = " + headerValue);
    }
    
    // 4. 获取请求头（作为 Date）
    long ifModifiedSince = request.getDateHeader("If-Modified-Since");
    if (ifModifiedSince != -1) {
        System.out.println("If-Modified-Since: " + new Date(ifModifiedSince));
    }
    
    // 5. 获取请求头（作为 Int）
    int contentLength = request.getIntHeader("Content-Length");
    System.out.println("Content-Length: " + contentLength);
}
```

### 4.4 获取客户端信息

```java
@GetMapping("/client-info")
public void getClientInfo(HttpServletRequest request) {
    // 1. 客户端 IP 地址
    String remoteAddr = request.getRemoteAddr();
    System.out.println("Remote Addr: " + remoteAddr); // 127.0.0.1 或真实 IP
    
    // 2. 客户端主机名
    String remoteHost = request.getRemoteHost();
    System.out.println("Remote Host: " + remoteHost);
    
    // 3. 客户端端口
    int remotePort = request.getRemotePort();
    System.out.println("Remote Port: " + remotePort); // 客户端临时端口
    
    // 4. 本地服务器地址
    String localAddr = request.getLocalAddr();
    System.out.println("Local Addr: " + localAddr); // 0:0:0:0:0:0:0:1 或 127.0.0.1
    
    // 5. 本地服务器名称
    String localName = request.getLocalName();
    System.out.println("Local Name: " + localName);
    
    // 6. 本地服务器端口
    int localPort = request.getLocalPort();
    System.out.println("Local Port: " + localPort); // 8080
    
    // 7. 服务器名称（从 Host 头获取）
    String serverName = request.getServerName();
    System.out.println("Server Name: " + serverName); // localhost
    
    // 8. 服务器端口
    int serverPort = request.getServerPort();
    System.out.println("Server Port: " + serverPort); // 8080
}
```

### 4.5 获取 Cookie

```java
@GetMapping("/cookies")
public void getCookies(HttpServletRequest request) {
    // 1. 获取所有 Cookie
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            System.out.println("Cookie Name: " + cookie.getName());
            System.out.println("Cookie Value: " + cookie.getValue());
            System.out.println("Cookie Max Age: " + cookie.getMaxAge());
            System.out.println("Cookie Path: " + cookie.getPath());
            System.out.println("Cookie Domain: " + cookie.getDomain());
            System.out.println("Cookie Secure: " + cookie.getSecure());
            System.out.println("Cookie HttpOnly: " + cookie.isHttpOnly());
            System.out.println("---");
        }
    }
    
    // 2. 获取指定名称的 Cookie
    String token = null;
    Cookie[] cookies2 = request.getCookies();
    if (cookies2 != null) {
        for (Cookie cookie : cookies2) {
            if ("token".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }
    }
    System.out.println("Token: " + token);
}
```

### 4.6 获取 Session

```java
@GetMapping("/session")
public void getSession(HttpServletRequest request) {
    // 1. 获取 Session（如果不存在则创建）
    HttpSession session = request.getSession();
    System.out.println("Session ID: " + session.getId());
    System.out.println("Session Created: " + new Date(session.getCreationTime()));
    System.out.println("Session Last Accessed: " + new Date(session.getLastAccessedTime()));
    
    // 2. 获取 Session（如果不存在返回 null）
    HttpSession session2 = request.getSession(false);
    
    // 3. Session 中存储数据
    session.setAttribute("userId", 1L);
    session.setAttribute("userName", "zhangsan");
    
    // 4. 从 Session 获取数据
    Long userId = (Long) session.getAttribute("userId");
    String userName = (String) session.getAttribute("userName");
    
    // 5. 移除 Session 中的数据
    session.removeAttribute("userId");
    
    // 6. 销毁 Session
    session.invalidate();
}
```

### 4.7 请求属性操作

```java
@GetMapping("/attributes")
public void attributesDemo(HttpServletRequest request) {
    // 1. 设置请求属性（用于同一请求内传递数据）
    request.setAttribute("userId", 1L);
    request.setAttribute("userName", "zhangsan");
    request.setAttribute("diaryList", Arrays.asList("日记1", "日记2"));
    
    // 2. 获取请求属性
    Long userId = (Long) request.getAttribute("userId");
    String userName = (String) request.getAttribute("userName");
    
    // 3. 获取所有属性名
    Enumeration<String> attrNames = request.getAttributeNames();
    while (attrNames.hasMoreElements()) {
        String attrName = attrNames.nextElement();
        Object attrValue = request.getAttribute(attrName);
        System.out.println(attrName + " = " + attrValue);
    }
    
    // 4. 移除请求属性
    request.removeAttribute("userId");
}
```

### 4.8 读取请求体

#### 方式一：getInputStream() - 字节流

```java
@PostMapping("/body-bytes")
public void readBodyAsBytes(HttpServletRequest request) throws IOException {
    ServletInputStream inputStream = request.getInputStream();
    byte[] buffer = new byte[1024];
    int bytesRead;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    while ((bytesRead = inputStream.read(buffer)) != -1) {
        baos.write(buffer, 0, bytesRead);
    }
    
    String body = baos.toString("UTF-8");
    System.out.println("Request Body: " + body);
}
```

#### 方式二：getReader() - 字符流

```java
@PostMapping("/body-string")
public void readBodyAsString(HttpServletRequest request) throws IOException {
    BufferedReader reader = request.getReader();
    StringBuilder body = new StringBuilder();
    String line;
    
    while ((line = reader.readLine()) != null) {
        body.append(line);
    }
    
    System.out.println("Request Body: " + body.toString());
}
```

#### 方式三：@RequestBody - Spring 方式（推荐）

```java
@PostMapping("/diary")
public Result<DiaryVO> createDiary(@RequestBody DiaryCreateDTO dto) {
    // Spring 自动将 JSON 转换为 Java 对象
    diaryService.createDiary(dto);
    return Result.success();
}
```

### 4.9 请求转发与包含

```java
@Controller
public class ForwardController {
    
    @GetMapping("/forward-demo")
    public void forwardDemo(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. 请求转发（服务器内部跳转）
        // 特点：浏览器地址栏不变，共享 Request 对象
        request.setAttribute("message", "这是转发的数据");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/target-page");
        dispatcher.forward(request, response);
        
        // 2. 请求包含（包含其他 Servlet 的输出）
        RequestDispatcher dispatcher2 = request.getRequestDispatcher("/header");
        dispatcher2.include(request, response);
    }
    
    @GetMapping("/target-page")
    public void targetPage(HttpServletRequest request) {
        String message = (String) request.getAttribute("message");
        System.out.println(message); // 输出：这是转发的数据
    }
}
```

### 4.10 内容类型判断

```java
@PostMapping("/content-type")
public void checkContentType(HttpServletRequest request) {
    String contentType = request.getContentType();
    
    // 1. 判断是否为 JSON
    if (contentType != null && contentType.contains("application/json")) {
        System.out.println("这是 JSON 请求");
    }
    
    // 2. 判断是否为表单提交
    if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
        System.out.println("这是表单提交");
    }
    
    // 3. 判断是否为文件上传
    if (contentType != null && contentType.contains("multipart/form-data")) {
        System.out.println("这是文件上传");
    }
    
    // 4. 判断是否为 XML
    if (contentType != null && contentType.contains("application/xml")) {
        System.out.println("这是 XML 请求");
    }
}
```

---

## 五、项目实战：JwtAuthenticationFilter 中的 Request 应用

让我们结合项目中的实际代码来理解 Request 的使用：

### 5.1 从 Request 中提取 Token

```java
@Component
public class JwtAuthenticationFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
                       FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // 1. 获取请求 URI，判断是否在白名单
        String uri = request.getRequestURI();
        Set<String> excludePaths = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/captcha",
            "/api/auth/send-code",
            "/api/auth/reset-password"
        );
        
        if (excludePaths.contains(uri)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 2. 获取 Token（从 Header 或 Cookie）
        String token = extractToken(request);
        
        // 3. 将用户信息存入 Request 属性
        String userName = jwtTokenUtil.extractUsername(token);
        Long userId = jwtTokenUtil.extractUserId(token);
        request.setAttribute("userName", userName);
        request.setAttribute("userId", userId);
        
        chain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        // 方式一：从 Authorization Header 获取
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // 方式二：从 Cookie 获取
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
}
```

### 5.2 在 Service 中获取 Request 中的 userId

```java
@Service
public class DiaryServiceImpl implements DiaryService {
    
    private Long getCurrentUserId() {
        // 从 RequestContextHolder 获取当前请求
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Long userId = (Long) request.getAttribute("userId");
            if (userId != null) {
                return userId;
            }
        }
        
        throw new IllegalStateException("用户未登录或UserId未找到");
    }
    
    @Override
    public Result<PageResult<DiaryVO>> getList(DiaryQueryDTO diaryQueryDTO) {
        Long userId = getCurrentUserId(); // 从 Request 属性获取
        // ...
    }
}
```

---

## 六、RequestContextHolder 在非 Web 层获取 Request

在 Service 层或其他非 Web 层，我们无法直接注入 HttpServletRequest，这时可以使用 `RequestContextHolder`：

### 6.1 基本用法

```java
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UserService {
    
    public Long getCurrentUserId() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return (Long) request.getAttribute("userId");
        }
        
        throw new IllegalStateException("用户未登录");
    }
}
```

### 6.2 工作原理

```
┌─────────────────────────────────────────────────────────────┐
│              RequestContextHolder 工作原理                    │
├─────────────────────────────────────────────────────────────┤
│                                                            │
│  ThreadLocal<RequestAttributes>                            │
│         │                                                  │
│         │ 1. 请求到达时，Spring 将 Request 绑定到当前线程     │
│         ▼                                                  │
│  ┌─────────────────────────────────────────────────────┐  │
│  │  ThreadLocal.set(requestAttributes)                  │  │
│  └─────────────────────────────────────────────────────┘  │
│         │                                                  │
│         │ 2. Service 层通过 RequestContextHolder.get() 获取 │
│         ▼                                                  │
│  ┌─────────────────────────────────────────────────────┐  │
│  │  (ServletRequestAttributes) ThreadLocal.get()        │  │
│  └─────────────────────────────────────────────────────┘  │
│         │                                                  │
│         │ 3. 请求结束时，Spring 清理 ThreadLocal            │
│         ▼                                                  │
│  ┌─────────────────────────────────────────────────────┐  │
│  │  ThreadLocal.remove()                                │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                            │
└─────────────────────────────────────────────────────────────┘
```

### 6.3 注意事项

```java
public class RequestContextHolderDemo {
    
    public void safeUsage() {
        // ✅ 正确做法：检查 null
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 使用 request
        } else {
            throw new IllegalStateException("不在 Web 请求上下文中");
        }
        
        // ❌ 错误做法：直接使用不检查
        // HttpServletRequest request = ((ServletRequestAttributes) 
        //      RequestContextHolder.getRequestAttributes()).getRequest();
        // 可能会抛出 NullPointerException
    }
}
```

---

## 七、常见问题与解决方案

### 7.1 解决获取真实 IP 的问题

**问题**：经过 Nginx 等代理后，`request.getRemoteAddr()` 获取的是代理服务器 IP。

**解决方案**：

```java
public class IpUtils {
    
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 多个代理时，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
```

### 7.2 请求体只能读取一次的问题

**问题**：`request.getInputStream()` 或 `request.getReader()` 只能调用一次，第二次会抛出异常。

**解决方案**：使用 `HttpServletRequestWrapper` 包装 Request，缓存请求体：

```java
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    
    private byte[] cachedBody;
    
    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        // 读取并缓存请求体
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(this.cachedBody);
    }
    
    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
}

public class CachedBodyServletInputStream extends ServletInputStream {
    
    private InputStream cachedBodyInputStream;
    
    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }
    
    @Override
    public boolean isFinished() {
        try {
            return cachedBodyInputStream.available() == 0;
        } catch (IOException e) {
            return true;
        }
    }
    
    @Override
    public boolean isReady() {
        return true;
    }
    
    @Override
    public void setReadListener(ReadListener readListener) {
        // 不处理异步
    }
    
    @Override
    public int read() throws IOException {
        return cachedBodyInputStream.read();
    }
}
```

### 7.3 中文乱码问题

**问题**：请求参数或请求体中的中文显示乱码。

**解决方案**：

```java
// 方式一：在 Filter 中设置字符编码
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                       FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }
}

// 方式二：Spring Boot 配置（application.yml）
// spring:
//   http:
//     encoding:
//       charset: UTF-8
//       enabled: true
//       force: true
```

### 7.4 请求参数绑定到 Java 对象

**Spring 方式**（推荐）：

```java
// 1. 定义 DTO
@Data
public class DiaryQueryDTO {
    private String keyword;
    private Integer moodType;
    private Integer weatherType;
    private String startDate;
    private String endDate;
    private Integer pageNum;
    private Integer pageSize;
}

// 2. Controller 直接绑定
@GetMapping("/diary/list")
public Result<PageResult<DiaryVO>> getList(@ModelAttribute DiaryQueryDTO dto) {
    // Spring 自动将请求参数绑定到 DTO
    return diaryService.getList(dto);
}
```

---

## 八、最佳实践总结

### 8.1 Request 使用的最佳实践

| 场景 | 推荐做法 | 避免做法 |
|------|---------|---------|
| **获取参数** | 使用 `@RequestParam` 或 `@ModelAttribute` | 手动调用 `request.getParameter()` |
| **获取请求体** | 使用 `@RequestBody` | 手动读取 `request.getInputStream()` |
| **传递用户信息** | 使用 `request.setAttribute()` | 静态变量或 ThreadLocal（除非必要） |
| **获取当前用户** | 使用 `RequestContextHolder` 或自定义工具类 | 每次都解析 Token |
| **文件上传** | 使用 `MultipartFile` | 手动解析 `multipart/form-data` |

### 8.2 性能优化建议

1. **避免重复读取请求体**：Request 的输入流只能读取一次，需要缓存时使用 Wrapper
2. **合理使用 Request 属性**：只在同一请求内需要共享数据时使用
3. **及时清理**：Request 结束后会自动销毁，无需手动清理属性
4. **避免大对象**：不要在 Request 中存储过大的对象

---

## 九、相关资源

- [Servlet 4.0 官方文档](https://javaee.github.io/servlet-spec/)
- [HttpServletRequest JavaDoc](https://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html)
- [Spring Web 文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)
- [Tomcat Servlet 实现](https://tomcat.apache.org/tomcat-9.0-doc/servletapi/index.html)

---

## 总结

### 核心要点回顾

1. **HttpServletRequest 是 HTTP 请求的 Java 封装**：包含请求行、请求头、请求体、客户端信息等
2. **生命周期短暂**：每个请求一个对象，请求结束后销毁
3. **线程安全**：每个线程独立的 Request 对象
4. **多用途**：获取参数、读取请求头、访问 Session/Cookie、请求转发等
5. **RequestContextHolder**：在非 Web 层获取 Request 的工具

### 关键应用场景

- 认证鉴权（如项目中的 JwtAuthenticationFilter）
- 参数接收与验证
- 日志记录（记录用户 IP、User-Agent 等）
- 请求转发与包含
- Session 管理

Request 对象是 Java Web 开发的基础，掌握它的使用对于理解整个 Web 请求流程至关重要！

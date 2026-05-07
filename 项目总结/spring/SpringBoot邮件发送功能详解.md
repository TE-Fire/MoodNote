# Spring Boot 邮件发送功能详解

## 一、邮件发送概述

### 1.1 什么是邮件发送

邮件发送是 Web 应用中常见的功能之一，主要用于：

| 应用场景 | 说明 |
|----------|------|
| **用户注册** | 发送注册验证码、激活链接 |
| **密码找回** | 发送重置密码链接或验证码 |
| **系统通知** | 订单确认、发货通知、活动推送 |
| **异步通信** | 发送日志、报告、统计数据 |

### 1.2 邮件协议简介

邮件发送涉及两个核心协议：

**SMTP（Simple Mail Transfer Protocol）**
- 用途：发送邮件
- 端口：25（明文）、465（SSL）、587（TLS）
- 特点：简单高效，支持身份认证

**IMAP（Internet Message Access Protocol）**
- 用途：接收邮件
- 端口：143（明文）、993（SSL）
- 特点：支持在服务器上管理邮件

```
发送流程：
用户邮箱 → SMTP服务器 → 目标SMTP服务器 → 收件人邮箱
   ↑                                        ↓
   └─────────────── 接收协议（IMAP） ─────────┘
```

## 二、依赖配置详解

### 2.1 Maven 依赖引入

Spring Boot 提供了便捷的邮件依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

### 2.2 依赖关系图

`spring-boot-starter-mail` 的依赖传递关系：

```
spring-boot-starter-mail
    ├── jakarta.mail-api          # 邮件 API 规范
    ├── jakarta.activation       # 数据类型激活
    ├── spring-context-support    # Spring 邮件支持
    └── jakarta.mail (实现)       # 具体实现
```

### 2.3 依赖版本管理

在父 POM 中已经统一管理了版本：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 三、核心 API 解析

### 3.1 JavaMailSender 接口

这是 Spring 封装的邮件发送核心接口：

```java
public interface JavaMailSender {

    // 创建简单邮件
    MimeMessage createMimeMessage();

    // 发送简单邮件
    void send(MimeMessage mimeMessage);

    // 发送多个邮件
    void send(MimeMessage... mimeMessages);

    // 发送MimeMessage的辅助方法
    void send(MimeMessagePreparator mimeMessagePreparator);
}
```

**继承结构：**

```
JavaMailSender（接口）
    ↓
    JavaMailSenderImpl（实现类）
            ↓
            com.sun.mail.smtp.SMTPTransport
```

### 3.2 MimeMessage 类

`MimeMessage` 代表一封邮件消息，是 JavaMail API 的核心类：

```java
MimeMessage message = mailSender.createMimeMessage();
```

**MimeMessage 的主要方法：**

| 方法 | 作用 | 示例 |
|------|------|------|
| `setFrom(Address address)` | 设置发件人 | `helper.setFrom("sender@example.com")` |
| `addRecipient(Type type, Address address)` | 添加收件人 | `helper.setTo("receiver@example.com")` |
| `setSubject(String subject)` | 设置邮件主题 | `helper.setSubject("验证码")` |
| `setText(String text)` | 设置邮件正文 | `helper.setText("您的验证码是：123")` |
| `setSentDate(Date date)` | 设置发送时间 | 自动设置 |

**收件人类型枚举：**

```java
public enum Type {
    TO,     // 主送收件人
    CC,     // 抄送收件人
    BCC     // 密送收件人
}
```

### 3.3 MimeMessageHelper 类

`MimeMessageHelper` 是 MimeMessage 的辅助类，大大简化了邮件创建过程：

```java
MimeMessageHelper helper = new MimeMessageHelper(message, true);
```

**构造方法解析：**

```java
public MimeMessageHelper(MimeMessage message, boolean multipart)
    throws MessagingException {
    // message: 关联的MimeMessage对象
    // multipart: 是否支持多部件（附件、HTML等）
}
```

**multipart 参数说明：**

| 值 | 说明 | 用途 |
|----|------|------|
| `false` | 纯文本邮件 | 简单的文本内容 |
| `true` | 多部件邮件 | 支持 HTML、附件、内嵌图片 |

**核心方法一览：**

| 方法 | 说明 |
|------|------|
| `setFrom(String from)` | 设置发件人邮箱 |
| `setTo(String to)` | 设置收件人邮箱 |
| `setCc(String cc)` | 设置抄送邮箱 |
| `setBcc(String bcc)` | 设置密送邮箱 |
| `setSubject(String subject)` | 设置邮件主题 |
| `setText(String text, boolean html)` | 设置邮件正文，html=true 表示 HTML 格式 |
| `addAttachment(String attachmentFilename, InputStreamSource inputStreamSource)` | 添加附件 |

## 四、YML 配置详解

### 4.1 完整配置示例

```yaml
spring:
  mail:
    # QQ邮箱 SMTP 服务器地址
    host: smtp.qq.com

    # SMTP 端口（587 为 TLS 端口，465 为 SSL 端口）
    port: 587

    # 发送邮件的 QQ 邮箱地址
    username: 3037749727@qq.com

    # QQ邮箱授权码（非密码，需在 QQ邮箱设置中开启 SMTP 并获取）
    password: xxxxxxxxxxxxxxxx

    # 邮件协议
    protocol: smtp

    # 邮件属性配置
    properties:
      mail:
        smtp:
          # 是否启用 SMTP 认证
          auth: true
          # 是否启用 STARTTLS（安全连接）
          starttls:
            enable: true
```

### 4.2 配置项详解

#### host - SMTP 服务器地址

| 邮箱服务商 | 服务器地址 |
|-----------|-----------|
| QQ 邮箱 | smtp.qq.com |
| 163 邮箱 | smtp.163.com |
| Gmail | smtp.gmail.com |
| 新浪邮箱 | smtp.sina.com |

#### port - 端口号

| 端口 | 协议 | 说明 |
|------|------|------|
| 25 | SMTP | 明文传输（已被大多数服务商禁用） |
| 465 | SMTP + SSL | 使用 SSL 加密 |
| 587 | SMTP + TLS | 使用 STARTTLS 加密（推荐） |

#### username - 发件人邮箱

填写完整的邮箱地址，用于身份认证。

#### password - 授权码

**这是最容易出错的地方！**

> QQ 邮箱使用的是「授权码」而非「登录密码」

**获取授权码的步骤：**

1. 登录 QQ 邮箱网页版
2. 进入「设置」→「账户」
3. 找到「POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务」
4. 开启「SMTP 服务」
5. 点击「生成授权码」
6. 将获得的授权码填写到配置中

#### protocol - 邮件协议

通常填写 `smtp`（默认也是 smtp）。

#### properties.mail.smtp.auth - 认证开关

```yaml
auth: true
```

必须设为 `true`，否则无法通过身份认证。

#### properties.mail.smtp.starttls.enable - 加密传输

```yaml
starttls:
  enable: true
```

设为 `true` 表示启用 STARTTLS，这是一种加密传输方式，端口 587 必须启用。

### 4.3 配置原理图

```
application.yml
    ↓
Spring Boot 自动配置
    ↓
JavaMailSenderImpl
    ↓
SMTPTransport
    ↓
smtp.qq.com:587（加密连接）
```

## 五、实际代码解析

### 5.1 完整发送流程

```java
@Slf4j
@Component
public class EmailUtil {

    // 注入邮件发送器（自动配置）
    @Autowired
    private JavaMailSender mailSender;

    // 注入发件人邮箱（从配置读取）
    @Value("${spring.mail.username}")
    private String from;

    public boolean sendCodeEmail(String to, String code, String subject) {
        try {
            // 1. 创建邮件消息
            MimeMessage message = mailSender.createMimeMessage();

            // 2. 创建辅助工具（true 表示支持 HTML）
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 3. 设置邮件内容
            helper.setFrom(from);                                    // 发件人
            helper.setTo(to);                                        // 收件人
            helper.setSubject(subject);                              // 主题
            helper.setText(buildHtmlContent(code), true);            // HTML 正文

            // 4. 发送邮件
            mailSender.send(message);

            log.info("邮件发送成功，收件人：{}", to);
            return true;

        } catch (MessagingException e) {
            log.error("邮件发送失败", e);
            return false;
        }
    }

    // 构建 HTML 格式的邮件内容
    private String buildHtmlContent(String code) {
        return "<html><body>" +
               "<h3>您好！</h3>" +
               "<p>您的验证码是：<strong style='color: #4CAF50; font-size: 24px;'>" +
               code + "</strong></p>" +
               "<p>该验证码有效期为5分钟，请及时使用。</p>" +
               "<p>如果不是您本人操作，请忽略此邮件。</p>" +
               "</body></html>";
    }
}
```

### 5.2 代码执行流程图

```
调用 sendCodeEmail()
        ↓
创建 MimeMessage 对象
        ↓
创建 MimeMessageHelper 对象（设置 multipart=true）
        ↓
设置发件人、收件人、主题
        ↓
构建 HTML 内容
        ↓
调用 mailSender.send()
        ↓
建立 SMTP 连接（smtp.qq.com:587）
        ↓
进行身份认证（用户名 + 授权码）
        ↓
发送邮件到收件人邮箱
```

### 5.3 邮件内容对比

**普通文本邮件：**

```
您好！
您的验证码是：123456
该验证码有效期为5分钟，请及时使用。
```

**HTML 邮件（带样式）：**

```html
<html>
<body>
    <h3 style="color: #333;">您好！</h3>
    <p>您的验证码是：<strong style="color: #4CAF50; font-size: 24px;">123456</strong></p>
    <p>该验证码有效期为5分钟，请及时使用。</p>
</body>
</html>
```

## 六、HTML 邮件的优势

### 6.1 纯文本 vs HTML

| 特性 | 纯文本 | HTML 邮件 |
|------|--------|-----------|
| 样式 | 无 | 丰富（颜色、字体、大小） |
| 布局 | 简单 | 复杂（表格、分栏） |
| 图片 | 无法展示 | 支持内嵌图片 |
| 兼容性 | 100%兼容 | 可能被邮件客户端过滤 |
| 文件大小 | 小 | 较大 |

### 6.2 HTML 邮件注意事项

1. **内联样式**：大部分邮件客户端不支持外部 CSS，必须使用内联样式
2. **表格布局**：推荐使用表格进行布局，而不是 CSS Grid 或 Flexbox
3. **图片处理**：图片可能默认不显示，需要用户点击加载
4. **宽度限制**：邮件宽度建议控制在 600px 以内

### 6.3 推荐模板结构

```html
<table width="600" cellpadding="0" cellspacing="0" border="0">
    <tr>
        <td style="padding: 20px;">
            <h3 style="color: #333;">标题</h3>
            <p style="color: #666; line-height: 1.6;">正文内容</p>
        </td>
    </tr>
</table>
```

## 七、常见问题与解决方案

### 7.1 认证失败

**错误信息：** `AuthenticationFailedException`

**可能原因：**
- 授权码错误
- 授权码已过期
- 未开启 SMTP 服务

**解决方案：**
1. 重新获取授权码
2. 确认授权码没有多余空格
3. 检查 QQ 邮箱是否开启了 SMTP 服务

### 7.2 连接超时

**错误信息：** `MessagingException: Could not connect to SMTP host`

**可能原因：**
- 网络问题
- 防火墙阻止
- 端口被禁用

**解决方案：**
```yaml
# 尝试更换端口
spring:
  mail:
    host: smtp.qq.com
    port: 465  # 改为 SSL 端口
    properties:
      mail:
        smtp:
          ssl:
            enable: true
```

### 7.3 收件人收不到邮件

**可能原因：**
- 邮件被邮箱服务商识别为垃圾邮件
- 收件人邮箱地址错误
- 发件人邮箱被封禁

**解决方案：**
1. 检查垃圾邮件文件夹
2. 验证收件人邮箱地址
3. 避免邮件内容包含敏感词
4. 使用企业邮箱而非个人邮箱

### 7.4 邮件内容乱码

**错误信息：** 中文显示为乱码

**解决方案：**

Spring Boot 已自动配置字符编码，通常不需要手动处理。如果仍有问题：

```java
helper.setText(content, "UTF-8", "html"); // 手动指定编码
```

## 八、安全最佳实践

### 8.1 敏感信息保护

```yaml
# ❌ 错误做法：直接明文配置
spring:
  mail:
    password: mypassword123

# ✅ 正确做法：使用环境变量或配置中心
spring:
  mail:
    password: ${MAIL_PASSWORD}
```

### 8.2 发送频率限制

为了防止被判定为垃圾邮件发送者：

```java
// 使用 Redis 记录发送次数
public boolean canSendEmail(String email) {
    String key = "email:send:count:" + email;
    Long count = redisTemplate.opsForValue().increment(key);

    if (count == 1) {
        // 首次发送，设置过期时间
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    return count != null && count <= 10; // 每天最多发送10次
}
```

### 8.3 异常处理策略

```java
public boolean sendCodeEmail(String to, String code, String subject) {
    try {
        // 发送逻辑
        mailSender.send(message);
        return true;
    } catch (MailException e) {
        // 记录日志，但不要暴露敏感信息
        log.error("邮件发送失败，收件人：{}", maskEmail(to), e);
        return false;
    }
}

// 邮箱脱敏
private String maskEmail(String email) {
    if (email == null || !email.contains("@")) {
        return "***";
    }
    String[] parts = email.split("@");
    String name = parts[0];
    return name.substring(0, 2) + "***@" + parts[1];
}
```

## 九、总结

### 9.1 核心要点

| 要点 | 说明 |
|------|------|
| **依赖** | spring-boot-starter-mail |
| **核心类** | JavaMailSender、MimeMessage、MimeMessageHelper |
| **配置** | host、port、username、password、properties |
| **关键** | QQ 邮箱必须使用授权码而非登录密码 |
| **格式** | setText(content, true) 表示 HTML 格式 |

### 9.2 发送流程总结

```
1. 引入依赖 spring-boot-starter-mail
2. 配置 yml（服务器、端口、账号、授权码）
3. 注入 JavaMailSender
4. 创建 MimeMessage
5. 使用 MimeMessageHelper 设置邮件内容
6. 调用 send() 方法发送
```

### 9.3 调试建议

1. **先测试连通性**：使用 telnet 测试 SMTP 服务器是否可达
   ```bash
   telnet smtp.qq.com 587
   ```

2. **检查授权码**：确保使用的是 16 位授权码

3. **查看日志**：开启 DEBUG 模式查看完整通信过程
   ```yaml
   logging:
     level:
       org.springframework.mail: DEBUG
   ```

4. **逐步验证**：先发送简单文本邮件，再尝试 HTML 邮件
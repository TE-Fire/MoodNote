# JSON 字段大小写敏感问题解决方案

## 一、问题描述

在 Spring Boot 项目中，前后端交互时经常遇到 JSON 字段映射失败的问题。典型场景：

**前端请求体：**
```json
{
  "username": "testuser",
  "password": "123456",
  "captchakey": "abc123def456"
}
```

**后端 DTO：**
```java
@Data
public class LoginDTO {
    private String username;
    private String password;
    private String captchaKey;  // 注意：camelCase 驼峰命名
}
```

**现象：** `captchaKey` 字段始终为 `null`，导致验证失败。

---

## 二、根本原因

### 2.1 Jackson 默认行为

Spring Boot 默认使用 Jackson 进行 JSON 序列化/反序列化。Jackson 默认**区分大小写**，即：
- JSON 中的 `captchakey` ≠ Java 字段 `captchaKey`
- JSON 中的 `CAPTCHAKEY` ≠ Java 字段 `captchaKey`

### 2.2 字段映射机制

Jackson 通过 getter/setter 方法进行属性映射：
- 字段 `captchaKey` → setter 方法 `setCaptchaKey()`
- JSON 字段 `captchakey` 会尝试匹配 `setCaptchakey()` 方法
- 由于方法名不匹配，导致映射失败

---

## 三、解决方案

### 方案一：统一字段命名（推荐）

**前后端约定统一的命名规范：**

| 规范 | 示例 | 适用场景 |
|------|------|----------|
| **Camel Case（驼峰）** | `captchaKey` | Java/JavaScript 主流规范 |
| **Snake Case（下划线）** | `captcha_key` | Python/数据库字段 |
| **Kebab Case（连字符）** | `captcha-key` | URL 参数 |

**前端请求示例：**
```json
{
  "username": "testuser",
  "password": "123456",
  "captchaKey": "abc123def456"  // 使用驼峰命名
}
```

**优点：**
- 前后端代码风格一致
- 无需额外配置
- 代码可读性好

**缺点：**
- 需要前端配合修改

---

### 方案二：使用 @JsonProperty 注解

在 DTO 字段上添加 `@JsonProperty` 注解指定 JSON 字段名：

```java
@Data
public class LoginDTO {
    private String username;
    private String password;
    
    @JsonProperty("captchakey")  // 允许小写字段名
    private String captchaKey;
}
```

**支持多种命名格式：**

```java
@Data
public class UserDTO {
    @JsonProperty("user_name")  // 支持下划线
    private String userName;
    
    @JsonProperty("USER_ID")    // 支持大写
    private String userId;
    
    @JsonProperty("email")      // 保持原样
    private String email;
}
```

**优点：**
- 灵活适配不同的 JSON 字段命名
- 不影响 Java 代码风格
- 适合对接外部系统

**缺点：**
- 需要逐个字段添加注解
- 增加代码维护成本

---

### 方案三：全局配置忽略大小写

在 `application.yml` 中配置 Jackson 忽略大小写：

```yaml
spring:
  jackson:
    property-naming-strategy: SNAKE_CASE
    mapper:
      accept-case-insensitive-properties: true  # 忽略大小写
```

**配置说明：**

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `accept-case-insensitive-properties` | 是否忽略属性名大小写 | `false` |
| `property-naming-strategy` | 属性命名策略 | `LOWER_CAMEL_CASE` |

**命名策略选项：**

| 策略 | 说明 | 示例 |
|------|------|------|
| `LOWER_CAMEL_CASE` | 小写驼峰（默认） | `captchaKey` |
| `SNAKE_CASE` | 下划线分隔 | `captcha_key` |
| `LOWER_CASE` | 全小写 | `captchakey` |
| `KEBAB_CASE` | 连字符分隔 | `captcha-key` |

**优点：**
- 全局生效，无需修改代码
- 兼容性最强

**缺点：**
- 可能隐藏命名不一致问题
- 不符合严格的编码规范

---

### 方案四：自定义 ObjectMapper Bean

创建自定义的 `ObjectMapper` 配置：

```java
@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 忽略大小写
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        
        // 设置命名策略
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        
        // 其他配置...
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
```

**自定义命名策略示例：**

```java
// 自定义命名策略：驼峰转下划线
mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

// 自定义命名策略：保持原样
mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
```

**优点：**
- 完全自定义控制
- 适合复杂场景

**缺点：**
- 配置较为复杂
- 需要了解 Jackson API

---

## 四、最佳实践建议

### 4.1 团队协作规范

1. **统一命名约定**：团队内部约定统一的 JSON 字段命名规范（推荐驼峰命名）
2. **代码审查**：Code Review 时检查字段命名一致性
3. **API 文档**：在 API 文档中明确标注字段名和大小写

### 4.2 前后端联调流程

```
┌─────────────────────────────────────────────────────────┐
│              前后端联调流程                            │
├─────────────────────────────────────────────────────────┤
│                                                       │
│  1. 后端定义 DTO 类                                   │
│     └── 使用 Lombok @Data 注解                        │
│     └── 字段使用驼峰命名                              │
│                                                       │
│  2. 生成 API 文档                                    │
│     └── 明确标注字段名（如 Swagger）                  │
│                                                       │
│  3. 前端根据文档编写请求                              │
│     └── 严格按照文档中的字段名                        │
│                                                       │
│  4. 联调测试                                          │
│     └── 使用 Postman/Apifox 验证                     │
│     └── 检查日志中的字段映射                          │
│                                                       │
└─────────────────────────────────────────────────────────┘
```

### 4.3 调试技巧

**查看 Jackson 反序列化日志：**

```java
// 在 application.yml 中开启调试日志
logging:
  level:
    com.fasterxml.jackson: DEBUG
```

**使用 `@Validated` 验证字段：**

```java
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "验证码key不能为空")
    private String captchaKey;
}
```

---

## 五、常见问题排查

### Q1：字段值始终为 null

**原因：** JSON 字段名与 Java 字段名不匹配

**排查步骤：**
1. 检查 JSON 请求体字段名
2. 检查 Java DTO 字段名
3. 开启 Jackson 调试日志
4. 使用 `@JsonProperty` 显式指定字段名

### Q2：全局配置不生效

**原因：** 自定义 `ObjectMapper` 覆盖了全局配置

**解决方案：**
```java
@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary  // 确保是首选 Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.createXmlMapper(false).build();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return mapper;
    }
}
```

### Q3：日期字段反序列化失败

**原因：** 日期格式不匹配

**解决方案：**
```java
@Data
public class UserDTO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

---

## 六、总结

### 推荐方案选择

| 场景 | 推荐方案 | 理由 |
|------|----------|------|
| **新项目** | 方案一（统一命名） | 代码风格一致，易于维护 |
| **对接外部系统** | 方案二（@JsonProperty） | 灵活适配，不影响内部代码 |
| **遗留系统改造** | 方案三（全局配置） | 最小改动，快速兼容 |
| **复杂场景** | 方案四（自定义配置） | 完全控制，适合定制化需求 |

### 核心原则

1. **一致性**：保持前后端字段命名一致
2. **明确性**：使用注解显式声明字段映射关系
3. **可维护性**：选择最适合团队的方案并坚持使用

---

## 附录：Jackson 常用注解

| 注解 | 说明 | 示例 |
|------|------|------|
| `@JsonProperty` | 指定 JSON 字段名 | `@JsonProperty("user_name")` |
| `@JsonIgnore` | 忽略字段 | `@JsonIgnore private String password` |
| `@JsonFormat` | 指定日期格式 | `@JsonFormat(pattern = "yyyy-MM-dd")` |
| `@JsonAlias` | 支持多个字段名 | `@JsonAlias({"name", "username"})` |
| `@JsonPropertyOrder` | 指定序列化顺序 | `@JsonPropertyOrder({"id", "name"})` |

---

**参考文献：**
- [Jackson Documentation](https://github.com/FasterXML/jackson)
- [Spring Boot Jackson Configuration](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto.spring-mvc.customize-jackson-objectmapper)

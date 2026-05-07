# Redis 配置与序列化器详解

## 一、概述

在 Spring Boot 项目中使用 Redis 时，序列化器的配置是一个关键环节。它决定了 Java 对象如何转换为 Redis 可存储的字节数据，以及如何从 Redis 中读取并还原为 Java 对象。

## 二、Redis 配置类解析

### 2.1 配置类整体结构

```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 配置逻辑
    }
}
```

**关键注解说明：**

| 注解 | 作用 |
|------|------|
| `@Configuration` | 标识这是一个配置类，Spring 会扫描并加载其中的 Bean |
| `@Bean` | 声明一个 Bean，Spring 会自动管理其生命周期 |

### 2.2 RedisTemplate 的作用

`RedisTemplate` 是 Spring Data Redis 提供的核心操作类，它封装了 Redis 的各种操作：

- **String 类型操作**：`opsForValue()`
- **Hash 类型操作**：`opsForHash()`
- **List 类型操作**：`opsForList()`
- **Set 类型操作**：`opsForSet()`
- **ZSet 类型操作**：`opsForZSet()`

### 2.3 完整配置流程

我们的配置类执行了以下步骤：

1. **创建 RedisTemplate 实例**
2. **设置连接工厂**
3. **配置 ObjectMapper**
4. **创建序列化器**
5. **分配序列化器**
6. **初始化模板**

## 三、序列化器深度解析

### 3.1 什么是序列化器

**序列化**：将 Java 对象转换为字节序列的过程。

**反序列化**：将字节序列还原为 Java 对象的过程。

在 Redis 中，所有数据最终都以字节形式存储，序列化器就是负责完成这个转换工作的组件。

### 3.2 为什么需要自定义序列化器

Spring Data Redis 默认使用 `JdkSerializationRedisSerializer`，它有以下缺点：

| 问题 | 说明 |
|------|------|
| **可读性差** | 序列化后是二进制数据，Redis GUI 工具中无法直接查看 |
| **跨语言不兼容** | 其他语言（如 Python、Node.js）无法读取 |
| **性能较差** | 序列化结果体积较大 |

### 3.3 StringRedisSerializer

**作用**：将字符串直接转换为字节数组。

```java
StringRedisSerializer stringSerializer = new StringRedisSerializer();
```

**使用场景**：
- Redis 的 Key（键）通常是字符串
- Hash 结构的 Field（字段名）
- 简单的字符串值

**优势**：
- 速度快
- 结果可读
- 兼容性好

### 3.4 Jackson2JsonRedisSerializer

**作用**：将 Java 对象序列化为 JSON 字符串。

```java
Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
```

**工作原理**：
1. 使用 Jackson 库将对象转换为 JSON 字符串
2. 将 JSON 字符串转换为字节数组存储
3. 读取时反向操作还原为对象

**优势**：
- **可读性强**：JSON 格式人类可读
- **跨语言兼容**：几乎所有语言都支持 JSON
- **体积较小**：相比 Java 原生序列化更紧凑

### 3.5 ObjectMapper 配置详解

`ObjectMapper` 是 Jackson 库的核心类，负责 JSON 序列化和反序列化：

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
objectMapper.registerModule(new JavaTimeModule());
objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
```

**配置项说明：**

| 配置 | 作用 |
|------|------|
| `Visibility.ANY` | 允许访问所有字段（包括私有字段） |
| `JavaTimeModule` | 支持 Java 8 时间类型（LocalDateTime 等） |
| `WRITE_DATES_AS_TIMESTAMPS` | 禁用时间戳格式，使用 ISO-8601 格式 |

## 四、序列化器分配策略

```java
template.setKeySerializer(stringSerializer);
template.setHashKeySerializer(stringSerializer);
template.setValueSerializer(jsonSerializer);
template.setHashValueSerializer(jsonSerializer);
```

**分配原则：**

| 组件 | 序列化器 | 原因 |
|------|----------|------|
| Key | StringRedisSerializer | Key 通常是字符串，需要可读 |
| Hash Key | StringRedisSerializer | Hash 的字段名也是字符串 |
| Value | Jackson2JsonRedisSerializer | 值可能是复杂对象，需要 JSON 序列化 |
| Hash Value | Jackson2JsonRedisSerializer | Hash 的值也可能是复杂对象 |

## 五、序列化流程演示

### 5.1 存储对象到 Redis

假设我们存储一个 User 对象：

```java
User user = new User();
user.setId(1L);
user.setUsername("zhangsan");
user.setEmail("zhangsan@example.com");

redisTemplate.opsForValue().set("user:1", user);
```

**序列化过程：**

```
User 对象
    ↓ Jackson2JsonRedisSerializer
{"id":1,"username":"zhangsan","email":"zhangsan@example.com"}
    ↓ StringRedisSerializer（内部转换）
字节数组存储到 Redis
```

### 5.2 从 Redis 读取对象

```java
User user = (User) redisTemplate.opsForValue().get("user:1");
```

**反序列化过程：**

```
Redis 读取字节数组
    ↓ Jackson2JsonRedisSerializer
{"id":1,"username":"zhangsan","email":"zhangsan@example.com"}
    ↓ ObjectMapper
User 对象
```

## 六、序列化器对比

### 6.1 常用序列化器对比表

| 序列化器 | 优点 | 缺点 | 适用场景 |
|----------|------|------|----------|
| **JdkSerializationRedisSerializer** | 支持所有对象，保留类型信息 | 二进制不可读，体积大 | 内部系统，需要精确类型还原 |
| **StringRedisSerializer** | 速度快，可读 | 只支持字符串 | Key、简单字符串值 |
| **Jackson2JsonRedisSerializer** | 可读，跨语言，体积小 | 需要配置 ObjectMapper | 大多数业务场景 |
| **GenericJackson2JsonRedisSerializer** | 自动保存类型信息 | 序列化结果稍大 | 需要多态类型支持 |

### 6.2 选择建议

```
业务数据存储 → Jackson2JsonRedisSerializer
配置项存储 → StringRedisSerializer  
复杂类型/多态 → GenericJackson2JsonRedisSerializer
内部临时数据 → JdkSerializationRedisSerializer
```

## 七、实际效果展示

### 7.1 使用默认序列化器

```
Redis 中存储的内容：
user:1 → \xAC\xED\x00\x05t\x00\x10...（二进制乱码）
```

### 7.2 使用 JSON 序列化器

```
Redis 中存储的内容：
user:1 → {"id":1,"username":"zhangsan","email":"zhangsan@example.com"}
```

在 Redis Desktop Manager 等 GUI 工具中可以直接阅读！

## 八、常见问题与解决方案

### 8.1 问题一：时间类型序列化失败

**现象**：存储包含 LocalDateTime 的对象时报错。

**原因**：Jackson 默认不支持 Java 8 时间类型。

**解决方案**：

```java
objectMapper.registerModule(new JavaTimeModule());
objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
```

### 8.2 问题二：反序列化时类型转换异常

**现象**：`ClassCastException`

**原因**：存储时用了 `Object.class`，读取时需要指定具体类型。

**解决方案**：

```java
// 方法一：使用具体类型的序列化器
Jackson2JsonRedisSerializer<User> serializer = new Jackson2JsonRedisSerializer<>(User.class);

// 方法二：读取时强转
User user = (User) redisTemplate.opsForValue().get("user:1");

// 方法三：使用工具类封装
<T> T get(String key, Class<T> clazz) {
    Object value = redisTemplate.opsForValue().get(key);
    return value != null ? (T) value : null;
}
```

### 8.3 问题三：Redis GUI 中显示乱码

**现象**：Redis Desktop Manager 中看到的是二进制数据。

**原因**：使用了 JdkSerializationRedisSerializer 或序列化器配置不正确。

**解决方案**：确保 Value 使用 Jackson2JsonRedisSerializer。

## 九、总结

### 9.1 配置要点

1. **Key 使用 StringRedisSerializer**：保证键的可读性
2. **Value 使用 Jackson2JsonRedisSerializer**：保证值的可读性和跨语言兼容性
3. **配置 ObjectMapper**：支持 Java 8 时间类型和正确的日期格式
4. **调用 afterPropertiesSet()**：完成模板初始化

### 9.2 设计原则

- **可读性优先**：选择人类可读的序列化格式
- **兼容性考虑**：考虑是否需要与其他语言交互
- **性能平衡**：JSON 序列化在大多数场景下性能足够

### 9.3 最佳实践

```java
// 推荐的配置模式
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    om.registerModule(new JavaTimeModule());
    om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
    Jackson2JsonRedisSerializer<Object> jsonSer = new Jackson2JsonRedisSerializer<>(om, Object.class);
    StringRedisSerializer strSer = new StringRedisSerializer();
    
    template.setKeySerializer(strSer);
    template.setHashKeySerializer(strSer);
    template.setValueSerializer(jsonSer);
    template.setHashValueSerializer(jsonSer);
    
    template.afterPropertiesSet();
    return template;
}
```

通过合理配置序列化器，我们可以让 Redis 中的数据既高效又易于维护，这是构建高质量 Spring Boot 应用的重要一环。
# Skill: 跨域配置

## 触发条件
当前端应用需要从不同域名访问后端 API 时，需要配置跨域支持。

## 前置依赖
- Skill-008-MyBatis-Plus配置

## 执行规范

### 文件位置
- 跨域配置类：`moodnote-server/src/main/java/com/moodnote/config/`

### 命名规范
- 配置类名称：`CorsConfig.java`

### 代码规范
- 使用 Spring Boot 的 CORS 配置
- 配置允许的源、方法、头部等
- 配置预检请求的缓存时间

### 依赖引入
- Spring Web 依赖

## 代码模板

### 模板说明
配置跨域支持，允许前端应用访问后端 API。

### 代码示例

#### CorsConfig.java 跨域配置类
```java
package com.moodnote.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*") // 在生产环境中应该配置具体的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*)")
                .exposedHeaders("Content-Type", "Authorization")
                .allowCredentials(true)
                .maxAge(3600); // 预检请求的缓存时间
    }
}
```

## 验收标准
1. 跨域配置类创建成功
2. 配置了允许的源、方法、头部等
3. 预检请求缓存时间设置合理
4. 前端应用能够正常访问后端 API

## 关联 Skill
前置：Skill-008-MyBatis-Plus配置

后置：Skill-010-日志配置
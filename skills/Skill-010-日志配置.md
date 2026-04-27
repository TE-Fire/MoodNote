# Skill: 日志配置

## 触发条件
当需要配置应用的日志系统，确保日志输出格式一致且便于排查问题时。

## 前置依赖
- Skill-009-跨域配置

## 执行规范

### 文件位置
- 日志配置文件：`moodnote-server/src/main/resources/`

### 命名规范
- 日志配置文件：`logback-spring.xml`

### 代码规范
- 使用 Logback 作为日志框架
- 配置不同环境的日志输出
- 配置日志文件滚动策略
- 配置日志级别

### 依赖引入
- Spring Boot 默认包含 Logback 依赖

## 代码模板

### 模板说明
配置 Logback 日志系统，设置不同环境的日志输出格式和策略。

### 代码示例

#### logback-spring.xml 日志配置文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 日志输出格式 -->
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <!-- 日志文件路径 -->
    <property name="LOG_PATH" value="logs"/>
    
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- 日志文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/moodnote.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/moodnote-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- 错误日志文件输出 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/moodnote-error.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/moodnote-error-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
    </appender>
    
    <!-- 根日志配置 -->
    <root level="info">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    
    <!-- 包级别日志配置 -->
    <logger name="com.moodnote" level="debug" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </logger>
    
    <!-- 第三方库日志配置 -->
    <logger name="org.springframework" level="info"/>
    <logger name="com.baomidou" level="info"/>
    <logger name="java.sql" level="info"/>
</configuration>
```

## 验收标准
1. 日志配置文件创建成功
2. 配置了控制台输出
3. 配置了文件输出和滚动策略
4. 配置了错误日志单独输出
5. 包级别日志配置合理

## 关联 Skill
前置：Skill-009-跨域配置

后置：Skill-011-创建BaseEntity基类
# Skill: 创建SpringBoot启动类

## 触发条件
当需要创建 Spring Boot 应用的启动类时。

## 前置依赖
- Skill-025-创建统计Controller

## 执行规范

### 文件位置
- 启动类文件：`moodnote-server/src/main/java/com/moodnote/`

### 命名规范
- 启动类名称：`MoodNoteApplication.java`

### 代码规范
- 使用 @SpringBootApplication 注解
- 配置组件扫描路径
- 配置 MyBatis-Plus 扫描路径
- 包含 main 方法

### 依赖引入
- Spring Boot 依赖

## 代码模板

### 模板说明
创建 Spring Boot 应用的启动类，配置必要的注解和扫描路径。

### 代码示例

#### MoodNoteApplication.java 启动类
```java
package com.moodnote;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.moodnote.mapper")
public class MoodNoteApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MoodNoteApplication.class, args);
    }
}
```

## 验收标准
1. Spring Boot 启动类创建成功
2. 使用了 @SpringBootApplication 注解
3. 配置了 Mapper 扫描路径
4. 包含了 main 方法
5. 应用能够正常启动

## 关联 Skill
前置：Skill-025-创建统计Controller

后置：Skill-027-Axios封装与拦截器
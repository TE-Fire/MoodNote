# Skill: 数据库连接配置

## 触发条件
当需要配置数据库连接和相关的数据源设置时。

## 前置依赖
- Skill-006-统一返回格式

## 执行规范

### 文件位置
- 数据库配置类：`moodnote-common/src/main/java/com/moodnote/config/`
- 数据库初始化脚本：`moodnote-server/src/main/resources/db/`

### 命名规范
- 配置类名称：`DataSourceConfig.java`
- 初始化脚本：`schema.sql`

### 代码规范
- 使用 Spring Boot 的数据源配置
- 配置连接池参数
- 提供数据库初始化脚本

### 依赖引入
- Spring Boot JDBC 依赖
- MySQL 驱动依赖

## 代码模板

### 模板说明
配置数据库连接和初始化脚本。

### 代码示例

#### 1. DataSourceConfig.java 数据源配置
```java
package com.moodnote.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        
        // 配置连接池参数
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setIdleTimeout(30000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setConnectionTimeout(30000);
        
        return dataSource;
    }
}
```

#### 2. schema.sql 数据库初始化脚本
```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS moodnote CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE moodnote;

-- 日记表
CREATE TABLE IF NOT EXISTS mood_diary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    mood_type TINYINT NOT NULL COMMENT '1开心/2平静/3难过/4焦虑/5生气',
    weather_type TINYINT NOT NULL COMMENT '1晴/2多云/3阴/4雨/5雪',
    city VARCHAR(50) DEFAULT NULL,
    is_private TINYINT DEFAULT 1 COMMENT '0公开/1私密',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted TINYINT DEFAULT 0 COMMENT '0正常/1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 标签表
CREATE TABLE IF NOT EXISTS mood_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE,
    color VARCHAR(7) DEFAULT '#409EFF',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted TINYINT DEFAULT 0 COMMENT '0正常/1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 日记-标签关联表
CREATE TABLE IF NOT EXISTS mood_diary_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    diary_id BIGINT NOT NULL COMMENT '日记ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME NOT NULL,
    deleted TINYINT DEFAULT 0 COMMENT '0正常/1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建索引
CREATE INDEX idx_mood_diary_create_time ON mood_diary(create_time);
CREATE INDEX idx_mood_diary_mood_type ON mood_diary(mood_type);
CREATE INDEX idx_mood_diary_tag_diary_id ON mood_diary_tag(diary_id);
CREATE INDEX idx_mood_diary_tag_tag_id ON mood_diary_tag(tag_id);
```

## 验收标准
1. 数据源配置类创建成功
2. 数据库初始化脚本创建成功
3. 数据库表结构符合设计要求
4. 索引创建合理
5. 连接池参数配置合理

## 关联 Skill
前置：Skill-006-统一返回格式

后置：Skill-008-MyBatis-Plus配置
# Skill: MyBatis-Plus配置

## 触发条件
当需要配置 MyBatis-Plus 插件和相关设置时。

## 前置依赖
- Skill-007-数据库连接配置

## 执行规范

### 文件位置
- MyBatis-Plus 配置类：`moodnote-common/src/main/java/com/moodnote/config/`

### 命名规范
- 配置类名称：`MyBatisPlusConfig.java`

### 代码规范
- 配置分页插件
- 配置乐观锁插件
- 配置 SQL 执行效率分析插件（开发环境）

### 依赖引入
- MyBatis-Plus 依赖

## 代码模板

### 模板说明
配置 MyBatis-Plus 的核心插件和设置。

### 代码示例

#### MyBatisPlusConfig.java 配置类
```java
package com.moodnote.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        return interceptor;
    }
}
```

## 验收标准
1. MyBatis-Plus 配置类创建成功
2. 分页插件配置正确
3. 乐观锁插件配置正确
4. 插件配置符合项目需求

## 关联 Skill
前置：Skill-007-数据库连接配置

后置：Skill-009-跨域配置
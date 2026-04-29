# MyBatis-Plus 配置详解

## 概述

MyBatis-Plus 是 MyBatis 的增强工具，在 MyBatis 的基础上提供了更便捷的 CRUD 操作、分页功能、乐观锁等特性，无需编写 XML 文件即可完成基本的数据库操作。

## 核心配置类解析

### MyBatisPlusConfig.java

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

---

## 一、分页插件（PaginationInnerInterceptor）

### 1.1 什么是分页插件？

分页插件是 MyBatis-Plus 最常用的插件之一，它能够自动拦截 SQL 并添加分页语句（`LIMIT`），让我们在查询时无需手动处理分页逻辑。

### 1.2 工作原理

MyBatis-Plus 的分页插件基于 MyBatis 的拦截器机制实现：

1. **拦截执行方法**：当执行 SELECT 查询时，分页插件会拦截 SQL 执行
2. **分析 SQL**：判断是否为查询语句
3. **改写 SQL**：在 SQL 末尾添加 `LIMIT` 语句
4. **返回结果**：返回分页后的数据列表和总记录数

```
原 SQL: SELECT * FROM mood_diary
分页 SQL: SELECT * FROM mood_diary LIMIT 0, 10
统计总数: SELECT COUNT(*) FROM mood_diary
```

### 1.3 核心配置方法

| 方法 | 说明 | 示例值 |
|------|------|--------|
| `setDbType()` | 设置数据库类型 | `DbType.MYSQL` |
| `setMaxLimit()` | 设置单页最大记录数 | `1000L` |
| `setOverflow()` | 设置溢出是否翻页 | `false` |

### 1.4 使用示例

```java
// Service 层分页查询
public Page<Diary> getDiaryPage(int current, int size) {
    return diaryMapper.selectPage(
        new Page<>(current, size),
        new QueryWrapper<Diary>().eq("deleted", 0)
    );
}
```

### 1.5 分页原理图解

```
┌─────────────────────────────────────────────────────────────┐
│                        用户请求分页数据                      │
│                      Page<Diary> page =                     │
│                      new Page<>(1, 10)                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    MybatisPlus 拦截器                        │
│  1. 判断是否为查询语句                                       │
│  2. 获取 Page 对象中的分页参数                                │
│  3. 改写 SQL，添加 LIMIT 子句                                │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
    ┌──────────────────┐           ┌──────────────────┐
    │   SELECT SQL      │           │   COUNT SQL      │
    │ LIMIT 0, 10       │           │   统计总数        │
    └──────────────────┘           └──────────────────┘
              │                               │
              ▼                               ▼
    ┌──────────────────┐           ┌──────────────────┐
    │   返回数据列表    │           │   返回总记录数    │
    └──────────────────┘           └──────────────────┘
              │                               │
              └───────────────┬───────────────┘
                              ▼
              ┌──────────────────────────────┐
              │     返回完整分页结果          │
              │  records: 数据列表            │
              │  total: 总记录数              │
              │  pages: 总页数               │
              └──────────────────────────────┘
```

---

## 二、乐观锁插件（OptimisticLockerInnerInterceptor）

### 2.1 什么是乐观锁？

乐观锁（Optimistic Locking）是一种并发控制策略，假设多个事务在大多数情况下不会相互干扰。在更新数据时，乐观锁会检查数据是否被其他事务修改过，如果被修改则更新失败。

### 2.2 工作原理

乐观锁的核心思想是：**先查后改，修改时检查版本号**

1. **读取数据**：读取数据时同时获取当前版本号
2. **修改数据**：更新时将版本号作为条件
3. **检查结果**：如果影响行数为 0，说明数据被修改过

```
UPDATE mood_diary
SET content = '新内容', version = version + 1
WHERE id = 1 AND version = 1
```

### 2.3 实体类配置

使用乐观锁需要在实体类中添加 `@Version` 注解的字段：

```java
@Data
@TableName("mood_diary")
public class Diary extends BaseEntity {

    private String title;
    private String content;

    @Version
    private Integer version;
}
```

### 2.4 乐观锁 vs 悲观锁

| 特性 | 乐观锁 | 悲观锁 |
|------|--------|--------|
| 思想 | 假设不会冲突 | 假设会冲突 |
| 实现方式 | 版本号/时间戳 | 数据库锁机制 |
| 适用场景 | 读多写少 | 写多读少 |
| 性能 | 高并发下性能好 | 性能较低 |
| 典型实现 | `@Version` 注解 | `SELECT FOR UPDATE` |

### 2.5 乐观锁流程图解

```
┌─────────────────────────────────────────────────────────────┐
│                    事务 A 修改数据                          │
│                 UPDATE SET ... WHERE id = 1                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  事务 A 检查版本号: WHERE id = 1 AND version = 1           │
│  版本号匹配，更新成功，version 变为 2                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
              ┌─────────────────────────────────┐
              │         事务 B 修改数据          │
              │     UPDATE SET ... WHERE        │
              │         id = 1 AND version = 1 │
              └─────────────────────────────────┘
                              │
                              ▼
              ┌─────────────────────────────────┐
              │  事务 B 检查版本号: version = 1  │
              │  但现在 version = 2（已被修改）   │
              │  条件不匹配，更新失败！           │
              └─────────────────────────────────┘
```

---

## 三、MybatisPlusInterceptor 拦截器链

### 3.1 什么是拦截器链？

MybatisPlusInterceptor 是 MyBatis-Plus 的核心拦截器，它管理着多个内部拦截器（InnerInterceptor），按照添加顺序依次执行。

### 3.2 拦截器执行顺序

```
请求进入
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│               MybatisPlusInterceptor 拦截器链               │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ 1. PaginationInnerInterceptor（分页插件）           │     │
│  │    - 改写 SQL，添加 LIMIT                            │     │
│  │    - 执行 COUNT 统计                                 │     │
│  └─────────────────────────────────────────────────────┘     │
│                          │                                   │
│                          ▼                                   │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ 2. OptimisticLockerInnerInterceptor（乐观锁插件）     │     │
│  │    - 拦截 UPDATE/DELETE                             │     │
│  │    - 添加版本号条件                                  │     │
│  └─────────────────────────────────────────────────────┘     │
│                          │                                   │
│                          ▼                                   │
│  ┌─────────────────────────────────────────────────────┐     │
│  │ 3. 其他插件...                                      │     │
│  └─────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                       执行 SQL
```

### 3.3 常用插件一览

| 插件名称 | 说明 |
|---------|------|
| PaginationInnerInterceptor | 分页插件 |
| OptimisticLockerInnerInterceptor | 乐观锁插件 |
| IllegalSQLInnerInterceptor | 攻击 SQL 阻断插件 |
| TenantLineInnerInterceptor | 多租户插件 |
| DynamicTableNameInnerInterceptor | 动态表名插件 |

---

## 四、配置类的作用与意义

### 4.1 为什么需要配置类？

虽然 MyBatis-Plus 提供了自动配置功能，但在实际项目中，我们往往需要：

1. **定制化配置**：根据业务需求调整插件参数
2. **优化性能**：设置合理的分页大小、连接池参数
3. **功能扩展**：添加自定义拦截器

### 4.2 配置类的优势

```
手动配置                          自动配置
─────────────────────────────────────────────────
✓ 灵活性高                      ✗ 灵活性低
✓ 参数可控                      ✗ 参数固定
✓ 便于理解原理                  ✗ 黑盒操作
✓ 适合生产环境                  ✗ 仅适合简单场景
```

### 4.3 与 application.yml 配置的关系

MyBatisPlusConfig 配置类 ≠ application.yml 中的配置

- **MyBatisPlusConfig**：配置插件行为，如分页、乐观锁
- **application.yml**：配置数据源、SQL 日志、驼峰命名转换等

两者相互补充，共同完成 MyBatis-Plus 的配置。

---

## 五、常见问题与解决方案

### 5.1 分页不生效

**问题**：调用分页方法但返回全部数据

**解决方案**：
1. 检查是否正确注册了分页插件
2. 确保使用的是 MyBatis-Plus 的 `Page` 对象
3. 检查是否在 Mapper 方法上使用了 `@InterceptorIgnore` 注解

### 5.2 乐观锁更新失败

**问题**：更新时影响行数为 0

**原因**：数据已被其他事务修改

**解决方案**：
```java
public boolean updateDiary(Diary diary) {
    int rows = diaryMapper.updateById(diary);
    if (rows == 0) {
        throw new BusinessException("数据已被修改，请刷新后重试");
    }
    return true;
}
```

### 5.3 分页参数越界

**问题**：传入过大的页码导致查询慢

**解决方案**：设置 `maxLimit` 限制最大返回条数

```java
paginationInnerInterceptor.setMaxLimit(1000L);
paginationInnerInterceptor.setOverflow(false); // 超页返回空
```

---

## 六、总结

### 配置类核心要点

| 要点 | 说明 |
|------|------|
| `@Configuration` | 标注为配置类，由 Spring 管理 |
| `@Bean` | 将拦截器注入到容器中 |
| 拦截器链 | 按添加顺序执行，先分页后乐观锁 |
| 分页原理 | 改写 SQL + COUNT 统计 |
| 乐观锁原理 | 版本号比较，失败则重试 |

### 学习建议

1. **理解拦截器机制**：MyBatis 拦截器是核心
2. **掌握分页原理**：SQL 改写 + COUNT
3. **区分锁的分类**：乐观锁 vs 悲观锁
4. **实践出真知**：多写代码，多调试

---

## 相关资源

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [MyBatis 拦截器机制](https://mybatis.org/mybatis-3/zh/configuration.html#plugins)
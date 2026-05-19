# PageHelper 分页插件详解

## 概述

PageHelper 是一款基于 MyBatis 的分页插件，由国内开发者开发维护，是目前最流行的 MyBatis 分页解决方案之一。它通过 MyBatis 的拦截器机制，在执行 SQL 时自动添加分页语句，实现零侵入式的分页功能。

---

## 一、基本使用方法

### 1.1 依赖配置

在 `pom.xml` 中添加 PageHelper 依赖：

```xml
<!-- PageHelper 分页插件 -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>2.1.1</version>
</dependency>
```

### 1.2 基础使用示例

PageHelper 的使用非常简单，核心 API 是 `PageHelper.startPage()` 方法：

```java
@Override
public Result<PageResult<DiaryVO>> getList(DiaryQueryDTO diaryQueryDTO) {
    // 设置默认分页参数
    int pageNum = diaryQueryDTO.getPageNum() != null ? diaryQueryDTO.getPageNum() : 1;
    int pageSize = diaryQueryDTO.getPageSize() != null ? diaryQueryDTO.getPageSize() : 10;

    // 开启分页，紧跟其后的第一个查询会被分页
    PageHelper.startPage(pageNum, pageSize);

    // 查询日记列表（PageHelper会自动添加分页）
    List<Diary> diaryList = diaryMapper.selectDiaryList(
            userId,
            diaryQueryDTO.getKeyword(),
            diaryQueryDTO.getMoodType(),
            diaryQueryDTO.getWeatherType(),
            diaryQueryDTO.getStartDate(),
            diaryQueryDTO.getEndDate()
    );

    // 获取分页信息
    Page<Diary> page = (Page<Diary>) diaryList;
    long total = page.getTotal();

    // 构建分页结果
    PageResult<DiaryVO> pageResult = PageResult.build(total, pageSize, pageNum, voList);
    return Result.success(pageResult);
}
```

### 1.3 SQL 编写方式

使用 PageHelper 时，Mapper XML 中的 SQL 无需手动添加 `LIMIT` 语句：

```xml
<!-- DiaryMapper.xml -->
<select id="selectDiaryList" resultType="com.moodnote.pojo.entity.Diary">
    SELECT 
        d.id, d.user_id, d.title, d.content, d.mood_type, 
        d.weather_type, d.city, d.is_private, d.create_time, d.update_time
    FROM mood_diary d
    WHERE d.deleted = 0
      AND d.user_id = #{userId}
    <if test="keyword != null and keyword != ''">
        AND (d.title LIKE CONCAT('%', #{keyword}, '%') 
             OR d.content LIKE CONCAT('%', #{keyword}, '%'))
    </if>
    ORDER BY d.create_time DESC
</select>
```

### 1.4 核心 API 说明

| 方法 | 说明 | 参数 |
|------|------|------|
| `PageHelper.startPage(pageNum, pageSize)` | 开启分页 | `pageNum`: 页码，`pageSize`: 每页条数 |
| `PageHelper.startPage(pageNum, pageSize, count)` | 开启分页（可控制是否查询总数） | `count`: 是否执行 COUNT 查询 |
| `PageHelper.offsetPage(offset, pageSize)` | 偏移量分页 | `offset`: 偏移量 |
| `PageHelper.clearPage()` | 清除分页参数 | 无 |

---

## 二、与 MyBatis 的集成机制

### 2.1 MyBatis 拦截器机制

PageHelper 基于 MyBatis 的 `Interceptor` 接口实现分页功能：

```
┌─────────────────────────────────────────────────────────────┐
│                    MyBatis 执行流程                          │
├─────────────────────────────────────────────────────────────┤
│                                                            │
│  SqlSessionFactory                                         │
│        │                                                   │
│        ▼                                                   │
│  SqlSession ──────────────────────────────────────────────►│
│        │                                                  │
│        ▼                                                  │
│  Executor (执行器)                                         │
│        │                                                  │
│        ▼                                                  │
│  ┌──────────────────────┐                                 │
│  │   PageInterceptor    │ ◄─── PageHelper 拦截器           │
│  │   (分页拦截器)        │                                 │
│  └──────────────────────┘                                 │
│        │                                                  │
│        ▼                                                  │
│  StatementHandler ───────────────────► 执行 SQL            │
│                                                            │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 分页执行流程

PageHelper 的分页执行分为四个阶段：

```
┌─────────────────────────────────────────────────────────────┐
│  1. PageHelper.startPage(pageNum, pageSize)               │
│     将分页参数存入 ThreadLocal                             │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  2. 执行 Mapper 查询方法                                   │
│     触发 MyBatis 拦截器链                                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  3. PageInterceptor 拦截 SQL                               │
│     - 从 ThreadLocal 获取分页参数                          │
│     - 分析 SQL 类型                                        │
│     - 改写 SQL，添加 LIMIT 子句                            │
│     - 执行 COUNT 查询统计总数                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  4. 返回分页结果                                          │
│     - 封装 Page 对象                                      │
│     - 包含数据列表和总记录数                               │
│     - 清空 ThreadLocal 中的分页参数                        │
└─────────────────────────────────────────────────────────────┘
```

### 2.3 ThreadLocal 参数传递

PageHelper 使用 `ThreadLocal` 存储分页参数，确保线程安全：

```java
// PageHelper 内部机制简化示意
public class PageHelper {
    // ThreadLocal 存储当前线程的分页参数
    private static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal<>();

    public static <E> Page<E> startPage(int pageNum, int pageSize) {
        Page<E> page = new Page<>(pageNum, pageSize);
        LOCAL_PAGE.set(page);
        return page;
    }

    // 拦截器中获取分页参数
    public static <E> Page<E> getLocalPage() {
        return LOCAL_PAGE.get();
    }

    // 查询完成后清除
    public static void clearPage() {
        LOCAL_PAGE.remove();
    }
}
```

### 2.4 SQL 改写机制

PageHelper 通过 `PageInterceptor` 拦截器改写 SQL：

```
原 SQL:
SELECT id, title, content FROM mood_diary WHERE user_id = 1 ORDER BY create_time DESC

改写后:
SELECT id, title, content FROM mood_diary WHERE user_id = 1 ORDER BY create_time DESC LIMIT 0, 10

COUNT SQL:
SELECT COUNT(0) FROM mood_diary WHERE user_id = 1
```

---

## 三、深入理解分页机制

### 3.1 分页参数边界处理

PageHelper 会自动处理分页参数的边界情况：

```java
// 设置默认分页参数
int pageNum = diaryQueryDTO.getPageNum() != null ? diaryQueryDTO.getPageNum() : 1;
int pageSize = diaryQueryDTO.getPageSize() != null ? diaryQueryDTO.getPageSize() : 10;

// 边界收敛：确保 pageNum >= 1，pageSize 在合理范围
if (pageNum < 1) pageNum = 1;
if (pageSize < 1) pageSize = 10;
if (pageSize > 100) pageSize = 100; // 限制最大每页条数
```

### 3.2 分页结果封装

项目中自定义了 `PageResult` 类来封装分页结果：

```java
@Data
public class PageResult<T> implements Serializable {
    private long total;      // 总记录数
    private int pageSize;    // 每页条数
    private int pageNum;     // 当前页码
    private List<T> list;    // 数据列表

    public static <T> PageResult<T> build(long total, int pageSize, int pageNum, List<T> list) {
        return new PageResult<>(total, pageSize, pageNum, list);
    }
}
```

### 3.3 Page 对象结构

PageHelper 返回的 `Page` 对象包含丰富的分页信息：

| 属性 | 类型 | 说明 |
|------|------|------|
| `pageNum` | int | 当前页码 |
| `pageSize` | int | 每页条数 |
| `size` | int | 当前页实际条数 |
| `startRow` | int | 当前页起始行号 |
| `endRow` | int | 当前页结束行号 |
| `total` | long | 总记录数 |
| `pages` | int | 总页数 |
| `list` | List | 数据列表 |
| `firstPage` | boolean | 是否第一页 |
| `lastPage` | boolean | 是否最后一页 |
| `hasPreviousPage` | boolean | 是否有上一页 |
| `hasNextPage` | boolean | 是否有下一页 |

---

## 四、配置与优化

### 4.1 application.yml 配置

```yaml
pagehelper:
  helper-dialect: mysql           # 数据库方言
  reasonable: true                # 分页合理化（页码越界时自动调整）
  support-methods-arguments: true # 支持通过方法参数传递分页参数
  params: count=countSql          # COUNT 查询参数
```

### 4.2 配置参数说明

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `helper-dialect` | 数据库方言（mysql/oracle/sqlserver/postgresql） | mysql |
| `reasonable` | 分页合理化，pageNum <= 0 时查询第一页，pageNum > pages 时查询最后一页 | false |
| `support-methods-arguments` | 是否支持通过 Mapper 方法参数传递分页参数 | false |
| `params` | 用于从方法参数中获取值的参数名 | count=countSql |
| `auto-dialect` | 自动检测数据库方言 | true |

### 4.3 性能优化建议

1. **限制最大每页条数**：防止单次查询过多数据
2. **合理使用 COUNT 查询**：对于不需要总数的场景，可关闭 COUNT 查询
3. **索引优化**：确保 WHERE 和 ORDER BY 子句中的字段有索引
4. **避免深度分页**：大量数据时，使用游标或 Keyset 分页替代 OFFSET

---

## 五、与 MyBatis-Plus 分页对比

项目中同时提到了 MyBatis-Plus，但实际使用的是 PageHelper，两者对比：

| 特性 | PageHelper | MyBatis-Plus |
|------|------------|--------------|
| **依赖** | `pagehelper-spring-boot-starter` | `mybatis-plus-boot-starter` |
| **使用方式** | `PageHelper.startPage()` 静态方法 | `IPage<T>` 对象 |
| **SQL 编写** | 普通 SQL，自动添加 LIMIT | 支持 Lambda 表达式 |
| **分页对象** | `Page<T>`（List 子类） | `IPage<T>` |
| **扩展性** | 仅分页功能 | 包含 CRUD、条件构造器等 |
| **适用场景** | 已有 MyBatis 项目 | 新项目或需要完整 ORM |

---

## 六、常见问题与解决方案

### 6.1 分页不生效

**问题**：调用 `PageHelper.startPage()` 后，查询返回全部数据。

**解决方案**：

```java
// 错误：中间插入了其他操作
PageHelper.startPage(1, 10);
someOtherMethod(); // 这会导致分页参数失效
List<Diary> list = diaryMapper.selectDiaryList();

// 正确：startPage 后紧跟查询方法
PageHelper.startPage(1, 10);
List<Diary> list = diaryMapper.selectDiaryList();
```

### 6.2 ThreadLocal 内存泄漏

**问题**：异步线程中使用 PageHelper 可能导致内存泄漏。

**解决方案**：

```java
try {
    PageHelper.startPage(pageNum, pageSize);
    List<Diary> list = diaryMapper.selectDiaryList();
    // 处理数据
} finally {
    PageHelper.clearPage(); // 手动清除分页参数
}
```

### 6.3 分页参数越界

**问题**：传入过大的页码或每页条数。

**解决方案**：

```java
// 在 DTO 层做参数校验
public class DiaryQueryDTO {
    private Integer pageNum;
    private Integer pageSize;

    public Integer getPageNum() {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    public Integer getPageSize() {
        if (pageSize == null || pageSize < 1) return 10;
        return Math.min(pageSize, 100); // 限制最大每页100条
    }
}
```

---

## 七、总结

### 核心要点

1. **零侵入式**：无需修改 SQL，自动添加分页语句
2. **ThreadLocal 传递**：线程安全的参数传递机制
3. **拦截器实现**：基于 MyBatis 的 Interceptor 机制
4. **简单易用**：一行代码开启分页，学习成本低

### 最佳实践

1. **参数校验**：在 DTO 层统一处理分页参数的默认值和边界
2. **资源清理**：在 finally 块中调用 `PageHelper.clearPage()`
3. **性能监控**：关注 COUNT 查询的性能开销
4. **合理配置**：根据数据库类型配置正确的方言

### 适用场景

- 已有 MyBatis 项目需要快速添加分页功能
- 需要灵活控制 SQL 编写的场景
- 追求简单易用、低学习成本的项目

---

## 相关资源

- [PageHelper 官方文档](https://pagehelper.github.io/)
- [MyBatis 拦截器机制](https://mybatis.org/mybatis-3/zh/configuration.html#plugins)
- [MyBatis-Plus 分页插件](https://baomidou.com/pages/97710a/)
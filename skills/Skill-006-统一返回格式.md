# Skill: 统一返回格式

## 触发条件
当需要统一 API 接口的返回格式，确保所有接口返回数据结构一致时。

## 前置依赖
- Skill-005-全局异常处理

## 执行规范

### 文件位置
- 统一返回结果类：`moodnote-common/src/main/java/com/moodnote/common/utils/`

### 命名规范
- 统一返回结果类：`Result.java`
- 分页返回结果类：`PageResult.java`

### 代码规范
- 使用泛型支持不同类型的返回数据
- 包含状态码、消息和数据字段
- 提供静态方法简化使用

### 依赖引入
- Lombok 依赖

## 代码模板

### 模板说明
创建统一的返回结果封装类，包括普通返回结果和分页返回结果。

### 代码示例

#### 1. Result.java 统一返回结果类
```java
package com.moodnote.common.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 错误
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 错误
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
```

#### 2. PageResult.java 分页返回结果类
```java
package com.moodnote.common.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 当前页码
     */
    private int current;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 数据列表
     */
    private List<T> records;

    public PageResult(long total, int pageSize, int current, List<T> records) {
        this.total = total;
        this.pageSize = pageSize;
        this.current = current;
        this.records = records;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> build(long total, int pageSize, int current, List<T> records) {
        return new PageResult<>(total, pageSize, current, records);
    }
}
```

## 验收标准
1. 统一返回结果类创建成功
2. 分页返回结果类创建成功
3. 包含必要的字段和方法
4. 与全局异常处理集成良好
5. 提供了便捷的静态方法

## 关联 Skill
前置：Skill-005-全局异常处理

后置：Skill-007-数据库连接配置
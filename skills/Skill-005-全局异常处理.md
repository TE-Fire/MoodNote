# Skill: 全局异常处理

## 触发条件
当需要统一处理应用中的异常，确保返回给前端的错误信息格式一致时。

## 前置依赖
- Skill-004-配置基础依赖

## 执行规范

### 文件位置
- 异常处理类：`moodnote-common/src/main/java/com/moodnote/exception/`

### 命名规范
- 全局异常处理器：`GlobalExceptionHandler.java`
- 业务异常类：`BusinessException.java`
- 错误码枚举：`ErrorCode.java`

### 代码规范
- 使用 `@RestControllerAdvice` 注解标记全局异常处理器
- 自定义业务异常类继承 `RuntimeException`
- 使用枚举定义错误码，便于统一管理

### 依赖引入
- Spring Web 依赖
- Lombok 依赖

## 代码模板

### 模板说明
创建全局异常处理器和相关的异常类，统一处理应用中的各种异常。

### 代码示例

#### 1. ErrorCode.java 错误码枚举
```java
package com.moodnote.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    BUSINESS_ERROR(501, "业务逻辑错误"),
    AI_SERVICE_ERROR(502, "AI 服务错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

#### 2. BusinessException.java 业务异常类
```java
package com.moodnote.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
```

#### 3. GlobalExceptionHandler.java 全局异常处理器
```java
package com.moodnote.exception;

import com.moodnote.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        return Result.error(ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public Result<?> handleThrowable(Throwable e) {
        log.error("Throwable: {}", e.getMessage(), e);
        return Result.error(ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getMessage());
    }
}
```

## 验收标准
1. 错误码枚举创建成功
2. 业务异常类创建成功
3. 全局异常处理器创建成功
4. 异常处理逻辑正确
5. 与统一返回格式集成良好

## 关联 Skill
前置：Skill-004-配置基础依赖

后置：Skill-006-统一返回格式
package com.moodnote.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.moodnote.common.enums.OperationType;

/**
 * 自定义注解，用于方法级别进行字段的自动填充
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    // 数据库操作类型：UPDATE\INSERT
    OperationType value();
}

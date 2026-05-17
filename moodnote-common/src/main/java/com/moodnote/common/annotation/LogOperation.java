package com.moodnote.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.moodnote.common.enums.Action;
import com.moodnote.common.enums.Module;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {
    /**
     * 功能模块（使用枚举）
     */
    Module module();
    
    /**
     * 操作行为（使用枚举）
     */
    Action action();
    
    /**
     * 操作描述（可选，默认使用 action 的描述）
     */
    String description() default "";
}

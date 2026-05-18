package com.moodnote.aspect;


import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.moodnote.common.annotation.LogOperation;
import com.moodnote.common.constant.MessageConstant;
import com.moodnote.common.constant.RedisKeyConstant;
import com.moodnote.common.enums.Action;
import com.moodnote.common.enums.Module;

import com.moodnote.common.records.OperationLogRecord;
import com.moodnote.common.utils.RedisUtil;
import com.moodnote.common.utils.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 记录用户的重要操作（如创建日记、更新个人信息等），用于审计和追踪。
 */
@Aspect
@Slf4j
@Component
public class OperationLogAspect {
    
    @Autowired
    private RedisUtil redisUtil;

    @Pointcut("@annotation(com.moodnote.common.annotation.LogOperation)")
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogOperation operation = signature.getMethod().getAnnotation(LogOperation.class);
        Module module = operation.module();
        Action action = operation.action();
        String description = operation.description().isEmpty() 
            ? action.getDescription() 
            : operation.description();

        // 从请求属性中获取个人信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Result.error(MessageConstant.REQUEST_ATTRIBUTE_ERROR);
        }
        Long userId = (Long) attributes.getRequest().getAttribute("userId");

        OperationLogRecord record = new OperationLogRecord(
            userId,
            action.getCode(),
            module.getCode(),
            description,
            attributes.getRequest().getRequestURI(),
            attributes.getRequest().getMethod(),
            LocalDateTime.now()
        );

        // 写入缓存（record 没有 setter，所以用构建方式或改成普通类）
        redisUtil.set(RedisKeyConstant.getOperationLogKey(module, System.currentTimeMillis()), record, 24, TimeUnit.HOURS);

        return joinPoint.proceed();
    }
}

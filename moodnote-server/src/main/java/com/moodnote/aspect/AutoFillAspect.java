package com.moodnote.aspect;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.apache.ibatis.builder.BuilderException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.moodnote.common.annotation.AutoFill;
import com.moodnote.common.constant.AutoFillConstant;
import com.moodnote.common.constant.MessageConstant;
import com.moodnote.common.enums.OperationType;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(1)
@Aspect
public class AutoFillAspect {
    
    /**
     * 切入点表达式
     */
    @Pointcut("execution(* com.moodnote.mapper.*.*(..)) && @annotation(com.moodnote.common.annotation.AutoFill)")
    public void AutoFillPointCut() {}

    /**
     * 前置通知
     */
    @Before("AutoFillPointCut()")
    public void AutoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段填充");

        // 获取当前被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("方法签名对象：{}", signature);
        
        // 获取方法上的注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        log.info("注解对象: {}", autoFill);

        // 获取操作类型
        OperationType operationType = autoFill.value();

        // 获取拦截方法的第一个参数（默认第一个参数）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        Object enity = args[0];

        LocalDateTime now = LocalDateTime.now();

        // 根据不同的操作类型，为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT) {
            try {
                // 获取enity类的方法
                Method setCreateTime = enity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = enity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                // 通过反射为对象属性赋值
                setCreateTime.invoke(enity, now);
                setUpdateTime.invoke(enity, now);
            } catch (Exception e) {
                throw new BuilderException(MessageConstant.INSERT_AUTO_FILL_ERROR);
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                // 获取enity类的方法
                Method setUpdateTime = enity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                // 通过反射为对象属性赋值
                setUpdateTime.invoke(enity, now);
            } catch (Exception e) {
                throw new BuilderException(MessageConstant.UPDATE_AUTO_FILL_ERROR);
            }
        }

    }
}

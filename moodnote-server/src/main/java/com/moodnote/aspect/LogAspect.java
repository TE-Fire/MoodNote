package com.moodnote.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志切面
 */
@Aspect
@Component
@Order(2)
@Slf4j
public class LogAspect {
    
    @Before("execution(* com.moodnote.service..*.*(..))")
    public void logJoinPoint() {}

    @Before("logJoinPoint")
    public void logBefore(JoinPoint joinPoint) {
        // 1 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();

        // 2 获取方法参数
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();

        // 3 过滤敏感参数
        Object[] filterSensitiveParams = filterSensitiveParams(args, parameterNames);

        // 4 获取请求信息（web环境）
        String requestInfo = getRequestInfo();

        // 5 记录日志
        log.info("[方法调用] {}.{}({}) - {}",
            className.substring(className.lastIndexOf(".") + 1),
            methodName,
            formatParams(filterSensitiveParams, parameterNames),
            requestInfo
        );
    }

    /**
     * 过滤敏感参数（如密码）
     * @param args
     * @param paramNames
     * @return
     */
    private Object[] filterSensitiveParams(Object[] args, String[] paramNames) {
        Object[] filtered = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (paramNames[i].contains("password")) {
                filtered[i] = "********";
            } else {
                filtered[i] = args[i];
            }
        }
        return filtered;
    }

    /**
     * 获取请求信息（web环境）
     * @return
     */
    private String getRequestInfo() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return String.format("请求: %s %s", request.getMethod(), request.getRequestURI());
        }
        return "非web请求";
    }

    /**
     * 格式化参数
     * @param values
     * @param names
     * @return
     */
    private String formatParams(Object[] values, String[] names) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(names[i]).append("=").append(values[i]);
        }
        
        return sb.toString();
    }
}

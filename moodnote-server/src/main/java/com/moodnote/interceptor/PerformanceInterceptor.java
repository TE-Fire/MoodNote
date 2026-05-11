package com.moodnote.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * PerformanceInterceptor 性能监控和日志记录
 * 
 * Interceptor 是 Spring MVC 的组件，位于 DispatcherServlet 之后
 * 只作用于 @RequestMapping 映射的请求
 *
 * 执行流程：
 * 1. preHandle(...)     → Controller 执行前（返回 true 才继续）
 * 2. Controller 执行    → 业务逻辑处理
 * 3. postHandle(...)    → Controller 执行后，视图渲染前
 * 4. afterCompletion()  → 整个请求完成后（无论成功失败）
 */
@Slf4j
public class PerformanceInterceptor implements HandlerInterceptor {
    
    @SuppressWarnings("null") // 忽略null指针异常
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        // handler	处理请求的处理器对象（可能是控制器方法、静态资源处理器等 HandlerMethod ResourceHttpRequestHandler SimpleUrlHandlerMapping）
        // HandlerMethod	Spring MVC 封装控制器方法的类，包含方法名、参数、Bean 实例等元信息
        // 只对controller方法进行记录
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            log.info("开始请求: {} {} [{}]",
                request.getMethod(),
                request.getRequestURI(),
                handlerMethod.getMethod().getName()
            );
        }

        return true;
    }

    @SuppressWarnings("null")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        log.info("请求完成: {} {} - 耗时: {}ms, 状态码: {}", 
        request.getMethod(), 
        request.getRequestURI(),
        duration,
        response.getStatus());

      if (duration > 2000) {
            log.warn("请求超时警告: {} {} 耗时 {}ms", 
                request.getMethod(), request.getRequestURI(), duration);
        }
    }
}

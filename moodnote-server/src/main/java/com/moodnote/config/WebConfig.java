package com.moodnote.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.moodnote.interceptor.PerformanceInterceptor;

/**
 * Web 配置类
 * 用于注册自定义的 Web 组件，如 Interceptor、Advice 等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{
    
    @SuppressWarnings("null")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册性能拦截器, 作用于所有请求
        registry.addInterceptor(new PerformanceInterceptor())
                .addPathPatterns("/**");
    }
}

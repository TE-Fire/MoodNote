package com.moodnote.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha配置类
 * 通过Java配置方式注册为Bean
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        
        // 是否有边框
        properties.setProperty("kaptcha.border", "no");
        
        // 边框颜色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        
        // 图片宽度
        properties.setProperty("kaptcha.image.width", "125");
        
        // 图片高度
        properties.setProperty("kaptcha.image.height", "45");
        
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "45");
        
        // Session key
        properties.setProperty("kaptcha.session.key", "code");
        
        // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        
        // 字体
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        
        return defaultKaptcha;
    }
}

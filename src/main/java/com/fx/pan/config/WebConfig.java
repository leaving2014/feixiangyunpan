package com.fx.pan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author leaving
 * @Date 2021/11/26 10:09
 * @Version 1.0
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 允许跨域访问  仅在开发时开启!!!!!
    // @CrossOrigin 或在controller上使用注解
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "POST","PUT", "DELETE", "OPTIONS")
                .allowCredentials(true).maxAge(3600);
    }

}

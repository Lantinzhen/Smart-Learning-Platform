package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类，用于配置静态资源访问等功能
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源访问，使上传的头像文件可以通过URL访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置本地文件系统的静态资源映射
        // 将 /uploads/** URL 映射到 D:/uploads/ 目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/uploads/");
    }
}

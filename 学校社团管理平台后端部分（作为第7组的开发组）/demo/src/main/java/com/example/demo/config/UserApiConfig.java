package com.example.demo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 用户端API配置类
 */
@Configuration
@ComponentScan(basePackages = "com.example.demo.service.user.impl")
public class UserApiConfig {
}
package com.example.demo.security;

import lombok.Data;

/**
 * Token实体类
 */
@Data
public class Token {
    private String token;    // JWT令牌
    private String role;     // 用户角色
    private String userId;   // 用户ID（学号或管理员ID）
    private String name;     // 用户姓名
}
package com.example.demo.dto.auth;

import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {
    private String student_id;  // 学生学号
    private String username;    // 管理员用户名
    private String password;    // 密码
}
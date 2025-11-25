package com.example.demo.service.auth;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {
    /**
     * 学生登录
     */
    LoginResponse studentLogin(LoginRequest request);

    /**
     * 学生注册
     */
    String studentRegister(RegisterRequest request);

    /**
     * 社团管理员登录
     */
    LoginResponse clubAdminLogin(LoginRequest request);

    /**
     * 学校管理员登录
     */
    LoginResponse schoolAdminLogin(LoginRequest request);

    /**
     * 退出登录
     */
    void logout(String token);
}
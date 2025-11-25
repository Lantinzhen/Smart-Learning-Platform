package com.example.demo.controller.auth;

import com.example.demo.common.Response;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 学生登录
     */
    @PostMapping("/student/login")
    public Response<LoginResponse> studentLogin(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.studentLogin(request);
        return Response.success(loginResponse);
    }

    /**
     * 学生注册
     */
    @PostMapping("/student/register")
    public Response<Map<String, String>> studentRegister(@RequestBody RegisterRequest request) {
        String studentId = authService.studentRegister(request);
        Map<String, String> data = new HashMap<>();
        data.put("student_id", studentId);
        return Response.success("注册成功", data);
    }

    /**
     * 社团管理员登录
     */
    @PostMapping("/club-admin/login")
    public Response<LoginResponse> clubAdminLogin(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.clubAdminLogin(request);
        return Response.success(loginResponse);
    }

    /**
     * 学校管理员登录
     */
    @PostMapping("/school-admin/login")
    public Response<LoginResponse> schoolAdminLogin(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.schoolAdminLogin(request);
        return Response.success(loginResponse);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Response<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return Response.success(null);
    }
}
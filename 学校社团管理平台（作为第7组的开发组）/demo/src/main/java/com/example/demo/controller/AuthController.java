package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 学生注册
     */
    @PostMapping("/users/register")
    public ResponseEntity<?> registerStudent(@RequestBody RegisterRequest request) {
        Object responseData = authService.registerStudent(request);
        return ResponseEntity.ok().body(responseData);
    }

    /**
     * 登录接口
     */
    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 刷新令牌和登出接口将在后续实现
}

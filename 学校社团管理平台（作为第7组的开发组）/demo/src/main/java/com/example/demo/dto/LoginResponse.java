package com.example.demo.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String refreshToken;
    private UserInfo userInfo;
    private Long expiresIn;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String role;
        private String name;
        private String studentId;
        private String avatarUrl;
    }
}

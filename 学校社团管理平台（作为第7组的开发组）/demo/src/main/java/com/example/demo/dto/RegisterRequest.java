package com.example.demo.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String studentId;
    private String name;
    private String email;
    private String phone;
    private String major;
    private String grade;
    private String className;
}

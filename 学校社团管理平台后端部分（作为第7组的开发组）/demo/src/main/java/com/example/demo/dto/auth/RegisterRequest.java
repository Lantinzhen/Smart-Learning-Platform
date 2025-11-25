package com.example.demo.dto.auth;

import lombok.Data;

/**
 * 学生注册请求DTO
 */
@Data
public class RegisterRequest {
    private String student_id;     // 学号
    private String name;           // 姓名
    private String password;       // 密码
    private String email;          // 邮箱
    private String phone;          // 电话
    private String major;          // 专业
    private String grade;          // 年级
    private Integer enrollment_year; // 入学年份
    private String gender;         // 性别
}
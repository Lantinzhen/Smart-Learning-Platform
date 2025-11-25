package com.example.demo.dto.user.profile;

import lombok.Data;

/**
 * 个人信息DTO
 */
@Data
public class ProfileDTO {
    private String studentId;        // 学号
    private String name;             // 姓名
    private String avatar;           // 头像URL
    private String email;            // 邮箱
    private String phone;            // 电话
    private String major;            // 专业
    private String grade;            // 年级
    private Integer enrollmentYear;   // 入学年份
    private String gender;           // 性别
    private Integer points;          // 积分
    private String createdAt;        // 注册时间
}
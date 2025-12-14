package com.example.demo.dto.user.profile;

import lombok.Data;

/**
 * 更新个人信息DTO
 */
@Data
public class UpdateProfileDTO {
    private String name;        // 姓名
    private String email;       // 邮箱
    private String phone;       // 电话
    private String major;       // 专业
    private String grade;       // 年级
    private Integer enrollmentYear; // 入学年份
    private String gender;      // 性别
}
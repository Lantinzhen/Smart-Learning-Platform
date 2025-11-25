package com.example.demo.dto.admin.user;

import lombok.Data;

/**
 * 学生创建/更新请求DTO
 */
@Data
public class StudentRequestDTO {
    private String student_id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private String major;
    private String grade;
    private Integer enrollment_year;
    private String gender;
}

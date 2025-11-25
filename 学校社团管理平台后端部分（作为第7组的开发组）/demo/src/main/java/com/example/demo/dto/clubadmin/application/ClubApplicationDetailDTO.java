package com.example.demo.dto.clubadmin.application;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 社团申请详情DTO
 */
@Data
public class ClubApplicationDetailDTO {
    private Integer applicationId;
    private String studentId;
    private String name;
    private String major;
    private String grade;
    private String phone;
    private String email;
    private String reason;
    private String experience;
    private String activityPreference;
    private String availableTime;
    private String portfolio;
    private String status;
    private Timestamp createdAt;
}

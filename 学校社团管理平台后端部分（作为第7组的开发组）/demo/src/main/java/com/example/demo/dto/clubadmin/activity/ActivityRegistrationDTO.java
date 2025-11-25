package com.example.demo.dto.clubadmin.activity;

import lombok.Data;

/**
 * 活动报名DTO
 */
@Data
public class ActivityRegistrationDTO {
    private Integer registrationId;
    private String studentId;
    private String name;
    private String major;
    private String grade;
    private String registrationTime;
    private String status;
    private Integer attended;
    private Double pointsEarned;
}
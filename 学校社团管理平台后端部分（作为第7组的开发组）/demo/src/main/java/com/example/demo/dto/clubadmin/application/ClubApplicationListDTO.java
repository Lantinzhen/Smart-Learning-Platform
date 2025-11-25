package com.example.demo.dto.clubadmin.application;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 社团申请列表DTO
 */
@Data
public class ClubApplicationListDTO {
    private Integer applicationId;
    private String studentId;
    private String name;
    private String major;
    private String grade;
    private String reason;
    private String status;
    private Timestamp createdAt;
}

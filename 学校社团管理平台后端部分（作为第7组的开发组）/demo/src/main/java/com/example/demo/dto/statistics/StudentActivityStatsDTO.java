package com.example.demo.dto.statistics;

import lombok.Data;

/**
 * 学生活动参与统计DTO
 */
@Data
public class StudentActivityStatsDTO {
    private String studentId;
    private String name;
    private String major;
    private String grade;
    private Integer totalActivities;
    private Integer attendedActivities;
    private Double totalPoints;
}

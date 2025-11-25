package com.example.demo.dto.clubadmin.activity;

import lombok.Data;

/**
 * 活动详情DTO
 */
@Data
public class ActivityDetailDTO {
    private Integer activityId;
    private String title;
    private String description;
    private Integer categoryId;
    private String categoryName;
    private String posterUrl;
    private String location;
    private String startTime;
    private String endTime;
    private Integer maxParticipants;
    private Integer registeredCount;
    private Double points;
    private String registrationDeadline;
    private String status;
    private String createdBy;
    private String createdAt;
}
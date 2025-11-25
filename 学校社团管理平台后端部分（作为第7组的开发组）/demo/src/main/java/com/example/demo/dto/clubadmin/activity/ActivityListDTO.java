package com.example.demo.dto.clubadmin.activity;

import lombok.Data;

/**
 * 活动列表DTO
 */
@Data
public class ActivityListDTO {
    private Integer activityId;
    private String title;
    private String categoryName;
    private String startTime;
    private String location;
    private Integer registeredCount;
    private String status;
}
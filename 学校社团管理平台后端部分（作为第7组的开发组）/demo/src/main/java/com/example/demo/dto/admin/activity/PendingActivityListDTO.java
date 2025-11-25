package com.example.demo.dto.admin.activity;

import lombok.Data;

/**
 * 待审批活动列表DTO
 */
@Data
public class PendingActivityListDTO {
    private Integer activityId;
    private Integer clubId;
    private String title;
    private String clubName;
    private String categoryName;
    private String startTime;
    private String endTime;
    private String location;
    private String createdBy;
    private String createdAt;
}

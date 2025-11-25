package com.example.demo.dto.admin.activity;

import lombok.Data;

/**
 * 活动审批详情DTO
 */
@Data
public class ActivityApprovalDTO {
    private Integer activityId;
    private Integer clubId;
    private String clubName;
    private String title;
    private String description;
    private Integer categoryId;
    private String categoryName;
    private String posterUrl;
    private String location;
    private String startTime;
    private String endTime;
    private Integer maxParticipants;
    private Double points;
    private String registrationDeadline;
    private String createdBy;
    private String createdAt;
    private Integer approvalId;
    private String status;
    private String comments;
    private String approverId;
    private String approvedAt;
}

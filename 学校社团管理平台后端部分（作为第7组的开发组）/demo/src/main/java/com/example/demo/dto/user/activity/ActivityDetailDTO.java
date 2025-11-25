package com.example.demo.dto.user.activity;

import lombok.Data;

/**
 * 活动详情DTO
 */
@Data
public class ActivityDetailDTO {
    private Integer activityId;         // 活动ID
    private String title;               // 活动标题
    private String description;         // 活动描述
    private String categoryName;         // 分类名称
    private String startTime;           // 开始时间
    private String endTime;             // 结束时间
    private String location;            // 活动地点
    private String clubName;            // 主办社团
    private String clubContact;         // 社团联系方式
    private Integer registeredCount;     // 已报名人数
    private Integer maxParticipants;     // 最大参与人数
    private String registrationDeadline; // 报名截止时间
    private String status;              // 活动状态
    private String createdAt;           // 创建时间
}
package com.example.demo.dto.user.activity;

import lombok.Data;

/**
 * 我的活动DTO
 */
@Data
public class MyActivityDTO {
    private Integer activityId;     // 活动ID
    private String title;           // 活动标题
    private String startTime;       // 开始时间
    private String endTime;         // 结束时间
    private String location;        // 活动地点
    private String clubName;        // 主办社团
    private String status;          // 活动状态
    private String registrationTime; // 报名时间
    private Double points;          // 获得积分
}
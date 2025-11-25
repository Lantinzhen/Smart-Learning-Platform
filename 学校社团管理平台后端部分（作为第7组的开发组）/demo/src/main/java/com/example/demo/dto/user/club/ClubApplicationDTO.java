package com.example.demo.dto.user.club;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 社团申请DTO
 */
@Data
public class ClubApplicationDTO {
    @JsonProperty("club_id")
    private Integer clubId; // 社团ID
    private String reason; // 申请理由
    private String phone; // 联系电话
    private String email; // 邮箱
    private String name; // 申请人姓名
    private String major; // 专业
    private String grade; // 年级
    private String experience; // 相关经验
    @JsonProperty("activity_preference")
    private String activityPreference; // 活动偏好
    @JsonProperty("available_time")
    private String availableTime; // 可参与活动的时间
    private String portfolio; // 作品展示链接
}
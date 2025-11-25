package com.example.demo.dto.admin;

import lombok.Data;
import java.util.List;

/**
 * 社团管理员仪表盘数据传输对象
 */
@Data
public class ClubAdminDashboardDTO {
    private String clubName;
    private Integer memberCount;
    private Integer pendingApplicationsCount;
    private Integer activitiesCount;
    private Integer upcomingActivitiesCount;
    private List<RecentMemberDTO> recentMembers;
    private List<RecentActivityDTO> recentActivities;

    /**
     * 最近成员信息DTO
     */
    @Data
    public static class RecentMemberDTO {
        private String studentId;
        private String name;
        private String joinDate;
    }
    
    /**
     * 最近活动信息DTO
     */
    @Data
    public static class RecentActivityDTO {
        private Integer activityId;
        private String title;
        private String startTime;
        private String status;
    }
}
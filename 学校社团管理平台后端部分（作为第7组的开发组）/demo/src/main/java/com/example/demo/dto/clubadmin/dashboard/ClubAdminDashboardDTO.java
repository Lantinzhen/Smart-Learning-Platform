package com.example.demo.dto.clubadmin.dashboard;

import lombok.Data;
import java.util.List;

/**
 * 社团管理员仪表盘数据DTO
 */
@Data
public class ClubAdminDashboardDTO {
    
    /**
     * 社团名称
     */
    private String clubName;
    
    /**
     * 成员数量
     */
    private Integer memberCount;
    
    /**
     * 待处理申请数量
     */
    private Integer pendingApplicationsCount;
    
    /**
     * 活动数量
     */
    private Integer activitiesCount;
    
    /**
     * 即将到来的活动数量
     */
    private Integer upcomingActivitiesCount;
    
    /**
     * 最近加入的成员列表
     */
    private List<RecentMemberDTO> recentMembers;
    
    /**
     * 最近加入成员的内部DTO类
     */
    @Data
    public static class RecentMemberDTO {
        
        /**
         * 学生ID
         */
        private String studentId;
        
        /**
         * 学生姓名
         */
        private String name;
        
        /**
         * 加入日期
         */
        private String joinDate;
    }
}

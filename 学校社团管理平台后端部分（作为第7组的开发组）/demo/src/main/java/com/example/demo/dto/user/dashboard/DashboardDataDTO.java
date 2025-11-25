package com.example.demo.dto.user.dashboard;

import lombok.Data;
import java.util.List;

/**
 * 仪表盘数据DTO
 */
@Data
public class DashboardDataDTO {
    private PersonalInfo personalInfo;              // 个人信息
    private List<ClubActivity> recentActivities;    // 最近活动
    private List<ClubInfo> myClubs;                 // 我的社团
    private Statistics statistics;                  // 统计信息

    @Data
    public static class PersonalInfo {
        private String studentId;     // 学号
        private String name;          // 姓名
        private String major;         // 专业
        private String grade;         // 年级
        private Double points;       // 积分
    }

    @Data
    public static class ClubActivity {
        private Integer activityId;   // 活动ID
        private String title;         // 活动标题
        private String time;          // 活动时间
        private String location;      // 活动地点
        private String clubName;      // 社团名称
    }

    @Data
    public static class ClubInfo {
        private Integer clubId;       // 社团ID
        private String name;          // 社团名称
        private String category;      // 社团分类
    }

    @Data
    public static class Statistics {
        private Integer totalClubs;        // 参与社团数
        private Integer totalActivities;   // 参与活动数
        private Double totalPoints;       // 总积分
        private Integer ranking;           // 排名
    }
}
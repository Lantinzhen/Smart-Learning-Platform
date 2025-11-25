package com.example.demo.dto.user.club;

import lombok.Data;
import java.util.List;

/**
 * 社团详情DTO
 */
@Data
public class ClubDetailDTO {
    private Integer club_id;             // 社团ID
    private String name;                // 社团名称
    private String description;          // 社团描述
    private String logo_url;            // 社团Logo URL
    private String president_name;       // 社长姓名
    private String foundation_date;      // 成立日期
    private Integer member_count;        // 成员数量
    private String contact_email;        // 联系邮箱
    private String address;              // 社团地址
    private List<RecentActivityDTO> recent_activities; // 最近活动列表
    private boolean joined;           // 是否已加入
    
    @Data
    public static class RecentActivityDTO {
        private Integer activity_id;     // 活动ID
        private String title;            // 活动标题
        private String start_time;       // 开始时间
    }
}
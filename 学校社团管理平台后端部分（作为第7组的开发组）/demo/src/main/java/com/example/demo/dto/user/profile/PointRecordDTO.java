package com.example.demo.dto.user.profile;

import lombok.Data;
import java.util.List;

/**
 * 积分记录DTO
 */
@Data
public class PointRecordDTO {
    private List<PointRecordItem> records;  // 积分记录列表
    private Integer totalPoints;            // 总积分
    private Integer currentPage;            // 当前页
    private Integer totalPages;             // 总页数
    private Long totalRecords;              // 总记录数

    @Data
    public static class PointRecordItem {
        private Integer recordId;      // 记录ID
        private String activityName;    // 活动名称
        private Integer points;        // 积分数
        private String type;           // 类型（获得/扣除）
        private String createdAt;      // 时间
        private String description;     // 描述
    }
}
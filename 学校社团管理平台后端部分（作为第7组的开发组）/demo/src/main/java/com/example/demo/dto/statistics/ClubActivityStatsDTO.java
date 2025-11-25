package com.example.demo.dto.statistics;

import lombok.Data;

/**
 * 社团活跃度统计DTO
 */
@Data
public class ClubActivityStatsDTO {
    private Integer clubId;
    private String clubName;
    private Integer totalActivities;
    private Integer totalParticipants;
    private Double avgPoints;
}

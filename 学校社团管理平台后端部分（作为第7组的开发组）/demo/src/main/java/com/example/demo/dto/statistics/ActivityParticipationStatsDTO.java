package com.example.demo.dto.statistics;

import lombok.Data;

/**
 * 活动参与率统计DTO
 */
@Data
public class ActivityParticipationStatsDTO {
    private Integer activityId;
    private String title;
    private String clubName;
    private Integer maxParticipants;
    private Integer registeredCount;
    private Double participationRate;
}

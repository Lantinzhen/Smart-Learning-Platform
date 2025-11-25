package com.example.demo.dto.clubadmin.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * 确认活动参与请求DTO
 */
@Data
public class ConfirmAttendanceRequestDTO {
    @JsonProperty("registration_ids")
    private List<Integer> registrationIds;
    @JsonProperty("points_earned")
    private Double pointsEarned;
}
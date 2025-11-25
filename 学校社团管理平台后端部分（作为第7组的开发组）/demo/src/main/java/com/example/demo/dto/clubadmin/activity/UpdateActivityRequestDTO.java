package com.example.demo.dto.clubadmin.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 更新活动请求DTO
 */
@Data
public class UpdateActivityRequestDTO {
    private String title;
    private String description;
    @JsonProperty("category_id")
    private Integer categoryId;
    @JsonProperty("poster_url")
    private String posterUrl;
    private String location;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    @JsonProperty("max_participants")
    private Integer maxParticipants;
    private Double points;
    @JsonProperty("registration_deadline")
    private String registrationDeadline;
}
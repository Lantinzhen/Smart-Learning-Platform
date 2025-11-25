package com.example.demo.dto.admin.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 活动分类DTO
 */
@Data
public class ActivityCategoryDTO {
    @JsonProperty("category_id")
    private Integer categoryId;
    private String name;
    private String description;
}
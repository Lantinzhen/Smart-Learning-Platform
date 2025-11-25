package com.example.demo.dto.user.activity;

import lombok.Data;

/**
 * 活动分类DTO
 */
@Data
public class ActivityCategoryDTO {
    private Integer categoryId;   // 分类ID
    private String categoryName;   // 分类名称
}
package com.example.demo.dto.user.club;

import lombok.Data;

/**
 * 社团分类DTO
 */
@Data
public class ClubCategoryDTO {
    private Integer categoryId;   // 分类ID
    private String categoryName;   // 分类名称
}
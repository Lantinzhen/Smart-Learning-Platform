package com.example.demo.dto.admin.club;

import lombok.Data;

/**
 * 社团分类创建/更新请求DTO
 */
@Data
public class ClubCategoryRequestDTO {
    private Integer category_id;
    private String name;
    private String description;
}
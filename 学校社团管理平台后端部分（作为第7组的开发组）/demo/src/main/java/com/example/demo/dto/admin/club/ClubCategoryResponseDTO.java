package com.example.demo.dto.admin.club;

import lombok.Data;
import java.util.List;

/**
 * 社团分类响应DTO
 */
@Data
public class ClubCategoryResponseDTO {
    private List<CategoryItemDTO> list;
    
    /**
     * 分类项DTO
     */
    @Data
    public static class CategoryItemDTO {
        private Integer category_id;
        private String name;
        private String description;
    }
}
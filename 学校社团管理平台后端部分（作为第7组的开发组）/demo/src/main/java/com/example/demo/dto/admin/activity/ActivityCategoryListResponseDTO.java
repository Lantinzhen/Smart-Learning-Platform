package com.example.demo.dto.admin.activity;

import lombok.Data;
import java.util.List;

/**
 * 活动分类列表响应DTO
 */
@Data
public class ActivityCategoryListResponseDTO {
    private List<ActivityCategoryDTO> list;
}
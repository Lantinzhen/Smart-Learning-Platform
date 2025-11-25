package com.example.demo.dto.clubadmin.activity;

import lombok.Data;
import java.util.List;

/**
 * 活动列表分页响应DTO
 */
@Data
public class ActivityPageResponseDTO {
    private List<ActivityListDTO> list;
    private int total;
    private int page;
    private int pageSize;
}
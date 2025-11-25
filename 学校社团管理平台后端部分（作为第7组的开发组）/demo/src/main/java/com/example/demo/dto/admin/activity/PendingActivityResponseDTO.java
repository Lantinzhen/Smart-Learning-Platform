package com.example.demo.dto.admin.activity;

import lombok.Data;

import java.util.List;

/**
 * 待审批活动响应DTO
 */
@Data
public class PendingActivityResponseDTO {
    private List<PendingActivityListDTO> list;
    private int total;
    private int page;
    private int pageSize;
}

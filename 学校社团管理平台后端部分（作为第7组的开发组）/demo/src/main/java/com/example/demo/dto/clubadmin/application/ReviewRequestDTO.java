package com.example.demo.dto.clubadmin.application;

import lombok.Data;

/**
 * 审批请求DTO
 */
@Data
public class ReviewRequestDTO {
    private String status;  // 通过或拒绝
    private String comments;
}

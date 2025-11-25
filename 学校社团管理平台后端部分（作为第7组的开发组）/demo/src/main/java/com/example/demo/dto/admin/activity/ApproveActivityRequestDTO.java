package com.example.demo.dto.admin.activity;

import lombok.Data;

/**
 * 活动审批请求DTO
 */
@Data
public class ApproveActivityRequestDTO {
    private String status; // 已批准或已拒绝
    private String comments; // 审批意见
}

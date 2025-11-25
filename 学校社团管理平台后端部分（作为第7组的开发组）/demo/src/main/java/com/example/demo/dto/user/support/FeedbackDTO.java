package com.example.demo.dto.user.support;

import lombok.Data;

/**
 * 反馈DTO
 */
@Data
public class FeedbackDTO {
    private String title;       // 标题
    private String content;     // 内容
    private String contact;     // 联系方式
}
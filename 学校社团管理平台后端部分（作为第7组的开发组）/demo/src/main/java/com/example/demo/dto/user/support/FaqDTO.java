package com.example.demo.dto.user.support;

import lombok.Data;

/**
 * 常见问题DTO
 */
@Data
public class FaqDTO {
    private Integer faqId;     // FAQ ID
    private String question;    // 问题
    private String answer;      // 答案
    private String category;    // 分类
}
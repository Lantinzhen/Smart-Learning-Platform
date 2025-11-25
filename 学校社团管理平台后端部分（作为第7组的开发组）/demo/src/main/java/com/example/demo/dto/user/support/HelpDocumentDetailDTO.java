package com.example.demo.dto.user.support;

import lombok.Data;

/**
 * 帮助文档详情DTO
 */
@Data
public class HelpDocumentDetailDTO {
    private Integer documentId;  // 文档ID
    private String title;        // 标题
    private String content;      // 内容
    private String category;     // 分类
    private String createdAt;    // 创建时间
    private String updatedAt;    // 更新时间
}
package com.example.demo.dto.user.support;

import lombok.Data;

/**
 * 帮助文档DTO
 */
@Data
public class HelpDocumentDTO {
    private Integer documentId;  // 文档ID
    private String title;        // 标题
    private String summary;      // 摘要
    private String category;     // 分类
    private String createdAt;    // 创建时间
}
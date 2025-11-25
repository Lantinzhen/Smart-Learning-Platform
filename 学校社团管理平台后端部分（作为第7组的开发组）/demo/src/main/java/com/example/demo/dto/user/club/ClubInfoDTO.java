package com.example.demo.dto.user.club;

import lombok.Data;

/**
 * 社团信息DTO
 */
@Data
public class ClubInfoDTO {
    private Integer clubId;        // 社团ID
    private String name;           // 社团名称
    private String description;    // 社团描述
    private String categoryName;    // 分类名称
    private Integer memberCount;    // 成员数量
    private String presidentName;   // 社长姓名
}
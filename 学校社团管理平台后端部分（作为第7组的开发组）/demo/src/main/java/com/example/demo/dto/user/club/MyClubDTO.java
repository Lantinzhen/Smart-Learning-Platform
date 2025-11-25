package com.example.demo.dto.user.club;

import lombok.Data;

/**
 * 我的社团DTO
 */
@Data
public class MyClubDTO {
    private Integer clubId;         // 社团ID
    private String name;            // 社团名称
    private String categoryName;     // 分类名称
    private String role;            // 在社团中的角色
    private String joinDate;        // 加入时间
    private Double points;          // 累计积分
}
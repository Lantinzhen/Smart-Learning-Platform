package com.example.demo.dto.admin.user;

import lombok.Data;

/**
 * 社团管理员创建/更新请求DTO
 */
@Data
public class ClubAdminRequestDTO {
    private String admin_id;
    private Integer club_id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
}

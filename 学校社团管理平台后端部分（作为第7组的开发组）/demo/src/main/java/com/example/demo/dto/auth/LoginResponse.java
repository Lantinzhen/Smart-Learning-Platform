package com.example.demo.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String token;    // JWT令牌
    private UserInfo user;   // 用户信息

    /**
     * 用户信息内部类
     */
    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserInfo {
        private String student_id;   // 学生学号
        private String admin_id;     // 管理员ID
        private String name;         // 姓名
        private Integer club_id;     // 社团ID（仅社团管理员）
        private String role;         // 角色
        private String admin_role;   // 管理员角色（仅学校管理员）
    }
}
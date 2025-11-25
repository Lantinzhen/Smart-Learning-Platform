package com.example.demo.dto.user.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 修改密码DTO
 */
@Data
public class ChangePasswordDTO {
    @JsonProperty("old_password")
    private String oldPassword;  // 旧密码
    @JsonProperty("new_password")
    private String newPassword;  // 新密码
}
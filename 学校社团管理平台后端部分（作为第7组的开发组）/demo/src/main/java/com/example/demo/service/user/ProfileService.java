package com.example.demo.service.user;

import com.example.demo.dto.user.profile.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人中心服务接口
 */
public interface ProfileService {
    
    /**
     * 获取个人信息
     * @param token JWT令牌
     * @return 个人信息
     */
    ProfileDTO getProfile(String token);
    
    /**
     * 更新个人信息（包含头像上传）
     * @param token JWT令牌
     * @param profile 个人信息
     * @param file 头像文件（可选）
     */
    void updateProfile(String token, UpdateProfileDTO profile, MultipartFile file);
    
    /**
     * 修改密码
     * @param token JWT令牌
     * @param passwordDTO 密码信息
     */
    void changePassword(String token, ChangePasswordDTO passwordDTO);
}
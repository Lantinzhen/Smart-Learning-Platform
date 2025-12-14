package com.example.demo.controller.user;

import com.example.demo.common.BusinessException;
import com.example.demo.common.Response;
import com.example.demo.dto.user.profile.*;
import com.example.demo.service.user.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心控制器
 */
@RestController
@RequestMapping("/api/v1/student")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    
    @Autowired
    private ProfileService profileService;

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Response<ProfileDTO> getProfile(@RequestHeader("Authorization") String token) {
        logger.info("开始获取用户个人信息");
        try {
            // 移除Bearer前缀（如果有），否则直接使用token
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            logger.debug("准备调用服务获取个人信息");
            ProfileDTO profile = profileService.getProfile(token);
            logger.info("获取用户个人信息成功");
            
            return Response.success(profile);
        } catch (BusinessException e) {
            logger.error("获取个人信息业务异常: {}", e.getMessage());
            throw new BusinessException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("获取个人信息时发生未知错误: {}", e.getMessage(), e);
            throw new BusinessException(500, "获取个人信息失败");
        }
    }

    /**
     * 更新个人信息
     */
    @PostMapping("/profile")
    public Response<Void> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateProfileDTO profile) {
        logger.info("开始更新用户个人信息");
        try {
            // 移除Bearer前缀（如果有），否则直接使用token
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            logger.debug("准备调用服务更新个人信息");
            profileService.updateProfile(token, profile);
            logger.info("更新用户个人信息成功");
            
            return Response.success(null);
        } catch (BusinessException e) {
            logger.error("更新个人信息业务异常: {}", e.getMessage());
            throw new BusinessException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("更新个人信息时发生未知错误: {}", e.getMessage(), e);
            throw new BusinessException(500, "更新个人信息失败");
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Response<Void> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordDTO passwordDTO) {
        logger.info("开始修改用户密码");
        try {
            // 移除Bearer前缀（如果有），否则直接使用token
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            logger.debug("准备调用服务修改密码");
            profileService.changePassword(token, passwordDTO);
            logger.info("修改用户密码成功");
            
            return Response.success(null);
        } catch (BusinessException e) {
            logger.error("修改密码业务异常: {}", e.getMessage());
            throw new BusinessException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("修改密码时发生未知错误: {}", e.getMessage(), e);
            throw new BusinessException(500, "修改密码失败");
        }
    }
}
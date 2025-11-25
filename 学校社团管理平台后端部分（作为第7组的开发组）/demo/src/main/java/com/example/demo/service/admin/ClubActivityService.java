package com.example.demo.service.admin;

import com.example.demo.dto.clubadmin.activity.*;

/**
 * 社团活动管理服务接口
 */
public interface ClubActivityService {
    
    /**
     * 获取社团活动列表
     */
    ActivityPageResponseDTO getActivityList(Integer clubId, String status, String keyword, int page, int pageSize);
    
    /**
     * 创建社团活动
     */
    Integer createActivity(Integer clubId, CreateActivityRequestDTO request, String adminId);
    
    /**
     * 获取活动详情
     */
    ActivityDetailDTO getActivityDetail(Integer activityId, Integer clubId);
    
    /**
     * 更新活动信息
     */
    void updateActivity(Integer activityId, Integer clubId, UpdateActivityRequestDTO request);
    
    /**
     * 提交活动审批
     */
    void submitForApproval(Integer activityId, Integer clubId);
    
    /**
     * 获取活动报名列表
     */
    ActivityRegistrationResponseDTO getActivityRegistrations(Integer activityId, Integer clubId, String status, int page, int pageSize);
    
    /**
     * 确认活动参与
     */
    void confirmAttendance(Integer activityId, Integer clubId, ConfirmAttendanceRequestDTO request);
}
package com.example.demo.service.admin;

import com.example.demo.dto.admin.activity.ActivityApprovalDTO;
import com.example.demo.dto.admin.activity.ApproveActivityRequestDTO;
import com.example.demo.dto.admin.activity.PendingActivityResponseDTO;
import com.example.demo.dto.admin.activity.ActivityCategoryDTO;
import com.example.demo.dto.admin.activity.ActivityCategoryListResponseDTO;


/**
 * 学校管理员活动审批服务接口
 */
public interface SchoolAdminActivityService {
    
    /**
     * 获取待审批活动列表
     * @param clubId 社团ID（可选）
     * @param keyword 搜索关键词（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 待审批活动列表
     */
    PendingActivityResponseDTO getPendingActivities(Integer clubId, String keyword, int page, int pageSize);
    
    /**
     * 审批活动
     * @param activityId 活动ID
     * @param request 审批请求
     * @param adminId 审批人ID
     */
    void approveActivity(Integer activityId, ApproveActivityRequestDTO request, String adminId);
    
    /**
     * 获取活动审批详情
     * @param activityId 活动ID
     * @return 活动审批详情
     */
    ActivityApprovalDTO getActivityApprovalDetail(Integer activityId);
    
    /**
     * 获取活动分类列表
     * @return 活动分类列表
     */
    ActivityCategoryListResponseDTO getActivityCategories();
    
    /**
     * 创建或更新活动分类
     * @param categoryDTO 活动分类信息
     * @return 创建或更新后的分类ID
     */
    Integer saveActivityCategory(ActivityCategoryDTO categoryDTO);
}

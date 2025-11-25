package com.example.demo.service.admin;

import com.example.demo.dto.clubadmin.application.ApplicationPageResponseDTO;
import com.example.demo.dto.clubadmin.application.ClubApplicationDetailDTO;
import com.example.demo.dto.clubadmin.application.ReviewRequestDTO;

/**
 * 社团申请管理服务接口
 */
public interface ClubApplicationService {
    
    /**
     * 获取社团申请列表
     * @param clubId 社团ID
     * @param status 申请状态
     * @param keyword 关键词搜索
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页响应数据
     */
    ApplicationPageResponseDTO getApplicationList(Integer clubId, String status, String keyword, int page, int pageSize);
    
    /**
     * 获取申请详情
     * @param applicationId 申请ID
     * @param clubId 社团ID
     * @return 申请详情
     */
    ClubApplicationDetailDTO getApplicationDetail(Integer applicationId, Integer clubId);
    
    /**
     * 审批社团申请
     * @param applicationId 申请ID
     * @param clubId 社团ID
     * @param adminId 管理员ID
     * @param request 审批请求
     */
    void reviewApplication(Integer applicationId, Integer clubId, String adminId, ReviewRequestDTO request);
    
    /**
     * 获取待处理申请数量
     * @param clubId 社团ID
     * @return 待处理申请数量
     */
    int getPendingApplicationCount(Integer clubId);
}

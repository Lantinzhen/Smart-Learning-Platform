package com.example.demo.service.admin;

import com.example.demo.dto.admin.ClubAdminDashboardDTO;

/**
 * 社团管理员服务接口
 */
public interface ClubAdminService {
     
    /**
     * 获取单个社团管理概览信息
     * @param clubId 社团ID
     * @return 社团的管理概览数据
     */
    ClubAdminDashboardDTO getClubDashboard(Integer clubId);
    
    /**
     * 根据管理员ID获取其管理的社团ID
     * @param adminId 管理员ID
     * @return 社团ID
     */
    Integer getClubIdByAdminId(String adminId);
}
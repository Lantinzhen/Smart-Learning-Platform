package com.example.demo.service.user;

import com.example.demo.dto.user.dashboard.DashboardDataDTO;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {
    
    /**
     * 获取仪表盘数据
     * @param userId 用户ID
     * @return 仪表盘数据
     */
    DashboardDataDTO getDashboardData(String userId);
}
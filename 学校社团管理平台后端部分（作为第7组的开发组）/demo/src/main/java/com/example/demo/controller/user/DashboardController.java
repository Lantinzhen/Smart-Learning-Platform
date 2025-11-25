package com.example.demo.controller.user;

import com.example.demo.common.Response;
import com.example.demo.dto.user.dashboard.DashboardDataDTO;
import com.example.demo.service.user.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 学生仪表盘控制器
 */
@RestController
@RequestMapping("/api/v1/student/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘数据
     */
    @GetMapping
    public Response<DashboardDataDTO> getDashboardData(HttpServletRequest request) {
        // 从请求属性中获取用户ID，由Security过滤器设置
        String userId = (String) request.getAttribute("userId");
        
        DashboardDataDTO dashboardData = dashboardService.getDashboardData(userId);
        return Response.success(dashboardData);
    }
}
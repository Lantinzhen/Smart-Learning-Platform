package com.example.demo.controller.clubadmin;

import com.example.demo.common.BusinessException;
import com.example.demo.common.Response;
import com.example.demo.dto.admin.ClubAdminDashboardDTO;
import com.example.demo.entity.ClubAdmin;
import com.example.demo.dto.clubadmin.application.ApplicationPageResponseDTO;
import com.example.demo.dto.clubadmin.application.ClubApplicationDetailDTO;
import com.example.demo.dto.clubadmin.application.ReviewRequestDTO;
import com.example.demo.repository.ClubAdminRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.admin.ClubAdminService;
import com.example.demo.service.admin.ClubApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 社团管理员控制器
 */
@RestController
@RequestMapping("/api/v1/club-admin")
public class ClubAdminController {

    @Autowired
    private ClubAdminService clubAdminService;
    
    @Autowired
    private ClubApplicationService clubApplicationService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ClubAdminRepository clubAdminRepository;

    /**
     * 获取社团管理概览
     */
    @GetMapping("/dashboard")
    public Response<ClubAdminDashboardDTO> getDashboard(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 解析Authorization头，获取token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
        } else if (authHeader != null) {
            // 处理可能没有Bearer前缀的情况
            token = authHeader;
        }
        
        // 检查token是否为空
        if (token == null) {
            throw new BusinessException(401, "缺少有效token");
        }
        
        // 从token中获取管理员ID
        String adminId = jwtUtil.getUserIdFromToken(token);
        if (adminId == null || adminId.isEmpty()) {
            throw new BusinessException(401, "无效的token，无法获取管理员ID");
        }
        
        // 验证管理员存在
        ClubAdmin clubAdmin = clubAdminRepository.findById(adminId).orElse(null);
        if (clubAdmin == null) {
            throw new BusinessException(404, "社团管理员不存在");
        }
        
        // 获取管理员管理的社团ID
        Integer clubId = clubAdminService.getClubIdByAdminId(adminId);
        if (clubId == null) {
            throw new BusinessException(404, "未找到管理员对应的社团");
        }
        
        // 调用服务层方法获取单个社团的仪表盘数据
        ClubAdminDashboardDTO dashboard = clubAdminService.getClubDashboard(clubId);
        
        return Response.success(dashboard);
    }
    
    /**
     * 获取社团申请列表
     */
    @GetMapping("/applications")
    public Response<ApplicationPageResponseDTO> getApplications(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer page_size) {
        // 解析token，获取管理员信息和社团ID
        Integer clubId = getClubIdFromToken(authHeader);
        
        // 调用服务层获取申请列表
        ApplicationPageResponseDTO response = clubApplicationService.getApplicationList(clubId, status, keyword, page, page_size);
        
        return Response.success(response);
    }
    
    /**
     * 获取申请详情
     */
    @GetMapping("/applications/{application_id}")
    public Response<ClubApplicationDetailDTO> getApplicationDetail(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("application_id") Integer applicationId) {
        // 解析token，获取管理员信息和社团ID
        Integer clubId = getClubIdFromToken(authHeader);
        
        // 调用服务层获取申请详情
        ClubApplicationDetailDTO detail = clubApplicationService.getApplicationDetail(applicationId, clubId);
        
        return Response.success(detail);
    }
    
    /**
     * 审批社团申请
     */
    @PutMapping("/applications/{application_id}/review")
    public Response<Void> reviewApplication(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("application_id") Integer applicationId,
            @RequestBody ReviewRequestDTO request) {
        // 解析token，获取管理员信息和社团ID
        String adminId = getAdminIdFromToken(authHeader);
        Integer clubId = getClubIdFromToken(authHeader);
        
        // 调用服务层进行审批
        clubApplicationService.reviewApplication(applicationId, clubId, adminId, request);
        
        Response<Void> resp = Response.success(null);
        resp.setMessage("审批成功");
        return resp;
    }
    
    /**
     * 从token中获取管理员ID
     */
    private String getAdminIdFromToken(String authHeader) {
        // 解析Authorization头，获取token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
        } else if (authHeader != null) {
            token = authHeader;
        }
        
        if (token == null) {
            throw new BusinessException(401, "缺少有效token");
        }
        
        // 从token中获取管理员ID
        String adminId = jwtUtil.getUserIdFromToken(token);
        if (adminId == null || adminId.isEmpty()) {
            throw new BusinessException(401, "无效的token，无法获取管理员ID");
        }
        
        return adminId;
    }
    
    /**
     * 从token中获取社团ID
     */
    private Integer getClubIdFromToken(String authHeader) {
        // 解析Authorization头，获取token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
        } else if (authHeader != null) {
            token = authHeader;
        }
        
        if (token == null) {
            throw new BusinessException(401, "缺少有效token");
        }
        
        // 从token中获取管理员ID
        String adminId = jwtUtil.getUserIdFromToken(token);
        if (adminId == null || adminId.isEmpty()) {
            throw new BusinessException(401, "无效的token，无法获取管理员ID");
        }
        
        // 验证管理员存在
        ClubAdmin clubAdmin = clubAdminRepository.findById(adminId).orElse(null);
        if (clubAdmin == null) {
            throw new BusinessException(404, "社团管理员不存在");
        }
        
        // 获取管理员管理的社团ID
        Integer clubId = clubAdminService.getClubIdByAdminId(adminId);
        if (clubId == null) {
            throw new BusinessException(404, "未找到管理员对应的社团");
        }
        
        return clubId;
    }

}

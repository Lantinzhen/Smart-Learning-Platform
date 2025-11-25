package com.example.demo.controller.admin;

import com.example.demo.common.BusinessException;
import com.example.demo.common.Response;
import com.example.demo.dto.clubadmin.activity.*;
import com.example.demo.entity.ClubAdmin;
import com.example.demo.repository.ClubAdminRepository;
import com.example.demo.service.admin.ClubActivityService;
import com.example.demo.service.admin.ClubAdminService;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 社团活动管理控制器
 */
@RestController
@RequestMapping("/api/v1/club-admin/activities")
public class ClubActivityController {
    
    @Autowired
    private ClubActivityService clubActivityService;
    
    @Autowired
    private ClubAdminRepository clubAdminRepository;
    
    @Autowired
    private ClubAdminService clubAdminService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 从token中获取社团管理员信息
     * 
     * @param authHeader Authorization头
     * @return 社团管理员实体
     */
    private ClubAdmin getClubAdminFromToken(String authHeader) {
        // 解析Authorization头，获取token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
        } else if (authHeader != null) {
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
        ClubAdmin clubAdmin = clubAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(404, "社团管理员不存在"));

        return clubAdmin;
    }
    
    /**
     * 从token中获取社团ID
     * 
     * @param authHeader Authorization头
     * @return 社团ID
     */
    private Integer getClubIdFromToken(String authHeader) {
        ClubAdmin clubAdmin = getClubAdminFromToken(authHeader);
        
        // 获取管理员管理的社团ID
        Integer clubId = clubAdminService.getClubIdByAdminId(clubAdmin.getAdminId());
        if (clubId == null) {
            throw new BusinessException(404, "未找到管理员对应的社团");
        }
        
        return clubId;
    }
    /**
     * 获取活动列表
     */
    @GetMapping
    public Response<ActivityPageResponseDTO> getActivityList(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        
        // 从token中获取社团ID，确保管理员只能看到自己社团的活动
        Integer clubId = getClubIdFromToken(authHeader);
        
        // 设置默认分页参数
        int pageNum = page != null ? page : 1;
        int size = pageSize != null ? pageSize : 10;
        
        ActivityPageResponseDTO response = clubActivityService.getActivityList(clubId, status, keyword, pageNum, size);
        return Response.success(response);
    }
    
    /**
     * 创建活动
     */
    @PostMapping
    public Response<Map<String, Integer>> createActivity(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateActivityRequestDTO request) {
        
        // 从token中获取社团ID和管理员信息
        Integer clubId = getClubIdFromToken(authHeader);
        ClubAdmin clubAdmin = getClubAdminFromToken(authHeader);
        String adminId = clubAdmin.getAdminId();
        
        Integer activityId = clubActivityService.createActivity(clubId, request, adminId);
        
        // 构建符合API文档要求的响应数据
        Map<String, Integer> responseData = new HashMap<>();
        responseData.put("activity_id", activityId);
        
        return Response.success("创建成功", responseData);
    }
    
    /**
     * 获取活动详情
     */
    @GetMapping("/{activityId}")
    public Response<ActivityDetailDTO> getActivityDetail(
            @PathVariable Integer activityId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 从token中获取社团ID
        Integer clubId = getClubIdFromToken(authHeader);
        
        ActivityDetailDTO detail = clubActivityService.getActivityDetail(activityId, clubId);
        return Response.success(detail);
    }
    
    /**
     * 更新活动
     */
    @PutMapping("/{activityId}")
    public Response<Void> updateActivity(
            @PathVariable Integer activityId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UpdateActivityRequestDTO request) {
        
        // 从token中获取社团ID
        Integer clubId = getClubIdFromToken(authHeader);
        
        clubActivityService.updateActivity(activityId, clubId, request);
        return Response.success(null);
    }
    
    /**
     * 提交审批
     */
    @PutMapping("/{activityId}/submit-for-approval")
    public Response<Void> submitForApproval(
            @PathVariable Integer activityId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 从token中获取社团ID
        Integer clubId = getClubIdFromToken(authHeader);
        
        clubActivityService.submitForApproval(activityId, clubId);
        return Response.success(null);
    }
    
    /**
     * 获取活动报名列表
     */
    @GetMapping("/{activityId}/registrations")
    public Response<ActivityRegistrationResponseDTO> getActivityRegistrations(
            @PathVariable Integer activityId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        
        // 从token中获取社团ID
        Integer clubId = getClubIdFromToken(authHeader);
        
        // 设置默认分页参数
        int pageNum = page != null ? page : 1;
        int size = pageSize != null ? pageSize : 10;
        
        ActivityRegistrationResponseDTO response = clubActivityService.getActivityRegistrations(
                activityId, clubId, status, pageNum, size);
        return Response.success(response);
    }
    
    /**
     * 确认活动参与
     */
    @PostMapping("/{activityId}/attendance")
    public Response<Void> confirmAttendance(
            @PathVariable Integer activityId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ConfirmAttendanceRequestDTO request) {
        
        // 从token中获取社团ID
        Integer clubId = getClubIdFromToken(authHeader);
        
        clubActivityService.confirmAttendance(activityId, clubId, request);
        return Response.success("确认成功", null);
    }
}
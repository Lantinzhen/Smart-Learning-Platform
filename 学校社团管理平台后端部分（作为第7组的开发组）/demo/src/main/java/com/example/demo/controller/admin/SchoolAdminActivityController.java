package com.example.demo.controller.admin;

import com.example.demo.common.Response;
import com.example.demo.dto.admin.activity.ActivityApprovalDTO;
import com.example.demo.dto.admin.activity.ApproveActivityRequestDTO;
import com.example.demo.dto.admin.activity.PendingActivityResponseDTO;
import com.example.demo.dto.admin.activity.ActivityCategoryDTO;
import com.example.demo.dto.admin.activity.ActivityCategoryListResponseDTO;
import java.util.Map;
import java.util.HashMap;
import com.example.demo.entity.SchoolAdmin;
import com.example.demo.repository.SchoolAdminRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.admin.SchoolAdminActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学校管理员活动审批控制器
 */
@RestController
@RequestMapping("/api/v1/school-admin")
public class SchoolAdminActivityController {
    
    @Autowired
    private SchoolAdminActivityService schoolAdminActivityService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SchoolAdminRepository schoolAdminRepository;
    
    /**
     * 获取待审批活动列表
     * @param authHeader Authorization头
     * @param clubId 社团ID（可选）
     * @param keyword 搜索关键词（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 待审批活动列表
     */
    @GetMapping("/activities/pending-approval")
    public Response<PendingActivityResponseDTO> getPendingActivities(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer club_id,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        
        // 验证管理员权限（后续会完善为完整的token验证）
        validateSchoolAdminToken(authHeader);
        
        PendingActivityResponseDTO response = schoolAdminActivityService.getPendingActivities(club_id, keyword, page, pageSize);
        return Response.success(response);
    }
    
    /**
     * 获取活动审批详情
     * @param activityId 活动ID
     * @param authHeader Authorization头
     * @return 活动审批详情
     */
    @GetMapping("/activities/{activityId}/approval")
    public Response<ActivityApprovalDTO> getActivityApprovalDetail(
            @PathVariable Integer activityId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 验证管理员权限
        validateSchoolAdminToken(authHeader);
        
        ActivityApprovalDTO detail = schoolAdminActivityService.getActivityApprovalDetail(activityId);
        return Response.success(detail);
    }
    
    /**
     * 审批活动
     * @param activityId 活动ID
     * @param request 审批请求
     * @param authHeader Authorization头
     * @return 审批结果
     */
    @PutMapping("/activities/{activityId}/approval")
    public Response<Void> approveActivity(
            @PathVariable Integer activityId,
            @RequestBody ApproveActivityRequestDTO request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 验证管理员权限
        String adminId = validateSchoolAdminToken(authHeader);
        
        schoolAdminActivityService.approveActivity(activityId, request, adminId);
        return Response.success(null);
    }

    /**
     * 获取活动分类列表
     * @param authHeader Authorization头
     * @return 活动分类列表
     */
    @GetMapping("/activity-categories")
    public Response<ActivityCategoryListResponseDTO> getActivityCategories(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 验证管理员权限
        validateSchoolAdminToken(authHeader);
        
        ActivityCategoryListResponseDTO response = schoolAdminActivityService.getActivityCategories();
        return Response.success(response);
    }
    
    /**
     * 创建或更新活动分类
     * @param categoryDTO 活动分类信息
     * @param authHeader Authorization头
     * @return 创建或更新后的分类ID
     */
    @PostMapping("/activity-categories")
    public Response<Map<String, Integer>> saveActivityCategory(
            @RequestBody ActivityCategoryDTO categoryDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 验证管理员权限
        validateSchoolAdminToken(authHeader);
        
        Integer categoryId = schoolAdminActivityService.saveActivityCategory(categoryDTO);
        Map<String, Integer> responseData = new HashMap<>();
        responseData.put("category_id", categoryId);
        return Response.success(responseData);
    }

    /**
     * 验证学校管理员token并返回管理员ID
     */
    private String validateSchoolAdminToken(String authHeader) {
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
            throw new RuntimeException("缺少有效的Authorization令牌");
        }
        
        // 验证token有效性
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("令牌已过期或无效");
        }
        
        // 从token中获取管理员ID
        String adminId = jwtUtil.getUserIdFromToken(token);
        if (adminId == null || adminId.isEmpty()) {
            throw new RuntimeException("无效的管理员令牌，无法获取管理员ID");
        }
        
        // 验证管理员存在性
        SchoolAdmin admin = schoolAdminRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("学校管理员不存在"));
        
        // 验证管理员状态
        if (admin.getStatus() != 1) {
            throw new RuntimeException("学校管理员账号已被禁用");
        }
        
        return adminId;
    }
}

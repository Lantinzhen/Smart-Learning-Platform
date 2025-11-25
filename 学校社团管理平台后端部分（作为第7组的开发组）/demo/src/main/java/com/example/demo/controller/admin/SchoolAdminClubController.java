package com.example.demo.controller.admin;

import com.example.demo.common.Response;
import com.example.demo.dto.admin.club.*;
import com.example.demo.entity.SchoolAdmin;
import com.example.demo.common.BusinessException;
import com.example.demo.repository.SchoolAdminRepository;
import com.example.demo.service.admin.ClubManagementService;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学校管理员社团管理控制器
 */
@RestController
@RequestMapping("/api/v1/school-admin")
public class SchoolAdminClubController {
    
    @Autowired
    private ClubManagementService clubManagementService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SchoolAdminRepository schoolAdminRepository;
    
    /**
     * 获取社团列表
     */
    @GetMapping("/clubs")
    public Response<ClubListResponseDTO> getClubs(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam(value = "category_id", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        
        // 验证学校管理员身份
        validateSchoolAdmin(authHeader);
        
        ClubListResponseDTO response = clubManagementService.getClubs(categoryId, keyword, status, page, pageSize);
        return Response.success(response);
    }
    
    /**
     * 创建/更新社团
     */
    @PostMapping("/clubs")
    public Response<Map<String, Integer>> createOrUpdateClub(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody ClubRequestDTO requestDTO) {
        
        // 验证学校管理员身份
        validateSchoolAdmin(authHeader);
        
        Map<String, Integer> result = clubManagementService.createOrUpdateClub(requestDTO);
        return Response.success(result);
    }
    
    /**
     * 解散/恢复社团
     */
    @PutMapping("/clubs/{club_id}/status")
    public Response<Void> updateClubStatus(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable("club_id") Integer clubId,
            @RequestBody ClubStatusRequestDTO requestDTO) {
        
        // 验证学校管理员身份
        validateSchoolAdmin(authHeader);
        
        clubManagementService.updateClubStatus(clubId, requestDTO);
        return Response.success(null);
    }
    
    /**
     * 获取社团分类列表
     */
    @GetMapping("/club-categories")
    public Response<ClubCategoryResponseDTO> getClubCategories(
            @RequestHeader(value = "Authorization") String authHeader) {
        
        // 验证学校管理员身份
        validateSchoolAdmin(authHeader);
        
        ClubCategoryResponseDTO response = clubManagementService.getClubCategories();
        return Response.success(response);
    }
    
    /**
     * 创建/更新社团分类
     */
    @PostMapping("/club-categories")
    public Response<Map<String, Integer>> createOrUpdateClubCategory(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody ClubCategoryRequestDTO requestDTO) {
        
        // 验证学校管理员身份
        validateSchoolAdmin(authHeader);
        
        Map<String, Integer> result = clubManagementService.createOrUpdateClubCategory(requestDTO);
        return Response.success(result);
    }
    
    /**
     * 验证学校管理员身份
     */
    private void validateSchoolAdmin(String authHeader) {
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
        SchoolAdmin schoolAdmin = schoolAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(404, "学校管理员不存在"));
        
        // 验证管理员状态
        if (schoolAdmin.getStatus() != 1) {
            throw new BusinessException(403, "管理员账号已被禁用");
        }
    }
}
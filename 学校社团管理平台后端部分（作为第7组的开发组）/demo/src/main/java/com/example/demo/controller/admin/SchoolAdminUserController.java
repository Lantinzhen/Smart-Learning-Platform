package com.example.demo.controller.admin;

import com.example.demo.common.Response;
import com.example.demo.dto.admin.user.StudentListResponseDTO;
import com.example.demo.dto.admin.user.StudentRequestDTO;
import com.example.demo.dto.admin.user.StudentStatusRequestDTO;
import com.example.demo.dto.admin.user.ClubAdminListResponseDTO;
import com.example.demo.dto.admin.user.ClubAdminRequestDTO;
import com.example.demo.service.admin.SchoolAdminUserService;
import com.example.demo.security.JwtUtil;
import com.example.demo.repository.SchoolAdminRepository;
import com.example.demo.entity.SchoolAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学校管理员用户管理控制器
 */
@RestController
@RequestMapping("/api/v1/school-admin")
public class SchoolAdminUserController {
    
    @Autowired
    private SchoolAdminUserService schoolAdminUserService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SchoolAdminRepository schoolAdminRepository;
    
    /**
     * 获取学生列表
     * @param authHeader Authorization头
     * @param keyword 搜索关键词
     * @param major 专业
     * @param status 状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 学生列表响应
     */
    @GetMapping("/students")
    public Response<StudentListResponseDTO> getStudents(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        
        // 验证管理员权限
        validateSchoolAdminToken(authHeader);
        
        StudentListResponseDTO response = schoolAdminUserService.getStudents(keyword, major, status, page, pageSize);
        return Response.success(response);
    }
    
    /**
     * 创建/更新学生信息
     * @param requestDTO 学生信息
     * @param authHeader Authorization头
     * @return 操作结果
     */
    @PostMapping("/students")
    public Response<Map<String, String>> createOrUpdateStudent(
            @RequestBody StudentRequestDTO requestDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 验证管理员权限
        validateSchoolAdminToken(authHeader);
        
        Map<String, String> result = schoolAdminUserService.createOrUpdateStudent(requestDTO);
        return Response.success("操作成功", result);
    }
    
    /**
     * 禁用/启用学生账号
     * @param studentId 学号
     * @param requestDTO 状态更新请求
     * @param authHeader Authorization头
     * @return 操作结果
     */
    @PutMapping("/students/{studentId}/status")
    public Response<Void> updateStudentStatus(
            @PathVariable String studentId,
            @RequestBody StudentStatusRequestDTO requestDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 验证管理员权限
        validateSchoolAdminToken(authHeader);
        
        schoolAdminUserService.updateStudentStatus(studentId, requestDTO.getStatus());
        return Response.success("操作成功", null);
    }
    
    /**
     * 获取社团管理员列表
     * @param authHeader Authorization头
     * @param keyword 搜索关键词
     * @param clubId 社团ID
     * @param status 状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 社团管理员列表响应
     */
    @GetMapping("/club-admins")
    public Response<ClubAdminListResponseDTO> getClubAdmins(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer club_id,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer page_size) {
        
        // 验证管理员权限
        validateSchoolAdminToken(authHeader);
        
        ClubAdminListResponseDTO response = schoolAdminUserService.getClubAdmins(keyword, club_id, status, page, page_size);
        return Response.success(response);
    }
    
    /**
     * 创建/更新社团管理员
     * @param requestDTO 社团管理员信息
     * @param authHeader Authorization头
     * @return 操作结果
     */
    @PostMapping("/club-admins")
    public Response<Map<String, String>> createOrUpdateClubAdmin(
            @RequestBody ClubAdminRequestDTO requestDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 验证管理员权限
        validateSchoolAdminToken(authHeader);
        
        Map<String, String> result = schoolAdminUserService.createOrUpdateClubAdmin(requestDTO);
        return Response.success("操作成功", result);
    }
    
    /**
     * 禁用/启用社团管理员账号
     * @param adminId 管理员ID
     * @param requestDTO 状态更新请求
     * @param authHeader Authorization头
     * @return 操作结果
     */
    @PutMapping("/club-admins/{adminId}/status")
    public Response<Void> updateClubAdminStatus(
            @PathVariable String adminId,
            @RequestBody StudentStatusRequestDTO requestDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 验证管理员权限
        validateSchoolAdminToken(authHeader);
        
        schoolAdminUserService.updateClubAdminStatus(adminId, requestDTO.getStatus());
        return Response.success("操作成功", null);
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

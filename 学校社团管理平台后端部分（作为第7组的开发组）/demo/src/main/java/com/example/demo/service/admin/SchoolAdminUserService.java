package com.example.demo.service.admin;

import com.example.demo.dto.admin.user.StudentListResponseDTO;
import com.example.demo.dto.admin.user.StudentRequestDTO;
import com.example.demo.dto.admin.user.ClubAdminListResponseDTO;
import com.example.demo.dto.admin.user.ClubAdminRequestDTO;
import java.util.Map;

/**
 * 学校管理员用户管理服务接口
 */
public interface SchoolAdminUserService {
    
    /**
     * 获取学生列表
     * @param keyword 搜索关键词
     * @param major 专业
     * @param status 状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 学生列表响应
     */
    StudentListResponseDTO getStudents(String keyword, String major, Integer status, int page, int pageSize);
    
    /**
     * 创建或更新学生信息
     * @param requestDTO 学生信息
     * @return 包含student_id的Map
     */
    Map<String, String> createOrUpdateStudent(StudentRequestDTO requestDTO);
    
    /**
     * 更新学生状态
     * @param studentId 学号
     * @param status 状态
     */
    void updateStudentStatus(String studentId, Integer status);
    
    /**
     * 获取社团管理员列表
     * @param keyword 搜索关键词
     * @param clubId 社团ID
     * @param status 状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 社团管理员列表响应
     */
    ClubAdminListResponseDTO getClubAdmins(String keyword, Integer clubId, Integer status, int page, int pageSize);
    
    /**
     * 创建或更新社团管理员
     * @param requestDTO 社团管理员信息
     * @return 包含admin_id的Map
     */
    Map<String, String> createOrUpdateClubAdmin(ClubAdminRequestDTO requestDTO);
    
    /**
     * 更新社团管理员状态
     * @param adminId 管理员ID
     * @param status 状态
     */
    void updateClubAdminStatus(String adminId, Integer status);
}

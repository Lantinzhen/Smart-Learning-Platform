package com.example.demo.service.admin;

import com.example.demo.dto.admin.club.ClubCategoryRequestDTO;
import com.example.demo.dto.admin.club.ClubCategoryResponseDTO;
import com.example.demo.dto.admin.club.ClubListResponseDTO;
import com.example.demo.dto.admin.club.ClubRequestDTO;
import com.example.demo.dto.admin.club.ClubStatusRequestDTO;
import java.util.Map;

/**
 * 社团管理服务接口
 */
public interface ClubManagementService {
    
    /**
     * 获取社团列表
     * @param categoryId 分类ID
     * @param keyword 搜索关键词
     * @param status 状态
     * @param page 页码
     * @param pageSize 每页数量
     * @return 社团列表响应
     */
    ClubListResponseDTO getClubs(Integer categoryId, String keyword, Integer status, int page, int pageSize);
    
    /**
     * 创建/更新社团
     * @param requestDTO 社团请求数据
     * @return 包含社团ID的map
     */
    Map<String, Integer> createOrUpdateClub(ClubRequestDTO requestDTO);
    
    /**
     * 更新社团状态
     * @param clubId 社团ID
     * @param requestDTO 状态请求数据
     */
    void updateClubStatus(Integer clubId, ClubStatusRequestDTO requestDTO);
    
    /**
     * 获取社团分类列表
     * @return 分类列表响应
     */
    ClubCategoryResponseDTO getClubCategories();
    
    /**
     * 创建/更新社团分类
     * @param requestDTO 分类请求数据
     * @return 包含分类ID的map
     */
    Map<String, Integer> createOrUpdateClubCategory(ClubCategoryRequestDTO requestDTO);
}
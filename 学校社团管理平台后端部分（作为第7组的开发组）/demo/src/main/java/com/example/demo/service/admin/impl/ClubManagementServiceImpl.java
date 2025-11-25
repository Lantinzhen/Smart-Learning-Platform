package com.example.demo.service.admin.impl;

import com.example.demo.dto.admin.club.*;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubCategory;
import com.example.demo.entity.Student;
import com.example.demo.common.BusinessException;
import com.example.demo.repository.ClubCategoryRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.admin.ClubManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 社团管理服务实现类
 */
@Service
public class ClubManagementServiceImpl implements ClubManagementService {
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private ClubCategoryRepository clubCategoryRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Override
    public ClubListResponseDTO getClubs(Integer categoryId, String keyword, Integer status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        List<Club> clubs;
        long total;
        
        // 根据筛选条件查询
        if (categoryId != null) {
            clubs = clubRepository.findByCategoryIdOrderByFoundationDateDesc(categoryId, pageable);
            total = clubRepository.countByCategoryId(categoryId);
        } else {
            clubs = clubRepository.findAllOrderByFoundationDateDesc(pageable);
            total = clubRepository.count();
        }
        
        // 进一步过滤（关键词和状态）
        List<Club> filteredClubs = clubs.stream()
            .filter(club -> (keyword == null || club.getName().contains(keyword) || club.getDescription().contains(keyword)))
            .filter(club -> (status == null || club.getStatus().equals(status)))
            .collect(Collectors.toList());
        
        // 转换为DTO
        ClubListResponseDTO response = new ClubListResponseDTO();
        response.setList(filteredClubs.stream().map(this::convertToClubItemDTO).collect(Collectors.toList()));
        response.setTotal(total);
        response.setPage(page);
        response.setPage_size(pageSize);
        
        return response;
    }
    
    @Override
    @Transactional
    public Map<String, Integer> createOrUpdateClub(ClubRequestDTO requestDTO) {
        // 验证分类是否存在
        if (requestDTO.getCategory_id() != null) {
            clubCategoryRepository.findById(requestDTO.getCategory_id())
                .orElseThrow(() -> new BusinessException(400, "社团分类不存在"));
        }
        
        // 验证社长是否存在
        if (requestDTO.getPresident_student_id() != null) {
            Student student = studentRepository.findByStudentId(requestDTO.getPresident_student_id());
            if (student == null) {
                throw new BusinessException(400, "社长不存在");
            }
        }
        
        Club club;
        
        if (requestDTO.getClub_id() != null) {
            // 更新社团
            club = clubRepository.findById(requestDTO.getClub_id())
                .orElseThrow(() -> new BusinessException(404, "社团不存在"));
            
            // 检查名称是否被其他社团使用
            Optional<Club> existingClub = clubRepository.findByNameContaining(requestDTO.getName())
                .stream()
                .filter(c -> !c.getClubId().equals(requestDTO.getClub_id()))
                .findFirst();
            if (existingClub.isPresent()) {
                throw new BusinessException(400, "社团名称已存在");
            }
        } else {
            // 创建新社团
            club = new Club();
            club.setStatus(1); // 默认启用状态
            club.setMemberCount(0); // 初始成员数为0
            
            // 检查名称是否已存在
            List<Club> existingClubs = clubRepository.findByNameContaining(requestDTO.getName());
            if (!existingClubs.isEmpty()) {
                throw new BusinessException(400, "社团名称已存在");
            }
        }
        
        // 设置字段
        club.setCategoryId(requestDTO.getCategory_id());
        club.setName(requestDTO.getName());
        club.setDescription(requestDTO.getDescription());
        club.setLogoUrl(requestDTO.getLogo_url());
        club.setPresidentStudentId(requestDTO.getPresident_student_id());
        club.setFoundationDate(requestDTO.getFoundation_date());
        club.setMaxMembers(requestDTO.getMax_members());
        club.setContactInfo(requestDTO.getContact_info());
        club.setMeetingTime(requestDTO.getMeeting_time());
        club.setMeetingLocation(requestDTO.getMeeting_location());
        
        club = clubRepository.save(club);
        
        Map<String, Integer> result = new HashMap<>();
        result.put("club_id", club.getClubId());
        return result;
    }
    
    @Override
    @Transactional
    public void updateClubStatus(Integer clubId, ClubStatusRequestDTO requestDTO) {
        Club club = clubRepository.findById(clubId)
            .orElseThrow(() -> new BusinessException(404, "社团不存在"));
        
        club.setStatus(requestDTO.getStatus());
        clubRepository.save(club);
    }
    
    @Override
    public ClubCategoryResponseDTO getClubCategories() {
        List<ClubCategory> categories = clubCategoryRepository.findAll();
        
        ClubCategoryResponseDTO response = new ClubCategoryResponseDTO();
        response.setList(categories.stream().map(this::convertToCategoryItemDTO).collect(Collectors.toList()));
        
        return response;
    }
    
    @Override
    @Transactional
    public Map<String, Integer> createOrUpdateClubCategory(ClubCategoryRequestDTO requestDTO) {
        ClubCategory category;
        
        if (requestDTO.getCategory_id() != null) {
            // 更新分类
            category = clubCategoryRepository.findById(requestDTO.getCategory_id())
                .orElseThrow(() -> new BusinessException(404, "分类不存在"));
            
            // 检查名称是否被其他分类使用
            Optional<ClubCategory> existingCategory = clubCategoryRepository.findAll().stream()
                .filter(c -> c.getName().equals(requestDTO.getName()) && !c.getCategoryId().equals(requestDTO.getCategory_id()))
                .findFirst();
            if (existingCategory.isPresent()) {
                throw new BusinessException(400, "分类名称已存在");
            }
        } else {
            // 创建新分类
            category = new ClubCategory();
            category.setCreatedAt(LocalDateTime.now());
            
            // 检查名称是否已存在
            Optional<ClubCategory> existingCategory = clubCategoryRepository.findAll().stream()
                .filter(c -> c.getName().equals(requestDTO.getName()))
                .findFirst();
            if (existingCategory.isPresent()) {
                throw new BusinessException(400, "分类名称已存在");
            }
        }
        
        // 设置字段
        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        
        category = clubCategoryRepository.save(category);
        
        Map<String, Integer> result = new HashMap<>();
        result.put("category_id", category.getCategoryId());
        return result;
    }
    
    /**
     * 将Club实体转换为ClubItemDTO
     */
    private ClubListResponseDTO.ClubItemDTO convertToClubItemDTO(Club club) {
        ClubListResponseDTO.ClubItemDTO dto = new ClubListResponseDTO.ClubItemDTO();
        dto.setClub_id(club.getClubId());
        dto.setCategory_id(club.getCategoryId());
        dto.setName(club.getName());
        dto.setPresident_student_id(club.getPresidentStudentId());
        dto.setMember_count(club.getMemberCount());
        dto.setFoundation_date(club.getFoundationDate());
        dto.setStatus(club.getStatus());
        
        // 获取分类名称
        if (club.getCategoryId() != null) {
            clubCategoryRepository.findById(club.getCategoryId())
                .ifPresent(category -> dto.setCategory_name(category.getName()));
        }
        
        // 获取社长姓名
        if (club.getPresidentStudentId() != null) {
            Student student = studentRepository.findByStudentId(club.getPresidentStudentId());
            if (student != null) {
                dto.setPresident_name(student.getName());
            }
        }
        
        return dto;
    }
    
    /**
     * 将ClubCategory实体转换为CategoryItemDTO
     */
    private ClubCategoryResponseDTO.CategoryItemDTO convertToCategoryItemDTO(ClubCategory category) {
        ClubCategoryResponseDTO.CategoryItemDTO dto = new ClubCategoryResponseDTO.CategoryItemDTO();
        dto.setCategory_id(category.getCategoryId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
package com.example.demo.service.admin.impl;

import com.example.demo.common.BusinessException;
import com.example.demo.dto.admin.activity.ActivityApprovalDTO;
import com.example.demo.dto.admin.activity.ApproveActivityRequestDTO;
import com.example.demo.dto.admin.activity.PendingActivityListDTO;
import com.example.demo.dto.admin.activity.PendingActivityResponseDTO;
import com.example.demo.dto.admin.activity.ActivityCategoryDTO;
import com.example.demo.dto.admin.activity.ActivityCategoryListResponseDTO;
import com.example.demo.entity.Activity;
import com.example.demo.entity.ActivityApproval;
import com.example.demo.entity.ActivityCategory;
import com.example.demo.entity.Club;
import com.example.demo.repository.ActivityApprovalRepository;
import com.example.demo.repository.ActivityCategoryRepository;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.service.admin.SchoolAdminActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 学校管理员活动审批服务实现类
 */
@Service
public class SchoolAdminActivityServiceImpl implements SchoolAdminActivityService {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private ActivityApprovalRepository activityApprovalRepository;
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private ActivityCategoryRepository activityCategoryRepository;
    
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // 获取待审批活动列表
    @Override
    public PendingActivityResponseDTO getPendingActivities(Integer clubId, String keyword, int page, int pageSize) {
        
        // 从activity_approvals表获取待审批状态的记录，而不是从activities表
        List<ActivityApproval> pendingApprovals = activityApprovalRepository.findByStatus("待审批");
        
        // 获取对应的活动ID列表
        List<Integer> activityIds = pendingApprovals.stream()
            .map(ActivityApproval::getActivityId)
            .collect(Collectors.toList());
        
        // 根据活动ID获取活动列表
        List<Activity> allActivities = new ArrayList<>();
        if (!activityIds.isEmpty()) {
            allActivities = activityRepository.findAllById(activityIds);
        }
        
        // 按社团ID筛选
        if (clubId != null) {
            allActivities = allActivities.stream()
                .filter(activity -> clubId.equals(activity.getClubId()))
                .collect(Collectors.toList());
        }
        
        // 按关键词筛选（标题或描述中包含关键词）
        if (keyword != null && !keyword.isEmpty()) {
            final String lowerKeyword = keyword.toLowerCase();
            allActivities = allActivities.stream()
                .filter(activity -> (activity.getTitle() != null && activity.getTitle().toLowerCase().contains(lowerKeyword)) ||
                        (activity.getDescription() != null && activity.getDescription().toLowerCase().contains(lowerKeyword)))
                .collect(Collectors.toList());
        }
        
        // 计算总数
        int total = allActivities.size();
        
        // 手动分页
        int start = Math.max(0, (page - 1) * pageSize);
        int end = Math.min(start + pageSize, allActivities.size());
        List<Activity> activities = new ArrayList<>();
        if (start < end) {
            activities = allActivities.subList(start, end);
        }
        
        // 转换为DTO列表
        List<PendingActivityListDTO> dtoList = new ArrayList<>();
        for (Activity activity : activities) {
            PendingActivityListDTO dto = new PendingActivityListDTO();
            dto.setActivityId(activity.getActivityId());
            dto.setClubId(activity.getClubId());  // 添加社团ID
            dto.setTitle(activity.getTitle());
            dto.setCreatedBy(activity.getCreatedBy());
            dto.setCreatedAt(activity.getCreatedAt().format(dateTimeFormatter));
            dto.setStartTime(activity.getStartTime().format(dateTimeFormatter));
            dto.setEndTime(activity.getEndTime().format(dateTimeFormatter));
            dto.setLocation(activity.getLocation());
            
            // 设置社团名称
            if (activity.getClubId() != null) {
                Optional<Club> clubOpt = clubRepository.findById(activity.getClubId());
                clubOpt.ifPresent(club -> dto.setClubName(club.getName()));
            }
            
            // 设置分类名称
            if (activity.getCategoryId() != null) {
                Optional<ActivityCategory> categoryOpt = activityCategoryRepository.findById(activity.getCategoryId());
                categoryOpt.ifPresent(category -> dto.setCategoryName(category.getName()));
            }
            
            dtoList.add(dto);
        }
        
        // 构建响应 - 调整结构以匹配API文档
        PendingActivityResponseDTO response = new PendingActivityResponseDTO();
        response.setList(dtoList);  // 修改字段名从activities到list
        response.setTotal(total);
        response.setPage(page);
        response.setPageSize(pageSize);
        
        return response;
    }
    
    // 审批活动
    @Override
    @Transactional
    public void approveActivity(Integer activityId, ApproveActivityRequestDTO request, String adminId) {
        // 查询活动
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new BusinessException(404, "活动不存在"));
        
        // 查询审批记录并验证状态（基于activity_approvals表中的状态）
        ActivityApproval approval = activityApprovalRepository.findByActivityId(activityId);
        if (approval == null || !"待审批".equals(approval.getStatus())) {
            throw new BusinessException(400, "该活动不是待审批状态，无法进行审批操作");
        }
        
        // 更新活动状态
        if ("已批准".equals(request.getStatus())) {
            activity.setStatus("已批准"); // 批准后活动状态与API文档保持一致
            activity.setApprovedBy(adminId);
            activity.setApprovedAt(LocalDateTime.now());
        } else if ("已拒绝".equals(request.getStatus())) {
            activity.setStatus("已拒绝");
        } else {
            throw new BusinessException(400, "无效的审批状态");
        }
        activity.setUpdatedAt(LocalDateTime.now());
        activityRepository.save(activity);
        
        // 更新活动审批记录
        // approval对象已经在前面查询过了
        approval.setActivityId(activityId);
        approval.setApproverId(adminId);
        approval.setStatus(request.getStatus());
        approval.setComments(request.getComments());
        approval.setApprovedAt(LocalDateTime.now());
        activityApprovalRepository.save(approval);
    }
    
    // 获取活动审批详情
    @Override
    public ActivityApprovalDTO getActivityApprovalDetail(Integer activityId) {
        // 查询活动
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new BusinessException(404, "活动不存在"));
        
        // 查询审批记录
        ActivityApproval approval = activityApprovalRepository.findByActivityId(activityId);
        
        // 转换为DTO
        ActivityApprovalDTO dto = new ActivityApprovalDTO();
        dto.setActivityId(activity.getActivityId());
        dto.setClubId(activity.getClubId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setCategoryId(activity.getCategoryId());
        dto.setPosterUrl(activity.getPosterUrl());
        dto.setLocation(activity.getLocation());
        dto.setMaxParticipants(activity.getMaxParticipants());
        dto.setPoints(activity.getPoints());
        if (activity.getRegistrationDeadline() != null) {
            dto.setRegistrationDeadline(activity.getRegistrationDeadline().format(dateTimeFormatter));
        }
        dto.setCreatedBy(activity.getCreatedBy());
        dto.setCreatedAt(activity.getCreatedAt().format(dateTimeFormatter));
        dto.setStartTime(activity.getStartTime().format(dateTimeFormatter));
        dto.setEndTime(activity.getEndTime().format(dateTimeFormatter));
        
        // 设置社团名称
        if (activity.getClubId() != null) {
            Optional<Club> clubOpt = clubRepository.findById(activity.getClubId());
            clubOpt.ifPresent(club -> dto.setClubName(club.getName()));
        }
        
        // 设置分类名称
        if (activity.getCategoryId() != null) {
            Optional<ActivityCategory> categoryOpt = activityCategoryRepository.findById(activity.getCategoryId());
            categoryOpt.ifPresent(category -> dto.setCategoryName(category.getName()));
        }
        
        // 设置审批信息
        if (approval != null) {
            dto.setApprovalId(approval.getApprovalId());
            dto.setStatus(approval.getStatus());
            dto.setComments(approval.getComments());
            dto.setApproverId(approval.getApproverId());
            if (approval.getApprovedAt() != null) {
                dto.setApprovedAt(approval.getApprovedAt().format(dateTimeFormatter));
            }
        }
        
        return dto;
    }
    
    // 获取活动分类列表
    @Override
    public ActivityCategoryListResponseDTO getActivityCategories() {
        List<ActivityCategory> categories = activityCategoryRepository.findAllByOrderByCreatedAtAsc();
        
        List<ActivityCategoryDTO> dtoList = new ArrayList<>();
        for (ActivityCategory category : categories) {
            ActivityCategoryDTO dto = new ActivityCategoryDTO();
            dto.setCategoryId(category.getCategoryId());
            dto.setName(category.getName());
            dto.setDescription(category.getDescription());
            dtoList.add(dto);
        }
        
        ActivityCategoryListResponseDTO response = new ActivityCategoryListResponseDTO();
        response.setList(dtoList);
        return response;
    }
    
    // 创建或更新活动分类
    @Override
    @Transactional
    public Integer saveActivityCategory(ActivityCategoryDTO categoryDTO) {
        // 明确区分创建和更新操作
        if (categoryDTO.getCategoryId() == null || categoryDTO.getCategoryId() <= 0) {
            // 创建新分类：当categoryId为null或小于等于0时，执行创建操作
            ActivityCategory existing = activityCategoryRepository.findByName(categoryDTO.getName());
            if (existing != null) {
                throw new BusinessException(400, "分类名称已存在，请使用其他名称");
            }
            
            ActivityCategory newCategory = new ActivityCategory();
            newCategory.setName(categoryDTO.getName());
            newCategory.setDescription(categoryDTO.getDescription());
            newCategory.setCreatedAt(LocalDateTime.now());
            
            ActivityCategory saved = activityCategoryRepository.save(newCategory);
            return saved.getCategoryId();
        } else {
            // 更新现有分类：当categoryId大于0时，执行更新操作
            ActivityCategory existing = activityCategoryRepository.findById(categoryDTO.getCategoryId())
                .orElseThrow(() -> new BusinessException(404, "指定ID的分类不存在，无法更新"));
            
            // 检查名称是否被其他分类使用（排除当前分类）
            if (activityCategoryRepository.existsByNameAndCategoryIdNot(categoryDTO.getName(), categoryDTO.getCategoryId())) {
                throw new BusinessException(400, "分类名称已被其他分类使用，请使用其他名称");
            }
            
            // 仅更新必要的字段
            existing.setName(categoryDTO.getName());
            existing.setDescription(categoryDTO.getDescription());
            
            // 保存更新后的分类
            ActivityCategory updated = activityCategoryRepository.save(existing);
            return updated.getCategoryId();
        }
    }
}

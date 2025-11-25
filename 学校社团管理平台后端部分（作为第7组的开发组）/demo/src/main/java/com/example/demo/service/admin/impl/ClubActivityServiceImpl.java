package com.example.demo.service.admin.impl;

import com.example.demo.common.BusinessException;
import com.example.demo.dto.clubadmin.activity.*;
import com.example.demo.entity.Activity;
import com.example.demo.entity.ActivityCategory;
import com.example.demo.entity.ActivityRegistration;
import com.example.demo.entity.ActivityApproval;
import com.example.demo.repository.ActivityApprovalRepository;
import com.example.demo.repository.ActivityCategoryRepository;
import com.example.demo.repository.ActivityRegistrationRepository;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.admin.ClubActivityService;
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
 * 社团活动管理服务实现
 */
@Service
public class ClubActivityServiceImpl implements ClubActivityService {

    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private ActivityCategoryRepository activityCategoryRepository;
    
    @Autowired
    private ActivityRegistrationRepository activityRegistrationRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ActivityApprovalRepository activityApprovalRepository;
    
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public ActivityPageResponseDTO getActivityList(Integer clubId, String status, String keyword, int page, int pageSize) {
        // 获取活动列表 - 如果clubId为null，则获取所有活动
        List<Activity> activities;
        if (clubId != null) {
            activities = activityRepository.findByClubId(clubId);
        } else {
            // 获取所有活动
            activities = activityRepository.findAll();
        }
        
        // 按状态筛选
        if (status != null && !status.isEmpty()) {
            activities = activities.stream()
                .filter(activity -> activity.getStatus() != null && status.equals(activity.getStatus()))
                .collect(Collectors.toList());
        }
        
        // 按关键词筛选
        if (keyword != null && !keyword.isEmpty()) {
            final String lowerKeyword = keyword.toLowerCase();
            activities = activities.stream()
                .filter(activity -> (activity.getTitle() != null && activity.getTitle().toLowerCase().contains(lowerKeyword)) ||
                        (activity.getDescription() != null && activity.getDescription().toLowerCase().contains(lowerKeyword)))
                .collect(Collectors.toList());
        }
        
        int total = activities.size();
        
        // 手动分页
        int start = Math.max(0, (page - 1) * pageSize);
        int end = Math.min(start + pageSize, activities.size());
        List<Activity> pagedActivities = new ArrayList<>();
        if (start < activities.size()) {
            pagedActivities = activities.subList(start, end);
        }
        
        // 转换为DTO
        List<ActivityListDTO> list = new ArrayList<>();
        for (Activity activity : pagedActivities) {
            try {
                ActivityListDTO dto = new ActivityListDTO();
                dto.setActivityId(activity.getActivityId());
                dto.setTitle(activity.getTitle() != null ? activity.getTitle() : "");
                dto.setLocation(activity.getLocation() != null ? activity.getLocation() : "");
                dto.setRegisteredCount(activity.getRegisteredCount() != null ? activity.getRegisteredCount() : 0);
                dto.setStatus(activity.getStatus() != null ? activity.getStatus() : "");
                
                // 设置开始时间
                if (activity.getStartTime() != null) {
                    try {
                        dto.setStartTime(activity.getStartTime().format(dateTimeFormatter));
                    } catch (Exception e) {
                        dto.setStartTime("");
                    }
                }
                
                // 设置分类名称
                if (activity.getCategoryId() != null) {
                    try {
                        Optional<ActivityCategory> categoryOpt = activityCategoryRepository.findById(activity.getCategoryId());
                        categoryOpt.ifPresent(category -> dto.setCategoryName(category.getName() != null ? category.getName() : ""));
                    } catch (Exception e) {
                        // 忽略分类查询异常，继续处理
                    }
                }
                
                list.add(dto);
            } catch (Exception e) {
                // 忽略单个活动处理异常，继续处理其他活动
            }
        }
        
        ActivityPageResponseDTO response = new ActivityPageResponseDTO();
        response.setList(list);
        response.setTotal(total);
        response.setPage(Math.max(1, page));
        response.setPageSize(Math.max(1, pageSize));
        
        return response;
    }
    
    @Override
    @Transactional
    public Integer createActivity(Integer clubId, CreateActivityRequestDTO request, String adminId) {
        // 验证分类是否存在
        if (request.getCategoryId() != null) {
            activityCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(400, "活动分类不存在"));
        }
        
        // 创建活动实体
        Activity activity = new Activity();
        activity.setClubId(clubId);
        activity.setCategoryId(request.getCategoryId());
        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        activity.setPosterUrl(request.getPosterUrl());
        activity.setLocation(request.getLocation());
        
        try {
            // 解析时间字符串
            if (request.getStartTime() != null) {
                activity.setStartTime(LocalDateTime.parse(request.getStartTime(), dateTimeFormatter));
            }
            if (request.getEndTime() != null) {
                activity.setEndTime(LocalDateTime.parse(request.getEndTime(), dateTimeFormatter));
            }
            if (request.getRegistrationDeadline() != null) {
                activity.setRegistrationDeadline(LocalDateTime.parse(request.getRegistrationDeadline(), dateTimeFormatter));
            }
        } catch (Exception e) {
            throw new BusinessException(400, "时间格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
        }
        
        activity.setMaxParticipants(request.getMaxParticipants());
        activity.setPoints(request.getPoints() != null ? request.getPoints().doubleValue() : 0.0);
        activity.setRegisteredCount(0);
        activity.setStatus("草稿"); // 初始状态为草稿
        activity.setCreatedBy(adminId);
        activity.setCreatedAt(LocalDateTime.now());
        activity.setUpdatedAt(LocalDateTime.now());
        
        // 保存活动
        Activity savedActivity = activityRepository.save(activity);
        return savedActivity.getActivityId();
    }
    
    @Override
    public ActivityDetailDTO getActivityDetail(Integer activityId, Integer clubId) {
        // 查询活动
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new BusinessException(404, "活动不存在"));
        
        // 验证活动是否属于该社团
        if (!activity.getClubId().equals(clubId)) {
            throw new BusinessException(403, "无权访问该活动");
        }
        
        // 转换为DTO
        ActivityDetailDTO dto = new ActivityDetailDTO();
        dto.setActivityId(activity.getActivityId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setCategoryId(activity.getCategoryId());
        dto.setPosterUrl(activity.getPosterUrl());
        dto.setLocation(activity.getLocation());
        dto.setMaxParticipants(activity.getMaxParticipants());
        dto.setRegisteredCount(activity.getRegisteredCount() != null ? activity.getRegisteredCount() : 0);
        dto.setPoints(activity.getPoints());
        dto.setStatus(activity.getStatus());
        dto.setCreatedBy(activity.getCreatedBy());
        
        // 设置时间
        if (activity.getStartTime() != null) {
            dto.setStartTime(activity.getStartTime().format(dateTimeFormatter));
        }
        if (activity.getEndTime() != null) {
            dto.setEndTime(activity.getEndTime().format(dateTimeFormatter));
        }
        if (activity.getRegistrationDeadline() != null) {
            dto.setRegistrationDeadline(activity.getRegistrationDeadline().format(dateTimeFormatter));
        }
        if (activity.getCreatedAt() != null) {
            dto.setCreatedAt(activity.getCreatedAt().format(dateTimeFormatter));
        }
        
        // 设置分类名称
        if (activity.getCategoryId() != null) {
            Optional<ActivityCategory> categoryOpt = activityCategoryRepository.findById(activity.getCategoryId());
            categoryOpt.ifPresent(category -> dto.setCategoryName(category.getName()));
        }
        
        return dto;
    }
    
    @Override
    @Transactional
    public void updateActivity(Integer activityId, Integer clubId, UpdateActivityRequestDTO request) {
        // 查询活动
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new BusinessException(404, "活动不存在"));
        
        // 验证活动是否属于该社团
        if (!activity.getClubId().equals(clubId)) {
            throw new BusinessException(403, "无权修改该活动");
        }
        
        // 验证是否可以修改（只有草稿状态才能修改）
        if (!"草稿".equals(activity.getStatus()) && !"待审批".equals(activity.getStatus())) {
            throw new BusinessException(400, "该活动状态不允许修改");
        }
        
        // 更新字段
        if (request.getTitle() != null) {
            activity.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            activity.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            // 验证分类是否存在
            activityCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(400, "活动分类不存在"));
            activity.setCategoryId(request.getCategoryId());
        }
        if (request.getPosterUrl() != null) {
            activity.setPosterUrl(request.getPosterUrl());
        }
        if (request.getLocation() != null) {
            activity.setLocation(request.getLocation());
        }
        if (request.getMaxParticipants() != null) {
            activity.setMaxParticipants(request.getMaxParticipants());
        }
        if (request.getPoints() != null) {
            activity.setPoints(request.getPoints().doubleValue());
        }
        
        // 更新时间字段
        try {
            if (request.getStartTime() != null) {
                activity.setStartTime(LocalDateTime.parse(request.getStartTime(), dateTimeFormatter));
            }
            if (request.getEndTime() != null) {
                activity.setEndTime(LocalDateTime.parse(request.getEndTime(), dateTimeFormatter));
            }
            if (request.getRegistrationDeadline() != null) {
                activity.setRegistrationDeadline(LocalDateTime.parse(request.getRegistrationDeadline(), dateTimeFormatter));
            }
        } catch (Exception e) {
            throw new BusinessException(400, "时间格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
        }
        
        activity.setUpdatedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }
    
    @Override
    @Transactional
    public void submitForApproval(Integer activityId, Integer clubId) {
        // 查询活动
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new BusinessException(404, "活动不存在"));
        
        // 验证活动是否属于该社团
        if (!activity.getClubId().equals(clubId)) {
            throw new BusinessException(403, "无权操作该活动");
        }
        
        // 验证状态 - 允许草稿和待审批状态提交
        if (!"草稿".equals(activity.getStatus()) && !"待审批".equals(activity.getStatus())) {
            throw new BusinessException(400, "只有草稿状态的活动可以提交审批");
        }
        
        // 更新状态为待审批
        activity.setStatus("待审批");
        activity.setUpdatedAt(LocalDateTime.now());
        activityRepository.save(activity);
        
        // 检查是否已存在待审批记录
        List<ActivityApproval> existingApprovals = activityApprovalRepository.findAll().stream()
            .filter(approval -> approval.getActivityId().equals(activityId) && "待审批".equals(approval.getStatus()))
            .collect(Collectors.toList());
        
        if (existingApprovals.isEmpty()) {
            // 创建新的审批记录
            ActivityApproval approval = new ActivityApproval();
            approval.setActivityId(activityId);
            approval.setApproverId("SA001"); // 设置默认学校管理员ID
            approval.setStatus("待审批");
            approval.setCreatedAt(LocalDateTime.now());
            activityApprovalRepository.save(approval);
        } else {
            // 更新现有待审批记录的时间
            ActivityApproval existingApproval = existingApprovals.get(0);
            existingApproval.setCreatedAt(LocalDateTime.now());
            activityApprovalRepository.save(existingApproval);
        }
    }
    
    @Override
    public ActivityRegistrationResponseDTO getActivityRegistrations(Integer activityId, Integer clubId, String status, int page, int pageSize) {
        // 查询活动并验证归属
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new BusinessException(404, "活动不存在"));
        
        if (!activity.getClubId().equals(clubId)) {
            throw new BusinessException(403, "无权访问该活动的报名信息");
        }
        
        // 获取报名记录
        List<ActivityRegistration> registrations = activityRegistrationRepository.findAll().stream()
            .filter(reg -> reg.getActivityId().equals(activityId))
            .collect(Collectors.toList());
        
        // 按状态筛选
        if (status != null && !status.isEmpty()) {
            registrations = registrations.stream()
                .filter(reg -> status.equals(reg.getStatus()))
                .collect(Collectors.toList());
        }
        
        int total = registrations.size();
        
        // 手动分页
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, registrations.size());
        List<ActivityRegistration> pagedRegistrations = new ArrayList<>();
        if (start < registrations.size()) {
            pagedRegistrations = registrations.subList(start, end);
        }
        
        // 转换为DTO
        List<ActivityRegistrationDTO> list = new ArrayList<>();
        for (ActivityRegistration registration : pagedRegistrations) {
            ActivityRegistrationDTO dto = new ActivityRegistrationDTO();
            dto.setRegistrationId(registration.getRegistrationId());
            dto.setStudentId(registration.getStudentId());
            dto.setStatus(registration.getStatus());
            dto.setAttended(registration.getAttended());
            dto.setPointsEarned(registration.getPointsEarned());
            
            // 设置报名时间
            if (registration.getRegistrationTime() != null) {
                dto.setRegistrationTime(registration.getRegistrationTime().format(dateTimeFormatter));
            }
            
            // 获取学生信息
            var studentOpt = studentRepository.findById(registration.getStudentId());
            if (studentOpt.isPresent()) {
                var student = studentOpt.get();
                dto.setName(student.getName());
                dto.setMajor(student.getMajor());
                dto.setGrade(student.getGrade());
            }
            
            list.add(dto);
        }
        
        ActivityRegistrationResponseDTO response = new ActivityRegistrationResponseDTO();
        response.setList(list);
        response.setTotal(total);
        response.setPage(page);
        response.setPageSize(pageSize);
        
        return response;
    }
    
    @Override
    @Transactional
    public void confirmAttendance(Integer activityId, Integer clubId, ConfirmAttendanceRequestDTO request) {
        // 查询活动并验证归属
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new BusinessException(404, "活动不存在"));
        
        if (!activity.getClubId().equals(clubId)) {
            throw new BusinessException(403, "无权操作该活动");
        }
        
        // 验证活动状态
        if (!"已批准".equals(activity.getStatus()) && !"进行中".equals(activity.getStatus())) {
            throw new BusinessException(400, "只有已批准或进行中的活动才能确认参与");
        }
        
        // 验证报名记录
        if (request.getRegistrationIds() == null || request.getRegistrationIds().isEmpty()) {
            throw new BusinessException(400, "请选择要确认的报名记录");
        }
        
        // 验证积分
        if (request.getPointsEarned() == null || request.getPointsEarned() < 0) {
            throw new BusinessException(400, "积分必须大于等于0");
        }
        
        // 批量更新报名记录
        for (Integer registrationId : request.getRegistrationIds()) {
            ActivityRegistration registration = activityRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new BusinessException(404, "报名记录不存在"));
            
            // 验证报名记录是否属于该活动
            if (!registration.getActivityId().equals(activityId)) {
                throw new BusinessException(400, "报名记录与活动不匹配");
            }
            
            // 验证报名记录状态，已取消的报名不能确认参与
            if ("已取消".equals(registration.getStatus())) {
                throw new BusinessException(400, "已取消的报名记录不能确认参与");
            }
            
            // 更新状态为已参加
            registration.setStatus("已参加");
            registration.setAttended(1);
            registration.setPointsEarned(request.getPointsEarned().doubleValue());
            
            activityRegistrationRepository.save(registration);
        }
    }
}
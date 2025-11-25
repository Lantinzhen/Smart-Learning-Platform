package com.example.demo.service.user.impl;

import com.example.demo.common.BusinessException;
import com.example.demo.dto.user.activity.*;
import com.example.demo.entity.Activity;
import com.example.demo.entity.ActivityCategory;
import com.example.demo.entity.ActivityRegistration;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubMember;
import com.example.demo.repository.ActivityCategoryRepository;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.ActivityRegistrationRepository;
import com.example.demo.repository.ClubMemberRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.user.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;;

/**
 * 活动管理服务实现类
 */
@Service
public class ActivityServiceImpl implements ActivityService {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private ActivityCategoryRepository activityCategoryRepository;
    
    @Autowired
    private ActivityRegistrationRepository activityRegistrationRepository;
    
    @Autowired
    private ClubMemberRepository clubMemberRepository;
    
    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private JwtUtil jwtUtil;
    
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    /**
     * 根据学生token获取其所在社团的活动列表
     * @param token 学生JWT令牌
     * @return 学生所在社团的活动列表
     */
    @Override
    public List<ActivityInfoDTO> getActivitiesByStudentToken(String token) {

        // 从JWT令牌中解析学生ID
        String currentStudentId = jwtUtil.getUserIdFromToken(token);
        if (currentStudentId == null || currentStudentId.isEmpty()) {
            throw new RuntimeException("无法从令牌中获取学生信息");
        }
        
        // 从数据库查询该学生所在的社团列表
        List<ClubMember> clubMembers = clubMemberRepository.findByStudentId(currentStudentId);
        
        // 获取所有社团信息并构建映射
        List<Club> allClubs = clubRepository.findAll();
        Map<Integer, String> clubNameMap = allClubs.stream()
                .collect(Collectors.toMap(Club::getClubId, Club::getName));
        
        List<Activity> allActivities;
        if (clubMembers.isEmpty()) {
            // 如果学生没有参加任何社团，则显示所有社团的已批准活动
            allActivities = activityRepository.findAll().stream()
                    .filter(activity -> "已批准".equals(activity.getStatus()))
                    .collect(Collectors.toList());
        } else {
            // 提取社团ID列表
            List<Integer> clubIds = clubMembers.stream()
                    .map(ClubMember::getClubId)
                    .collect(Collectors.toList());
            
            // 查询学生所在社团的所有已批准活动
            allActivities = new ArrayList<>();
            for (Integer clubId : clubIds) {
                List<Activity> clubActivities = activityRepository.findByClubId(clubId);
                // 过滤只显示已批准状态的活动
                List<Activity> approvedActivities = clubActivities.stream()
                        .filter(activity -> "已批准".equals(activity.getStatus()))
                        .collect(Collectors.toList());
                allActivities.addAll(approvedActivities);
            }
        }
        
        // 转换为DTO对象
        List<ActivityInfoDTO> activityDTOs = new ArrayList<>();
        for (Activity activity : allActivities) {
            ActivityInfoDTO dto = new ActivityInfoDTO();
            dto.setActivityId(activity.getActivityId());
            dto.setTitle(activity.getTitle());
            dto.setDescription(activity.getDescription());
            dto.setStartTime(activity.getStartTime() != null ? activity.getStartTime().format(dateTimeFormatter) : "");
            dto.setEndTime(activity.getEndTime() != null ? activity.getEndTime().format(dateTimeFormatter) : "");
            dto.setLocation(activity.getLocation());
            dto.setStatus(activity.getStatus());
            dto.setClubName(clubNameMap.getOrDefault(activity.getClubId(), "未知社团"));
            
            // 从数据库获取分类名称
            if (activity.getCategoryId() != null) {
                Optional<ActivityCategory> categoryOpt = activityCategoryRepository.findById(activity.getCategoryId());
                categoryOpt.ifPresent(category -> dto.setCategoryName(category.getName()));
            } else {
                dto.setCategoryName("校园活动"); // 默认分类名称
            }
            
            // 从数据库获取最大参与人数
            if (activity.getMaxParticipants() != null) {
                dto.setMaxParticipants(activity.getMaxParticipants());
            }
            // 如果数据库中没有maxParticipants，则不设置该字段（保持为null）
            
            // 从活动实体获取已报名人数
            dto.setRegisteredCount(activity.getRegisteredCount() != null ? activity.getRegisteredCount() : 0);
            
            activityDTOs.add(dto);
        }
        
        return activityDTOs;
    }
    
    
    /**
     * 获取活动详情
     * @param activityId 活动ID
     * @return 活动详情
     */
    @Override
    public ActivityDetailDTO getActivityDetail(Integer activityId) {
        // 从数据库查询活动详情
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        
        ActivityDetailDTO activityDetail = new ActivityDetailDTO();
        activityDetail.setActivityId(activity.getActivityId());
        activityDetail.setTitle(activity.getTitle());
        activityDetail.setDescription(activity.getDescription());
        
        // 设置分类名称
        if (activity.getCategoryId() != null) {
            Optional<ActivityCategory> categoryOpt = activityCategoryRepository.findById(activity.getCategoryId());
            categoryOpt.ifPresent(category -> activityDetail.setCategoryName(category.getName()));
        }
        
        // 设置时间格式 - 使用统一的DateTimeFormatter
        if (activity.getStartTime() != null) {
            activityDetail.setStartTime(activity.getStartTime().format(dateTimeFormatter));
        }
        if (activity.getEndTime() != null) {
            activityDetail.setEndTime(activity.getEndTime().format(dateTimeFormatter));
        }
        
        activityDetail.setLocation(activity.getLocation());
        
        // 设置社团信息
        if (activity.getClubId() != null) {
            Optional<Club> clubOpt = clubRepository.findById(activity.getClubId());
            if (clubOpt.isPresent()) {
                Club club = clubOpt.get();
                activityDetail.setClubName(club.getName());
                // 从社团实体获取联系邮箱
                activityDetail.setClubContact(club.getContactInfo() != null ? club.getContactInfo() : "");
            }
        }
        
        // 从活动实体获取已报名人数
        activityDetail.setRegisteredCount(activity.getRegisteredCount() != null ? activity.getRegisteredCount() : 0);
        activityDetail.setMaxParticipants(activity.getMaxParticipants());
        
        // 直接使用实体中的报名截止时间字段
        if (activity.getRegistrationDeadline() != null) {
            activityDetail.setRegistrationDeadline(activity.getRegistrationDeadline().format(dateTimeFormatter));
        }
        
        activityDetail.setStatus(activity.getStatus());
        
        // 直接使用实体中的创建时间字段
        if (activity.getCreatedAt() != null) {
            activityDetail.setCreatedAt(activity.getCreatedAt().format(dateTimeFormatter));
        }
        
        return activityDetail;
    }
    
    /**
     * 报名参加活动
     * @param token JWT令牌
     * @param activityId 活动ID
     */
    @Override
    public void registerForActivity(String token, Integer activityId) {
        // 从JWT令牌中解析学生ID
        String studentId = jwtUtil.getUserIdFromToken(token);
        if (studentId == null || studentId.isEmpty()) {
            throw new RuntimeException("无法从令牌中获取学生信息");
        }
        
        // 检查活动是否存在
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(404,"活动不存在"));
        
        // 检查活动状态是否为已批准
        if (!"已批准".equals(activity.getStatus())) {
            throw new BusinessException(404,"活动尚未批准，无法报名");
        }
        
        // 检查报名截止时间
        if (activity.getRegistrationDeadline() != null && 
            java.time.LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
            throw new BusinessException(404,"报名已截止");
        }
        
        // 检查是否已达到最大参与人数
        if (activity.getMaxParticipants() != null && 
            activity.getRegisteredCount() != null && 
            activity.getRegisteredCount() >= activity.getMaxParticipants()) {
            throw new BusinessException(404,"活动人数已满");
        }
        
        // 检查是否已报名
        // 使用更通用的查询方式
        List<ActivityRegistration> registrations = activityRegistrationRepository.findByStudentId(studentId);
        boolean alreadyRegistered = registrations.stream()
                .anyMatch(reg -> reg.getActivityId().equals(activityId));
        if (alreadyRegistered) {
            throw new BusinessException(404,"您已经报名过该活动");
        }
        
        // 验证学生是否是活动所属社团的成员
        if (activity.getClubId() != null) {
            ClubMember clubMember = clubMemberRepository.findByClubIdAndStudentId(activity.getClubId(), studentId);
            if (clubMember == null) {
                throw new BusinessException(404,"只有该社团成员才能报名此活动");
            }
        } else {
            throw new BusinessException(404,"活动未关联到任何社团");
        }
        
        // 创建新的报名记录
        ActivityRegistration registration = new ActivityRegistration();
        registration.setStudentId(studentId);
        registration.setActivityId(activityId);
        registration.setRegistrationTime(java.time.LocalDateTime.now());
        registration.setStatus("已报名"); // 设置初始状态
        registration.setAttended(0); // 初始未参加
        
        // 保存到数据库
        activityRegistrationRepository.save(registration);
        
        // 更新活动的已报名人数
        if (activity.getRegisteredCount() == null) {
            activity.setRegisteredCount(1);
        } else {
            activity.setRegisteredCount(activity.getRegisteredCount() + 1);
        }
        activityRepository.save(activity);

    }
    
    /**
     * 取消活动报名
     * @param token JWT令牌
     * @param activityId 活动ID
     */
    @Override
    public void cancelRegistration(String token, Integer activityId) {
        // 从JWT令牌中解析学生ID
        String studentId = jwtUtil.getUserIdFromToken(token);
        if (studentId == null || studentId.isEmpty()) {
            throw new BusinessException(401, "无法从令牌中获取学生信息");
        }
        
        // 检查活动是否存在
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(404, "活动不存在"));
        
        // 检查是否已经报名
        List<ActivityRegistration> registrations = activityRegistrationRepository.findByStudentId(studentId);
        Optional<ActivityRegistration> registrationOpt = registrations.stream()
                .filter(reg -> reg.getActivityId().equals(activityId))
                .findFirst();
        
        if (!registrationOpt.isPresent()) {
            throw new BusinessException(404, "您未报名该活动");
        }
        
        // 检查活动是否已经开始，已开始的活动不允许取消报名
        if (activity.getStartTime() != null && java.time.LocalDateTime.now().isAfter(activity.getStartTime())) {
            throw new BusinessException(400, "活动已开始，不允许取消报名");
        }
        
        // 删除报名记录
        activityRegistrationRepository.delete(registrationOpt.get());
        
        // 更新活动的已报名人数
        if (activity.getRegisteredCount() != null && activity.getRegisteredCount() > 0) {
            activity.setRegisteredCount(activity.getRegisteredCount() - 1);
            activityRepository.save(activity);
        }
    }
    
    /**
     * 获取我报名的活动列表
     * @param token JWT令牌
     * @return 我的活动列表
     */
    @Override
    public List<MyActivityDTO> getMyActivities(String token) {
        // 从JWT令牌中解析学生ID
        String studentId = jwtUtil.getUserIdFromToken(token);
        if (studentId == null || studentId.isEmpty()) {
            throw new RuntimeException("无法从令牌中获取学生信息");
        }
        
        // 从数据库查询该学生报名的活动
        List<ActivityRegistration> registrations = activityRegistrationRepository.findByStudentId(studentId);
        
        if (registrations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取活动ID列表
        List<Integer> activityIds = registrations.stream()
                .map(ActivityRegistration::getActivityId)
                .collect(Collectors.toList());
        
        // 获取活动信息
        List<Activity> activities = activityRepository.findAllById(activityIds);
        Map<Integer, Activity> activityMap = activities.stream()
                .collect(Collectors.toMap(Activity::getActivityId, a -> a));
        
        // 获取社团信息
        List<Integer> clubIds = activities.stream()
                .map(Activity::getClubId)
                .distinct()
                .collect(Collectors.toList());
        List<Club> clubs = clubRepository.findAllById(clubIds);
        Map<Integer, String> clubNameMap = clubs.stream()
                .collect(Collectors.toMap(Club::getClubId, Club::getName));
        
        // 构建我的活动列表
        List<MyActivityDTO> myActivities = new ArrayList<>();
        for (ActivityRegistration registration : registrations) {
            Activity activity = activityMap.get(registration.getActivityId());
            if (activity != null) {
                MyActivityDTO dto = new MyActivityDTO();
                dto.setActivityId(activity.getActivityId());
                dto.setTitle(activity.getTitle());
                dto.setStartTime(activity.getStartTime() != null ? activity.getStartTime().format(dateTimeFormatter) : "");
                dto.setEndTime(activity.getEndTime() != null ? activity.getEndTime().format(dateTimeFormatter) : "");
                dto.setLocation(activity.getLocation());
                dto.setClubName(clubNameMap.getOrDefault(activity.getClubId(), "未知社团"));
                dto.setStatus(activity.getStatus());
                dto.setRegistrationTime(registration.getRegistrationTime() != null ? registration.getRegistrationTime().format(dateTimeFormatter) : "");
                dto.setPoints(registration.getPointsEarned() != null ? registration.getPointsEarned() : 0.0);
                
                myActivities.add(dto);
            }
        }
        
        return myActivities;
    }
}
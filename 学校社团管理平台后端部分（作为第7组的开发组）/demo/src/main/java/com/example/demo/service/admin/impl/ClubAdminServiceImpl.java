package com.example.demo.service.admin.impl;

import com.example.demo.common.BusinessException;
import com.example.demo.dto.admin.ClubAdminDashboardDTO;
import com.example.demo.service.admin.ClubAdminService;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 社团管理员服务实现类
 */
@Service
public class ClubAdminServiceImpl implements ClubAdminService {
    private static final Logger logger = LoggerFactory.getLogger(ClubAdminServiceImpl.class);

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubApplicationRepository clubApplicationRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ClubAdminRepository clubAdminRepository;
    
    /**
     * 获取单个社团管理概览信息
     * @param clubId 社团ID
     * @return 社团的管理概览数据
     */
    @Override
    public ClubAdminDashboardDTO getClubDashboard(Integer clubId) {
        logger.info("开始获取社团管理概览信息，社团ID: {}", clubId);
        
        try {
            logger.debug("开始查询社团信息，社团ID: {}", clubId);
            Club club = clubRepository.findById(clubId).orElse(null);
            if (club == null) {
                logger.warn("未找到指定ID的社团，社团ID: {}", clubId);
                throw new BusinessException(404, "未找到指定ID的社团");
            }
            
            // 为社团创建DashboardDTO
            ClubAdminDashboardDTO dashboardDTO = createClubAdminDashboardDTO(club);
            
            logger.info("成功获取社团管理概览信息，社团ID: {}", clubId);
            return dashboardDTO;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("获取社团管理概览信息异常，社团ID: {}", clubId, e);
            throw new BusinessException(500, "获取社团管理概览信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据社团信息创建DashboardDTO
     * @param club 社团信息
     * @return 社团管理概览数据
     */
    private ClubAdminDashboardDTO createClubAdminDashboardDTO(Club club) {
        logger.debug("开始为社团创建DashboardDTO，社团ID: {}, 社团名称: {}", club.getClubId(), club.getName());
        
        Integer clubId = club.getClubId();
        ClubAdminDashboardDTO dashboardDTO = new ClubAdminDashboardDTO();
        dashboardDTO.setClubName(club.getName());
        dashboardDTO.setMemberCount(club.getMemberCount() != null ? club.getMemberCount() : 0);

        // 查询待处理的申请数量
        try {
            logger.debug("查询待处理申请数量，社团ID: {}", clubId);
            long pendingApplicationsCount = clubApplicationRepository.countByClubIdAndStatus(clubId, "待审核");
            dashboardDTO.setPendingApplicationsCount((int) pendingApplicationsCount);
        } catch (Exception e) {
            logger.error("查询待处理申请数量异常，社团ID: {}", clubId, e);
            dashboardDTO.setPendingApplicationsCount(0);
        }

        // 查询活动数量
        try {
            logger.debug("查询活动数量，社团ID: {}", clubId);
            long activitiesCount = activityRepository.countByClubId(clubId);
            dashboardDTO.setActivitiesCount((int) activitiesCount);
        } catch (Exception e) {
            logger.error("查询活动数量异常，社团ID: {}", clubId, e);
            dashboardDTO.setActivitiesCount(0);
        }

        // 查询活跃活动数量
        try {
            logger.debug("查询活跃活动数量，社团ID: {}", clubId);
            // 先获取该社团的所有活动
            List<Activity> allActivities = activityRepository.findByClubId(clubId);
            // 过滤出状态为"进行中"的活动
            int activeActivitiesCount = (int) allActivities.stream()
                    .filter(activity -> "进行中".equals(activity.getStatus()))
                    .count();
            dashboardDTO.setUpcomingActivitiesCount(activeActivitiesCount);
        } catch (Exception e) {
            logger.error("查询活跃活动数量异常，社团ID: {}", clubId, e);
            dashboardDTO.setUpcomingActivitiesCount(0);
        }

        // 查询最近加入的成员
        try {
            logger.debug("查询最近加入的成员，社团ID: {}", clubId);
            // 获取该社团的所有成员，并过滤出活跃成员（status=1）
            List<ClubMember> allMembers = clubMemberRepository.findByClubId(clubId)
                    .stream()
                    .filter(member -> member.getStatus() == 1)
                    .collect(Collectors.toList());
            
            if (!allMembers.isEmpty()) {
                // 按加入日期降序排序，并取前5名
                List<ClubAdminDashboardDTO.RecentMemberDTO> recentMembers = allMembers.stream()
                        .sorted((m1, m2) -> {
                            if (m1.getJoinDate() == null) return 1;
                            if (m2.getJoinDate() == null) return -1;
                            return m2.getJoinDate().compareTo(m1.getJoinDate()); // 降序
                        })
                        .limit(5)
                        .map(member -> {
                            ClubAdminDashboardDTO.RecentMemberDTO dto = new ClubAdminDashboardDTO.RecentMemberDTO();
                            dto.setStudentId(member.getStudentId());
                            
                            // 获取学生姓名
                            Student student = studentRepository.findByStudentId(member.getStudentId());
                            if (student != null) {
                                dto.setName(student.getName());
                            }
                            
                            // 格式化日期
                            if (member.getJoinDate() != null) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                dto.setJoinDate(member.getJoinDate().format(formatter));
                            }
                            return dto;
                        })
                        .collect(Collectors.toList());
                
                dashboardDTO.setRecentMembers(recentMembers);
            }
        } catch (Exception e) {
            logger.error("查询最近加入的成员异常，社团ID: {}", clubId, e);
            dashboardDTO.setRecentMembers(new ArrayList<>());
        }

        // 查询最近的活动
        try {
            logger.debug("查询最近的活动，社团ID: {}", clubId);
            // 获取该社团的所有活动
            List<Activity> allActivities = activityRepository.findByClubId(clubId);
            
            if (allActivities != null && !allActivities.isEmpty()) {
                // 按开始时间降序排序，并取前5名
                List<ClubAdminDashboardDTO.RecentActivityDTO> recentActivities = allActivities.stream()
                        .sorted((a1, a2) -> {
                            if (a1.getStartTime() == null) return 1;
                            if (a2.getStartTime() == null) return -1;
                            return a2.getStartTime().compareTo(a1.getStartTime()); // 降序
                        })
                        .limit(5)
                        .map(activity -> {
                            ClubAdminDashboardDTO.RecentActivityDTO dto = new ClubAdminDashboardDTO.RecentActivityDTO();
                            dto.setActivityId(activity.getActivityId());
                            dto.setTitle(activity.getTitle());
                            dto.setStatus(activity.getStatus());
                            
                            // 格式化时间
                            if (activity.getStartTime() != null) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                                dto.setStartTime(activity.getStartTime().format(formatter));
                            }
                            return dto;
                        })
                        .collect(Collectors.toList());
                
                dashboardDTO.setRecentActivities(recentActivities);
            }
        } catch (Exception e) {
            logger.error("查询最近的活动异常，社团ID: {}", clubId, e);
            dashboardDTO.setRecentActivities(new ArrayList<>());
        }

        logger.debug("为社团创建DashboardDTO成功，社团ID: {}, 社团名称: {}", clubId, club.getName());
        return dashboardDTO;
    }
    
    /**
     * 根据管理员ID获取其管理的社团ID
     * @param adminId 管理员ID
     * @return 社团ID
     */
    @Override
    public Integer getClubIdByAdminId(String adminId) {
        // 查询社团管理员信息
        Optional<ClubAdmin> clubAdminOpt = clubAdminRepository.findById(adminId);
        if (clubAdminOpt.isPresent()) {
            return clubAdminOpt.get().getClubId();
        }
        return null;
    }
}
package com.example.demo.service.user.impl;

import com.example.demo.dto.user.dashboard.DashboardDataDTO;
import com.example.demo.entity.Student;
import com.example.demo.entity.ClubMember;
import com.example.demo.entity.ActivityRegistration;
import com.example.demo.entity.Club;
import com.example.demo.entity.Activity;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.ClubMemberRepository;
import com.example.demo.repository.ActivityRegistrationRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.StudentPointsRepository;
import com.example.demo.service.user.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ClubMemberRepository clubMemberRepository;
    
    @Autowired
    private ActivityRegistrationRepository activityRegistrationRepository;
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private StudentPointsRepository studentPointsRepository;
    
    /**
     * 获取仪表盘数据
     * @param userId 用户ID
     * @return 仪表盘数据
     */
    @Override
    public DashboardDataDTO getDashboardData(String userId) {
        // userId已经从请求属性中获取，直接使用
        // 查询数据库获取相关数据
        
        DashboardDataDTO dashboardData = new DashboardDataDTO();
        
        // 设置个人信息
        Student student = studentRepository.findByStudentId(userId);
        if (student != null) {
            DashboardDataDTO.PersonalInfo personalInfo = new DashboardDataDTO.PersonalInfo();
            personalInfo.setStudentId(student.getStudentId());
            personalInfo.setName(student.getName());
            personalInfo.setMajor(student.getMajor());
            personalInfo.setGrade(student.getGrade());
            // 获取学生总积分
            Double totalPoints = studentPointsRepository.sumPointsByStudentId(userId);
            // 直接使用Double类型保留小数，不进行截断
            personalInfo.setPoints(totalPoints != null ? totalPoints : 0.0);
            dashboardData.setPersonalInfo(personalInfo);
        }
        
        // 设置最近活动
        // 使用分页查询获取最近的5个活动注册记录
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 5);
        List<ActivityRegistration> registrations = activityRegistrationRepository.findLatestByStudentId(userId, pageable);
        List<DashboardDataDTO.ClubActivity> recentActivities = new ArrayList<>();
        
        // 提取活动ID列表以便批量查询
        List<Integer> activityIds = registrations.stream()
            .map(ActivityRegistration::getActivityId)
            .filter(java.util.Objects::nonNull)
            .collect(java.util.stream.Collectors.toList());
            
        // 批量查询活动信息
        List<Activity> activities = activityIds.isEmpty() ? new ArrayList<>() : activityRepository.findAllById(activityIds);
        java.util.Map<Integer, Activity> activityMap = activities.stream()
            .collect(java.util.stream.Collectors.toMap(Activity::getActivityId, java.util.function.Function.identity()));
            
        // 提取社团ID列表以便批量查询
        List<Integer> clubIds = activities.stream()
            .map(Activity::getClubId)
            .filter(java.util.Objects::nonNull)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
            
        // 批量查询社团信息
        List<Club> activityClubs = clubIds.isEmpty() ? new ArrayList<>() : clubRepository.findAllById(clubIds);
        java.util.Map<Integer, Club> activityClubMap = activityClubs.stream()
            .collect(java.util.stream.Collectors.toMap(Club::getClubId, java.util.function.Function.identity()));
        
        // 构建最近活动列表
        registrations.forEach(registration -> {
            Activity activity = activityMap.get(registration.getActivityId());
            if (activity != null) {
                DashboardDataDTO.ClubActivity clubActivity = new DashboardDataDTO.ClubActivity();
                clubActivity.setActivityId(activity.getActivityId());
                clubActivity.setTitle(activity.getTitle());
                clubActivity.setTime(activity.getStartTime().toString());
                clubActivity.setLocation(activity.getLocation());
                
                // 获取社团名称
                Club club = activityClubMap.get(activity.getClubId());
                if (club != null) {
                    clubActivity.setClubName(club.getName());
                }
                
                recentActivities.add(clubActivity);
            }
        });
        
        dashboardData.setRecentActivities(recentActivities);
        
        // 设置我的社团
        List<ClubMember> clubMembers = clubMemberRepository.findByStudentId(userId);
        List<DashboardDataDTO.ClubInfo> myClubs = new ArrayList<>();
        
        // 提取社团ID列表以便批量查询
        List<Integer> myClubIds = clubMembers.stream()
            .map(ClubMember::getClubId)
            .filter(java.util.Objects::nonNull)
            .collect(java.util.stream.Collectors.toList());
            
        // 批量查询社团信息
        List<Club> myClubsList = myClubIds.isEmpty() ? new ArrayList<>() : clubRepository.findAllById(myClubIds);
        java.util.Map<Integer, Club> myClubMap = myClubsList.stream()
            .collect(java.util.stream.Collectors.toMap(Club::getClubId, java.util.function.Function.identity()));
        
        clubMembers.forEach(clubMember -> {
            Club club = myClubMap.get(clubMember.getClubId());
            if (club != null) {
                DashboardDataDTO.ClubInfo clubInfo = new DashboardDataDTO.ClubInfo();
                clubInfo.setClubId(club.getClubId());
                clubInfo.setName(club.getName());
                // 获取社团分类名称
                if (club.getCategory() != null) {
                    clubInfo.setCategory(club.getCategory().getName());
                } else {
                    clubInfo.setCategory("社团");
                }
                myClubs.add(clubInfo);
            }
        });
        
        dashboardData.setMyClubs(myClubs);
        
        // 设置统计数据
        DashboardDataDTO.Statistics statistics = new DashboardDataDTO.Statistics();
        statistics.setTotalClubs(clubMemberRepository.countByStudentId(userId));
        statistics.setTotalActivities(activityRegistrationRepository.countParticipatedByStudentId(userId));
        // 获取学生总积分
        Double totalPoints = studentPointsRepository.sumPointsByStudentId(userId);
        // 直接使用Double类型保留小数，不进行截断
        statistics.setTotalPoints(totalPoints != null ? totalPoints : 0.0);
        // 实现排名逻辑
        // 使用更高效的查询方式获取排名
        Integer ranking = studentPointsRepository.findStudentRanking(userId);
        statistics.setRanking(ranking != null ? ranking : 0);
        dashboardData.setStatistics(statistics);
        
        return dashboardData;
    }
}
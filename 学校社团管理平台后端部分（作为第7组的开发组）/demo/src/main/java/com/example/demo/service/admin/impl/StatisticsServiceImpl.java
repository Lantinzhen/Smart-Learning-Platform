package com.example.demo.service.admin.impl;

import com.example.demo.dto.statistics.ActivityParticipationStatsDTO;
import com.example.demo.dto.statistics.ClubActivityStatsDTO;
import com.example.demo.dto.statistics.StudentActivityStatsDTO;
import com.example.demo.entity.Activity;
import com.example.demo.entity.ActivityRegistration;
import com.example.demo.entity.Club;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.admin.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private ActivityRegistrationRepository activityRegistrationRepository;
    
    @Autowired
    private StudentPointsRepository studentPointsRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Override
    public List<ClubActivityStatsDTO> getClubActivityStatistics(LocalDate startDate, LocalDate endDate, Integer categoryId) {
        // 查询符合条件的社团
        List<Club> clubs = categoryId != null ? 
                clubRepository.findByCategoryId(categoryId) : 
                clubRepository.findAll();
        
        List<ClubActivityStatsDTO> statsList = new ArrayList<>();
        
        // 转换日期为LocalDateTime以便查询
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;
        
        for (Club club : clubs) {
            ClubActivityStatsDTO stats = new ClubActivityStatsDTO();
            stats.setClubId(club.getClubId());
            stats.setClubName(club.getName());
            
            // 查询该社团的所有活动
            List<Activity> activities = activityRepository.findByClubId(club.getClubId());
            
            // 过滤时间范围内的活动
            List<Activity> filteredActivities = activities.stream()
                    .filter(activity -> {
                        if (startDateTime != null && activity.getStartTime().isBefore(startDateTime)) {
                            return false;
                        }
                        if (endDateTime != null && activity.getEndTime().isAfter(endDateTime)) {
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            
            stats.setTotalActivities(filteredActivities.size());
            
            // 计算参与人数和平均分
            int totalParticipants = 0;
            double totalPoints = 0;
            int pointCount = 0;
            
            for (Activity activity : filteredActivities) {
                // 统计参与人数
                List<ActivityRegistration> allRegistrations = activityRegistrationRepository.findAll();
                List<ActivityRegistration> registrations = allRegistrations.stream()
                        .filter(reg -> activity.getActivityId().equals(reg.getActivityId()))
                        .collect(Collectors.toList());
                totalParticipants += registrations.size();
                
                // 统计积分（通过查询所有积分记录并过滤）
                List<StudentPoints> allPoints = studentPointsRepository.findAll();
                List<StudentPoints> pointsList = allPoints.stream()
                        .filter(p -> activity.getActivityId().equals(p.getActivityId()))
                        .collect(Collectors.toList());
                for (StudentPoints points : pointsList) {
                    if (points.getPoints() != null) {
                        totalPoints += points.getPoints();
                        pointCount++;
                    }
                }
            }
            
            stats.setTotalParticipants(totalParticipants);
            stats.setAvgPoints(pointCount > 0 ? totalPoints / pointCount : 0);
            
            statsList.add(stats);
        }
        
        return statsList;
    }
    
    @Override
    public List<StudentActivityStatsDTO> getStudentActivityStatistics(LocalDate startDate, LocalDate endDate, String major, String grade) {
        // 查询所有学生，后续进行过滤
        List<Student> allStudents = studentRepository.findAll();
        
        // 根据条件过滤学生
        List<Student> students = allStudents.stream()
                .filter(student -> {
                    if (major != null && !major.equals(student.getMajor())) {
                        return false;
                    }
                    if (grade != null && !grade.equals(student.getGrade())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        List<StudentActivityStatsDTO> statsList = new ArrayList<>();
        
        // 转换日期为LocalDateTime以便查询
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;
        
        for (Student student : students) {
            StudentActivityStatsDTO stats = new StudentActivityStatsDTO();
            stats.setStudentId(student.getStudentId());
            stats.setName(student.getName());
            stats.setMajor(student.getMajor());
            stats.setGrade(student.getGrade());
            
            // 查询学生的报名记录
            List<ActivityRegistration> allRegistrations = activityRegistrationRepository.findAll();
            List<ActivityRegistration> registrations = allRegistrations.stream()
                    .filter(reg -> student.getStudentId().equals(reg.getStudentId()))
                    .collect(Collectors.toList());
            
            // 过滤时间范围内的报名记录
            List<ActivityRegistration> filteredRegistrations = registrations.stream()
                    .filter(registration -> {
                        Activity activity = activityRepository.findById(registration.getActivityId()).orElse(null);
                        if (activity == null) {
                            return false;
                        }
                        if (startDateTime != null && activity.getStartTime().isBefore(startDateTime)) {
                            return false;
                        }
                        if (endDateTime != null && activity.getEndTime().isAfter(endDateTime)) {
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            
            stats.setTotalActivities(filteredRegistrations.size());
            
            // 统计已参加的活动数量
            long attendedCount = filteredRegistrations.stream()
                    .filter(reg -> "已参加".equals(reg.getStatus()) || reg.getAttended() == 1)
                    .count();
            stats.setAttendedActivities((int) attendedCount);
            
            // 计算总积分
            List<StudentPoints> allPoints = studentPointsRepository.findAll();
            Double totalPoints = allPoints.stream()
                    .filter(p -> student.getStudentId().equals(p.getStudentId()) && p.getPoints() != null)
                    .mapToDouble(StudentPoints::getPoints)
                    .sum();
            stats.setTotalPoints(totalPoints);
            
            statsList.add(stats);
        }
        
        return statsList;
    }
    
    @Override
    public List<ActivityParticipationStatsDTO> getActivityParticipationStatistics(LocalDate startDate, LocalDate endDate, Integer clubId) {
        // 查询符合条件的活动
        List<Activity> activities = clubId != null ? 
                activityRepository.findByClubId(clubId) : 
                activityRepository.findAll();
        
        // 转换日期为LocalDateTime以便查询
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;
        
        // 过滤时间范围内的活动
        List<Activity> filteredActivities = activities.stream()
                .filter(activity -> {
                    if (startDateTime != null && activity.getStartTime().isBefore(startDateTime)) {
                        return false;
                    }
                    if (endDateTime != null && activity.getEndTime().isAfter(endDateTime)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        List<ActivityParticipationStatsDTO> statsList = new ArrayList<>();
        
        // 获取社团名称映射
        Map<Integer, String> clubNameMap = new HashMap<>();
        List<Integer> clubIds = filteredActivities.stream()
                .map(Activity::getClubId)
                .distinct()
                .collect(Collectors.toList());
        
        List<Club> clubs = clubRepository.findAllById(clubIds);
        for (Club club : clubs) {
            clubNameMap.put(club.getClubId(), club.getName());
        }
        
        for (Activity activity : filteredActivities) {
            ActivityParticipationStatsDTO stats = new ActivityParticipationStatsDTO();
            stats.setActivityId(activity.getActivityId());
            stats.setTitle(activity.getTitle());
            stats.setClubName(clubNameMap.getOrDefault(activity.getClubId(), "未知社团"));
            stats.setMaxParticipants(activity.getMaxParticipants());
            stats.setRegisteredCount(activity.getRegisteredCount() != null ? activity.getRegisteredCount() : 0);
            
            // 计算参与率
            if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
                double rate = (double) stats.getRegisteredCount() / activity.getMaxParticipants() * 100;
                stats.setParticipationRate(Math.round(rate * 10) / 10.0); // 保留一位小数
            } else {
                stats.setParticipationRate(0.0);
            }
            
            statsList.add(stats);
        }
        
        return statsList;
    }
}

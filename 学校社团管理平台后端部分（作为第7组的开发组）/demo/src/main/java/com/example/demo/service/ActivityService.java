package com.example.demo.service;

import com.example.demo.entity.Activity;
import com.example.demo.entity.ActivityApplication;
import com.example.demo.entity.Student;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.ActivityApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ActivityApplicationRepository activityApplicationRepository;



    @Transactional(readOnly = true)
    public Activity getActivityDetail(Long activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
    }

    @Transactional
    public ActivityApplication registerForActivity(Long userId, Long activityId, String applicationReason, 
                                                  String specialRequirements, String emergencyContact, String emergencyPhone) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Activity activity = getActivityDetail(activityId);

        // 检查活动是否已开始或已结束
        if (LocalDateTime.now().isAfter(activity.getEndTime())) {
            throw new RuntimeException("Activity has already ended");
        }

        // 检查是否已经报名
        Optional<ActivityApplication> existingApplication = activityApplicationRepository.findByStudentAndActivity(student, activity);
        if (existingApplication.isPresent()) {
            throw new RuntimeException("Already registered for this activity");
        }

        // 创建活动报名
        ActivityApplication application = new ActivityApplication();
        application.setStudent(student);
        application.setActivity(activity);
        application.setApplicationReason(applicationReason);
        application.setSpecialRequirements(specialRequirements);
        application.setEmergencyContact(emergencyContact);
        application.setEmergencyPhone(emergencyPhone);
        application.setStatus(ActivityApplication.ApplicationStatus.PENDING);
        // 设置创建时间（虽然有@PrePersist，但为了明确性还是在这里设置）
        application.setAppliedAt(LocalDateTime.now());

        // 保存报名记录
        ActivityApplication savedApplication = activityApplicationRepository.save(application);
        
        // 更新活动当前参与人数
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityRepository.save(activity);
        
        return savedApplication;
    }

    @Transactional(readOnly = true)
    public List<ActivityApplication> getMyActivities(Long userId) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return activityApplicationRepository.findByStudent(student);
    }

    @Transactional
    public void cancelActivityRegistration(Long userId, Long activityId) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Activity activity = getActivityDetail(activityId);
        
        // 查找学生对该活动的报名记录
        ActivityApplication application = activityApplicationRepository.findByStudentAndActivity(student, activity)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        // 只能取消待审批或已批准的报名
        if (application.getStatus().equals(ActivityApplication.ApplicationStatus.REJECTED)) {
            throw new RuntimeException("Cannot cancel rejected registration");
        }

        // 删除报名记录
        activityApplicationRepository.delete(application);
        
        // 更新活动当前参与人数
        if (activity.getCurrentParticipants() > 0) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            activityRepository.save(activity);
        }
    }

    @Transactional(readOnly = true)
    public List<Activity> searchActivities(String keyword) {
        // 获取所有非取消状态的活动，然后在内存中过滤包含关键词的活动
        List<Activity> allActivities = activityRepository.findAll();
        return allActivities.stream()
                .filter(activity -> activity.getStatus() != Activity.ActivityStatus.CANCELLED)
                .filter(activity -> activity.getTitle().contains(keyword) || activity.getDescription().contains(keyword))
                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Activity> getUpcomingActivities() {
        // 获取所有已批准且开始时间在当前时间之后的活动
        List<Activity> allActivities = activityRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        return allActivities.stream()
                .filter(activity -> activity.getStatus() == Activity.ActivityStatus.APPROVED)
                .filter(activity -> activity.getStartTime().isAfter(now))
                .sorted((a1, a2) -> a1.getStartTime().compareTo(a2.getStartTime()))
                .toList();
    }
    
    // 更新默认状态值以匹配数据库定义
    @Transactional(readOnly = true)
    public List<Activity> getActivityCenterList(int page, int size) {
        // 返回审批通过的活动
        return activityRepository.findByStatus(Activity.ActivityStatus.APPROVED);
    }

    @Transactional(readOnly = true)
    public List<Activity> getPopularActivities() {
        // 获取所有已批准的活动，并按参与人数降序排序
        List<Activity> allActivities = activityRepository.findAll();
        return allActivities.stream()
                .filter(activity -> activity.getStatus() == Activity.ActivityStatus.APPROVED)
                .sorted((a1, a2) -> a2.getCurrentParticipants().compareTo(a1.getCurrentParticipants()))
                .toList();
    }
}

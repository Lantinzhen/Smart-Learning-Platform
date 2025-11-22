package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClubApplicationService clubApplicationService;

    @Autowired
    private ActivityApplicationService activityApplicationService;

    @Transactional(readOnly = true)
    public Student getStudentInfo(Long userId) {
        return studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Transactional
    public Map<String, Object> updateStudentInfo(Long userId, Map<String, Object> updateData) {
        Student student = getStudentInfo(userId);
        User user = student.getUser();
        Map<String, Object> responseData = new HashMap<>();
        List<String> updatedFields = new ArrayList<>();

        // 更新用户信息
        if (updateData.containsKey("email")) {
            user.setEmail((String) updateData.get("email"));
            updatedFields.add("email");
        }
        if (updateData.containsKey("phone")) {
            user.setPhone((String) updateData.get("phone"));
            updatedFields.add("phone");
        }

        // 更新学生信息
        if (updateData.containsKey("name")) {
            student.setName((String) updateData.get("name"));
            updatedFields.add("name");
        }
        if (updateData.containsKey("major")) {
            student.setMajor((String) updateData.get("major"));
            updatedFields.add("major");
        }
        if (updateData.containsKey("grade")) {
            student.setGrade((String) updateData.get("grade"));
            updatedFields.add("grade");
        }
        if (updateData.containsKey("className")) {
            student.setClassName((String) updateData.get("className"));
            updatedFields.add("className");
        }
        if (updateData.containsKey("avatarUrl")) {
            student.setAvatarUrl((String) updateData.get("avatarUrl"));
            updatedFields.add("avatarUrl");
        }
        // 添加对bio字段的处理
        if (updateData.containsKey("bio")) {
            student.setBio((String) updateData.get("bio"));
            updatedFields.add("bio");
        }
        // 添加对gender字段的处理
        if (updateData.containsKey("gender")) {
            student.setGender(Student.Gender.valueOf((String) updateData.get("gender")));
            updatedFields.add("gender");
        }
        // 添加对birthDate字段的处理
        if (updateData.containsKey("birthDate")) {
            try {
                // 假设传入的是日期字符串，需要转换为Date对象
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                student.setBirthDate(sdf.parse((String) updateData.get("birthDate")));
                updatedFields.add("birthDate");
            } catch (Exception e) {
                throw new RuntimeException("Invalid birth date format", e);
            }
        }

        userRepository.save(user);
        studentRepository.save(student);
        
        // 构建符合API文档的响应数据
        responseData.put("updatedFields", updatedFields);
        responseData.put("updatedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return responseData;
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // 更新新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardData(Long userId) {
        Student student = getStudentInfo(userId);
        Map<String, Object> dashboardData = new HashMap<>();

        // 获取已加入的社团数量
        long joinedClubsCount = clubApplicationService.countJoinedClubs(student);
        dashboardData.put("joinedClubsCount", joinedClubsCount);

        // 获取已参加的活动数量
        long participatedActivitiesCount = activityApplicationService.countParticipatedActivities(student);
        dashboardData.put("participatedActivitiesCount", participatedActivitiesCount);

        // 获取待审批的申请数量
        long pendingApplicationsCount = clubApplicationService.countPendingApplications(student) +
                activityApplicationService.countPendingApplications(student);
        dashboardData.put("pendingApplicationsCount", pendingApplicationsCount);

        return dashboardData;
    }
}

package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/dashboard")
    @Transactional
    public ResponseEntity<Map<String, Object>> getDashboardData(@AuthenticationPrincipal UserDetails userDetails) {
        // 从UserDetails中获取用户ID
        Long userId = Long.parseLong(userDetails.getUsername());
        Map<String, Object> dashboardData = studentService.getDashboardData(userId);
        return ResponseEntity.ok(dashboardData);
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    @Transactional
    public ResponseEntity<Student> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Student student = studentService.getStudentInfo(userId);
        return ResponseEntity.ok(student);
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    @Transactional
    public ResponseEntity<Map<String, Object>> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, Object> updateData) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Map<String, Object> responseData = studentService.updateStudentInfo(userId, updateData);
        
        // 构建符合API文档的全局响应结构
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("code", 200);
        result.put("message", "更新成功");
        result.put("data", responseData);
        result.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return ResponseEntity.ok(result);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails, 
                                           @RequestBody Map<String, String> passwordData) {
        Long userId = Long.parseLong(userDetails.getUsername());
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        studentService.changePassword(userId, oldPassword, newPassword);
        
        // 构建符合API文档的全局响应结构
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("code", 200);
        result.put("message", "密码修改成功");
        result.put("data", null);
        result.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return ResponseEntity.ok(result);
    }
}

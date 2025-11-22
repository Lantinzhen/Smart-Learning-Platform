package com.example.demo.controller;

import com.example.demo.entity.Activity;
import com.example.demo.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 获取活动列表
     */
    @GetMapping("")
    public ResponseEntity<List<Activity>> getActivityCenterList(@RequestParam(defaultValue = "0") int page, 
                                                             @RequestParam(defaultValue = "10") int size, 
                                                             @RequestParam(required = false) String search, 
                                                             @RequestParam(required = false) String type, 
                                                             @RequestParam(defaultValue = "startTime") String sort, 
                                                             @RequestParam(defaultValue = "asc") String order) {
        List<Activity> activities = activityService.getActivityCenterList(page, size);
        return ResponseEntity.ok(activities);
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{activityId}")
    public ResponseEntity<Activity> getActivityDetail(@PathVariable Long activityId) {
        Activity activity = activityService.getActivityDetail(activityId);
        return ResponseEntity.ok(activity);
    }

    /**
     * 报名参加活动
     */
    @PostMapping("/{activityId}/register")
    public ResponseEntity<?> registerForActivity(@AuthenticationPrincipal UserDetails userDetails, 
                                                @PathVariable Long activityId, 
                                                @RequestBody Map<String, String> registrationData) {
        Long userId = Long.parseLong(userDetails.getUsername());
        // 从请求体中提取所有需要的字段
        String applicationReason = registrationData.get("applicationReason");
        String specialRequirements = registrationData.get("specialRequirements");
        String emergencyContact = registrationData.get("emergencyContact");
        String emergencyPhone = registrationData.get("emergencyPhone");
        
        // 调用Service层方法，传递所有字段
        Object result = activityService.registerForActivity(userId, activityId, applicationReason, 
                                                           specialRequirements, emergencyContact, emergencyPhone);
        
        // 构建符合API文档的全局响应结构
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("code", 200);
        responseData.put("message", "活动报名成功");
        responseData.put("data", result);
        responseData.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return ResponseEntity.ok(responseData);
    }

    /**
     * 获取我的活动列表
     */
    @GetMapping("/student/activities")
    public ResponseEntity<?> getMyActivities(@AuthenticationPrincipal UserDetails userDetails, 
                                           @RequestParam(defaultValue = "0") int page, 
                                           @RequestParam(defaultValue = "10") int size, 
                                           @RequestParam(required = false) String status) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Object result = activityService.getMyActivities(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 取消活动报名
     */
    @DeleteMapping("/{activityId}/register")
    public ResponseEntity<?> cancelActivityRegistration(@AuthenticationPrincipal UserDetails userDetails, 
                                                      @PathVariable Long activityId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        activityService.cancelActivityRegistration(userId, activityId);
        
        // 构建符合API文档的全局响应结构
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("code", 200);
        result.put("message", "活动报名取消成功");
        result.put("data", null);
        result.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return ResponseEntity.ok(result);
    }

    /**
     * 搜索活动
     */
    @GetMapping("/search")
    public ResponseEntity<List<Activity>> searchActivities(@RequestParam String keyword) {
        List<Activity> activities = activityService.searchActivities(keyword);
        return ResponseEntity.ok(activities);
    }

    /**
     * 获取即将开始的活动
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<Activity>> getUpcomingActivities() {
        List<Activity> activities = activityService.getUpcomingActivities();
        return ResponseEntity.ok(activities);
    }

    /**
     * 获取热门活动
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Activity>> getPopularActivities() {
        List<Activity> activities = activityService.getPopularActivities();
        return ResponseEntity.ok(activities);
    }
}

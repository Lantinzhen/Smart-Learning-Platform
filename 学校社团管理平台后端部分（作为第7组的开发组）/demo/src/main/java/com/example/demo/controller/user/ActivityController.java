package com.example.demo.controller.user;

import com.example.demo.common.Response;
import com.example.demo.dto.user.activity.*;
import com.example.demo.entity.ActivityRegistration;
import com.example.demo.repository.ActivityRegistrationRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.user.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 活动管理控制器
 */
@RestController
@RequestMapping("/api/v1/student")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ActivityRegistrationRepository activityRegistrationRepository;

    /**
     * 获取学生所在社团的活动列表
     * @return 活动列表
     */
    @GetMapping("/activities")
    public Response<List<ActivityInfoDTO>> getActivities(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 解析Authorization头，获取token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
        } else if (authHeader != null) {
            // 处理可能没有Bearer前缀的情况
            token = authHeader;
        }
        
        // 调用服务层方法获取学生所在社团的活动列表
        List<ActivityInfoDTO> activities = activityService.getActivitiesByStudentToken(token);
        return Response.success(activities);
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/activities/{activityId}")
    public Response<ActivityDetailDTO> getActivityDetail(@PathVariable Integer activityId) {
        ActivityDetailDTO activityDetail = activityService.getActivityDetail(activityId);
        return Response.success(activityDetail);
    }

    /**
     * 报名参加活动
     */
    @PostMapping("/{activityId}/register")
    public Response<Map<String, Integer>> registerForActivity(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Integer activityId) {
        // 解析Authorization头，获取token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
        } else if (authHeader != null) {
            // 处理可能没有Bearer前缀的情况
            token = authHeader;
        }
        
        // 调用服务层进行报名
        activityService.registerForActivity(token, activityId);
        
        // 由于服务层不返回registration_id，我们需要手动查询最新创建的报名记录
        String studentId = jwtUtil.getUserIdFromToken(token);
        List<ActivityRegistration> registrations = activityRegistrationRepository.findByStudentId(studentId);
        // 按注册时间排序，获取最新的
        Optional<ActivityRegistration> latestRegistrationOpt = registrations.stream()
                .filter(reg -> reg.getActivityId().equals(activityId))
                .max(Comparator.comparing(ActivityRegistration::getRegistrationTime));
        
        // 构建响应数据
        Map<String, Integer> responseData = new HashMap<>();
        if (latestRegistrationOpt.isPresent()) {
            responseData.put("registration_id", latestRegistrationOpt.get().getRegistrationId());
        }
        
        return Response.success(responseData);
    }

    /**
     * 取消活动报名
     */
    @DeleteMapping("/{activityId}/cancel")
    public Response<Void> cancelRegistration(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Integer activityId) {
        // 解析Authorization头，获取token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
        } else if (authHeader != null) {
            // 处理可能没有Bearer前缀的情况
            token = authHeader;
        }
        
        activityService.cancelRegistration(token, activityId);
        return Response.success(null);
    }

    /**
     * 获取我报名的活动列表
     * @return 我的活动列表
     */
    @GetMapping("/my-activities")
    public Response<List<MyActivityDTO>> getMyActivities(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 解析Authorization头，获取token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
        } else if (authHeader != null) {
            // 处理可能没有Bearer前缀的情况
            token = authHeader;
        }
        
        // 调用服务层方法获取我报名的活动列表
        List<MyActivityDTO> activities = activityService.getMyActivities(token);
        return Response.success(activities);
    }
}
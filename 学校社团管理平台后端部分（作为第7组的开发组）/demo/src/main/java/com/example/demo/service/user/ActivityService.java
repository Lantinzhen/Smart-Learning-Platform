package com.example.demo.service.user;

import com.example.demo.dto.user.activity.*;

import java.util.List;

/**
 * 活动管理服务接口
 */
public interface ActivityService {
    
    /**
     * 根据学生token获取其所在社团的活动列表
     * @param token 学生JWT令牌
     * @return 学生所在社团的活动列表
     */
    List<ActivityInfoDTO> getActivitiesByStudentToken(String token);
    
    /**
     * 获取活动详情
     * @param activityId 活动ID
     * @return 活动详情
     */
    ActivityDetailDTO getActivityDetail(Integer activityId);
    
    /**
     * 报名参加活动
     * @param token JWT令牌
     * @param activityId 活动ID
     */
    void registerForActivity(String token, Integer activityId);
    
    /**
     * 取消活动报名
     * @param token JWT令牌
     * @param activityId 活动ID
     */
    void cancelRegistration(String token, Integer activityId);
    
    /**
     * 获取我报名的活动列表
     * @param token JWT令牌
     * @return 我的活动列表
     */
    List<MyActivityDTO> getMyActivities(String token);
}
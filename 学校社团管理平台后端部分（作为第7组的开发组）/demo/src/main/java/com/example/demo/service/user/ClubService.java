package com.example.demo.service.user;

import com.example.demo.dto.user.club.*;

import java.util.List;

/**
 * 社团管理服务接口
 */
public interface ClubService {
    
    
    /**
     * 获取社团列表
     * @return 社团列表
     */
    List<ClubInfoDTO> getClubs();

    
    /**
     * 获取社团详情
     * @param token JWT令牌
     * @param clubId 社团ID
     * @return 社团详情
     */
    ClubDetailDTO getClubDetail(String token, Integer clubId);
    
    /**
     * 申请加入社团
     * @param token JWT令牌
     * @param clubId 社团ID
     * @param application 申请信息
     * @return 申请ID
     */
    Integer applyToJoinClub(String token, Integer clubId, ClubApplicationDTO application);
    
    /**
     * 获取我的社团列表
     * @param token JWT令牌
     * @return 我的社团列表
     */
    List<MyClubDTO> getMyClubs(String token);
    
    /**
     * 退出社团
     * @param token JWT令牌
     * @param clubId 社团ID
     */
    void leaveClub(String token, Integer clubId);
}
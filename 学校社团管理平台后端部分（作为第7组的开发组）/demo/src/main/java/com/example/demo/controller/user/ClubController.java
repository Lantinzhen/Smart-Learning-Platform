package com.example.demo.controller.user;

import com.example.demo.common.Response;
import com.example.demo.dto.user.club.*;
import com.example.demo.service.user.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 社团管理控制器
 */
@RestController
@RequestMapping({"/api/v1/student"})
public class ClubController {

    @Autowired
    private ClubService clubService;
    /**
     * 获取社团列表
     */
    @GetMapping("/clubs")
    public Response<List<ClubInfoDTO>> getClubs() {
        List<ClubInfoDTO> clubs = clubService.getClubs();
        return Response.success(clubs);
    }

    /**
     * 获取社团详情
     */
    @GetMapping("/clubs/{clubId}")
    public Response<ClubDetailDTO> getClubDetail(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Integer clubId) {
        // 移除Bearer前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        ClubDetailDTO clubDetail = clubService.getClubDetail(token, clubId);
        return Response.success(clubDetail);
    }

    /**
     * 申请加入社团
     */
    @PostMapping("/clubs/{clubId}/applications")
    public Response<Map<String, Integer>> applyToJoinClub(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer clubId,
            @RequestBody ClubApplicationDTO application) {
        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 调用service方法，获取申请ID
        Integer applicationId = clubService.applyToJoinClub(token, clubId, application);
        
        // 返回成功的响应，包含申请ID
        Map<String, Integer> data = new HashMap<>();
        data.put("application_id", applicationId);
        return Response.success(data);
    }

    /**
     * 获取我的社团列表
     */
    @GetMapping("/my-clubs")
    public Response<List<MyClubDTO>> getMyClubs(@RequestHeader("Authorization") String token) {
        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        List<MyClubDTO> myClubs = clubService.getMyClubs(token);
        return Response.success(myClubs);
    }

    /**
     * 退出社团
     */
    @DeleteMapping("/clubs/{clubId}/leave")
    public Response<Void> leaveClub(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer clubId) {
        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        clubService.leaveClub(token, clubId);
        return Response.success(null);
    }
}
package com.example.demo.controller;

import com.example.demo.entity.Club;
import com.example.demo.entity.ClubApplication;
import com.example.demo.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/v1/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    /**
     * 获取社团广场列表
     */
    @GetMapping("")
    public ResponseEntity<List<Club>> getClubSquareList(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestParam(defaultValue = "0") int page, 
                                                      @RequestParam(defaultValue = "10") int size, 
                                                      @RequestParam(required = false) String search, 
                                                      @RequestParam(required = false) String type, 
                                                      @RequestParam(defaultValue = "name") String sort, 
                                                      @RequestParam(defaultValue = "asc") String order) {
        List<Club> clubs = clubService.getClubSquareList(page, size);
        return ResponseEntity.ok(clubs);
    }

    /**
     * 获取社团详情
     */
    @GetMapping("/{clubId}")
    public ResponseEntity<Club> getClubDetail(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long clubId) {
        Club club = clubService.getClubDetail(clubId);
        return ResponseEntity.ok(club);
    }

    /**
     * 申请加入社团
     */
    @PostMapping("/{clubId}/apply")
    public ResponseEntity<?> applyToJoinClub(@AuthenticationPrincipal UserDetails userDetails, 
                                           @PathVariable Long clubId, 
                                           @RequestBody Map<String, String> applicationData) {
        Long userId = Long.parseLong(userDetails.getUsername());
        String applicationReason = applicationData.get("applicationReason");
        String relevantExperience = applicationData.get("relevantExperience");
        String expectedActivityTypes = applicationData.get("expectedActivityTypes");
        String availableTime = applicationData.get("availableTime");
        String portfolioUrl = applicationData.get("portfolioUrl");
        
        // 调用service层方法，传递所有需要的字段
        ClubApplication application = clubService.applyToJoinClub(userId, clubId, applicationReason, relevantExperience, 
                                                                 expectedActivityTypes, availableTime, portfolioUrl);
        
        // 构建符合API文档的全局响应结构
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("code", 200);
        result.put("message", "申请提交成功");
        
        // 构建data部分
        Map<String, Object> data = new HashMap<>();
        data.put("applicationId", application.getId());
        data.put("status", application.getStatus().name());
        // 修复：将LocalDateTime转换为Date再格式化
        LocalDateTime appliedAt = application.getAppliedAt();
        Instant instant = appliedAt.atZone(ZoneId.systemDefault()).toInstant();
        Date appliedAtDate = Date.from(instant);
        data.put("appliedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(appliedAtDate));
        data.put("estimatedReviewTime", "3-5个工作日");
        
        result.put("data", data);
        result.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取我的社团列表
     */
    @GetMapping("/student/clubs")
    public ResponseEntity<List<ClubApplication>> getMyClubs(@AuthenticationPrincipal UserDetails userDetails, 
                                                          @RequestParam(defaultValue = "0") int page, 
                                                          @RequestParam(defaultValue = "10") int size, 
                                                          @RequestParam(required = false) String status) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<ClubApplication> applications = clubService.getMyClubs(userId);
        return ResponseEntity.ok(applications);
    }

    /**
     * 取消加入社团申请
     */
    @DeleteMapping("/{clubId}/apply")
    public ResponseEntity<?> cancelClubApplication(@AuthenticationPrincipal UserDetails userDetails, 
                                                 @PathVariable Long clubId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        // 调用service层方法并获取被取消的申请ID
        Long applicationId = clubService.cancelClubApplication(userId, clubId);
        
        // 构建符合API文档的全局响应结构
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("code", 200);
        result.put("message", "申请已取消");
        
        // 构建data部分
        Map<String, Object> data = new HashMap<>();
        data.put("applicationId", applicationId); // 返回实际的申请ID
        data.put("status", "CANCELLED");
        data.put("cancelledAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        result.put("data", data);
        result.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return ResponseEntity.ok(result);
    }

    /**
     * 搜索社团
     */
    @GetMapping("/search")
    public ResponseEntity<List<Club>> searchClubs(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestParam String keyword) {
        List<Club> clubs = clubService.searchClubs(keyword);
        return ResponseEntity.ok(clubs);
    }

    /**
     * 获取热门社团
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Club>> getPopularClubs(@AuthenticationPrincipal UserDetails userDetails) {
        List<Club> clubs = clubService.getPopularClubs();
        return ResponseEntity.ok(clubs);
    }
}

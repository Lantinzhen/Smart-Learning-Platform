package com.example.demo.controller.admin;

import com.example.demo.common.BusinessException;
import com.example.demo.common.Response;
import com.example.demo.dto.statistics.ActivityParticipationStatsDTO;
import com.example.demo.dto.statistics.ClubActivityStatsDTO;
import com.example.demo.dto.statistics.StudentActivityStatsDTO;
import com.example.demo.entity.SchoolAdmin;
import com.example.demo.repository.SchoolAdminRepository;
import com.example.demo.service.admin.StatisticsService;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学校管理员统计分析控制器
 */
@RestController
@RequestMapping("/api/v1/school-admin/statistics")
public class SchoolAdminStatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SchoolAdminRepository schoolAdminRepository;
    
    /**
     * 获取社团活跃度统计
     */
    @GetMapping("/club-activity")
    public Response<Map<String, Object>> getClubActivityStatistics(
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) Integer category_id) {
        
        // 验证学校管理员身份
        SchoolAdmin admin = validateSchoolAdmin();
        if (admin == null) {
            throw new BusinessException(401, "未授权访问");
        }
        
        // 转换日期参数
        LocalDate startDate = null;
        LocalDate endDate = null;
        try {
            if (start_date != null && !start_date.isEmpty()) {
                startDate = LocalDate.parse(start_date);
            }
            if (end_date != null && !end_date.isEmpty()) {
                endDate = LocalDate.parse(end_date);
            }
        } catch (Exception e) {
            throw new BusinessException(400, "日期格式错误，请使用YYYY-MM-DD格式");
        }
        
        // 获取统计数据
        List<ClubActivityStatsDTO> statsList = statisticsService.getClubActivityStatistics(startDate, endDate, category_id);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", statsList);
        
        return Response.success(data);
    }
    
    /**
     * 获取学生活动参与统计
     */
    @GetMapping("/student-activity")
    public Response<Map<String, Object>> getStudentActivityStatistics(
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String grade) {
        
        // 验证学校管理员身份
        SchoolAdmin admin = validateSchoolAdmin();
        if (admin == null) {
            throw new BusinessException(401, "未授权访问");
        }
        
        // 转换日期参数
        LocalDate startDate = null;
        LocalDate endDate = null;
        try {
            if (start_date != null && !start_date.isEmpty()) {
                startDate = LocalDate.parse(start_date);
            }
            if (end_date != null && !end_date.isEmpty()) {
                endDate = LocalDate.parse(end_date);
            }
        } catch (Exception e) {
            throw new BusinessException(400, "日期格式错误，请使用YYYY-MM-DD格式");
        }
        
        // 获取统计数据
        List<StudentActivityStatsDTO> statsList = statisticsService.getStudentActivityStatistics(startDate, endDate, major, grade);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", statsList);
        
        return Response.success(data);
    }
    
    /**
     * 获取活动参与率统计
     */
    @GetMapping("/activity-participation")
    public Response<Map<String, Object>> getActivityParticipationStatistics(
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) Integer club_id) {
        
        // 验证学校管理员身份
        SchoolAdmin admin = validateSchoolAdmin();
        if (admin == null) {
            throw new BusinessException(401, "未授权访问");
        }
        
        // 转换日期参数
        LocalDate startDate = null;
        LocalDate endDate = null;
        try {
            if (start_date != null && !start_date.isEmpty()) {
                startDate = LocalDate.parse(start_date);
            }
            if (end_date != null && !end_date.isEmpty()) {
                endDate = LocalDate.parse(end_date);
            }
        } catch (Exception e) {
            throw new BusinessException(400, "日期格式错误，请使用YYYY-MM-DD格式");
        }
        
        // 获取统计数据
        List<ActivityParticipationStatsDTO> statsList = statisticsService.getActivityParticipationStatistics(startDate, endDate, club_id);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", statsList);
        
        return Response.success(data);
    }
    
    /**
     * 验证学校管理员身份
     * @return 学校管理员对象，如果验证失败则返回null
     */
    private SchoolAdmin validateSchoolAdmin() {
        String token = getTokenFromHeader();
        if (token == null) {
            return null;
        }
        
        String adminId = jwtUtil.getUserIdFromToken(token);
        if (adminId == null) {
            return null;
        }
        
        SchoolAdmin admin = schoolAdminRepository.findById(adminId).orElse(null);
        if (admin == null || admin.getStatus() != 1) {
            return null;
        }
        
        return admin;
    }
    
    /**
     * 从请求头获取token
     * @return token字符串
     */
    private String getTokenFromHeader() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        
        HttpServletRequest request = attributes.getRequest();
        String authorization = request.getHeader("Authorization");
        if (authorization != null && !authorization.isEmpty()) {
            // 如果token带有Bearer前缀，则提取前缀后的部分
            if (authorization.startsWith("Bearer ")) {
                return authorization.substring(7);
            }
            // 否则直接返回整个Authorization头的值（假设这就是token）
            return authorization;
        }
        
        return null;
    }
}


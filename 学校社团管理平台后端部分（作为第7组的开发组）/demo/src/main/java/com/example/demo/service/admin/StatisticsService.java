package com.example.demo.service.admin;

import com.example.demo.dto.statistics.ActivityParticipationStatsDTO;
import com.example.demo.dto.statistics.ClubActivityStatsDTO;
import com.example.demo.dto.statistics.StudentActivityStatsDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取社团活跃度统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param categoryId 社团分类ID
     * @return 社团活跃度统计列表
     */
    List<ClubActivityStatsDTO> getClubActivityStatistics(LocalDate startDate, LocalDate endDate, Integer categoryId);
    
    /**
     * 获取学生活动参与统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param major 专业
     * @param grade 年级
     * @return 学生活动参与统计列表
     */
    List<StudentActivityStatsDTO> getStudentActivityStatistics(LocalDate startDate, LocalDate endDate, String major, String grade);
    
    /**
     * 获取活动参与率统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param clubId 社团ID
     * @return 活动参与率统计列表
     */
    List<ActivityParticipationStatsDTO> getActivityParticipationStatistics(LocalDate startDate, LocalDate endDate, Integer clubId);
}

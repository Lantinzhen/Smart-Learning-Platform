package com.example.demo.repository;

import com.example.demo.entity.ActivityRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 活动报名Repository接口
 */
@Repository
public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Integer> {
    /**
     * 根据学生ID查询活动报名记录
     */
    List<ActivityRegistration> findByStudentId(String studentId);

    /**
     * 根据活动ID和学生ID查询活动报名记录
     */
    ActivityRegistration findByActivityIdAndStudentId(Integer activityId, String studentId);

    /**
     * 根据学生ID和状态查询活动报名记录
     */
    List<ActivityRegistration> findByStudentIdAndStatus(String studentId, String status);

    /**
     * 根据学生ID查询参与的活动数量
     */
    @Query("SELECT COUNT(ar) FROM ActivityRegistration ar WHERE ar.studentId = ?1 AND ar.status = '已参加'")
    Integer countParticipatedByStudentId(String studentId);
    
    /**
     * 根据学生ID查询最近的活动报名记录
     */
    @Query("SELECT ar FROM ActivityRegistration ar WHERE ar.studentId = ?1 ORDER BY ar.registrationTime DESC")
    List<ActivityRegistration> findLatestByStudentId(String studentId, org.springframework.data.domain.Pageable pageable);
    
    /**
     * 根据活动ID统计报名人数
     */
    @Query("SELECT COUNT(ar) FROM ActivityRegistration ar WHERE ar.activityId = ?1")
    Integer countByActivityId(Integer activityId);
}
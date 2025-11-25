package com.example.demo.repository;

import com.example.demo.entity.StudentPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学生活动积分数据访问接口
 */
@Repository
public interface StudentPointsRepository extends JpaRepository<StudentPoints, Integer> {
    
    /**
     * 根据学生ID查询积分记录
     * @param studentId 学生ID
     * @return 积分记录列表
     */
    List<StudentPoints> findByStudentId(String studentId);
    
    /**
     * 计算学生总积分
     * @param studentId 学生ID
     * @return 总积分
     */
    @Query("SELECT COALESCE(SUM(sp.points), 0) FROM StudentPoints sp WHERE sp.studentId = :studentId")
    Double sumPointsByStudentId(@Param("studentId") String studentId);
    
    /**
     * 获取所有学生的积分总和，按学生ID分组并排序
     * @return 学生积分列表
     */
    @Query("SELECT sp.studentId, COALESCE(SUM(sp.points), 0) as totalPoints FROM StudentPoints sp GROUP BY sp.studentId ORDER BY totalPoints DESC")
    List<Object[]> findAllStudentsTotalPoints();
    
    /**
     * 计算指定学生在所有学生中的排名
     * @param studentId 学生ID
     * @return 排名
     */
    @Query(value = "SELECT COUNT(*) + 1 FROM (SELECT student_id, COALESCE(SUM(points), 0) as total_points FROM student_points GROUP BY student_id ORDER BY total_points DESC) as ranked_students WHERE total_points > (SELECT COALESCE(SUM(points), 0) FROM student_points WHERE student_id = ?1)", nativeQuery = true)
    Integer findStudentRanking(String studentId);
    
    /**
     * 计算学生在特定社团的积分总和
     * @param studentId 学生ID
     * @param clubId 社团ID
     * @return 积分总和
     */
    @Query("SELECT COALESCE(SUM(sp.points), 0) FROM StudentPoints sp JOIN Activity a ON sp.activityId = a.activityId WHERE sp.studentId = :studentId AND a.clubId = :clubId")
    Double sumPointsByStudentIdAndClubId(@Param("studentId") String studentId, @Param("clubId") Integer clubId);
}
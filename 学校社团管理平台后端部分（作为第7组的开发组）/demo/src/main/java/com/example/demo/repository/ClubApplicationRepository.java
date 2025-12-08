package com.example.demo.repository;

import com.example.demo.entity.ApplicationStatus;
import com.example.demo.entity.ClubApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 社团申请Repository接口
 */
@Repository
public interface ClubApplicationRepository extends JpaRepository<ClubApplication, Integer> {

    /**
     * 根据学生ID查询申请记录
     */
    List<ClubApplication> findByStudentId(String studentId);

    /**
     * 根据社团ID查询申请记录
     */
    List<ClubApplication> findByClubId(Integer clubId);

    /**
     * 根据学生ID和社团ID查询申请记录
     */
    ClubApplication findByStudentIdAndClubId(String studentId, Integer clubId);

    /**
     * 根据社团ID和状态查询申请记录
     */
    List<ClubApplication> findByClubIdAndStatus(Integer clubId, String status);
    
    /**
     * 根据社团ID和状态统计申请数量
     */
    long countByClubIdAndStatus(Integer clubId, ApplicationStatus status);
}
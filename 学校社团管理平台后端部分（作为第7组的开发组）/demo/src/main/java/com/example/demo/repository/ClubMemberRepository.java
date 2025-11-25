package com.example.demo.repository;

import com.example.demo.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 社团成员Repository接口
 */
@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, Integer> {
    /**
     * 根据学生ID查询社团成员关系
     */
    List<ClubMember> findByStudentId(String studentId);

    /**
     * 根据社团ID和学生ID查询社团成员关系
     */
    ClubMember findByClubIdAndStudentId(Integer clubId, String studentId);

    /**
     * 根据学生ID查询加入的社团数量
     */
    @Query("SELECT COUNT(cm) FROM ClubMember cm WHERE cm.studentId = ?1 AND cm.status = 1")
    Integer countByStudentId(String studentId);
    
    /**
     * 根据社团ID查询所有社团成员关系
     */
    List<ClubMember> findByClubId(Integer clubId);

    /**
     * 根据社团ID和角色查询成员列表
     */
    List<ClubMember> findByClubIdAndRole(Integer clubId, String role);
    
    /**
     * 根据社团ID和学生姓名关键词查询成员列表
     */
    @Query("SELECT cm FROM ClubMember cm JOIN Student s ON cm.studentId = s.studentId WHERE cm.clubId = :clubId AND s.name LIKE CONCAT('%', :keyword, '%')")
    List<ClubMember> findByClubIdAndStudentNameContaining(@Param("clubId") Integer clubId, @Param("keyword") String keyword);
    
    /**
     * 根据社团ID统计成员数量
     */
    long countByClubId(Integer clubId);
}
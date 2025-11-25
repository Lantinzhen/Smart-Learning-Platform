package com.example.demo.repository;

import com.example.demo.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 学生Repository接口
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    /**
     * 根据条件查询学生列表
     */
    @Query("SELECT s FROM Student s WHERE (?1 IS NULL OR s.studentId LIKE %?1% OR s.name LIKE %?1% OR s.email LIKE %?1%) AND (?2 IS NULL OR s.major = ?2) AND (?3 IS NULL OR s.status = ?3)")
    Page<Student> findStudentsByConditions(String keyword, String major, Integer status, Pageable pageable);
    /**
     * 根据学号查询学生
     */
    Student findByStudentId(String studentId);

    /**
     * 检查学号是否存在
     */
    boolean existsByStudentId(String studentId);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}
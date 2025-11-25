package com.example.demo.repository;

import com.example.demo.entity.SchoolAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 学校管理员Repository接口
 */
@Repository
public interface SchoolAdminRepository extends JpaRepository<SchoolAdmin, String> {
    /**
     * 根据用户名查询学校管理员
     */
    SchoolAdmin findByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}
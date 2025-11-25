package com.example.demo.repository;

import com.example.demo.entity.ClubAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * 社团管理员Repository接口
 */
@Repository
public interface ClubAdminRepository extends JpaRepository<ClubAdmin, String> {
    /**
     * 根据条件查询社团管理员列表
     */
    @Query("SELECT c FROM ClubAdmin c WHERE (?1 IS NULL OR c.adminId LIKE %?1% OR c.name LIKE %?1% OR c.username LIKE %?1% OR c.email LIKE %?1%) AND (?2 IS NULL OR c.clubId = ?2) AND (?3 IS NULL OR c.status = ?3)")
    Page<ClubAdmin> findClubAdminsByConditions(String keyword, Integer clubId, Integer status, Pageable pageable);
    /**
     * 根据用户名查询社团管理员
     */
    ClubAdmin findByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}
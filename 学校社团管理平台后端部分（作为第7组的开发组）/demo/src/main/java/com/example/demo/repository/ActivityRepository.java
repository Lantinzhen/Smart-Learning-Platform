package com.example.demo.repository;

import com.example.demo.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 活动Repository接口
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    
    /**
     * 根据状态查询活动列表
     */
    Page<Activity> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据分类ID查询活动列表
     */
    List<Activity> findByCategoryId(Integer categoryId);

    /**
     * 根据标题模糊查询活动列表
     */
    List<Activity> findByTitleContaining(String keyword);

    /**
     * 根据社团ID查询活动列表
     */
    List<Activity> findByClubId(Integer clubId);
    
    /**
     * 根据社团ID统计活动数量
     */
    long countByClubId(Integer clubId);
}
package com.example.demo.repository;

import com.example.demo.entity.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 活动分类数据访问接口
 */
@Repository
public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {
    
    /**
     * 根据名称查找活动分类
     */
    ActivityCategory findByName(String name);
    
    /**
     * 查找所有分类并按创建时间排序
     */
    List<ActivityCategory> findAllByOrderByCreatedAtAsc();
    
    /**
     * 检查分类名称是否已存在（排除指定ID）
     */
    boolean existsByNameAndCategoryIdNot(String name, Integer categoryId);
}
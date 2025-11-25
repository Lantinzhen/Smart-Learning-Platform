package com.example.demo.repository;

import com.example.demo.entity.ClubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 社团分类数据访问接口
 */
@Repository
public interface ClubCategoryRepository extends JpaRepository<ClubCategory, Integer> {
}
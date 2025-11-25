package com.example.demo.repository;

import com.example.demo.entity.Club;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 社团Repository接口
 */
@Repository
public interface ClubRepository extends JpaRepository<Club, Integer> {
    /**
     * 根据分类ID查询社团列表
     */
    List<Club> findByCategoryId(Integer categoryId);

    /**
     * 根据分类ID查询社团数量
     */
    long countByCategoryId(Integer categoryId);

    /**
     * 根据分类ID分页查询社团列表，按创建时间倒序
     */
    @Query("SELECT c FROM Club c WHERE c.categoryId = :categoryId ORDER BY c.foundationDate DESC")
    List<Club> findByCategoryIdOrderByFoundationDateDesc(@Param("categoryId") Integer categoryId, Pageable pageable);

    /**
     * 分页查询所有社团列表，按创建时间倒序
     */
    @Query("SELECT c FROM Club c ORDER BY c.foundationDate DESC")
    List<Club> findAllOrderByFoundationDateDesc(Pageable pageable);

    /**
     * 根据名称模糊查询社团列表
     */
    List<Club> findByNameContaining(String keyword);
    
    /**
     * 根据名称或描述中包含关键词搜索社团
     */
    List<Club> findByNameContainingOrDescriptionContaining(String nameKeyword, String descriptionKeyword);
}
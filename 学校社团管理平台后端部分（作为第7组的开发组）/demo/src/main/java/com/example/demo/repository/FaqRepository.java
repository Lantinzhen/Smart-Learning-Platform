package com.example.demo.repository;

import com.example.demo.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 常见问题Repository接口
 */
@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer> {
    
    /**
     * 查询所有已启用的常见问题
     */
    List<Faq> findByStatus(Integer status);
    
    /**
     * 根据分类查询已启用的常见问题
     */
    List<Faq> findByCategoryAndStatus(String category, Integer status);
}

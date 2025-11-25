package com.example.demo.repository;

import com.example.demo.entity.HelpDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帮助文档Repository接口
 */
@Repository
public interface HelpDocumentRepository extends JpaRepository<HelpDocument, Integer> {
    
    /**
     * 查询所有已发布的帮助文档
     */
    List<HelpDocument> findByStatus(Integer status);
    
    /**
     * 根据分类查询已发布的帮助文档
     */
    List<HelpDocument> findByCategoryAndStatus(String category, Integer status);
}

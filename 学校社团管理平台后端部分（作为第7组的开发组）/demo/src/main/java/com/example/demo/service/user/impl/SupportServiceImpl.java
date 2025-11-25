package com.example.demo.service.user.impl;

import com.example.demo.dto.user.support.HelpDocumentDTO;
import com.example.demo.dto.user.support.HelpDocumentDetailDTO;
import com.example.demo.dto.user.support.FaqDTO;
import com.example.demo.entity.HelpDocument;
import com.example.demo.entity.Faq;
import com.example.demo.repository.HelpDocumentRepository;
import com.example.demo.repository.FaqRepository;
import com.example.demo.service.user.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 帮助与支持服务实现类
 */
@Service
public class SupportServiceImpl implements SupportService {
    
    @Autowired
    private HelpDocumentRepository helpDocumentRepository;
    
    @Autowired
    private FaqRepository faqRepository;
    
    /**
     * 获取帮助文档列表
     * @return 帮助文档列表
     */
    @Override
    public List<HelpDocumentDTO> getHelpDocuments() {
        // 从数据库查询已发布的帮助文档（状态为1）
        List<HelpDocument> documents = helpDocumentRepository.findByStatus(1);
        
        // 将实体类转换为DTO
        return documents.stream().map(document -> {
            HelpDocumentDTO dto = new HelpDocumentDTO();
            dto.setDocumentId(document.getDocumentId());
            dto.setTitle(document.getTitle());
            // 摘要可以取内容的前100个字符
            String content = document.getContent();
            dto.setSummary(content.length() > 100 ? content.substring(0, 100) + "..." : content);
            dto.setCategory(document.getCategory());
            dto.setCreatedAt(document.getCreatedAt().toString());
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取帮助文档详情
     * @param documentId 文档ID
     * @return 帮助文档详情
     */
    @Override
    public HelpDocumentDetailDTO getHelpDocumentDetail(Integer documentId) {
        // 从数据库根据ID查询帮助文档
        Optional<HelpDocument> optionalDocument = helpDocumentRepository.findById(documentId);
        
        // 如果存在且状态为已发布，则转换为DTO返回
        return optionalDocument.filter(doc -> doc.getStatus() == 1).map(document -> {
            HelpDocumentDetailDTO dto = new HelpDocumentDetailDTO();
            dto.setDocumentId(document.getDocumentId());
            dto.setTitle(document.getTitle());
            dto.setContent(document.getContent());
            dto.setCategory(document.getCategory());
            dto.setCreatedAt(document.getCreatedAt().toString());
            dto.setUpdatedAt(document.getUpdatedAt().toString());
            return dto;
        }).orElse(null);
    }
    
    /**
     * 获取常见问题列表
     * @return 常见问题列表
     */
    @Override
    public List<FaqDTO> getFaqs() {
        // 从数据库查询已启用的常见问题（状态为1）
        List<Faq> faqs = faqRepository.findByStatus(1);
        
        // 将实体类转换为DTO
        return faqs.stream().map(faq -> {
            FaqDTO dto = new FaqDTO();
            dto.setFaqId(faq.getFaqId());
            dto.setQuestion(faq.getQuestion());
            dto.setAnswer(faq.getAnswer());
            dto.setCategory(faq.getCategory());
            return dto;
        }).collect(Collectors.toList());
    }
    

}
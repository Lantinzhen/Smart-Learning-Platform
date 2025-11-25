package com.example.demo.service.user;

import com.example.demo.dto.user.support.*;

import java.util.List;

/**
 * 帮助与支持服务接口
 */
public interface SupportService {
    
    /**
     * 获取帮助文档列表
     * @return 帮助文档列表
     */
    List<HelpDocumentDTO> getHelpDocuments();
    
    /**
     * 获取帮助文档详情
     * @param documentId 文档ID
     * @return 帮助文档详情
     */
    HelpDocumentDetailDTO getHelpDocumentDetail(Integer documentId);
    
    /**
     * 获取常见问题列表
     * @return 常见问题列表
     */
    List<FaqDTO> getFaqs();
    
}
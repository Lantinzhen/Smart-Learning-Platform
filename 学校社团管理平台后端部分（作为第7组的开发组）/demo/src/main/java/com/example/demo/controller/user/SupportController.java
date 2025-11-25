package com.example.demo.controller.user;

import com.example.demo.common.Response;
import com.example.demo.dto.user.support.*;
import com.example.demo.service.user.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 帮助与支持控制器
 */
@RestController
@RequestMapping("/api/v1/student")
public class SupportController {

    @Autowired
    private SupportService supportService;

    /**
     * 获取帮助文档列表
     */
    @GetMapping("/documents")
    public Response<List<HelpDocumentDTO>> getHelpDocuments() {
        List<HelpDocumentDTO> documents = supportService.getHelpDocuments();
        return Response.success(documents);
    }

    /**
     * 获取帮助文档详情
     */
    @GetMapping("/documents/{documentId}")
    public Response<HelpDocumentDetailDTO> getHelpDocumentDetail(@PathVariable Integer documentId) {
        HelpDocumentDetailDTO document = supportService.getHelpDocumentDetail(documentId);
        return Response.success(document);
    }

    /**
     * 获取常见问题列表
     */
    @GetMapping("/faqs")
    public Response<List<FaqDTO>> getFaqs() {
        List<FaqDTO> faqs = supportService.getFaqs();
        return Response.success(faqs);
    }


}
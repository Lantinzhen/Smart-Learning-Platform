package com.example.demo.controller;

import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取管理概览
     */
    @GetMapping("/overview")
    public ResponseEntity<?> getAdminOverview(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam String adminType) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Object overviewData = adminService.getAdminOverview(userId, adminType);
        return ResponseEntity.ok(overviewData);
    }

    // 其他管理员接口将在后续实现
}

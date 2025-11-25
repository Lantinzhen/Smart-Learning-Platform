package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动审批实体类
 */
@Entity
@Table(name = "activity_approvals")
@Data
public class ActivityApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer approvalId;

    @Column(name = "activity_id", nullable = false)
    private Integer activityId;

    @Column(name = "approver_id", nullable = false)
    private String approverId;

    @Column(name = "status", nullable = false, columnDefinition = "enum('待审批','已批准','已拒绝')")
    private String status;

    @Column(name = "comments")
    private String comments;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

package com.example.demo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;
import com.example.demo.entity.ClubAdmin;

@Data
@Entity
@Table(name = "activity_applications")
public class ActivityApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_reason", columnDefinition = "TEXT")
    private String applicationReason;

    @Column(name = "special_requirements", columnDefinition = "TEXT")
    private String specialRequirements;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "processed_note", columnDefinition = "TEXT")
    private String processedNote;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "attended_at")
    private LocalDateTime attendedAt;

    @Column(name = "points_earned", columnDefinition = "INT DEFAULT 0")
    private Integer pointsEarned = 0;

    // 外键关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    @JsonIgnore
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    @JsonIgnore
    private ClubAdmin processedBy;

    // 枚举定义
    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED, CANCELLED, ATTENDED, NO_SHOW
    }

    // 生命周期回调
    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }
}
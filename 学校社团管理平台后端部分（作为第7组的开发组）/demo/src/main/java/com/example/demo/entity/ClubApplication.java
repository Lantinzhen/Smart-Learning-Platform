package com.example.demo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;
import com.example.demo.entity.ClubAdmin;

@Data
@Entity
@Table(name = "club_applications")
public class ClubApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_reason", columnDefinition = "TEXT", nullable = false)
    private String applicationReason;

    @Column(name = "relevant_experience", columnDefinition = "TEXT")
    private String relevantExperience;

    @Column(name = "expected_activity_types", columnDefinition = "TEXT")
    private String expectedActivityTypes;

    @Column(name = "available_time", columnDefinition = "TEXT")
    private String availableTime;

    @Column(name = "portfolio_url", length = 255)
    private String portfolioUrl;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "processed_note", columnDefinition = "TEXT")
    private String processedNote;

    // 外键关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    @JsonIgnoreProperties({"activities", "clubAdminAssignments", "studentClubMemberships", "clubApplications"})
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"user"})
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    @JsonIgnoreProperties({"clubAdminAssignments"})
    private ClubAdmin processedBy;

    // 枚举定义
    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    // 生命周期回调
    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }
}
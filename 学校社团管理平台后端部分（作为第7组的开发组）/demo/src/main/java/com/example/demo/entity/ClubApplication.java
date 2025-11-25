package com.example.demo.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * 社团申请表实体类
 */
@Data
@Entity
@Table(name = "club_applications")
public class ClubApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "club_id", nullable = false)
    private Integer clubId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "major")
    private String major;

    @Column(name = "grade")
    private String grade;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "experience", columnDefinition = "TEXT")
    private String experience;

    @Column(name = "activity_preference", columnDefinition = "TEXT")
    private String activityPreference;

    @Column(name = "available_time", columnDefinition = "TEXT")
    private String availableTime;

    @Column(name = "portfolio")
    private String portfolio;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private Timestamp reviewedAt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    // 关联关系
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "club_id", referencedColumnName = "club_id", insertable = false, updatable = false)
    private Club club;

    @ManyToOne
    @JoinColumn(name = "reviewed_by", referencedColumnName = "admin_id", insertable = false, updatable = false)
    private ClubAdmin reviewedByAdmin;
}
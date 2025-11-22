package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_activity_scores")
public class UserActivityScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "score_type", nullable = false)
    private ScoreType scoreType;

    // 外键关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // 枚举定义
    public enum ScoreType {
        ACTIVITY_PARTICIPATION, ACTIVITY_WINNING, CLUB_CONTRIBUTION, SYSTEM_REWARD, MANUAL_ADJUST
    }

    // 生命周期回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
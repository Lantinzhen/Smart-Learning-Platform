package com.example.demo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "club_admin_assignments")
public class ClubAdminAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ClubAdminRole role;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    // 外键关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    @JsonIgnoreProperties({"activities", "clubAdminAssignments", "studentClubMemberships", "clubApplications"})
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_admin_id", nullable = false)
    @JsonIgnoreProperties({"clubAdminAssignments", "user"})
    private ClubAdmin clubAdmin;

    // 枚举定义
    public enum ClubAdminRole {
        PRESIDENT, VICE_PRESIDENT, SECRETARY, TREASURER, MEMBER
    }

    // 生命周期回调
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
}
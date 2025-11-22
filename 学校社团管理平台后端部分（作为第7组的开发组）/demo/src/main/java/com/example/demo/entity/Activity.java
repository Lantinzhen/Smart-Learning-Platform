package com.example.demo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType type;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "max_participants")
    private Integer maxParticipants = 100;

    @Column(name = "current_participant_count")
    private Integer currentParticipants = 0;

    @Column(name = "required_points", nullable = false)
    private Integer requiredPoints = 0;

    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints = 0;

    @Column(name = "registration_fee", precision = 10, scale = 2)
    private BigDecimal registrationFee = BigDecimal.ZERO;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "materials_needed", columnDefinition = "TEXT")
    private String materialsNeeded;

    @Column(name = "contact_info", length = 200)
    private String contactInfo;

    @Column(name = "poster_url", length = 255)
    private String posterUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActivityStatus status = ActivityStatus.DRAFT;

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // 外键关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    @JsonIgnore
    private Club club;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ActivityApplication> activityApplications;

    // 枚举定义
    public enum ActivityType {
        MEETING, COMPETITION, WORKSHOP, EXCURSION, SOCIAL, OTHERS
    }

    public enum ActivityStatus {
        DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, CANCELLED, COMPLETED
    }
}
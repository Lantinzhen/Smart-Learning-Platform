package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生活动积分实体类
 */
@Entity
@Table(name = "student_points")
@Data
public class StudentPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Integer pointId;

    @Column(name = "student_id", length = 20, nullable = false)
    private String studentId;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "points")
    private Double points;

    @Column(name = "reason")
    private String reason;

    @Column(name = "awarded_at")
    private LocalDateTime awardedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
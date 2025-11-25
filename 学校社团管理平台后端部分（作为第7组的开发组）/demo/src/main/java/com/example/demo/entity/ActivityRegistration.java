package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名实体类
 */
@Entity
@Table(name = "activity_registrations")
@Data
public class ActivityRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer registrationId;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "student_id", length = 20, nullable = false)
    private String studentId;

    @Column(name = "registration_time")
    private LocalDateTime registrationTime;

    @Column(name = "status")
    private String status;

    @Column(name = "attended")
    private Integer attended;

    @Column(name = "points_earned")
    private Double pointsEarned;
}
package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 社团成员实体类
 */
@Entity
@Table(name = "club_members")
@Data
public class ClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "club_id")
    private Integer clubId;

    @Column(name = "student_id", length = 20, nullable = false)
    private String studentId;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "role")
    private String role;

    @Column(name = "status")
    private Integer status;
}
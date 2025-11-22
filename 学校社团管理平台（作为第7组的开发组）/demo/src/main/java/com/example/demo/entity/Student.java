package com.example.demo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true, length = 20)
    private String studentId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "grade", length = 20)
    private String grade;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    // 外键关联用户表
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"password", "roles", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private User user;

    // 枚举定义
    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
package com.example.demo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@Entity
@Table(name = "club_admins")
public class ClubAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "position", length = 50)
    private String position;

    @Column(name = "department", length = 100)
    private String department;

    // 外键关联用户表
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"password", "roles", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private User user;

    // 外键关系
    @OneToMany(mappedBy = "clubAdmin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"clubAdmin", "club"})
    private java.util.List<ClubAdminAssignment> clubAdminAssignments;
}
package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "clubs")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "declaration", columnDefinition = "TEXT")
    private String declaration;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ClubType type;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "banner_url", length = 255)
    private String bannerUrl;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    @Column(name = "current_member_count")
    private Integer memberCount = 0;

    @Column(name = "max_members")
    private Integer maxMembers = 100;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "location", length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClubStatus status = ClubStatus.ACTIVE;

    // 外键关系
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ClubAdminAssignment> clubAdminAssignments;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<StudentClubMembership> studentClubMemberships;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ClubApplication> clubApplications;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Activity> activities;

    // 枚举定义
    public enum ClubType {
        TECHNOLOGY, ARTS, SPORTS, LITERATURE, VOLUNTEER, ACADEMIC, OTHER
    }

    public enum ClubStatus {
        ACTIVE, INACTIVE, AUDITING, CLOSED
    }
}
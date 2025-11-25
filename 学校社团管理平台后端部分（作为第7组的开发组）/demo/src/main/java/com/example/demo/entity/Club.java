package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 社团实体类
 */
@Entity
@Table(name = "clubs")
@Data
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Integer clubId;

    @Column(name = "category_id")
    private Integer categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private ClubCategory category;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "president_student_id", length = 20)
    private String presidentStudentId;

    @Column(name = "foundation_date")
    private LocalDate foundationDate;

    @Column(name = "member_count")
    private Integer memberCount;
    
    @Column(name = "max_members")
    private Integer maxMembers;
    
    @Column(name = "contact_info")
    private String contactInfo;
    
    @Column(name = "meeting_time")
    private String meetingTime;
    
    @Column(name = "meeting_location")
    private String meetingLocation;
    
    @Column(name = "status")
    private Integer status;
}
package com.example.demo.dto.admin;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClubMemberDTO {
    private Integer id;
    private String studentId;
    private String name;
    private String major;
    private String grade;
    private String role;
    private LocalDate joinDate;
    private Integer status;
}
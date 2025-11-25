package com.example.demo.dto.admin.club;

import lombok.Data;
import java.time.LocalDate;

/**
 * 社团创建/更新请求DTO
 */
@Data
public class ClubRequestDTO {
    private Integer club_id;
    private Integer category_id;
    private String name;
    private String description;
    private String logo_url;
    private String president_student_id;
    private LocalDate foundation_date;
    private Integer max_members;
    private String contact_info;
    private String meeting_time;
    private String meeting_location;
}
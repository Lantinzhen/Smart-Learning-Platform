package com.example.demo.dto.admin.club;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 社团列表响应DTO
 */
@Data
public class ClubListResponseDTO {
    private List<ClubItemDTO> list;
    private long total;
    private int page;
    private int page_size;
    
    /**
     * 社团列表项DTO
     */
    @Data
    public static class ClubItemDTO {
        private Integer club_id;
        private Integer category_id;
        private String category_name;
        private String name;
        private String president_student_id;
        private String president_name;
        private Integer member_count;
        private LocalDate foundation_date;
        private Integer status;
    }
}
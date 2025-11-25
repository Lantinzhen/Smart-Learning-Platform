package com.example.demo.dto.admin.user;

import lombok.Data;
import java.util.List;

/**
 * 学生列表响应DTO
 */
@Data
public class StudentListResponseDTO {
    private List<StudentInfoDTO> list;
    private Long total;
    private Integer page;
    private Integer page_size;
    
    /**
     * 学生信息DTO
     */
    @Data
    public static class StudentInfoDTO {
        private String student_id;
        private String name;
        private String email;
        private String phone;
        private String major;
        private String grade;
        private Integer status;
    }
}

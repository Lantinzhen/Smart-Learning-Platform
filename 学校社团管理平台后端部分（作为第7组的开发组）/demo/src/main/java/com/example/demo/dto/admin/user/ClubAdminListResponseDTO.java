package com.example.demo.dto.admin.user;

import lombok.Data;
import java.util.List;

/**
 * 社团管理员列表响应DTO
 */
@Data
public class ClubAdminListResponseDTO {
    private List<ClubAdminInfoDTO> list;
    private Long total;
    private Integer page;
    private Integer page_size;
    
    /**
     * 社团管理员信息DTO
     */
    @Data
    public static class ClubAdminInfoDTO {
        private String admin_id;
        private Integer club_id;
        private String club_name;
        private String name;
        private String username;
        private String email;
        private String phone;
        private Integer status;
    }
}

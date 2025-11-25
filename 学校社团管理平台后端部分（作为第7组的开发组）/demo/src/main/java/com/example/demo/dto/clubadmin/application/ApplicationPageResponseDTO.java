package com.example.demo.dto.clubadmin.application;

import lombok.Data;
import java.util.List;

/**
 * 申请列表分页响应DTO
 */
@Data
public class ApplicationPageResponseDTO {
    private List<ClubApplicationListDTO> list;
    private int total;
    private int page;
    private int pageSize;
}

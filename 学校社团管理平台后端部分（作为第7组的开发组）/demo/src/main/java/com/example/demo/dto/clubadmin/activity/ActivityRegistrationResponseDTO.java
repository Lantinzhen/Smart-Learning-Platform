package com.example.demo.dto.clubadmin.activity;

import lombok.Data;
import java.util.List;

/**
 * 活动报名列表响应DTO
 */
@Data
public class ActivityRegistrationResponseDTO {
    private List<ActivityRegistrationDTO> list;
    private int total;
    private int page;
    private int pageSize;
}
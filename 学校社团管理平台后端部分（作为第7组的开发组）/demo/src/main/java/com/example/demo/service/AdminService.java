package com.example.demo.service;

import com.example.demo.entity.Club;
import com.example.demo.entity.ClubAdmin;
import com.example.demo.entity.SchoolAdmin;
import com.example.demo.entity.User;
import com.example.demo.repository.ClubAdminRepository;
import com.example.demo.repository.SchoolAdminRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private ClubAdminRepository clubAdminRepository;

    @Autowired
    private SchoolAdminRepository schoolAdminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubService clubService;

    @Autowired
    private ClubApplicationService clubApplicationService;

    @Transactional(readOnly = true)
    public ClubAdmin getClubAdminInfo(Long userId) {
        return clubAdminRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Club admin not found"));
    }

    @Transactional(readOnly = true)
    public SchoolAdmin getSchoolAdminInfo(Long userId) {
        return schoolAdminRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("School admin not found"));
    }

    @Transactional(readOnly = true)
    public Object getAdminOverview(Long userId, String adminType) {
        if ("CLUB_ADMIN".equals(adminType)) {
            ClubAdmin clubAdmin = getClubAdminInfo(userId);
            // 获取社团管理概览数据
            // 一个社团管理员可能管理多个俱乐部，这里返回第一个俱乐部的信息
            if (clubAdmin.getClubAdminAssignments() != null && !clubAdmin.getClubAdminAssignments().isEmpty()) {
                Club club = clubAdmin.getClubAdminAssignments().get(0).getClub();
                return clubService.getClubDetail(club.getId());
            } else {
                throw new RuntimeException("No clubs assigned to this admin");
            }
        } else if ("SCHOOL_ADMIN".equals(adminType)) {
            // 获取学校管理概览数据
            return new Object(); // 这里可以返回学校管理的概览数据
        } else {
            throw new RuntimeException("Invalid admin type");
        }
    }
}

package com.example.demo.service.admin.impl;

import com.example.demo.common.BusinessException;
import com.example.demo.dto.clubadmin.application.ApplicationPageResponseDTO;
import com.example.demo.dto.clubadmin.application.ClubApplicationDetailDTO;
import com.example.demo.dto.clubadmin.application.ClubApplicationListDTO;
import com.example.demo.dto.clubadmin.application.ReviewRequestDTO;
import com.example.demo.entity.ApplicationStatus;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubApplication;
import com.example.demo.entity.ClubMember;
import com.example.demo.repository.ClubApplicationRepository;
import com.example.demo.repository.ClubMemberRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.service.admin.ClubApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社团申请管理服务实现类
 */
@Service
public class ClubApplicationServiceImpl implements ClubApplicationService {

    @Autowired
    private ClubApplicationRepository clubApplicationRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Override
    public ApplicationPageResponseDTO getApplicationList(Integer clubId, String status, String keyword, int page, int pageSize) {
        // 查询申请列表
        List<ClubApplication> applications;
        if (status != null && !status.isEmpty()) {
            applications = clubApplicationRepository.findByClubIdAndStatus(clubId, status);
        } else {
            applications = clubApplicationRepository.findByClubId(clubId);
        }

        // 关键词筛选
        if (keyword != null && !keyword.isEmpty()) {
            final String lowerKeyword = keyword.toLowerCase();
            applications = applications.stream()
                .filter(app -> app.getName().toLowerCase().contains(lowerKeyword) ||
                                app.getStudentId().toLowerCase().contains(lowerKeyword) ||
                                (app.getMajor() != null && app.getMajor().toLowerCase().contains(lowerKeyword)))
                .collect(Collectors.toList());
        }

        int total = applications.size();

        // 手动分页
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, applications.size());
        List<ClubApplication> pagedApplications;
        if (start < applications.size()) {
            pagedApplications = applications.subList(start, end);
        } else {
            pagedApplications = List.of();
        }

        // 转换为DTO
        List<ClubApplicationListDTO> list = pagedApplications.stream().map(this::convertToListDTO).collect(Collectors.toList());

        ApplicationPageResponseDTO response = new ApplicationPageResponseDTO();
        response.setList(list);
        response.setTotal(total);
        response.setPage(page);
        response.setPageSize(pageSize);

        return response;
    }

    @Override
    public ClubApplicationDetailDTO getApplicationDetail(Integer applicationId, Integer clubId) {
        // 查询申请详情
        ClubApplication application = clubApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new BusinessException(404, "申请记录不存在"));

        // 验证申请是否属于当前社团
        if (!application.getClubId().equals(clubId)) {
            throw new BusinessException(403, "无权访问该申请记录");
        }

        return convertToDetailDTO(application);
    }

    @Override
    @Transactional
    public void reviewApplication(Integer applicationId, Integer clubId, String adminId, ReviewRequestDTO request) {
        // 查询申请详情
        ClubApplication application = clubApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new BusinessException(404, "申请记录不存在"));

        // 验证申请是否属于当前社团
        if (!application.getClubId().equals(clubId)) {
            throw new BusinessException(403, "无权处理该申请记录");
        }

        // 检查申请是否已经被处理
        if (application.getStatus() != ApplicationStatus.待审核) {
            throw new BusinessException(400, "该申请已经被处理过");
        }

        // 验证并转换状态值
        if (request.getStatus() == null || (!"通过".equals(request.getStatus()) && !"拒绝".equals(request.getStatus()))) {
            throw new BusinessException(400, "无效的状态值，只能是'通过'或'拒绝'");
        }

        // 安全地设置申请状态
        ApplicationStatus newStatus = null;
        try {
            newStatus = ApplicationStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "状态值格式错误");
        }

        application.setStatus(newStatus);
        application.setReviewedBy(adminId);
        application.setReviewedAt(new Timestamp(System.currentTimeMillis()));

        // 保存更新后的申请
        clubApplicationRepository.save(application);

        // 如果申请被批准，将学生添加为社团成员
        if (application.getStatus() == ApplicationStatus.通过) {
            addStudentToClub(application);
        }
    }

    @Override
    public int getPendingApplicationCount(Integer clubId) {
        // 查询待审核申请数量，使用long转int
        List<ClubApplication> pendingApps = clubApplicationRepository.findByClubIdAndStatus(clubId, "待审核");
        return pendingApps != null ? pendingApps.size() : 0;
    }

    /**
     * 将学生添加为社团成员
     */
    private void addStudentToClub(ClubApplication application) {
        // 检查学生是否已经是社团成员（注意参数顺序：clubId在前，studentId在后）
        ClubMember existingMember = clubMemberRepository.findByClubIdAndStudentId(
            application.getClubId(), application.getStudentId());

        if (existingMember == null) {
            // 创建新的社团成员记录
            ClubMember member = new ClubMember();
            member.setStudentId(application.getStudentId());
            member.setClubId(application.getClubId());
            // ClubMember实体没有name字段，不需要设置
            member.setRole("普通成员");
            // 使用LocalDate而不是Timestamp
            member.setJoinDate(java.time.LocalDate.now());
            // status是Integer类型，1表示正常
            member.setStatus(1);

            clubMemberRepository.save(member);

            // 更新社团成员数量
            Club club = clubRepository.findById(application.getClubId()).orElse(null);
            if (club != null) {
                Integer currentCount = club.getMemberCount() != null ? club.getMemberCount() : 0;
                club.setMemberCount(currentCount + 1);
                clubRepository.save(club);
            }
        }
    }

    /**
     * 转换为列表DTO
     */
    private ClubApplicationListDTO convertToListDTO(ClubApplication application) {
        ClubApplicationListDTO dto = new ClubApplicationListDTO();
        BeanUtils.copyProperties(application, dto);
        if (application.getStatus() != null) {
            dto.setStatus(application.getStatus().name());
        }
        return dto;
    }

    /**
     * 转换为详情DTO
     */
    private ClubApplicationDetailDTO convertToDetailDTO(ClubApplication application) {
        ClubApplicationDetailDTO dto = new ClubApplicationDetailDTO();
        BeanUtils.copyProperties(application, dto);
        if (application.getStatus() != null) {
            dto.setStatus(application.getStatus().name());
        }
        return dto;
    }
}

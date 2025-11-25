package com.example.demo.service.user.impl;

import com.example.demo.dto.user.club.*;
import com.example.demo.entity.Club;
import com.example.demo.entity.ClubCategory;
import com.example.demo.entity.Student;
import com.example.demo.entity.Activity;
import com.example.demo.entity.ClubApplication;
import com.example.demo.entity.ApplicationStatus;
import com.example.demo.entity.ClubMember;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.ClubApplicationRepository;
import com.example.demo.repository.ClubCategoryRepository;
import com.example.demo.repository.ClubMemberRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.StudentPointsRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.user.ClubService;
import com.example.demo.security.JwtUtil;
import com.example.demo.common.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 社团管理服务实现类
 */
@Service
public class ClubServiceImpl implements ClubService {
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private ClubCategoryRepository clubCategoryRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private ClubMemberRepository clubMemberRepository;
    
    @Autowired
    private ClubApplicationRepository clubApplicationRepository;
    
    @Autowired
    private StudentPointsRepository studentPointsRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    
    /**
     * 获取社团列表
     * @return 社团列表
     */
    @Override
    public List<ClubInfoDTO> getClubs() {
        
        List<Club> clubs= clubRepository.findAll();
        
        // 按照成立日期降序排序
        clubs.sort((c1, c2) -> c2.getFoundationDate().compareTo(c1.getFoundationDate()));
        
        // 转换为DTO并补充关联信息
        List<ClubInfoDTO> clubInfoDTOs = new ArrayList<>();
        for (Club club : clubs) {
            ClubInfoDTO dto = new ClubInfoDTO();
            dto.setClubId(club.getClubId());
            dto.setName(club.getName());
            dto.setDescription(club.getDescription());
            dto.setMemberCount(club.getMemberCount());
            
            // 获取分类名称
            Optional<ClubCategory> categoryOptional = clubCategoryRepository.findById(club.getCategoryId());
            if (categoryOptional.isPresent()) {
                dto.setCategoryName(categoryOptional.get().getName());
            }
            
            // 获取社长姓名
            if (club.getPresidentStudentId() != null) {
                Student president = studentRepository.findByStudentId(club.getPresidentStudentId());
                if (president != null) {
                    dto.setPresidentName(president.getName());
                }
            }
            
            clubInfoDTOs.add(dto);
        }
        
        return clubInfoDTOs;
    }
    
    
    /**
     * 获取社团详情
     * @param token JWT令牌
     * @param clubId 社团ID
     * @return 社团详情
     */
    @Override
    public ClubDetailDTO getClubDetail(String token, Integer clubId) {

        // 从JWT令牌中解析学生ID
        String currentStudentId = jwtUtil.getUserIdFromToken(token);
        if (currentStudentId == null || currentStudentId.isEmpty()) {
            throw new RuntimeException("无法从令牌中获取学生信息");
        }

        // 从数据库查询社团详情
        Club club = clubRepository.findById(clubId).orElse(null);
        if (club == null) {
            return null;
        }
        
        ClubDetailDTO clubDetail = new ClubDetailDTO();
        clubDetail.setClub_id(club.getClubId());
        clubDetail.setName(club.getName());
        clubDetail.setDescription(club.getDescription());
        
        // 从数据库获取Logo URL
        clubDetail.setLogo_url(club.getLogoUrl() != null ? club.getLogoUrl() : "");
        
        // 获取社长信息
        if (club.getPresidentStudentId() != null) {
            Student president = studentRepository.findByStudentId(club.getPresidentStudentId());
            if (president != null) {
                clubDetail.setPresident_name(president.getName());
            }
        }
        
        // 设置成立日期
        if (club.getFoundationDate() != null) {
            clubDetail.setFoundation_date(club.getFoundationDate().toString());
        }
        
        // 获取成员数量
        clubDetail.setMember_count(club.getMemberCount());
        
        // 设置联系邮箱（从数据库contact_info字段获取）
        clubDetail.setContact_email(club.getContactInfo() != null ? club.getContactInfo() : "");
        
        // 设置社团地址（从数据库meeting_location字段获取）
        clubDetail.setAddress(club.getMeetingLocation() != null ? club.getMeetingLocation() : "");
        
        // 添加最近活动列表（从数据库获取）
        List<ClubDetailDTO.RecentActivityDTO> recentActivities = new ArrayList<>();
        List<Activity> activities = activityRepository.findByClubId(clubId);
        
        for (Activity activity : activities) {
            ClubDetailDTO.RecentActivityDTO activityDTO = new ClubDetailDTO.RecentActivityDTO();
            activityDTO.setActivity_id(activity.getActivityId());
            activityDTO.setTitle(activity.getTitle());
            // 格式化LocalDateTime为字符串
            activityDTO.setStart_time(activity.getStartTime() != null ? 
                activity.getStartTime().toString().replace('T', ' ') : "");
            recentActivities.add(activityDTO);
        }
        
        clubDetail.setRecent_activities(recentActivities);
        
        // 查询当前学生是否已加入该社团（检查状态为1的成员关系）
        ClubMember member = clubMemberRepository.findByClubIdAndStudentId(clubId, currentStudentId);
        boolean isJoined = member != null && member.getStatus() == 1;
        clubDetail.setJoined(isJoined);
        
        return clubDetail;
    }
    
    /**
     * 申请加入社团
     * @param token JWT令牌
     * @param clubId 社团ID
     * @param application 申请信息
     * @return 申请ID
     */
    @Override
    public Integer applyToJoinClub(String token, Integer clubId, ClubApplicationDTO application) {
        // 解析token获取学生ID
        String studentId = jwtUtil.getUserIdFromToken(token);
        if (studentId == null || studentId.isEmpty()) {
            throw new BusinessException(401, "无法从令牌中获取学生信息");
        }

        // 获取学生信息
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            throw new BusinessException(404, "学生不存在");
        }

        // 验证社团是否存在
        Club club = clubRepository.findById(clubId).orElse(null);
        if (club == null) {
            throw new BusinessException(404, "社团不存在");
        }

        // 检查是否已经申请过该社团
        ClubApplication existingApplication = clubApplicationRepository.findByStudentIdAndClubId(studentId, clubId);
        if (existingApplication != null) {
            throw new BusinessException(400, "已申请该社团");
        }

        // 创建社团申请记录
        ClubApplication clubApplication = new ClubApplication();
        clubApplication.setStudentId(studentId);
        clubApplication.setClubId(clubId);
        clubApplication.setName(application.getName() != null ? application.getName() : student.getName());
        clubApplication.setMajor(application.getMajor() != null ? application.getMajor() : student.getMajor());
        clubApplication.setGrade(application.getGrade() != null ? application.getGrade() : student.getGrade());
        clubApplication.setPhone(application.getPhone() != null ? application.getPhone() : student.getPhone());
        clubApplication.setEmail(application.getEmail() != null ? application.getEmail() : student.getEmail());
        clubApplication.setReason(application.getReason());
        clubApplication.setExperience(application.getExperience());
        
        // 直接使用DTO中的值，已通过@JsonProperty确保正确映射
        clubApplication.setActivityPreference(application.getActivityPreference());
        clubApplication.setAvailableTime(application.getAvailableTime());
        
        clubApplication.setPortfolio(application.getPortfolio());
        
        // 设置申请状态为待审核
        clubApplication.setStatus(ApplicationStatus.待审核);
        
        // 设置创建时间
        clubApplication.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        // 保存申请记录到数据库
        ClubApplication savedApplication = clubApplicationRepository.save(clubApplication);
        
        // 返回申请ID
        return savedApplication.getApplicationId();
    }
    
    /**
     * 获取我的社团列表
     * @param token JWT令牌
     * @return 我的社团列表
     */
    @Override
    public List<MyClubDTO> getMyClubs(String token) {
        // 解析token获取学生ID
        String studentId = jwtUtil.getUserIdFromToken(token);
        if (studentId == null || studentId.isEmpty()) {
            throw new RuntimeException("无法从令牌中获取学生信息");
        }
        
        // 从数据库查询该学生加入的社团
        List<ClubMember> clubMembers = clubMemberRepository.findByStudentId(studentId);
        List<MyClubDTO> myClubs = new ArrayList<>();
        
        for (ClubMember clubMember : clubMembers) {
            // 获取社团信息
            Club club = clubRepository.findById(clubMember.getClubId()).orElse(null);
            if (club == null) {
                continue;
            }
            
            MyClubDTO myClubDTO = new MyClubDTO();
            myClubDTO.setClubId(club.getClubId());
            myClubDTO.setName(club.getName());
            
            // 获取分类名称
            if (club.getCategory() != null) {
                myClubDTO.setCategoryName(club.getCategory().getName());
            }
            
            myClubDTO.setRole(clubMember.getRole());
            
            // 格式化加入日期
            if (clubMember.getJoinDate() != null) {
                myClubDTO.setJoinDate(clubMember.getJoinDate().toString());
            }
            
            // 从数据库获取学生在当前社团的积分
            Double clubPoints = studentPointsRepository.sumPointsByStudentIdAndClubId(studentId, club.getClubId());
            myClubDTO.setPoints(clubPoints != null ? clubPoints : 0);
            
            myClubs.add(myClubDTO);
        }
        
        return myClubs;
    }
    
    /**
     * 退出社团
     * @param token JWT令牌
     * @param clubId 社团ID
     */
    @Override
    public void leaveClub(String token, Integer clubId) {
        // 解析token获取学生ID
        String studentId = jwtUtil.getUserIdFromToken(token);
        if (studentId == null || studentId.isEmpty()) {
            throw new RuntimeException("无法从令牌中获取学生信息");
        }
        
        // 从数据库查询该学生的社团成员关系
        ClubMember clubMember = clubMemberRepository.findByClubIdAndStudentId(clubId, studentId);
        if (clubMember == null) {
            throw new BusinessException(404,"您不是该社团的成员");
        }
        
        // 从数据库删除该学生的社团成员关系
        clubMemberRepository.delete(clubMember);
    }
}
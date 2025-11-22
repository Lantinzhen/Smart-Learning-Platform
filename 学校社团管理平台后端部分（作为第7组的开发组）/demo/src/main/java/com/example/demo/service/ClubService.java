package com.example.demo.service;

import com.example.demo.entity.Club;
import com.example.demo.entity.ClubApplication;
import com.example.demo.entity.Student;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.ClubApplicationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClubApplicationRepository clubApplicationRepository;

    @Transactional(readOnly = true)
    public List<Club> getClubSquareList(int page, int size) {
        // 这里可以添加分页查询逻辑
        return clubRepository.findByStatus(Club.ClubStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Club getClubDetail(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Club not found with id: " + clubId));
    }

    @Transactional
    public ClubApplication applyToJoinClub(Long userId, Long clubId, String applicationReason, 
                                          String relevantExperience, String expectedActivityTypes, 
                                          String availableTime, String portfolioUrl) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Club club = getClubDetail(clubId);

        // 检查是否已经申请过
        Optional<ClubApplication> existingApplication = clubApplicationRepository.findByStudentAndClub(student, club);
        if (existingApplication.isPresent()) {
            throw new RuntimeException("Already applied to this club");
        }

        // 创建申请
        ClubApplication application = new ClubApplication();
        application.setStudent(student);
        application.setClub(club);
        application.setApplicationReason(applicationReason);
        application.setRelevantExperience(relevantExperience);
        application.setExpectedActivityTypes(expectedActivityTypes);
        application.setAvailableTime(availableTime);
        application.setPortfolioUrl(portfolioUrl);
        // 设置基本联系信息
        application.setContactPhone(student.getUser().getPhone());
        application.setContactEmail(student.getUser().getEmail());
        application.setStatus(ClubApplication.ApplicationStatus.PENDING);

        return clubApplicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public List<ClubApplication> getMyClubs(Long userId) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return clubApplicationRepository.findByStudent(student);
    }

    @Transactional
    public Long cancelClubApplication(Long userId, Long clubId) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        
        // 根据学生和社团查找申请
        ClubApplication application = clubApplicationRepository.findByStudentAndClub(student, club)
                .orElseThrow(() -> new RuntimeException("Application not found for this club"));

        // 只能取消待审批的申请
        if (!application.getStatus().equals(ClubApplication.ApplicationStatus.PENDING)) {
            throw new RuntimeException("Cannot cancel approved/rejected application");
        }

        // 保存申请ID以便返回
        Long applicationId = application.getId();
        clubApplicationRepository.delete(application);
        
        // 返回被删除的申请ID
        return applicationId;
    }

    @Transactional(readOnly = true)
    public List<Club> searchClubs(String keyword) {
        return clubRepository.searchClubs(keyword);
    }

    @Transactional(readOnly = true)
    public List<Club> getPopularClubs() {
        return clubRepository.findPopularClubs();
    }
}

package com.example.demo.service.admin.impl;

import com.example.demo.common.BusinessException;
import com.example.demo.dto.admin.user.StudentListResponseDTO;
import com.example.demo.dto.admin.user.StudentRequestDTO;
import com.example.demo.dto.admin.user.ClubAdminListResponseDTO;
import com.example.demo.dto.admin.user.ClubAdminRequestDTO;
import com.example.demo.entity.Student;
import com.example.demo.entity.ClubAdmin;
import com.example.demo.entity.Club;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.ClubAdminRepository;
import com.example.demo.repository.ClubRepository;
import com.example.demo.service.admin.SchoolAdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 学校管理员用户管理服务实现类
 */
@Service
public class SchoolAdminUserServiceImpl implements SchoolAdminUserService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ClubAdminRepository clubAdminRepository;
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public StudentListResponseDTO getStudents(String keyword, String major, Integer status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        // 使用自定义查询方法
        Page<Student> studentPage = studentRepository.findStudentsByConditions(keyword, major, status, pageable);
        
        StudentListResponseDTO responseDTO = new StudentListResponseDTO();
        responseDTO.setTotal(studentPage.getTotalElements());
        responseDTO.setPage(page);
        responseDTO.setPage_size(pageSize);
        
        List<StudentListResponseDTO.StudentInfoDTO> studentInfoList = new ArrayList<>();
        for (Student student : studentPage.getContent()) {
            StudentListResponseDTO.StudentInfoDTO infoDTO = new StudentListResponseDTO.StudentInfoDTO();
            infoDTO.setStudent_id(student.getStudentId());
            infoDTO.setName(student.getName());
            infoDTO.setEmail(student.getEmail());
            infoDTO.setPhone(student.getPhone());
            infoDTO.setMajor(student.getMajor());
            infoDTO.setGrade(student.getGrade());
            infoDTO.setStatus(student.getStatus());
            studentInfoList.add(infoDTO);
        }
        responseDTO.setList(studentInfoList);
        
        return responseDTO;
    }
    
    @Override
    public Map<String, String> createOrUpdateStudent(StudentRequestDTO requestDTO) {
        Optional<Student> existingStudentOpt = studentRepository.findById(requestDTO.getStudent_id());
        Student student;
        
        if (existingStudentOpt.isPresent()) {
            // 更新学生信息
            student = existingStudentOpt.get();
            student.setName(requestDTO.getName());
            student.setEmail(requestDTO.getEmail());
            student.setPhone(requestDTO.getPhone());
            student.setMajor(requestDTO.getMajor());
            student.setGrade(requestDTO.getGrade());
            student.setEnrollmentYear(requestDTO.getEnrollment_year());
            student.setGender(requestDTO.getGender());
            
            // 如果提供了密码，则更新密码
            if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
                student.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
            }
        } else {
            // 创建新学生
            student = new Student();
            student.setStudentId(requestDTO.getStudent_id());
            student.setName(requestDTO.getName());
            student.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
            student.setEmail(requestDTO.getEmail());
            student.setPhone(requestDTO.getPhone());
            student.setMajor(requestDTO.getMajor());
            student.setGrade(requestDTO.getGrade());
            student.setEnrollmentYear(requestDTO.getEnrollment_year());
            student.setGender(requestDTO.getGender());
            student.setStatus(1); // 默认启用状态
        }
        
        studentRepository.save(student);
        
        Map<String, String> result = new HashMap<>();
        result.put("student_id", student.getStudentId());
        return result;
    }
    
    @Override
    public void updateStudentStatus(String studentId, Integer status) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new BusinessException(404,"学生不存在"));
        student.setStatus(status);
        studentRepository.save(student);
    }
    
    @Override
    public ClubAdminListResponseDTO getClubAdmins(String keyword, Integer clubId, Integer status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        // 使用自定义查询方法
        Page<ClubAdmin> clubAdminPage = clubAdminRepository.findClubAdminsByConditions(keyword, clubId, status, pageable);
        
        // 获取所有社团信息，用于填充社团名称
        List<Club> allClubs = clubRepository.findAll();
        Map<Integer, String> clubNameMap = new HashMap<>();
        for (Club club : allClubs) {
            clubNameMap.put(club.getClubId(), club.getName());
        }
        
        ClubAdminListResponseDTO responseDTO = new ClubAdminListResponseDTO();
        responseDTO.setTotal(clubAdminPage.getTotalElements());
        responseDTO.setPage(page);
        responseDTO.setPage_size(pageSize);
        
        List<ClubAdminListResponseDTO.ClubAdminInfoDTO> adminInfoList = new ArrayList<>();
        for (ClubAdmin admin : clubAdminPage.getContent()) {
            ClubAdminListResponseDTO.ClubAdminInfoDTO infoDTO = new ClubAdminListResponseDTO.ClubAdminInfoDTO();
            infoDTO.setAdmin_id(admin.getAdminId());
            infoDTO.setClub_id(admin.getClubId());
            infoDTO.setClub_name(clubNameMap.getOrDefault(admin.getClubId(), "未命名社团"));
            infoDTO.setName(admin.getName());
            infoDTO.setUsername(admin.getUsername());
            infoDTO.setEmail(admin.getEmail());
            infoDTO.setPhone(admin.getPhone());
            infoDTO.setStatus(admin.getStatus());
            adminInfoList.add(infoDTO);
        }
        responseDTO.setList(adminInfoList);
        
        return responseDTO;
    }
    
    @Override
    public Map<String, String> createOrUpdateClubAdmin(ClubAdminRequestDTO requestDTO) {
        // 验证社团是否存在
        clubRepository.findById(requestDTO.getClub_id())
            .orElseThrow(() -> new BusinessException(404,"社团不存在"));
        
        Optional<ClubAdmin> existingAdminOpt = clubAdminRepository.findById(requestDTO.getAdmin_id());
        ClubAdmin admin;
        
        if (existingAdminOpt.isPresent()) {
            // 更新社团管理员信息
            admin = existingAdminOpt.get();
            admin.setClubId(requestDTO.getClub_id());
            admin.setName(requestDTO.getName());
            admin.setPhone(requestDTO.getPhone());
            
            // 检查用户名是否被其他管理员使用
            if (!admin.getUsername().equals(requestDTO.getUsername())) {
                if (clubAdminRepository.existsByUsername(requestDTO.getUsername())) {
                    throw new BusinessException(400,"用户名已存在");
                }
                admin.setUsername(requestDTO.getUsername());
            }
            
            // 检查邮箱是否被其他管理员使用
            if (!admin.getEmail().equals(requestDTO.getEmail())) {
                if (clubAdminRepository.existsByEmail(requestDTO.getEmail())) {
                    throw new BusinessException(400,"邮箱已存在");
                }
                admin.setEmail(requestDTO.getEmail());
            }
            
            // 如果提供了密码，则更新密码
            if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
                admin.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
            }
        } else {
            // 创建新社团管理员
            // 检查用户名是否已存在
            if (clubAdminRepository.existsByUsername(requestDTO.getUsername())) {
                throw new BusinessException(400,"用户名已存在");
            }
            
            // 检查邮箱是否已存在
            if (clubAdminRepository.existsByEmail(requestDTO.getEmail())) {
                throw new BusinessException(400,"邮箱已存在");
            }
            
            admin = new ClubAdmin();
            admin.setAdminId(requestDTO.getAdmin_id());
            admin.setClubId(requestDTO.getClub_id());
            admin.setName(requestDTO.getName());
            admin.setUsername(requestDTO.getUsername());
            admin.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
            admin.setEmail(requestDTO.getEmail());
            admin.setPhone(requestDTO.getPhone());
            admin.setStatus(1); // 默认启用状态
        }
        
        clubAdminRepository.save(admin);
        
        Map<String, String> result = new HashMap<>();
        result.put("admin_id", admin.getAdminId());
        return result;
    }
    
    @Override
    public void updateClubAdminStatus(String adminId, Integer status) {
        ClubAdmin admin = clubAdminRepository.findById(adminId)
            .orElseThrow(() -> new BusinessException(404,"社团管理员不存在"));
        admin.setStatus(status);
        clubAdminRepository.save(admin);
    }
}

package com.example.demo.service.user.impl;

import com.example.demo.common.BusinessException;
import com.example.demo.dto.user.profile.*;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentPointsRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.user.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 个人中心服务实现类
 */
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private StudentPointsRepository studentPointsRepository;
    
    /**
     * 获取个人信息
     * @param token JWT令牌
     * @return 个人信息
     */
    @Override
    public ProfileDTO getProfile(String token) {
        // 从token中获取学生ID
        String studentId = jwtUtil.getUserIdFromToken(token);
        
        // 根据学生ID查询学生信息
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            throw new BusinessException(404, "学生信息不存在");
        }
        
        // 查询学生积分
        Double totalPoints = studentPointsRepository.sumPointsByStudentId(studentId);
        
        // 构建返回的DTO
        ProfileDTO profile = new ProfileDTO();
        profile.setStudentId(student.getStudentId());
        profile.setName(student.getName());
        profile.setAvatar(student.getAvatarUrl()); // 注意这里使用的是avatarUrl
        profile.setEmail(student.getEmail());
        profile.setPhone(student.getPhone());
        profile.setMajor(student.getMajor());
        profile.setGrade(student.getGrade());
        profile.setEnrollmentYear(student.getEnrollmentYear());
        profile.setGender(student.getGender());
        profile.setPoints(totalPoints != null ? totalPoints.intValue() : 0);
        profile.setCreatedAt(student.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        return profile;
    }

    /**
     * 更新个人信息
     * @param token JWT令牌
     * @param profile 个人信息
     * @param file 头像文件（可选，保留兼容性）
     */
    @Override
    public void updateProfile(String token, UpdateProfileDTO profile) {
        // 从token中获取学生ID
        String studentId = jwtUtil.getUserIdFromToken(token);
        
        // 查询学生信息
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            throw new BusinessException(404, "学生信息不存在");
        }
        
        // 更新个人信息 - 只更新非空字段，支持部分更新
        if (profile.getName() != null) {
            student.setName(profile.getName());
        }
        if (profile.getEmail() != null) {
            student.setEmail(profile.getEmail());
        }
        if (profile.getPhone() != null) {
            student.setPhone(profile.getPhone());
        }
        if (profile.getMajor() != null) {
            student.setMajor(profile.getMajor());
        }
        if (profile.getGrade() != null) {
            student.setGrade(profile.getGrade());
        }
        if (profile.getEnrollmentYear() != null) {
            student.setEnrollmentYear(profile.getEnrollmentYear());
        }
        if (profile.getGender() != null) {
            student.setGender(profile.getGender());
        }
        
        // 确保更新时间被正确设置
        student.setUpdatedAt(LocalDateTime.now());
        
        // 保存更新后的信息
        studentRepository.saveAndFlush(student);
    }
    


    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 修改密码
     * @param token JWT令牌
     * @param passwordDTO 密码信息
     */
    @Override
    public void changePassword(String token, ChangePasswordDTO passwordDTO) {
        // 从token中获取学生ID
        String studentId = jwtUtil.getUserIdFromToken(token);
        
        // 查询学生信息
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            throw new BusinessException(404, "学生信息不存在");
        }
        
        // 验证旧密码（支持明文密码和加密密码）
        String dbPassword = student.getPassword();
        boolean passwordMatch = false;
        
        // 检查数据库中的密码是否已经是加密的（BCrypt密码通常以$2a$开头）
        if (dbPassword != null && dbPassword.startsWith("$2a$")) {
            passwordMatch = passwordEncoder.matches(passwordDTO.getOldPassword(), dbPassword);
        } else {
            // 如果是明文密码，直接比较
            passwordMatch = dbPassword != null && passwordDTO.getOldPassword().equals(dbPassword);
        }
        
        if (!passwordMatch) {
            throw new BusinessException(400, "原密码错误");
        }
        
        // 使用BCrypt加密新密码
        String encodedPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
        student.setPassword(encodedPassword);
        
        // 保存更新后的信息
        studentRepository.save(student);
    }
    

}
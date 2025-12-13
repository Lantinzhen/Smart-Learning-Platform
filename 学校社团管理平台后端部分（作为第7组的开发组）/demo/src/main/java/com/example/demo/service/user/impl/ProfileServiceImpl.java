package com.example.demo.service.user.impl;

import com.example.demo.common.BusinessException;
import com.example.demo.dto.user.profile.*;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentPointsRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.user.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 个人中心服务实现类
 */
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
    
    // 从配置文件读取上传路径和基础URL
    @Value("${file.upload-dir:./uploads/avatars}")
    private String uploadDir;
    
    @Value("${file.base-url:http://localhost:8080/uploads/avatars}")
    private String baseUrl;

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
    public void updateProfile(String token, UpdateProfileDTO profile, MultipartFile file) {
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
        
        // 只支持通过文件上传更新头像
        if (file != null && !file.isEmpty()) {
            // 保存文件到服务器，获取头像URL
            String avatarUrl = saveAvatarFile(file);
            // 设置头像URL
            student.setAvatarUrl(avatarUrl);
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
    
    /**
     * 保存头像文件
     * @param file 头像文件
     * @return 头像URL
     */
    private String saveAvatarFile(MultipartFile file) {
        logger.info("开始处理头像文件上传");
        
        try {
            // 1. 验证文件类型和大小
            if (file == null || file.isEmpty()) {
                logger.error("上传的文件为空");
                throw new BusinessException(400, "上传的文件不能为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.error("文件类型无效: {}", contentType);
                throw new BusinessException(400, "只支持图片文件上传");
            }
            
            // 验证文件大小（限制为5MB）
            long fileSize = file.getSize();
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (fileSize > maxSize) {
                logger.error("文件大小超过限制: {} bytes, 最大允许: {} bytes", fileSize, maxSize);
                throw new BusinessException(400, "文件大小不能超过5MB");
            }
            
            // 2. 生成唯一文件名
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 使用UUID生成唯一文件名，避免文件名冲突
            String uniqueFilename = "avatar_" + UUID.randomUUID().toString() + fileExtension;
            logger.debug("生成的唯一文件名为: {}", uniqueFilename);
            
            // 3. 保存文件到指定位置
            // 创建上传目录（使用配置的路径）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                logger.info("创建上传目录: {}", uploadDir);
                Files.createDirectories(uploadPath);
            }
            
            // 完整文件路径
            Path targetLocation = uploadPath.resolve(uniqueFilename);
            
            // 保存文件 - 使用Files.copy方法更安全
            logger.info("开始保存文件到: {}", targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // 4. 返回可访问的URL（使用配置的基础URL）
            String avatarUrl = baseUrl + "/" + uniqueFilename;
            logger.info("文件上传成功，生成的URL为: {}", avatarUrl);
            
            return avatarUrl;
            
        } catch (BusinessException e) {
            // 直接重新抛出已定义的业务异常
            throw e;
        } catch (IOException e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("处理文件上传时发生未知错误: {}", e.getMessage(), e);
            throw new BusinessException(500, "处理文件时发生错误");
        }
    }
}
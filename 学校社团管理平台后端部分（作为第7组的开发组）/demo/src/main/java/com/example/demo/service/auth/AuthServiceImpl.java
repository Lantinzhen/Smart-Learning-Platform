package com.example.demo.service.auth;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.ClubAdmin;
import com.example.demo.entity.SchoolAdmin;
import com.example.demo.entity.Student;
import com.example.demo.repository.ClubAdminRepository;
import com.example.demo.repository.SchoolAdminRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.TokenBlacklist;
import com.example.demo.common.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ClubAdminRepository clubAdminRepository;
    
    @Autowired
    private SchoolAdminRepository schoolAdminRepository;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private TokenBlacklist tokenBlacklist;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse studentLogin(LoginRequest request) {
        // 根据学号查询学生
        Student student = studentRepository.findByStudentId(request.getStudent_id());
        if (student == null) {
            throw new BusinessException(401, "学号或密码错误");
        }

        // 检查状态
        if (student.getStatus() != 1) {
            throw new BusinessException(401, "账号已被禁用");
        }

        // 验证密码（支持明文密码）
        String dbPassword = student.getPassword();
        boolean passwordMatch = false;
        
        // 检查数据库中的密码是否已经是加密的（BCrypt密码通常以$2a$开头）
        if (dbPassword.startsWith("$2a$")) {
            passwordMatch = passwordEncoder.matches(request.getPassword(), dbPassword);
        } else {
            // 如果是明文密码，直接比较
            passwordMatch = request.getPassword().equals(dbPassword);
        }
        
        if (!passwordMatch) {
            throw new BusinessException(401, "学号或密码错误");
        }

        // 生成token
        String token = jwtUtil.generateToken(student.getStudentId(), "student", student.getName());

        // 构建响应 - 只设置学生相关的非空字段
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                student.getStudentId(),
                null,
                student.getName(),
                null,
                "student",
                null
        );
        return new LoginResponse(token, userInfo);
    }

    @Override
    public String studentRegister(RegisterRequest request) {
        // 检查学号是否已存在
        if (studentRepository.existsByStudentId(request.getStudent_id())) {
            throw new BusinessException(400, "学号已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty() && studentRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(400, "邮箱已被注册");
        }

        // 创建学生对象
        Student student = new Student();
        student.setStudentId(request.getStudent_id());
        student.setName(request.getName());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setMajor(request.getMajor());
        student.setGrade(request.getGrade());
        student.setEnrollmentYear(request.getEnrollment_year());
        student.setGender(request.getGender());
        student.setStatus(1); // 默认为启用状态

        // 保存到数据库
        studentRepository.save(student);

        return student.getStudentId();
    }

    @Override
    public LoginResponse clubAdminLogin(LoginRequest request) {
        // 根据用户名查询社团管理员
        ClubAdmin clubAdmin = clubAdminRepository.findByUsername(request.getUsername());
        if (clubAdmin == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查状态
        if (clubAdmin.getStatus() != 1) {
            throw new BusinessException(401, "账号已被禁用");
        }

        // 验证密码（支持明文密码）
        String dbPassword = clubAdmin.getPassword();
        boolean passwordMatch = false;
        
        // 检查数据库中的密码是否已经是加密的（BCrypt密码通常以$2a$开头）
        if (dbPassword.startsWith("$2a$")) {
            passwordMatch = passwordEncoder.matches(request.getPassword(), dbPassword);
        } else {
            // 如果是明文密码，直接比较
            passwordMatch = request.getPassword().equals(dbPassword);
        }
        
        if (!passwordMatch) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 生成token
        String token = jwtUtil.generateToken(clubAdmin.getAdminId(), "club_admin", clubAdmin.getName());

        // 构建响应
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                null,
                clubAdmin.getAdminId(),
                clubAdmin.getName(),
                clubAdmin.getClubId(),
                "club_admin",
                null
        );
        return new LoginResponse(token, userInfo);
    }

    @Override
    public LoginResponse schoolAdminLogin(LoginRequest request) {
        // 参数校验
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new BusinessException(400, "请求参数不完整");
        }

        // 根据用户名查询学校管理员
        SchoolAdmin schoolAdmin = schoolAdminRepository.findByUsername(request.getUsername());
        if (schoolAdmin == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查状态
        if (schoolAdmin.getStatus() != 1) {
            throw new BusinessException(401, "账号已被禁用");
        }

        // 验证密码（支持明文密码）
        String dbPassword = schoolAdmin.getPassword();
        boolean passwordMatch = false;
        
        // 检查数据库中的密码是否已经是加密的（BCrypt密码通常以$2a$开头）
        if (dbPassword != null && dbPassword.startsWith("$2a$")) {
            passwordMatch = passwordEncoder.matches(request.getPassword(), dbPassword);
        } else {
            // 如果是明文密码，直接比较（增加null检查）
            passwordMatch = dbPassword != null && request.getPassword().equals(dbPassword);
        }
        
        if (!passwordMatch) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 生成token（确保所有字段不为null）
        String adminId = schoolAdmin.getAdminId();
        String name = schoolAdmin.getName();
        if (adminId == null || name == null) {
            throw new BusinessException(500, "用户信息不完整");
        }
        String token = jwtUtil.generateToken(adminId, "school_admin", name);

        // 构建响应
        // 添加role字段的null检查，防止空指针异常
        String role = schoolAdmin.getRole();
        if (role == null) {
            role = "超级管理员"; // 使用默认值
        }
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                null,
                adminId,
                name,
                null,
                "school_admin",
                role
        );
        return new LoginResponse(token, userInfo);
    }

    @Override
    public void logout(String token) {
        // 将token添加到黑名单中
        tokenBlacklist.addToBlacklist(token);
    }
}
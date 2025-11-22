package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Transactional
    public Object registerStudent(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(User.Role.STUDENT);
        user.setStatus(User.UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        // 创建学生信息
        Student student = new Student();
        student.setStudentId(request.getStudentId());
        student.setName(request.getName());
        student.setMajor(request.getMajor());
        student.setGrade(request.getGrade());
        student.setClassName(request.getClassName());
        student.setUser(savedUser);

        Student savedStudent = studentRepository.save(student);

        // 构建API响应数据结构
        return new java.util.HashMap<String, Object>() {{  
            put("userId", savedUser.getId());
            put("studentId", savedStudent.getStudentId());
            put("username", savedUser.getUsername());
        }};
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        try {
            // 验证用户名和密码
            System.out.println("Attempting login for user: " + request.getUsername() + ", role: " + request.getRole());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            System.out.println("Authentication successful for user: " + request.getUsername());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 加载用户信息
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("User loaded from database: " + user.getUsername() + ", role: " + user.getRole().name());
            
            // 验证角色
            if (!user.getRole().name().equals(request.getRole())) {
                System.out.println("Role mismatch: expected " + request.getRole() + ", found " + user.getRole().name());
                throw new RuntimeException("Invalid role");
            }

            System.out.println("Role validation successful");
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername(), user.getRole().name());

            System.out.println("JWT tokens generated successfully");
            
            // 构建登录响应
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setExpiresIn(jwtExpiration);

            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRole(user.getRole().name());

            // 根据角色设置不同的信息
            if (user.getRole() == User.Role.STUDENT) {
                Student student = studentRepository.findByUser(user)
                        .orElseThrow(() -> new RuntimeException("Student info not found"));
                userInfo.setName(student.getName());
                userInfo.setStudentId(student.getStudentId());
                userInfo.setAvatarUrl(student.getAvatarUrl());
            }

            loginResponse.setUserInfo(userInfo);

            System.out.println("Login response built successfully");
            
            return loginResponse;
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
-- =====================================================
-- 学校社团管理平台 API测试模拟数据
-- 数据库名: school_club_platform
-- 生成时间: 2024-01-01
-- =====================================================
SET NAMES utf8mb4;
USE school_club_platform;

-- =====================================================
-- 1. 用户基础信息表 (users)
-- =====================================================
INSERT INTO users (username, password, email, phone, role, status, created_at, updated_at, last_login_time) VALUES
('student1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student1@example.com', '13800138001', 'STUDENT', 'ACTIVE', NOW(), NOW(), NULL),
('student2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student2@example.com', '13800138002', 'STUDENT', 'ACTIVE', NOW(), NOW(), NULL),
('student3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student3@example.com', '13800138003', 'STUDENT', 'ACTIVE', NOW(), NOW(), NULL),
('clubadmin1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'clubadmin1@example.com', '13800138006', 'CLUB_ADMIN', 'ACTIVE', NOW(), NOW(), NULL),
('schooladmin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'schooladmin@example.com', '13800138008', 'SCHOOL_ADMIN', 'ACTIVE', NOW(), NOW(), NULL);

-- 所有用户的密码都是: password123

-- =====================================================
-- 2. 学生详细信息表 (students)
-- =====================================================
INSERT INTO students (user_id, student_id, name, major, grade, class_name, gender, birth_date, avatar_url, bio, total_points, created_at, updated_at) VALUES
(1, '20210001', '张三', '计算机科学与技术', '2021级', '计科1班', 'MALE', '2003-05-15', 'https://example.com/avatars/student1.jpg', '热爱编程和人工智能', 150, NOW(), NOW()),
(2, '20210002', '李四', '软件工程', '2021级', '软工1班', 'FEMALE', '2003-08-20', 'https://example.com/avatars/student2.jpg', '喜欢前端开发和UI设计', 120, NOW(), NOW()),
(3, '20210003', '王五', '数据科学与大数据技术', '2021级', '大数据1班', 'MALE', '2003-03-10', 'https://example.com/avatars/student3.jpg', '数据爱好者，擅长数据分析', 90, NOW(), NOW());

-- =====================================================
-- 3. 社团管理员信息表 (club_admins)
-- =====================================================
INSERT INTO club_admins (user_id, name, employee_id, department, position, avatar_url, created_at, updated_at) VALUES
(4, '周八', 'CA001', '团委', '社团管理主任', 'https://example.com/avatars/clubadmin1.jpg', NOW(), NOW());

-- =====================================================
-- 4. 学校管理员信息表 (school_admins)
-- =====================================================
INSERT INTO school_admins (user_id, name, employee_id, department, position, permissions, avatar_url, created_at, updated_at) VALUES
(5, '郑十', 'SA001', '学工部', '学工处处长', '{"PERMISSION_1": true, "PERMISSION_2": true}', 'https://example.com/avatars/schooladmin.jpg', NOW(), NOW());

-- =====================================================
-- 5. 社团信息表 (clubs)
-- =====================================================
INSERT INTO clubs (name, description, type, declaration, logo_url, banner_url, established_date, max_members, current_member_count, contact_email, contact_phone, location, status, created_at, updated_at, created_by) VALUES
('计算机协会', '致力于计算机技术交流与学习的学生社团', 'TECHNOLOGY', '科技改变世界，创新引领未来', 'https://example.com/logos/tech_club.jpg', 'https://example.com/banners/tech_club.jpg', '2015-09-01', 100, 30, 'tech_club@example.com', '13800138009', '科技楼301室', 'ACTIVE', NOW(), NOW(), 1),
('音乐社', '热爱音乐的同学交流和展示的平台', 'ARTS', '用音乐传递快乐，用旋律连接你我', 'https://example.com/logos/music_club.jpg', 'https://example.com/banners/music_club.jpg', '2016-03-15', 80, 40, 'music_club@example.com', '13800138010', '艺术楼202室', 'ACTIVE', NOW(), NOW(), 1);

-- =====================================================
-- 6. 社团管理员与社团关联表 (club_admin_assignments)
-- =====================================================
INSERT INTO club_admin_assignments (club_id, admin_id, position, assigned_at, is_active) VALUES
(1, 1, 'HEAD', NOW(), TRUE),  -- 周八管理计算机协会
(2, 1, 'VICE_HEAD', NOW(), TRUE);  -- 周八兼任音乐社副社长

-- =====================================================
-- 7. 学生社团关系表 (student_club_memberships)
-- =====================================================
INSERT INTO student_club_memberships (student_id, club_id, role, joined_at, status, points_earned) VALUES
(1, 1, 'VICE_LEADER', '2023-09-01 10:00:00', 'ACTIVE', 80),  -- 张三是计算机协会副社长
(2, 1, 'MEMBER', '2023-09-01 10:00:00', 'ACTIVE', 50),  -- 李四加入计算机协会
(3, 2, 'MEMBER', '2023-09-03 09:15:00', 'ACTIVE', 30);  -- 王五加入音乐社

-- =====================================================
-- 8. 申请加入社团表 (club_applications)
-- =====================================================
INSERT INTO club_applications (student_id, club_id, application_reason, relevant_experience, expected_activity_types, available_time, portfolio_url, contact_phone, contact_email, status, applied_at, processed_at, processed_by, processed_note) VALUES
(2, 2, '我对音乐很感兴趣，希望能加入音乐社学习更多音乐知识', '曾学习过钢琴5年', '音乐欣赏、乐器学习', '周末和晚上', NULL, '13800138002', 'student2@example.com', 'PENDING', '2023-10-01 10:00:00', NULL, NULL, NULL),
(3, 1, '我对计算机技术很感兴趣，希望能加入计算机协会学习更多知识', '曾参加过编程比赛', '技术分享、项目开发', '晚上和周末', 'https://example.com/portfolio/student3', '13800138003', 'student3@example.com', 'APPROVED', '2023-10-02 14:30:00', '2023-10-03 09:00:00', 1, '申请通过，欢迎加入计算机协会');

-- =====================================================
-- 9. 活动信息表 (activities)
-- =====================================================
INSERT INTO activities (club_id, title, description, activity_type, start_time, end_time, registration_deadline, location, max_participants, current_participant_count, required_points, reward_points, registration_fee, requirements, materials_needed, contact_info, poster_url, status, created_at, updated_at, created_by, approved_by, approved_at) VALUES
(1, 'Python编程入门讲座', '面向初学者的Python编程入门讲座，介绍Python的基本语法和应用', 'WORKSHOP', '2023-11-10 14:00:00', '2023-11-10 16:00:00', '2023-11-09 23:59:59', '科技楼301室', 50, 2, 0, 20, 0.00, '无特殊要求', '笔记本电脑', 'tech_club@example.com', 'https://example.com/posters/python_workshop.jpg', 'APPROVED', NOW(), NOW(), 1, 1, '2023-10-20 10:00:00'),
(2, '校园音乐会', '展示社团成员的音乐才华，包括独唱、合唱、乐器演奏等', 'SOCIAL', '2023-11-15 19:00:00', '2023-11-15 21:00:00', '2023-11-14 23:59:59', '艺术楼报告厅', 200, 1, 0, 10, 5.00, '无特殊要求', '无', 'music_club@example.com', 'https://example.com/posters/music_concert.jpg', 'APPROVED', NOW(), NOW(), 1, 1, '2023-10-25 14:30:00');

-- =====================================================
-- 10. 活动报名申请表 (activity_applications)
-- =====================================================
INSERT INTO activity_applications (activity_id, student_id, application_reason, special_requirements, emergency_contact, emergency_phone, status, applied_at, processed_at, processed_by, processed_note, points_earned, attended_at) VALUES
(1, 1, '我想学习Python编程，提升自己的技术能力', NULL, '张父', '13800138009', 'APPROVED', '2023-10-15 10:00:00', '2023-10-15 11:00:00', 1, '申请通过', 20, '2023-11-10 14:00:00'),
(1, 2, '作为计算机协会的成员，我应该参加这个讲座', NULL, '李母', '13800138010', 'APPROVED', '2023-10-15 10:30:00', '2023-10-15 11:00:00', 1, '申请通过', 20, NULL),
(2, 3, '我想欣赏音乐表演，放松一下', NULL, '王母', '13800138011', 'APPROVED', '2023-10-20 14:30:00', '2023-10-20 15:00:00', 1, '申请通过', 10, NULL);

-- =====================================================
-- 11. 用户活动积分记录表 (user_activity_scores)
-- =====================================================
INSERT INTO user_activity_scores (student_id, activity_id, club_id, score_type, points, description, created_at, created_by) VALUES
(1, 1, 1, 'ACTIVITY_PARTICIPATION', 20, '参加Python编程入门讲座', '2023-11-10 16:00:00', 1),
(1, NULL, 1, 'CLUB_CONTRIBUTION', 30, '为计算机协会撰写活动策划', '2023-10-10 14:30:00', 1),
(2, NULL, 1, 'CLUB_CONTRIBUTION', 10, '为计算机协会准备学习材料', '2023-10-20 09:15:00', 1),
(3, NULL, 2, 'SYSTEM_REWARD', 5, '新成员奖励', '2023-09-05 11:20:00', 1);

-- =====================================================
-- 12. 系统日志表 (system_logs)
-- =====================================================
INSERT INTO system_logs (user_id, action, target_type, target_id, description, ip_address, user_agent, created_at) VALUES
(1, 'LOGIN', 'USER', 1, '用户登录系统', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/119.0.0.0', '2023-11-01 08:30:00'),
(1, 'JOIN_CLUB', 'CLUB', 1, '用户加入计算机协会', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/119.0.0.0', '2023-09-01 14:30:00'),
(4, 'CREATE_ACTIVITY', 'ACTIVITY', 1, '社团管理员创建Python编程入门讲座活动', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/119.0.0.0', '2023-10-01 10:00:00');

-- =====================================================
-- 13. 通知消息表 (notifications)
-- =====================================================
INSERT INTO notifications (user_id, title, content, type, related_type, related_id, is_read, created_at, read_at) VALUES
(1, '活动报名成功', '您已成功报名参加Python编程入门讲座', 'APPLICATION_RESULT', 'ACTIVITY_APPLICATION', 1, TRUE, '2023-10-15 11:00:00', '2023-10-15 11:30:00'),
(2, '社团申请待处理', '您的音乐社申请正在处理中，请耐心等待', 'APPLICATION_RESULT', 'CLUB_APPLICATION', 1, FALSE, '2023-10-01 10:00:00', NULL),
(3, '社团申请通过', '您的计算机协会申请已通过', 'APPLICATION_RESULT', 'CLUB_APPLICATION', 2, TRUE, '2023-10-03 09:00:00', '2023-10-03 09:30:00');
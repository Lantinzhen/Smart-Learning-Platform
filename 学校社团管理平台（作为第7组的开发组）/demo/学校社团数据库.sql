-- =====================================================
-- 学校社团管理平台数据库设计
-- 数据库名: school_club_platform
-- =====================================================
-- 创建数据库
CREATE DATABASE
IF NOT EXISTS school_club_platform CHARACTER
  SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  USE school_club_platform;
  
  -- =====================================================
  -- 1. 用户基础信息表 (所有用户的通用信息)
  -- =====================================================
  CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR (50) NOT NULL UNIQUE COMMENT '用户名',
    PASSWORD VARCHAR (255) NOT NULL COMMENT '密码(加密存储)',
    email VARCHAR (100) UNIQUE COMMENT '邮箱',
    phone VARCHAR (20) COMMENT '手机号',
    role ENUM ('STUDENT', 'CLUB_ADMIN', 'SCHOOL_ADMIN') NOT NULL COMMENT '用户角色',
    STATUS ENUM ('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE' COMMENT '账户状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time TIMESTAMP NULL COMMENT '最后登录时间',
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (STATUS)
  ) COMMENT '用户基础信息表';
  
  -- =====================================================
  -- 2. 学生详细信息表
  -- =====================================================
  CREATE TABLE students (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联users表ID',
    student_id VARCHAR (20) NOT NULL UNIQUE COMMENT '学号',
    NAME VARCHAR (50) NOT NULL COMMENT '姓名',
    major VARCHAR (100) COMMENT '专业',
    grade VARCHAR (20) COMMENT '年级',
    class_name VARCHAR (50) COMMENT '班级',
    gender ENUM ('MALE', 'FEMALE', 'OTHER') COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    avatar_url VARCHAR (255) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    total_points INT DEFAULT 0 COMMENT '总积分',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_name (NAME),
    INDEX idx_major (major),
    INDEX idx_grade (grade)
  ) COMMENT '学生详细信息表';
  
  -- =====================================================
  -- 3. 社团管理员信息表
  -- =====================================================
  CREATE TABLE club_admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '社团管理员ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联users表ID',
    NAME VARCHAR (50) NOT NULL COMMENT '姓名',
    employee_id VARCHAR (20) COMMENT '员工号',
    department VARCHAR (100) COMMENT '所属部门',
    position VARCHAR (50) COMMENT '职位',
    avatar_url VARCHAR (255) COMMENT '头像URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id)
  ) COMMENT '社团管理员信息表';
  
  -- =====================================================
  -- 4. 学校管理员信息表
  -- =====================================================
  CREATE TABLE school_admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学校管理员ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联users表ID',
    NAME VARCHAR (50) NOT NULL COMMENT '姓名',
    employee_id VARCHAR (20) COMMENT '员工号',
    department VARCHAR (100) COMMENT '所属部门',
    position VARCHAR (50) COMMENT '职位',
    permissions JSON COMMENT '权限配置JSON',
    avatar_url VARCHAR (255) COMMENT '头像URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id)
  ) COMMENT '学校管理员信息表';
  
  -- =====================================================
  -- 5. 社团信息表
  -- =====================================================
  CREATE TABLE clubs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '社团ID',
    NAME VARCHAR (100) NOT NULL COMMENT '社团名称',
    description TEXT COMMENT '社团描述',
    type ENUM ('ACADEMIC', 'ARTS', 'SPORTS', 'SOCIAL', 'TECHNOLOGY', 'OTHERS') NOT NULL COMMENT '社团类型',
    declaration TEXT COMMENT '社团宣言',
    logo_url VARCHAR (255) COMMENT '社团logo URL',
    banner_url VARCHAR (255) COMMENT '社团横幅URL',
    established_date DATE COMMENT '成立日期',
    max_members INT DEFAULT 100 COMMENT '最大成员数',
    current_member_count INT DEFAULT 0 COMMENT '当前成员数',
    contact_email VARCHAR (100) COMMENT '联系邮箱',
    contact_phone VARCHAR (20) COMMENT '联系电话',
    location VARCHAR (200) COMMENT '活动地点',
    STATUS ENUM ('ACTIVE', 'INACTIVE', 'DISBANDED') DEFAULT 'ACTIVE' COMMENT '社团状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建者ID (学校管理员ID)',
    INDEX idx_name (NAME),
    INDEX idx_type (type),
    INDEX idx_status (STATUS),
    INDEX idx_established_date (established_date)
  ) COMMENT '社团信息表';
  
  -- =====================================================
  -- 6. 社团管理员与社团关联表
  -- =====================================================
  CREATE TABLE club_admin_assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分配ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    admin_id BIGINT NOT NULL COMMENT '社团管理员ID',
    position ENUM ('HEAD', 'VICE_HEAD', 'SECRETARY', 'MEMBER') DEFAULT 'MEMBER' COMMENT '管理职位',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否在职',
    FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES club_admins (id) ON DELETE CASCADE,
    UNIQUE KEY unique_active_assignment (club_id, admin_id),
    INDEX idx_club_id (club_id),
    INDEX idx_admin_id (admin_id),
    INDEX idx_is_active (is_active)
  ) COMMENT '社团管理员与社团关联表';
  
  -- =====================================================
  -- 7. 学生社团关系表
  -- =====================================================
  CREATE TABLE student_club_memberships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    role ENUM ('MEMBER', 'VICE_LEADER', 'LEADER') DEFAULT 'MEMBER' COMMENT '成员角色',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    STATUS ENUM ('ACTIVE', 'INACTIVE', 'GRADUATED') DEFAULT 'ACTIVE' COMMENT '成员状态',
    points_earned INT DEFAULT 0 COMMENT '在社团获得的总积分',
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE,
    UNIQUE KEY unique_membership (student_id, club_id),
    INDEX idx_student_id (student_id),
    INDEX idx_club_id (club_id),
    INDEX idx_role (role),
    INDEX idx_status (STATUS)
  ) COMMENT '学生社团关系表';
  
  -- =====================================================
  -- 8. 申请加入社团表
  -- =====================================================
  CREATE TABLE club_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    student_id BIGINT NOT NULL COMMENT '申请学生ID',
    club_id BIGINT NOT NULL COMMENT '目标社团ID',
    application_reason TEXT NOT NULL COMMENT '申请理由',
    relevant_experience TEXT COMMENT '相关经验',
    expected_activity_types TEXT COMMENT '期望参加的活动类型',
    available_time TEXT COMMENT '一般可参与活动的时间',
    portfolio_url VARCHAR (255) COMMENT '作品展示URL',
    contact_phone VARCHAR (20) COMMENT '联系电话',
    contact_email VARCHAR (100) COMMENT '联系邮箱',
    STATUS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '申请状态',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    processed_at TIMESTAMP NULL COMMENT '处理时间',
    processed_by BIGINT NULL COMMENT '处理者ID (社团管理员ID)',
    processed_note TEXT COMMENT '处理备注',
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES club_admins (id) ON DELETE
    SET NULL,
    INDEX idx_student_id (student_id),
    INDEX idx_club_id (club_id),
    INDEX idx_status (STATUS),
    INDEX idx_applied_at (applied_at)
  ) COMMENT '申请加入社团表';
  
  -- =====================================================
  -- 9. 活动信息表
  -- =====================================================
  CREATE TABLE activities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    club_id BIGINT NOT NULL COMMENT '所属社团ID',
    title VARCHAR (200) NOT NULL COMMENT '活动标题',
    description TEXT COMMENT '活动描述',
    activity_type ENUM ('MEETING', 'COMPETITION', 'WORKSHOP', 'EXCURSION', 'SOCIAL', 'OTHERS') NOT NULL COMMENT '活动类型',
    start_time TIMESTAMP NOT NULL COMMENT '开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '结束时间',
    registration_deadline TIMESTAMP COMMENT '报名截止时间',
    location VARCHAR (200) COMMENT '活动地点',
    max_participants INT COMMENT '最大参与人数',
    current_participant_count INT DEFAULT 0 COMMENT '当前参与人数',
    required_points INT DEFAULT 0 COMMENT '所需积分',
    reward_points INT DEFAULT 0 COMMENT '奖励积分',
    registration_fee DECIMAL (10, 2) DEFAULT 0.00 COMMENT '报名费用',
    requirements TEXT COMMENT '参与要求',
    materials_needed TEXT COMMENT '需要准备的材料',
    contact_info VARCHAR (200) COMMENT '联系信息',
    poster_url VARCHAR (255) COMMENT '活动海报URL',
    STATUS ENUM ('DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED') DEFAULT 'DRAFT' COMMENT '活动状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT NOT NULL COMMENT '创建者ID (社团管理员ID)',
    approved_by BIGINT NULL COMMENT '审批者ID (学校管理员ID)',
    approved_at TIMESTAMP NULL COMMENT '审批时间',
    FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES club_admins (id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES school_admins (id) ON DELETE
    SET NULL,
    INDEX idx_club_id (club_id),
    INDEX idx_status (STATUS),
    INDEX idx_activity_type (activity_type),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_registration_deadline (registration_deadline)
  ) COMMENT '活动信息表';
  -- =====================================================
  -- 10. 活动报名申请表
  -- =====================================================
  CREATE TABLE activity_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    student_id BIGINT NOT NULL COMMENT '申请学生ID',
    application_reason TEXT COMMENT '申请理由',
    special_requirements TEXT COMMENT '特殊需求',
    emergency_contact VARCHAR (100) COMMENT '紧急联系人',
    emergency_phone VARCHAR (20) COMMENT '紧急联系电话',
    STATUS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'ATTENDED', 'NO_SHOW') DEFAULT 'PENDING' COMMENT '申请状态',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    processed_at TIMESTAMP NULL COMMENT '处理时间',
    processed_by BIGINT NULL COMMENT '处理者ID (社团管理员ID)',
    processed_note TEXT COMMENT '处理备注',
    points_earned INT DEFAULT 0 COMMENT '获得积分',
    attended_at TIMESTAMP NULL COMMENT '实际参与时间',
    FOREIGN KEY (activity_id) REFERENCES activities (id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES club_admins (id) ON DELETE
    SET NULL,
    UNIQUE KEY unique_application (activity_id, student_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (STATUS),
    INDEX idx_applied_at (applied_at)
  ) COMMENT '活动报名申请表';
  -- =====================================================
  -- 11. 用户活动积分记录表
  -- =====================================================
  CREATE TABLE user_activity_scores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    activity_id BIGINT NULL COMMENT '活动ID (可能通过其他方式获得积分)',
    club_id BIGINT NULL COMMENT '社团ID',
    score_type ENUM ('ACTIVITY_PARTICIPATION', 'ACTIVITY_WINNING', 'CLUB_CONTRIBUTION', 'SYSTEM_REWARD', 'MANUAL_ADJUST') NOT NULL COMMENT '积分类型',
    points INT NOT NULL COMMENT '积分数量',
    description VARCHAR (255) COMMENT '积分描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    created_by BIGINT NULL COMMENT '操作者ID',
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activities (id) ON DELETE
    SET NULL,
    FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE
    SET NULL,
    FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE
    SET NULL,
    INDEX idx_student_id (student_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_score_type (score_type),
    INDEX idx_created_at (created_at)
  ) COMMENT '用户活动积分记录表';
  -- =====================================================
  -- 12. 系统日志表
  -- =====================================================
  CREATE TABLE system_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NULL COMMENT '操作用户ID',
    action VARCHAR (100) NOT NULL COMMENT '操作类型',
    target_type VARCHAR (50) COMMENT '操作目标类型',
    target_id BIGINT COMMENT '操作目标ID',
    description TEXT COMMENT '操作描述',
    ip_address VARCHAR (45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE
    SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_target_type (target_type),
    INDEX idx_created_at (created_at)
  ) COMMENT '系统日志表';
  -- =====================================================
  -- 13. 通知消息表
  -- =====================================================
  CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR (200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    type ENUM ('SYSTEM', 'APPLICATION_RESULT', 'ACTIVITY_REMINDER', 'CLUB_NEWS', 'ANNOUNCEMENT') NOT NULL COMMENT '通知类型',
    related_type VARCHAR (50) COMMENT '关联对象类型',
    related_id BIGINT COMMENT '关联对象ID',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_at TIMESTAMP NULL COMMENT '阅读时间',
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
  ) COMMENT '通知消息表';
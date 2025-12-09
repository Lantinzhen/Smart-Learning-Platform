-- 学校社团管理系统数据库设计
-- 创建数据库
CREATE DATABASE IF NOT EXISTS school_club_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE school_club_management;

-- 1. 学生表
CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    major VARCHAR(100) COMMENT '专业',
    grade VARCHAR(20) COMMENT '年级',
    enrollment_year INT COMMENT '入学年份',
    gender ENUM('男', '女', '其他') COMMENT '性别',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用'
) COMMENT '学生表';

-- 2. 社团管理员表
CREATE TABLE club_admins (
    admin_id VARCHAR(20) PRIMARY KEY COMMENT '管理员ID',
    club_id INT NOT NULL COMMENT '所属社团ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用'
) COMMENT '社团管理员表';

-- 3. 学校管理员表
CREATE TABLE school_admins (
    admin_id VARCHAR(20) PRIMARY KEY COMMENT '管理员ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    role ENUM('超级管理员', '普通管理员') DEFAULT '普通管理员' COMMENT '角色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用'
) COMMENT '学校管理员表';

-- 4. 社团分类表
CREATE TABLE club_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '社团分类表';

-- 5. 社团信息表
CREATE TABLE clubs (
    club_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '社团ID',
    category_id INT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '社团名称',
    description TEXT COMMENT '社团描述',
    logo_url VARCHAR(255) COMMENT '社团Logo URL',
    president_student_id VARCHAR(20) COMMENT '社长学号',
    foundation_date DATE COMMENT '成立日期',
    member_count INT DEFAULT 0 COMMENT '成员数量',
    max_members INT DEFAULT 100 COMMENT '最大成员数',
    contact_info VARCHAR(255) COMMENT '联系方式',
    meeting_time VARCHAR(100) COMMENT '常规会议时间',
    meeting_location VARCHAR(100) COMMENT '常规会议地点',
    recruitment_status TINYINT DEFAULT 1 COMMENT '招新状态：1-开放，0-关闭',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-解散',
    
    FOREIGN KEY (category_id) REFERENCES club_categories(category_id),
    FOREIGN KEY (president_student_id) REFERENCES students(student_id)
) COMMENT '社团信息表';

-- 6. 社团成员关系表
CREATE TABLE club_members (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '关系ID',
    club_id INT NOT NULL COMMENT '社团ID',
    student_id VARCHAR(20) NOT NULL COMMENT '学号',
    join_date DATE NOT NULL COMMENT '加入日期',
    role ENUM('普通成员', '副社长', '社长') DEFAULT '普通成员' COMMENT '角色',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-已退出',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE KEY unique_member (club_id, student_id),
    FOREIGN KEY (club_id) REFERENCES clubs(club_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
) COMMENT '社团成员关系表';

-- 7. 社团申请表
CREATE TABLE club_applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    student_id VARCHAR(20) NOT NULL COMMENT '申请人学号',
    club_id INT NOT NULL COMMENT '申请社团ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    major VARCHAR(100) COMMENT '专业',
    grade VARCHAR(20) COMMENT '年级',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    reason TEXT COMMENT '申请理由',
    experience TEXT COMMENT '相关经验',
    activity_preference TEXT COMMENT '期望参加的活动类型',
    available_time TEXT COMMENT '一般可参与活动的时间',
    portfolio VARCHAR(255) COMMENT '作品展示链接',
    status ENUM('待审核', '通过', '拒绝') DEFAULT '待审核' COMMENT '申请状态',
    reviewed_by VARCHAR(20) COMMENT '审核人ID',
    reviewed_at TIMESTAMP NULL COMMENT '审核时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (club_id) REFERENCES clubs(club_id),
    FOREIGN KEY (reviewed_by) REFERENCES club_admins(admin_id)
) COMMENT '社团申请表';

-- 8. 活动分类表
CREATE TABLE activity_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '活动分类表';

-- 9. 活动信息表
CREATE TABLE activities (
    activity_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '活动ID',
    club_id INT NOT NULL COMMENT '主办社团ID',
    category_id INT NOT NULL COMMENT '分类ID',
    title VARCHAR(100) NOT NULL COMMENT '活动标题',
    description TEXT COMMENT '活动描述',
    poster_url VARCHAR(255) COMMENT '海报图片URL',
    location VARCHAR(100) NOT NULL COMMENT '活动地点',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    max_participants INT DEFAULT 100 COMMENT '最大参与人数',
    registered_count INT DEFAULT 0 COMMENT '已报名人数',
    points DECIMAL(5,2) DEFAULT 0.00 COMMENT '活动积分',
    registration_deadline DATETIME NOT NULL COMMENT '报名截止时间',
    status ENUM('草稿', '待审批', '已批准', '已拒绝', '进行中', '已结束', '已取消') DEFAULT '草稿' COMMENT '活动状态',
    approved_by VARCHAR(20) COMMENT '审批人ID',
    approved_at TIMESTAMP NULL COMMENT '审批时间',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (club_id) REFERENCES clubs(club_id),
    FOREIGN KEY (category_id) REFERENCES activity_categories(category_id),
    FOREIGN KEY (approved_by) REFERENCES school_admins(admin_id),
    FOREIGN KEY (created_by) REFERENCES club_admins(admin_id)
) COMMENT '活动信息表';

-- 10. 活动报名表
CREATE TABLE activity_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '报名ID',
    activity_id INT NOT NULL COMMENT '活动ID',
    student_id VARCHAR(20) NOT NULL COMMENT '报名人学号',
    registration_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    status ENUM('已报名', '已取消', '已参加', '未参加') DEFAULT '已报名' COMMENT '报名状态',
    cancellation_reason TEXT COMMENT '取消原因',
    attended TINYINT DEFAULT 0 COMMENT '是否参加：0-未参加，1-已参加',
    points_earned DECIMAL(5,2) DEFAULT 0.00 COMMENT '获得积分数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE KEY unique_registration (activity_id, student_id),
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
) COMMENT '活动报名表';

-- 11. 活动审批记录表
CREATE TABLE activity_approvals (
    approval_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '审批ID',
    activity_id INT NOT NULL COMMENT '活动ID',
    approver_id VARCHAR(20) NOT NULL COMMENT '审批人ID',
    status ENUM('待审批', '已批准', '已拒绝') NOT NULL COMMENT '审批结果',
    comments TEXT COMMENT '审批意见',
    approved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id),
    FOREIGN KEY (approver_id) REFERENCES school_admins(admin_id)
) COMMENT '活动审批记录表';

-- 12. 学生活动积分表
CREATE TABLE student_points (
    point_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '积分记录ID',
    student_id VARCHAR(20) NOT NULL COMMENT '学号',
    activity_id INT NOT NULL COMMENT '活动ID',
    points DECIMAL(5,2) NOT NULL COMMENT '积分数',
    reason VARCHAR(100) COMMENT '积分原因',
    awarded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '授予时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id)
) COMMENT '学生活动积分表';

-- 13. 帮助文档表
CREATE TABLE help_documents (
    document_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '文档ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    category VARCHAR(50) COMMENT '分类',
    author_id VARCHAR(20) COMMENT '作者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-发布，0-草稿'
) COMMENT '帮助文档表';

-- 14. 常见问题表
CREATE TABLE faqs (
    faq_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'FAQ ID',
    question TEXT NOT NULL COMMENT '问题',
    answer TEXT NOT NULL COMMENT '答案',
    category VARCHAR(50) COMMENT '分类',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用'
) COMMENT '常见问题表';

-- 15. 系统日志表
CREATE TABLE system_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id VARCHAR(20) COMMENT '用户ID',
    user_type ENUM('学生', '社团管理员', '学校管理员') COMMENT '用户类型',
    action VARCHAR(100) NOT NULL COMMENT '操作',
    description TEXT COMMENT '详细描述',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '系统日志表';

-- 创建索引以提高查询性能
CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_students_major ON students(major);
CREATE INDEX idx_clubs_category ON clubs(category_id);
CREATE INDEX idx_clubs_president ON clubs(president_student_id);
CREATE INDEX idx_club_members_student ON club_members(student_id);
CREATE INDEX idx_club_members_club ON club_members(club_id);
CREATE INDEX idx_club_applications_student ON club_applications(student_id);
CREATE INDEX idx_club_applications_club ON club_applications(club_id);
CREATE INDEX idx_activities_club ON activities(club_id);
CREATE INDEX idx_activities_category ON activities(category_id);
CREATE INDEX idx_activities_time ON activities(start_time, end_time);
CREATE INDEX idx_activity_registrations_student ON activity_registrations(student_id);
CREATE INDEX idx_activity_registrations_activity ON activity_registrations(activity_id);
CREATE INDEX idx_student_points_student ON student_points(student_id);
CREATE INDEX idx_student_points_activity ON student_points(activity_id);

-- 插入基础数据
-- 插入社团分类
INSERT INTO club_categories (name, description) VALUES
('学术科技类', '以学术研究、科技创新为主的社团'),
('文化艺术类', '以文化传承、艺术表演为主的社团'),
('体育健身类', '以体育运动、健身锻炼为主的社团'),
('公益服务类', '以志愿服务、公益活动为主的社团'),
('兴趣爱好类', '以兴趣爱好交流为主的社团');

-- 插入活动分类
INSERT INTO activity_categories (name, description) VALUES
('讲座论坛', '学术讲座、专题论坛等活动'),
('文艺演出', '音乐会、话剧表演等文艺活动'),
('体育竞赛', '各类体育比赛和竞技活动'),
('志愿服务', '社区服务、公益志愿活动'),
('技能培训', '各类技能培训班和工作坊'),
('展览展示', '作品展、成果展等展示活动');

-- 创建视图
-- 学生参与活动统计视图
CREATE VIEW student_activity_stats AS
SELECT 
    s.student_id,
    s.name,
    COUNT(ar.activity_id) as total_activities,
    SUM(CASE WHEN ar.status = '已参加' THEN 1 ELSE 0 END) as attended_activities,
    SUM(ar.points_earned) as total_points
FROM students s
LEFT JOIN activity_registrations ar ON s.student_id = ar.student_id
GROUP BY s.student_id, s.name;

-- 社团活跃度统计视图
CREATE VIEW club_activity_stats AS
SELECT 
    c.club_id,
    c.name,
    COUNT(a.activity_id) as total_activities,
    SUM(a.registered_count) as total_participants,
    AVG(a.points) as avg_points
FROM clubs c
LEFT JOIN activities a ON c.club_id = a.club_id
GROUP BY c.club_id, c.name;

-- 活动参与统计视图
CREATE VIEW activity_participation_stats AS
SELECT 
    a.activity_id,
    a.title,
    a.max_participants,
    a.registered_count,
    ROUND((a.registered_count / a.max_participants) * 100, 2) as participation_rate
FROM activities a
WHERE a.status IN ('已批准', '进行中', '已结束');

-- 修复外键约束问题的完整脚本

-- 简单而可靠的方法：先禁用外键检查，然后进行修改
SET FOREIGN_KEY_CHECKS = 0;

-- 修改 activity_registrations 表中的 student_id 列
ALTER TABLE activity_registrations MODIFY COLUMN student_id VARCHAR(20) NOT NULL;

-- 修改 student_points 表中的 student_id 列
ALTER TABLE student_points MODIFY COLUMN student_id VARCHAR(20) NOT NULL;

-- 修改 club_members 表中的 student_id 列
ALTER TABLE club_members MODIFY COLUMN student_id VARCHAR(20) NOT NULL;

-- 修改 clubs 表中的 president_student_id 列
ALTER TABLE clubs MODIFY COLUMN president_student_id VARCHAR(20);

-- 创建一个存储过程来安全地删除和添加外键约束
DELIMITER $$
CREATE PROCEDURE safe_add_foreign_key()
BEGIN
  -- 处理重复外键约束的情况
  DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN END;
  
  -- 尝试删除可能已存在的外键约束
  ALTER TABLE activity_registrations DROP FOREIGN KEY activity_registrations_ibfk_2;
  ALTER TABLE student_points DROP FOREIGN KEY student_points_ibfk_1;
  ALTER TABLE club_members DROP FOREIGN KEY club_members_ibfk_2;
  ALTER TABLE clubs DROP FOREIGN KEY clubs_ibfk_2;
  
  -- 启用外键检查
  SET FOREIGN_KEY_CHECKS = 1;
  
  -- 添加外键约束
  ALTER TABLE activity_registrations 
  ADD CONSTRAINT activity_registrations_ibfk_2 
  FOREIGN KEY (student_id) REFERENCES students(student_id);
  
  ALTER TABLE student_points 
  ADD CONSTRAINT student_points_ibfk_1 
  FOREIGN KEY (student_id) REFERENCES students(student_id);
  
  ALTER TABLE club_members 
  ADD CONSTRAINT club_members_ibfk_2 
  FOREIGN KEY (student_id) REFERENCES students(student_id);
  
  ALTER TABLE clubs 
  ADD CONSTRAINT clubs_ibfk_2 
  FOREIGN KEY (president_student_id) REFERENCES students(student_id);
END $$
DELIMITER ;

-- 调用存储过程
CALL safe_add_foreign_key();

-- 删除存储过程
DROP PROCEDURE safe_add_foreign_key;
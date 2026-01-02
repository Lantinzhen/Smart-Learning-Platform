/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : school_club_management

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 10/12/2025 12:39:59
*/

CREATE DATABASE IF NOT EXISTS school_club_management;
USE school_club_management;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activities
-- ----------------------------
DROP TABLE IF EXISTS `activities`;
CREATE TABLE `activities`  (
                               `activity_id` int NOT NULL AUTO_INCREMENT COMMENT '活动ID',
                               `club_id` int NOT NULL COMMENT '主办社团ID',
                               `category_id` int NOT NULL COMMENT '分类ID',
                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                               `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                               `poster_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '海报图片URL',
                               `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                               `start_time` datetime NOT NULL COMMENT '开始时间',
                               `end_time` datetime NOT NULL COMMENT '结束时间',
                               `max_participants` int NULL DEFAULT 100 COMMENT '最大参与人数',
                               `registered_count` int NULL DEFAULT 0 COMMENT '已报名人数',
                               `points` double NULL DEFAULT NULL,
                               `registration_deadline` datetime NOT NULL COMMENT '报名截止时间',
                               `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                               `approved_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批人ID',
                               `approved_at` timestamp NULL DEFAULT NULL COMMENT '审批时间',
                               `created_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人ID',
                               `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`activity_id`) USING BTREE,
                               INDEX `approved_by`(`approved_by` ASC) USING BTREE,
                               INDEX `created_by`(`created_by` ASC) USING BTREE,
                               INDEX `idx_activities_club`(`club_id` ASC) USING BTREE,
                               INDEX `idx_activities_category`(`category_id` ASC) USING BTREE,
                               INDEX `idx_activities_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
                               CONSTRAINT `activities_ibfk_1` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`club_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                               CONSTRAINT `activities_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `activity_categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                               CONSTRAINT `activities_ibfk_3` FOREIGN KEY (`approved_by`) REFERENCES `school_admins` (`admin_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                               CONSTRAINT `activities_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `club_admins` (`admin_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activities
-- ----------------------------
INSERT INTO `activities` VALUES (1, 1, 1, 'Java编程入门讲座', '面向初学者的Java编程入门讲座，讲解基础语法和面向对象编程概念', 'https://example.com/poster/java_lecture.jpg', '实验楼A201', '2025-11-25 14:00:00', '2026-01-01 16:00:00', 50, 25, 2, '2025-12-11 23:59:59', '已批准', 'SA001', NULL, 'CA001', '2023-11-01 09:00:00', '2025-11-24 20:17:54');
INSERT INTO `activities` VALUES (2, 1, 1, 'Python数据分析工作坊', '学习如何使用Python进行数据分析，包括pandas和matplotlib库的使用', 'https://example.com/poster/python_workshop.jpg', '实验楼B301', '2025-11-25 10:00:00', '2026-06-10 12:00:00', 30, 16, 3, '2025-12-24 23:59:59', '已批准', 'SA001', NULL, 'CA001', '2023-11-05 14:30:00', '2025-11-25 16:59:50');
INSERT INTO `activities` VALUES (3, 2, 2, '古典音乐会', '由音乐社成员演奏的经典古典音乐作品', 'https://example.com/poster/classical_concert.jpg', '艺术中心音乐厅', '2023-11-25 19:00:00', '2023-11-25 21:00:00', 100, 80, 1.5, '2023-11-20 23:59:59', '已批准', 'SA001', NULL, 'CA002', '2023-11-02 11:00:00', '2025-11-24 10:53:41');
INSERT INTO `activities` VALUES (4, 3, 3, '新生篮球友谊赛', '欢迎新生参加的篮球友谊赛，增进同学间的友谊', 'https://example.com/poster/basketball_match.jpg', '体育馆篮球场', '2023-11-18 15:00:00', '2023-11-18 17:00:00', 20, 16, 1, '2023-11-13 23:59:59', '已批准', 'SA001', NULL, 'CA003', '2023-11-03 16:45:00', '2025-11-24 10:53:41');
INSERT INTO `activities` VALUES (5, 4, 4, '校园清洁志愿活动', '组织志愿者清理校园垃圾，维护美丽校园环境', 'https://example.com/poster/cleaning_volunteer.jpg', '全校范围', '2023-11-12 09:00:00', '2023-11-12 11:00:00', 50, 35, 2.5, '2023-11-07 23:59:59', '已批准', 'SA001', NULL, 'CA004', '2023-11-01 13:20:00', '2025-11-24 10:53:41');
INSERT INTO `activities` VALUES (6, 5, 6, '秋季摄影作品展', '展示摄影社成员秋季拍摄的优秀作品', 'https://example.com/poster/photo_exhibition.jpg', '艺术楼一楼展厅', '2023-11-30 10:00:00', '2023-11-30 17:00:00', 200, 0, 0, '2023-11-25 23:59:59', '已批准', 'SA001', '2025-11-25 19:40:01', 'CA005', '2023-11-06 08:30:00', '2025-11-25 19:40:01');
INSERT INTO `activities` VALUES (10, 1, 1, '更新后的编程竞赛讲座', '更新后的详细介绍...', 'http://example.com/poster.png', '教学楼B301', '2023-10-15 14:00:00', '2023-10-15 16:00:00', 200, 0, 2, '2023-10-14 23:59:59', '已批准', 'SA001', '2025-11-25 17:35:20', 'CA001', '2025-11-25 16:34:43', '2025-11-25 17:35:20');
INSERT INTO `activities` VALUES (11, 2, 3, '游戏', '比赛方式佛奥数据发送i福建师范', 'https://www.kimi.com/', 'B207', '2025-12-17 08:00:00', '2025-12-31 08:00:00', 100, 0, 2, '2025-12-12 08:00:00', '待审批', NULL, NULL, 'CA002', '2025-12-08 15:45:56', '2025-12-08 15:46:47');

-- ----------------------------
-- Table structure for activity_approvals
-- ----------------------------
DROP TABLE IF EXISTS `activity_approvals`;
CREATE TABLE `activity_approvals`  (
                                       `approval_id` int NOT NULL AUTO_INCREMENT COMMENT '审批ID',
                                       `activity_id` int NOT NULL COMMENT '活动ID',
                                       `approver_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '审批人ID',
                                       `status` enum('待审批','已批准','已拒绝') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '审批结果',
                                       `comments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审批意见',
                                       `approved_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
                                       `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       PRIMARY KEY (`approval_id`) USING BTREE,
                                       INDEX `activity_id`(`activity_id` ASC) USING BTREE,
                                       INDEX `approver_id`(`approver_id` ASC) USING BTREE,
                                       CONSTRAINT `activity_approvals_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`activity_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `activity_approvals_ibfk_2` FOREIGN KEY (`approver_id`) REFERENCES `school_admins` (`admin_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动审批记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_approvals
-- ----------------------------
INSERT INTO `activity_approvals` VALUES (1, 1, 'SA001', '已批准', '活动内容符合要求，场地安排合理', '2023-11-02 15:30:00', '2023-11-02 15:30:00');
INSERT INTO `activity_approvals` VALUES (2, 2, 'SA001', '已批准', '工作坊主题有教育意义，讲师资质符合要求', '2023-11-06 10:15:00', '2023-11-06 10:15:00');
INSERT INTO `activity_approvals` VALUES (3, 3, 'SA001', '已批准', '音乐会曲目安排合理，符合校园文化氛围', '2023-11-03 14:20:00', '2023-11-03 14:20:00');
INSERT INTO `activity_approvals` VALUES (4, 4, 'SA001', '已批准', '友谊赛有助于新生融入校园生活，安全保障措施到位', '2023-11-04 09:45:00', '2023-11-04 09:45:00');
INSERT INTO `activity_approvals` VALUES (5, 5, 'SA001', '已批准', '志愿活动有助于校园环境建设，组织方案详细', '2023-11-02 16:10:00', '2023-11-02 16:10:00');
INSERT INTO `activity_approvals` VALUES (6, 6, 'SA001', '已批准', '活动内容符合要求，批准举办。', '2025-11-25 19:40:01', '2023-11-06 08:30:00');
INSERT INTO `activity_approvals` VALUES (7, 10, 'SA001', '待审批', '活动内容符合要求，批准举办。', '2025-11-25 17:35:20', '2025-11-25 16:41:58');
INSERT INTO `activity_approvals` VALUES (8, 11, 'SA001', '待审批', NULL, NULL, '2025-12-08 15:46:47');

-- ----------------------------
-- Table structure for activity_categories
-- ----------------------------
DROP TABLE IF EXISTS `activity_categories`;
CREATE TABLE `activity_categories`  (
                                        `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                        `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
                                        `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '分类描述',
                                        `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`category_id`) USING BTREE,
                                        UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_categories
-- ----------------------------
INSERT INTO `activity_categories` VALUES (1, '讲座论坛', '学术讲座、专题论坛等活动', '2025-11-23 20:25:16');
INSERT INTO `activity_categories` VALUES (2, '文艺演出', '音乐会、话剧表演等文艺活动', '2025-11-23 20:25:16');
INSERT INTO `activity_categories` VALUES (3, '体育竞赛', '各类体育比赛和竞技活动', '2025-11-23 20:25:16');
INSERT INTO `activity_categories` VALUES (4, '志愿服务', '社区服务、公益志愿活动', '2025-11-23 20:25:16');
INSERT INTO `activity_categories` VALUES (5, '技能培训', '各类技能培训班和工作坊', '2025-11-23 20:25:16');
INSERT INTO `activity_categories` VALUES (6, '展览展示', '作品展、成果展等展示活动', '2025-11-23 20:25:16');

-- ----------------------------
-- Table structure for activity_registrations
-- ----------------------------
DROP TABLE IF EXISTS `activity_registrations`;
CREATE TABLE `activity_registrations`  (
                                           `registration_id` int NOT NULL AUTO_INCREMENT COMMENT '报名ID',
                                           `activity_id` int NOT NULL COMMENT '活动ID',
                                           `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                           `registration_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
                                           `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                           `cancellation_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '取消原因',
                                           `attended` int NULL DEFAULT NULL,
                                           `points_earned` double NULL DEFAULT NULL,
                                           `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           PRIMARY KEY (`registration_id`) USING BTREE,
                                           UNIQUE INDEX `unique_registration`(`activity_id` ASC, `student_id` ASC) USING BTREE,
                                           INDEX `idx_activity_registrations_student`(`student_id` ASC) USING BTREE,
                                           INDEX `idx_activity_registrations_activity`(`activity_id` ASC) USING BTREE,
                                           CONSTRAINT `activity_registrations_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`activity_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                           CONSTRAINT `activity_registrations_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_registrations
-- ----------------------------
INSERT INTO `activity_registrations` VALUES (1, 1, '2021006', '2023-11-02 10:30:00', '已参加', NULL, 1, 2, '2023-11-02 10:30:00');
INSERT INTO `activity_registrations` VALUES (2, 1, '2021007', '2023-11-02 11:15:00', '已参加', NULL, 1, 2, '2023-11-02 11:15:00');
INSERT INTO `activity_registrations` VALUES (3, 1, '2022001', '2023-11-03 09:45:00', '已参加', NULL, 1, 2, '2023-11-03 09:45:00');
INSERT INTO `activity_registrations` VALUES (4, 1, '2022002', '2023-11-03 14:20:00', '已参加', NULL, 1, 2, '2023-11-03 14:20:00');
INSERT INTO `activity_registrations` VALUES (5, 1, '2021008', '2023-11-04 16:10:00', '已取消', '临时有事，无法参加', 0, 0, '2023-11-04 16:10:00');
INSERT INTO `activity_registrations` VALUES (7, 2, '2021006', '2023-11-06 15:30:00', '已参加', NULL, 1, 3, '2023-11-06 15:30:00');
INSERT INTO `activity_registrations` VALUES (8, 2, '2021007', '2023-11-07 10:20:00', '已参加', NULL, 1, 3, '2023-11-07 10:20:00');
INSERT INTO `activity_registrations` VALUES (9, 3, '2021002', '2023-11-03 11:00:00', '已参加', NULL, 1, 1.5, '2023-11-03 11:00:00');
INSERT INTO `activity_registrations` VALUES (10, 3, '2021008', '2023-11-04 09:30:00', '已参加', NULL, 1, 1.5, '2023-11-04 09:30:00');
INSERT INTO `activity_registrations` VALUES (11, 3, '2022001', '2023-11-04 14:15:00', '已参加', NULL, 1, 1.5, '2023-11-04 14:15:00');
INSERT INTO `activity_registrations` VALUES (12, 4, '2021003', '2023-11-05 16:45:00', '已参加', NULL, 1, 1, '2023-11-05 16:45:00');
INSERT INTO `activity_registrations` VALUES (13, 4, '2021004', '2023-11-05 17:20:00', '已参加', NULL, 1, 1, '2023-11-05 17:20:00');
INSERT INTO `activity_registrations` VALUES (14, 4, '2022002', '2023-11-06 08:30:00', '已参加', NULL, 1, 1, '2023-11-06 08:30:00');
INSERT INTO `activity_registrations` VALUES (15, 5, '2021004', '2023-11-02 10:15:00', '已参加', NULL, 1, 2.5, '2023-11-02 10:15:00');
INSERT INTO `activity_registrations` VALUES (16, 5, '2021005', '2023-11-02 11:30:00', '已参加', NULL, 1, 2.5, '2023-11-02 11:30:00');
INSERT INTO `activity_registrations` VALUES (17, 5, '2021006', '2023-11-03 13:45:00', '已参加', NULL, 1, 2.5, '2023-11-03 13:45:00');
INSERT INTO `activity_registrations` VALUES (18, 5, '2021007', '2023-11-03 15:20:00', '已参加', NULL, 1, 2.5, '2023-11-03 15:20:00');
INSERT INTO `activity_registrations` VALUES (19, 5, '2022001', '2023-11-04 09:10:00', '已参加', NULL, 1, 2.5, '2023-11-04 09:10:00');
INSERT INTO `activity_registrations` VALUES (21, 2, '2021001', '2025-11-25 17:02:05', '已参加', NULL, 1, 2.5, '2025-11-25 17:02:05');
INSERT INTO `activity_registrations` VALUES (22, 3, '2022004', '2025-12-09 15:50:24', '已参加', NULL, 1, 1.5, '2025-12-12 15:50:47');

-- ----------------------------
-- Table structure for club_admins
-- ----------------------------
DROP TABLE IF EXISTS `club_admins`;
CREATE TABLE `club_admins`  (
                                `admin_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理员ID',
                                `club_id` int NOT NULL COMMENT '所属社团ID',
                                `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
                                `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
                                `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                                `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                                `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
                                `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
                                `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `status` int NOT NULL,
                                PRIMARY KEY (`admin_id`) USING BTREE,
                                UNIQUE INDEX `username`(`username` ASC) USING BTREE,
                                UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社团管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of club_admins
-- ----------------------------
INSERT INTO `club_admins` VALUES ('CA001', 1, '李四', 'club_admin', '$2a$10$kcO8sSEYiANHs5VTzxHoPuAdNI9XrLA8.3o02VYEUC9ag3JOuBwOC', 'lisi@example.com', '13900139000', 'https://example.com/avatar/clubadminA.jpg', '2025-11-24 10:51:13', '2025-11-25 19:49:06', 0);
INSERT INTO `club_admins` VALUES ('CA002', 2, '社团管理员B', 'club_admin_b', '12345678', 'clubadminB@example.com', '13900139002', 'https://example.com/avatar/clubadminB.jpg', '2025-11-24 10:51:13', '2025-11-24 11:03:52', 1);
INSERT INTO `club_admins` VALUES ('CA003', 3, '社团管理员C', 'club_admin_c', '12345678', 'clubadminC@example.com', '13900139003', 'https://example.com/avatar/clubadminC.jpg', '2025-11-24 10:51:13', '2025-11-24 11:03:54', 1);
INSERT INTO `club_admins` VALUES ('CA004', 4, '社团管理员D', 'club_admin_d', '12345678', 'clubadminD@example.com', '13900139004', 'https://example.com/avatar/clubadminD.jpg', '2025-11-24 10:51:13', '2025-11-24 11:03:55', 1);
INSERT INTO `club_admins` VALUES ('CA005', 5, '社团管理员E', 'club_admin_e', '12345678', 'clubadminE@example.com', '13900139005', 'https://example.com/avatar/clubadminE.jpg', '2025-11-24 10:51:13', '2025-11-24 11:12:07', 1);

-- ----------------------------
-- Table structure for club_applications
-- ----------------------------
DROP TABLE IF EXISTS `club_applications`;
CREATE TABLE `club_applications`  (
                                      `application_id` int NOT NULL AUTO_INCREMENT COMMENT '申请ID',
                                      `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请人学号',
                                      `club_id` int NOT NULL COMMENT '申请社团ID',
                                      `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
                                      `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业',
                                      `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '年级',
                                      `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
                                      `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                                      `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '申请理由',
                                      `experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '相关经验',
                                      `activity_preference` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '期望参加的活动类型',
                                      `available_time` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '一般可参与活动的时间',
                                      `portfolio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作品展示链接',
                                      `status` enum('待审核','通过','拒绝') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '待审核' COMMENT '申请状态',
                                      `reviewed_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人ID',
                                      `reviewed_at` timestamp NULL DEFAULT NULL COMMENT '审核时间',
                                      `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                                      PRIMARY KEY (`application_id`) USING BTREE,
                                      INDEX `reviewed_by`(`reviewed_by` ASC) USING BTREE,
                                      INDEX `idx_club_applications_student`(`student_id` ASC) USING BTREE,
                                      INDEX `idx_club_applications_club`(`club_id` ASC) USING BTREE,
                                      CONSTRAINT `club_applications_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                      CONSTRAINT `club_applications_ibfk_2` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`club_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                      CONSTRAINT `club_applications_ibfk_3` FOREIGN KEY (`reviewed_by`) REFERENCES `club_admins` (`admin_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社团申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of club_applications
-- ----------------------------
INSERT INTO `club_applications` VALUES (1, '2022001', 1, '郑一', '软件工程', '2022级', '13800138009', 'zhengyi@example.com', '对计算机技术很感兴趣，希望能在实践中提升自己的编程能力', '参加过学校组织的编程竞赛，获得过三等奖', '技术讲座、项目实战', '周一、三、五晚上', 'https://github.com/zhengyi', '通过', 'CA001', '2023-10-15 10:30:00', '2023-10-10 14:20:00');
INSERT INTO `club_applications` VALUES (2, '2022002', 2, '王二', '信息安全', '2022级', '13800138010', 'wanger@example.com', '热爱音乐，会弹吉他，希望能和更多志同道合的朋友一起交流', '高中时期是学校乐队成员，有一定的音乐基础', '音乐演出、乐器交流', '周二、四晚上', NULL, '通过', 'CA002', '2025-12-08 15:43:01', '2023-10-12 09:15:00');
INSERT INTO `club_applications` VALUES (3, '2021006', 3, '孙八', '物联网工程', '2021级', '13800138006', 'sunba@example.com', '喜欢运动，特别是篮球，希望能通过社团活动保持身体健康', '校队篮球替补队员', '篮球比赛、体能训练', '周二、四下午', NULL, '通过', 'CA003', '2023-10-20 16:45:00', '2023-10-15 11:30:00');
INSERT INTO `club_applications` VALUES (4, '2021007', 4, '周九', '网络工程', '2021级', '13800138007', 'zhoujiu@example.com', '热心公益，希望能通过志愿服务帮助更多的人', '参加过社区组织的志愿活动', '社区服务、公益活动', '周末', NULL, '拒绝', 'CA004', '2023-10-18 09:20:00', '2023-10-13 15:40:00');
INSERT INTO `club_applications` VALUES (5, '2021008', 5, '吴十', '计算机科学与技术', '2021级', '13800138008', 'wushi@example.com', '喜欢摄影，希望能通过社团活动提升摄影技巧', '有一定的摄影基础，有自己的作品集', '外拍活动、摄影技巧交流', '周六', 'https://wushi.photos', '待审核', NULL, NULL, '2023-10-16 13:25:00');
INSERT INTO `club_applications` VALUES (10, '2021005', 1, '钱七', '人工智能', '2021级', '13800138005', 'qianqi@example.com', '我对计算机技术很感兴趣...', '曾参加过校级编程比赛...', '技术讲座、编程竞赛', '周三下午、周末', 'http://github.com/zhangsan', '通过', 'CA001', '2025-11-25 19:55:02', '2025-11-24 16:46:09');

-- ----------------------------
-- Table structure for club_categories
-- ----------------------------
DROP TABLE IF EXISTS `club_categories`;
CREATE TABLE `club_categories`  (
                                    `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    PRIMARY KEY (`category_id`) USING BTREE,
                                    UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社团分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of club_categories
-- ----------------------------
INSERT INTO `club_categories` VALUES (1, '学术科技类', '以学术研究、科技创新为主的社团', '2025-11-23 20:25:16');
INSERT INTO `club_categories` VALUES (2, '文化艺术类', '以文化传承、艺术表演为主的社团', '2025-11-23 20:25:16');
INSERT INTO `club_categories` VALUES (3, '体育健身类', '以体育运动、健身锻炼为主的社团', '2025-11-23 20:25:16');
INSERT INTO `club_categories` VALUES (4, '公益服务类', '以志愿服务、公益活动为主的社团', '2025-11-23 20:25:16');
INSERT INTO `club_categories` VALUES (5, '兴趣爱好类', '以兴趣爱好交流为主的社团', '2025-11-23 20:25:16');

-- ----------------------------
-- Table structure for club_members
-- ----------------------------
DROP TABLE IF EXISTS `club_members`;
CREATE TABLE `club_members`  (
                                 `id` int NOT NULL AUTO_INCREMENT COMMENT '关系ID',
                                 `club_id` int NOT NULL COMMENT '社团ID',
                                 `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `join_date` date NOT NULL COMMENT '加入日期',
                                 `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                 `status` int NULL DEFAULT NULL,
                                 `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE INDEX `unique_member`(`club_id` ASC, `student_id` ASC) USING BTREE,
                                 INDEX `idx_club_members_student`(`student_id` ASC) USING BTREE,
                                 INDEX `idx_club_members_club`(`club_id` ASC) USING BTREE,
                                 CONSTRAINT `club_members_ibfk_1` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`club_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                 CONSTRAINT `club_members_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社团成员关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of club_members
-- ----------------------------
INSERT INTO `club_members` VALUES (2, 1, '2021006', '2021-09-01', '社长', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (3, 1, '2021007', '2021-09-15', '普通成员', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (4, 1, '2022001', '2022-09-01', '普通成员', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (6, 2, '2021002', '2021-09-01', '社长', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (7, 2, '2021008', '2021-09-15', '普通成员', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (8, 3, '2021003', '2021-09-01', '社长', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (9, 3, '2021004', '2021-09-15', '普通成员', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (10, 4, '2021004', '2021-09-01', '社长', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (11, 4, '2021005', '2021-09-15', '普通成员', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (12, 5, '2021005', '2021-09-01', '社长', 1, '2025-11-24 10:53:22');
INSERT INTO `club_members` VALUES (15, 1, '2021005', '2025-11-25', '普通成员', 1, '2025-11-25 15:11:47');
INSERT INTO `club_members` VALUES (17, 2, '2022002', '2025-12-08', '普通成员', 1, '2025-12-08 15:43:01');

-- ----------------------------
-- Table structure for clubs
-- ----------------------------
DROP TABLE IF EXISTS `clubs`;
CREATE TABLE `clubs`  (
                          `club_id` int NOT NULL AUTO_INCREMENT COMMENT '社团ID',
                          `category_id` int NOT NULL COMMENT '分类ID',
                          `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                          `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                          `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '社团Logo URL',
                          `president_student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                          `foundation_date` date NULL DEFAULT NULL COMMENT '成立日期',
                          `member_count` int NULL DEFAULT 0 COMMENT '成员数量',
                          `max_members` int NULL DEFAULT 100 COMMENT '最大成员数',
                          `contact_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
                          `meeting_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '常规会议时间',
                          `meeting_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '常规会议地点',
                          `recruitment_status` tinyint NULL DEFAULT 1 COMMENT '招新状态：1-开放，0-关闭',
                          `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          `status` int NULL DEFAULT NULL,
                          PRIMARY KEY (`club_id`) USING BTREE,
                          UNIQUE INDEX `name`(`name` ASC) USING BTREE,
                          INDEX `idx_clubs_category`(`category_id` ASC) USING BTREE,
                          INDEX `idx_clubs_president`(`president_student_id` ASC) USING BTREE,
                          CONSTRAINT `clubs_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `club_categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                          CONSTRAINT `clubs_ibfk_2` FOREIGN KEY (`president_student_id`) REFERENCES `students` (`student_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社团信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of clubs
-- ----------------------------
INSERT INTO `clubs` VALUES (1, 1, '计算机协会', '计算机爱好者的聚集地...', 'http://example.com/logo.png', '2021001', '2020-09-01', 27, 200, 'computer@example.com', '每周三下午', '实验室A', 1, '2025-11-24 10:52:51', '2025-11-25 19:43:55', 0);
INSERT INTO `clubs` VALUES (2, 2, '音乐社', '热爱音乐的学生聚集地，定期举办音乐会', 'https://example.com/logo/music_club.jpg', '2021002', '2019-09-01', 32, 60, 'music_club@example.com', '每周五晚上18:30-20:30', '艺术楼B201', 1, '2025-11-24 10:52:51', '2025-12-08 15:43:01', 1);
INSERT INTO `clubs` VALUES (3, 3, '篮球社', '喜欢篮球运动的同学的聚集地', 'https://example.com/logo/basketball_club.jpg', '2021003', '2018-09-01', 20, 40, 'basketball_club@example.com', '每周二、四下午17:00-19:00', '体育馆篮球场', 1, '2025-11-24 10:52:51', '2025-11-24 10:52:51', 1);
INSERT INTO `clubs` VALUES (4, 4, '志愿者协会', '组织各类志愿服务活动，传递爱心', 'https://example.com/logo/volunteer_club.jpg', '2021004', '2017-09-01', 40, 100, 'volunteer_club@example.com', '每月第一个周六上午9:00-11:00', '学生活动中心', 1, '2025-11-24 10:52:51', '2025-11-24 10:52:51', 1);
INSERT INTO `clubs` VALUES (5, 5, '摄影社', '分享摄影技巧，组织外拍活动', 'https://example.com/logo/photo_club.jpg', '2021005', '2020-09-01', 15, 30, 'photo_club@example.com', '每周六下午14:00-16:00', '教学楼C301', 1, '2025-11-24 10:52:51', '2025-11-24 10:52:51', 1);

-- ----------------------------
-- Table structure for faqs
-- ----------------------------
DROP TABLE IF EXISTS `faqs`;
CREATE TABLE `faqs`  (
                         `faq_id` int NOT NULL AUTO_INCREMENT COMMENT 'FAQ ID',
                         `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题',
                         `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '答案',
                         `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类',
                         `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
                         PRIMARY KEY (`faq_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '常见问题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of faqs
-- ----------------------------
INSERT INTO `faqs` VALUES (1, '如何修改个人信息？', '登录后进入个人中心，点击编辑按钮即可修改个人信息。', '账户相关', '2023-09-01 12:00:00', '2023-09-01 12:00:00', 1);
INSERT INTO `faqs` VALUES (2, '忘记密码怎么办？', '可以通过邮箱找回密码，点击登录页面的忘记密码链接，按照提示操作即可。', '账户相关', '2023-09-01 12:00:00', '2023-09-01 12:00:00', 1);
INSERT INTO `faqs` VALUES (3, '如何申请创建新社团？', '请联系学校社团管理部门，提交社团成立申请表及相关材料。', '社团相关', '2023-09-01 12:00:00', '2023-09-01 12:00:00', 1);
INSERT INTO `faqs` VALUES (4, '社团活动报名有限制吗？', '每名学生最多可同时参加3个社团，每个活动有人数限制，先到先得。', '活动相关', '2023-09-01 12:00:00', '2023-09-01 12:00:00', 1);
INSERT INTO `faqs` VALUES (5, '如何联系社团管理员？', '可以在社团详情页面找到联系方式，或通过系统消息功能联系。', '社团相关', '2023-09-01 12:00:00', '2023-09-01 12:00:00', 1);
INSERT INTO `faqs` VALUES (6, '积分有什么用？', '积分可用于评选优秀学生、兑换礼品等，具体规则请查看积分规则说明。', '积分相关', '2023-09-01 12:00:00', '2023-09-01 12:00:00', 1);

-- ----------------------------
-- Table structure for help_documents
-- ----------------------------
DROP TABLE IF EXISTS `help_documents`;
CREATE TABLE `help_documents`  (
                                   `document_id` int NOT NULL AUTO_INCREMENT COMMENT '文档ID',
                                   `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
                                   `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
                                   `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类',
                                   `author_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者ID',
                                   `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-发布，0-草稿',
                                   PRIMARY KEY (`document_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '帮助文档表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of help_documents
-- ----------------------------
INSERT INTO `help_documents` VALUES (1, '如何加入社团', '1. 登录系统后进入社团页面\n2. 浏览感兴趣的社团并点击查看详情\n3. 点击申请加入按钮\n4. 填写申请信息并提交\n5. 等待社团管理员审核', '使用指南', 'SA001', '2023-09-01 10:00:00', '2023-09-01 10:00:00', 1);
INSERT INTO `help_documents` VALUES (2, '如何创建活动', '1. 社团管理员登录系统\n2. 进入活动管理页面\n3. 点击创建活动按钮\n4. 填写活动详细信息\n5. 提交活动申请等待学校管理员审批', '管理员指南', 'SA001', '2023-09-01 10:30:00', '2023-09-01 10:30:00', 1);
INSERT INTO `help_documents` VALUES (3, '积分规则说明', '参与活动可获得相应积分：\n- 讲座类活动：2分\n- 工作坊类活动：3分\n- 文艺演出类活动：1.5分\n- 体育竞赛类活动：1分\n- 志愿服务类活动：2.5分\n积分可用于评选优秀学生', '积分系统', 'SA001', '2023-09-01 11:00:00', '2023-09-01 11:00:00', 1);
INSERT INTO `help_documents` VALUES (4, '社团管理条例', '1. 社团成员需遵守学校规章制度\n2. 积极参与社团活动\n3. 维护社团形象和利益\n4. 按时缴纳会费（如有）\n5. 尊重社团管理人员和其他成员', '规章制度', 'SA001', '2023-09-01 11:30:00', '2023-09-01 11:30:00', 1);

-- ----------------------------
-- Table structure for school_admins
-- ----------------------------
DROP TABLE IF EXISTS `school_admins`;
CREATE TABLE `school_admins`  (
                                  `admin_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理员ID',
                                  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
                                  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
                                  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                                  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                                  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
                                  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
                                  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `status` int NOT NULL,
                                  PRIMARY KEY (`admin_id`) USING BTREE,
                                  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
                                  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学校管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of school_admins
-- ----------------------------
INSERT INTO `school_admins` VALUES ('SA001', '学校管理员A', 'school_admin_a', '12345678', 'schooladminA@example.com', '13700137001', 'https://example.com/avatar/schooladminA.jpg', '超级管理员', '2025-11-24 10:52:13', '2025-11-24 11:04:05', 1);
INSERT INTO `school_admins` VALUES ('SA002', '学校管理员B', 'school_admin_b', '12345678', 'schooladminB@example.com', '13700137002', 'https://example.com/avatar/schooladminB.jpg', '普通管理员', '2025-11-24 10:52:13', '2025-11-24 11:04:07', 1);

-- ----------------------------
-- Table structure for student_points
-- ----------------------------
DROP TABLE IF EXISTS `student_points`;
CREATE TABLE `student_points`  (
                                   `point_id` int NOT NULL AUTO_INCREMENT COMMENT '积分记录ID',
                                   `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                   `activity_id` int NOT NULL COMMENT '活动ID',
                                   `points` double NULL DEFAULT NULL,
                                   `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                   `awarded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授予时间',
                                   `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `award_time` datetime(6) NULL DEFAULT NULL,
                                   `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                   PRIMARY KEY (`point_id`) USING BTREE,
                                   INDEX `idx_student_points_student`(`student_id` ASC) USING BTREE,
                                   INDEX `idx_student_points_activity`(`activity_id` ASC) USING BTREE,
                                   CONSTRAINT `student_points_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `student_points_ibfk_2` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`activity_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生活动积分表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_points
-- ----------------------------
INSERT INTO `student_points` VALUES (1, '2021006', 1, 2, '参加Java编程入门讲座', '2023-11-15 17:00:00', '2023-11-15 17:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (2, '2021007', 1, 2, '参加Java编程入门讲座', '2023-11-15 17:00:00', '2023-11-15 17:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (3, '2022001', 1, 2, '参加Java编程入门讲座', '2023-11-15 17:00:00', '2023-11-15 17:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (4, '2022002', 1, 2, '参加Java编程入门讲座', '2023-11-15 17:00:00', '2023-11-15 17:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (5, '2021001', 2, 3, '参加Python数据分析工作坊', '2023-11-20 13:00:00', '2023-11-20 13:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (6, '2021006', 2, 3, '参加Python数据分析工作坊', '2023-11-20 13:00:00', '2023-11-20 13:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (7, '2021007', 2, 3, '参加Python数据分析工作坊', '2023-11-20 13:00:00', '2023-11-20 13:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (8, '2021002', 3, 1.5, '参加古典音乐会', '2023-11-25 22:00:00', '2023-11-25 22:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (9, '2021008', 3, 1.5, '参加古典音乐会', '2023-11-25 22:00:00', '2023-11-25 22:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (10, '2022001', 3, 1.5, '参加古典音乐会', '2023-11-25 22:00:00', '2023-11-25 22:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (11, '2021003', 4, 1, '参加新生篮球友谊赛', '2023-11-18 18:00:00', '2023-11-18 18:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (12, '2021004', 4, 1, '参加新生篮球友谊赛', '2023-11-18 18:00:00', '2023-11-18 18:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (13, '2022002', 4, 1, '参加新生篮球友谊赛', '2023-11-18 18:00:00', '2023-11-18 18:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (14, '2021004', 5, 2.5, '参加校园清洁志愿活动', '2023-11-12 12:00:00', '2023-11-12 12:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (15, '2021005', 5, 2.5, '参加校园清洁志愿活动', '2023-11-12 12:00:00', '2023-11-12 12:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (16, '2021006', 5, 2.5, '参加校园清洁志愿活动', '2023-11-12 12:00:00', '2023-11-12 12:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (17, '2021007', 5, 2.5, '参加校园清洁志愿活动', '2023-11-12 12:00:00', '2023-11-12 12:00:00', NULL, NULL);
INSERT INTO `student_points` VALUES (18, '2022001', 5, 2.5, '参加校园清洁志愿活动', '2023-11-12 12:00:00', '2023-11-12 12:00:00', NULL, NULL);

-- ----------------------------
-- Table structure for students
-- ----------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students`  (
                             `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
                             `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
                             `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                             `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
                             `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业',
                             `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '年级',
                             `enrollment_year` int NULL DEFAULT NULL COMMENT '入学年份',
                             `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                             `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `status` int NOT NULL,
                             PRIMARY KEY (`student_id`) USING BTREE,
                             UNIQUE INDEX `email`(`email` ASC) USING BTREE,
                             INDEX `idx_students_email`(`email` ASC) USING BTREE,
                             INDEX `idx_students_major`(`major` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of students
-- ----------------------------
INSERT INTO `students` VALUES ('2021001', '张三', '$2a$10$LtvJLLeC3AfE0FcrM9oVhewgZGjICmMzmnDczLoe1A6Z7w5Pt8BAG', 'zhangsan@example.com', '13800138000', '计算机科学与技术', '2021级', 2021, '男', 'https://example.com/avatar/zhangsan.jpg', '2025-11-24 10:50:32', '2025-11-25 19:47:40', 0);
INSERT INTO `students` VALUES ('2021002', '李四', '12345678', 'lisi@example.com', '13800138002', '软件工程', '2021级', 2021, '女', 'https://example.com/avatar/lisi.jpg', '2025-11-24 10:50:32', '2025-11-24 11:03:29', 1);
INSERT INTO `students` VALUES ('2021003', '王五', '12345678', 'wangwu@example.com', '13800138003', '信息安全', '2021级', 2021, '男', 'https://example.com/avatar/wangwu.jpg', '2025-11-24 10:50:32', '2025-11-24 11:03:31', 1);
INSERT INTO `students` VALUES ('2021004', '赵六', '$2a$10$JxI5TmU7rYqFXlIllxVNR.G2Ae9z6mtrxyDDqYhbmKc2gMameezCK', 'zhangsan_new@example.com', '13900139000', '数据科学与大数据技术', '2021级', 2021, '女', 'http://example.com/new_avatar.png', '2025-11-24 10:50:32', '2025-11-24 21:57:16', 1);
INSERT INTO `students` VALUES ('2021005', '钱七', '12345678', 'qianqi@example.com', '13800138005', '人工智能', '2021级', 2021, '男', 'https://example.com/avatar/qianqi.jpg', '2025-11-24 10:50:32', '2025-11-24 11:03:34', 1);
INSERT INTO `students` VALUES ('2021006', '孙八', '12345678', 'sunba@example.com', '13800138006', '物联网工程', '2021级', 2021, '女', 'https://example.com/avatar/sunba.jpg', '2025-11-24 10:50:32', '2025-11-24 11:03:36', 1);
INSERT INTO `students` VALUES ('2021007', '周九', '12345678', 'zhoujiu@example.com', '13800138007', '网络工程', '2021级', 2021, '男', 'https://example.com/avatar/zhoujiu.jpg', '2025-11-24 10:50:32', '2025-11-24 11:03:38', 1);
INSERT INTO `students` VALUES ('2021008', '吴十', '12345678', 'wushi@example.com', '13800138008', '计算机科学与技术', '2021级', 2021, '女', 'https://example.com/avatar/wushi.jpg', '2025-11-24 10:50:32', '2025-11-24 11:03:40', 1);
INSERT INTO `students` VALUES ('2022001', '郑一', '12345678', 'zhengyi@example.com', '13800138009', '软件工程', '2022级', 2022, '男', 'https://example.com/avatar/zhengyi.jpg', '2025-11-24 10:50:32', '2025-11-24 11:03:41', 1);
INSERT INTO `students` VALUES ('2022002', '王二', '12345678', 'wanger@example.com', '13800138010', '信息安全', '2022级', 2022, '女', 'https://example.com/avatar/wanger.jpg', '2025-11-24 10:50:32', '2025-11-24 11:06:15', 1);
INSERT INTO `students` VALUES ('2022003', 'lc', '12345678', 'lccccc188@example.com', '13800138011', '计算机科学与技术', '2022级', 2022, '男', NULL, '2025-11-27 18:37:33', '2025-11-29 18:37:37', 1);
INSERT INTO `students` VALUES ('2022004', 'lccccc', '$2a$10$88oeBkxvpvLe0FsbOam83O5AJIQvtjH0CfyoIjjaAgiJwnC60NL0m', 'zhangsa8n@example.com', '13800138011', '软件工程', '2021级', 2021, '男', NULL, '2025-11-25 18:32:19', '2025-11-25 18:33:18', 0);

-- ----------------------------
-- Table structure for system_logs
-- ----------------------------
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE `system_logs`  (
                                `log_id` int NOT NULL AUTO_INCREMENT COMMENT '日志ID',
                                `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户ID',
                                `user_type` enum('学生','社团管理员','学校管理员') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户类型',
                                `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作',
                                `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '详细描述',
                                `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
                                `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户代理',
                                `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_logs
-- ----------------------------
INSERT INTO `system_logs` VALUES (1, '2021001', '学生', '登录系统', '学生张三登录系统', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2023-11-01 08:30:00');
INSERT INTO `system_logs` VALUES (2, 'CA001', '社团管理员', '创建活动', '社团管理员A创建了Java编程入门讲座活动', '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '2023-11-01 09:00:00');
INSERT INTO `system_logs` VALUES (3, 'SA001', '学校管理员', '审批活动', '学校管理员A审批通过了Java编程入门讲座活动', '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2023-11-02 15:30:00');
INSERT INTO `system_logs` VALUES (4, '2021006', '学生', '报名活动', '学生孙八报名参加了Java编程入门讲座活动', '192.168.1.104', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '2023-11-02 10:30:00');
INSERT INTO `system_logs` VALUES (5, '2021007', '学生', '报名活动', '学生周九报名参加了Java编程入门讲座活动', '192.168.1.105', 'Mozilla/5.0 (Linux; Android 10; SM-G960U) AppleWebKit/537.36', '2023-11-02 11:15:00');
INSERT INTO `system_logs` VALUES (6, '2021001', '学生', '参加活动', '学生张三参加了Java编程入门讲座活动', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2023-11-15 14:05:00');
INSERT INTO `system_logs` VALUES (7, '2021006', '学生', '参加活动', '学生孙八参加了Java编程入门讲座活动', '192.168.1.104', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15', '2023-11-15 14:10:00');
INSERT INTO `system_logs` VALUES (8, 'CA002', '社团管理员', '创建活动', '社团管理员B创建了古典音乐会活动', '192.168.1.106', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '2023-11-02 11:00:00');
INSERT INTO `system_logs` VALUES (9, 'SA001', '学校管理员', '审批活动', '学校管理员A审批通过了古典音乐会活动', '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2023-11-03 14:20:00');
INSERT INTO `system_logs` VALUES (10, '2021002', '学生', '参加活动', '学生李四参加了古典音乐会活动', '192.168.1.107', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2023-11-25 19:05:00');

-- ----------------------------
-- View structure for activity_participation_stats
-- ----------------------------
DROP VIEW IF EXISTS `activity_participation_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `activity_participation_stats` AS select `a`.`activity_id` AS `activity_id`,`a`.`title` AS `title`,`a`.`max_participants` AS `max_participants`,`a`.`registered_count` AS `registered_count`,round(((`a`.`registered_count` / `a`.`max_participants`) * 100),2) AS `participation_rate` from `activities` `a` where (`a`.`status` in ('已批准','进行中','已结束'));

-- ----------------------------
-- View structure for club_activity_stats
-- ----------------------------
DROP VIEW IF EXISTS `club_activity_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `club_activity_stats` AS select `c`.`club_id` AS `club_id`,`c`.`name` AS `name`,count(`a`.`activity_id`) AS `total_activities`,sum(`a`.`registered_count`) AS `total_participants`,avg(`a`.`points`) AS `avg_points` from (`clubs` `c` left join `activities` `a` on((`c`.`club_id` = `a`.`club_id`))) group by `c`.`club_id`,`c`.`name`;

-- ----------------------------
-- View structure for student_activity_stats
-- ----------------------------
DROP VIEW IF EXISTS `student_activity_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `student_activity_stats` AS select `s`.`student_id` AS `student_id`,`s`.`name` AS `name`,count(`ar`.`activity_id`) AS `total_activities`,sum((case when (`ar`.`status` = '已参加') then 1 else 0 end)) AS `attended_activities`,sum(`ar`.`points_earned`) AS `total_points` from (`students` `s` left join `activity_registrations` `ar` on((`s`.`student_id` = `ar`.`student_id`))) group by `s`.`student_id`,`s`.`name`;

SET FOREIGN_KEY_CHECKS = 1;

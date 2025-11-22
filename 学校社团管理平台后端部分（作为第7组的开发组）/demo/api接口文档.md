# 学校社团管理平台 API 接口文档
# 基础URL: http://localhost:8080/api/v1

# 全局响应数据结构
common_response:
  success: boolean    # 操作是否成功
  code: integer       # 响应码
  message: string     # 响应消息
  data: object        # 响应数据
  timestamp: string   # 时间戳

# 分页响应数据结构
page_response:
  success: boolean
  code: integer
  message: string
  data:
    content: array    # 数据列表
    totalElements: integer  # 总记录数
    totalPages: integer     # 总页数
    size: integer          # 每页大小
    number: integer        # 当前页码
  timestamp: string

# 错误响应码定义
error_codes:
  SUCCESS: 200
  BAD_REQUEST: 400
  UNAUTHORIZED: 401
  FORBIDDEN: 403
  NOT_FOUND: 404
  METHOD_NOT_ALLOWED: 405
  CONFLICT: 409
  INTERNAL_SERVER_ERROR: 500

# 1. 认证相关接口 (/auth)

# 1.1 学生注册
POST /auth/student/register
描述: 学生用户注册
请求体:
{
  "username": "string",      # 用户名
  "password": "string",      # 密码
  "studentId": "string",     # 学号
  "name": "string",          # 姓名
  "email": "string",         # 邮箱
  "phone": "string",         # 手机号
  "major": "string",         # 专业
  "grade": "string",         # 年级
  "className": "string"      # 班级
}
响应:
{
  "success": true,
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 1,
    "studentId": "20210001",
    "username": "student001"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 1.2 用户登录
POST /auth/login
描述: 用户登录
请求体:
{
  "username": "string",      # 用户名(学号/员工号)
  "password": "string",      # 密码
  "role": "STUDENT|CLUB_ADMIN|SCHOOL_ADMIN"  # 用户角色
}
响应:
{
  "success": true,
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "jwt_token_string",
    "refreshToken": "refresh_token_string",
    "userInfo": {
      "id": 1,
      "username": "student001",
      "role": "STUDENT",
      "name": "张三",
      "studentId": "20210001",
      "avatarUrl": "http://example.com/avatar.jpg"
    },
    "expiresIn": 3600
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 1.3 刷新令牌
POST /auth/refresh
描述: 刷新访问令牌
请求体:
{
  "refreshToken": "string"
}
响应:
{
  "success": true,
  "code": 200,
  "message": "令牌刷新成功",
  "data": {
    "token": "new_jwt_token",
    "expiresIn": 3600
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 1.4 登出
POST /auth/logout
描述: 用户登出
请求头: Authorization: Bearer {token}
响应:
{
  "success": true,
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": "2024-01-01 12:00:00"
}

# 2. 学生端接口 (/student)

# 2.1 获取学生仪表盘数据
GET /student/dashboard
描述: 获取学生仪表盘概览信息
请求头: Authorization: Bearer {token}
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "joinedClubCount": 5,           # 已加入社团数量
    "pendingActivityCount": 3,      # 待批准活动申请数量
    "totalActivityCount": 15,       # 历史参与活动数量
    "totalPoints": 120,             # 总积分
    "recentActivities": [           # 近期活动
      {
        "id": 1,
        "title": "编程竞赛",
        "clubName": "计算机协会",
        "startTime": "2024-01-15 14:00:00",
        "status": "UPCOMING"
      }
    ],
    "quickActions": {
      "canApplyClub": true,
      "canJoinActivity": true,
      "canBrowseActivity": true
    }
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 2.2 获取个人信息
GET /student/profile
描述: 获取学生个人信息
请求头: Authorization: Bearer {token}
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "userId": 1,
    "username": "student001",
    "email": "student001@school.edu.cn",
    "phone": "13800138000",
    "studentId": "20210001",
    "name": "张三",
    "major": "计算机科学与技术",
    "grade": "2021",
    "className": "计科2101",
    "gender": "MALE",
    "birthDate": "2003-05-15",
    "avatarUrl": "http://example.com/avatar.jpg",
    "bio": "热爱编程，喜欢挑战",
    "totalPoints": 120,
    "joinedAt": "2021-09-01 00:00:00"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 2.3 更新个人信息
PUT /student/profile
描述: 更新学生个人信息
请求头: Authorization: Bearer {token}
请求体:
{
  "email": "newemail@school.edu.cn",
  "phone": "13900139000",
  "major": "软件工程",
  "grade": "2021",
  "className": "软工2101",
  "gender": "MALE",
  "birthDate": "2003-06-20",
  "bio": "专注于软件开发"
}
响应:
{
  "success": true,
  "code": 200,
  "message": "更新成功",
  "data": {
    "updatedFields": ["email", "phone", "major", "grade", "className", "bio"],
    "updatedAt": "2024-01-01 12:00:00"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 2.4 修改密码
PUT /student/password
描述: 修改用户密码
请求头: Authorization: Bearer {token}
请求体:
{
  "oldPassword": "old_password",
  "newPassword": "new_password",
  "confirmPassword": "new_password"
}
响应:
{
  "success": true,
  "code": 200,
  "message": "密码修改成功",
  "data": null,
  "timestamp": "2024-01-01 12:00:00"
}

# =====================================================
# 3. 社团相关接口 (/clubs)
# =====================================================

# 3.1 获取社团广场列表
GET /clubs
描述: 获取所有社团列表(支持分页、搜索、筛选)
请求头: Authorization: Bearer {token}
查询参数:
- page: integer (default: 0) - 页码
- size: integer (default: 10) - 每页大小
- search: string - 搜索关键词
- type: string - 社团类型筛选
- sort: string (default: "name") - 排序字段
- order: string (default: "asc") - 排序方向
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "计算机协会",
        "description": "致力于推广计算机技术学习",
        "type": "TECHNOLOGY",
        "memberCount": 150,
        "maxMembers": 200,
        "logoUrl": "http://example.com/logo1.jpg",
        "establishedDate": "2020-09-01",
        "location": "实验楼A301",
        "isJoined": false
      }
    ],
    "totalElements": 25,
    "totalPages": 3,
    "size": 10,
    "number": 0
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 3.2 获取社团详情
GET /clubs/{clubId}
描述: 获取特定社团详细信息
请求头: Authorization: Bearer {token}
路径参数:
- clubId: integer - 社团ID
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "name": "计算机协会",
    "description": "致力于推广计算机技术学习",
    "type": "TECHNOLOGY",
    "declaration": "用代码改变世界",
    "logoUrl": "http://example.com/logo1.jpg",
    "bannerUrl": "http://example.com/banner1.jpg",
    "establishedDate": "2020-09-01",
    "memberCount": 150,
    "maxMembers": 200,
    "contactEmail": "cs@school.edu.cn",
    "contactPhone": "010-12345678",
    "location": "实验楼A301",
    "status": "ACTIVE",
    "recentActivities": [
      {
        "id": 1,
        "title": "编程竞赛",
        "startTime": "2024-01-15 14:00:00",
        "status": "UPCOMING",
        "participantCount": 50,
        "maxParticipants": 100
      }
    ],
    "isJoined": false,
    "applicationStatus": null
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 3.3 申请加入社团
POST /clubs/{clubId}/apply
描述: 申请加入指定社团
请求头: Authorization: Bearer {token}
路径参数:
- clubId: integer - 社团ID
请求体:
{
  "applicationReason": "我对编程很感兴趣，希望通过社团提升技能",
  "relevantExperience": "高中时参加信息学竞赛，获得省级二等奖",
  "expectedActivityTypes": "技术分享会、编程比赛、项目实战",
  "availableTime": "每周三晚上，周末下午",
  "portfolioUrl": "http://example.com/portfolio.pdf",
  "contactPhone": "13800138000",
  "contactEmail": "student001@school.edu.cn"
}
响应:
{
  "success": true,
  "code": 200,
  "message": "申请提交成功",
  "data": {
    "applicationId": 123,
    "status": "PENDING",
    "appliedAt": "2024-01-01 12:00:00",
    "estimatedReviewTime": "3-5个工作日"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 3.4 获取我的社团列表
GET /student/clubs
描述: 获取学生已加入的社团列表
请求头: Authorization: Bearer {token}
查询参数:
- page: integer (default: 0)
- size: integer (default: 10)
- status: string - 成员状态筛选
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "计算机协会",
        "description": "致力于推广计算机技术学习",
        "logoUrl": "http://example.com/logo1.jpg",
        "role": "MEMBER",
        "joinedAt": "2021-09-15 10:30:00",
        "status": "ACTIVE",
        "pointsEarned": 50,
        "recentActivityTitle": "编程竞赛",
        "unreadNotifications": 2
      }
    ],
    "totalElements": 3,
    "totalPages": 1,
    "size": 10,
    "number": 0
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 3.5 取消加入社团申请
DELETE /clubs/{clubId}/apply
描述: 取消加入社团的申请
请求头: Authorization: Bearer {token}
路径参数:
- clubId: integer - 社团ID
响应:
{
  "success": true,
  "code": 200,
  "message": "申请已取消",
  "data": {
    "applicationId": 123,
    "status": "CANCELLED",
    "cancelledAt": "2024-01-01 12:00:00"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 4. 活动相关接口 (/activities)

# 4.1 获取活动中心列表
GET /activities
描述: 获取所有活动列表
请求头: Authorization: Bearer {token}
查询参数:
- page: integer (default: 0)
- size: integer (default: 10)
- search: string - 搜索关键词
- type: string - 活动类型筛选
- clubId: integer - 社团ID筛选
- status: string - 活动状态筛选
- startDate: string - 开始日期筛选
- endDate: string - 结束日期筛选
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "编程竞赛",
        "description": "全校编程技能竞赛",
        "type": "COMPETITION",
        "clubName": "计算机协会",
        "startTime": "2024-01-15 14:00:00",
        "endTime": "2024-01-15 18:00:00",
        "location": "实验楼A301",
        "maxParticipants": 100,
        "currentParticipantCount": 65,
        "requiredPoints": 10,
        "rewardPoints": 50,
        "registrationFee": 0.00,
        "registrationDeadline": "2024-01-10 23:59:59",
        "posterUrl": "http://example.com/poster1.jpg",
        "status": "APPROVED",
        "isRegistered": false,
        "canRegister": true
      }
    ],
    "totalElements": 30,
    "totalPages": 3,
    "size": 10,
    "number": 0
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 4.2 获取活动详情
GET /activities/{activityId}
描述: 获取活动详细信息
请求头: Authorization: Bearer {token}
路径参数:
- activityId: integer - 活动ID
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "title": "编程竞赛",
    "description": "全校编程技能竞赛，包含算法、数据结构等多个环节",
    "activityType": "COMPETITION",
    "clubName": "计算机协会",
    "startTime": "2024-01-15 14:00:00",
    "endTime": "2024-01-15 18:00:00",
    "registrationDeadline": "2024-01-10 23:59:59",
    "location": "实验楼A301",
    "maxParticipants": 100,
    "currentParticipantCount": 65,
    "requiredPoints": 10,
    "rewardPoints": 50,
    "registrationFee": 0.00,
    "requirements": "具备基础编程能力，熟悉至少一种编程语言",
    "materialsNeeded": "笔记本电脑、笔、纸",
    "contactInfo": "联系人: 李老师 电话: 010-12345678",
    "posterUrl": "http://example.com/poster1.jpg",
    "status": "APPROVED",
    "isRegistered": false,
    "canRegister": true,
    "registrationStatus": null
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 4.3 报名参加活动
POST /activities/{activityId}/register
描述: 报名参加指定活动
请求头: Authorization: Bearer {token}
路径参数:
- activityId: integer - 活动ID
请求体:
{
  "applicationReason": "希望通过竞赛提升编程技能",
  "specialRequirements": "需要使用特定的编程语言",
  "emergencyContact": "张三",
  "emergencyPhone": "13900139000"
}
响应:
{
  "success": true,
  "code": 200,
  "message": "报名成功",
  "data": {
    "applicationId": 456,
    "activityId": 1,
    "status": "PENDING",
    "appliedAt": "2024-01-01 12:00:00",
    "estimatedProcessingTime": "1-2个工作日",
    "requiredPoints": 10,
    "currentPoints": 120
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 4.4 获取我的活动列表
GET /student/activities
描述: 获取学生参与的活动列表
请求头: Authorization: Bearer {token}
查询参数:
- page: integer (default: 0)
- size: integer (default: 10)
- status: string - 活动申请状态筛选
- activityStatus: string - 活动本身状态筛选
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "编程竞赛",
        "description": "全校编程技能竞赛",
        "activityType": "COMPETITION",
        "clubName": "计算机协会",
        "startTime": "2024-01-15 14:00:00",
        "endTime": "2024-01-15 18:00:00",
        "location": "实验楼A301",
        "applicationStatus": "PENDING",     # PENDING/APPROVED/REJECTED/CANCELLED/ATTENDED
        "activityStatus": "APPROVED",       # 活动本身状态
        "appliedAt": "2024-01-01 12:00:00",
        "processedAt": null,
        "pointsEarned": 0,
        "canCancel": true,
        "canModify": true
      }
    ],
    "totalElements": 8,
    "totalPages": 1,
    "size": 10,
    "number": 0
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 4.5 取消活动报名
DELETE /activities/{activityId}/register
描述: 取消活动报名
请求头: Authorization: Bearer {token}
路径参数:
- activityId: integer - 活动ID
响应:
{
  "success": true,
  "code": 200,
  "message": "报名已取消",
  "data": {
    "applicationId": 456,
    "status": "CANCELLED",
    "cancelledAt": "2024-01-01 12:00:00",
    "refundPoints": 10
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 5. 社团管理员接口 (/club-admin)

# 5.1 获取管理概览
GET /club-admin/dashboard
描述: 获取社团管理员仪表盘数据
请求头: Authorization: Bearer {token}
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "managedClubs": [
      {
        "id": 1,
        "name": "计算机协会",
        "memberCount": 150,
        "pendingActivityCount": 2,
        "pendingApplications": 5
      }
    ],
    "totalMembers": 150,
    "totalActivities": 8,
    "pendingApprovals": 7,
    "recentActivities": [
      {
        "id": 1,
        "title": "编程竞赛",
        "status": "APPROVED",
        "participantCount": 65,
        "startTime": "2024-01-15 14:00:00"
      }
    ],
    "statistics": {
      "thisMonthNewMembers": 12,
      "thisMonthActivities": 3,
      "averageParticipationRate": 0.75
    }
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 5.2 获取成员列表
GET /club-admin/clubs/{clubId}/members
描述: 获取社团成员列表
请求头: Authorization: Bearer {token}
路径参数:
- clubId: integer - 社团ID
查询参数:
- page: integer (default: 0)
- size: integer (default: 10)
- role: string - 角色筛选
- status: string - 状态筛选
- search: string - 搜索关键词
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "studentId": "20210001",
        "name": "张三",
        "major": "计算机科学与技术",
        "grade": "2021",
        "className": "计科2101",
        "role": "MEMBER",
        "joinedAt": "2021-09-15 10:30:00",
        "status": "ACTIVE",
        "pointsEarned": 50,
        "lastActivityDate": "2024-01-01 10:00:00",
        "contactInfo": {
          "email": "zhangsan@school.edu.cn",
          "phone": "13800138000"
        }
      }
    ],
    "totalElements": 150,
    "totalPages": 15,
    "size": 10,
    "number": 0
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 5.3 审批成员申请
PUT /club-admin/clubs/{clubId}/applications/{applicationId}
描述: 审批加入社团申请
请求头: Authorization: Bearer {token}
路径参数:
- clubId: integer - 社团ID
- applicationId: integer - 申请ID
请求体:
{
  "status": "APPROVED",        # APPROVED/REJECTED
  "processedNote": "欢迎加入社团"
}
响应:
{
  "success": true,
  "code": 200,
  "message": "审批完成",
  "data": {
    "applicationId": 123,
    "studentId": "20210001",
    "studentName": "张三",
    "status": "APPROVED",
    "processedAt": "2024-01-01 12:00:00",
    "processedBy": "李老师",
    "processedNote": "欢迎加入社团"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 5.4 创建活动
POST /club-admin/activities
描述: 创建新活动
请求头: Authorization: Bearer {token}
请求体:
{
  "clubId": 1,
  "title": "技术分享会",
  "description": "邀请业界专家分享最新技术趋势",
  "activityType": "WORKSHOP",
  "startTime": "2024-01-20 14:00:00",
  "endTime": "2024-01-20 17:00:00",
  "registrationDeadline": "2024-01-18 23:59:59",
  "location": "实验楼A301",
  "maxParticipants": 80,
  "requiredPoints": 5,
  "rewardPoints": 20,
  "registrationFee": 0.00,
  "requirements": "对技术有一定了解",
  "materials_needed": "笔记本电脑",
  "contactInfo": "联系人: 李老师 电话: 010-12345678"
}
响应:
{
  "success": true,
  "code": 200,
  "message": "活动创建成功，等待审批",
  "data": {
    "activityId": 2,
    "status": "PENDING_APPROVAL",
    "submittedAt": "2024-01-01 12:00:00",
    "estimatedApprovalTime": "1-3个工作日"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 5.5 获取活动申请列表
GET /club-admin/activities/{activityId}/applications
描述: 获取活动的报名申请列表
请求头: Authorization: Bearer {token}
路径参数:
- activityId: integer - 活动ID
查询参数:
- page: integer (default: 0)
- size: integer (default: 10)
- status: string - 申请状态筛选
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 456,
        "studentId": "20210001",
        "studentName": "张三",
        "major": "计算机科学与技术",
        "grade": "2021",
        "className": "计科2101",
        "applicationReason": "希望通过竞赛提升编程技能",
        "specialRequirements": "需要使用特定的编程语言",
        "emergencyContact": "张三父亲",
        "emergencyPhone": "13900139000",
        "status": "PENDING",
        "appliedAt": "2024-01-01 12:00:00",
        "contactInfo": {
          "email": "zhangsan@school.edu.cn",
          "phone": "13800138000"
        }
      }
    ],
    "totalElements": 65,
    "totalPages": 7,
    "size": 10,
    "number": 0
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 6. 学校管理员接口 (/school-admin)

# 6.1 获取学校管理概览
GET /school-admin/dashboard
描述: 获取学校管理员仪表盘数据
请求头: Authorization: Bearer {token}
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalClubs": 25,
    "totalStudents": 1200,
    "totalClubAdmins": 30,
    "totalActivities": 150,
    "pendingActivityApprovals": 8,
    "totalRegistrations": 2500,
    "statistics": {
      "activeClubs": 23,
      "activeMembers": 980,
      "completedActivities": 120,
      "averageParticipationRate": 0.68,
      "thisMonthNewClubs": 2,
      "thisMonthActivities": 15
    },
    "recentActivities": [
      {
        "id": 1,
        "title": "编程竞赛",
        "clubName": "计算机协会",
        "status": "PENDING_APPROVAL",
        "submittedAt": "2024-01-01 10:00:00",
        "submittedBy": "李老师"
      }
    ],
    "activityApprovalQueue": [
      {
        "activityId": 2,
        "title": "技术分享会",
        "clubName": "计算机协会",
        "submittedAt": "2024-01-01 12:00:00",
        "urgencyLevel": "NORMAL"
      }
    ]
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 6.2 获取用户管理列表
GET /school-admin/users
描述: 获取所有用户列表
请求头: Authorization: Bearer {token}
查询参数:
- page: integer (default: 0)
- size: integer (default: 10)
- role: string - 角色筛选 (STUDENT/CLUB_ADMIN/SCHOOL_ADMIN)
- status: string - 状态筛选
- search: string - 搜索关键词
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "student001",
        "role": "STUDENT",
        "name": "张三",
        "email": "student001@school.edu.cn",
        "phone": "13800138000",
        "status": "ACTIVE",
        "studentId": "20210001",
        "major": "计算机科学与技术",
        "grade": "2021",
        "joinedAt": "2021-09-01 00:00:00",
        "lastLoginTime": "2024-01-01 08:30:00",
        "clubCount": 2
      }
    ],
    "totalElements": 1230,
    "totalPages": 123,
    "size": 10,
    "number": 0
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 6.3 创建用户
POST /school-admin/users
描述: 创建新用户(学生或管理员)
请求头: Authorization: Bearer {token}
请求体:
{
  "username": "admin001",
  "password": "password123",
  "role": "CLUB_ADMIN",
  "name": "王老师",
  "email": "wang@school.edu.cn",
  "phone": "13700137000",
  "employeeId": "T001",
  "department": "学生处",
  "position": "社团管理专员"
}
响应:
{
  "success": true,
  "code": 200,
  "message": "用户创建成功",
  "data": {
    "userId": 101,
    "username": "admin001",
    "role": "CLUB_ADMIN",
    "name": "王老师",
    "createdAt": "2024-01-01 12:00:00"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 6.4 审批活动申请
PUT /school-admin/activities/{activityId}/approve
描述: 审批社团活动申请
请求头: Authorization: Bearer {token}
路径参数:
- activityId: integer - 活动ID
请求体:
{
  "status": "APPROVED",        # APPROVED/REJECTED
  "processedNote": "活动内容符合学校规定，同意举办"
}
响应:
{
  "success": true,
  "code": 200,
  "message": "审批完成",
  "data": {
    "activityId": 2,
    "title": "技术分享会",
    "clubName": "计算机协会",
    "status": "APPROVED",
    "processedAt": "2024-01-01 12:00:00",
    "processedBy": "管理员001",
    "processedNote": "活动内容符合学校规定，同意举办",
    "approvedStartTime": "2024-01-20 14:00:00"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 6.5 获取数据分析报告
GET /school-admin/analytics
描述: 获取平台数据分析报告
请求头: Authorization: Bearer {token}
查询参数:
- startDate: string - 开始日期 (YYYY-MM-DD)
- endDate: string - 结束日期 (YYYY-MM-DD)
- reportType: string - 报告类型 (OVERVIEW/CLUB_ACTIVITY/MEMBER_GROWTH/PARTICIPATION)
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "reportType": "OVERVIEW",
    "dateRange": {
      "startDate": "2023-12-01",
      "endDate": "2024-01-01"
    },
    "summary": {
      "totalClubs": 25,
      "totalActiveMembers": 980,
      "totalActivities": 150,
      "totalParticipations": 2500,
      "averageParticipationRate": 0.68
    },
    "clubTypeDistribution": [
      {
        "type": "TECHNOLOGY",
        "count": 8,
        "percentage": 32.0
      },
      {
        "type": "ARTS",
        "count": 6,
        "percentage": 24.0
      }
    ],
    "monthlyGrowth": [
      {
        "month": "2023-12",
        "newClubs": 1,
        "newMembers": 45,
        "newActivities": 12
      }
    ],
    "topClubs": [
      {
        "clubId": 1,
        "clubName": "计算机协会",
        "memberCount": 150,
        "activityCount": 12,
        "participationRate": 0.75
      }
    ],
    "participationTrends": [
      {
        "date": "2024-01-01",
        "activities": 3,
        "participations": 125,
        "rate": 0.72
      }
    ]
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 7. 通知相关接口 (/notifications)

# 7.1 获取通知列表
GET /notifications
描述: 获取用户通知列表
请求头: Authorization: Bearer {token}
查询参数:
- page: integer (default: 0)
- size: integer (default: 10)
- type: string - 通知类型筛选
- isRead: boolean - 是否已读筛选
响应:
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "申请已通过",
        "content": "您的加入计算机协会申请已通过审核",
        "type": "APPLICATION_RESULT",
        "relatedType": "CLUB_APPLICATION",
        "relatedId": 123,
        "isRead": false,
        "createdAt": "2024-01-01 10:30:00",
        "readAt": null
      }
    ],
    "totalElements": 15,
    "totalPages": 2,
    "size": 10,
    "number": 0,
    "unreadCount": 3
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 7.2 标记通知已读
PUT /notifications/{notificationId}/read
描述: 标记指定通知为已读
请求头: Authorization: Bearer {token}
路径参数:
- notificationId: integer - 通知ID
响应:
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "notificationId": 1,
    "isRead": true,
    "readAt": "2024-01-01 12:00:00"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 7.3 批量标记已读
PUT /notifications/read-all
描述: 批量标记所有通知为已读
请求头: Authorization: Bearer {token}
响应:
{
  "success": true,
  "code": 200,
  "message": "所有通知已标记为已读",
  "data": {
    "markedCount": 15,
    "markedAt": "2024-01-01 12:00:00"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 8. 文件上传接口 (/upload)

# 8.1 上传头像
POST /upload/avatar
描述: 上传用户头像
请求头: 
- Authorization: Bearer {token}
- Content-Type: multipart/form-data
请求体: (form-data)
- file: File - 头像文件
响应:
{
  "success": true,
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://example.com/avatars/user_1_20240101.jpg",
    "fileName": "user_1_20240101.jpg",
    "fileSize": 102400,
    "uploadedAt": "2024-01-01 12:00:00"
  },
  "timestamp": "2024-01-01 12:00:00"
}

# 8.2 上传活动海报
POST /upload/poster
描述: 上传活动海报
请求头: 
- Authorization: Bearer {token}
- Content-Type: multipart/form-data
请求体: (form-data)
- file: File - 海报文件
响应:
{
  "success": true,
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://example.com/posters/activity_1_20240101.jpg",
    "fileName": "activity_1_20240101.jpg",
    "fileSize": 204800,
    "uploadedAt": "2024-01-01 12:00:00"
  },
  "timestamp": "2024-01-01 12:00:00"
}
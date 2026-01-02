# 学校社团管理系统 API 文档

## 1. 文档概述

本文档详细描述了学校社团管理系统的 API 接口，包括用户端(C 端)、社团管理员后台和学校管理员后台的所有功能接口。系统支持学生、社团管理员和学校管理员三种角色的用户进行不同权限的操作。

## 2. API 基础信息

### 2.1 基础 URL

```
http://localhost:8080/api/v1
```

### 2.2 认证方式

系统采用 JWT (JSON Web Token) 进行身份认证。登录成功后，服务器会返回一个 JWT token，客户端需要在后续的请求头中携带此 token：

```
Authorization: Bearer {token}
```

### 2.3 响应格式

所有 API 响应均采用统一格式：

```json
{
    "code": 200, // 状态码
    "message": "success", // 状态信息
    "data": {} // 响应数据
}
```

### 2.4 状态码说明

-   200: 请求成功
-   400: 请求参数错误
-   401: 未授权或 token 过期
-   403: 权限不足
-   404: 资源不存在
-   500: 服务器内部错误

## 3. 认证模块

### 3.1 学生登录

**URL**: `/auth/student/login`
**方法**: `POST`
**请求体**:

```json
{
    "student_id": "2021001",
    "password": "123456"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
            "student_id": "2021001",
            "name": "张三",
            "role": "student"
        }
    }
}
```

### 3.2 学生注册

**URL**: `/auth/student/register`
**方法**: `POST`
**请求体**:

```json
{
    "student_id": "2021001",
    "name": "张三",
    "password": "123456",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "major": "计算机科学与技术",
    "grade": "2021级",
    "enrollment_year": 2021,
    "gender": "男"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "student_id": "2021001"
    }
}
```

### 3.3 社团管理员登录

**URL**: `/auth/club-admin/login`
**方法**: `POST`
**请求体**:

```json
{
    "username": "club_admin",
    "password": "123456"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
            "admin_id": "CA001",
            "name": "李四",
            "club_id": 1,
            "role": "club_admin"
        }
    }
}
```

### 3.4 学校管理员登录

**URL**: `/auth/school-admin/login`
**方法**: `POST`
**请求体**:

```json
{
    "username": "school_admin_a",
    "password": "12345678"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
            "admin_id": "SA001",
            "name": "王五",
            "role": "school_admin",
            "admin_role": "超级管理员"
        }
    }
}
```

### 3.5 退出登录

**URL**: `/auth/logout`
**方法**: `POST`
**响应**:

```json
{
    "code": 200,
    "message": "退出成功",
    "data": null
}
```

## 4. 用户端(C 端) API

### 4.1 学生仪表盘

#### 4.1.1 获取学生仪表盘数据

**URL**: `/student/dashboard`
**方法**: `GET`
**权限**: 学生
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "personalInfo": {
            "studentId": "2022002",
            "name": "王二",
            "major": "信息安全",
            "grade": "2022级",
            "points": 3.0
        },
        "recentActivities": [
            {
                "activityId": 4,
                "title": "新生篮球友谊赛",
                "time": "2023-11-18T15:00",
                "location": "体育馆篮球场",
                "clubName": "篮球社"
            }
        ],
        "myClubs": [],
        "statistics": {
            "totalClubs": 0,
            "totalActivities": 2,
            "totalPoints": 3.0,
            "ranking": 5
        }
    }
}
```

### 4.2 社团管理

#### 4.2.1 获取社团列表

**URL**: `/student/clubs`
**方法**: `GET`
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "clubId": 1,
                "name": "计算机协会",
                "description": "计算机爱好者的聚集地...",
                "categoryName": "学术科技类",
                "memberCount": 27,
                "presidentName": "张三"
            }
        ]
    }
}
```

#### 4.2.2 获取社团详情

**URL**: `/student/clubs/{club_id}`
**方法**: `GET`
**参数**: `club_id` - 社团 ID
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "club_id": 1,
        "name": "计算机协会",
        "description": "计算机爱好者的聚集地...",
        "logo_url": "http://example.com/logo.png",
        "president_name": "张三",
        "foundation_date": "2020-09-01",
        "member_count": 27,
        "contact_email": "computer@example.com",
        "address": "实验室A",
        "recent_activities": [
            {
                "activity_id": 1,
                "title": "编程竞赛讲座",
                "start_time": "2023-10-15 14:00:00"
            }
        ],
        "joined": false
    }
}
```

#### 4.2.3 申请加入社团

**URL**: `/student/clubs/{clubId}/applications`
**方法**: `POST`
**请求体**:

```json
{
    "club_id": 1,
    "name": "张三",
    "major": "计算机科学与技术",
    "grade": "2021级",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "reason": "我对计算机技术很感兴趣...",
    "experience": "曾参加过校级编程比赛...",
    "activity_preference": "技术讲座、编程竞赛",
    "available_time": "周三下午、周末",
    "portfolio": "http://github.com/zhangsan"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "申请成功",
    "data": {
        "application_id": 1
    }
}
```

#### 4.2.4 获取我的社团

**URL**: `/student/my-clubs`
**方法**: `GET`
**权限**: 学生
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "clubId": 1,
            "name": "计算机协会",
            "categoryName": "学术科技类",
            "role": "成员",
            "joinDate": "2021-09-15",
            "points": 120
        }
    ]
}
```

#### 4.2.5 退出社团

**URL**: `/student/clubs/{club_id}/leave`
**方法**: `DELETE`
**权限**: 学生
**参数**:

-   `club_id` - 路径参数，社团 ID
    **请求头**:
-   `Authorization: Bearer <token>` - JWT 令牌
    **响应**:

```json
{
    "code": 200,
    "message": "退出成功",
    "data": null
}
```

### 4.3 活动管理

#### 4.3.1 获取活动列表

**URL**: `/student/activities`
**方法**: `GET`
**权限**: 学生
**功能描述**: 获取学生所在社团的活动列表
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "activity_id": 1,
            "title": "编程竞赛讲座",
            "club_name": "计算机协会",
            "poster_url": "http://example.com/poster.png",
            "start_time": "2023-10-15 14:00:00",
            "location": "教学楼A301",
            "status": "已批准",
            "is_registered": false
        }
    ]
}
```

**说明**: 学生只能看到自己已加入社团的活动，活动列表根据学生的 token 进行筛选。当一个学生没有参加任何社团时，他们查看活动列表将能看到所有社团的全部活动。

#### 4.3.2 获取活动详情

**URL**: `/student/activities/{activity_id}`
**方法**: `GET`
**参数**: `activity_id` - 活动 ID
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "activity_id": 1,
        "title": "编程竞赛讲座",
        "description": "详细介绍各类编程竞赛...",
        "club_id": 1,
        "club_name": "计算机协会",
        "poster_url": "http://example.com/poster.png",
        "location": "教学楼A301",
        "start_time": "2023-10-15 14:00:00",
        "end_time": "2023-10-15 16:00:00",
        "max_participants": 200,
        "registered_count": 150,
        "points": 2.0,
        "registration_deadline": "2023-10-14 23:59:59",
        "status": "已批准",
        "is_registered": false
    }
}
```

#### 4.3.3 活动报名

**URL**: `/student/{activityId}/register`
**方法**: `POST`
**请求体**:

```json
{
    "activity_id": 1
}
```

**响应**:

```json
{
    "code": 200,
    "message": "报名成功",
    "data": {
        "registration_id": 1
    }
}
```

#### 4.3.4 取消活动报名

**URL**: `/student/{activityId}/cancel`
**方法**: `DELETE`
**参数**: `registration_id` - 报名 ID
**请求体**:

```json
{
    "cancellation_reason": "临时有事无法参加"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "取消成功",
    "data": null
}
```

#### 4.3.5 获取我的活动

**URL**: `/student/my-activities`
**方法**: `GET`
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "activity_id": 1,
                "title": "编程竞赛讲座",
                "club_name": "计算机协会",
                "start_time": "2023-10-15 14:00:00",
                "location": "教学楼A301",
                "registration_status": "已报名",
                "points_earned": 0
            }
        ]
    }
}
```

### 4.4 个人信息管理

#### 4.4.1 获取个人信息

**URL**: `/student/profile`
**方法**: `GET`
**权限**: 学生
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "student_id": "2021001",
        "name": "张三",
        "email": "zhangsan@example.com",
        "phone": "13800138000",
        "major": "计算机科学与技术",
        "grade": "2021级",
        "enrollment_year": 2021,
        "gender": "男",
        "avatar_url": "http://example.com/avatar.png"
    }
}
```

#### 4.4.2 更新个人信息

**URL**: `/student/profile`
**方法**: `POST`
**权限**: 学生
**请求体**:

```json
{
  "email": "zhangsan_new@example.com",
  "phone": "13900139000",
}
```

**响应**:

```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

#### 4.4.3 修改密码

**URL**: `/student/change-password`
**方法**: `PUT`
**权限**: 学生
**请求体**:

```json
{
    "old_password": "123456",
    "new_password": "654321"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "密码修改成功",
    "data": null
}
```

### 4.5 帮助与支持

#### 4.5.1 获取常见问题

**URL**: `/student/faqs`
**方法**: `GET`
**参数**:

-   `category`: 可选，分类
-   `keyword`: 可选，搜索关键词
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "faq_id": 1,
                "question": "如何申请加入社团？",
                "answer": "在社团详情页点击'申请加入'按钮，填写申请表单即可。",
                "category": "社团申请"
            }
        ]
    }
}
```

#### 4.5.2 获取帮助文档

**URL**: `/student/documents`
**方法**: `GET`
**参数**:

-   `category`: 可选，分类
-   `keyword`: 可选，搜索关键词
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "document_id": 1,
                "title": "学生用户使用指南",
                "category": "使用指南",
                "created_at": "2023-09-01 00:00:00"
            }
        ]
    }
}
```

#### 4.5.3 获取帮助文档详情

**URL**: `/student/documents/{document_id}`
**方法**: `GET`
**参数**: `document_id` - 文档 ID
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "document_id": 1,
        "title": "学生用户使用指南",
        "content": "详细的使用指南内容...",
        "category": "使用指南",
        "created_at": "2023-09-01 00:00:00"
    }
}
```

## 5. 社团管理员后台 API

### 5.1 管理概览

#### 5.1.1 获取社团管理概览

**URL**: `/club-admin/dashboard`
**方法**: `GET`
**权限**: 社团管理员
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "clubName": "计算机协会",
        "memberCount": 25,
        "pendingApplicationsCount": 1, //待处理的社团申请数
        "activitiesCount": 2,
        "upcomingActivitiesCount": 0, //进行中的活动数
        "recentMembers": [
            {
                "studentId": "2022002",
                "name": "2022002",
                "joinDate": "2022-09-15"
            },
            {
                "studentId": "2022001",
                "name": "2022001",
                "joinDate": "2022-09-01"
            },
            {
                "studentId": "2021007",
                "name": "2021007",
                "joinDate": "2021-09-15"
            },
            {
                "studentId": "2021001",
                "name": "2021001",
                "joinDate": "2021-09-01"
            },
            {
                "studentId": "2021006",
                "name": "2021006",
                "joinDate": "2021-09-01"
            }
        ],
        "recentActivities": [
            {
                "activityId": 1,
                "title": "Java编程入门讲座",
                "startTime": "2025-11-25T14:00",
                "status": "已批准"
            },
            {
                "activityId": 2,
                "title": "Python数据分析工作坊",
                "startTime": "2023-11-20T10:00",
                "status": "已批准"
            }
        ]
    }
}
```

### 5.2 成员管理

#### 5.2.1 获取社团成员列表

**URL**: `/club-admin/members`
**方法**: `GET`
**权限**: 社团管理员
**参数**:

-   `keyword`: 可选，搜索关键词
-   `role`: 可选，成员角色
-   `page`: 页码，默认 1
-   `page_size`: 每页数量，默认 10
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "content": [
            {
                "id": 1,
                "studentId": "2021001",
                "name": "张三",
                "major": "计算机科学与技术",
                "grade": "2021级",
                "role": "社长",
                "joinDate": "2021-09-01",
                "status": 1
            },
            {
                "id": 2,
                "studentId": "2021006",
                "name": "孙八",
                "major": "物联网工程",
                "grade": "2021级",
                "role": "副社长",
                "joinDate": "2021-09-01",
                "status": 1
            },
            {
                "id": 3,
                "studentId": "2021007",
                "name": "周九",
                "major": "网络工程",
                "grade": "2021级",
                "role": "普通成员",
                "joinDate": "2021-09-15",
                "status": 1
            },
            {
                "id": 4,
                "studentId": "2022001",
                "name": "郑一",
                "major": "软件工程",
                "grade": "2022级",
                "role": "普通成员",
                "joinDate": "2022-09-01",
                "status": 1
            },
            {
                "id": 5,
                "studentId": "2022002",
                "name": "王二",
                "major": "信息安全",
                "grade": "2022级",
                "role": "普通成员",
                "joinDate": "2022-09-15",
                "status": 1
            }
        ],
        "pageable": {
            //分页信息
            "pageNumber": 0, //当前页码，从0开始
            "pageSize": 10, //每页数量
            "sort": {
                //排序信息
                "empty": true, //排序条件是否为空
                "sorted": false, //是否排序
                "unsorted": true //是否未排序
            },
            "offset": 0, //偏移量，从0开始
            "paged": true, //是否分页
            "unpaged": false //是否未分页
        },
        "last": true, //是否最后一页
        "totalElements": 5, //总元素数
        "totalPages": 1, //总页数
        "size": 10, //每页数量
        "number": 0, //当前页码，从0开始
        "sort": {
            "empty": true, //排序条件是否为空
            "sorted": false, //是否排序
            "unsorted": true //是否未排序
        },
        "numberOfElements": 5, //当前页元素数
        "first": true, //是否第一页
        "empty": false //是否为空
    }
}
```

#### 5.2.2 修改成员角色

**URL**: `/club-admin/members/{id}/role`
**方法**: `PUT`
**权限**: 社团管理员
**参数**: `id` - 成员关系 ID
**请求体**:

```json
{
    "role": "副社长"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success"
}
```

#### 5.2.3 移除社团成员

**URL**: `/club-admin/members/{id}`
**方法**: `DELETE`
**权限**: 社团管理员
**参数**: `id` - 成员关系 ID
**响应**:

```json
{
    "code": 200,
    "message": "success"
}
```

### 5.3 申请管理

#### 5.3.1 获取社团申请列表

**URL**: `/club-admin/applications`
**方法**: `GET`
**权限**: 社团管理员
**参数**:

-   `status`: 可选，申请状态
-   `keyword`: 可选，搜索关键词
-   `page`: 页码，默认 1
-   `page_size`: 每页数量，默认 10
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "applicationId": 1,
                "studentId": "2022001",
                "name": "郑一",
                "major": "软件工程",
                "grade": "2022级",
                "reason": "对计算机技术很感兴趣，希望能在实践中提升自己的编程能力",
                "status": "通过",
                "createdAt": "2023-10-10T06:20:00.000+00:00"
            }
        ],
        "total": 5,
        "page": 1,
        "page_size": 10
    }
}
```

#### 5.3.2 获取申请详情

**URL**: `/club-admin/applications/{application_id}`
**方法**: `GET`
**权限**: 社团管理员
**参数**: `application_id` - 申请 ID
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "applicationId": 1,
        "studentId": "2022001",
        "name": "郑一",
        "major": "软件工程",
        "grade": "2022级",
        "phone": "13800138009",
        "email": "zhengyi@example.com",
        "reason": "对计算机技术很感兴趣，希望能在实践中提升自己的编程能力",
        "experience": "参加过学校组织的编程竞赛，获得过三等奖",
        "activityPreference": "技术讲座、项目实战",
        "availableTime": "周一、三、五晚上",
        "portfolio": "https://github.com/zhengyi",
        "status": "通过",
        "createdAt": "2023-10-10T06:20:00.000+00:00"
    }
}
```

#### 5.3.3 审批社团申请

**URL**: `/club-admin/applications/{application_id}/review`
**方法**: `PUT`
**权限**: 社团管理员
**参数**: `application_id` - 申请 ID
**请求体**:

```json
{
    "status": "通过",
    "comments": "欢迎加入！"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "审批成功"
}
```

### 5.4 活动管理

#### 5.4.1 获取社团活动列表

**URL**: `/club-admin/activities`
**方法**: `GET`
**权限**: 社团管理员
**参数**:

-   `status`: 可选，活动状态
-   `keyword`: 可选，搜索关键词
-   `page`: 页码，默认 1
-   `page_size`: 每页数量，默认 10
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "activity_id": 1,
                "title": "编程竞赛讲座",
                "category_name": "讲座论坛",
                "start_time": "2023-10-15 14:00:00",
                "location": "教学楼A301",
                "registered_count": 150,
                "status": "已批准"
            }
        ],
        "total": 20,
        "page": 1,
        "page_size": 10
    }
}
```

#### 5.4.2 创建社团活动

**URL**: `/club-admin/activities`
**方法**: `POST`
**权限**: 社团管理员
**请求体**:

```json
{
    "category_id": 1,
    "title": "编程竞赛讲座",
    "description": "详细介绍各类编程竞赛...",
    "poster_url": "http://example.com/poster.png",
    "location": "教学楼A301",
    "start_time": "2023-10-15 14:00:00",
    "end_time": "2023-10-15 16:00:00",
    "max_participants": 200,
    "points": 2.0,
    "registration_deadline": "2023-10-14 23:59:59"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "activity_id": 1
    }
}
```

#### 5.4.3 获取活动详情

**URL**: `/club-admin/activities/{activity_id}`
**方法**: `GET`
**权限**: 社团管理员
**参数**: `activity_id` - 活动 ID
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "activity_id": 1,
        "title": "编程竞赛讲座",
        "description": "详细介绍各类编程竞赛...",
        "category_id": 1,
        "category_name": "讲座论坛",
        "poster_url": "http://example.com/poster.png",
        "location": "教学楼A301",
        "start_time": "2023-10-15 14:00:00",
        "end_time": "2023-10-15 16:00:00",
        "max_participants": 200,
        "registered_count": 150,
        "points": 2.0,
        "registration_deadline": "2023-10-14 23:59:59",
        "status": "已批准",
        "created_by": "CA001",
        "created_at": "2023-10-01 00:00:00"
    }
}
```

#### 5.4.4 更新活动信息

**URL**: `/club-admin/activities/{activity_id}`
**方法**: `PUT`
**权限**: 社团管理员
**参数**: `activity_id` - 活动 ID
**请求体**:

```json
{
    "title": "更新后的编程竞赛讲座",
    "description": "更新后的详细介绍...",
    "location": "教学楼B301"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success"
}
```

#### 5.4.5 提交活动审批

**URL**: `/club-admin/activities/{activity_id}/submit-for-approval`
**方法**: `PUT`
**权限**: 社团管理员
**参数**: `activity_id` - 活动 ID
**响应**:

```json
{
    "code": 200,
    "message": "success"
}
```

#### 5.4.6 获取活动报名列表

**URL**: `/club-admin/activities/{activity_id}/registrations`
**方法**: `GET`
**权限**: 社团管理员
**参数**:

-   `activity_id`: 活动 ID
-   `status`: 可选，报名状态
-   `page`: 页码，默认 1
-   `page_size`: 每页数量，默认 10
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "registration_id": 1,
                "student_id": "2021001",
                "name": "张三",
                "major": "计算机科学与技术",
                "grade": "2021级",
                "registration_time": "2023-10-01 10:00:00",
                "status": "已报名",
                "attended": 0,
                "points_earned": 0
            }
        ],
        "total": 150,
        "page": 1,
        "page_size": 10
    }
}
```

#### 5.4.7 确认活动参与

**URL**: `/club-admin/activities/{activity_id}/attendance`
**方法**: `POST`
**权限**: 社团管理员
**参数**: `activity_id` - 活动 ID
**请求体**:

```json
{
    "registration_ids": [1, 2, 3],
    "points_earned": 2.0
}
```

**响应**:

```json
{
    "code": 200,
    "message": "确认成功",
    "data": null
}
```

## 6. 学校管理员后台 API

### 6.1 用户管理

#### 6.1.1 获取学生列表

**URL**: `/school-admin/students`
**方法**: `GET`
**权限**: 学校管理员
**参数**:

-   `keyword`: 可选，搜索关键词
-   `major`: 可选，专业
-   `status`: 可选，状态
-   `page`: 页码，默认 1
-   `page_size`: 每页数量，默认 10
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "student_id": "2021001",
                "name": "张三",
                "email": "zhangsan@example.com",
                "phone": "13800138000",
                "major": "软件工程",
                "grade": "2021级",
                "status": 1
            }
        ],
        "total": 1000,
        "page": 1,
        "page_size": 10
    }
}
```

#### 6.1.2 创建/更新学生信息

**URL**: `/school-admin/students`
**方法**: `POST`
**权限**: 学校管理员
**请求体**:

```json
{
    "student_id": "2021001",
    "name": "张三",
    "password": "123456",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "major": "计算机科学与技术",
    "grade": "2021级",
    "enrollment_year": 2021,
    "gender": "男"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "student_id": "2021001"
    }
}
```

#### 6.1.3 禁用/启用学生账号

**URL**: `/school-admin/students/{student_id}/status`
**方法**: `PUT`
**权限**: 学校管理员
**参数**: `student_id` - 学号
**请求体**:

```json
{
    "status": 0
}
```

**响应**:

```json
{
    "code": 200,
    "message": "操作成功"
}
```

#### 6.1.4 获取社团管理员列表

**URL**: `/school-admin/club-admins`
**方法**: `GET`
**权限**: 学校管理员
**参数**:

-   `keyword`: 可选，搜索关键词
-   `club_id`: 可选，社团 ID
-   `status`: 可选，状态
-   `page`: 页码，默认 1
-   `page_size`: 每页数量，默认 10
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "admin_id": "CA001",
                "club_id": 1,
                "club_name": "计算机协会",
                "name": "李四",
                "username": "club_admin",
                "email": "lisi@example.com",
                "phone": "13900139000",
                "status": 1
            }
        ],
        "total": 50,
        "page": 1,
        "page_size": 10
    }
}
```

#### 6.1.5 创建/更新社团管理员

**URL**: `/school-admin/club-admins`
**方法**: `POST`
**权限**: 学校管理员
**请求体**:

```json
{
    "admin_id": "CA001",
    "club_id": 1,
    "name": "李四",
    "username": "club_admin",
    "password": "123456",
    "email": "lisi@example.com",
    "phone": "13900139000"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "admin_id": "CA001"
    }
}
```

#### 6.1.6 禁用/启用社团管理员账号

**URL**: `/school-admin/club-admins/{admin_id}/status`
**方法**: `PUT`
**权限**: 学校管理员
**参数**: `admin_id` - 管理员 ID
**请求体**:

```json
{
    "status": 0
}
```

**响应**:

```json
{
    "code": 200,
    "message": "操作成功"
}
```

### 6.2 社团管理

#### 6.2.1 获取社团列表

**URL**: `/school-admin/clubs`
**方法**: `GET`
**权限**: 学校管理员
**参数**:

-   `category_id`: 可选，分类 ID
-   `keyword`: 可选，搜索关键词
-   `status`: 可选，状态
-   `page`: 页码，默认 1
-   `page_size`: 每页数量，默认 10
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "club_id": 1,
                "category_id": 1,
                "category_name": "学术科技类",
                "name": "计算机协会",
                "president_student_id": "2021001",
                "president_name": "张三",
                "member_count": 27,
                "foundation_date": "2020-09-01",
                "status": 1
            }
        ],
        "total": 30,
        "page": 1,
        "page_size": 10
    }
}
```

#### 6.2.2 创建/更新社团

**URL**: `/school-admin/clubs`
**方法**: `POST`
**权限**: 学校管理员
**请求体**:

```json
{
    "club_id": 1,
    "category_id": 1,
    "name": "计算机协会",
    "description": "计算机爱好者的聚集地...",
    "logo_url": "http://example.com/logo.png",
    "president_student_id": "2021001",
    "foundation_date": "2020-09-01",
    "max_members": 200,
    "contact_info": "computer@example.com",
    "meeting_time": "每周三下午",
    "meeting_location": "实验室A"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "club_id": 1
    }
}
```

#### 6.2.3 解散/恢复社团

**URL**: `/school-admin/clubs/{club_id}/status`
**方法**: `PUT`
**权限**: 学校管理员
**参数**: `club_id` - 社团 ID
**请求体**:

```json
{
    "status": 0
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success"
}
```

#### 6.2.4 获取社团分类列表

**URL**: `/school-admin/club-categories`
**方法**: `GET`
**权限**: 学校管理员
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "category_id": 1,
                "name": "学术科技类",
                "description": "以学术研究、科技创新为主的社团"
            },
            {
                "category_id": 2,
                "name": "文化艺术类",
                "description": "以文化传承、艺术表演为主的社团"
            }
        ]
    }
}
```

#### 6.2.5 创建/更新社团分类

**URL**: `/school-admin/club-categories`
**方法**: `POST`
**权限**: 学校管理员
**请求体**:

```json
{
    "category_id": 1,
    "name": "学术科技类",
    "description": "以学术研究、科技创新为主的社团"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "category_id": 1
    }
}
```

### 6.3 活动审批

#### 6.3.1 获取待审批活动列表

**URL**: `/school-admin/activities/pending-approval`
**方法**: `GET`
**权限**: 学校管理员
**参数**:

-   `club_id`: 可选，社团 ID
-   `keyword`: 可选，搜索关键词
-   `page`: 页码，默认 1
-   `page_size`: 每页数量，默认 10
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "activityId": 6,
                "clubId": 5,
                "title": "秋季摄影作品展",
                "clubName": "摄影社",
                "categoryName": "展览展示",
                "startTime": "2023-11-30 10:00:00",
                "endTime": "2023-11-30 17:00:00",
                "location": "艺术楼一楼展厅",
                "createdBy": "CA005",
                "createdAt": "2023-11-06 08:30:00"
            }
        ],
        "total": 5,
        "page": 1,
        "page_size": 10
    }
}
```

#### 6.3.2 获取活动详情用于审批

**URL**: `/school-admin/activities/{activity_id}/approval`
**方法**: `GET`
**权限**: 学校管理员
**参数**: `activity_id` - 活动 ID
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "activityId": 6,
        "clubId": 5,
        "clubName": "摄影社",
        "title": "秋季摄影作品展",
        "description": "展示摄影社成员秋季拍摄的优秀作品",
        "categoryId": 6,
        "categoryName": "展览展示",
        "posterUrl": "https://example.com/poster/photo_exhibition.jpg",
        "location": "艺术楼一楼展厅",
        "startTime": "2023-11-30 10:00:00",
        "endTime": "2023-11-30 17:00:00",
        "maxParticipants": 200,
        "points": 0.0,
        "registrationDeadline": "2023-11-25 23:59:59",
        "createdBy": "CA005",
        "createdAt": "2023-11-06 08:30:00",
        "approvalId": 6,
        "status": "待审批",
        "comments": "活动内容符合要求，批准举办。",
        "approverId": "SA001",
        "approvedAt": "2025-11-25 17:34:33"
    }
}
```

#### 6.3.3 审批活动

**URL**: `/school-admin/activities/{activity_id}/approval`
**方法**: `PUT`
**权限**: 学校管理员
**参数**: `activity_id` - 活动 ID
**请求体**:

```json
{
    "status": "已批准",
    "comments": "活动内容符合要求，批准举办。"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success"
}
```

#### 6.3.4 获取活动分类列表

**URL**: `/school-admin/activity-categories`
**方法**: `GET`
**权限**: 学校管理员
**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "name": "讲座论坛",
                "description": "学术讲座、专题论坛等活动",
                "category_id": 1
            },
            {
                "name": "文艺演出",
                "description": "音乐会、话剧表演等文艺活动",
                "category_id": 2
            }
        ]
    }
}
```

#### 6.3.5 创建/更新活动分类

**URL**: `/school-admin/activity-categories`
**方法**: `POST`
**权限**: 学校管理员
**请求体**:

```json
{
    "category_id": 1,
    "name": "讲座论坛",
    "description": "学术讲座、专题论坛等活动"
}
```

**响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "category_id": 1
    }
}
```

### 6.4 数据分析与报告

#### 6.4.1 获取社团活跃度统计

**URL**: `/school-admin/statistics/club-activity`
**方法**: `GET`
**权限**: 学校管理员
**参数**:

-   `start_date`: 可选，开始日期
-   `end_date`: 可选，结束日期
-   `category_id`: 可选，社团分类 ID
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "clubId": 1,
                "clubName": "计算机协会",
                "totalActivities": 3,
                "totalParticipants": 8,
                "avgPoints": 2.4285714285714284
            }
        ]
    }
}
```

#### 6.4.2 获取学生活动参与统计

**URL**: `/school-admin/statistics/student-activity`
**方法**: `GET`
**权限**: 学校管理员
**参数**:

-   `start_date`: 可选，开始日期
-   `end_date`: 可选，结束日期
-   `major`: 可选，专业
-   `grade`: 可选，年级
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "studentId": "2021001",
                "name": "张三",
                "major": "软件工程",
                "grade": "2021级",
                "totalActivities": 1,
                "attendedActivities": 1,
                "totalPoints": 3.0
            }
        ]
    }
}
```

#### 6.4.3 获取活动参与率统计

**URL**: `/school-admin/statistics/activity-participation`
**方法**: `GET`
**权限**: 学校管理员
**参数**:

-   `start_date`: 可选，开始日期
-   `end_date`: 可选，结束日期
-   `club_id`: 可选，社团 ID
    **响应**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "activityId": 1,
                "title": "Java编程入门讲座",
                "clubName": "计算机协会",
                "maxParticipants": 50,
                "registeredCount": 25,
                "participationRate": 50.0
            }
            // ...
        ]
    }
}
```

```json
{
    "activity_id": 1,
    "club_id": 1,
    "category_id": 1,
    "title": "编程竞赛讲座",
    "description": "详细介绍各类编程竞赛...",
    "poster_url": "http://example.com/poster.png",
    "location": "教学楼A301",
    "start_time": "2023-10-15 14:00:00",
    "end_time": "2023-10-15 16:00:00",
    "max_participants": 200,
    "registered_count": 150,
    "points": 2.0,
    "registration_deadline": "2023-10-14 23:59:59",
    "status": "已批准",
    "approved_by": "SA001",
    "approved_at": "2023-10-02 00:00:00",
    "created_by": "CA001",
    "created_at": "2023-10-01 00:00:00",
    "updated_at": "2023-10-02 00:00:00"
}
```

## 7. 总结

本文档详细描述了学校社团管理系统的 API 接口设计，涵盖了学生端、社团管理员后台和学校管理员后台的所有功能需求。API 设计遵循 RESTful 规范，提供了完整的 CRUD 操作，支持权限控制和数据验证。系统采用 JWT 认证机制，确保 API 调用的安全性。

后续开发过程中，建议根据实际需求对 API 进行适当调整和扩展。

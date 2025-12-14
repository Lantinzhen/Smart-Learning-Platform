export interface MemberItem {
  // 成员ID
  id: number;
  // 成员名称
  name: string;
  // 成员学号
  studentId: number;
  // 成员年级
  grade: string;
  // 成员专业
  major: string;
  // 成员角色
  role: string;
  // 加入时间
  joinDate: string;
  // 状态
  status: 0 | 1;
}

export interface StudentItem {
  // 学生学号
  student_id: number;
  // 学生名称
  name: string;
  // 学生邮箱
  email: string;
  // 学生手机号
  phone: string;
  // 学生专业
  major: string;
  // 学生年级
  grade: string;
  // 状态
  status: 0 | 1;
}

export interface StudentProfile {
  // 学生学号
  student_id: string;
  // 学生名称
  name: string;
  // 密码
  password: string;
  // 学生邮箱
  email: string;
  // 学生手机号
  phone: string;
  // 学生专业
  major: string;
  // 学生年级
  grade: string;
  // 入学年份
  enrollment_year: number;
  // 性别
  gender: "男" | "女";
}

export interface ClubAdminItem {
  // 社团管理员id
  admin_id: string;
  // 社团id
  club_id: number;
  // 社团名
  club_name: string;
  // 社团管理员名
  name: string;
  // 社团管理员账号
  username: string;
  // 社团管理员邮箱
  email: string;
  // 社团管理员手机号
  phone: string;
  // 状态
  status: 0 | 1;
}

export interface ClubAdminProfile {
  // 社团管理员id
  admin_id: string;
  // 社团id
  club_id: number;
  // 社团管理员名
  name: string;
  // 社团管理员账号
  username: string;
  // 社团管理员密码
  password: string;
  // 社团管理员邮箱
  email: string;
  // 社团管理员手机号
  phone: string;
}

export interface ClubItem {
  // 社团id
  club_id: number;
  // 社团类型id
  category_id: number;
  // 社团类型名称
  category_name: string;
  // 社团名
  name: string;
  // 社长id
  president_student_id: string;
  // 社长名
  president_name: string;
  // 社团成员数量
  member_count: number;
  // 社团成立时间
  foundation_date: string;
  // 状态
  status: 0 | 1;
}

export interface ClubProfile {
  // 社团id
  club_id: number;
  // 社团类型id
  category_id: number;
  // 社团名
  name: string;
  // 社团描述
  description: string;
  // 社团头像
  logo_url: string;
  // 社长id
  president_student_id: string;
  // 社团成立时间
  foundation_date: string;
  // 最大成员数
  max_members: number;
  // 联系方式
  contact_info: string;
  // 社团活动时间
  meeting_time: string;
  // 社团活动地点
  meeting_location: string;
}

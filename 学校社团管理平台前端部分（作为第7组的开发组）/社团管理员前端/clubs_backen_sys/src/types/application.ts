
// 申请列表类型
export  interface ApplicationItem {
  // 申请ID
  applicationId: number,
  // 申请人
  name: string,
  // 申请时间
  createdAt: string,
  // 申请状态
  status: string,
  // 申请人学号
  grade: string,
  // 申请人专业
  major: string,
  // 申请人学号
  studentId: string,
  // 申请理由
  reason: string,
}

export interface ApplicationDetail {
  // 申请人
  name: string,
  // 申请人学号
  studentId: number,
  // 年级
  grade: string,
  // 申请人专业
  major: string,
  // 邮件
  email: string,
  // 联系方式
  phone: string,
  // 活动兴趣偏好
  activityPreference: string,
  // 闲暇时间
  availableTime: string,
  // 申请人经历
  experience: string,
  // 申请状态
  status: string,
  // 申请时间
  createdAt: string,
  // 申请理由
  reason: string,
  // 申请ID
  applicationId: number
}
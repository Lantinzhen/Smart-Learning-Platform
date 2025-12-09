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
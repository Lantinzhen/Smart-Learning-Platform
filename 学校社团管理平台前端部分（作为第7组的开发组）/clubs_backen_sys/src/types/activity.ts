export interface ActivityItem {
  // 活动ID
  activityId: number;
  // 活动类别
  categoryName: string;
  // 活动名称
  title:string;
  // 活动开始时间
  startTime: string;
  // 活动地点
  location: string;
  // 活动状态
  status: '已批准' | '待审批' | '已拒绝' | '已取消'|'草稿';
  // 已报名人数
  registeredCount: number;

}

// src/types/activity.ts
export interface ActivityDetail {
  // 活动ID
  activityId: number;
  // 活动类别ID
  categoryId: number;
  // 活动类别名称
  categoryName: string;
  // 活动开始时间
  createdAt: string;
  // 活动创建者
  createdBy: string;
  // 活动描述
  description: string;
  // 活动结束时间
  endTime: string;
  // 活动地点
  location: string;
  // 活动最大人数
  maxParticipants: number;
  // 活动所能获得的积分
  points: number;
  // 活动海报URL
  posterUrl: string;
  // 已报名人数
  registeredCount: number;
  // 报名截止时间
  registrationDeadline: string;
  // 活动开始时间
  startTime: string;
  // 活动状态
  status: '已批准' | '待审批' | '已拒绝' | '已取消';
  // 活动标题
  title: string;
}

export interface ActivityForm {
  // 活动ID
  activity_id?: number;
  // 活动名称
  title: string,
  // 分类ID
  category_id: number,
  // 活动描述
  description: string,
  // 活动图片
  poster_url: string,
  // 活动地点
  location: string,
  // 活动开始时间
  start_time: string,
  // 活动结束时间
  end_time: string,
  // 活动报名人数限制
  max_participants: number,
  // 活动积分
  points: number,
  // 报名截止时间
  registration_deadline: string,
}

// 活动积分发放
export interface ActivityAttendance {
  // 参与人ids
  registration_ids:number[],
  // 积分
  points_earned:number
}

export interface updateActivityForm {
  // 活动标题
  title: string;
  // 活动描述
  description: string;
  // 活动位置
  location: string;
}

export interface ActivityEnrollItem {
  // 活动报名id 序号
  registrationId:number,
  // 学号
  student_id: number,
  // 姓名
  name: string,
  // 专业
  major: string,
  // 年级
  grade: string,
  // 报名时间
  registrationTime: string,
  // 状态
  status: string,
  // 是否参与
  attended: number,
  // 积分
  pointsEarned: number
}

// 按活动ID存储分页状态和报名列表
export interface ActivityPagination {
  currentPage: number; // 当前页
  pageSize: number;    // 每页条数
  total: number;       // 总条数
  list: ActivityEnrollItem[]; // 报名列表
  registrationIds:number[];
}

// 活动审批详情（用于学校管理员审批）
export interface ActivityApprovalDetail extends ActivityDetail {
  // 审批ID
  approvalId?: number;
  // 审批意见
  comments?: string;
  // 审批人ID
  approverId?: string;
  // 审批时间
  approvedAt?: string;
  // 社团ID
  clubId?: number;
  // 社团名称
  clubName?: string;
}

// 审批表单
export interface ActivityApprovalForm {
  // 审批状态
  status: '已批准' | '已拒绝';
  // 审批意见
  comments: string;
}
import { defineStore } from 'pinia'

export interface Activity {
  id: string
  title: string
  clubId: string
  clubName: string
  description: string
  time: string
  location: string
  status: '报名中' | '已结束' | '即将开始'
  participantCount: number
  maxParticipants: number
  tags: string[]
  points: number // 活动积分
}

export interface ActivityBooking {
  id: string
  activityId: string
  activityTitle: string
  clubName: string
  time: string
  location: string
  status: '已预约' | '已取消' | '已完成'
  bookingTime: string
  points?: number // 活动积分（可选，可从Activity中获取）
}

export const useActivityStore = defineStore('activity', {
  state: () => ({
    activities: [] as Activity[],
    myActivities: [] as ActivityBooking[],
    currentActivity: null as Activity | null
  }),
  actions: {
    // 获取所有活动
    fetchActivities() {
      // 模拟数据
      this.activities = [
        {
          id: '1',
          title: '春季摄影外拍',
          clubId: '3',
          clubName: '摄影社',
          description: '春季校园美景外拍活动，欢迎摄影爱好者参加',
          time: '2024-03-15 14:00',
          location: '校园西湖边',
          status: '报名中',
          participantCount: 25,
          maxParticipants: 50,
          tags: ['摄影', '外拍', '春季'],
          points: 10
        },
        {
          id: '2',
          title: '音乐鉴赏会',
          clubId: '1',
          clubName: '音乐社',
          description: '古典音乐鉴赏与分享活动',
          time: '2024-03-16 19:00',
          location: '大学生活动中心',
          status: '报名中',
          participantCount: 40,
          maxParticipants: 80,
          tags: ['音乐', '鉴赏', '古典'],
          points: 8
        },
        {
          id: '3',
          title: '编程竞赛',
          clubId: '5',
          clubName: '编程社',
          description: '算法编程竞赛，锻炼编程能力',
          time: '2024-03-23 14:00',
          location: '计算机实验室',
          status: '即将开始',
          participantCount: 0,
          maxParticipants: 60,
          tags: ['编程', '竞赛', '算法'],
          points: 20
        },
        {
          id: '4',
          title: '篮球友谊赛',
          clubId: '4',
          clubName: '篮球社',
          description: '与外校篮球社的友谊交流赛',
          time: '2024-03-17 15:00',
          location: '学校体育馆',
          status: '已结束',
          participantCount: 20,
          maxParticipants: 20,
          tags: ['篮球', '比赛', '友谊赛'],
          points: 15
        },
        {
          id: '5',
          title: '绘画创作 workshop',
          clubId: '2',
          clubName: '美术社',
          description: '主题绘画创作活动，提供绘画材料',
          time: '2024-03-20 14:00',
          location: '艺术楼画室',
          status: '报名中',
          participantCount: 15,
          maxParticipants: 30,
          tags: ['绘画', '创作', 'workshop'],
          points: 12
        }
      ]
    },
    // 获取我的活动
    fetchMyActivities() {
      // 模拟数据
      this.myActivities = [
        {
          id: '1',
          activityId: '1',
          activityTitle: '春季摄影外拍',
          clubName: '摄影社',
          time: '2024-03-15 14:00',
          location: '校园西湖边',
          status: '已预约',
          bookingTime: '2024-03-01 10:30'
        },
        {
          id: '2',
          activityId: '2',
          activityTitle: '音乐鉴赏会',
          clubName: '音乐社',
          time: '2024-03-16 19:00',
          location: '大学生活动中心',
          status: '已预约',
          bookingTime: '2024-03-02 14:20'
        },
        {
          id: '3',
          activityId: '4',
          activityTitle: '篮球友谊赛',
          clubName: '篮球社',
          time: '2024-03-17 15:00',
          location: '学校体育馆',
          status: '已完成',
          bookingTime: '2024-03-05 09:15'
        }
      ]
    },
    // 获取活动详情
    fetchActivityDetail(id: string) {
      const activity = this.activities.find(a => a.id === id)
      this.currentActivity = activity || null
    },
    // 预约活动
    bookActivity(activityId: string) {
      // 模拟预约活动
      return true
    },
    // 取消活动预约
    cancelBooking(bookingId: string) {
      // 模拟取消预约
      const booking = this.myActivities.find(b => b.id === bookingId)
      if (booking) {
        booking.status = '已取消'
      }
      return true
    }
  }
})
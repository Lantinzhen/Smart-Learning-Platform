import { defineStore } from 'pinia'

export interface Club {
  id: string
  name: string
  type: string
  description: string
  declaration: string
  memberCount: number
  joinProgress: number
  avatar: string
  tags: string[]
}

export interface ClubMember {
  id: string
  name: string
  role: string
  avatar: string
}

export const useClubStore = defineStore('club', {
  state: () => ({
    clubs: [] as Club[],
    myClubs: [] as Club[],
    currentClub: null as Club | null,
    clubMembers: [] as ClubMember[],
    clubActivities: [] as any[]
  }),
  actions: {
    // 获取所有社团
    fetchClubs() {
      // 模拟数据
      this.clubs = [
        {
          id: '1',
          name: '音乐社',
          type: '文艺社团',
          description: '致力于音乐的传播和推广，定期举办音乐会和培训班',
          declaration: '以音乐为纽带，连接志同道合的朋友',
          memberCount: 50,
          joinProgress: 85,
          avatar: 'https://ui-avatars.com/api/?name=音乐社&background=0D8ABC',
          tags: ['音乐', '乐器', '表演']
        },
        {
          id: '2',
          name: '美术社',
          type: '文艺社团',
          description: '专注于绘画和艺术创作，提供绘画课程和展览机会',
          declaration: '用画笔记录生活，用艺术表达情感',
          memberCount: 35,
          joinProgress: 72,
          avatar: 'https://ui-avatars.com/api/?name=美术社&background=F44336',
          tags: ['绘画', '艺术', '创作']
        },
        {
          id: '3',
          name: '摄影社',
          type: '兴趣社团',
          description: '热爱摄影，分享摄影技巧，组织外拍活动',
          declaration: '捕捉瞬间，记录美好',
          memberCount: 42,
          joinProgress: 68,
          avatar: 'https://ui-avatars.com/api/?name=摄影社&background=4CAF50',
          tags: ['摄影', '相机', '外拍']
        },
        {
          id: '4',
          name: '篮球社',
          type: '体育社团',
          description: '篮球爱好者的聚集地，定期组织训练和比赛',
          declaration: '以球会友，享受运动乐趣',
          memberCount: 65,
          joinProgress: 90,
          avatar: 'https://ui-avatars.com/api/?name=篮球社&background=FF9800',
          tags: ['篮球', '运动', '团队']
        },
        {
          id: '5',
          name: '编程社',
          type: '学术社团',
          description: '探讨编程技术，举办编程比赛和技术分享会',
          declaration: '代码改变世界，技术创造未来',
          memberCount: 48,
          joinProgress: 75,
          avatar: 'https://ui-avatars.com/api/?name=编程社&background=9C27B0',
          tags: ['编程', '技术', '算法']
        },
        {
          id: '6',
          name: '辩论社',
          type: '学术社团',
          description: '培养辩论能力，参加各类辩论比赛',
          declaration: '思辨明真理，论道展才华',
          memberCount: 30,
          joinProgress: 60,
          avatar: 'https://ui-avatars.com/api/?name=辩论社&background=2196F3',
          tags: ['辩论', '逻辑', '表达']
        }
      ]
    },
    // 获取我的社团
    fetchMyClubs() {
      // 模拟数据
      this.myClubs = [
        {
          id: '1',
          name: '音乐社',
          type: '文艺社团',
          description: '致力于音乐的传播和推广，定期举办音乐会和培训班',
          declaration: '以音乐为纽带，连接志同道合的朋友',
          memberCount: 50,
          joinProgress: 85,
          avatar: 'https://ui-avatars.com/api/?name=音乐社&background=0D8ABC',
          tags: ['音乐', '乐器', '表演']
        },
        {
          id: '3',
          name: '摄影社',
          type: '兴趣社团',
          description: '热爱摄影，分享摄影技巧，组织外拍活动',
          declaration: '捕捉瞬间，记录美好',
          memberCount: 42,
          joinProgress: 68,
          avatar: 'https://ui-avatars.com/api/?name=摄影社&background=4CAF50',
          tags: ['摄影', '相机', '外拍']
        }
      ]
    },
    // 获取社团详情
    fetchClubDetail(id: string) {
      const club = this.clubs.find(c => c.id === id)
      this.currentClub = club || null
      
      // 模拟社团成员数据
      this.clubMembers = [
        { id: '1', name: '李四', role: '社长', avatar: 'https://ui-avatars.com/api/?name=李四&background=random' },
        { id: '2', name: '王五', role: '副社长', avatar: 'https://ui-avatars.com/api/?name=王五&background=random' },
        { id: '3', name: '赵六', role: '成员', avatar: 'https://ui-avatars.com/api/?name=赵六&background=random' },
        { id: '4', name: '孙七', role: '成员', avatar: 'https://ui-avatars.com/api/?name=孙七&background=random' },
        { id: '5', name: '周八', role: '成员', avatar: 'https://ui-avatars.com/api/?name=周八&background=random' }
      ]
      
      // 模拟社团活动数据
      this.clubActivities = [
        {
          id: '1',
          title: '春季摄影外拍',
          time: '2024-03-15 14:00',
          location: '校园西湖边',
          status: '报名中',
          participantCount: 25
        },
        {
          id: '2',
          title: '摄影技巧分享会',
          time: '2024-03-22 19:00',
          location: '教学楼301',
          status: '已结束',
          participantCount: 35
        },
        {
          id: '3',
          title: '摄影作品展',
          time: '2024-04-01 10:00',
          location: '图书馆展览厅',
          status: '即将开始',
          participantCount: 0
        }
      ]
    },
    // 申请加入社团
    applyToJoinClub(clubId: string, application: any) {
      // 模拟申请加入社团
      return true
    }
  }
})
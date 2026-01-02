import { defineStore } from 'pinia'

export interface User {
  id: string
  studentId: string
  name: string
  major: string
  grade: string
  phone: string
  email: string
  avatar: string
}

export const useUserStore = defineStore('user', {
  state: () => ({
    isLoggedIn: localStorage.getItem('isLoggedIn') === 'true',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null') as User | null,
    token: localStorage.getItem('token') || ''
  }),
  actions: {
    login(studentId: string, password: string) {
      // 模拟登录
      this.isLoggedIn = true
      this.userInfo = {
        id: '1',
        studentId: studentId,
        name: '张三',
        major: '计算机科学与技术',
        grade: '2021级',
        phone: '13800138000',
        email: 'zhangsan@example.com',
        avatar: 'https://ui-avatars.com/api/?name=张三&background=random'
      }
      this.token = 'mock-token-' + Date.now()
      
      // 保存到localStorage
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      localStorage.setItem('token', this.token)
    },
    register(studentId: string, password: string, name: string, major: string, grade: string, phone: string, email: string) {
      // 模拟注册
      this.isLoggedIn = true
      this.userInfo = {
        id: Date.now().toString(),
        studentId: studentId,
        name: name,
        major: major,
        grade: grade,
        phone: phone,
        email: email,
        avatar: `https://ui-avatars.com/api/?name=${name}&background=random`
      }
      this.token = 'mock-token-' + Date.now()
      
      // 保存到localStorage
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      localStorage.setItem('token', this.token)
    },
    logout() {
      this.isLoggedIn = false
      this.userInfo = null
      this.token = ''
      
      // 清除localStorage
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    },
    updateUserInfo(info: Partial<User>) {
      if (this.userInfo) {
        this.userInfo = { ...this.userInfo, ...info }
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      }
    },
    changePassword(oldPassword: string, newPassword: string) {
      // 模拟修改密码
      return true
    }
  }
})
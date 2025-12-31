import { createRouter, createWebHistory } from 'vue-router'
import MainEntry from '@/views/MainEntry.vue'
import StudentLogin from '@/views/student/StudentLogin.vue'
import StudentDashboard from '@/views/student/StudentDashboard.vue'
import StudentClubSquare from '@/views/student/StudentClubSquare.vue'
import StudentActivityCenter from '@/views/student/StudentActivityCenter.vue'
import StudentMyClub from '@/views/student/StudentMyClub.vue'
import StudentMyActivity from '@/views/student/StudentMyActivity.vue'
import StudentActivityDetail from '@/views/student/StudentActivityDetail.vue'
import StudentClubDetail from '@/views/student/StudentClubDetail.vue'
// import StudentApplication from '@/views/student/StudentApplication.vue'
// import ClubMembers from '@/views/student/ClubMembers.vue'
import ClubActivities from '@/views/student/ClubActivities.vue'
import StudentProfile from '@/views/student/StudentProfile.vue'
import StudentReguster from '@/views/student/StudentReguster.vue'
import StudentHelp from '@/views/student/StudentHelp.vue'

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes: [
    { path: '/', name: 'MainEntry', component: MainEntry },
    // 学生端路由
    { path: '/student/login', name: 'StudentLogin', component: StudentLogin },
    { 
      path: '/student/dashboard', 
      name: 'StudentDashboard', 
      component: StudentDashboard,
      meta: { requiresAuth: true } // 需要登录
    },
    // 临时将管理员登录重定向到学生登录页面
    { path: '/admin/login', redirect: '/student/login' },
    { path: '/school/login', redirect: '/student/login' },
    {
      path: '/student/clubsquare',
      name: 'clubsquare',
      component: StudentClubSquare,
    },
    {
      path: '/student/help',
      name: 'StudentHelp',
      component: StudentHelp,
    },
    {
      path: '/student/activitycenter',
      name: 'activitycenter',
      component: StudentActivityCenter,
    },
    {
      path: '/student/myclub',
      name: 'myclub',
      component: StudentMyClub,
    },
    {
      path: '/student/myactivity',
      name: 'myactivity',
      component: StudentMyActivity,
    },
    {
      path: '/student/activity-detail/:activityId', // 使用动态参数 :id
      name: 'StudentActivityDetail',
      component: StudentActivityDetail
    },

    {
      path: '/student/club-detail/:clubId', // 使用动态参数 :id
      name: 'StudentClubDetail',
      component: StudentClubDetail
    },

    // {
    //   path: '/student/application/:clubId', // 使用动态参数 :clubId
    //   name: 'StudentApplication',
    //   component: StudentApplication
    // },
    {
      path: '/student/application/:clubId',
      name: 'StudentApplication',
      component: () => import('@/views/student/StudentApplication.vue'),
      props: true 
    },

  {
    path: '/student/club/:clubId/members',
    name: 'ClubMembers',
    component: () => import('@/views/student/ClubMembers.vue')
  },

    {
      path: '/club/:clubId/activities',
      name: 'ClubActivities',
      component: ClubActivities
    },
    {
      path: '/student/profile',
      name: 'StudentProfile',
      component: StudentProfile,
      meta: { requiresAuth: true }
    },

    {
      path: '/student/register',
      name: 'StudentReguster',
      component: StudentReguster,
      meta: { requiresAuth: false }
    },



  ]
})

// 登录拦截
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('studentToken')
  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ name: 'StudentLogin' })
  } else {
    next()
  }
})

export default router
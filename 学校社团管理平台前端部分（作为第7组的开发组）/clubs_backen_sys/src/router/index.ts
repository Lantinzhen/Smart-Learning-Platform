import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login/Login.vue'
import Layout from '@/views/Layout/Layout.vue'
import Dashboard from '@/views/MainViews/Dashboard.vue'
import Activity from '@/views/MainViews/ActivityManagement.vue'
import Member from '@/views/MainViews/MemberManagement.vue'
import Application from '@/views/MainViews/ApplicationManagement.vue'
import ActivityDetail from '@/views/Detail/ActivityDetail.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 默认路由
    {
      path: '/',
      redirect: '/login'
    },
    {
      path:'/login',
      name:'login',
      component:Login,
      // 身份验证
      meta: { requiresAuth: false }
    },
    {
      path:'/layout',
      name:'layout',
      component:Layout,
      // 身份验证
      meta: { requiresAuth: true },
      children:[
        {
          //仪表盘
          path:'/dashboard',
          name:'dashboard',
          component:Dashboard,
        },
        {
          //活动管理
          path:'/activity',
          name:'activity',
          component:Activity,
        },
        {
          //成员管理
          path:'/member',
          name:'member',
          component:Member,
        },
        {
          //申请管理
          path:'/application',
          name:'application',
          component:Application,
        },
        {
          //活动详情
          path:'/activity/:id',
          name:'activityDetail',
          props:true,
          component:ActivityDetail,
        }
      ]
    }
    
  ],
})

//全局前置守卫(beforeEach)，用于路由权限控制。
router.beforeEach((to,from,next) => { 
  // 判断当前路由是否需要登录权限
  const requireAuth = to.meta.requiresAuth !==false
  // 判断当前用户是否登录
  const isLogin = localStorage.getItem('admin_isLogin') ==='true'
  // 登录验证
  if(requireAuth && !isLogin){
    // 跳转到登录页面
    next('/login')
  }else{
    // 允许通过
    next()
  }
})


export default router

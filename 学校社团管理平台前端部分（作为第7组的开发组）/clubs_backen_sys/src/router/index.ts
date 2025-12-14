import { createRouter, createWebHistory } from "vue-router";
import Login from "@/views/Login/Login.vue";
import Layout from "@/views/Layout/Layout.vue";
import Dashboard from "@/views/MainViews/Dashboard.vue";
import Activity from "@/views/MainViews/ActivityManagement.vue";
import Member from "@/views/MainViews/MemberManagement.vue";
import Application from "@/views/MainViews/ApplicationManagement.vue";
import ActivityDetail from "@/views/Detail/ActivityDetail.vue";
import AdminLogin from "@/views/admin/login/login.vue";
import AdminLayout from "@/views/admin/layout/Layout.vue";
import AdminDashboard from "@/views/admin/mainViews/Dashboard.vue";
import AdminActivity from "@/views/admin/mainViews/ActivityManagement.vue";
import AdminMember from "@/views/admin/mainViews/MemberManagement.vue";
import AdminClubMember from "@/views/admin/mainViews/clubAdminManagement.vue";
import AdminClub from "@/views/admin/mainViews/clubManagement.vue";
import AdminApplication from "@/views/admin/mainViews/ApplicationManagement.vue";
import AdminActivityDetail from "@/views/admin/Detail/ActivityDetail.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 默认路由
    {
      path: "/",
      redirect: "/login",
    },
    {
      path: "/login",
      name: "login",
      component: Login,
      // 身份验证
      meta: { requiresAuth: false },
    },
    {
      path: "/admin/login",
      name: "adminLogin",
      component: AdminLogin,
      // 身份验证
      meta: { requiresAuth: false },
    },
    {
      path: "/layout",
      name: "layout",
      component: Layout,
      // 身份验证
      meta: { requiresAuth: true },
      children: [
        {
          //仪表盘
          path: "/dashboard",
          name: "dashboard",
          component: Dashboard,
        },
        {
          //活动管理
          path: "/activity",
          name: "activity",
          component: Activity,
        },
        {
          //成员管理
          path: "/member",
          name: "member",
          component: Member,
        },
        {
          //申请管理
          path: "/application",
          name: "application",
          component: Application,
        },
        {
          //活动详情
          path: "/activity/:id",
          name: "activityDetail",
          props: true,
          component: ActivityDetail,
        },
      ],
    },
    {
      path: "/admin/layout",
      name: "adminLayout",
      component: AdminLayout,
      // 身份验证
      meta: { requiresAuth: true },
      children: [
        {
          //用户管理
          path: "member",
          name: "adminMember",
          component: AdminMember,
        },
        {
          //社团管理员管理
          path: "clubAdmin",
          name: "clubAdmin",
          component: AdminClubMember,
        },
        {
          //社团管理员管理
          path: "club",
          name: "club",
          component: AdminClub,
        },
        {
          //活动审批
          path: "activity",
          name: "adminActivity",
          component: AdminActivity,
        },
        {
          //申请管理
          path: "application",
          name: "adminApplication",
          component: AdminApplication,
        },
        {
          //活动详情
          path: "activity/:id",
          name: "adminActivityDetail",
          props: true,
          component: AdminActivityDetail,
        },
        {
          //数据分析于报告
          path: "dashboard",
          name: "adminDashboard",
          component: AdminDashboard,
        },
      ],
    },
  ],
});

//全局前置守卫(beforeEach)，用于路由权限控制。
router.beforeEach((to, from, next) => {
  // 判断当前路由是否需要登录权限
  const requireAuth = to.meta.requiresAuth !== false;
  // 判断当前用户是否登录
  const isLogin = localStorage.getItem("admin_isLogin") === "true";
  // 登录验证
  if (requireAuth && !isLogin) {
    console.log("未登录");
    // 跳转到登录页面
    next("/login");
  } else {
    // 允许通过
    next();
  }
});

export default router;

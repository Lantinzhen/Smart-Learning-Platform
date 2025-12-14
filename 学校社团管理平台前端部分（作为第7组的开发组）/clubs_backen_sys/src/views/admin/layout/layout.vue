<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="layout-header">
      <!-- 顶部 左侧-->
      <div class="header-left">
        <button @click="toggleSidebar" class="sidebar-toggle">
          <el-icon v-if="sidebarCollapsed">

            <DArrowRight />
          </el-icon>
          <el-icon v-else>
            <DArrowLeft />
          </el-icon>

        </button>
        <h1 class="system-title">社团管理系统
        </h1>
      </div>
      <!-- 顶部 右侧 -->
      <div class="header-right">
        <!-- 创建下拉菜单 -->
        <el-dropdown trigger="hover" placement="bottom">
          <!-- 创建下拉菜单按钮 -->
          <el-button type="text" class="user-button">
            <el-avatar :size="32" :src="userAvatar" />
            <span class="user-name">{{ userInfo }}</span>
            <el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
            <!-- 下拉菜单 -->
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="item in dropdownItems" :key="item.label" @click="item.handler">
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                {{ item.label }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <div class="layout-main">
      <!-- 左侧侧边栏 -->
      <el-aside :width="sidebarCollapsed ? '64px' : '180px'" class="layout-sidebar">

        <!-- 主要内容 -->
        <el-scrollbar>
          <!-- 侧边栏菜单 -->
          <el-menu :default-active="route.path" class="sidebar-menu" router :collapse="sidebarCollapsed"
            :uniqueOpened="true">
            <el-menu-item v-for="item in menuItems" :key="item.index" :index="item.index">
              <el-icon>
                <component :is="item.icon" />
              </el-icon>
              <template #title>
                <span>{{ item.title }}</span>
              </template>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
      </el-aside>
      <!-- 右侧内容区 -->
      <el-main class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "../../../stores/user";
import { useAppStore } from "../../../stores/app";

const router = useRouter();
const route = useRoute();

// 应用程序store
const appStore = useAppStore();
// 用户store
const userStore = useUserStore();

// 用户头像
const userAvatar = ref("");
const userInfo = computed(() => localStorage.getItem("admin_name"));

// 侧边栏折叠状态
const sidebarCollapsed = computed({
  get: () => appStore.sidebarCollapsed,
  set: (value) => appStore.setSidebarCollapsed(value),
});
// 切换侧边栏
const toggleSidebar = () => {
  appStore.toggleSidebar();
};

const handleProfile = () => {
  // 跳转到个人中心页面
  // router.push("/profile");
  ElMessage.info("个人中心功能未完成");
};

// 切换主题
const handleThemeToggle = () => {
  appStore.toggleTheme();

  ElMessage.success("切换主题成功");
};

const handleLogout = () => {
  ElMessageBox.confirm("确定要退出登录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("退出登录成功");
      // 登出
      userStore.logout();
      // 跳转到登录页面
      router.push("/login");
    })
    .catch(() => {
      ElMessage.info("取消退出登录");
    });
};

onMounted(() => {
  // 初始化应用程序设置
  appStore.init();
  // 生成随机头像
  if (userInfo.value) {
    const name = userInfo.value;
    const initial = name.charAt(0).toUpperCase();
    userAvatar.value = `https://ui-avatars.com/api/?name=${initial}&background=random&color=fff&size=32`;
  }
});

// 定义下拉菜单项配置
const dropdownItems = [
  // {
  //   label: "个人中心",
  //   icon: "User",
  //   divided: false,
  //   handler: handleProfile,
  // },
  {
    label: "切换主题",
    icon: "Sunny",
    divided: false,
    handler: handleThemeToggle,
  },

  {
    label: "退出登录",
    icon: "SwitchButton",
    divided: true,
    handler: handleLogout,
  },
];

// 定义菜单项配置
const menuItems = [
  {
    index: "/admin/layout/member",
    icon: "User",
    title: "学生管理",
  },
  {
    index: "/admin/layout/clubAdmin",
    icon: "User",
    title: "社团管理员",
  },
  {
    index: "/admin/layout/club",
    icon: "User",
    title: "社团管理",
  },
  {
    index: "/admin/layout/activity",
    icon: "Suitcase",
    title: "活动审批",
  },
  {
    index: "/admin/layout/dashboard",
    icon: "House",
    title: "数据分析",
  },
];
</script>
<style scoped>
/* Apple Design System 字体栈 */
.layout-container {
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.layout-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 顶部导航栏 - Apple风格 */
.layout-header {
  height: 64px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.05),
    0 1px 3px rgba(0, 0, 0, 0.04);
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sidebar-toggle {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  border-radius: 8px;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 18px;
  padding: 0;
}

.sidebar-toggle:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #007AFF;
}

.sidebar-toggle:active {
  background: rgba(0, 0, 0, 0.1);
  transform: scale(0.95);
}

.system-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #1d1d1f;
  letter-spacing: -0.3px;
  line-height: 1.2;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: none !important;
}

.user-button:hover {
  background: rgba(0, 0, 0, 0.05);
}

.user-button:active {
  background: rgba(0, 0, 0, 0.1);
  transform: scale(0.98);
}

.user-name {
  font-size: 15px;
  font-weight: 500;
  color: #1d1d1f;
  letter-spacing: -0.2px;
}

/* 主要内容 */
.layout-main {
  flex: 1;
  display: flex;
  overflow: hidden;
  background: #f5f7fa;
}

/* 左侧侧边栏 - Apple风格 */
.layout-sidebar {
  height: calc(100vh - 64px);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-right: 0.5px solid rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 1px 0 3px rgba(0, 0, 0, 0.02);
}

.sidebar-menu {
  background-color: transparent;
  border-right: none;
  padding: 8px;
}

.sidebar-menu .el-menu-item {
  color: #1d1d1f;
  height: 44px;
  line-height: 44px;
  font-size: 15px;
  padding: 0 12px;
  margin-bottom: 4px;
  border-radius: 10px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 400;
  letter-spacing: -0.2px;
}

.sidebar-menu .el-menu-item:hover {
  color: #007AFF;
  background: rgba(0, 122, 255, 0.08);
}

.sidebar-menu .el-menu-item.is-active {
  color: #007AFF;
  background: rgba(0, 122, 255, 0.12);
  font-weight: 500;
}

.sidebar-menu .el-menu-item.is-active::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: #007AFF;
  border-radius: 0 2px 2px 0;
}

/* 菜单图标样式 */
:deep(.sidebar-menu .el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 12px;
  width: 20px;
  text-align: center;
}

.layout-content {
  flex: 1;
  padding: 24px;
  background: #f5f7fa;
  overflow-y: auto;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 滚动条样式 - Apple风格 */
:deep(.el-scrollbar__bar) {
  opacity: 0.6;
}

:deep(.el-scrollbar__thumb) {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

:deep(.el-scrollbar__thumb:hover) {
  background: rgba(0, 0, 0, 0.3);
}

/* 下拉菜单样式 - Apple风格 */
:deep(.el-dropdown-menu) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08),
    0 1px 2px rgba(0, 0, 0, 0.04);
  padding: 8px;
  margin-top: 8px;
}

:deep(.el-dropdown-menu__item) {
  height: 40px;
  line-height: 40px;
  padding: 0 12px;
  border-radius: 8px;
  font-size: 15px;
  color: #1d1d1f;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 2px;
}

:deep(.el-dropdown-menu__item:hover) {
  background: rgba(0, 0, 0, 0.05);
  color: #007AFF;
}

:deep(.el-dropdown-menu__item.is-divided) {
  border-top: 0.5px solid rgba(0, 0, 0, 0.08);
  margin-top: 4px;
  padding-top: 4px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  margin-right: 8px;
  font-size: 16px;
}

/* 暗黑模式 - Apple风格 */
[data-theme="dark"] .layout-container {
  background: #000000;
}

[data-theme="dark"] .layout-header {
  background: rgba(28, 28, 30, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 0.5px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 1px 0 rgba(255, 255, 255, 0.05),
    0 1px 3px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .sidebar-toggle {
  color: #f5f5f7;
}

[data-theme="dark"] .sidebar-toggle:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #0a84ff;
}

[data-theme="dark"] .system-title {
  color: #f5f5f7;
}

[data-theme="dark"] .user-name {
  color: #f5f5f7;
}

[data-theme="dark"] .user-button:hover {
  background: rgba(255, 255, 255, 0.1);
}

[data-theme="dark"] .layout-sidebar {
  background: rgba(28, 28, 30, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-right: 0.5px solid rgba(255, 255, 255, 0.1);
}

[data-theme="dark"] .sidebar-menu .el-menu-item {
  color: #f5f5f7;
}

[data-theme="dark"] .sidebar-menu .el-menu-item:hover {
  color: #0a84ff;
  background: rgba(10, 132, 255, 0.15);
}

[data-theme="dark"] .sidebar-menu .el-menu-item.is-active {
  color: #0a84ff;
  background: rgba(10, 132, 255, 0.2);
}

[data-theme="dark"] .sidebar-menu .el-menu-item.is-active::before {
  background: #0a84ff;
}

[data-theme="dark"] .layout-content {
  background: #000000;
}

[data-theme="dark"] :deep(.el-dropdown-menu) {
  background: rgba(28, 28, 30, 0.95);
  border: 0.5px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.4),
    0 1px 2px rgba(0, 0, 0, 0.2);
}

[data-theme="dark"] :deep(.el-dropdown-menu__item) {
  color: #f5f5f7;
}

[data-theme="dark"] :deep(.el-dropdown-menu__item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: #0a84ff;
}

[data-theme="dark"] :deep(.el-dropdown-menu__item.is-divided) {
  border-top: 0.5px solid rgba(255, 255, 255, 0.1);
}

/* 页面过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-header {
    padding: 0 16px;
  }

  .system-title {
    font-size: 18px;
  }

  .layout-content {
    padding: 16px;
  }
}
</style>

<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="layout-header">
      <!-- 顶部 左侧-->
      <div class="header-left">
        <button @click="toggleSidebar"
                class="sidebar-toggle">
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
        <el-dropdown trigger="hover"
                     placement="bottom">
          <!-- 创建下拉菜单按钮 -->
          <el-button type="text"
                     class="user-button">
            <el-avatar :size="32"
                       :src="userAvatar" />
            <span class="user-name">{{ userInfo }}</span>
            <el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
            <!-- 下拉菜单 -->
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="item in dropdownItems"
                                :key="item.label"
                                @click="item.handler">
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                {{item.label}}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <div class="layout-main">
      <!-- 左侧侧边栏 -->
      <el-aside :width="sidebarCollapsed ? '64px' : '180px'"
                class="layout-sidebar">

        <!-- 主要内容 -->
        <el-scrollbar>
          <!-- 侧边栏菜单 -->
          <el-menu :default-active="route.path"
                   class="sidebar-menu"
                   router
                   :collapse="sidebarCollapsed"
                   :uniqueOpened="true">
            <el-menu-item v-for="item in menuItems"
                          :key="item.index"
                          :index="item.index">
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
          <transition name="fade"
                      mode="out-in">
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
import { useUserStore } from "../../stores/user";
import { useAppStore } from "../../stores/app";

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
<<<<<<< HEAD
  // {
  //   label: "个人中心",
  //   icon: "User",
  //   divided: false,
  //   handler: handleProfile,
  // },
=======
  {
    label: "个人中心",
    icon: "User",
    divided: false,
    handler: handleProfile,
  },
>>>>>>> 4d7e1947506a809d715c2ad8e2e93f83fbb1cb74
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
    index: "/dashboard",
    icon: "House",
    title: "管理概览",
  },
  {
    index: "/activity",
    icon: "Suitcase",
    title: "活动管理",
  },
  {
    index: "/member",
    icon: "User",
    title: "成员管理",
  },
  {
    index: "/application",
    icon: "DocumentAdd",
    title: "申请管理",
  },
];
</script>
<style scoped>
.layout-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  transition: all 0.3s ease-in-out;
}
/* 顶部导航栏 */
.layout-header {
  height: 60px;
  background-color: #fff;
  /* border-bottom: 1px solid #fff; */
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 35px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease-in-out;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.sidebar-toggle {
  margin-top: 5px;
  font-size: 20px;
  margin-right: 10px;
  color: #000;
  vertical-align: bottom;
  height: 30px;
  line-height: 30px;
  border: none;
  background: none;
}
.sidebar-toggle:hover {
  color: #0084f7;
}
.system-title {
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-button {
  display: flex;
  align-items: center;
  gap: 10px;
  /* padding: 10px; */
  /* border: none !important; */
  box-shadow: none !important;
  border-radius: 30px;
}

.user-button:hover {
  border: 1px solid #4ade80;
  box-shadow: 0 0 0 2px rgba(74, 222, 128, 0.1);
}

.user-name {
  font-size: 16px;
  font-weight: bold;
  margin-left: 5px;
  background: linear-gradient(135deg, #4ade80, #facc15);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
/* 主要内容 */
.layout-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}
/* 左侧侧边栏 */
.layout-sidebar {
  height: calc(100vh -60px);
  background: linear-gradient(135deg, #00c9a7, #ffd166);
  overflow: hidden;
  /* 折叠状态 */
  transition: all 0.3s ease-in-out;
}

.sidebar-menu {
  background-color: transparent;
  border-right: none;
}

.sidebar-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.65);
  height: 56px;
  line-height: 56px;
  font-size: 14px;
  padding: 0 20px;
  transition: all 0.3s;
}

.sidebar-menu .el-menu-item.is-active {
  color: #fff;
  background: linear-gradient(90deg, #ffd166, #00c9a7);
  box-shadow: 0 2px 8px rgba(0, 201, 167, 0.3);
}

.sidebar-menu .el-menu-item:hover {
  color: #fff;
  background: linear-gradient(90deg, #ffd166, #00c9a7);
}

.layout-content {
  transition: all 0.3s ease-in-out;
}

/* 暗黑模式 */
[data-theme="dark"] .layout-container {
  background-color: #000;
}
/* 头部 */
[data-theme="dark"] .layout-header {
  background-color: #000;
  /* box-shadow: 0 2px 12px 0 rgba(#fff, #fff, #fff, 0.1); */
  box-shadow: 0 2px 12px 0 #ffffff40;
}

/* 切换按钮 */
[data-theme="dark"] .sidebar-toggle {
  color: #fff;
}
[data-theme="dark"] .sidebar-toggle:hover {
  color: #0084f7;
}
/* 侧边栏 */
[data-theme="dark"] .layout-sidebar {
  background: linear-gradient(135deg, #01977e, #8f6401);
  opacity: 0.9;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

<template>
  <!-- 导航栏组件 -->
  <nav class="mynavbar">
    <div class="nav-brand">
      <h1>社团管理系统</h1>
      <span>学生用户中心</span>
    </div>
    <div class="nav-menu">
      <a 
        href="#" 
        class="nav-item" 
        :class="{ active: currentRoute === 'dashboard' }"
        @click.prevent="navigateTo('StudentDashboard')"
      >
        我的主页
      </a>
      <a 
        href="#" 
        class="nav-item" 
        :class="{ active: currentRoute === 'clubsquare' }"
        @click.prevent="navigateTo('clubsquare')"
      >
        社团广场
      </a>
      <a 
        href="#" 
        class="nav-item" 
        :class="{ active: currentRoute === 'activitycenter' }"
        @click.prevent="navigateTo('activitycenter')"
      >
        活动中心
      </a>
      <a 
        href="#" 
        class="nav-item" 
        :class="{ active: currentRoute === 'myclub' }"
        @click.prevent="navigateTo('myclub')"
      >
        我的社团
      </a>
      <a 
        href="#" 
        class="nav-item" 
        :class="{ active: currentRoute === 'myactivity' }"
        @click.prevent="navigateTo('myactivity')"
      >
        我的活动
      </a>
      <a 
        href="#" 
        class="nav-item" 
        :class="{ active: currentRoute === 'help' }"
        @click.prevent="navigateTo('StudentHelp')"
      >
        帮助与支持
      </a>
    </div>
    <div class="user-info" @click="navigateToProfile">
      <div class="user-avatar">
        <span>{{ userName.charAt(0) }}</span>
      </div>
      <div class="user-details">
        <div class="user-name">{{ userName }}</div>
        <div class="user-id">学号: {{ userId }}</div>
        <div class="user-status online">在线</div>
      </div>
      <button class="logout-btn" @click.stop="handleLogout">退出登录</button>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'myNavbar',
  // 接收父组件传递的属性（用户信息、当前路由）
  props: {
    userName: {
      type: String,
      required: true,
      default: '未知用户'
    },
    userId: {
      type: String,
      required: true,
      default: '未知学号'
    },
    currentRoute: {
      type: String,
      required: true,
      default: '' // 用于高亮当前导航项
    }
  },
  methods: {
    // 导航跳转（通过事件通知父组件，避免组件依赖路由）
    navigateTo(routeName) {
      this.$emit('navigate', routeName);
    },
    // 新增：跳转到用户资料页面
    navigateToProfile() {
      this.$emit('navigate', 'StudentProfile');
    },
    
    // 退出登录（通过事件通知父组件处理逻辑）
    handleLogout() {
      this.$emit('logout');
    }
  }
};
</script>

<style scoped>
/* 导航栏样式（提取原代码中的导航相关样式） */
.mynavbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.nav-brand h1 {
  font-size: 24px;
  margin-bottom: 5px;
}

.nav-brand span {
  font-size: 14px;
  opacity: 0.8;
}

.nav-menu {
  display: flex;
  gap: 30px;
}

.nav-item {
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.nav-item:hover,
.nav-item.active {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6a11cb, #2575fc);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
}

.user-details {
  text-align: right;
}

.user-name {
  font-weight: 600;
}

.user-id {
  font-size: 12px;
  opacity: 0.8;
}

.user-status {
  font-size: 12px;
  margin-top: 2px;
}

.user-status.online {
  color: #4CAF50;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .mynavbar {
    flex-direction: column;
    gap: 15px;
    padding: 15px;
  }
  
  .nav-menu {
    gap: 15px;
    flex-wrap: wrap;
    justify-content: center;
  }
}

</style>
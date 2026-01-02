<template>
  <div class="main-entry-container">
    <!-- 顶部标题区域 -->
    <header class="entry-header">
      <div class="logo-container">
        <!-- 替换为系统图标 -->
        <i class="icon-school logo-icon"></i>
        <h1 class="system-title">社团管理系统</h1>
      </div>
      <p class="system-desc">为校园社团活动提供便捷的资源预约和管理服务</p>
      <div class="features">
        <span class="feature-item">
          <!-- 安全图标 -->
          <i class="icon-shield feature-icon"></i>
          安全可靠
        </span>
        <span class="feature-item">
          <!-- 实时更新图标 -->
          <i class="icon-sync feature-icon"></i>
          实时更新
        </span>
        <span class="feature-item">
          <!-- 社区互动图标 -->
          <i class="icon-group feature-icon"></i>
          社区互动
        </span>
      </div>
    </header>

    <!-- 角色入口卡片区域 -->
    <main class="role-cards">
      <!-- 学生用户卡片 -->
      <el-card class="role-card student-card" @mouseenter="hoverCard = 'student'" @mouseleave="hoverCard = ''">
        <div class="card-icon student-icon">
          <!-- 学生图标 -->
          <i class="icon-student"></i>
        </div>
        <h2 class="card-title">学生用户</h2>
        <p class="card-desc">参与社团活动，申请加入社团</p>
        <div class="card-features">
          <div class="feature-item">
            <i class="icon-check"></i> 免费参与活动
          </div>
          <div class="feature-item">
            <i class="icon-check"></i> 积分奖励系统
          </div>
        </div>
        <el-button 
          type="primary" 
          class="login-btn"
          :class="{ 'hover-btn': hoverCard === 'student' }"
          @click="$router.push('/student/login')"
        >
          <i class="icon-login"></i> 学生登录
        </el-button>
      </el-card>
    </main>

    <!-- 底部特性区域 -->
    <footer class="entry-footer">
      <div class="copyright">©2025 学校社团管理中心 版权所有</div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElCard, ElButton } from 'element-plus'

const hoverCard = ref('') // 卡片悬浮状态
</script>

<style scoped>
.main-entry-container {
  width: 100%;
  min-height: 100vh;
  padding: 4rem 2rem;
  box-sizing: border-box;
  background-color: #e6f2ff;
  background-image: radial-gradient(circle at 20% 20%, rgba(173, 219, 255, 0.3) 0%, transparent 50%),
                    radial-gradient(circle at 80% 80%, rgba(224, 242, 255, 0.3) 0%, transparent 50%);
}

.entry-header {
  text-align: center;
  margin-bottom: 4rem;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.logo-img {
  width: 40px;
  height: 40px;
}

.system-title {
  font-size: 2.5rem;
  color: #1E88E5;
  margin: 0;
  font-weight: bold;
}

.system-desc {
  font-size: 1rem;
  color: #666;
  margin: 0.5rem 0;
}

.features {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1rem;
}

.feature-item {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.8rem;
  color: #666;
}

/* 顶部特性图标 */
.feature-icon {
  font-size: 1rem;
  color: #1E88E5;
}

/* 图标样式（使用Font Awesome） */
.icon-shield:before { content: "🛡️"; }
.icon-sync:before { content: "🔄"; }
.icon-group:before { content: "👥"; }
.icon-student:before { content: "🎓"; font-size: 5rem; display: block; }
.icon-admin:before { content: "👨‍💼"; font-size: 5rem; display: block; }
.icon-school:before { content: "🏫"; font-size: 5rem; display: block; }
.icon-login:before { content: "➡️"; margin-right: 5px; }

.icon-school,
.icon-admin {
  font-style: normal; /* 移除倾斜 */
}

/* 自定义图标图片 */
/* .icon-student {
  display: block;
  width: 80px;
  height: 80px;
  background-image: url('@/assets/student-icon.png');
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
} */

.role-cards {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
  margin-bottom: 4rem;
}

.role-card {
  width: 320px;
  height: 450px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  overflow: hidden;
  position: relative;
}

.role-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 16px 32px rgba(0,0,0,0.15);
}

.card-icon {
  font-size: 3rem;
  margin-bottom: 1.5rem;
  color: #1E88E5;
  width: 100%;
  height: 175px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.student-icon { color: #1E88E5; }
.admin-icon { color: #FF9800; }
.school-icon { color: #4CAF50; }

.card-title {
  font-size: 1.4rem;
  font-weight: 700;
  margin-bottom: 0.8rem;
  color: #333;
}

.card-desc {
  font-size: 0.9rem;
  color: #666;
  text-align: center;
  margin-bottom: 1.5rem;
  padding: 0 1rem;
}

.card-features {
  margin-bottom: 1.5rem;
  width: 100%;
  padding: 0 1rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.icon-check {
  display: inline-block;
  width: 12px;
  height: 12px;
  background-color: #4CAF50;
  border-radius: 50%;
  color: white;
  font-size: 0.8rem;
  line-height: 12px;
  text-align: center;
}

.login-btn {
  width: 100%;
  height: 45px;
  border-radius: 25px;
  font-size: 0.9rem;
  border: none;
  padding: 0;
  margin: 0;
}

.hover-btn {
  background-color: #1976D2;
}

.entry-footer {
  text-align: center;
  margin-top: 2rem;
}

.copyright {
  font-size: 0.8rem;
  color: #999;
}
/* 新增 logo-icon 样式 */
.logo-icon {
  font-size: 40px;
  color: #1E88E5;
  display: inline-block;
}
</style>
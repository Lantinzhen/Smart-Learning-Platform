<template>
  <div class="my-clubs-page">
    <!-- 顶部导航栏 -->
    <MyNavbar 
      :user-name="userInfo.name" 
      :user-id="userInfo.id" 
      :current-route="currentRoute"
      @navigate="handleNavigate"
      @logout="handleLogout"
    />

    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="page-header">
        <h1>我的社团</h1>
        <p>管理您加入的社团，查看社团动态和活动</p>
      </div>

      <div class="content-layout">
        <!-- 已加入社团列表 -->
        <div class="main-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="fas fa-user-friends"></i>
              已加入社团
            </h2>
            <div class="section-actions">
              <button class="btn-explore" @click="goToClubSquare">
                <i class="fas fa-plus"></i>
                探索更多社团
              </button>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <i class="fas fa-spinner fa-spin"></i>
            加载中...
          </div>

          <!-- 社团列表 -->
          <div class="clubs-container">
            <div 
              v-for="club in joinedClubs" 
              :key="club.clubId" 
              class="club-card"
              @click="viewClubDetail(club.clubId)"
            >
              <div class="club-header">
                <div class="club-avatar">
                  <i :class="getClubIcon(club.categoryName)"></i>
                </div>
                <div class="club-info">
                  <h3 class="club-name">{{ club.name }}</h3>
                  <div class="club-meta">
                    <span class="member-count">
                      <i class="fas fa-tag"></i>
                      {{ club.categoryName }}
                    </span>
                    <span class="activity-count">
                      <i class="fas fa-calendar"></i>
                      加入时间：{{ formatDate(club.joinDate) }}
                    </span>
                  </div>
                </div>
                <div class="club-role">
                  <span class="role-badge" :class="getRoleClass(club.role)">{{ club.role }}</span>
                </div>
              </div>

              <div class="club-stats">
                <div class="stat-item">
                  <div class="stat-value">{{ club.points }}</div>
                  <div class="stat-label">我的积分</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ getRoleLevel(club.role) }}</div>
                  <div class="stat-label">角色等级</div>
                </div>
              </div>

              <div class="club-actions">


                <button 
                  v-if="club.role !== '社长' && club.role !== '社长(创始人)'"
                  class="btn-action btn-quit" 
                  @click.stop="quitClub(club)"
                >
                  <i class="fas fa-sign-out-alt"></i>
                  退出社团
                </button>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="!loading && joinedClubs.length === 0" class="empty-state">
            <i class="fas fa-users-slash"></i>
            <h3>您还没有加入任何社团</h3>
            <p>去社团广场发现感兴趣的社团吧！</p>
            <button class="btn-explore" @click="goToClubSquare">
              探索社团
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 退出社团确认对话框 -->
    <div v-if="showQuitDialog" class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3>退出社团确认</h3>
          <button class="close-btn" @click="showQuitDialog = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-content">
          <p>确定要退出"{{ selectedClub?.name }}"社团吗？</p>
          <p class="warning-text">退出后您将失去在该社团的所有积分和记录</p>
          <div class="quit-reason">
            <label>退出原因（可选）:</label>
            <textarea v-model="quitReason" placeholder="请简要说明退出原因..."></textarea>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="showQuitDialog = false">
            再考虑一下
          </button>
          <button class="btn-danger" @click="confirmQuit">
            确认退出
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import MyNavbar from '@/components/MyNavbar.vue';
import { clubApi, profileApi } from '@/api/studentApi.js';

export default {
  name: 'MyClubs',
  components: { MyNavbar },
  data() {
    return {
      userInfo: {
        name: '',
        id: ''
      },
      currentRoute: 'myclub',
      loading: true,
      joinedClubs: [],
      showQuitDialog: false,
      selectedClub: null,
      quitReason: ''
    }
  },
  mounted() {
    this.loadUserInfo();
    this.fetchMyClubs();
  },
  methods: {
    // 从localStorage加载用户信息
  async loadUserInfo() {
    try {
      const userData = await profileApi.getProfile();
      this.userInfo = {
        name: userData.name || '学生',
        id: userData.studentId || userData.id || ''
      };
    } catch (error) {
      console.error('获取用户信息失败:', error);
      const cachedInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
      this.userInfo = {
        name: cachedInfo.name || '学生',
        id: cachedInfo.studentId || cachedInfo.id || ''
      };
    }
  },

    // 获取我的社团列表
    async fetchMyClubs() {
      this.loading = true;
      try {
        const response = await clubApi.getMyClubs();
        this.joinedClubs = response || [];
      } catch (error) {
        console.error('获取我的社团失败:', error);
        this.$message.error('获取社团列表失败: ' + (error.message || '未知错误'));
      } finally {
        this.loading = false;
      }
    },

    // 根据社团分类获取图标
    getClubIcon(categoryName) {
      const iconMap = {
        '学术科技类': 'fas fa-flask',
        '文化艺术类': 'fas fa-palette',
        '体育健身类': 'fas fa-running',
        '公益服务类': 'fas fa-hands-helping',
        '创新创业类': 'fas fa-lightbulb',
        '音乐舞蹈类': 'fas fa-music',
        '摄影影视类': 'fas fa-camera',
        '文学创作类': 'fas fa-book',
        '计算机类': 'fas fa-code',
        '电子技术类': 'fas fa-microchip'
      };
      return iconMap[categoryName] || 'fas fa-users';
    },

    // 根据角色获取CSS类
    getRoleClass(role) {
      if (role.includes('社长') || role.includes('负责人')) {
        return 'president';
      } else if (role.includes('副') || role.includes('管理员')) {
        return 'vice_president';
      } else {
        return 'member';
      }
    },

    // 获取角色等级（用于显示）
    getRoleLevel(role) {
      if (role.includes('社长') || role.includes('负责人')) {
        return '高级';
      } else if (role.includes('副') || role.includes('管理员')) {
        return '中级';
      } else {
        return '初级';
      }
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
    },

    handleNavigate(routeName) {
      this.$router.push({ name: routeName });
    },

    viewClubDetail(clubId) {
      this.$router.push({
        name: 'StudentClubDetail',
        params: { clubId }
      });
    },
    
    viewClubActivities(clubId) {
      this.$router.push({
        name: 'StudentClubDetail',
        params: { clubId }
      });
    },
    
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('studentToken');
        localStorage.removeItem('userInfo');
        this.$router.push('/student/login');
      }
    },
    
    quitClub(club) {
      this.selectedClub = club;
      this.showQuitDialog = true;
    },
    
    async confirmQuit() {
      if (!this.selectedClub) return;
      
      try {
        // 调用退出社团API
        await clubApi.leaveClub(this.selectedClub.clubId);
        
        // 从本地列表中移除
        this.joinedClubs = this.joinedClubs.filter(club => club.clubId !== this.selectedClub.clubId);
        
        // 关闭对话框并重置
        this.showQuitDialog = false;
        this.selectedClub = null;
        this.quitReason = '';
        
        // 显示成功消息
        this.$message.success('已成功退出社团');
      } catch (error) {
        console.error('退出社团失败:', error);
        this.$message.error('退出社团失败: ' + (error.message || '未知错误'));
      }
    },
    
    goToClubSquare() {
      this.$router.push('/student/clubsquare');
    }
  }
}
</script>
<style scoped>
/* 主内容区域 */
.main-content {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 32px;
  color: #1e3c72;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

/* 内容布局 */
.content-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 30px;
}

/* 主区域 */
.main-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-title {
  font-size: 20px;
  color: #1e3c72;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: #2a5298;
  border-radius: 2px;
}
.section-actions {
  margin-left: auto; /* 将按钮推到右侧 */
}

.btn-explore {
  padding: 10px 20px;
  background: #2a5298;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-explore:hover {
  background: #1e3c72;
  transform: translateY(-2px);
}

/* 社团卡片 */
.clubs-container {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.club-card {
  border: 1px solid #eaeaea;
  border-radius: 12px;
  padding: 25px;
  transition: all 0.3s;
  cursor: pointer;
}

.club-card:hover {
  border-color: #2a5298;
  box-shadow: 0 4px 15px rgba(42, 82, 152, 0.1);
  transform: translateY(-2px);
}

.club-header {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  margin-bottom: 15px;
}

.club-avatar {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.club-info {
  flex: 1;
}

.club-name {
  font-size: 20px;
  font-weight: 600;
  color: #1e3c72;
  margin-bottom: 8px;
}

.club-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.club-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.role-badge {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.role-badge.member {
  background: #2196F3;
}

.role-badge.vice_president {
  background: #FF9800;
}

.role-badge.president {
  background: #F44336;
}

.club-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e3c72;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 12px;
}

.club-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  margin-right: auto; 
}

.btn-action {
  padding: 8px 16px;
  background: #f0f7ff;
  color: #2a5298;
  border: 1px solid #2a5298;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  margin-left: auto;
}

.btn-action:hover {
  background: #2a5298;
  color: white;
  transform: translateY(-1px);
}

.btn-quit {
  background: #fff5f5;
  color: #e74c3c;
  border-color: #e74c3c;
}

.btn-quit:hover {
  background: #e74c3c;
  color: white;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-state i {
  font-size: 60px;
  color: #ccc;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin-bottom: 10px;
  color: #555;
}

.empty-state p {
  margin-bottom: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 15px;
    padding: 15px;
  }

  .nav-menu {
    gap: 15px;
    flex-wrap: wrap;
    justify-content: center;
  }

  .club-header {
    flex-direction: column;
    text-align: center;
  }

  .club-stats {
    grid-template-columns: 1fr;
  }

  .club-actions {
    justify-content: center;
  }

  .main-content {
    padding: 15px;
  }
}

</style>
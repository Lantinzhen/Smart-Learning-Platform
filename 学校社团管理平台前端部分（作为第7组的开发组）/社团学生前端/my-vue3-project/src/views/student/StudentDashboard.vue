<template>
  <div class="student-dashboard">
    <!-- 顶部导航栏 -->
    <MyNavbar 
      :user-name="userInfo.name" 
      :user-id="userInfo.studentId" 
      :current-route="currentRoute"
      @navigate="handleNavigate"
      @logout="handleLogout"
    />

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-content">
          <h1>学生仪表盘</h1>
          <p>查看您的社团活动统计和快速操作</p>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i> 加载中...
      </div>

      <!-- 错误状态 -->
      <div v-else-if="loadingError" class="error-state">
        <i class="fas fa-exclamation-triangle"></i> 
        <p>数据加载失败</p>
        <button @click="loadDashboardData" class="retry-btn">重新加载</button>
      </div>

      <!-- 统计卡片区域 -->
      <div v-else class="stats-section">
        <!-- 我的社团 -->
        <div class="stat-card">
          <div class="stat-header">
            <h3>我的社团</h3>
          </div>
          <div class="stat-value">{{ statistics.totalClubs || 0 }}</div>
          <div class="stat-note">已加入的社团数量</div>
        </div>
        
        <!-- 参与活动 -->
        <div class="stat-card">
          <div class="stat-header">
            <h3>参与活动</h3>
          </div>
          <div class="stat-value">{{ statistics.totalActivities || 0 }}</div>
          <div class="stat-note">已参与的活动数量</div>
        </div>
        
        <!-- 活动积分 -->
        <div class="stat-card">
          <div class="stat-header">
            <h3>活动积分</h3>
          </div>
          <div class="stat-value">{{ personalInfo.points || 0 }}</div>
          <div class="stat-badge">+0 本周</div>
          <div class="stat-note">累计获得积分</div>
        </div>
        
      </div>

      <!-- 内容区域 -->
      <div v-if="!loading && !loadingError" class="content-section">
        <!-- 我的社团 -->
        <div class="clubs-section">
          <div class="section-header">
            <h2>我的社团</h2>
            <button class="view-all-btn" @click="handleNavigate('clubsquare')">
              浏览更多 <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <div class="clubs-list" v-if="myClubs && myClubs.length > 0">
            <div 
              v-for="club in myClubs" 
              :key="club.clubId" 
              class="club-item"
              @click="handleNavigate('clubdetail', { id: club.clubId })"
            >
              <div class="club-icon">
                <i class="fas fa-users"></i>
              </div>
              <div class="club-info">
                <h4>{{ club.name }}</h4>

                <p class="club-role">角色: {{ club.role || '成员' }}</p>
              </div>
              <div class="club-tag">{{ club.categoryName }}</div>
            </div>
          </div>
          <div v-else class="empty-state">
            <i class="fas fa-users-slash"></i>
            <p>尚未加入任何社团</p>
            <button @click="handleNavigate('clubsquare')" class="join-btn">立即加入</button>
          </div>
        </div>

        <!-- 近期活动 -->
        <div class="activities-section">
          <div class="section-header">
            <h2>近期活动</h2>
            <button class="view-all-btn" @click="handleNavigate('activitycenter')">
              查看全部 <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <div class="activities-list" v-if="recentActivities && recentActivities.length > 0">
            <div 
              v-for="activity in recentActivities" 
              :key="activity.activityId" 
              class="activity-item"
              @click="handleNavigate('activitydetail', { id: activity.activityId })"
            >
              <div class="activity-icon" :class="getActivityType(activity)">
                <i :class="getActivityIcon(activity)"></i>
              </div>
              <div class="activity-info">
                <h4>{{ activity.title }}</h4>
                <p>{{ activity.clubName }} · {{ formatDateTime(activity.time || activity.startTime) }}</p>
                <p class="activity-location">{{ activity.location }}</p>
              </div>
              <div class="activity-tag" :class="getActivityStatusClass(activity.status)">
                {{ getActivityStatusLabel(activity.status) }}
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <i class="fas fa-calendar-times"></i>
            <p>暂无活动安排</p>
            <button @click="handleNavigate('activitycenter')" class="join-btn">查看活动</button>
          </div>
        </div>


      </div>
    </main>
  </div>
</template>

<script>
import MyNavbar from '@/components/MyNavbar.vue';
import { dashboardApi, authApi } from '@/api/studentApi';

export default {
  name: 'StudentDashboard',
  
  components: {
    MyNavbar
  },
  
  data() {
    return {
      currentRoute: 'dashboard',
      userInfo: {
        name: '',
        studentId: ''
      },
      // 仪表盘数据
      personalInfo: {},
      recentActivities: [],
      myClubs: [],
      statistics: {},
      loading: true,
      loadingError: false
    }
  },
  
  mounted() {
    this.loadDashboardData();
  },
  
  methods: {
    async loadDashboardData() {
      try {
        this.loading = true;
        this.loadingError = false;
        
        // 获取仪表盘数据
        const dashboardData = await dashboardApi.getDashboard();
        
        console.log('仪表盘数据:', dashboardData); // 调试用
        
        // 更新数据 - 根据API文档调整
        this.personalInfo = dashboardData.personalInfo || {};
        this.recentActivities = dashboardData.recentActivities || [];
        this.myClubs = dashboardData.myClubs || [];
        this.statistics = dashboardData.statistics || {};
        
        // 更新用户信息
        this.userInfo = {
          name: this.personalInfo.name || '学生',
          studentId: this.personalInfo.studentId || ''
        };
        
      } catch (error) {
        console.error('加载仪表盘数据失败:', error);
        this.loadingError = true;
        
        // 使用静态数据作为备用
        this.fallbackData();
      } finally {
        this.loading = false;
      }
    },
    
    fallbackData() {
      // 根据API文档示例数据调整备用数据
      this.personalInfo = {
        studentId: '2022002',
        name: '王二',
        major: '信息安全',
        grade: '2022级',
        points: 3.0
      };
      this.userInfo = {
        name: '王二',
        studentId: '2022002'
      };
      this.recentActivities = [
        { 
          activityId: 4, 
          title: '新生篮球友谊赛', 
          clubName: '篮球社', 
          time: '2023-11-18T15:00', 
          location: '体育馆篮球场',
          status: '已批准'
        },
        { 
          activityId: 5, 
          title: '编程竞赛讲座', 
          clubName: '计算机协会', 
          time: '2023-11-20T14:00', 
          location: '教学楼A301',
          status: '已批准'
        },
        { 
          activityId: 6, 
          title: '摄影作品展', 
          clubName: '摄影社', 
          time: '2023-11-22T10:00', 
          location: '艺术楼展厅',
          status: '已批准'
        }
      ];
      this.myClubs = [
        { 
          clubId: 1, 
          name: '计算机协会', 
          categoryName: '学术科技类', 
          role: '成员', 
          joinDate: '2021-09-15',
          memberCount: 27
        },
        { 
          clubId: 2, 
          name: '篮球社', 
          categoryName: '体育竞技类', 
          role: '成员', 
          joinDate: '2022-03-10',
          memberCount: 15
        }
      ];
      this.statistics = {
        totalClubs: 2,
        totalActivities: 2,
        totalPoints: 3.0,
        ranking: 5
      };
    },
    
    getRankingProgress(ranking) {
      if (!ranking || ranking <= 0) return 0;
      // 假设总人数为100，排名越靠前进度越高
      const progress = Math.max(0, 100 - (ranking * 10));
      return Math.min(progress, 100);
    },
    
    formatDateTime(dateTime) {
      if (!dateTime) return '';
      
      const date = new Date(dateTime);
      const now = new Date();
      const diffDays = Math.floor((date - now) / (1000 * 60 * 60 * 24));
      
      if (diffDays === 0) {
        return `今天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      } else if (diffDays === 1) {
        return `明天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      } else if (diffDays === -1) {
        return `昨天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      } else if (diffDays > 0 && diffDays < 7) {
        const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
        return `${days[date.getDay()]} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      } else {
        return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      }
    },
    
    getActivityType(activity) {
      // 根据活动标题判断活动类型
      const title = activity.title || '';
      if (title.includes('讲座') || title.includes('论坛')) return 'lecture';
      if (title.includes('竞赛') || title.includes('比赛')) return 'competition';
      if (title.includes('工作坊') || title.includes('培训')) return 'workshop';
      if (title.includes('展览') || title.includes('展示')) return 'exhibition';
      if (title.includes('演出') || title.includes('表演')) return 'performance';
      if (title.includes('友谊赛') || title.includes('比赛')) return 'sports';
      return 'default';
    },
    
    getActivityIcon(activity) {
      const type = this.getActivityType(activity);
      const icons = {
        lecture: 'fas fa-microphone',
        competition: 'fas fa-trophy',
        workshop: 'fas fa-laptop-code',
        exhibition: 'fas fa-images',
        performance: 'fas fa-music',
        sports: 'fas fa-running',
        default: 'fas fa-calendar-alt'
      };
      return icons[type] || 'fas fa-calendar-alt';
    },
    
    getActivityStatusClass(status) {
      const classMap = {
        '已批准': 'approved',
        '待审批': 'pending',
        '已取消': 'cancelled',
        '进行中': 'ongoing',
        '已结束': 'ended',
        '审核中': 'reviewing'
      };
      return classMap[status] || 'pending';
    },
    
    getActivityStatusLabel(status) {
      const labelMap = {
        '已批准': '已批准',
        '待审批': '待审批',
        '已取消': '已取消',
        '进行中': '进行中',
        '已结束': '已结束',
        '审核中': '审核中'
      };
      return labelMap[status] || '待审批';
    },
    
handleNavigate(routeName, params) {
  // 根据 routeName 和 params 动态生成路由跳转逻辑
  if (routeName === 'clubdetail' && params && params.id) {
    // 跳转到社团详情页，类似于 StudentClubSquare.vue 的 goToClubPage 方法
    this.$router.push({
      name: 'StudentClubDetail',
      params: { clubId: params.id }
    });
  } else if (routeName === 'activitydetail' && params && params.id) {
    // 跳转到活动详情页
    this.$router.push({
      name: 'StudentActivityDetail',
      params: { activityId: params.id }
    });
  } else {
    // 其他路由跳转保持原有逻辑
    this.$router.push({ name: routeName });
  }
},
    
    async handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        try {
          // 调用退出登录API
          await authApi.logout();
        } catch (error) {
          console.error('退出登录失败:', error);
        } finally {
          // 清除本地存储
          localStorage.removeItem('studentToken');
          localStorage.removeItem('rememberedStudentId');
          
          // 跳转到登录页
          this.$router.push('/student/login');
        }
      }
    }
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f5f7fa;
  color: #333;
  line-height: 1.6;
}

.student-dashboard {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 20px;
  font-size: 18px;
  color: #666;
}

.loading-state i {
  margin-right: 10px;
}

/* 主内容区域 */
.main-content {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面标题 */
.page-header {
  margin-bottom: 30px;
}

.header-content h1 {
  font-size: 32px;
  color: #1e3c72;
  margin-bottom: 8px;
}

.header-content p {
  color: #666;
  font-size: 16px;
}

/* 统计卡片区域 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.stat-header h3 {
  color: #1e3c72;
  font-size: 16px;
  font-weight: 600;
}

.stat-badge,
.stat-tag {
  font-size: 10px;
  padding: 3px 8px;
  border-radius: 8px;
}

.stat-badge {
  background: #27ae60;
  color: white;
}

.stat-tag {
  background: #e74c3c;
  color: white;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #1e3c72;
  margin-bottom: 10px;
}

.stat-progress {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1e3c72, #2a5298);
  border-radius: 3px;
}

.progress-text {
  font-size: 12px;
  color: #666;
}

.stat-note {
  font-size: 12px;
  line-height: 1.3;
  color: #666;
}

/* 内容区域 */
.content-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

/* 社团区域 */
.clubs-section,
.activities-section,
.quick-actions-section {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.clubs-section:hover,
.activities-section:hover,
.quick-actions-section:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  color: #1e3c72;
  font-size: 24px;
  font-weight: 600;
}

.view-all-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid #1e3c72;
  background: white;
  color: #1e3c72;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.view-all-btn:hover {
  background: #1e3c72;
  color: white;
}

/* 社团列表 */
.clubs-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.club-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border: 1px solid #eaeaea;
  border-radius: 10px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.club-item:hover {
  border-color: #1e3c72;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 60, 114, 0.1);
}

.club-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  background: linear-gradient(135deg, #3498db, #2980b9);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  color: white;
}

.club-info {
  flex: 1;
}

.club-info h4 {
  color: #1e3c72;
  margin-bottom: 5px;
  font-weight: 600;
}

.club-info p {
  color: #666;
  font-size: 14px;
  margin-bottom: 3px;
}

.club-role {
  color: #27ae60 !important;
  font-weight: 500;
  margin-bottom: 0 !important;
}

.club-tag {
  background: #f0f7ff;
  color: #1e3c72;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

/* 活动列表 */
.activities-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border: 1px solid #eaeaea;
  border-radius: 10px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.activity-item:hover {
  border-color: #1e3c72;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 60, 114, 0.1);
}

.activity-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  color: white;
}

.activity-icon.lecture {
  background: linear-gradient(135deg, #1e3c72, #2a5298);
}

.activity-icon.competition {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
}

.activity-icon.workshop {
  background: linear-gradient(135deg, #3498db, #2980b9);
}

.activity-icon.exhibition {
  background: linear-gradient(135deg, #9b59b6, #8e44ad);
}

.activity-icon.performance {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
}

.activity-icon.sports {
  background: linear-gradient(135deg, #27ae60, #219653);
}

.activity-icon.default {
  background: linear-gradient(135deg, #f39c12, #e67e22);
}

.activity-info {
  flex: 1;
}

.activity-info h4 {
  color: #1e3c72;
  margin-bottom: 5px;
  font-weight: 600;
}

.activity-info p {
  color: #666;
  font-size: 14px;
  margin-bottom: 3px;
}

.activity-location {
  color: #666;
  font-size: 13px;
  margin-top: 2px;
  margin-bottom: 0 !important;
}

.activity-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

/* 活动状态标签 */
.activity-tag.approved {
  background: #d4edda;
  color: #155724;
}

.activity-tag.pending {
  background: #fff3cd;
  color: #856404;
}

.activity-tag.cancelled {
  background: #f8d7da;
  color: #721c24;
}

.activity-tag.ongoing {
  background: #cce5ff;
  color: #004085;
}

.activity-tag.ended {
  background: #e2e3e5;
  color: #383d41;
}

.activity-tag.reviewing {
  background: #e8e8e8;
  color: #5a6268;
}

/* 快速操作 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px;
  border: 1px solid #eaeaea;
  border-radius: 10px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.action-card:hover {
  border-color: #1e3c72;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 60, 114, 0.1);
}

.action-icon {
  width: 60px;
  height: 60px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
  margin-bottom: 15px;
}

.action-icon.join-club {
  background: linear-gradient(135deg, #27ae60, #219653);
}

.action-icon.book-activity {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
}

.action-icon.browse-clubs {
  background: linear-gradient(135deg, #3498db, #2980b9);
}

.action-icon.profile {
  background: linear-gradient(135deg, #9b59b6, #8e44ad);
}

.action-card h3 {
  color: #1e3c72;
  margin-bottom: 8px;
  font-weight: 600;
}

.action-card p {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

/* 错误状态 */
.error-state {
  text-align: center;
  padding: 40px 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.06);
  margin-bottom: 30px;
}

.error-state i {
  font-size: 48px;
  color: #e74c3c;
  margin-bottom: 15px;
}

.error-state p {
  color: #666;
  margin-bottom: 20px;
}

.retry-btn {
  padding: 8px 20px;
  background: #1e3c72;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  background: #2a5298;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.empty-state i {
  font-size: 48px;
  color: #ccc;
  margin-bottom: 15px;
}

.empty-state p {
  margin-bottom: 20px;
}

.join-btn {
  padding: 8px 20px;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.join-btn:hover {
  background: #219653;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-section {
    grid-template-columns: 1fr;
  }
  
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 15px;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .content-section {
    gap: 20px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .club-item,
  .activity-item {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
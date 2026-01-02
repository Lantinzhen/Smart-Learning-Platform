<template>
  <div class="my-activities-page">
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
        <h1>我的活动</h1>
        <p>管理您的活动预约和参与记录</p>
      </div>

      <!-- 统计数据 -->
      <div class="stats-section">
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-calendar-check"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.participated }}</div>
              <div class="stat-label">参与活动</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-star"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.points }}</div>
              <div class="stat-label">活动积分</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-chart-line"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.thisMonth }}</div>
              <div class="stat-label">本月参与</div>
            </div>
          </div>
        </div>
      </div>

      <div class="content-layout">
        <!-- 左侧：活动记录 -->
        <div class="main-section">
          <!-- 活动状态筛选 -->
          <div class="filter-tabs">
            <button 
              v-for="tab in tabs" 
              :key="tab.id"
              :class="['tab-btn', { active: activeTab === tab.id }]"
              @click="activeTab = tab.id"
            >
              {{ tab.label }}
            </button>
          </div>

          <!-- 活动列表 -->
          <div class="activities-section">
            <h2 class="section-title">活动记录</h2>
            
            <!-- 加载状态 -->
            <div v-if="loading" class="loading-state">
              <i class="fas fa-spinner fa-spin"></i>
              <p>加载中...</p>
            </div>

            <!-- 即将开始的活动 -->
            <div v-else-if="activeTab === 'upcoming'" class="activity-list">
              <div 
                v-for="activity in upcomingActivities" 
                :key="activity.activityId" 
                class="activity-card"
              >
                <div class="activity-main">
                  <div class="activity-header">
                    <h3 class="activity-title">{{ activity.title }}</h3>
                    <div class="activity-club">{{ activity.clubName }}</div>
                  </div>
                  <div class="activity-meta">
                    <div class="activity-time">
                      <i class="far fa-clock"></i>
                      {{ formatDateTime(activity.startTime, activity.endTime) }}
                    </div>
                    <div class="activity-location">
                      <i class="fas fa-map-marker-alt"></i>
                      {{ activity.location || '地点待定' }}
                    </div>
                    <div class="activity-points">
                      <span class="points-badge">
                        +{{ activity.points }}积分
                      </span>
                    </div>
                  </div>
                </div>
                
                <div class="activity-actions">
                  <div class="activity-status upcoming">
                    {{ activity.status || '已批准' }}
                  </div>
                  <div class="action-buttons">
                    <button class="btn-cancel" @click="cancelBooking(activity)">
                      <i class="fas fa-times"></i>
                      取消预约
                    </button>
                    <button class="btn-details" @click="viewActivityDetails(activity.activityId)">
                      查看详情
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 已参与的活动 -->
            <div v-else-if="activeTab === 'participated'" class="activity-list">
              <div 
                v-for="activity in participatedActivities" 
                :key="activity.activityId" 
                class="activity-card"
              >
                <div class="activity-main">
                  <div class="activity-header">
                    <h3 class="activity-title">{{ activity.title }}</h3>
                    <div class="activity-club">{{ activity.clubName }}</div>
                  </div>
                  <div class="activity-meta">
                    <div class="activity-time">
                      <i class="far fa-clock"></i>
                      {{ formatDateTime(activity.startTime, activity.endTime) }}
                    </div>
                    <div class="activity-points">
                      <span class="points-badge">
                        +{{ activity.points }}积分
                      </span>
                    </div>
                  </div>
                </div>
                
                <div class="activity-actions">
                  <div class="activity-status completed">
                    已参与
                  </div>
                  <div class="action-buttons">
                    <button class="btn-certificate" v-if="activity.hasCertificate" @click="downloadCertificate(activity.activityId)">
                      <i class="fas fa-download"></i>
                      下载证书
                    </button>
                    <button class="btn-review" @click="writeReview(activity.activityId)">
                      <i class="fas fa-edit"></i>
                      写评价
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 已取消的活动 -->
            <div v-else-if="activeTab === 'cancelled'" class="activity-list">
              <div 
                v-for="activity in cancelledActivities" 
                :key="activity.activityId" 
                class="activity-card"
              >
                <div class="activity-main">
                  <div class="activity-header">
                    <h3 class="activity-title">{{ activity.title }}</h3>
                    <div class="activity-club">{{ activity.clubName }}</div>
                  </div>
                  <div class="activity-meta">
                    <div class="activity-time">
                      <i class="far fa-clock"></i>
                      {{ formatDateTime(activity.startTime, activity.endTime) }}
                    </div>
                    <div class="cancelled-reason">
                      取消时间: {{ formatRegistrationTime(activity.registrationTime) }}
                    </div>
                  </div>
                </div>
                
                <div class="activity-actions">
                  <div class="activity-status cancelled">
                    已取消
                  </div>
                  <div class="action-buttons">
                    <button class="btn-rebook" @click="rebookActivity(activity)">
                      <i class="fas fa-redo"></i>
                      重新预约
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-if="!loading && getCurrentActivities.length === 0" class="empty-state">
              <i class="fas fa-calendar-times"></i>
              <h3>暂无{{ getActiveTabLabel }}的活动</h3>
              <p v-if="activeTab === 'upcoming'">去活动中心发现更多精彩活动吧！</p>
              <p v-else>您还没有{{ getActiveTabLabel }}的活动记录</p>
              <button class="btn-explore" @click="goToActivityCenter" v-if="activeTab === 'upcoming'">
                探索活动
              </button>
            </div>
          </div>
        </div>

        <!-- 右侧：快速操作和提醒 -->
        <div class="sidebar">
          <!-- 即将开始提醒 -->
          <div class="reminder-section">
            <h3 class="sidebar-title">
              <i class="fas fa-bell"></i>
              即将开始
            </h3>
            <div class="reminder-list">
              <div 
                v-for="reminder in upcomingReminders" 
                :key="reminder.activityId" 
                class="reminder-item"
              >
                <div class="reminder-time">{{ formatTime(reminder.startTime) }}</div>
                <div class="reminder-title">{{ reminder.title }}</div>
                <div class="reminder-location">{{ reminder.location || '地点待定' }}</div>
              </div>
              <div v-if="upcomingReminders.length === 0" class="no-reminders">
                暂无即将开始的活动
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 取消预约确认对话框 -->
    <div v-if="showCancelDialog" class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3>取消预约确认</h3>
          <button class="close-btn" @click="closeCancelDialog">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-content">
          <p>确定要取消预约"{{ selectedActivity?.title }}"吗？</p>
          <div class="cancel-reason">
            <label>取消原因（可选）:</label>
            <textarea v-model="cancelReason" placeholder="请简要说明取消原因..."></textarea>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="closeCancelDialog">
            再考虑一下
          </button>
          <button class="btn-danger" @click="confirmCancel">
            确认取消
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MyNavbar from '@/components/MyNavbar.vue';
import { activityApi, profileApi } from '@/api/studentApi';

export default {
  name: 'MyActivities',
  components: { MyNavbar },
  
  data() {
    return {
      userInfo: {
        name: '',
        id: ''
      },
      currentRoute: 'myactivity',
      stats: {
        participated: 0,
        points: 0,
        certificates: 0,
        thisMonth: 0
      },
      tabs: [
        { id: 'upcoming', label: '即将开始' },
        { id: 'participated', label: '已参与' },
        { id: 'cancelled', label: '已取消' }
      ],
      activeTab: 'upcoming',
      // API 返回的活动数据
      allActivities: [],
      // 从所有活动中筛选出的数据
      upcomingActivities: [],
      participatedActivities: [],
      cancelledActivities: [],
      // 提醒列表
      upcomingReminders: [],
      loading: false,
      showCancelDialog: false,
      selectedActivity: null,
      cancelReason: ''
    }
  },
  
  computed: {
    getCurrentActivities() {
      switch (this.activeTab) {
        case 'upcoming':
          return this.upcomingActivities;
        case 'participated':
          return this.participatedActivities;
        case 'cancelled':
          return this.cancelledActivities;
        default:
          return [];
      }
    },
    getActiveTabLabel() {
      const tab = this.tabs.find(t => t.id === this.activeTab);
      return tab ? tab.label : '';
    }
  },
  
  async created() {
    await this.loadUserInfo();
    await this.loadMyActivities();
  },
  
  methods: {
    // 加载用户信息
    async loadUserInfo() {
      try {
        const userData = await profileApi.getProfile();
        this.userInfo = {
          name: userData.name || '用户',
          id: userData.studentId || '未知'
        };
      } catch (error) {
        console.error('加载用户信息失败:', error);
        this.userInfo = {
          name: '用户',
          id: '未知'
        };
        this.$message.error('加载用户信息失败，请检查网络连接');
      }
    },
    
    // 加载我的活动
    async loadMyActivities() {
      this.loading = true;
      try {
        const response = await activityApi.getMyActivities();
        console.log('活动数据:', response);
        
        // 根据API返回的数据结构调整
        this.allActivities = response || []; // 注意：API直接返回数组，不是response.list
        
        // 分类活动
        this.classifyActivities();
        
        // 更新统计数据
        this.updateStats();
        
        // 更新提醒
        this.updateReminders();
        
      } catch (error) {
        console.error('加载活动失败:', error);
        this.$message.error('加载活动数据失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },
    
    // 分类活动
    classifyActivities() {
      const now = new Date();
      
      // 根据API数据结构调整字段名
      this.upcomingActivities = this.allActivities.filter(activity => {
        const activityTime = new Date(activity.startTime);
        const status = activity.status;
        // 即将开始：状态为"已批准"且开始时间在未来
        return (status === '已批准' || status === '已报名') && activityTime > now;
      });
      
      this.participatedActivities = this.allActivities.filter(activity => {
        const activityTime = new Date(activity.startTime);
        const endTime = new Date(activity.endTime);
        // 已参与：开始时间在过去且状态不是"已取消"
        return activityTime < now && endTime < now && activity.status !== '已取消';
      });
      
      // 已取消的活动
      this.cancelledActivities = this.allActivities.filter(activity => 
        activity.status === '已取消'
      ) || [];
    },
    
    // 更新统计数据
    updateStats() {
      this.stats.participated = this.participatedActivities.length;
      
      // 计算总积分（使用API返回的points字段）
      this.stats.points = this.participatedActivities.reduce((total, activity) => {
        return total + (activity.points || 0);
      }, 0);
      
      // 计算本月参与
      const currentMonth = new Date().getMonth();
      const currentYear = new Date().getFullYear();
      
      this.stats.thisMonth = this.participatedActivities.filter(activity => {
        const activityDate = new Date(activity.startTime);
        return activityDate.getMonth() === currentMonth && 
               activityDate.getFullYear() === currentYear;
      }).length;
      
      // 证书数量（需要从其他接口获取）
      this.stats.certificates = this.participatedActivities.filter(
        activity => activity.hasCertificate
      ).length;
    },
    
    // 更新提醒
    updateReminders() {
      // 只显示未来24小时内的活动
      const now = new Date();
      const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
      
      this.upcomingReminders = this.upcomingActivities.filter(activity => {
        const activityTime = new Date(activity.startTime);
        return activityTime > now && activityTime < tomorrow;
      }).slice(0, 3); // 最多显示3条
    },
    
    // 格式化日期时间
    formatDateTime(startTime, endTime) {
      if (!startTime) return '';
      
      const startDate = new Date(startTime);
      const now = new Date();
      const diffDays = Math.floor((startDate - now) / (1000 * 60 * 60 * 24));
      
      let dateStr = '';
      if (diffDays === 0) {
        dateStr = '今天';
      } else if (diffDays === 1) {
        dateStr = '明天';
      } else {
        dateStr = `${startDate.getMonth() + 1}月${startDate.getDate()}日`;
      }
      
      const startStr = `${startDate.getHours().toString().padStart(2, '0')}:${startDate.getMinutes().toString().padStart(2, '0')}`;
      
      if (endTime) {
        const endDate = new Date(endTime);
        const endStr = `${endDate.getHours().toString().padStart(2, '0')}:${endDate.getMinutes().toString().padStart(2, '0')}`;
        return `${dateStr} ${startStr}-${endStr}`;
      }
      
      return `${dateStr} ${startStr}`;
    },
    
    // 格式化报名时间
    formatRegistrationTime(registrationTime) {
      if (!registrationTime) return '';
      
      const date = new Date(registrationTime);
      return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    
    // 格式化时间（只显示时间部分）
    formatTime(dateTime) {
      if (!dateTime) return '';
      
      const date = new Date(dateTime);
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    
    handleNavigate(routeName) {
      this.$router.push({ name: routeName });
    },
    
    // 取消预约
    cancelBooking(activity) {
      this.selectedActivity = activity;
      this.showCancelDialog = true;
    },
    
    closeCancelDialog() {
      this.showCancelDialog = false;
      this.selectedActivity = null;
      this.cancelReason = '';
    },
    
    // 确认取消
    async confirmCancel() {
      if (!this.selectedActivity) return;
      
      try {
        const requestData = {
          cancellation_reason: this.cancelReason || '个人原因'
        };
        
        // 调用取消活动报名API
        await activityApi.cancelActivity(this.selectedActivity.activityId, requestData);
        
        // 更新本地数据
        this.allActivities = this.allActivities.map(activity => {
          if (activity.activityId === this.selectedActivity.activityId) {
            return {
              ...activity,
              status: '已取消'
            };
          }
          return activity;
        });
        
        // 重新分类活动
        this.classifyActivities();
        
        // 更新统计数据
        this.updateStats();
        
        // 更新提醒
        this.updateReminders();
        
        this.closeCancelDialog();
        this.$message.success('预约已取消');
        
      } catch (error) {
        console.error('取消预约失败:', error);
        this.$message.error('取消预约失败: ' + error.message);
      }
    },
    
    // 查看活动详情
    viewActivityDetails(activityId) {
      this.$router.push({
        name: 'StudentActivityDetail',
        params: { activityId }
      });
    },
    
    // 下载证书
    downloadCertificate(activityId) {
      // TODO: 调用下载证书API
      alert(`下载活动 ${activityId} 的证书`);
    },
    
    // 写评价
    writeReview(activityId) {
      // TODO: 调用写评价API
      alert(`为活动 ${activityId} 写评价`);
    },
    
    // 重新预约
    rebookActivity(activity) {
      // TODO: 调用重新预约API
      alert(`重新预约活动: ${activity.title}`);
    },
    
    // 退出登录
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('studentToken');
        this.$router.push('/student/login');
      }
    },
    
    // 跳转到活动中心
    goToActivityCenter() {
      this.$router.push('/student/activitycenter');
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

.my-activities-page {
  min-height: 100vh;
}

/* 导航栏样式 */
.navbar {
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
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
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

/* 统计数据 */
.stats-section {
  margin-bottom: 30px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 15px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #1e3c72;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

/* 内容布局 */
.content-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  background: white;
  border-radius: 10px;
  padding: 5px;
  margin-bottom: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.tab-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
  color: #666;
}

.tab-btn.active {
  background: #2a5298;
  color: white;
}

/* 活动列表 */
.activities-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 20px;
  color: #1e3c72;
  margin-bottom: 20px;
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

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.activity-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  border: 1px solid #eaeaea;
  border-radius: 10px;
  transition: all 0.3s;
}

.activity-card:hover {
  border-color: #2a5298;
  box-shadow: 0 2px 8px rgba(42, 82, 152, 0.1);
}

.activity-main {
  flex: 1;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.activity-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-right: 15px;
}

.activity-club {
  color: #2a5298;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.activity-time,
.activity-location {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.activity-points {
  margin-top: 10px;
}

.points-badge {
  background: #e8f5e9;
  color: #4CAF50;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.cancelled-reason {
  color: #e74c3c;
  font-size: 14px;
  margin-top: 8px;
}

.activity-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  min-width: 150px;
}

.activity-status {
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.activity-status.upcoming {
  background: #2196F3;
}

.activity-status.completed {
  background: #4CAF50;
}

.activity-status.cancelled {
  background: #e74c3c;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-buttons button {
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.btn-cancel {
  background: #fff5f5;
  color: #e74c3c;
  border: 1px solid #e74c3c;
}

.btn-cancel:hover {
  background: #e74c3c;
  color: white;
}

.btn-details {
  background: #f0f7ff;
  color: #2a5298;
  border: 1px solid #2a5298;
}

.btn-details:hover {
  background: #2a5298;
  color: white;
}

.btn-certificate {
  background: #e8f5e9;
  color: #4CAF50;
  border: 1px solid #4CAF50;
}

.btn-certificate:hover {
  background: #4CAF50;
  color: white;
}

.btn-review {
  background: #fff8e1;
  color: #ff9800;
  border: 1px solid #ff9800;
}

.btn-review:hover {
  background: #ff9800;
  color: white;
}

.btn-rebook {
  background: #e3f2fd;
  color: #2196F3;
  border: 1px solid #2196F3;
}

.btn-rebook:hover {
  background: #2196F3;
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

.btn-explore {
  padding: 12px 24px;
  background: #2a5298;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-explore:hover {
  background: #1e3c72;
  transform: translateY(-2px);
}

/* 右侧边栏 */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.reminder-section,
.points-section,
.quick-actions {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.sidebar-title {
  font-size: 16px;
  color: #1e3c72;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.reminder-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.reminder-item {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #2a5298;
}

.reminder-time {
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.reminder-title {
  color: #666;
  margin-bottom: 3px;
}

.reminder-location {
  color: #999;
  font-size: 12px;
}

.points-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.points-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.points-item:last-child {
  border-bottom: none;
}

.points-label {
  color: #666;
}

.points-value {
  font-weight: 600;
  color: #1e3c72;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.btn-action {
  padding: 12px 16px;
  background: #f0f7ff;
  color: #2a5298;
  border: 1px solid #2a5298;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
}

.btn-action:hover {
  background: #2a5298;
  color: white;
  transform: translateY(-2px);
}

/* 模态对话框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-dialog {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #eaeaea;
}

.modal-header h3 {
  color: #1e3c72;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 18px;
  color: #666;
  cursor: pointer;
  padding: 5px;
}

.close-btn:hover {
  color: #333;
}

.modal-content {
  padding: 25px;
}

.modal-content p {
  margin-bottom: 15px;
  color: #333;
}

.cancel-reason {
  margin-top: 15px;
}

.cancel-reason label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-weight: 500;
}

.cancel-reason textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
}

.cancel-reason textarea:focus {
  outline: none;
  border-color: #2a5298;
}

.modal-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  padding: 20px 25px;
  border-top: 1px solid #eaeaea;
}

.modal-actions button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-secondary {
  background: #f8f9fa;
  color: #666;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background: #e9ecef;
}

.btn-danger {
  background: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background: #c0392b;
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
  
  .content-layout {
    grid-template-columns: 1fr;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .activity-card {
    flex-direction: column;
    gap: 15px;
  }
  
  .activity-actions {
    align-items: stretch;
    width: 100%;
  }
  
  .action-buttons {
    justify-content: space-between;
  }
  
  .main-content {
    padding: 15px;
  }
  
  .modal-actions {
    flex-direction: column;
  }
}
/* 原有的样式保持不变，只添加新的加载状态样式 */

.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.loading-state i {
  font-size: 40px;
  color: #2a5298;
  margin-bottom: 15px;
}

.loading-state p {
  font-size: 16px;
}

.no-reminders {
  text-align: center;
  padding: 20px;
  color: #999;
  font-style: italic;
}

/* 原有的其他样式保持不变 */
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

.my-activities-page {
  min-height: 100vh;
}

/* ... 其余样式保持不变 ... */

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
  
  .content-layout {
    grid-template-columns: 1fr;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .activity-card {
    flex-direction: column;
    gap: 15px;
  }
  
  .activity-actions {
    align-items: stretch;
    width: 100%;
  }
  
  .action-buttons {
    justify-content: space-between;
  }
  
  .main-content {
    padding: 15px;
  }
  
  .modal-actions {
    flex-direction: column;
  }
}
</style>
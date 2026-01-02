<template>
  <div class="club-detail-page">
    <!-- 顶部导航栏 -->
    <MyNavbar 
      :user-name="userInfo?.name" 
      :user-id="userInfo?.studentId" 
      :current-route="currentRoute"
      @navigate="handleNavigate"
      @logout="handleLogout"
    />

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        加载中...
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-state">
        <i class="fas fa-exclamation-triangle"></i>
        {{ error }}
      </div>

      <!-- 正常显示内容 -->
      <div v-else>
        <!-- 社团头部信息 -->
        <section class="club-header">
          <div class="club-basic-info">
            <h1 class="club-name">{{ clubDetails?.name || '未知社团' }}</h1>
            <div class="club-stats">
              <div class="stat-item">
                <span class="stat-number">{{ clubDetails?.member_count || 0 }}</span>
                <span class="stat-label">成员</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ formatDate(clubDetails?.foundation_date) || '未知' }}</span>
                <span class="stat-label">成立时间</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ clubDetails?.recent_activities?.length || 0 }}</span>
                <span class="stat-label">近期活动</span>
              </div>
            </div>
          </div>

          <div class="club-extra-info">
            <div class="club-info-item">
              <i class="fas fa-user-tie"></i>
              <span>社长：{{ clubDetails?.president_name || '待任命' }}</span>
            </div>
            <div class="club-info-item" v-if="clubDetails?.contact_email">
              <i class="fas fa-envelope"></i>
              <span>联系邮箱：{{ clubDetails.contact_email }}</span>
            </div>
            <div class="club-info-item" v-if="clubDetails?.address">
              <i class="fas fa-map-marker-alt"></i>
              <span>地点：{{ clubDetails.address }}</span>
            </div>
          </div>

          <div class="action-buttons">
            <button 
              v-if="!clubDetails?.joined"
              class="btn-primary" 
              @click="applyToJoin"
              :disabled="isApplying"
            >
              <i class="fas fa-user-plus"></i>
              {{ isApplying ? '申请中...' : '申请加入社团' }}
            </button>
            <button 
              v-else
              class="btn-joined" 
              disabled
            >
              <i class="fas fa-check-circle"></i>
              已加入
            </button>
            <!-- <button class="btn-secondary"
                    @click="viewClubMembers">
              <i class="fas fa-share-alt"></i>
              查看社团成员
            </button> -->
          </div>
        </section>

        <div class="content-layout">
          <!-- 左侧主要内容 -->
          <div class="main-sections">
            <!-- 社团介绍 -->
            <section class="info-section">
              <h2 class="section-title">
                <i class="fas fa-info-circle"></i>
                社团介绍
              </h2>
              <div class="section-content">
                <p>{{ clubDetails?.description || '暂无描述' }}</p>
              </div>
            </section>

            <!-- 社团活动 -->
            <section class="info-section" v-if="clubDetails?.recent_activities?.length">
              <div class="section-header">
                <h2 class="section-title">
                  <i class="fas fa-calendar-alt"></i>
                  近期活动
                </h2>
                <!-- <a href="#" class="view-all-link" @click.prevent="viewAllActivities">
                  查看全部活动 <i class="fas fa-arrow-right"></i>
                </a> -->
              </div>
              <div class="section-content">
                <div 
                  v-for="activity in clubDetails.recent_activities" 
                  :key="activity.activity_id" 
                  class="activity-card"
                >
                  <div class="activity-header">
                    <h3 class="activity-title">{{ activity.title }}</h3>
                    <span class="activity-date">{{ formatActivityDate(activity.start_time) }}</span>
                  </div>
                  <p class="activity-description">{{ activity.description || '暂无描述' }}</p>
                  <div class="activity-actions">
                    <button 
                      class="btn-small" 
                      @click="registerActivity(activity.activity_id)"
                      :disabled="activity.isRegistered || activity.status !== '已批准'"
                    >
                      {{ activity.isRegistered ? '已报名' : '立即报名' }}
                    </button>
                    <button 
                      class="btn-small btn-outline" 
                      @click="viewActivityDetail(activity.activity_id)"
                    >
                      查看详情
                    </button>
                  </div>
                </div>
              </div>
            </section>

            <!-- 无活动提示 -->
            <section class="info-section" v-else>
              <h2 class="section-title">
                <i class="fas fa-calendar-alt"></i>
                社团活动
              </h2>
              <div class="section-content">
                <p class="no-data">暂无近期活动</p>
              </div>
            </section>
          </div>

          <!-- 右侧边栏 -->
          <div class="sidebar">
            <!-- 社团logo -->
            <section class="info-section" v-if="clubDetails?.logo_url">
              <h2 class="section-title">
                <i class="fas fa-image"></i>
                社团Logo
              </h2>
              <div class="section-content">
                <div class="club-logo">
                  <img :src="clubDetails.logo_url" :alt="clubDetails.name" />
                </div>
              </div>
            </section>

            <!-- 社团数据 -->
            <section class="info-section">
              <h2 class="section-title">
                <i class="fas fa-chart-bar"></i>
                社团数据
              </h2>
              <div class="section-content">
                <div class="stats-grid">
                  <div class="stat-card">
                    <div class="stat-icon">
                      <i class="fas fa-user-friends"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-number">{{ clubDetails?.member_count || 0 }}</div>
                      <div class="stat-label">总成员</div>
                    </div>
                  </div>
                  <div class="stat-card">
                    <div class="stat-icon">
                      <i class="fas fa-calendar-check"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-number">{{ clubDetails?.recent_activities?.length || 0 }}</div>
                      <div class="stat-label">近期活动</div>
                    </div>
                  </div>
                  <div class="stat-card">
                    <div class="stat-icon">
                      <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-number">{{ formatFoundationYears(clubDetails?.foundation_date) }}</div>
                      <div class="stat-label">成立年限</div>
                    </div>
                  </div>
                </div>
              </div>
            </section>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import MyNavbar from '@/components/MyNavbar.vue';
import { clubApi, profileApi, activityApi } from '@/api/studentApi';

export default {
  name: 'StudentClubDetail',
  
  components: { MyNavbar },
  
  data() {
    return {
      // 用户信息
      userInfo: null,
      currentRoute: 'club-detail',
      
      // 加载状态
      isLoading: true,
      error: null,
      isApplying: false,
      
      // 报名状态
      isRegistering: false,
      
      // 社团数据
      clubId: null,
      clubDetails: null
    };
  },
  
  created() {
    // 获取路由参数中的社团 ID
    this.clubId = this.$route.params.clubId;
    this.loadUserInfo();
    this.loadClubDetails();
  },
  
  methods: {
    // 加载用户信息
    async loadUserInfo() {
      try {
        const userData = await profileApi.getProfile();
        this.userInfo = userData;
      } catch (error) {
        console.error('获取用户信息失败:', error);
        // 如果获取失败，显示默认信息
        this.userInfo = {
          name: '用户',
          studentId: '未知'
        };
      }
    },
    
    // 加载社团详情
    async loadClubDetails() {
      this.isLoading = true;
      this.error = null;
      
      try {
        const response = await clubApi.getClubDetail(this.clubId);
        this.clubDetails = response;
        
        // 检查用户是否已报名社团中的活动
        await this.checkActivityRegistrationStatus();
      } catch (error) {
        console.error('加载社团详情失败:', error);
        this.error = '加载社团详情失败，请稍后重试';
      } finally {
        this.isLoading = false;
      }
    },
    
    // 检查用户是否已报名社团中的活动
    async checkActivityRegistrationStatus() {
      try {
        // 获取我的活动列表
        const myActivities = await activityApi.getMyActivities();
        if (myActivities && Array.isArray(myActivities) && this.clubDetails?.recent_activities) {
          // 为每个活动设置是否已报名状态
          this.clubDetails.recent_activities = this.clubDetails.recent_activities.map(activity => {
            const isRegistered = myActivities.some(myActivity => {
              const activityId = myActivity.activityId || myActivity.activity_id;
              return activityId == activity.activity_id && 
                    (myActivity.status === '已批准' || myActivity.status === '已报名');
            });
            return {
              ...activity,
              isRegistered: isRegistered
            };
          });
        }
      } catch (error) {
        console.error('检查活动报名状态失败:', error);
      }
    },
  //     // 跳转到社团成员页面
  // viewClubMembers() {
  //   this.$router.push({
  //     name: 'ClubMembers', // 确保路由名称与实际配置一致
  //     params: { clubId: this.clubId } // 传递社团 ID
  //   });
  // },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },
    
    // 格式化活动日期
    formatActivityDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN', {
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    // 计算成立年限
    formatFoundationYears(foundationDate) {
      if (!foundationDate) return '未知';
      const now = new Date();
      const foundation = new Date(foundationDate);
      const years = now.getFullYear() - foundation.getFullYear();
      return `${years}年`;
    },
    
    // 申请加入社团
    async applyToJoin(clubId) {
      event.stopPropagation();
      
      try {
        // 可以在这里添加申请加入的逻辑
        // 比如：await clubApi.applyClub(clubId, { applyReason: '申请加入' });
        
        // 临时使用弹窗提示
        const confirmed = await this.$confirm('确定要申请加入该社团吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        });
        
        if (confirmed) {
          // 调用申请加入API
          // await clubApi.applyClub(clubId, { applyReason: '申请加入' });
          
          // this.$message.success('申请已提交，等待审核');
          this.$router.push({
            name: 'StudentApplication',
            params: { clubId }
          });
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('申请加入失败:', error);
          this.$message.error('申请失败，请稍后重试');
        }
      }
    },
    
    // 报名活动 - 修改为与ActivityDetail页面一致的逻辑
    async registerActivity(activityId) {
      if (this.isRegistering) return;
      
      try {
        if (confirm(`确定要报名参加此活动吗？`)) {
          this.isRegistering = true;
          
          // 调用报名API - 使用与ActivityDetail页面相同的API调用
          const result = await activityApi.registerActivity(activityId, {
            activity_id: parseInt(activityId)
          });
          
          if (result) {
            // 更新本地状态
            this.updateActivityRegistrationStatus(activityId, true);
            
            // 显示成功消息
            this.$message.success('报名成功！');
          }
        }
      } catch (error) {
        console.error('活动报名失败:', error);
        this.$message.error(error.message || '报名失败，请稍后重试');
      } finally {
        this.isRegistering = false;
      }
    },
    
    // 更新活动报名状态
    updateActivityRegistrationStatus(activityId, isRegistered) {
      if (this.clubDetails?.recent_activities) {
        this.clubDetails.recent_activities = this.clubDetails.recent_activities.map(activity => {
          if (activity.activity_id == activityId) {
            return {
              ...activity,
              isRegistered: isRegistered
            };
          }
          return activity;
        });
      }
    },
    
    // 查看活动详情
    viewActivityDetail(activityId) {
      this.$router.push({
        name: 'StudentActivityDetail',
        params: { activityId }
      });
    },
    
    // 查看所有活动
    viewAllActivities() {
      this.$router.push({
        name: 'ClubActivities',
        params: { clubId: this.clubId }
      });
    },
    
    // 导航处理
    handleNavigate(routeName) {
      this.$router.push({ name: routeName });
    },
    
    // 退出登录
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('studentToken');
        this.$router.push('/student/login');
      }
    }
  }
};
</script>

<style scoped>
/* 样式保持不变 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f8f9fa;
  color: #333;
  line-height: 1.6;
}

.club-detail-page {
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

.nav-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.nav-brand h1 {
  font-size: 24px;
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

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-avatar {
  width: 40px;
  height: 40px;
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

/* 社团头部信息 */
.club-header {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.club-basic-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.club-name {
  font-size: 32px;
  color: #1e3c72;
  font-weight: 700;
}

.club-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1e3c72;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.club-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.tag {
  padding: 6px 16px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.btn-primary, .btn-secondary {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #2a5298, #3a62a8);
  transform: translateY(-2px);
}

.btn-secondary {
  background: white;
  color: #1e3c72;
  border: 2px solid #1e3c72;
}

.btn-secondary:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
}

/* 内容布局 */
.content-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

/* 信息区块通用样式 */
.info-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  margin-bottom: 25px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  color: #1e3c72;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title i {
  color: #2a5298;
}

.view-all-link {
  color: #2a5298;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: color 0.3s;
}

.view-all-link:hover {
  color: #1e3c72;
}

.section-content {
  color: #555;
  line-height: 1.7;
}

/* 社团宣言样式 */
.declaration blockquote {
  border-left: 4px solid #2a5298;
  padding-left: 20px;
  font-style: italic;
  color: #666;
  font-size: 16px;
}

/* 活动卡片样式 */
.activity-card {
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
}

.activity-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  color: #1e3c72;
}

.activity-date {
  color: #666;
  font-size: 14px;
  white-space: nowrap;
}

.activity-description {
  color: #666;
  margin-bottom: 15px;
}

.activity-actions {
  display: flex;
  gap: 10px;
}

.btn-small {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-small:not(.btn-outline) {
  background: #2a5298;
  color: white;
}

.btn-small.btn-outline {
  background: white;
  color: #2a5298;
  border: 1px solid #2a5298;
}

.btn-small:hover {
  transform: translateY(-1px);
}

/* 成员列表样式 */
.members-list {
  margin-bottom: 20px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.member-item:last-child {
  border-bottom: none;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6a11cb, #2575fc);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
}

.member-info {
  flex: 1;
}

.member-name {
  font-weight: 600;
  color: #333;
}

.member-role {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

.members-count {
  text-align: center;
  color: #666;
  font-size: 14px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

/* 统计数据样式 */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #e3f2fd;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2a5298;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
  color: #1e3c72;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 15px;
    padding: 15px;
  }
  
  .nav-left {
    width: 100%;
    justify-content: space-between;
  }
  
  .nav-menu {
    gap: 15px;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .content-layout {
    grid-template-columns: 1fr;
  }
  
  .club-basic-info {
    flex-direction: column;
    gap: 20px;
  }
  
  .club-stats {
    justify-content: space-around;
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .main-content {
    padding: 15px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

/* 保持原有的CSS样式，仅添加新增的样式 */

.club-detail-page {
  min-height: 100vh;
}

/* 主内容区域 */
.main-content {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 加载状态 */
.loading-state, .error-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
  font-size: 18px;
}

.loading-state i {
  font-size: 48px;
  color: #1e3c72;
  margin-bottom: 20px;
  animation: spin 1s linear infinite;
}

.error-state i {
  font-size: 48px;
  color: #f56c6c;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 社团头部信息 */
.club-header {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.club-basic-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.club-name {
  font-size: 32px;
  color: #1e3c72;
  font-weight: 700;
  margin-bottom: 10px;
}

.club-extra-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 25px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.club-info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #555;
}

.club-info-item i {
  color: #2a5298;
  width: 20px;
}

.club-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1e3c72;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

/* 按钮样式 */
.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.btn-primary, .btn-secondary, .btn-joined {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #2a5298, #3a62a8);
  transform: translateY(-2px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: white;
  color: #1e3c72;
  border: 2px solid #1e3c72;
}

.btn-secondary:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
}

.btn-joined {
  background: #4CAF50;
  color: white;
  cursor: default;
}

/* 内容布局 */
.content-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

/* 信息区块通用样式 */
.info-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  margin-bottom: 25px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  color: #1e3c72;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title i {
  color: #2a5298;
}

.view-all-link {
  color: #2a5298;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: color 0.3s;
}

.view-all-link:hover {
  color: #1e3c72;
}

.section-content {
  color: #555;
  line-height: 1.7;
}

.no-data {
  color: #999;
  text-align: center;
  padding: 20px;
}

/* 活动卡片样式 */
.activity-card {
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
}

.activity-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  color: #1e3c72;
}

.activity-date {
  color: #666;
  font-size: 14px;
  white-space: nowrap;
}

.activity-description {
  color: #666;
  margin-bottom: 15px;
}

.activity-actions {
  display: flex;
  gap: 10px;
}

.btn-small {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-small:not(.btn-outline) {
  background: #2a5298;
  color: white;
}

.btn-small.btn-outline {
  background: white;
  color: #2a5298;
  border: 1px solid #2a5298;
}

.btn-small:hover:not(:disabled) {
  transform: translateY(-1px);
}

.btn-small:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Logo样式 */
.club-logo {
  text-align: center;
}

.club-logo img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 8px;
  object-fit: contain;
}

/* 统计数据样式 */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #e3f2fd;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2a5298;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
  color: #1e3c72;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-layout {
    grid-template-columns: 1fr;
  }
  
  .club-basic-info {
    flex-direction: column;
    gap: 20px;
  }
  
  .club-stats {
    justify-content: space-around;
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .main-content {
    padding: 15px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
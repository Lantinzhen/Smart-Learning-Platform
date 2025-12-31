<template>
  <div class="activity-detail-page">
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
      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>正在加载活动详情...</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-state">
        <i class="fas fa-exclamation-triangle"></i>
        <h3>{{ error }}</h3>
        <p>请稍后重试或返回上一页</p>
        <button class="back-btn" @click="goBack">返回活动中心</button>
      </div>

      <!-- 正常显示内容 -->
      <div v-else class="activity-container">
        <!-- 活动头部信息 -->
        <div class="activity-header">
          <div class="activity-status-tag">
            <span class="status-badge" :class="getStatusClass(activityDetails.status)">
              {{ getStatusText(activityDetails.status) }}
            </span>
            <span class="category-badge">{{ activityDetails.categoryName }}</span>
          </div>
          <h1 class="activity-title">{{ activityDetails.title }}</h1>
          <div class="activity-meta">
            <div class="meta-item">
              <i class="far fa-calendar"></i>
              <span>{{ formatDateTimeRange(activityDetails.startTime, activityDetails.endTime) }}</span>
            </div>
            <div class="meta-item">
              <i class="fas fa-map-marker-alt"></i>
              <span>{{ activityDetails.location || '地点待定' }}</span>
            </div>
            <div class="meta-item">
              <i class="fas fa-users"></i>
              <span>主办：{{ activityDetails.clubName }}</span>
            </div>
          </div>
        </div>

        <div class="content-layout">
          <!-- 左侧主要内容 -->
          <div class="main-content-section">
            <!-- 活动统计 -->
            <div class="stats-section">
              <div class="stat-card">
                <div class="stat-number">{{ activityDetails.registeredCount || 0 }}</div>
                <div class="stat-label">已报名</div>
              </div>
              <div class="stat-card">
                <div class="stat-number">{{ activityDetails.maxParticipants || 0 }}</div>
                <div class="stat-label">总名额</div>
              </div>
              <div class="stat-card">
                <div class="stat-number">{{ remainingSpots }}</div>
                <div class="stat-label">剩余名额</div>
              </div>
              <!-- <div class="stat-card">
                <div class="stat-number">{{ activityDetails.points || 0 }}</div>
                <div class="stat-label">活动积分</div>
              </div> -->
            </div>

            <!-- 活动海报 -->
            <section v-if="activityDetails.posterUrl" class="info-section">
              <h2 class="section-title">
                <i class="fas fa-image"></i>
                活动海报
              </h2>
              <div class="section-content">
                <img 
                  :src="activityDetails.posterUrl" 
                  :alt="activityDetails.title" 
                  class="activity-poster"
                  @error="handleImageError"
                />
              </div>
            </section>

            <!-- 活动介绍 -->
            <section class="info-section">
              <h2 class="section-title">
                <i class="fas fa-info-circle"></i>
                活动介绍
              </h2>
              <div class="section-content">
                <div class="description-content" v-html="formatDescription(activityDetails.description)"></div>
              </div>
            </section>

            <!-- 活动详情 -->
            <section class="info-section">
              <h2 class="section-title">
                <i class="fas fa-calendar-alt"></i>
                活动详情
              </h2>
              <div class="section-content">
                <div class="detail-list">
                  <div class="detail-item">
                    <span class="label">活动时间：</span>
                    <span class="value">{{ formatDateTimeRange(activityDetails.startTime, activityDetails.endTime) }}</span>
                  </div>
                  <div class="detail-item" v-if="activityDetails.registrationDeadline">
                    <span class="label">报名截止：</span>
                    <span class="value">{{ formatDateTime(activityDetails.registrationDeadline) }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">活动地点：</span>
                    <span class="value">{{ activityDetails.location || '待通知' }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">主办社团：</span>
                    <span class="value">{{ activityDetails.clubName }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">社团联系方式：</span>
                    <span class="value">{{ activityDetails.clubContact || '暂无' }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">活动类别：</span>
                    <span class="value">{{ activityDetails.categoryName || '未分类' }}</span>
                  </div>
                  <!-- <div class="detail-item" v-if="activityDetails.points">
                    <span class="label">活动积分：</span>
                    <span class="value">{{ activityDetails.points }} 分</span>
                  </div> -->
                  <div class="detail-item">
                    <span class="label">活动状态：</span>
                    <span class="value">{{ getStatusText(activityDetails.status) }}</span>
                  </div>
                </div>
              </div>
            </section>
          </div>

          <!-- 右侧边栏 -->
          <div class="sidebar">
            <!-- 报名信息 -->
            <section class="info-section">
              <h2 class="section-title">报名信息</h2>
              <div class="section-content">
                <div class="registration-info">
                  <div class="info-item">
                    <span class="label">报名状态</span>
                    <span class="value" :class="registrationStatusClass">
                      {{ registrationStatusText }}
                    </span>
                  </div>
                  <!-- <div class="info-item">
                    <span class="label">活动积分</span>
                    <span class="value">{{ activityDetails.points || 0 }} 分</span>
                  </div> -->
                  <div class="info-item">
                    <span class="label">剩余名额</span>
                    <span class="value">{{ remainingSpots }} 个</span>
                  </div>
                </div>
                
                <!-- 报名按钮 -->
                <div v-if="canRegister">
                  <button 
                    class="btn-register" 
                    @click="handleRegistration"
                    :disabled="isRegistering"
                  >
                    <i v-if="isRegistering" class="fas fa-spinner fa-spin"></i>
                    {{ isRegistering ? '处理中...' : '立即报名' }}
                  </button>
                  <p class="registration-note" v-if="activityDetails.registrationDeadline">
                    报名截止：{{ formatDateTime(activityDetails.registrationDeadline) }}
                  </p>
                </div>
                
                <!-- 已报名状态 -->
                <div v-else-if="activityDetails.isRegistered" class="registration-success">
                  <i class="fas fa-check-circle"></i>
                  <div>
                    <div>您已成功报名此活动</div>
                    <div class="registration-hint">请按时参加活动</div>
                  </div>
                </div>
                
                <!-- 已满员 -->
                <div v-else-if="isFull" class="registration-full">
                  <i class="fas fa-users-slash"></i>
                  活动已满员
                </div>
                
                <!-- 报名已截止 -->
                <div v-else-if="isRegistrationClosed" class="registration-closed">
                  <i class="fas fa-clock"></i>
                  报名已截止
                </div>
                
                <!-- 活动未批准 -->
                <div v-else-if="activityDetails.status !== '已批准'" class="registration-pending">
                  <i class="fas fa-hourglass-half"></i>
                  活动{{ getStatusText(activityDetails.status) }}
                </div>
              </div>
            </section>

            <!-- 主办社团 -->
            <section class="info-section">
              <h2 class="section-title">主办社团</h2>
              <div class="section-content">
                <div class="club-info" @click="viewClubDetail" style="cursor: pointer;">
                  <div class="club-avatar">
                    <i class="fas fa-users"></i>
                  </div>
                  <div class="club-details">
                    <h3 class="club-name">{{ activityDetails.clubName }}</h3>
                    <div class="club-meta">
                      <span class="club-contact">联系方式: {{ activityDetails.clubContact || '暂无' }}</span>
                    </div>
                  </div>
                </div>
                <button class="btn-view-club" @click="viewClubDetail" v-if="activityDetails.clubId">
                  查看社团详情
                </button>
              </div>
            </section>

            <!-- 温馨提示 -->
            <section class="info-section">
              <h2 class="section-title">
                <i class="fas fa-exclamation-circle"></i>
                温馨提示
              </h2>
              <div class="section-content">
                <ul class="notice-list">
                  <li class="notice-item">
                    <i class="fas fa-check"></i>
                    请准时参加活动，迟到会影响积分获取
                  </li>
                  <li class="notice-item">
                    <i class="fas fa-check"></i>
                    活动开始前15分钟到达现场签到
                  </li>
                  <li class="notice-item" v-if="activityDetails.points">
                    <i class="fas fa-check"></i>
                    完成活动可获得 {{ activityDetails.points }} 积分
                  </li>
                  <li class="notice-item">
                    <i class="fas fa-check"></i>
                    请遵守活动现场秩序，尊重他人
                  </li>
                  <li class="notice-item" v-if="activityDetails.registrationDeadline">
                    <i class="fas fa-check"></i>
                    请在报名截止时间前完成报名
                  </li>
                </ul>
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
import { activityApi, profileApi } from '@/api/studentApi';

export default {
  name: 'StudentActivityDetail',

  components: { MyNavbar },

  data() {
    return {
      activityId: null,
      activityDetails: {
        activityId: null,
        title: '',
        description: '',
        clubId: null,
        clubName: '',
        clubContact: '',
        categoryName: '',
        posterUrl: '',
        location: '',
        startTime: '',
        endTime: '',
        maxParticipants: 0,
        registeredCount: 0,
        points: 0,
        registrationDeadline: '',
        status: '',
        isRegistered: false,
        createdAt: ''
      },
      userInfo: null,
      loading: false,
      error: null,
      isRegistering: false,
      currentRoute: 'activitycenter',
      imageError: false
    };
  },

  computed: {
    // 剩余名额
    remainingSpots() {
      const max = this.activityDetails.maxParticipants || 0;
      const registered = this.activityDetails.registeredCount || 0;
      if (max === 0) return '不限';
      const remaining = Math.max(0, max - registered);
      return remaining;
    },

    // 是否已满员
    isFull() {
      const max = this.activityDetails.maxParticipants || 0;
      const registered = this.activityDetails.registeredCount || 0;
      return max > 0 && registered >= max;
    },

    // 报名是否已截止
    isRegistrationClosed() {
      if (!this.activityDetails.registrationDeadline) return false;
      try {
        const deadline = new Date(this.activityDetails.registrationDeadline);
        return deadline < new Date();
      } catch {
        return false;
      }
    },

    // 是否可以报名
    canRegister() {
      return !this.activityDetails.isRegistered && 
             !this.isFull && 
             !this.isRegistrationClosed &&
             this.activityDetails.status === '已批准';
    },

    // 报名状态文本
    registrationStatusText() {
      if (this.activityDetails.isRegistered) return '已报名';
      if (this.isFull) return '已满员';
      if (this.isRegistrationClosed) return '已截止';
      if (this.activityDetails.status !== '已批准') return '未开放';
      return '可报名';
    },

    // 报名状态样式类
    registrationStatusClass() {
      if (this.activityDetails.isRegistered) return 'status-registered';
      if (this.isFull) return 'status-full';
      if (this.isRegistrationClosed) return 'status-closed';
      if (this.activityDetails.status !== '已批准') return 'status-pending';
      return 'status-available';
    }
  },

  async created() {
    await this.loadUserInfo();
    this.loadActivityDetails();
  },

  methods: {
    // 加载用户信息
    async loadUserInfo() {
      try {
        const token = localStorage.getItem('studentToken');
        if (!token) {
          this.$router.push('/student/login');
          return;
        }
        
        const userData = await profileApi.getProfile();
        this.userInfo = userData;
      } catch (error) {
        console.error('获取用户信息失败:', error);
        this.userInfo = {
          name: '用户',
          studentId: '未知'
        };
      }
    },

    // 加载活动详情
    async loadActivityDetails() {
      this.activityId = this.$route.params.activityId;
      if (!this.activityId) {
        this.error = '活动ID无效';
        return;
      }

      this.loading = true;
      this.error = null;

      try {
        const response = await activityApi.getActivityDetail(this.activityId);
        console.log('活动详情API返回数据:', response);
        
        if (response) {
          // 根据API返回的数据结构调整字段映射
          this.activityDetails = {
            activityId: response.activityId || this.activityId,
            title: response.title || '',
            description: response.description || '',
            clubId: response.clubId || null,
            clubName: response.clubName || '',
            clubContact: response.clubContact || '',
            categoryName: response.categoryName || '',
            posterUrl: response.posterUrl || '',
            location: response.location || '',
            startTime: response.startTime || '',
            endTime: response.endTime || '',
            maxParticipants: response.maxParticipants || 0,
            registeredCount: response.registeredCount || 0,
            points: response.points || 0,
            registrationDeadline: response.registrationDeadline || '',
            status: response.status || '',
            isRegistered: response.isRegistered || false,
            createdAt: response.createdAt || ''
          };
          
          // 检查用户是否已报名该活动
          await this.checkRegistrationStatus();
        } else {
          this.error = '活动不存在或已被删除';
        }
      } catch (error) {
        console.error('获取活动详情失败:', error);
        this.error = error.message || '加载活动详情失败';
      } finally {
        this.loading = false;
      }
    },
    
    // 检查用户是否已报名该活动
    async checkRegistrationStatus() {
      try {
        // 获取我的活动列表
        const myActivities = await activityApi.getMyActivities();
        if (myActivities && Array.isArray(myActivities)) {
          const isRegistered = myActivities.some(activity => {
            const activityId = activity.activityId || activity.activity_id;
            return activityId == this.activityId && 
                   (activity.status === '已批准' || activity.status === '已报名');
          });
          this.activityDetails.isRegistered = isRegistered;
        }
      } catch (error) {
        console.error('检查报名状态失败:', error);
      }
    },

    // 处理报名
    async handleRegistration() {
      if (!this.canRegister || this.isRegistering) return;

      try {
        if (confirm(`确定要报名参加"${this.activityDetails.title}"吗？`)) {
          this.isRegistering = true;
          
          // 调用报名API
          const result = await activityApi.registerActivity(this.activityId, {
            activity_id: parseInt(this.activityId)
          });
          
          if (result) {
            // 更新本地状态
            this.activityDetails.isRegistered = true;
            this.activityDetails.registeredCount = (this.activityDetails.registeredCount || 0) + 1;
            
            // 显示成功消息
            this.$message.success('报名成功！');
            
            // 重新加载活动详情以获取最新数据
            setTimeout(() => {
              this.loadActivityDetails();
            }, 500);
          }
        }
      } catch (error) {
        console.error('报名失败:', error);
        this.$message.error(error.message || '报名失败，请稍后重试');
      } finally {
        this.isRegistering = false;
      }
    },

    // 查看社团详情
    viewClubDetail() {
      if (this.activityDetails.clubId) {
        this.$router.push({
          name: 'StudentClubDetail',
          params: { clubId: this.activityDetails.clubId }
        });
      }
    },

    // 格式化描述文本
    formatDescription(description) {
      if (!description) return '暂无详细描述';
      // 将换行符转换为br标签
      return description.replace(/\n/g, '<br>');
    },

    // 格式化日期时间
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return '待定';
      try {
        const date = new Date(dateTimeStr);
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        }).replace(/\//g, '-');
      } catch {
        return '时间待定';
      }
    },

    // 格式化时间（仅时间部分）
    formatTime(dateTimeStr) {
      if (!dateTimeStr) return '';
      try {
        const date = new Date(dateTimeStr);
        return date.toLocaleTimeString('zh-CN', {
          hour: '2-digit',
          minute: '2-digit'
        });
      } catch {
        return '';
      }
    },

    // 格式化日期时间范围
    formatDateTimeRange(startTime, endTime) {
      if (!startTime || !endTime) return '时间待定';
      
      try {
        const startDate = new Date(startTime);
        const endDate = new Date(endTime);
        
        // 如果是同一天，只显示一次日期
        if (startDate.toDateString() === endDate.toDateString()) {
          return `${startDate.getFullYear()}-${(startDate.getMonth() + 1).toString().padStart(2, '0')}-${startDate.getDate().toString().padStart(2, '0')} ${this.formatTime(startTime)}-${this.formatTime(endTime)}`;
        } else {
          return `${this.formatDateTime(startTime)} 至 ${this.formatDateTime(endTime)}`;
        }
      } catch {
        return '时间待定';
      }
    },

    // 获取状态样式类
    getStatusClass(status) {
      switch (status) {
        case '已批准': return 'status-approved';
        case '待审核': return 'status-pending';
        case '已取消': return 'status-cancelled';
        case '已结束': return 'status-ended';
        default: return 'status-unknown';
      }
    },

    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case '已批准': return '进行中';
        case '待审核': return '待审核';
        case '已取消': return '已取消';
        case '已结束': return '已结束';
        default: return status || '未知';
      }
    },

    // 处理图片加载错误
    handleImageError() {
      this.imageError = true;
    },

    // 返回上一页
    goBack() {
      this.$router.push({ name: 'activitycenter' });
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

.activity-detail-page {
  min-height: 100vh;
}

.loading-state,
.error-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.loading-state i,
.error-state i {
  font-size: 60px;
  color: #ccc;
  margin-bottom: 20px;
}

.loading-state h3,
.error-state h3 {
  margin-bottom: 10px;
  color: #555;
}

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
  gap: 25px;
}

.nav-item {
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 20px;
  transition: background-color 0.3s;
  font-size: 14px;
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

.main-content {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.activity-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.activity-header {
  padding: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  position: relative;
}

.activity-status-tag {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.status-badge报名中,
.type-badge,
.category-badge {
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge报名中 {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.type-badge,
.category-badge {
  background: rgba(255, 255, 255, 0.15);
}

.activity-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.2;
}

.activity-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  opacity: 0.9;
}

.content-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  padding: 30px;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 25px 20px;
  text-align: center;
  border: 1px solid #eaeaea;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #1e3c72;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.info-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 20px;
  color: #1e3c72;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title i {
  color: #2a5298;
}

.section-content {
  color: #555;
  line-height: 1.7;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.schedule-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #2a5298;
}

.schedule-day {
  min-width: 180px;
}

.day-number {
  display: inline-block;
  width: 24px;
  height: 24px;
  background: #2a5298;
  color: white;
  border-radius: 50%;
  text-align: center;
  line-height: 24px;
  font-size: 12px;
  margin-right: 10px;
}

.day-title {
  font-weight: 600;
  color: #333;
}

.schedule-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.time-slot {
  display: flex;
  gap: 15px;
}

.time {
  min-width: 100px;
  color: #666;
  font-size: 14px;
}

.activity {
  color: #333;
  flex: 1;
}

.notice-list {
  list-style: none;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 8px 0;
}

.notice-item i {
  color: #4CAF50;
  margin-top: 2px;
}

.sidebar .info-section {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 25px;
  border: 1px solid #eaeaea;
}

.registration-info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eaeaea;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  color: #666;
}

.value {
  font-weight: 600;
  color: #333;
}

.value.free {
  color: #4CAF50;
}

.btn-register {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 15px;
}

.btn-register:hover:not(.btn-disabled) {
  background: linear-gradient(135deg, #2a5298, #3a62a8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(42, 82, 152, 0.3);
}

.btn-register.btn-disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.registration-success {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4CAF50;
  font-weight: 500;
  text-align: center;
  justify-content: center;
}

.club-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.club-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.club-details {
  flex: 1;
}

.club-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.club-members {
  color: #666;
  font-size: 14px;
}

.btn-view-club {
  width: 100%;
  padding: 12px;
  background: white;
  color: #2a5298;
  border: 2px solid #2a5298;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-view-club:hover {
  background: #2a5298;
  color: white;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.view-all-link {
  color: #2a5298;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.participants-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.participant-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #eaeaea;
}

.participant-avatar {
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

.participant-info {
  flex: 1;
}

.participant-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.participant-college {
  color: #666;
  font-size: 12px;
}

.registration-time {
  color: #999;
  font-size: 12px;
  white-space: nowrap;
}

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
    gap: 10px;
    flex-wrap: wrap;
    justify-content: center;
  }

  .content-layout {
    grid-template-columns: 1fr;
    padding: 20px;
    gap: 20px;
  }

  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }

  .activity-header {
    padding: 25px;
  }

  .activity-title {
    font-size: 24px;
  }

  .schedule-item {
    flex-direction: column;
    gap: 15px;
  }

  .main-content {
    padding: 15px;
  }
}
/* 原有的样式保持不变，只添加一些新样式 */

.loading-state {
  text-align: center;
  padding: 100px 20px;
  color: #666;
}

.loading-state i {
  font-size: 48px;
  color: #1e3c72;
  margin-bottom: 20px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-state {
  text-align: center;
  padding: 100px 20px;
  color: #f56c6c;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.error-state i {
  font-size: 60px;
  margin-bottom: 20px;
}

.error-state h3 {
  margin-bottom: 15px;
  font-size: 20px;
}

.error-state .back-btn {
  margin-top: 20px;
  padding: 10px 30px;
  background: #1e3c72;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s;
}

.error-state .back-btn:hover {
  background: #2a5298;
}

/* 活动海报 */
.activity-poster {
  width: 100%;
  max-height: 400px;
  object-fit: contain;
  border-radius: 8px;
  border: 1px solid #eaeaea;
}

/* 详情列表 */
.detail-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
}

.detail-item .label {
  min-width: 100px;
  color: #666;
  font-weight: 500;
}

.detail-item .value {
  flex: 1;
  color: #333;
}

/* 状态标签样式 */
.status-badge {
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.status-badge.status-approved {
  background-color: #4CAF50;
}

.status-badge.status-pending {
  background-color: #FF9800;
}

.status-badge.status-cancelled {
  background-color: #F44336;
}

.status-badge.status-ended {
  background-color: #9E9E9E;
}

/* 报名状态样式 */
.status-registered {
  color: #4CAF50;
  font-weight: 600;
}

.status-full {
  color: #F44336;
  font-weight: 600;
}

.status-closed {
  color: #9E9E9E;
  font-weight: 600;
}

.status-pending {
  color: #FF9800;
  font-weight: 600;
}

.status-available {
  color: #4CAF50;
  font-weight: 600;
}

/* 报名提示 */
.registration-note {
  margin-top: 10px;
  color: #666;
  font-size: 12px;
  text-align: center;
}

.registration-success,
.registration-full,
.registration-closed {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border-radius: 6px;
  font-weight: 500;
  text-align: center;
}

.registration-success {
  background: #E8F5E9;
  color: #4CAF50;
}

.registration-full {
  background: #FFEBEE;
  color: #F44336;
}

.registration-closed {
  background: #F5F5F5;
  color: #9E9E9E;
}

/* 社团信息点击效果 */
.club-info:hover {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 10px;
  margin: -10px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .content-layout {
    grid-template-columns: 1fr;
  }
  
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .detail-item {
    flex-direction: column;
    gap: 5px;
  }
  
  .detail-item .label {
    min-width: auto;
  }
}
</style>
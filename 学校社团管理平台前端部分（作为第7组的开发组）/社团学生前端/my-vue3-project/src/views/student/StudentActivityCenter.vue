<template>
  <div class="activity-center">
    <MyNavbar 
      :user-name="userInfo.name" 
      :user-id="userInfo.id" 
      :current-route="currentRoute"
      @navigate="handleNavigate"
      @logout="handleLogout"
    />

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-content">
          <h1>活动中心</h1>
          <p>探索全校精彩活动，丰富你的校园生活</p>
        </div>
      </div>

      <!-- 筛选、排序和搜索 -->
      <div class="filter-section">
        <div class="filter-controls">
          <!-- 搜索框 -->
          <div class="search-box">
            <i class="fas fa-search"></i>
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="搜索活动名称、社团或关键词..."
              @input="filterActivities"
            >
          </div>

          <!-- 筛选条件 -->
          <div class="filter-group">
            <label>活动类型:</label>
            <select v-model="selectedCategory" @change="filterActivities">
              <option value="">全部类型</option>
              <option v-for="category in categories" :key="category" :value="category">
                {{ category }}
              </option>
            </select>
          </div>
          
          <div class="filter-group">
            <label>时间范围:</label>
            <select v-model="selectedTime" @change="filterActivities">
              <option value="">全部时间</option>
              <option value="today">今天</option>
              <option value="tomorrow">明天</option>
              <option value="week">本周</option>
              <option value="month">本月</option>
              <option value="upcoming">即将开始</option>
              <option value="ended">已结束</option>
            </select>
          </div>

          <div class="filter-group">
            <label>排序方式:</label>
            <select v-model="sortBy" @change="sortActivities">
              <option value="time">按时间排序</option>
              <option value="popularity">按热度排序</option>
              <option value="newest">最新发布</option>
            </select>
          </div>
        </div>

        <div class="activity-stats">
          <span>共找到 {{ filteredActivities.length }} 个活动</span>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>正在加载活动数据...</p>
      </div>

      <!-- 活动列表 -->
      <div v-else class="activities-container">
        <div 
          v-for="activity in displayedActivities" 
          :key="activity.activityId" 
          class="activity-card"
        >
          <div class="activity-header">
            <!-- 类别标签 - 添加这里 -->
            <div class="activity-category-tag" :class="getCategoryClass(activity.categoryName)">
              {{ activity.categoryName || '活动' }}
            </div>
            <div class="activity-time">
              <i class="far fa-clock"></i>
              {{ formatActivityTime(activity) }}
            </div>
          </div>

          <div class="activity-content">
            <h3 class="activity-title">{{ activity.title }}</h3>
            <div class="activity-club">
              <i class="fas fa-users"></i>
              {{ activity.clubName }}
            </div>
            <p class="activity-description">{{ activity.description }}</p>
            
            <div class="activity-details">
              <div class="detail-item">
                <i class="fas fa-map-marker-alt"></i>
                <span>{{ activity.location || '地点待定' }}</span>
              </div>
              <div class="detail-item">
                <i class="fas fa-users"></i>
                <span>已报名: {{ activity.registeredCount || 0 }}/{{ activity.maxParticipants || '不限' }}人</span>
              </div>
              <!-- <div class="detail-item">
                <i class="fas fa-star"></i>
                <span>活动积分: {{ activity.points || 0 }}分</span>
              </div> -->
            </div>

            <!-- 活动状态 -->
            <div class="activity-status">
              <span class="status-tag" :class="getStatusClass(activity)">
                {{ getStatusText(activity) }}
              </span>
            </div>
          </div>

          <div class="activity-footer">
            <div class="activity-actions">
              <button 
                class="btn-primary" 
                :class="{ 
                  'btn-disabled': !canRegister(activity) || isRegistering[activity.activityId] 
                }"
                @click="signUpActivity(activity)"
                :disabled="!canRegister(activity) || isRegistering[activity.activityId]"
              >
                <i v-if="isRegistering[activity.activityId]" class="fas fa-spinner fa-spin"></i>
                {{ getRegisterButtonText(activity) }}
              </button>
              <button class="btn-secondary" @click="viewActivityDetail(activity.activityId)">
                查看详情
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && filteredActivities.length === 0" class="empty-state">
        <i class="fas fa-calendar-times"></i>
        <h3>未找到符合条件的活动</h3>
        <p>尝试调整筛选条件或搜索关键词</p>
        <button class="btn-primary" @click="resetFilters">重置筛选条件</button>
      </div>

      <!-- 加载更多 -->
      <div v-if="!loading && filteredActivities.length > 0 && hasMore" class="load-more">
        <button class="btn-load-more" @click="loadMoreActivities">
          加载更多活动
        </button>
      </div>
    </main>
  </div>
</template>

<script>
import MyNavbar from '@/components/MyNavbar.vue';
import { activityApi , profileApi} from '@/api/studentApi';

export default {
  name: 'ActivityCenter',
  components: { MyNavbar },
  data() {
    return {
      userInfo: {
        name: '',
        id: ''
      },
      currentRoute: 'activitycenter',
      searchQuery: '',
      selectedCategory: '',
      selectedTime: '',
      sortBy: 'time',
      loading: false,
      allActivities: [],
      filteredActivities: [],
      displayedCount: 6,
      currentPage: 1,
      pageSize: 10,
      hasMore: true,
      categories: [],
      isRegistering: {} // 用于跟踪每个活动的报名状态
    }
  },
  computed: {
    displayedActivities() {
      return this.filteredActivities.slice(0, this.displayedCount);
    }
  },
  async mounted() {
    await this.loadUserInfo();
    await this.loadActivities();
    this.extractCategories();
    this.filterActivities();
  },
  methods: {
// 加载用户信息
async loadUserInfo() {
  try {
    const token = localStorage.getItem('studentToken');
    if (token) {
      // 调用API获取用户信息
      const userData = await profileApi.getProfile();
      this.userInfo = {
        name: userData?.name || '学生',
        id: userData?.studentId || '未知'
      };
    } else {
      // 如果没有token，设置默认用户信息
      this.userInfo = {
        name: '学生',
        id: '未知'
      };
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    // 如果获取失败，显示默认信息
    this.userInfo = {
      name: '学生',
      id: '未知'
    };
  }
},

    // 加载活动列表
    async loadActivities() {
      this.loading = true;
      try {
        const response = await activityApi.getActivities({
          page: this.currentPage,
          size: this.pageSize
        });
        
        console.log('API响应数据:', response); // 调试用
        
        if (response && Array.isArray(response)) {
          // 如果是数组，直接使用
          this.allActivities = response.map(activity => ({
            activityId: activity.activityId,
            title: activity.title,
            description: activity.description,
            clubName: activity.clubName,
            categoryName: activity.categoryName,
            location: activity.location,
            startTime: activity.startTime,
            endTime: activity.endTime,
            maxParticipants: activity.maxParticipants,
            registeredCount: activity.registeredCount,
            points: activity.points,
            status: activity.status,
            registrationDeadline: activity.registrationDeadline
          }));
        } else if (response && response.records) {
          // 如果是有分页结构的数据
          this.allActivities = response.records.map(activity => ({
            activityId: activity.activityId,
            title: activity.title,
            description: activity.description,
            clubName: activity.clubName,
            categoryName: activity.categoryName,
            location: activity.location,
            startTime: activity.startTime,
            endTime: activity.endTime,
            maxParticipants: activity.maxParticipants,
            registeredCount: activity.registeredCount,
            points: activity.points,
            status: activity.status,
            registrationDeadline: activity.registrationDeadline
          }));
          this.hasMore = response.current < response.pages;
        }
        
        // 初始化报名状态
        this.initializeRegistrationStatus();
      } catch (error) {
        console.error('加载活动列表失败:', error);
        // 如果API调用失败，使用示例数据
        this.loadSampleData();
      } finally {
        this.loading = false;
      }
    },

    // 加载示例数据（用于测试）
    loadSampleData() {
      this.allActivities = [
        {
          activityId: 1,
          title: '更新的编程竞赛讲座',
          description: '更新后的详细介绍...',
          clubName: '计算机协会',
          categoryName: '科技竞赛',
          location: '教学楼B301',
          startTime: '2023-10-15T14:00:00',
          endTime: '2023-10-15T16:00:00',
          maxParticipants: 200,
          registeredCount: 0,
          points: 0,
          status: '已批准',
          registrationDeadline: '2023-10-14T23:59:59'
        },
        {
          activityId: 2,
          title: 'Python数据分析工作坊',
          description: '学习如何使用Python进行数据分析，包括pandas和matplotlib库的使用',
          clubName: '计算机协会',
          categoryName: '技术培训',
          location: '实验楼B301',
          startTime: '2025-11-25T10:00:00',
          endTime: '2026-06-10T12:00:00',
          maxParticipants: 30,
          registeredCount: 17,
          points: 0,
          status: '已批准',
          registrationDeadline: '2025-11-24T23:59:59'
        },
        {
          activityId: 3,
          title: '古典音乐会',
          description: '由音乐社成员演奏的经典古典音乐作品',
          clubName: '音乐社',
          categoryName: '文艺演出',
          location: '艺术中心音乐厅',
          startTime: '2025-12-09T19:00:00',
          endTime: '2025-12-09T21:00:00',
          maxParticipants: 100,
          registeredCount: 80,
          points: 10,
          status: '已批准',
          registrationDeadline: '2025-12-08T23:59:59'
        }
      ];
      
      this.initializeRegistrationStatus();
    },

    // 初始化报名状态
    initializeRegistrationStatus() {
      const registrationStatus = {};
      this.allActivities.forEach(activity => {
        registrationStatus[activity.activityId] = false;
      });
      this.isRegistering = registrationStatus;
    },

    // 提取活动类别
    extractCategories() {
      const categories = new Set();
      this.allActivities.forEach(activity => {
        if (activity.categoryName) {
          categories.add(activity.categoryName);
        }
      });
      this.categories = Array.from(categories);
    },

    // 获取类别样式类
    getCategoryClass(categoryName) {
      if (!categoryName) return 'category-default';
      
      // 根据类别名称返回不同的样式类
      const categoryMap = {
        '科技竞赛': 'category-competition',
        '技术培训': 'category-training',
        '文艺演出': 'category-performance',
        '讲座': 'category-lecture',
        '工作坊': 'category-workshop',
        '比赛': 'category-match',
        '展览': 'category-exhibition',
        '社交': 'category-social',
        '体育': 'category-sports'
      };
      
      return categoryMap[categoryName] || 'category-default';
    },

    // 过滤活动
    filterActivities() {
      this.filteredActivities = this.allActivities.filter(activity => {
        // 搜索条件匹配
        const matchesSearch = this.searchQuery === '' || 
          (activity.title && activity.title.toLowerCase().includes(this.searchQuery.toLowerCase())) ||
          (activity.clubName && activity.clubName.toLowerCase().includes(this.searchQuery.toLowerCase())) ||
          (activity.description && activity.description.toLowerCase().includes(this.searchQuery.toLowerCase()));

        // 类别匹配
        const matchesCategory = this.selectedCategory === '' || 
          activity.categoryName === this.selectedCategory;

        // 时间匹配
        const matchesTime = this.selectedTime === '' || this.checkTimeMatch(activity);

        return matchesSearch && matchesCategory && matchesTime;
      });
      
      this.sortActivities();
      this.displayedCount = 6;
    },

    // 检查时间匹配
    checkTimeMatch(activity) {
      if (!activity.startTime) return false;
      
      const now = new Date();
      const startTime = new Date(activity.startTime);
      const endTime = activity.endTime ? new Date(activity.endTime) : null;
      
      switch(this.selectedTime) {
        case 'today': {
          const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
          const tomorrow = new Date(today);
          tomorrow.setDate(tomorrow.getDate() + 1);
          return startTime >= today && startTime < tomorrow;
        }
        case 'tomorrow': {
          const tomorrow = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1);
          const dayAfterTomorrow = new Date(tomorrow);
          dayAfterTomorrow.setDate(dayAfterTomorrow.getDate() + 1);
          return startTime >= tomorrow && startTime < dayAfterTomorrow;
        }
        case 'week': {
          const weekLater = new Date(now);
          weekLater.setDate(weekLater.getDate() + 7);
          return startTime >= now && startTime < weekLater;
        }
        case 'month': {
          const monthLater = new Date(now);
          monthLater.setMonth(monthLater.getMonth() + 1);
          return startTime >= now && startTime < monthLater;
        }
        case 'upcoming':
          return startTime > now;
        case 'ended':
          return endTime && endTime < now;
        default:
          return true;
      }
    },

    // 排序活动
    sortActivities() {
      if (this.sortBy === 'time') {
        this.filteredActivities.sort((a, b) => {
          if (!a.startTime) return 1;
          if (!b.startTime) return -1;
          return new Date(a.startTime) - new Date(b.startTime);
        });
      } else if (this.sortBy === 'popularity') {
        this.filteredActivities.sort((a, b) => {
          const aRegistered = a.registeredCount || 0;
          const bRegistered = b.registeredCount || 0;
          const aMax = a.maxParticipants || 1;
          const bMax = b.maxParticipants || 1;
          const aRatio = aMax > 0 ? aRegistered / aMax : 0;
          const bRatio = bMax > 0 ? bRegistered / bMax : 0;
          return bRatio - aRatio;
        });
      } else if (this.sortBy === 'newest') {
        this.filteredActivities.sort((a, b) => {
          if (!a.startTime) return 1;
          if (!b.startTime) return -1;
          return new Date(b.startTime) - new Date(a.startTime);
        });
      }
    },

    // 格式化活动时间
    formatActivityTime(activity) {
      if (!activity.startTime) return '时间待定';
      
      const startTime = new Date(activity.startTime);
      const endTime = activity.endTime ? new Date(activity.endTime) : null;
      
      // 简化的时间显示
      if (endTime) {
        // 如果是同一天
        if (startTime.toDateString() === endTime.toDateString()) {
          const dateStr = startTime.toLocaleDateString('zh-CN');
          const startTimeStr = startTime.toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'});
          const endTimeStr = endTime.toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'});
          return `${dateStr} ${startTimeStr} - ${endTimeStr}`;
        } else {
          // 不同天，显示完整日期时间
          const startStr = startTime.toLocaleString('zh-CN');
          const endStr = endTime.toLocaleString('zh-CN');
          return `${startStr} - ${endStr}`;
        }
      } else {
        return startTime.toLocaleString('zh-CN');
      }
    },

    // 获取状态样式类
    getStatusClass(activity) {
      const now = new Date();
      const startTime = activity.startTime ? new Date(activity.startTime) : null;
      const endTime = activity.endTime ? new Date(activity.endTime) : null;
      
      if (activity.status !== '已批准') {
        return 'status-pending';
      } else if (endTime && endTime < now) {
        return 'status-ended';
      } else if (startTime && startTime <= now && (!endTime || endTime > now)) {
        return 'status-ongoing';
      } else {
        return 'status-upcoming';
      }
    },

    // 获取状态文本
    getStatusText(activity) {
      const now = new Date();
      const startTime = activity.startTime ? new Date(activity.startTime) : null;
      const endTime = activity.endTime ? new Date(activity.endTime) : null;
      
      if (activity.status !== '已批准') {
        return activity.status || '待审核';
      } else if (endTime && endTime < now) {
        return '已结束';
      } else if (startTime && startTime <= now && (!endTime || endTime > now)) {
        return '进行中';
      } else {
        return '即将开始';
      }
    },

    // 是否可以报名
    canRegister(activity) {
      if (activity.status !== '已批准') return false;
      
      const now = new Date();
      const startTime = activity.startTime ? new Date(activity.startTime) : null;
      
      // 如果活动已开始，不能报名
      if (startTime && startTime <= now) return false;
      
      // 检查报名截止时间
      if (activity.registrationDeadline) {
        const deadline = new Date(activity.registrationDeadline);
        if (deadline < now) return false;
      }
      
      // 检查是否已满员
      if (activity.maxParticipants && activity.registeredCount >= activity.maxParticipants) {
        return false;
      }
      
      return true;
    },

    // 获取报名按钮文本
    getRegisterButtonText(activity) {
      if (activity.status !== '已批准') {
        return activity.status || '未开放';
      }
      
      const now = new Date();
      const startTime = activity.startTime ? new Date(activity.startTime) : null;
      
      if (startTime && startTime <= now) {
        return '已开始';
      }
      
      if (activity.registrationDeadline) {
        const deadline = new Date(activity.registrationDeadline);
        if (deadline < now) {
          return '已截止';
        }
      }
      
      if (activity.maxParticipants && activity.registeredCount >= activity.maxParticipants) {
        return '已满员';
      }
      
      return '立即报名';
    },

    // 报名活动signUpActivity
    async signUpActivity(activity) {
      if (!this.canRegister(activity)) return;
      
      try {
        this.isRegistering[activity.activityId] = true;
        
        const response = await activityApi.registerActivity(activity.activityId, {
          activity_id: activity.activityId
        });
        
        if (response) {
          // 更新本地数据
          activity.registeredCount = (activity.registeredCount || 0) + 1;
          
          alert('报名成功！');
          
          // 重新加载活动数据以获取最新状态
          await this.loadActivities();

          this.filterActivities();
        }
      } catch (error) {
        console.error('报名失败:', error);
        alert(error.message || '报名失败，请稍后重试');
      } finally {
        this.isRegistering[activity.activityId] = false;
      }
    },

    // 查看活动详情
    viewActivityDetail(activityId) {
      this.$router.push({
        name: 'StudentActivityDetail',
        params: { activityId }
      });
    },

    // 重置筛选条件
    resetFilters() {
      this.searchQuery = '';
      this.selectedCategory = '';
      this.selectedTime = '';
      this.sortBy = 'time';
      this.filterActivities();
    },

    // 加载更多活动
    async loadMoreActivities() {
      this.displayedCount += 6;
      
      // 如果当前显示的数量接近总数，加载下一页
      if (this.displayedCount >= this.filteredActivities.length && this.hasMore) {
        this.currentPage++;
        await this.loadActivities();
        this.filterActivities();
      }
    },

    handleNavigate(routeName) {
      this.$router.push({ name: routeName });
    },

    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('studentToken');
        localStorage.removeItem('userInfo');
        this.$router.push('/student/login');
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

.activity-center {
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

/* 筛选区域 */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 30px;
}

.filter-controls {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  align-items: center;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 300px;
}

.search-box i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

.search-box input {
  width: 100%;
  padding: 8px 12px 8px 40px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.search-box input:focus {
  outline: none;
  border-color: #2a5298;
  box-shadow: 0 0 0 3px rgba(42, 82, 152, 0.1);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-group label {
  font-weight: 500;
  white-space: nowrap;
}

.filter-group select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  min-width: 120px;
}

.activity-stats {
  color: #666;
  font-size: 14px;
}

/* 活动卡片容器 */
.activities-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

/* 活动卡片样式 */
.activity-card {
  background: white;
  border-radius: 12px;
  padding: 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.activity-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.activity-type-tag {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.activity-type-tag.lecture { background: #2196F3; }
.activity-type-tag.workshop { background: #FF9800; }
.activity-type-tag.competition { background: #F44336; }
.activity-type-tag.exhibition { background: #9C27B0; }
.activity-type-tag.social { background: #4CAF50; }
.activity-type-tag.sports { background: #795548; }

.activity-time {
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.activity-content {
  padding: 20px;
}

.activity-title {
  font-size: 20px;
  color: #1e3c72;
  font-weight: 600;
  margin-bottom: 10px;
  line-height: 1.3;
}

.activity-club {
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.activity-description {
  color: #555;
  line-height: 1.5;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.detail-item i {
  width: 16px;
  color: #2a5298;
}

.activity-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  background: #f0f7ff;
  color: #2a5298;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-top: 1px solid #eaeaea;
}

.activity-price {
  font-size: 18px;
  font-weight: 700;
  color: #e74c3c;
}

.activity-price.free {
  color: #27ae60;
}

.activity-actions {
  display: flex;
  gap: 10px;
}

.btn-primary, .btn-secondary {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-primary {
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
}

.btn-primary:hover:not(.btn-disabled) {
  background: linear-gradient(135deg, #2a5298, #3a62a8);
  transform: translateY(-2px);
}

.btn-primary.btn-disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: white;
  color: #2a5298;
  border: 1px solid #2a5298;
}

.btn-secondary:hover {
  background: #f0f7ff;
  transform: translateY(-2px);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
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

/* 加载更多 */
.load-more {
  text-align: center;
  margin-top: 40px;
}

.btn-load-more {
  padding: 12px 30px;
  background: white;
  color: #2a5298;
  border: 2px solid #2a5298;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-load-more:hover {
  background: #2a5298;
  color: white;
  transform: translateY(-2px);
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
  
  .filter-controls {
    flex-direction: column;
    width: 100%;
  }
  
  .search-box {
    max-width: 100%;
  }
  
  .activities-container {
    grid-template-columns: 1fr;
  }
  
  .activity-footer {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .activity-actions {
    justify-content: space-between;
  }
  
  .main-content {
    padding: 15px;
  }
}
.activity-status {
  margin-top: 15px;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.status-tag.status-ongoing {
  background: #4CAF50;
}

.status-tag.status-upcoming {
  background: #2196F3;
}

.status-tag.status-ended {
  background: #9E9E9E;
}

.status-tag.status-pending {
  background: #FF9800;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.loading-state i {
  font-size: 40px;
  color: #1e3c72;
  margin-bottom: 20px;
}
.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 20px 15px;
  border-bottom: 1px solid #f0f0f0;
}

/* 类别标签样式 */
.activity-category-tag {
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 13px;
  font-weight: 600;
  color: white;
  text-align: center;
  min-width: 60px;
}

/* 不同类别的颜色 */
.activity-category-tag.category-competition {
  background: linear-gradient(135deg, #FF6B6B, #FF8E53);
}

.activity-category-tag.category-training {
  background: linear-gradient(135deg, #4ECDC4, #44A08D);
}

.activity-category-tag.category-performance {
  background: linear-gradient(135deg, #9C27B0, #673AB7);
}

.activity-category-tag.category-lecture {
  background: linear-gradient(135deg, #2196F3, #1976D2);
}

.activity-category-tag.category-workshop {
  background: linear-gradient(135deg, #FF9800, #F57C00);
}

.activity-category-tag.category-match {
  background: linear-gradient(135deg, #F44336, #D32F2F);
}

.activity-category-tag.category-exhibition {
  background: linear-gradient(135deg, #9C27B0, #7B1FA2);
}

.activity-category-tag.category-social {
  background: linear-gradient(135deg, #4CAF50, #388E3C);
}

.activity-category-tag.category-sports {
  background: linear-gradient(135deg, #795548, #5D4037);
}

.activity-category-tag.category-default {
  background: linear-gradient(135deg, #607D8B, #455A64);
}

.activity-time {
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  text-align: right;
}

/* 状态标签样式 */
.activity-status {
  margin-top: 15px;
  display: flex;
  justify-content: flex-start;
}

.status-tag {
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  text-align: center;
  min-width: 60px;
}

.status-tag.status-ongoing {
  background: #4CAF50;
}

.status-tag.status-upcoming {
  background: #2196F3;
}

.status-tag.status-ended {
  background: #9E9E9E;
}

.status-tag.status-pending {
  background: #FF9800;
}

/* 活动描述限制行数 */
.activity-description {
  color: #555;
  line-height: 1.5;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 4.5em; /* 3行 × 1.5行高 */
}

/* 活动详情项 */
.activity-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.detail-item i {
  width: 16px;
  color: #2a5298;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.loading-state i {
  font-size: 40px;
  color: #1e3c72;
  margin-bottom: 20px;
}

/* 按钮样式 */
.btn-primary {
  padding: 10px 20px;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 100px;
}

.btn-primary:hover:not(.btn-disabled) {
  background: linear-gradient(135deg, #2a5298, #3a62a8);
  transform: translateY(-2px);
}

.btn-primary.btn-disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  padding: 10px 20px;
  background: white;
  color: #2a5298;
  border: 1px solid #2a5298;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-secondary:hover {
  background: #f0f7ff;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .activity-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .activity-time {
    align-self: flex-start;
  }
  
  .activity-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .btn-primary, .btn-secondary {
    width: 100%;
  }
}
</style>
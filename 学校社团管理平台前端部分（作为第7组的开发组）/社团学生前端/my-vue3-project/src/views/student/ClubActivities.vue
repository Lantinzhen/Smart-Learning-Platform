<template>
  <div class="club-activities-page">
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
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <button class="btn-back" @click="goBack">
            <i class="fas fa-arrow-left"></i>
            返回
          </button>
          <div class="header-info">
            <h1>{{ clubInfo.name }} - 活动列表</h1>
            <p>共 {{ activities.length }} 个活动</p>
          </div>
          <div class="header-actions">
            <button class="btn-export" @click="exportActivities">
              <i class="fas fa-download"></i>
              导出活动
            </button>
          </div>
        </div>
      </div>

      <!-- 筛选和搜索 -->
      <div class="filter-section">
        <div class="search-box">
          <i class="fas fa-search"></i>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="搜索活动名称..."
            @input="handleSearch"
          >
        </div>
        <div class="filter-options">
          <select v-model="selectedStatus" @change="filterActivities">
            <option value="">全部状态</option>
            <option value="upcoming">未开始</option>
            <option value="ongoing">进行中</option>
            <option value="completed">已结束</option>
          </select>
          <select v-model="sortBy" @change="sortActivities">
            <option value="startTime">按开始时间</option>
            <option value="title">按活动名称</option>
            <option value="participants">按参与人数</option>
          </select>
        </div>
      </div>

      <!-- 活动列表 -->
      <div class="activities-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <i class="fas fa-spinner fa-spin"></i>
          <p>正在加载活动数据...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-triangle"></i>
          <h3>数据加载失败</h3>
          <p>{{ error }}</p>
          <div class="error-actions">
            <button class="btn-retry" @click="loadActivities">
              <i class="fas fa-redo"></i>
              重新加载
            </button>
            <button class="btn-use-static" @click="useStaticData">
              <i class="fas fa-database"></i>
              使用示例数据
            </button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="filteredActivities.length === 0" class="empty-state">
          <i class="fas fa-calendar-times"></i>
          <h3>暂无活动数据</h3>
          <p>没有找到符合条件的活动</p>
          <button class="btn-clear-filters" @click="clearFilters">
            清除筛选条件
          </button>
        </div>

        <!-- 活动列表 -->
        <div v-else class="activities-list">
          <!-- 按状态分组显示 -->
          <div class="status-group" v-if="groupedActivities.upcoming.length > 0">
            <h3 class="status-title upcoming">
              <i class="fas fa-clock"></i>
              未开始活动 ({{ groupedActivities.upcoming.length }})
            </h3>
            <div class="activities-grid">
              <div 
                v-for="activity in groupedActivities.upcoming" 
                :key="activity.id"
                class="activity-card upcoming"
                @click="viewActivityDetail(activity.id)"
              >
                <div class="activity-header">
                  <h3 class="activity-title">{{ activity.title }}</h3>
                  <span class="activity-status upcoming">未开始</span>
                </div>
                <div class="activity-time">
                  <i class="fas fa-calendar"></i>
                  {{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}
                </div>
                <div class="activity-location">
                  <i class="fas fa-map-marker-alt"></i>
                  {{ activity.location }}
                </div>
                <div class="activity-description">
                  {{ activity.description }}
                </div>
                <div class="activity-stats">
                  <div class="stat-item">
                    <i class="fas fa-users"></i>
                    <span>{{ activity.participants }}/{{ activity.maxParticipants }}人</span>
                  </div>
                  <div class="stat-item">
                    <i class="fas fa-star"></i>
                    <span>{{ activity.points }}积分</span>
                  </div>
                </div>
                <div class="activity-actions">
                  <button class="btn-action btn-join" @click.stop="joinActivity(activity)">
                    <i class="fas fa-user-plus"></i>
                    立即报名
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="status-group" v-if="groupedActivities.ongoing.length > 0">
            <h3 class="status-title ongoing">
              <i class="fas fa-play-circle"></i>
              进行中活动 ({{ groupedActivities.ongoing.length }})
            </h3>
            <div class="activities-grid">
              <div 
                v-for="activity in groupedActivities.ongoing" 
                :key="activity.id"
                class="activity-card ongoing"
                @click="viewActivityDetail(activity.id)"
              >
                <div class="activity-header">
                  <h3 class="activity-title">{{ activity.title }}</h3>
                  <span class="activity-status ongoing">进行中</span>
                </div>
                <div class="activity-time">
                  <i class="fas fa-calendar"></i>
                  {{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}
                </div>
                <div class="activity-location">
                  <i class="fas fa-map-marker-alt"></i>
                  {{ activity.location }}
                </div>
                <div class="activity-description">
                  {{ activity.description }}
                </div>
                <div class="activity-stats">
                  <div class="stat-item">
                    <i class="fas fa-users"></i>
                    <span>{{ activity.participants }}/{{ activity.maxParticipants }}人</span>
                  </div>
                  <div class="progress-container">
                    <div class="progress-bar">
                      <div 
                        class="progress-fill" 
                        :style="{ width: `${(activity.participants / activity.maxParticipants) * 100}%` }"
                      ></div>
                    </div>
                    <span class="progress-text">{{ Math.round((activity.participants / activity.maxParticipants) * 100) }}%</span>
                  </div>
                </div>
                <div class="activity-actions">
                  <button class="btn-action btn-join" @click.stop="joinActivity(activity)">
                    <i class="fas fa-user-plus"></i>
                    立即参与
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="status-group" v-if="groupedActivities.completed.length > 0">
            <h3 class="status-title completed">
              <i class="fas fa-check-circle"></i>
              已结束活动 ({{ groupedActivities.completed.length }})
            </h3>
            <div class="activities-grid">
              <div 
                v-for="activity in groupedActivities.completed" 
                :key="activity.id"
                class="activity-card completed"
                @click="viewActivityDetail(activity.id)"
              >
                <div class="activity-header">
                  <h3 class="activity-title">{{ activity.title }}</h3>
                  <span class="activity-status completed">已结束</span>
                </div>
                <div class="activity-time">
                  <i class="fas fa-calendar"></i>
                  {{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}
                </div>
                <div class="activity-location">
                  <i class="fas fa-map-marker-alt"></i>
                  {{ activity.location }}
                </div>
                <div class="activity-description">
                  {{ activity.description }}
                </div>
                <div class="activity-stats">
                  <div class="stat-item">
                    <i class="fas fa-users"></i>
                    <span>{{ activity.participants }}人参与</span>
                  </div>
                  <div class="stat-item">
                    <i class="fas fa-star"></i>
                    <span>{{ activity.points }}积分</span>
                  </div>
                </div>
                <div class="activity-actions">
                  <button class="btn-action btn-review" @click.stop="viewActivityDetail(activity.id)">
                    <i class="fas fa-eye"></i>
                    查看详情
                  </button>
                  <!-- <button class="btn-action btn-feedback" @click.stop="giveFeedback(activity)">
                    <i class="fas fa-comment"></i>
                    活动反馈
                  </button> -->
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="filteredActivities.length > 0" class="pagination">
          <button 
            class="page-btn" 
            :disabled="currentPage === 1"
            @click="changePage(currentPage - 1)"
          >
            <i class="fas fa-chevron-left"></i>
            上一页
          </button>
          
          <div class="page-numbers">
            <span class="page-info">
              第 {{ currentPage }} 页，共 {{ totalPages }} 页
            </span>
          </div>
          
          <button 
            class="page-btn" 
            :disabled="currentPage === totalPages"
            @click="changePage(currentPage + 1)"
          >
            下一页
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </main>

    <!-- 报名确认模态框 -->
    <div v-if="showJoinModal" class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3>报名活动</h3>
          <button class="close-btn" @click="showJoinModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-content">
          <p>确定要报名参加"{{ selectedActivity?.title }}"活动吗？</p>
          <div class="activity-info">
            <div class="info-item">
              <label>活动时间：</label>
              <span>{{ formatDate(selectedActivity?.startTime) }} - {{ formatDate(selectedActivity?.endTime) }}</span>
            </div>
            <div class="info-item">
              <label>活动地点：</label>
              <span>{{ selectedActivity?.location }}</span>
            </div>
            <div class="info-item">
              <label>可获得积分：</label>
              <span class="points">{{ selectedActivity?.points }}分</span>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="showJoinModal = false">
            取消
          </button>
          <button class="btn-primary" @click="confirmJoin">
            确认报名
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MyNavbar from '@/components/MyNavbar.vue';

// 静态示例数据
const STATIC_ACTIVITIES = [
  {
    id: 1,
    title: '春季摄影外拍活动',
    description: '组织成员前往公园进行春季主题摄影，记录美好春光',
    startTime: '2024-04-15T09:00:00',
    endTime: '2024-04-15T17:00:00',
    location: '中山公园',
    participants: 25,
    maxParticipants: 30,
    points: 20,
    status: 'upcoming',
    category: '户外活动',
    organizer: '摄影社',
    createdAt: '2024-03-10T10:00:00'
  },
  {
    id: 2,
    title: '摄影后期处理讲座',
    description: '专业摄影师讲解Photoshop和Lightroom后期处理技巧',
    startTime: '2024-04-10T14:00:00',
    endTime: '2024-04-10T16:30:00',
    location: '教学楼A201',
    participants: 45,
    maxParticipants: 50,
    points: 15,
    status: 'ongoing',
    category: '讲座',
    organizer: '摄影社',
    createdAt: '2024-03-05T09:00:00'
  },
  {
    id: 3,
    title: '摄影作品展览',
    description: '展示社团成员的优秀摄影作品，互相学习交流',
    startTime: '2024-03-20T10:00:00',
    endTime: '2024-03-25T18:00:00',
    location: '艺术楼展厅',
    participants: 120,
    maxParticipants: 200,
    points: 10,
    status: 'completed',
    category: '展览',
    organizer: '摄影社',
    createdAt: '2024-02-15T14:00:00'
  },
  {
    id: 4,
    title: '夜景摄影教学',
    description: '学习夜景摄影技巧，包括长曝光、光线控制等',
    startTime: '2024-04-20T19:00:00',
    endTime: '2024-04-20T22:00:00',
    location: '校园湖畔',
    participants: 18,
    maxParticipants: 25,
    points: 25,
    status: 'upcoming',
    category: '实践教学',
    organizer: '摄影社',
    createdAt: '2024-03-12T16:00:00'
  },
  {
    id: 5,
    title: '手机摄影比赛',
    description: '使用手机拍摄创意照片，评选最佳作品',
    startTime: '2025-11-22T09:00:00',
    endTime: '2025-11-25T18:00:00',
    location: '线上',
    participants: 85,
    maxParticipants: 100,
    points: 30,
    status: 'completed',
    category: '比赛',
    organizer: '摄影社',
    createdAt: '2025-11-21T10:00:00'
  },
  {
    id: 6,
    title: '人像摄影工作坊',
    description: '专业人像摄影师指导人像拍摄技巧和灯光运用',
    startTime: '2025-11-21T13:00:00',
    endTime: '2025-11-29T17:00:00',
    location: '摄影棚',
    participants: 12,
    maxParticipants: 15,
    points: 35,
    status: 'ongoing',
    category: '工作坊',
    organizer: '摄影社',
    createdAt: '2025-11-20T11:00:00'
  }
  
];

export default {
  name: 'ClubActivities',
  components: { MyNavbar },
  data() {
    return {
      userInfo: {
        name: '张三',
        id: '2200310527'
      },
      currentRoute: 'club-activities',
      clubInfo: {
        id: null,
        name: '社团名称'
      },
      activities: [],
      filteredActivities: [],
      loading: false,
      error: null,
      searchQuery: '',
      selectedStatus: '',
      sortBy: 'startTime',
      currentPage: 1,
      pageSize: 6,
      showJoinModal: false,
      selectedActivity: null
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredActivities.length / this.pageSize);
    },
    paginatedActivities() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredActivities.slice(start, end);
    },
    groupedActivities() {
      const grouped = {
        upcoming: [],
        ongoing: [],
        completed: []
      };
      
      this.filteredActivities.forEach(activity => {
        if (grouped[activity.status]) {
          grouped[activity.status].push(activity);
        }
      });
      
      return grouped;
    }
  },
  async mounted() {
    // 从路由参数获取社团ID
    this.clubInfo.id = this.$route.params.clubId;
    await this.loadClubInfo();
    await this.loadActivities();
  },
  methods: {
    // 加载社团信息
    async loadClubInfo() {
      try {
        const clubData = {
          1: { id: 1, name: '音乐社' },
          2: { id: 2, name: '摄影社' },
          3: { id: 3, name: '编程社' }
        };
        
        this.clubInfo = clubData[this.clubInfo.id] || { 
          id: this.clubInfo.id, 
          name: '未知社团' 
        };
      } catch (error) {
        console.error('加载社团信息失败:', error);
        this.clubInfo.name = '社团详情加载失败';
      }
    },

    // 加载活动数据
    async loadActivities() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await this.fetchActivitiesFromAPI();
        this.activities = response;
        this.filteredActivities = [...this.activities];
        this.updateActivityStatus();
        this.sortActivities();
      } catch (err) {
        this.error = err.message || '无法加载活动数据';
        console.error('加载活动数据失败:', err);
      } finally {
        this.loading = false;
      }
    },

    // 模拟API调用
    async fetchActivitiesFromAPI() {
      // 模拟网络延迟
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 模拟随机失败（在实际应用中删除这部分）
      if (Math.random() < 0.3) {
        throw new Error('网络请求失败，请检查网络连接');
      }
      
      // 返回静态数据
      return STATIC_ACTIVITIES;
    },

    // 使用静态数据
    useStaticData() {
      this.activities = STATIC_ACTIVITIES;
      this.filteredActivities = [...this.activities];
      this.updateActivityStatus();
      this.error = null;
      this.sortActivities();
    },

    // 更新活动状态（基于当前时间）
    updateActivityStatus() {
      const now = new Date();
      this.activities.forEach(activity => {
        const startTime = new Date(activity.startTime);
        const endTime = new Date(activity.endTime);
        
        if (now < startTime) {
          activity.status = 'upcoming';
        } else if (now >= startTime && now <= endTime) {
          activity.status = 'ongoing';
        } else {
          activity.status = 'completed';
        }
      });
    },

    // 处理搜索
    handleSearch() {
      this.filterActivities();
    },

    // 筛选活动
    filterActivities() {
      let filtered = this.activities.filter(activity => {
        const matchesSearch = !this.searchQuery || 
          activity.title.toLowerCase().includes(this.searchQuery.toLowerCase());
        
        const matchesStatus = !this.selectedStatus || activity.status === this.selectedStatus;
        
        return matchesSearch && matchesStatus;
      });
      
      this.filteredActivities = filtered;
      this.currentPage = 1;
    },

    // 排序活动
    sortActivities() {
      this.filteredActivities.sort((a, b) => {
        switch (this.sortBy) {
          case 'title':
            return a.title.localeCompare(b.title);
          case 'participants':
            return b.participants - a.participants;
          case 'startTime':
          default:
            return new Date(a.startTime) - new Date(b.startTime);
        }
      });
    },

    // 清除筛选条件
    clearFilters() {
      this.searchQuery = '';
      this.selectedStatus = '';
      this.filterActivities();
    },

    // 分页
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    },

    // 格式化日期
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },

    // 查看活动详情
    viewActivityDetail(activityId) {
      this.$router.push({
        name: 'StudentActivityDetail',
        params: { activityId }
      });
    },

    // 报名活动
    joinActivity(activity) {
      this.selectedActivity = activity;
      this.showJoinModal = true;
    },

    // 确认报名
    confirmJoin() {
      if (this.selectedActivity) {
        // 模拟报名逻辑
        this.selectedActivity.participants += 1;
        this.showJoinModal = false;
        this.selectedActivity = null;
        alert('报名成功！');
      }
    },

    // 活动反馈
    giveFeedback(activity) {
      alert(`为活动"${activity.title}"提供反馈`);
    },

    // 导出活动
    exportActivities() {
      const csvContent = this.generateCSV();
      this.downloadCSV(csvContent, `${this.clubInfo.name}_活动列表.csv`);
    },

    // 生成CSV内容
    generateCSV() {
      const headers = ['活动名称', '开始时间', '结束时间', '地点', '状态', '参与人数', '积分'];
      const rows = this.activities.map(activity => [
        activity.title,
        this.formatDate(activity.startTime),
        this.formatDate(activity.endTime),
        activity.location,
        this.getStatusText(activity.status),
        activity.participants,
        activity.points
      ]);
      
      return [headers, ...rows].map(row => row.join(',')).join('\n');
    },

    // 下载CSV文件
    downloadCSV(content, filename) {
      const blob = new Blob(['\uFEFF' + content], { type: 'text/csv;charset=utf-8;' });
      const link = document.createElement('a');
      const url = URL.createObjectURL(blob);
      
      link.setAttribute('href', url);
      link.setAttribute('download', filename);
      link.style.visibility = 'hidden';
      
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        upcoming: '未开始',
        ongoing: '进行中',
        completed: '已结束'
      };
      return statusMap[status] || status;
    },

    // 导航方法
    handleNavigate(routeName) {
      this.$router.push({ name: routeName });
    },

    handleLogout() {
        if (confirm('确定要退出登录吗？')) {
          console.log('执行退出登录操作');
          this.$router.push('/student/login');
      }
    },

    goBack() {
      this.$router.go(-1);
    }
  }
};
</script>

<style scoped>
.club-activities-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.main-content {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  margin-bottom: 30px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.btn-back {
  padding: 10px 20px;
  background: #f8f9fa;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #e9ecef;
}

.header-info h1 {
  font-size: 28px;
  color: #1e3c72;
  margin-bottom: 5px;
}

.header-info p {
  color: #666;
}

.btn-export {
  padding: 10px 20px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-export:hover {
  background: #218838;
  transform: translateY(-2px);
}

/* 筛选区域 */
.filter-section {
  background: white;
  padding: 20px 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  margin-bottom: 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 400px;
}

.search-box i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
}

.search-box input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.search-box input:focus {
  outline: none;
  border-color: #2a5298;
}

.filter-options {
  display: flex;
  gap: 15px;
}

.filter-options select {
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  font-size: 14px;
}

.filter-options select:focus {
  outline: none;
  border-color: #2a5298;
}

/* 活动容器 */
.activities-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 加载状态 */
.loading-state {
  padding: 60px 20px;
  text-align: center;
  color: #666;
}

.loading-state i {
  font-size: 40px;
  color: #2a5298;
  margin-bottom: 15px;
}

/* 错误状态 */
.error-state {
  padding: 60px 20px;
  text-align: center;
  color: #666;
}

.error-state i {
  font-size: 50px;
  color: #e74c3c;
  margin-bottom: 20px;
}

.error-state h3 {
  margin-bottom: 10px;
  color: #333;
}

.error-actions {
  margin-top: 20px;
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn-retry, .btn-use-static {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-retry {
  background: #2a5298;
  color: white;
}

.btn-retry:hover {
  background: #1e3c72;
}

.btn-use-static {
  background: #f8f9fa;
  color: #666;
  border: 1px solid #ddd;
}

.btn-use-static:hover {
  background: #e9ecef;
}

/* 空状态 */
.empty-state {
  padding: 60px 20px;
  text-align: center;
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

.btn-clear-filters {
  padding: 10px 20px;
  background: #2a5298;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 15px;
  transition: all 0.3s;
}

.btn-clear-filters:hover {
  background: #1e3c72;
}

/* 活动列表 */
.activities-list {
  padding: 0;
}

.status-group {
  margin-bottom: 30px;
}

.status-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  padding: 15px 25px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-title.upcoming {
  background: #e3f2fd;
  color: #1976d2;
  border-left: 4px solid #1976d2;
}

.status-title.ongoing {
  background: #e8f5e8;
  color: #2e7d32;
  border-left: 4px solid #2e7d32;
}

.status-title.completed {
  background: #f5f5f5;
  color: #616161;
  border-left: 4px solid #616161;
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
  padding: 0 25px 25px;
}

.activity-card {
  border: 1px solid #eaeaea;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.activity-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.activity-card.upcoming {
  border-left: 4px solid #1976d2;
}

.activity-card.ongoing {
  border-left: 4px solid #2e7d32;
}

.activity-card.completed {
  border-left: 4px solid #616161;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.activity-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e3c72;
  margin: 0;
  flex: 1;
}

.activity-status {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
}

.activity-status.upcoming {
  background: #1976d2;
}

.activity-status.ongoing {
  background: #2e7d32;
}

.activity-status.completed {
  background: #616161;
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

.activity-description {
  color: #666;
  line-height: 1.5;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 14px;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  max-width: 120px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: #e0e0e0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #4caf50;
  transition: width 0.3s;
}

.progress-text {
  font-size: 12px;
  color: #666;
  min-width: 30px;
}

.activity-actions {
  display: flex;
  gap: 10px;
}

.btn-action {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  justify-content: center;
}

.btn-join {
  background: #2a5298;
  color: white;
}

.btn-join:hover {
  background: #1e3c72;
}

.btn-review {
  background: #f8f9fa;
  color: #666;
  border: 1px solid #ddd;
}

.btn-review:hover {
  background: #e9ecef;
}

.btn-feedback {
  background: #ff9800;
  color: white;
}

.btn-feedback:hover {
  background: #f57c00;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-top: 1px solid #eaeaea;
  background: #f8f9fa;
}

.page-btn {
  padding: 8px 16px;
  background: white;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background: #2a5298;
  color: white;
  border-color: #2a5298;
}

.page-btn:disabled {
  background: #f8f9fa;
  color: #999;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
}

/* 模态框样式 */
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

.activity-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item label {
  font-weight: 500;
  color: #666;
}

.info-item .points {
  color: #ff9800;
  font-weight: 600;
}

.modal-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  padding: 20px 25px;
  border-top: 1px solid #eaeaea;
}

.btn-secondary {
  padding: 10px 20px;
  background: #f8f9fa;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background: #e9ecef;
}

.btn-primary {
  padding: 10px 20px;
  background: #2a5298;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary:hover {
  background: #1e3c72;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    padding: 15px;
  }

  .header-content {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    max-width: none;
  }

  .filter-options {
    justify-content: space-between;
  }

  .activities-grid {
    grid-template-columns: 1fr;
    padding: 0 15px 15px;
  }

  .activity-header {
    flex-direction: column;
    gap: 10px;
  }

  .activity-stats {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }

  .activity-actions {
    flex-direction: column;
  }

  .pagination {
    flex-direction: column;
    gap: 15px;
  }

  .error-actions {
    flex-direction: column;
  }

  .modal-actions {
    flex-direction: column;
  }
}
</style>
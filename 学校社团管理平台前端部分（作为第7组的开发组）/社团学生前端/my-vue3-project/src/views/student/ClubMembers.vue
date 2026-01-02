<template>
  <div class="club-members-page">
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
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <button class="btn-back" @click="goBack">
            <i class="fas fa-arrow-left"></i>
            返回
          </button>
          <div class="header-info">
            <h1>{{ clubInfo.name }} - 成员列表</h1>
            <p>共 {{ totalElements || 0 }} 名成员</p>
          </div>
          <div class="header-actions">
            <button class="btn-export" @click="exportMembers">
              <i class="fas fa-download"></i>
              导出名单
            </button>
          </div>
        </div>
      </div>

      <!-- 搜索和筛选 -->
      <div class="filter-section">
        <div class="search-box">
          <i class="fas fa-search"></i>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="搜索成员姓名或学号..."
            @input="handleSearch"
          >
        </div>
        <div class="filter-options">
          <select v-model="selectedRole" @change="filterMembers">
            <option value="">全部角色</option>
            <option value="社长">社长</option>
            <option value="副社长">副社长</option>
            <option value="普通成员">普通成员</option>
          </select>
          <select v-model="sortBy" @change="sortMembers">
            <option value="name">按姓名排序</option>
            <option value="joinDate">按加入时间</option>
            <option value="grade">按年级排序</option>
          </select>
        </div>
      </div>

      <!-- 成员列表 -->
      <div class="members-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <i class="fas fa-spinner fa-spin"></i>
          <p>正在加载成员数据...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-triangle"></i>
          <h3>数据加载失败</h3>
          <p>{{ error }}</p>
          <div class="error-actions">
            <button class="btn-retry" @click="loadMembers">
              <i class="fas fa-redo"></i>
              重新加载
            </button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="members.length === 0" class="empty-state">
          <i class="fas fa-users-slash"></i>
          <h3>暂无成员数据</h3>
          <p>没有找到符合条件的成员</p>
          <button class="btn-clear-filters" @click="clearFilters">
            清除筛选条件
          </button>
        </div>

        <!-- 成员列表 -->
        <div v-else class="members-list">
          <div 
            v-for="member in members" 
            :key="member.id"
            class="member-card"
          >
            <div class="member-avatar">
              <div class="avatar-placeholder">
                {{ getInitials(member.name) }}
              </div>
            </div>
            
            <div class="member-info">
              <div class="member-name-role">
                <h3 class="member-name">{{ member.name }}</h3>
                <span class="role-badge" :class="getRoleClass(member.role)">
                  {{ member.role }}
                </span>
              </div>
              <div class="member-details">
                <div class="detail-item">
                  <i class="fas fa-id-card"></i>
                  <span>学号: {{ member.studentId }}</span>
                </div>
                <div class="detail-item">
                  <i class="fas fa-graduation-cap"></i>
                  <span>专业: {{ member.major }}</span>
                </div>
                <div class="detail-item">
                  <i class="fas fa-user-graduate"></i>
                  <span>年级: {{ member.grade }}</span>
                </div>
                <div class="detail-item">
                  <i class="fas fa-calendar-alt"></i>
                  <span>加入时间: {{ formatDate(member.joinDate) }}</span>
                </div>
              </div>
            </div>

            <div class="member-status">
              <div class="status-indicator" :class="member.status === 1 ? 'active' : 'inactive'">
                {{ member.status === 1 ? '活跃' : '非活跃' }}
              </div>
            </div>

            <div class="member-actions">
              <button 
                class="btn-action btn-contact"
                @click="contactMember(member)"
              >
                <i class="fas fa-comment"></i>
                联系
              </button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="members.length > 0" class="pagination">
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
              第 {{ currentPage }} 页，共 {{ totalPages || 1 }} 页
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

    <!-- 联系成员模态框 -->
    <div v-if="showContactModal" class="modal-overlay">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3>联系 {{ selectedMember?.name }}</h3>
          <button class="close-btn" @click="showContactModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-content">
          <div class="contact-methods">
            <div class="contact-item">
              <i class="fas fa-id-card"></i>
              <div class="contact-info">
                <div class="contact-label">学号</div>
                <div class="contact-value">{{ selectedMember?.studentId }}</div>
              </div>
              <button class="btn-copy" @click="copyToClipboard(selectedMember?.studentId)">
                复制
              </button>
            </div>
            <div class="contact-item">
              <i class="fas fa-graduation-cap"></i>
              <div class="contact-info">
                <div class="contact-label">专业</div>
                <div class="contact-value">{{ selectedMember?.major }}</div>
              </div>
            </div>
            <div class="contact-item">
              <i class="fas fa-user-graduate"></i>
              <div class="contact-info">
                <div class="contact-label">年级</div>
                <div class="contact-value">{{ selectedMember?.grade }}</div>
              </div>
            </div>
          </div>
          <div class="contact-note">
            <i class="fas fa-info-circle"></i>
            如需联系该成员，请通过学号查询联系方式
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="showContactModal = false">
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MyNavbar from '@/components/MyNavbar.vue';
import axios from 'axios';

export default {
  name: 'ClubMembers',
  components: { MyNavbar },
  data() {
    return {
      userInfo: null,
      currentRoute: 'club-members',
      clubInfo: {
        id: null,
        name: '社团名称'
      },
      members: [],
      loading: false,
      error: null,
      searchQuery: '',
      selectedRole: '',
      sortBy: 'name',
      currentPage: 1,
      pageSize: 10,
      totalPages: 1,
      totalElements: 0,
      showContactModal: false,
      selectedMember: null,
      searchTimer: null
    };
  },
  computed: {
    userToken() {
      return localStorage.getItem('studentToken');
    }
  },
  async created() {
    await this.loadUserInfo();
    await this.loadClubInfo();
    await this.loadMembers();
  },
  methods: {
    // 加载用户信息
    async loadUserInfo() {
      try {
        // 从localStorage获取用户信息或调用API
        const token = localStorage.getItem('studentToken');
        if (token) {
          // 解码token获取用户信息（实际项目中可能需要调用API）
          // 这里假设token中包含了基本信息，或者我们可以调用profile API
          this.userInfo = {
            name: '学生用户',
            studentId: localStorage.getItem('studentId') || '未知'
          };
        }
      } catch (error) {
        console.error('加载用户信息失败:', error);
      }
    },

    // 加载社团信息
    async loadClubInfo() {
      this.clubInfo.id = this.$route.params.clubId;
      // 这里可以添加获取社团名称的API调用
      // 暂时使用路由参数中的clubId
      this.clubInfo.name = `社团 ${this.clubInfo.id}`;
    },

    // 加载成员数据
    async loadMembers() {
      this.loading = true;
      this.error = null;
      
      try {
        const token = this.userToken;
        if (!token) {
          throw new Error('请先登录');
        }

        const params = {
          page: this.currentPage - 1, // API页码从0开始
          page_size: this.pageSize
        };

        // 添加搜索条件
        if (this.searchQuery) {
          params.keyword = this.searchQuery;
        }

        // 添加角色筛选
        if (this.selectedRole) {
          params.role = this.selectedRole;
        }

        // 注意：API文档中URL是/club-admin/members，但学生端可能需要不同的权限
        // 根据实际情况调整API端点
        const response = await axios.get('http://10.34.70.91:8080/api/v1/student/clubs/' + this.clubInfo.id + '/members', {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          params: params
        });

    console.log('API返回的原始数据:', response.data);
        if (response.data.code === 200) {
          this.members = response.data.data.content || [];
          this.totalPages = response.data.data.totalPages || 1;
          this.totalElements = response.data.data.totalElements || 0;
          
          // 如果需要本地排序，可以在这里调用
          if (this.sortBy) {
            this.sortMembers();
          }
        } else {
          throw new Error(response.data.message || '获取成员列表失败');
        }
      } catch (error) {
        console.error('加载成员数据失败:', error);
        this.error = error.message || '无法加载成员数据，请检查网络连接';
        
        // 如果是401错误，跳转到登录页
        if (error.response && error.response.status === 401) {
          localStorage.removeItem('studentToken');
          this.$router.push('/student/login');
        }
      } finally {
        this.loading = false;
      }
    },

    // 处理搜索（添加防抖）
    handleSearch() {
      clearTimeout(this.searchTimer);
      this.searchTimer = setTimeout(() => {
        this.currentPage = 1;
        this.loadMembers();
      }, 500);
    },

    // 筛选成员
    filterMembers() {
      this.currentPage = 1;
      this.loadMembers();
    },

    // 本地排序成员
    sortMembers() {
      if (!this.members.length) return;
      
      this.members.sort((a, b) => {
        switch (this.sortBy) {
          case 'name':
            return a.name.localeCompare(b.name);
          case 'joinDate':
            return new Date(b.joinDate) - new Date(a.joinDate);
          case 'grade':
            return b.grade.localeCompare(a.grade);
          default:
            return 0;
        }
      });
    },

    // 清除筛选条件
    clearFilters() {
      this.searchQuery = '';
      this.selectedRole = '';
      this.currentPage = 1;
      this.loadMembers();
    },

    // 分页
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
        this.loadMembers();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    },

    // 获取角色对应的CSS类
    getRoleClass(role) {
      const roleMap = {
        '社长': 'president',
        '副社长': 'vice_president',
        '管理员': 'manager',
        '普通成员': 'member'
      };
      return roleMap[role] || 'member';
    },

    // 获取姓名的首字母
    getInitials(name) {
      if (!name) return '?';
      return name.charAt(0);
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '';
      try {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN', {
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        });
      } catch (error) {
        return dateString;
      }
    },

    // 联系成员
    contactMember(member) {
      this.selectedMember = member;
      this.showContactModal = true;
    },

    // 导出成员名单
    exportMembers() {
      const csvContent = this.generateCSV();
      this.downloadCSV(csvContent, `${this.clubInfo.name}_成员名单.csv`);
    },

    // 生成CSV内容
    generateCSV() {
      const headers = ['姓名', '学号', '专业', '年级', '角色', '加入时间', '状态'];
      const rows = this.members.map(member => [
        member.name,
        member.studentId,
        member.major,
        member.grade,
        member.role,
        this.formatDate(member.joinDate),
        member.status === 1 ? '活跃' : '非活跃'
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

    // 复制到剪贴板
    copyToClipboard(text) {
      navigator.clipboard.writeText(text).then(() => {
        alert('已复制到剪贴板');
      }).catch(err => {
        console.error('复制失败:', err);
        alert('复制失败，请手动复制');
      });
    },

    // 导航方法
    handleNavigate(routeName) {
      this.$router.push({ name: routeName });
    },

    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('studentToken');
        localStorage.removeItem('studentId');
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
.club-members-page {
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

/* 成员容器 */
.members-container {
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

.btn-retry {
  padding: 10px 20px;
  background: #2a5298;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-retry:hover {
  background: #1e3c72;
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

/* 成员列表 */
.members-list {
  padding: 0;
}

.member-card {
  display: flex;
  align-items: center;
  padding: 25px;
  border-bottom: 1px solid #eaeaea;
  transition: background-color 0.3s;
  gap: 20px;
}

.member-card:hover {
  background-color: #f8f9fa;
}

.member-card:last-child {
  border-bottom: none;
}

.member-avatar {
  flex-shrink: 0;
}

.avatar-placeholder {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 24px;
}

.member-info {
  flex: 1;
}

.member-name-role {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.member-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.role-badge {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.role-badge.president {
  background: #e74c3c;
}

.role-badge.vice_president {
  background: #e67e22;
}

.role-badge.manager {
  background: #f39c12;
}

.role-badge.member {
  background: #3498db;
}

.member-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.detail-item i {
  width: 16px;
  color: #999;
}

.member-status {
  margin-right: 20px;
}

.status-indicator {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
}

.status-indicator.active {
  background: #d4edda;
  color: #155724;
}

.status-indicator.inactive {
  background: #f8d7da;
  color: #721c24;
}

.member-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
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
}

.btn-contact {
  background: #e3f2fd;
  color: #2196F3;
}

.btn-contact:hover {
  background: #2196F3;
  color: white;
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

.contact-methods {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.contact-item i {
  font-size: 20px;
  color: #2a5298;
  width: 24px;
}

.contact-info {
  flex: 1;
}

.contact-label {
  font-weight: 500;
  color: #333;
  margin-bottom: 3px;
}

.contact-value {
  color: #666;
  font-size: 14px;
}

.btn-copy {
  padding: 6px 12px;
  background: #2a5298;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s;
}

.btn-copy:hover {
  background: #1e3c72;
}

.contact-note {
  margin-top: 20px;
  padding: 12px;
  background: #e3f2fd;
  border-radius: 6px;
  color: #2a5298;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.modal-actions {
  display: flex;
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

  .member-card {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }

  .member-name-role {
    justify-content: center;
  }

  .member-status {
    margin-right: 0;
  }

  .member-actions {
    justify-content: center;
  }

  .pagination {
    flex-direction: column;
    gap: 15px;
  }

  .error-actions {
    flex-direction: column;
  }
}
</style>
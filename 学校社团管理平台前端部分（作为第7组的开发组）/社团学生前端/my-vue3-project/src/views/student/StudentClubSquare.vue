<template>
  <div class="club-square">
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
      <div class="page-header">
        <h2>社团广场</h2>
        <p>探索全校社团，找到你的兴趣所在</p>
      </div>

      <!-- 筛选和搜索区域 -->
      <div class="filter-section">
        <div class="search-box">
          <i class="fas fa-search"></i>
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="搜索社团名称或关键词..."
            @input="filterClubs"
          >
        </div>
        
        <div class="filter-controls">
          <div class="filter-group">
            <label>社团类型:</label>
            <select v-model="selectedType" @change="filterClubs">
              <option value="">全部类型</option>
              <option value="文化艺术类">文化艺术类</option>
              <option value="学术科技类">学术科技类</option>
              <option value="体育健身类">体育健身类</option>
              <option value="公益服务类">公益服务类</option>
              <option value="兴趣爱好类">兴趣爱好类</option>
            </select>
          </div>
          
          <div class="filter-group">
            <label>成员规模:</label>
            <select v-model="selectedSize" @change="filterClubs">
              <option value="">全部规模</option>
              <option value="small">小型 (1-20人)</option>
              <option value="medium">中型 (21-50人)</option>
              <option value="large">大型 (51人以上)</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>正在加载社团数据...</p>
      </div>

      <!-- 社团列表 -->
      <div v-else class="clubs-container">
        <div 
          v-for="club in filteredClubs" 
          :key="club.clubId" 
          class="club-card"
          @click="goToClubPage(club.clubId)"
        >
          <div class="club-header">
            <h3 class="club-name">{{ club.name }}</h3>
            <div class="club-members">成员: {{ club.memberCount }}人</div>
          </div>
          
          <div class="club-description">
            {{ club.description || '暂无描述' }}
          </div>
          
          <div class="club-stats">
            <div class="stat-item">
              <span class="stat-label">社长:</span>
              <span class="stat-value">{{ club.presidentName || '待任命' }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">类型:</span>
              <span class="stat-value">{{ club.categoryName }}</span>
            </div>
          </div>
          
          <div class="club-tags">
            <span class="tag type-tag">{{ club.categoryName }}</span>
            <span class="tag size-tag">{{ getSizeLabel(club.memberCount) }}</span>
          </div>
          
          <button 
            class="join-btn"
            @click.stop="applyToJoin(club.clubId)"
          >
            + 申请加入
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && filteredClubs.length === 0" class="empty-state">
        <i class="fas fa-search"></i>
        <h3>未找到符合条件的社团</h3>
        <p>尝试调整筛选条件或搜索关键词</p>
      </div>
    </main>
  </div>
</template>

<script>
import MyNavbar from '@/components/MyNavbar.vue';
import { clubApi, profileApi } from '@/api/studentApi';

export default {
  name: 'ClubSquare',

  components: { MyNavbar },
  
  data() {
    return {
      userInfo: null,
      currentRoute: 'clubsquare',

      searchQuery: '',
      selectedType: '',
      selectedSize: '',
      clubs: [],
      filteredClubs: [],
      loading: false
    }
  },
  
  async created() {
    await this.loadUserInfo();
    await this.loadClubs();
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
    
    // 加载社团列表
    async loadClubs() {
      this.loading = true;
      try {
        const response = await clubApi.getClubs();
        // 根据API响应结构，可能需要调整为 response.list
        this.clubs = Array.isArray(response) ? response : (response.list || []);
        this.filteredClubs = [...this.clubs];
      } catch (error) {
        console.error('获取社团列表失败:', error);
        // 可以在这里添加错误提示
        this.clubs = [];
        this.filteredClubs = [];
      } finally {
        this.loading = false;
      }
    },
    
    filterClubs() {
      this.filteredClubs = this.clubs.filter(club => {
        // 搜索条件匹配
        const matchesSearch = this.searchQuery === '' || 
          (club.name && club.name.toLowerCase().includes(this.searchQuery.toLowerCase())) ||
          (club.description && club.description.toLowerCase().includes(this.searchQuery.toLowerCase()));
        
        // 类型条件匹配
        const matchesType = this.selectedType === '' || 
          (club.categoryName && club.categoryName === this.selectedType);
        
        // 规模条件匹配（根据人数判断）
        const matchesSize = this.selectedSize === '' || this.checkClubSize(club.memberCount);
        
        return matchesSearch && matchesType && matchesSize;
      });
    },
    
    // 根据人数判断规模
    checkClubSize(memberCount) {
      if (!memberCount) return false;
      
      switch (this.selectedSize) {
        case 'small':
          return memberCount <= 20;
        case 'medium':
          return memberCount > 20 && memberCount <= 50;
        case 'large':
          return memberCount > 50;
        default:
          return true;
      }
    },
    
    getSizeLabel(memberCount) {
      if (!memberCount) return '未知';
      
      if (memberCount <= 20) return '小型';
      if (memberCount <= 50) return '中型';
      return '大型';
    },
    
    goToClubPage(clubId) {
      this.$router.push({
        name: 'StudentClubDetail',
        params: { clubId }
      });
    },
    
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
    
    handleNavigate(routeName) {
      this.$router.push({ name: routeName });
    },
    
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        console.log('执行退出登录操作');
        localStorage.removeItem('studentToken');
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

.club-square {
  min-height: 100vh;
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

.page-header h2 {
  font-size: 28px;
  color: #1e3c72;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
}

/* 筛选区域 */
.filter-section {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 300px;
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
  padding: 12px 20px 12px 45px;
  border: 1px solid #ddd;
  border-radius: 25px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.search-box input:focus {
  outline: none;
  border-color: #2a5298;
}

.filter-controls {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
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
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background: white;
  cursor: pointer;
  min-width: 150px;
}

/* 社团卡片 */
.clubs-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
}

.club-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.club-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.club-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #1e3c72, #2a5298);
}

.club-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.club-name {
  font-size: 20px;
  color: #1e3c72;
  font-weight: 600;
}

.club-members {
  color: #666;
  font-size: 14px;
  white-space: nowrap;
}

.club-description {
  color: #555;
  margin-bottom: 20px;
  line-height: 1.5;
}

.club-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-weight: 600;
  color: #1e3c72;
}

.club-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.tag {
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.type-tag {
  background: #e3f2fd;
  color: #1976d2;
}

.size-tag {
  background: #f3e5f5;
  color: #7b1fa2;
}

.join-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.join-btn:hover {
  background: linear-gradient(135deg, #2a5298, #3a62a8);
  transform: translateY(-2px);
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
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    min-width: 100%;
  }
  
  .filter-controls {
    justify-content: space-between;
  }
  
  .clubs-container {
    grid-template-columns: 1fr;
  }
  
  .main-content {
    padding: 15px;
  }
}



.loading-state {
  text-align: center;
  padding: 60px 20px;
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

.loading-state p {
  font-size: 18px;
  color: #555;
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

.club-square {
  min-height: 100vh;
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

.page-header h2 {
  font-size: 28px;
  color: #1e3c72;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
}

/* 筛选区域 */
.filter-section {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 300px;
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
  padding: 12px 20px 12px 45px;
  border: 1px solid #ddd;
  border-radius: 25px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.search-box input:focus {
  outline: none;
  border-color: #2a5298;
}

.filter-controls {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
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
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background: white;
  cursor: pointer;
  min-width: 150px;
}

/* 社团卡片 */
.clubs-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
}

.club-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.club-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.club-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #1e3c72, #2a5298);
}

.club-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.club-name {
  font-size: 20px;
  color: #1e3c72;
  font-weight: 600;
}

.club-members {
  color: #666;
  font-size: 14px;
  white-space: nowrap;
}

.club-description {
  color: #555;
  margin-bottom: 20px;
  line-height: 1.5;
}

.club-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-weight: 600;
  color: #1e3c72;
}

.club-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.tag {
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.type-tag {
  background: #e3f2fd;
  color: #1976d2;
}

.size-tag {
  background: #f3e5f5;
  color: #7b1fa2;
}

.join-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.join-btn:hover {
  background: linear-gradient(135deg, #2a5298, #3a62a8);
  transform: translateY(-2px);
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
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    min-width: 100%;
  }
  
  .filter-controls {
    justify-content: space-between;
  }
  
  .clubs-container {
    grid-template-columns: 1fr;
  }
  
  .main-content {
    padding: 15px;
  }
}
</style>
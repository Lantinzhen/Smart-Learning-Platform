<template>
  <div class="application-page">
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
      <div class="application-container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1>申请加入 {{ clubName }} 社团</h1>
          <p>请填写以下申请信息，我们将尽快审核您的申请</p>
          <div class="club-info">
            <span>社团ID: {{ clubId }}</span>
          </div>
        </div>

        <!-- 申请表单 -->
        <form class="application-form" @submit.prevent="submitApplication">
          <!-- 基本信息部分 -->
          <section class="form-section">
            <h2 class="section-title">基本信息</h2>
            <div class="form-grid">
              <div class="form-group">
                <label for="name" class="required">姓名</label>
                <input
                  id="name"
                  type="text"
                  v-model="form.name"
                  placeholder="请输入您的姓名"
                  required
                  :disabled="isFormLocked"
                >
              </div>

              <div class="form-group">
                <label for="studentId" class="required">学号</label>
                <input
                  id="studentId"
                  type="text"
                  v-model="form.student_id"
                  placeholder="请输入您的学号"
                  required
                  :disabled="isFormLocked"
                >
              </div>

              <div class="form-group">
                <label for="major" class="required">专业</label>
                <input
                  id="major"
                  type="text"
                  v-model="form.major"
                  placeholder="请输入您的专业"
                  required
                  :disabled="isFormLocked"
                >
              </div>

              <div class="form-group">
                <label for="grade" class="required">年级</label>
                <select id="grade" v-model="form.grade" required :disabled="isFormLocked">
                  <option value="">请选择年级</option>
                  <option value="2021级">2021级</option>
                  <option value="2022级">2022级</option>
                  <option value="2023级">2023级</option>
                  <option value="2024级">2024级</option>
                  <option value="2025级">2025级</option>
                </select>
              </div>

              <div class="form-group">
                <label for="phone" class="required">联系电话</label>
                <input
                  id="phone"
                  type="tel"
                  v-model="form.phone"
                  placeholder="请输入您的联系电话"
                  required
                  :disabled="isFormLocked"
                >
              </div>

              <div class="form-group">
                <label for="email" class="required">邮箱</label>
                <input
                  id="email"
                  type="email"
                  v-model="form.email"
                  placeholder="请输入您的邮箱"
                  required
                  :disabled="isFormLocked"
                >
              </div>
            </div>
          </section>

          <!-- 申请理由部分 -->
          <section class="form-section">
            <h2 class="section-title">申请信息</h2>
            <div class="form-group">
              <label for="reason" class="required">申请理由</label>
              <textarea
                id="reason"
                v-model="form.reason"
                placeholder="请说明您为什么想要加入本社团..."
                rows="4"
                required
              ></textarea>
            </div>

            <div class="form-group">
              <label for="experience">相关经验</label>
              <textarea
                id="experience"
                v-model="form.experience"
                placeholder="请描述您在该领域相关的经验或技能..."
                rows="4"
              ></textarea>
            </div>
          </section>

          <!-- 期望参与的活动类型 -->
          <section class="form-section">
            <h2 class="section-title">期望参与的活动类型</h2>
            <div class="checkbox-group">
              <label v-for="activity in activityTypes" :key="activity.value" class="checkbox-label">
                <input
                  type="checkbox"
                  :value="activity.label"
                  v-model="form.activity_preference"

                >
                <span class="checkmark"></span>
                {{ activity.label }}
              </label>
            </div>
          </section>

          <!-- 可参与时间 -->
          <section class="form-section">
            <h2 class="section-title">可参与时间</h2>
            <div class="checkbox-group">
              <label v-for="day in availableDays" :key="day.value" class="checkbox-label">
                <input
                  type="checkbox"
                  :value="day.label"
                  v-model="form.available_time"

                >
                <span class="checkmark"></span>
                {{ day.label }}
              </label>
            </div>
          </section>

          <!-- 作品展示 -->
          <section class="form-section">
            <h2 class="section-title">作品展示（可选）</h2>
            <div class="upload-section">
              <p class="upload-hint">可上传您的作品链接或文件（支持 JPG、PNG、PDF格式，单个文件不超过 10MB）</p>
              
              <div class="upload-input-group">
                <input
                  type="text"
                  v-model="form.portfolio"
                  placeholder="请输入作品链接（如 GitHub、个人网站等）"
                  class="portfolio-input"
                >
              </div>
              
              <div class="upload-area" @click="triggerFileInput" :class="{ 'has-files': form.portfolioFiles.length > 0 }">
                <div v-if="form.portfolioFiles.length === 0" class="upload-placeholder">
                  <i class="fas fa-cloud-upload-alt"></i>
                  <p>或点击上传作品文件</p>
                </div>
                <div v-else class="uploaded-files">
                  <div v-for="(file, index) in form.portfolioFiles" :key="index" class="file-item">
                    <i class="fas fa-file-image"></i>
                    <span class="file-name">{{ file.name }}</span>
                    <button type="button" class="remove-file" @click.stop="removeFile(index)">
                      <i class="fas fa-times"></i>
                    </button>
                  </div>
                </div>
                <input
                  ref="fileInput"
                  type="file"
                  multiple
                  accept=".jpg,.jpeg,.png,.pdf"
                  @change="handleFileUpload"
                  style="display: none"
                >
              </div>
            </div>
          </section>

          <!-- 表单操作按钮 -->
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="cancelApplication">
              取消申请
            </button>
            <button type="submit" class="btn-submit" :disabled="isSubmitting">
              {{ isSubmitting ? '提交中...' : '提交申请' }}
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<script>
import MyNavbar from '@/components/MyNavbar.vue';
import { profileApi, clubApi } from '@/api/studentApi';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'ClubApplication',
  components: { MyNavbar },
  props: {
    // 移除 clubId prop
  },
  data() {
    return {
      // 用户信息
      userInfo: {
        name: '',
        studentId: '',
        major: '',
        grade: '',
        phone: '',
        email: ''
      },
      
      currentRoute: 'clubsquare',
      isFormLocked: false, // 是否锁定基本信息（从API获取的数据）
      isSubmitting: false, // 是否正在提交
      
      form: {
        club_id: '', // 对应API的club_id
        name: '',
        student_id: '', 
        major: '',
        grade: '',
        phone: '',
        email: '',
        reason: '',
        experience: '',
        activity_preference: [], // 对应API的activity_preference
        available_time: [], // 对应API的available_time
        portfolio: '', // 对应API的portfolio（链接）
        portfolioFiles: [] // 本地文件，需要转换为链接
      },
      
      activityTypes: [
        { value: 'outdoor', label: '外拍活动' },
        { value: 'lecture', label: '线下讲座' },
        { value: 'exhibition', label: '作品展览' },
        { value: 'technical', label: '技术交流' },
        { value: 'equipment', label: '设备分享' },
        { value: 'other', label: '其他活动' }
      ],
      
      availableDays: [
        { value: 'monday', label: '周一' },
        { value: 'tuesday', label: '周二' },
        { value: 'wednesday', label: '周三' },
        { value: 'thursday', label: '周四' },
        { value: 'friday', label: '周五' },
        { value: 'saturday', label: '周六' },
        { value: 'sunday', label: '周日' },
        { value: 'anytime', label: '随时' }
      ],
      
      clubName: '', // 将 clubName 移动到 data 中
      clubId: '' // 将 clubId 移动到 data 中
    }
  },
  
  async created() {
    // 从路由参数获取社团ID和名称
    this.clubId = this.$route.params.clubId || this.clubId;
    
    // 从路由query参数获取社团名称
    if (this.$route.query.clubName) {
      this.clubName = this.$route.query.clubName; // 直接赋值给 data 中的 clubName
    }
    
    // 如果没有通过路由参数获取到，尝试从props获取
    if (!this.clubName || this.clubName === '未知社团') {
      this.clubName = this.$route.params.clubName || '未知社团';
    }
    
    // 设置社团ID到表单
    this.form.club_id = parseInt(this.clubId) || this.clubId;
    
    // 加载用户信息
    await this.loadUserInfo();
    
    // 加载社团详细信息（如果需要）
    await this.loadClubInfo();
  },
  
  methods: {

      isFormValid() {
    // 检查所有必填字段
    const requiredFields = [
      this.form.name,
      this.form.student_id,
      this.form.major,
      this.form.grade,
      this.form.phone,
      this.form.email,
      this.form.reason
    ];
    
    // 确保所有必填字段都不为空
    const allFilled = requiredFields.every(field => 
      field && field.toString().trim().length > 0
    );
    
    // 可选：额外验证邮箱格式
    if (!allFilled) return false;
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.form.email)) return false;
    
    return true;
  },
    
    // 加载用户信息
    async loadUserInfo() {
      try {
        // 调用API获取用户信息
        const response = await profileApi.getProfile();
        this.userInfo = response;
        
        // 自动填充用户信息到表单（按照API字段名）
        this.form.name = response.name || '';
        this.form.student_id = response.studentId || response.student_id || '';
        this.form.major = response.major || '';
        this.form.grade = response.grade || '';
        this.form.phone = response.phone || '';
        this.form.email = response.email || '';
        
        // 锁定已填充的基本信息字段
        if (response.studentId || response.student_id) {
          this.isFormLocked = true;
        }
        
      } catch (error) {
        console.error('加载用户信息失败:', error);
        // 显示错误提示
        ElMessage.error('加载用户信息失败，请刷新页面重试');
      }
    },
    
    // 加载社团信息
    async loadClubInfo() {
      if (!this.clubId) return;
      
      try {
        // 如果社团名称没有通过路由参数传递，则调用API获取
        if (!this.clubName || this.clubName === '未知社团') {
          const response = await clubApi.getClubDetail(this.clubId);
          this.clubName = response.name || '未知社团';
        }
      } catch (error) {
        console.error('加载社团信息失败:', error);
      }
    },
    
    handleNavigate(routeName) {
      this.$router.push({ name: routeName });
    },
    
    handleLogout() {
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('studentToken');
        this.$router.push('/student/login');
      }).catch(() => {
        // 取消操作
      });
    },
    
    goBack() {
      this.$router.go(-1)
    },
    
    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    
    handleFileUpload(event) {
      const files = Array.from(event.target.files)
      // 文件大小验证 (10MB)
      const validFiles = files.filter(file => file.size <= 10 * 1024 * 1024)
      
      if (validFiles.length !== files.length) {
        ElMessage.warning('部分文件超过10MB限制，已自动过滤')
      }
      
      this.form.portfolioFiles = [...this.form.portfolioFiles, ...validFiles]
      event.target.value = '' // 重置input
    },
    
    removeFile(index) {
      this.form.portfolioFiles.splice(index, 1)
    },
    
    // 处理文件上传到服务器（实际项目中需要实现）
    async uploadFiles(files) {
      if (!files || files.length === 0) return '';
      
      // 这里应该实现文件上传逻辑
      // 假设我们只处理第一个文件，并返回一个临时的URL
      // 实际项目中需要调用文件上传API
      const file = files[0];
      return `http://example.com/portfolio/${file.name}`;
    },
    
    cancelApplication() {
      ElMessageBox.confirm('确定要取消申请吗？所有填写的信息将不会被保存。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.goBack()
      }).catch(() => {
        // 取消操作
      });
    },
    
    // 准备提交的数据
    prepareSubmitData() {
      const submitData = {
        club_id: this.form.club_id,
        name: this.form.name,
        student_id: this.form.student_id, // 注意字段名
        major: this.form.major,
        grade: this.form.grade,
        phone: this.form.phone,
        email: this.form.email,
        reason: this.form.reason,
        experience: this.form.experience || '',
        activity_preference: this.form.activity_preference.join('、'),
        available_time: this.form.available_time.join('、'),
        portfolio: this.form.portfolio || ''
      };
      
      return submitData;
    },
    
    async submitApplication() {
      if (!this.isFormValid) {
        ElMessage.error('请填写所有必填字段');
        return;
      }
      
      this.isSubmitting = true;
      
      try {
        // 准备提交数据
        const submitData = this.prepareSubmitData();
        
        // 如果有上传的文件，先上传文件获取URL
        if (this.form.portfolioFiles.length > 0) {
          const fileUrl = await this.uploadFiles(this.form.portfolioFiles);
          if (fileUrl) {
            submitData.portfolio = submitData.portfolio 
              ? `${submitData.portfolio}, ${fileUrl}`
              : fileUrl;
          }
        }
        
        // 调用API提交申请
        const response = await clubApi.applyClub(this.clubId, submitData);
        
        if (response && (response.code === 200 || response.application_id)) {
          ElMessage.success('申请提交成功！我们将尽快审核您的申请。');
          
          // 跳转到社团广场或我的申请页面
          setTimeout(() => {
            this.$router.push({ name: 'clubsquare' });
          }, 1500);
        } else {
          throw new Error(response?.message || '申请提交失败');
        }
        
      } catch (error) {
        console.error('提交申请失败:', error);
        ElMessage.error(error.message || '申请提交失败，请稍后重试');
      } finally {
        this.isSubmitting = false;
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
  background-color: #f8f9fa;
  color: #333;
  line-height: 1.6;
}

.application-page {
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

/* 主内容区域 */
.main-content {
  padding: 30px;
  max-width: 1000px;
  margin: 0 auto;
}

.application-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.page-header {
  padding: 30px;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
  text-align: center;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 10px;
}

.page-header p {
  opacity: 0.9;
}

/* 表单样式 */
.application-form {
  padding: 30px;
}

.form-section {
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eaeaea;
}

.form-section:last-of-type {
  border-bottom: none;
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

/* 表单网格布局 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

label.required::after {
  content: '*';
  color: #e74c3c;
  margin-left: 4px;
}

input, select, textarea {
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

input:focus, select:focus, textarea:focus {
  outline: none;
  border-color: #2a5298;
  box-shadow: 0 0 0 3px rgba(42, 82, 152, 0.1);
}

textarea {
  resize: vertical;
  min-height: 100px;
}

/* 复选框组样式 */
.checkbox-group {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.checkbox-label, .commitment-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 0;
}

.checkbox-label input, .commitment-label input {
  margin: 0;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 2px solid #ddd;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

input[type="checkbox"] {
  display: none;
}

input[type="checkbox"]:checked + .checkmark {
  background: #2a5298;
  border-color: #2a5298;
}

input[type="checkbox"]:checked + .checkmark::after {
  content: '✓';
  color: white;
  font-size: 12px;
  font-weight: bold;
}

/* 文件上传区域 */
.upload-section {
  margin-top: 15px;
}

.upload-hint {
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #2a5298;
  background: #f8f9fa;
}

.upload-area.has-files {
  border-style: solid;
  padding: 20px;
}

.upload-placeholder i {
  font-size: 48px;
  color: #ccc;
  margin-bottom: 15px;
}

.upload-placeholder p {
  color: #666;
}

.uploaded-files {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.file-item i {
  color: #2a5298;
}

.file-name {
  flex: 1;
  font-size: 14px;
}

.remove-file {
  background: none;
  border: none;
  color: #e74c3c;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.remove-file:hover {
  background: #ffeaea;
}

/* 承诺部分 */
.commitment-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.commitment-label {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.commitment-label:hover {
  background: #e9ecef;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #eaeaea;
}

.btn-cancel, .btn-submit {
  padding: 14px 40px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 150px;
}

.btn-cancel {
  background: #f8f9fa;
  color: #666;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background: #e9ecef;
}

.btn-submit {
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: linear-gradient(135deg, #2a5298, #3a62a8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(42, 82, 152, 0.3);
}

.btn-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
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
  
  .main-content {
    padding: 15px;
  }
  
  .application-form {
    padding: 20px;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .checkbox-group {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn-cancel, .btn-submit {
    width: 100%;
  }
}
/* 保持原有的样式不变，只添加新样式 */

.club-info {
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
}

.upload-input-group {
  margin-bottom: 15px;
}

.portfolio-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.portfolio-input:focus {
  outline: none;
  border-color: #2a5298;
  box-shadow: 0 0 0 3px rgba(42, 82, 152, 0.1);
}

.btn-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .club-info {
    text-align: center;
  }
}

/* 保持原有的其他样式不变 */
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

.application-page {
  min-height: 100vh;
}
</style>
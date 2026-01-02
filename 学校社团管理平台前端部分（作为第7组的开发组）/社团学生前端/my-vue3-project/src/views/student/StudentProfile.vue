<template>
  <div class="user-profile-container">
    <!-- 顶部导航栏 -->
    <MyNavbar 
      :user-name="userInfo?.name || '用户'" 
      :user-id="userInfo?.studentId || '未知学号'" 
      :current-route="currentRoute"
      @navigate="handleNavigate"
      @logout="handleLogout"
    />

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-content">
          <h1>用户资料</h1>
          <p>管理您的个人信息和账户设置</p>
        </div>
      </div>

      <!-- 提示消息 -->
      <div v-if="successMessage" class="success-message">
        <i class="fas fa-check-circle"></i>
        {{ successMessage }}
      </div>
      <div v-if="errorMessage" class="error-message">
        <i class="fas fa-exclamation-circle"></i>
        {{ errorMessage }}
      </div>

      <!-- 标签页 -->
      <div class="tabs-container">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'profile' }"
          @click="activeTab = 'profile'"
        >
          <i class="fas fa-user"></i>
          个人信息
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'password' }"
          @click="activeTab = 'password'"
        >
          <i class="fas fa-lock"></i>
          修改密码
        </button>
      </div>

      <!-- 个人信息表单 -->
      <div v-if="activeTab === 'profile'" class="form-container">
        <form @submit.prevent="saveUserInfo" class="profile-form">
          <div class="form-section">
            <h2 class="section-title">
              <i class="fas fa-id-card"></i>
              基本信息
            </h2>
            
            <div class="form-row">
              <div class="form-group">
                <label for="name" class="form-label">姓名</label>
                <input 
                  type="text" 
                  id="name" 
                  v-model="userInfoForm.name"
                  class="form-input"
                  :class="{ 'input-error': nameError }"
                  placeholder="请输入姓名"
                  required
                  @blur="validateName"
                />
                <div v-if="nameError" class="field-error">
                  <i class="fas fa-exclamation-triangle"></i>
                  {{ nameError }}
                </div>
              </div>
              <div class="form-group">
                <label for="student_id" class="form-label">学号</label>
                <input 
                  type="text" 
                  id="student_id" 
                  v-model="userInfoForm.student_id"
                  class="form-input"
                  :class="{ 'input-error': studentIdError }"
                  placeholder="请输入学号"
                  required
                  readonly
                  disabled
                />
                <div class="field-hint">
                  <i class="fas fa-info-circle"></i>
                  学号不可修改
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="major" class="form-label">专业</label>
                <input 
                  type="text" 
                  id="major" 
                  v-model="userInfoForm.major"
                  class="form-input"
                  :class="{ 'input-error': majorError }"
                  placeholder="请输入专业"
                  required
                  readonly
                  disabled
                />
                <div class="field-hint">
                  <i class="fas fa-info-circle"></i>
                  专业信息请联系管理员修改
                </div>
              </div>
              <div class="form-group">
                <label for="grade" class="form-label">年级</label>
                <input
                  type="text"
                  id="grade"
                  v-model="userInfoForm.grade"
                  class="form-input"
                  :class="{ 'input-error': gradeError }"
                  placeholder="年级"
                  required
                  readonly
                  disabled
                />
                <div class="field-hint">
                  <i class="fas fa-info-circle"></i>
                  年级信息不可修改
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="phone" class="form-label">联系电话</label>
                <input 
                  type="tel" 
                  id="phone" 
                  v-model="userInfoForm.phone"
                  class="form-input"
                  :class="{ 'input-error': phoneError }"
                  placeholder="请输入联系电话"
                  required
                  maxlength="11"
                  @input="validatePhone"
                  @blur="validatePhone"
                />
                <div v-if="phoneError" class="field-error">
                  <i class="fas fa-exclamation-triangle"></i>
                  {{ phoneError }}
                </div>
              </div>
              <div class="form-group">
                <label for="email" class="form-label">电子邮箱</label>
                <input 
                  type="email" 
                  id="email" 
                  v-model="userInfoForm.email"
                  class="form-input"
                  :class="{ 'input-error': emailError }"
                  placeholder="请输入电子邮箱"
                  required
                  @blur="validateEmail"
                />
                <div v-if="emailError" class="field-error">
                  <i class="fas fa-exclamation-triangle"></i>
                  {{ emailError }}
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="gender" class="form-label">性别</label>
                <select
                  id="gender"
                  v-model="userInfoForm.gender"
                  class="form-select"
                  :class="{ 'input-error': genderError }"
                >
                  <option value="">请选择性别</option>
                  <option value="男">男</option>
                  <option value="女">女</option>
                  <option value="其他">其他</option>
                </select>
                <div v-if="genderError" class="field-error">
                  <i class="fas fa-exclamation-triangle"></i>
                  {{ genderError }}
                </div>
              </div>
            </div>
          </div>



          <div class="form-actions">
            <button type="submit" class="submit-btn" :disabled="isLoading || !isFormValid">
              <span v-if="isLoading" class="loading-spinner"></span>
              <i v-else class="fas fa-save"></i>
              {{ isLoading ? '保存中...' : '保存修改' }}
            </button>
          </div>
        </form>
      </div>

      <!-- 修改密码表单 -->
      <div v-if="activeTab === 'password'" class="form-container">
        <form @submit.prevent="changePassword" class="password-form">
          <div class="form-section">
            <h2 class="section-title">
              <i class="fas fa-key"></i>
              修改密码
            </h2>
            
            <div class="form-group">
              <label for="old_password" class="form-label">旧密码</label>
              <input 
                type="password" 
                id="old_password" 
                v-model="passwordForm.old_password"
                class="form-input"
                :class="{ 'input-error': oldPasswordError }"
                placeholder="请输入旧密码"
                required
                @blur="validateOldPassword"
              />
              <div v-if="oldPasswordError" class="field-error">
                <i class="fas fa-exclamation-triangle"></i>
                {{ oldPasswordError }}
              </div>
            </div>

            <div class="form-group">
              <label for="new_password" class="form-label">新密码</label>
              <input 
                type="password" 
                id="new_password" 
                v-model="passwordForm.new_password"
                class="form-input"
                :class="{ 'input-error': newPasswordError }"
                placeholder="请输入新密码（至少6位）"
                required
                @blur="validateNewPassword"
              />
              <div v-if="newPasswordError" class="field-error">
                <i class="fas fa-exclamation-triangle"></i>
                {{ newPasswordError }}
              </div>
            </div>

            <div class="form-group">
              <label for="confirm_password" class="form-label">确认新密码</label>
              <input 
                type="password" 
                id="confirm_password" 
                v-model="passwordForm.confirm_password"
                class="form-input"
                :class="{ 'input-error': confirmPasswordError }"
                placeholder="请再次输入新密码"
                required
                @blur="validateConfirmPassword"
              />
              <div v-if="confirmPasswordError" class="field-error">
                <i class="fas fa-exclamation-triangle"></i>
                {{ confirmPasswordError }}
              </div>
            </div>

            <div v-if="passwordError" class="form-error">
              <i class="fas fa-exclamation-circle"></i>
              {{ passwordError }}
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="submit-btn" :disabled="isLoading || !isPasswordFormValid">
              <span v-if="isLoading" class="loading-spinner"></span>
              <i v-else class="fas fa-key"></i>
              {{ isLoading ? '修改中...' : '修改密码' }}
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<script>
import MyNavbar from '@/components/MyNavbar.vue';
import { profileApi,authApi } from '@/api/studentApi';

export default {
  name: 'UserProfileView',
  components: {
    MyNavbar
  },
  data() {
    return {
      currentRoute: 'profile',
      userInfo: null,
      // 用户信息表单 - 根据API响应结构调整
      userInfoForm: {
        student_id: '',
        name: '',
        email: '',
        phone: '',
        major: '',
        grade: '',
        gender: '',
        avatar: ''
      },
      // 密码修改表单 - 根据API请求结构调整
      passwordForm: {
        old_password: '',
        new_password: '',
        confirm_password: ''
      },
      // 状态管理
      isLoading: false,
      activeTab: 'profile',
      successMessage: '',
      errorMessage: '',
      passwordError: '',
      // 表单验证错误信息
      nameError: '',
      studentIdError: '',
      majorError: '',
      gradeError: '',
      phoneError: '',
      emailError: '',
      genderError: '',
      oldPasswordError: '',
      newPasswordError: '',
      confirmPasswordError: ''
    }
  },
  computed: {
    // 头像预览
    avatarUrlPreview() {
      return this.userInfoForm.avatar_url || '';
    },
    // 验证新密码是否匹配
    isPasswordMatch() {
      return this.passwordForm.new_password === this.passwordForm.confirm_password
    },
    // 个人信息表单是否有效
    isFormValid() {
      return !this.nameError && 
             !this.studentIdError && 
             !this.majorError && 
             !this.gradeError && 
             !this.phoneError && 
             !this.emailError && 
             !this.genderError &&
             this.userInfoForm.name &&
             this.userInfoForm.student_id &&
             this.userInfoForm.major &&
             this.userInfoForm.grade &&
             this.userInfoForm.phone &&
             this.userInfoForm.email
    },
    // 密码表单是否有效
    isPasswordFormValid() {
      return !this.oldPasswordError && 
             !this.newPasswordError && 
             !this.confirmPasswordError &&
             this.passwordForm.old_password &&
             this.passwordForm.new_password &&
             this.passwordForm.confirm_password &&
             this.isPasswordMatch
    }
  },
  mounted() {
    this.loadUserInfo()
  },
  methods: {
    // 加载用户信息 - 调用API
    async loadUserInfo() {
      try {
        this.isLoading = true;
        const response = await profileApi.getProfile();
        
        // 根据API响应格式，response已经是data部分
        this.userInfo = response;
        
        // 填充表单数据
        this.userInfoForm = {
          student_id: response.studentId || '',
          name: response.name || '',
          email: response.email || '',
          phone: response.phone || '',
          major: response.major || '',
          grade: response.grade || '',
          gender: response.gender || '',
          avatar: response.avatar || ''
        };
        console.log('User info:', response);
          const token = localStorage.getItem('studentToken');
  console.log('Token:', token);
        this.successMessage = '';
        this.errorMessage = '';
      } catch (err) {
        console.error('Failed to load user info:', err);
        this.errorMessage = '加载用户信息失败: ' + (err.message || '请检查网络连接');
      } finally {
        this.isLoading = false;
      }
    },
    // 验证姓名
    validateName() {
      if (!this.userInfoForm.name.trim()) {
        this.nameError = '姓名不能为空'
      } else if (this.userInfoForm.name.length < 2) {
        this.nameError = '姓名至少2个字符'
      } else {
        this.nameError = ''
      }
    },
    // 验证学号
    validateStudentId() {
      const studentId = this.userInfoForm.student_id.trim()
      if (!studentId) {
        this.studentIdError = '学号不能为空'
      } else {
        this.studentIdError = ''
      }
    },
    // 验证专业
    validateMajor() {
      if (!this.userInfoForm.major.trim()) {
        this.majorError = '专业不能为空'
      } else {
        this.majorError = ''
      }
    },
    // 验证年级
    validateGrade() {
      if (!this.userInfoForm.grade) {
        this.gradeError = '年级不能为空'
      } else {
        this.gradeError = ''
      }
    },
    // 验证电话号码
    validatePhone() {
      const phone = this.userInfoForm.phone.trim()
      if (!phone) {
        this.phoneError = '联系电话不能为空'
      } else if (!/^1[3-9]\d{9}$/.test(phone)) {
        this.phoneError = '请输入有效的11位手机号码'
      } else {
        this.phoneError = ''
      }
    },
    // 验证邮箱
    validateEmail() {
      const email = this.userInfoForm.email.trim()
      if (!email) {
        this.emailError = '邮箱不能为空'
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        this.emailError = '请输入有效的邮箱地址'
      } else {
        this.emailError = ''
      }
    },
    // 验证性别
    validateGender() {
      if (!this.userInfoForm.gender) {
        this.genderError = '请选择性别'
      } else {
        this.genderError = ''
      }
    },
    // 验证旧密码
    validateOldPassword() {
      if (!this.passwordForm.old_password) {
        this.oldPasswordError = '请输入旧密码'
      } else {
        this.oldPasswordError = ''
      }
    },
    // 验证新密码
    validateNewPassword() {
      if (!this.passwordForm.new_password) {
        this.newPasswordError = '请输入新密码'
      } else if (this.passwordForm.new_password.length < 6) {
        this.newPasswordError = '密码长度不能少于6位'
      } else {
        this.newPasswordError = ''
      }
    },
    // 验证确认密码
    validateConfirmPassword() {
      if (!this.passwordForm.confirm_password) {
        this.confirmPasswordError = '请确认新密码'
      } else if (!this.isPasswordMatch) {
        this.confirmPasswordError = '两次输入的密码不匹配'
      } else {
        this.confirmPasswordError = ''
      }
    },
    // 验证整个个人信息表单
    validateUserInfoForm() {
      this.validateName()
      this.validateStudentId()
      this.validateMajor()
      this.validateGrade()
      this.validatePhone()
      this.validateEmail()
      this.validateGender()
      
      return this.isFormValid
    },
    // 验证整个密码表单
    validatePasswordForm() {
      this.validateOldPassword()
      this.validateNewPassword()
      this.validateConfirmPassword()
      
      return this.isPasswordFormValid
    },
    // 保存个人信息 - 调用API
    async saveUserInfo() {
      if (!this.validateUserInfoForm()) {
        this.errorMessage = '请正确填写所有必填字段'
        return
      }

      try {
        this.isLoading = true;
        
        // 根据API文档，更新个人信息只需要部分字段
        const updateData = {
          email: this.userInfoForm.email,
          phone: this.userInfoForm.phone

        };
        
        // 调用更新用户信息的API
        await profileApi.updateProfile(updateData);
        
        // this.successMessage = '个人信息更新成功';
        this.errorMessage = '';
        
        // 重新加载用户信息以获取最新数据
        await this.loadUserInfo();
        
        this.successMessage = '个人信息更新成功';
        
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      } catch (err) {
        this.errorMessage = '更新个人信息失败: ' + (err.message || '请稍后重试');
        this.successMessage = '';
        console.error('Failed to update user info:', err);
      } finally {
        this.isLoading = false;
      }
    },
    // 修改密码 - 调用API
    async changePassword() {
      if (!this.validatePasswordForm()) {
        this.passwordError = '请正确填写所有密码字段';
        return;
      }

      try {
        this.isLoading = true;
        
        // 准备密码修改数据
        const passwordData = {
          old_password: this.passwordForm.old_password,
          new_password: this.passwordForm.new_password
        };
        
        // 调用修改密码的API
        await profileApi.changePassword(passwordData);
        
        this.successMessage = '密码修改成功';
        this.errorMessage = '';
        this.passwordError = '';
        
        // 清空密码表单
        this.passwordForm.old_password = '';
        this.passwordForm.new_password = '';
        this.passwordForm.confirm_password = '';
        
        // 清空错误信息
        this.oldPasswordError = '';
        this.newPasswordError = '';
        this.confirmPasswordError = '';
        
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      } catch (err) {
        this.errorMessage = '修改密码失败: ' + (err.message || '请稍后重试');
        this.successMessage = '';
        this.passwordError = '密码修改失败';
        console.error('Failed to change password:', err);
      } finally {
        this.isLoading = false;
      }
    },
    // 导航处理
    handleNavigate(routeName) {
      this.$router.push({ name: routeName });
    },
    // 退出登录
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

.user-profile-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 主内容区域 */
.main-content {
  padding: 30px;
  max-width: 1000px;
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
  font-weight: 700;
}

.header-content p {
  color: #666;
  font-size: 16px;
}

/* 消息提示 */
.success-message {
  background: linear-gradient(135deg, #27ae60, #219653);
  color: white;
  padding: 15px 20px;
  border-radius: 10px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
  box-shadow: 0 3px 10px rgba(39, 174, 96, 0.2);
}

.error-message {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: white;
  padding: 15px 20px;
  border-radius: 10px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
  box-shadow: 0 3px 10px rgba(231, 76, 60, 0.2);
}

/* 标签页 */
.tabs-container {
  display: flex;
  gap: 8px;
  margin-bottom: 30px;
  border-bottom: 2px solid #e0e0e0;
  padding-bottom: 5px;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 30px;
  border: none;
  background-color: transparent;
  color: #666;
  font-size: 1.1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 3px solid transparent;
  position: relative;
  top: 2px;
}

.tab-btn:hover {
  color: #1e3c72;
}

.tab-btn.active {
  color: #1e3c72;
  border-bottom-color: #1e3c72;
}

/* 表单容器 */
.form-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  padding: 30px;
  transition: all 0.3s ease;
}

.form-container:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

/* 表单部分 */
.form-section {
  margin-bottom: 30px;
}

.section-title {
  color: #1e3c72;
  font-size: 1.4rem;
  font-weight: 600;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f7ff;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 表单行 */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

/* 表单组 */
.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  color: #333;
  font-size: 0.95rem;
  font-weight: 600;
  margin-bottom: 8px;
}

.form-input,
.form-textarea,
.form-select {
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  color: #333;
  transition: all 0.3s ease;
  background: white;
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: #1e3c72;
  box-shadow: 0 0 0 3px rgba(30, 60, 114, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
  font-family: inherit;
}

.form-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='m6 8 4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 12px center;
  background-repeat: no-repeat;
  background-size: 16px;
  padding-right: 40px;
}

/* 输入错误样式 */
.input-error {
  border-color: #e74c3c !important;
  box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.1) !important;
}

.field-error {
  color: #e74c3c;
  font-size: 0.85rem;
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 5px;
  font-weight: 500;
}

/* 字符计数 */
.char-count {
  text-align: right;
  font-size: 0.85rem;
  color: #666;
  margin-top: 4px;
}

/* 密码错误提示 */
.form-error {
  color: #e74c3c;
  font-size: 0.95rem;
  margin-top: 12px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 500;
  padding: 10px;
  background: rgba(231, 76, 60, 0.05);
  border-radius: 6px;
}

/* 表单操作 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 40px;
}

.submit-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 30px;
  border: none;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 3px 10px rgba(30, 60, 114, 0.2);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(30, 60, 114, 0.3);
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 加载动画 */
.loading-spinner {
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    padding: 20px;
    max-width: 800px;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 15px;
  }
  
  .form-container {
    padding: 20px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .form-actions {
    justify-content: center;
  }
  
  .tab-btn {
    padding: 10px 20px;
    font-size: 1rem;
  }
  
  .header-content h1 {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .header-content h1 {
    font-size: 24px;
  }
  
  .submit-btn {
    padding: 12px 24px;
    font-size: 1rem;
    width: 100%;
    justify-content: center;
  }
  
  .tabs-container {
    flex-direction: column;
    border-bottom: none;
    gap: 5px;
  }
  
  .tab-btn {
    border-bottom: none;
    border-left: 3px solid transparent;
    text-align: left;
    justify-content: flex-start;
  }
  
  .tab-btn.active {
    border-bottom-color: transparent;
    border-left-color: #1e3c72;
    background-color: rgba(30, 60, 114, 0.05);
  }
  
  .section-title {
    font-size: 1.2rem;
  }
}
</style>
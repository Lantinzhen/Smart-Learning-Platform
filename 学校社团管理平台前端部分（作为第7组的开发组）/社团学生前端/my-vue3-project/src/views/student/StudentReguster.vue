<template>
  <div class="register-container">
    <div class="register-wrapper">
      <!-- 左侧渐变面板 -->
      <div class="register-gradient-panel">
        <div class="register-gradient-content">
          <h1 class="gradient-text">社团管理系统</h1>
          <p class="gradient-subtitle">创建账号，开启你的社团之旅</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon size="24"><User /></el-icon>
              <span>完善个人信息</span>
            </div>
            <div class="feature-item">
              <el-icon size="24"><Grid /></el-icon>
              <span>探索各类社团</span>
            </div>
            <div class="feature-item">
              <el-icon size="24"><Calendar /></el-icon>
              <span>参与精彩活动</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧注册卡片 -->
      <el-card class="register-card">
        <div class="register-header">
          <h2>学生用户注册</h2>
          <p>创建新账号，参与校园社团活动</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
        >
          <div class="form-row">
            <el-form-item label="姓名" prop="name">
              <el-input
                v-model="registerForm.name"
                placeholder="请输入姓名"
                :prefix-icon="User"
                size="large"
              />
            </el-form-item>
            <el-form-item label="学号" prop="studentId">
              <el-input
                v-model="registerForm.studentId"
                placeholder="请输入学号"
                :prefix-icon="Document"
                size="large"
                maxlength="12"
                show-word-limit
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="专业" prop="major">
              <el-input
                v-model="registerForm.major"
                placeholder="请输入专业"
                :prefix-icon="School"
                size="large"
              />
            </el-form-item>
            <el-form-item label="年级" prop="grade">
              <el-select
                v-model="registerForm.grade"
                placeholder="请选择年级"
                size="large"
                style="width: 100%;"
              >
                <el-option label="大一" value="大一" />
                <el-option label="大二" value="大二" />
                <el-option label="大三" value="大三" />
                <el-option label="大四" value="大四" />
                <el-option label="研究生" value="研究生" />
              </el-select>
            </el-form-item>
          </div>

          <div class="form-row">
            <!-- 联系电话标签垂直排列 -->
            <el-form-item prop="phone">
              <template #label>
                <div class="vertical-label">
                  <span class="label-line">联系</span>
                  <span class="label-line">电话</span>
                </div>
              </template>
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入联系电话"
                :prefix-icon="Phone"
                size="large"
                maxlength="11"
                show-word-limit
              />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                :prefix-icon="Message"
                size="large"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="性别" prop="gender">
              <el-select
                v-model="registerForm.gender"
                placeholder="请选择性别"
                size="large"
                style="width: 100%;"
              >
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
            <!-- 入学年份标签垂直排列 -->
            <el-form-item prop="enrollmentYear">
              <template #label>
                <div class="vertical-label">
                  <span class="label-line">入学</span>
                  <span class="label-line">年份</span>
                </div>
              </template>
              <el-input
                v-model="registerForm.enrollmentYear"
                placeholder="请输入入学年份"
                :prefix-icon="Calendar"
                size="large"
                maxlength="4"
                show-word-limit
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                :show-password="true"
                size="large"
                maxlength="16"
                show-word-limit
              />
            </el-form-item>
            <!-- 确认密码标签垂直排列 -->
            <el-form-item prop="confirmPassword">
              <template #label>
                <div class="vertical-label">
                  <span class="label-line">确认</span>
                  <span class="label-line">密码</span>
                </div>
              </template>
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                :prefix-icon="Lock"
                :show-password="true"
                size="large"
                maxlength="16"
                show-word-limit
              />
            </el-form-item>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              @click="handleRegister"
              :loading="registerLoading"
              size="large"
              class="register-btn"
            >
              <el-icon v-if="!registerLoading"><CircleCheck /></el-icon>
              <span>{{ registerLoading ? '注册中...' : '立即注册' }}</span>
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>已有账号？</span>
            <el-link type="primary" :underline="false" @click="goToLogin">
              点击登录
            </el-link>
          </div>
        </el-form>

        <el-button
          type="default"
          @click="goBack"
          size="large"
          class="back-btn"
        >
          <el-icon><ArrowLeft /></el-icon>
          <span>返回登录</span>
        </el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElCard, ElForm, ElFormItem, ElInput, ElButton, ElSelect, ElOption, ElMessage, ElLink } from 'element-plus'
import { User, Lock, CircleCheck, ArrowLeft, Document, School, Phone, Message, Grid, Calendar } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/studentApi'

const router = useRouter()
const registerFormRef = ref(null)
const registerLoading = ref(false)

// 注册表单数据
const registerForm = reactive({
  name: '',
  studentId: '',
  major: '',
  grade: '',
  phone: '',
  email: '',
  gender: '',
  enrollmentYear: '',
  password: '',
  confirmPassword: ''
})

// 表单校验规则
const registerRules = reactive({
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, message: '姓名至少2个字符', trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{7,15}$/, message: '学号为7-15位数字', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  grade: [
    { required: true, message: '请选择年级', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的11位手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  enrollmentYear: [
    { required: true, message: '请输入入学年份', trigger: 'blur' },
    { pattern: /^\d{4}$/, message: '请输入4位数字的年份', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,16}$/, message: '密码需6-16位，包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
})

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    registerLoading.value = true
    
    // 调用注册API
    const response = await authApi.register({
      student_id: registerForm.studentId,
      name: registerForm.name,
      password: registerForm.password,
      email: registerForm.email,
      phone: registerForm.phone,
      major: registerForm.major,
      grade: registerForm.grade,
      enrollment_year: parseInt(registerForm.enrollmentYear),
      gender: registerForm.gender
    })
         console.log('注册返回数据:', response)
    

      ElMessage.success('注册成功！')
      router.push('/student/login')

  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error(error.message || '注册失败，请稍后重试')
  } finally {
    registerLoading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/student/login')
}

// 返回登录页面
const goBack = () => {
  router.push('/student/login')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  padding: 20px;
  overflow: hidden;
}

.register-wrapper {
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-width: 900px;
  height: 680px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  overflow: hidden;
}

.register-gradient-panel {
  flex: 1;
  min-width: 400px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 40px;
  color: white;
}

.register-gradient-content {
  text-align: center;
  max-width: 400px;
  width: 100%;
}

.gradient-text {
  font-size: 2.8rem;
  margin-bottom: 25px;
  font-weight: 700;
}

.gradient-subtitle {
  font-size: 1.3rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 50px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
  align-items: center;
}

.feature-item {
  display: flex;
  align-items: center;
  font-size: 1.1rem;
  font-weight: 500;
  gap: 15px;
}

.register-card {
  flex: 1.5;
  min-width: 550px;
  border: none;
  border-radius: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 50px 60px;
  max-width: 650px;
}

.register-header {
  text-align: center;
  margin-bottom: 35px;
}

.register-header h2 {
  color: #1f2937;
  margin: 0 0 12px 0;
  font-size: 2rem;
  font-weight: 600;
}

.register-header p {
  color: #6b7280;
  margin: 0;
  font-size: 1.05rem;
}

.register-form {
  margin-top: 25px;
}

:deep(.el-form-item__label) {
  color: #374151;
  font-weight: 500;
  margin-bottom: 10px;
  font-size: 0.95rem;
}

/* 垂直排列的标签样式 */
.vertical-label {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  line-height: 1.2;
  min-height: 40px; /* 确保标签有足够的高度 */
}

.label-line {
  display: block;
  text-align: left;
}

/* 针对垂直标签的特殊样式 */
:deep(.el-form-item[prop="phone"] .el-form-item__label),
:deep(.el-form-item[prop="enrollmentYear"] .el-form-item__label),
:deep(.el-form-item[prop="confirmPassword"] .el-form-item__label) {
  height: auto;
  line-height: normal;
  padding-top: 4px;
  padding-bottom: 4px;
  display: flex;
  align-items: center;
}

/* 确保所有垂直标签的样式一致 */
:deep(.vertical-label) {
  font-weight: 500;
  color: #374151;
}

:deep(.el-input) {
  border-radius: 10px;
  font-size: 1rem;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  min-height: 48px;
  padding: 0 16px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #409eff, 0 2px 6px rgba(0, 0, 0, 0.12);
}

:deep(.el-input__inner) {
  font-size: 1rem;
  letter-spacing: 0.3px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;
  margin-bottom: 25px;
}

.register-btn {
  width: 100%;
  height: 55px;
  font-size: 1.1rem;
  font-weight: 600;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  color: white;
  margin-top: 10px;
}

.register-btn:hover {
  color: white;
  opacity: 0.9;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.35);
}

.form-footer {
  text-align: center;
  margin-top: 25px;
  font-size: 0.95rem;
}

.form-footer span {
  color: #6b7280;
}

.back-btn {
  width: 100%;
  margin-top: 25px;
  border-color: #e5e7eb;
  color: #6b7280;
  border-radius: 10px;
  height: 50px;
  font-size: 1rem;
}

.back-btn:hover {
  background-color: #f9fafb;
  border-color: #409eff;
  color: #409eff;
}

/* 响应式设计 */
@media (max-width: 1100px) {
  .register-wrapper {
    max-width: 1000px;
    min-width: 800px;
    height: auto;
  }
  
  .register-gradient-panel {
    min-width: 350px;
    padding: 40px 30px;
  }
  
  .register-card {
    min-width: 450px;
    padding: 40px 50px;
  }
  
  .gradient-text {
    font-size: 2.4rem;
  }
}

@media (max-width: 900px) {
  .register-wrapper {
    flex-direction: column;
    height: auto;
    min-width: auto;
    max-width: 90%;
  }
  
  .register-gradient-panel {
    min-height: 250px;
    min-width: auto;
    padding: 30px;
    width: 100%;
  }
  
  .register-card {
    min-width: auto;
    width: 100%;
    padding: 40px 30px;
    max-width: 100%;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 20px;
    margin-bottom: 20px;
  }
  
  .feature-list {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    gap: 20px;
  }
  
  .feature-item {
    flex: 1;
    min-width: 140px;
    justify-content: center;
    font-size: 0.95rem;
  }
  
  .gradient-text {
    font-size: 2.2rem;
  }
  
  .gradient-subtitle {
    font-size: 1.1rem;
  }
  
  /* 移动端垂直标签样式调整 */
  .vertical-label {
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .register-container {
    padding: 10px;
  }
  
  .register-wrapper {
    max-width: 100%;
    border-radius: 12px;
  }
  
  .register-gradient-panel {
    padding: 25px 20px;
  }
  
  .register-card {
    padding: 30px 25px;
  }
  
  .feature-item {
    min-width: 120px;
    font-size: 0.9rem;
  }
  
  .gradient-text {
    font-size: 1.8rem;
  }
  
  :deep(.el-input__wrapper) {
    min-height: 44px;
  }
  
  .form-row {
    gap: 15px;
    margin-bottom: 15px;
  }
  
  /* 移动端垂直标签字体大小调整 */
  .vertical-label {
    font-size: 0.9rem;
  }
}
</style>
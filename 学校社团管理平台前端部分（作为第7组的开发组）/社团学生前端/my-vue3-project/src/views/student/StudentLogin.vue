<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧渐变面板 -->
      <div class="login-gradient-panel">
        <div class="login-gradient-content">
          <h1 class="gradient-text">社团管理系统</h1>
          <p class="gradient-subtitle">连接志同道合的同学，参与精彩社团活动</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon size="24"><Star /></el-icon>
              <span>发现感兴趣的社团</span>
            </div>
            <div class="feature-item">
              <el-icon size="24"><Calendar /></el-icon>
              <span>参与丰富的活动</span>
            </div>
            <div class="feature-item">
              <el-icon size="24"><User /></el-icon>
              <span>展示个人才华</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录卡片 -->
      <el-card class="login-card">
        <div class="login-header">
          <h2>学生用户登录</h2>
          <p>参与社团活动和申请加入社团</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
        >
          <el-form-item label="学号" prop="studentId">
            <el-input
              v-model="loginForm.studentId"
              placeholder="请输入学号"
              :prefix-icon="User"
              size="large"
              maxlength="12"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              :show-password="true"
              size="large"
              maxlength="16"
              show-word-limit
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="loginForm.rememberMe" size="large">记住我</el-checkbox>
            <el-link type="primary" :underline="false" @click="handleForgotPwd">忘记密码？</el-link>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              @click="handleLogin"
              :loading="loginLoading"
              size="large"
              class="login-btn"
            >
              <el-icon v-if="!loginLoading"><CircleCheck /></el-icon>
              <span>{{ loginLoading ? '登录中...' : '立即登录' }}</span>
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>还没有账号？</span>
            <el-link type="primary" :underline="false" @click="$router.push('/student/register')">
              点击注册
            </el-link>
          </div>
        </el-form>

        <el-button
          type="default"
          @click="$router.push('/')"
          size="large"
          class="back-btn"
        >
          <el-icon><ArrowLeft /></el-icon>
          <span>返回主页面</span>
        </el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElCard, ElForm, ElFormItem, ElInput, ElButton, ElCheckbox, ElMessage, ElMessageBox, ElLink } from 'element-plus'
import { User, Lock, CircleCheck, ArrowLeft, Star, Calendar } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/studentApi'

const router = useRouter()
const loginFormRef = ref(null)
const loginLoading = ref(false)

// 登录表单数据
const loginForm = reactive({
  studentId: localStorage.getItem('rememberedStudentId') || '',
  password: '',
  rememberMe: localStorage.getItem('rememberedStudentId') ? true : false
})

// 表单校验规则
const loginRules = reactive({
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { min: 3, message: '学号为10-15位数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码需6-16位，包含字母和数字', trigger: 'blur' }
  ]
})

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loginLoading.value = true
    
    // 调用登录API
    const response = await authApi.login({
      student_id: loginForm.studentId,
      password: loginForm.password
    })
    
    // 存储登录状态
    if (response.token) {
      localStorage.setItem('studentToken', response.token)
    }
    
    // 记住账号
    if (loginForm.rememberMe) {
      localStorage.setItem('rememberedStudentId', loginForm.studentId)
    } else {
      localStorage.removeItem('rememberedStudentId')
    }
    
    ElMessage.success('登录成功！')
    router.push('/student/dashboard')
        // router.push('/student/clubsquare')
        // router.push('/student/activitycenter')
        // router.push('/student/myactivity')
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '学号或密码错误')
  } finally {
    loginLoading.value = false
  }
}

// 忘记密码处理
const handleForgotPwd = () => {
  ElMessageBox.prompt('请输入您的学号和绑定邮箱', '密码找回', {
    confirmButtonText: '发送验证码',
    cancelButtonText: '取消',
    inputFields: [
      {
        label: '学号',
        key: 'studentId',
        type: 'text',
        placeholder: '请输入学号'
      },
      {
        label: '邮箱',
        key: 'email',
        type: 'email',
        placeholder: '请输入绑定邮箱'
      }
    ]
  }).then(async ({ value }) => {
    try {
      // 调用API发送验证码
      await authApi.sendResetCode({
        studentId: value.studentId,
        email: value.email
      })
      ElMessage.success('验证码已发送至您的邮箱，请查收')
      // 后续验证码验证与密码重置逻辑省略
    } catch (error) {
      console.error('发送验证码失败:', error)
      ElMessage.error(error.message || '发送失败，请检查学号和邮箱是否匹配')
    }
  }).catch(() => {
    // 取消操作
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  padding: 20px;
  overflow: hidden;
}

.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 900px;
  height: 550px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  overflow: hidden;
}

.login-gradient-panel {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
}

.login-gradient-content {
  text-align: center;
}

.gradient-text {
  font-size: 2.5rem;
  margin-bottom: 20px;
  font-weight: 700;
}

.gradient-subtitle {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 40px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.feature-item {
  display: flex;
  align-items: center;
  font-size: 1rem;
  font-weight: 500;
  gap: 12px;
}

.login-card {
  flex: 1;
  border: none;
  border-radius: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #1f2937;
  margin: 0 0 10px 0;
  font-size: 1.8rem;
  font-weight: 600;
}

.login-header p {
  color: #6b7280;
  margin: 0;
  font-size: 0.95rem;
}

.login-form {
  margin-top: 20px;
}

:deep(.el-form-item__label) {
  color: #374151;
  font-weight: 500;
  margin-bottom: 8px;
}

:deep(.el-input) {
  border-radius: 8px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff, 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

:deep(.el-checkbox) {
  color: #6b7280;
}

:deep(.el-link) {
  font-size: 0.9rem;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 1rem;
  font-weight: 600;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.login-btn:hover {
  color: white;
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 0.9rem;
}

.form-footer span {
  color: #6b7280;
}

.back-btn {
  width: 100%;
  margin-top: 20px;
  border-color: #e5e7eb;
  color: #6b7280;
  border-radius: 8px;
}

.back-btn:hover {
  background-color: #f9fafb;
  border-color: #409eff;
  color: #409eff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    height: auto;
  }
  
  .login-gradient-panel {
    padding: 30px;
    min-height: 250px;
  }
  
  .login-card {
    padding: 30px;
  }
  
  .feature-list {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    gap: 15px;
  }
  
  .feature-item {
    flex: 1;
    min-width: 120px;
    justify-content: center;
    font-size: 0.9rem;
  }
  
  .gradient-text {
    font-size: 2rem;
  }
  
  .gradient-subtitle {
    font-size: 1rem;
  }
}
</style>
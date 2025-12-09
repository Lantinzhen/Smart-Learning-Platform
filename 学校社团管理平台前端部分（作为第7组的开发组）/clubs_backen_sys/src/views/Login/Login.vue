<template>
  <div class="login-container">
    <div class="login-form">
      <!-- 顶部 -->
      <div class="logo">
        <el-avatar :size="60" />
        <h2>社团管理员登录</h2>
        <p class="subtitle">管理员的社团管理系统</p>
      </div>
      <!-- 表单 -->
      <el-form ref="loginFormRef"
               :model="loginForm"
               :rules="loginRules"
               label-width="80px">
        <!-- 用户名 -->
        <el-form-item label="用户名"
                      prop="username"
                      class="login-input-item">
          <el-input v-model="loginForm.username"
                    placeholder="请输入用户名"
                    prefix-icon="el-icon-user"
                    class="form-input"
                    clearable />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item label="密码"
                      prop="password"
                      class="login-input-item">
          <el-input v-model="loginForm.password"
                    type="password"
                    placeholder="请输入密码"
                    prefix-icon="el-icon-lock"
                    class="form-input"
                    clearable
                    show-password />
        </el-form-item>
        <!-- 登录按钮 -->
        <el-form-item>
          <el-button type="primary"
                     class="login-btn"
                     @click="handleLogin"
                     :loading="loading">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 返回总主页 -->
      <div class="login-footer">
        <el-button type="text"
                   @click="goToHome">返回主页面</el-button>
      </div>
    </div>

    <!-- 美化动画 -->
    <div class="animate-class">
      <ul class="square">
        <li v-for="i in 5"
            :key="i"></li>
      </ul>
      <ul class="circle">
        <li v-for="i in 5"
            :key="i"></li>
      </ul>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import type { FormInstance, FormRules } from "element-plus";
import { useUserStore } from "../../stores/user";

const userStore = useUserStore();

const router = useRouter();
const loginFormRef = ref<FormInstance | null>(null);
const loading = ref(false);

// 登录表单数据
const loginForm = ref({
  username: "club_admin_b",
  password: "12345678",
});

// 登录规则
const loginRules = ref<FormRules>({
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 3,
      max: 20,
      message: "用户名长度在 3 到 20 个字符",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于 6 个字符", trigger: "blur" },
  ],
});

const handleLogin = async () => {
  // 登录表单验证 是否成功绑定 是否为 null 或 undefined
  if (!loginFormRef.value) return;

  try {
    // 验证表单
    loginFormRef.value.validate();
    // 加载状态
    loading.value = true;

    const res = await userStore.login(loginForm.value);
    // 登录
    if (res) {
      // 提示信息
      ElMessage.success("登录成功");
      await router.push("/dashboard");
    }
  } catch (error) {
    ElMessage.error("登录失败");
  } finally {
    loading.value = false;
  }
};

const goToHome = () => {
  // router.push("/");
  // ElMessage.success("已返回");
  // ElMessage.error("已返回");
  ElMessage.info("返回功能有待实现");
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  background-image: linear-gradient(
    135deg,
    #4ade80 0%,
    #37cb0a 60%,
    #facc15 100%
  );
  position: relative;
  overflow: hidden;
  animation: gradientBG 15s ease infinite;
  background-size: 400% 400%;
}

@keyframes gradientBG {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.login-form {
  width: 420px;
  padding: 48px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.2);
}
.logo {
  text-align: center;
  margin-bottom: 40px;
}

.logo .el-avatar {
  background: linear-gradient(135deg, #4ade80 0%, #facc15 100%) !important;
  transition: all 0.3s ease;
}

.logo .el-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 16px rgba(24, 144, 255, 0.3);
}

.logo h2 {
  margin: 20px 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
  background: linear-gradient(135deg, #4ade80, #facc15);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
  opacity: 0.8;
}

/* 表单样式优化 */
:deep(.el-form) {
  width: 100%;
}

/* 登录输入框项 */
.login-input-item {
  margin-bottom: 28px;
  position: relative;
}

/* 输入框样式优化 */
:deep(.form-input) {
  width: 100%;
  height: 44px;
  transition: all 0.3s;
}

/* 优化输入框外观 */
:deep(.el-input__wrapper) {
  /* 移除输入框的阴影 */
  box-shadow: none !important;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
  overflow: visible;
}
/* 优化输入框悬停样式 */
:deep(.el-input__wrapper:hover) {
  border-color: #4ade80;
  box-shadow: 0 0 0 2px rgba(74, 222, 128, 0.1);
}

/* 优化输入框聚焦样式 */
:deep(.el-input__wrapper.is-focus) {
  border-color: #4ade80;
  box-shadow: 0 0 0 4px rgba(74, 222, 128, 0.2);
}
/* 优化输入框内文本样式 */
:deep(.el-input__inner) {
  padding: 12px 0;
  font-size: 14px;
  height: 40px;
  border: none;
  box-shadow: none;
  background: transparent;
}

/* 表单标签样式 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
  font-size: 14px;
  padding: 0 12px 0 0;
}
/* 优化表单标签悬停样式 */
:deep(.el-form-item__label:hover) {
  color: #1890ff;
}

/* 登录按钮样式优化 */

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #4ade80 0%, #facc15 100%);
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(74, 222, 128, 0.3);
  position: relative;
  overflow: hidden;
}
/* 滑块 样式优化 */
.login-btn::before {
  content: "";
  position: absolute;
  top: 0;
  /* 滑块移动 */
  left: -20%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: all 0.5s;
}

/* 登录按钮 悬停样式优化 */
.login-btn:hover {
  background: linear-gradient(135deg, #34d399 0%, #eab308 100%);
  border-color: #34d399;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(74, 222, 128, 0.4);
}

/* 登录按钮 悬浮 滑块移动 */
.login-btn:hover::before {
  left: 100%;
}

/* 登录激活状态 */
.login-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(24, 144, 255, 0.3);
}

/* 返回主页按钮 */
.login-footer {
  text-align: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

/* 优化返回总按钮 */
:deep(.login-footer .el-button) {
  font-size: 14px;
  color: #606266;
  background: transparent;
  border: 1px solid transparent;
  padding: 6px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
}
/* 优化返回总按钮 悬停样式 */
:deep(.login-footer .el-button:hover) {
  color: #1890ff;
  background: rgba(24, 144, 255, 0.05);
  border-color: rgba(24, 144, 255, 0.2);
}

.animate-class li {
  position: absolute;
  border: 1px solid #fff;
  background-color: #fff;
  width: 30px;
  height: 30px;
  list-style: none;
  opacity: 0;
}

.square li {
  top: 40vh;
  left: 80vw;
  /* 执行动画 动画名 时长 线性的 无限次数 */
  animation: square 5s linear infinite;
}

.square li:nth-child(2) {
  top: 80vh;
  left: 10vw;
  animation-delay: 2s;
}
.square li:nth-child(3) {
  top: 80vh;
  left: 85vw;
  animation-delay: 4s;
}

.square li:nth-child(4) {
  top: 10vh;
  left: 70vw;
  animation-delay: 6s;
}
.square li:nth-child(5) {
  top: 20vh;
  left: 10vw;
  animation-delay: 8s;
}

.circle li {
  bottom: 0;
  left: 15vw;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: circle 5s linear infinite;
}
.circle li:nth-child(2) {
  left: 25vw;
  animation-delay: 2s;
}
.circle li:nth-child(3) {
  left: 55vw;
  animation-delay: 4s;
}
.circle li:nth-child(4) {
  left: 75vw;
  animation-delay: 6s;
}
.circle li:nth-child(5) {
  left: 90vw;
  animation-delay: 8s;
}

@keyframes square {
  0% {
    transform: scale(0) rotateY(0deg);
    opacity: 1;
  }
  100% {
    transform: scale(5) rotateY(1000deg);
    opacity: 0;
  }
}
@keyframes circle {
  0% {
    transform: scale(0) rotateY(0deg);
    opacity: 1;
    bottom: 0;
    border-radius: 0;
  }
  100% {
    transform: scale(5) rotateY(1000deg);
    opacity: 0;
    bottom: 90vh;
    border-radius: 50%;
  }
}
</style>

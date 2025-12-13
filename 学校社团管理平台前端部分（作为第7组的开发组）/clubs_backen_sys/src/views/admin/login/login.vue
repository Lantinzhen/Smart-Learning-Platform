<template>
    <div class="login-container">
        <div class="login-form">
            <!-- 顶部 -->
            <div class="logo">
                <el-avatar :size="60" />
                <h2>学校管理员登录</h2>
                <p class="subtitle">学校管理员的社团管理系统</p>
            </div>
            <!-- 表单 -->
            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="80px">
                <!-- 用户名 -->
                <el-form-item label="用户名" prop="username" class="login-input-item">
                    <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="el-icon-user"
                        class="form-input" clearable />
                </el-form-item>

                <!-- 密码 -->
                <el-form-item label="密码" prop="password" class="login-input-item">
                    <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"
                        prefix-icon="el-icon-lock" class="form-input" clearable show-password />
                </el-form-item>
                <!-- 登录按钮 -->
                <el-form-item>
                    <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">
                        登录
                    </el-button>
                </el-form-item>
            </el-form>

            <!-- 返回总主页 -->
            <div class="login-footer">
                <el-button type="text" @click="goToHome">返回主页面</el-button>
            </div>
        </div>

        <!-- 美化动画 -->
        <div class="animate-class">
            <ul class="square">
                <li v-for="i in 5" :key="i"></li>
            </ul>
            <ul class="circle">
                <li v-for="i in 5" :key="i"></li>
            </ul>
        </div>

    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import type { FormInstance, FormRules } from "element-plus";
import { useAdminStore } from "@/stores/admin/user";

const adminStore = useAdminStore();

const router = useRouter();
const loginFormRef = ref<FormInstance | null>(null);
const loading = ref(false);

// 登录表单数据
const loginForm = ref({
    username: "school_admin_a",
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

        const res = await adminStore.login(loginForm.value);
        // 登录
        if (res) {
            // 提示信息
            ElMessage.success("登录成功");
            await router.push("/admin/layout/dashboard");
        }
    } catch (error) {
        ElMessage.error("登录失败");
        throw error;
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
/* Apple Design System 字体栈 */
.login-container {
    font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.login-container {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
    position: relative;
    overflow: hidden;
}

/* 微妙的背景装饰 */
.login-container::before {
    content: "";
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle at 30% 30%, rgba(0, 122, 255, 0.03) 0%, transparent 50%),
        radial-gradient(circle at 70% 70%, rgba(88, 86, 214, 0.03) 0%, transparent 50%);
    animation: subtleMove 20s ease-in-out infinite;
}

@keyframes subtleMove {

    0%,
    100% {
        transform: translate(0, 0) rotate(0deg);
    }

    50% {
        transform: translate(5%, 5%) rotate(180deg);
    }
}

.login-form {
    width: 420px;
    padding: 56px 48px;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: saturate(180%) blur(20px);
    -webkit-backdrop-filter: saturate(180%) blur(20px);
    border-radius: 24px;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06),
        0 1px 2px rgba(0, 0, 0, 0.04),
        inset 0 1px 0 rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(255, 255, 255, 0.18);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    z-index: 1;
}

.login-form:hover {
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08),
        0 2px 4px rgba(0, 0, 0, 0.06),
        inset 0 1px 0 rgba(255, 255, 255, 0.9);
    transform: translateY(-2px);
}

.logo {
    text-align: center;
    margin-bottom: 48px;
}

.logo .el-avatar {
    background: linear-gradient(135deg, #007AFF 0%, #5856D6 100%) !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(0, 122, 255, 0.2);
}

.logo .el-avatar:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 16px rgba(0, 122, 255, 0.3);
}

.logo h2 {
    margin: 24px 0 8px 0;
    color: #1d1d1f;
    font-size: 28px;
    font-weight: 600;
    letter-spacing: -0.5px;
    line-height: 1.2;
}

.subtitle {
    color: #86868b;
    font-size: 15px;
    margin: 0;
    font-weight: 400;
    letter-spacing: -0.2px;
}

/* 表单样式 */
:deep(.el-form) {
    width: 100%;
}

.login-input-item {
    margin-bottom: 24px;
    position: relative;
}

:deep(.form-input) {
    width: 100%;
    height: 48px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Apple风格输入框 */
:deep(.el-input__wrapper) {
    box-shadow: none !important;
    border-radius: 12px;
    border: 1px solid #d2d2d7;
    background-color: rgba(255, 255, 255, 0.8);
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    overflow: visible;
}

:deep(.el-input__wrapper:hover) {
    border-color: #a1a1a6;
    background-color: rgba(255, 255, 255, 0.95);
}

:deep(.el-input__wrapper.is-focus) {
    border-color: #007AFF;
    background-color: rgba(255, 255, 255, 1);
    box-shadow: 0 0 0 4px rgba(0, 122, 255, 0.1);
}

:deep(.el-input__inner) {
    padding: 14px 16px;
    font-size: 17px;
    height: 48px;
    border: none;
    box-shadow: none;
    background: transparent;
    color: #1d1d1f;
    font-weight: 400;
}

:deep(.el-input__inner::placeholder) {
    color: #86868b;
    font-weight: 400;
}

/* 表单标签 */
:deep(.el-form-item__label) {
    font-weight: 500;
    color: #1d1d1f;
    font-size: 15px;
    padding: 0 12px 0 0;
    letter-spacing: -0.2px;
}

/* 登录按钮 - Apple风格 */
.login-btn {
    width: 100%;
    height: 50px;
    font-size: 17px;
    font-weight: 500;
    background: #007AFF;
    color: #ffffff;
    border: none;
    border-radius: 12px;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(0, 122, 255, 0.3);
    position: relative;
    overflow: hidden;
    letter-spacing: -0.2px;
}

.login-btn:hover {
    background: #0051d5;
    box-shadow: 0 4px 16px rgba(0, 122, 255, 0.4);
    transform: translateY(-1px);
}

.login-btn:active {
    transform: translateY(0);
    box-shadow: 0 1px 4px rgba(0, 122, 255, 0.3);
    background: #0040aa;
}

.login-btn::after {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    transform: translate(-50%, -50%);
    transition: width 0.6s, height 0.6s;
}

.login-btn:active::after {
    width: 300px;
    height: 300px;
}

/* 返回主页按钮 */
.login-footer {
    text-align: center;
    margin-top: 32px;
    padding-top: 24px;
    border-top: 0.5px solid rgba(0, 0, 0, 0.1);
}

:deep(.login-footer .el-button) {
    font-size: 15px;
    color: #007AFF;
    background: transparent;
    border: none;
    padding: 8px 16px;
    border-radius: 8px;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    font-weight: 400;
    letter-spacing: -0.2px;
}

:deep(.login-footer .el-button:hover) {
    color: #0051d5;
    background: rgba(0, 122, 255, 0.08);
}

:deep(.login-footer .el-button:active) {
    background: rgba(0, 122, 255, 0.12);
}

/* 简化动画装饰 - Apple风格 */
.animate-class {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    overflow: hidden;
}

.animate-class li {
    position: absolute;
    list-style: none;
    opacity: 0;
}

.square li {
    width: 4px;
    height: 4px;
    background: rgba(0, 122, 255, 0.15);
    border-radius: 1px;
    top: 40vh;
    left: 80vw;
    animation: floatSquare 8s ease-in-out infinite;
}

.square li:nth-child(2) {
    top: 80vh;
    left: 10vw;
    animation-delay: 1.5s;
}

.square li:nth-child(3) {
    top: 80vh;
    left: 85vw;
    animation-delay: 3s;
}

.square li:nth-child(4) {
    top: 10vh;
    left: 70vw;
    animation-delay: 4.5s;
}

.square li:nth-child(5) {
    top: 20vh;
    left: 10vw;
    animation-delay: 6s;
}

.circle li {
    width: 6px;
    height: 6px;
    background: rgba(88, 86, 214, 0.12);
    border-radius: 50%;
    bottom: 0;
    left: 15vw;
    animation: floatCircle 10s ease-in-out infinite;
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

@keyframes floatSquare {

    0%,
    100% {
        transform: translateY(0) scale(1);
        opacity: 0;
    }

    10% {
        opacity: 0.15;
    }

    50% {
        transform: translateY(-60vh) scale(1.5);
        opacity: 0.08;
    }

    90% {
        opacity: 0.15;
    }
}

@keyframes floatCircle {

    0%,
    100% {
        transform: translateY(0) scale(1);
        opacity: 0;
    }

    10% {
        opacity: 0.12;
    }

    50% {
        transform: translateY(-80vh) scale(1.8);
        opacity: 0.05;
    }

    90% {
        opacity: 0.12;
    }
}

/* 响应式设计 */
@media (max-width: 480px) {
    .login-form {
        width: 90%;
        padding: 40px 32px;
        border-radius: 20px;
    }

    .logo h2 {
        font-size: 24px;
    }
}
</style>
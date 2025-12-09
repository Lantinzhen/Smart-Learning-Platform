<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>{{ club_name }} 管理中心</h1>
    </div>
    <!-- 数据卡片统计 -->
    <div class="stats-cards">
      <!-- 单独小卡片 -->
      <DataCard :cards="cards" />
    </div>
    <div class="dashboard-main">
      <el-card class="dashboard-section">
        <div class="section-header">
          <h3>待处理申请</h3>
        </div>
        <div class="pending-applications">
          <div v-if="filterApplications.length === 0"
               class="empty-message">
            暂无待处理申请
          </div>
          <template v-else>
            <div v-for="app in filterApplications"
                 :key="app.applicationId"
                 class="application-item"
                 @click="showApplicationDetail(app.applicationId)">
              <div class="app-info">
                <p class="app-name"> <span>{{ app.name }} </span>申请加入社团</p>
                <p class="app-time">{{ formatIsoTime(app.createdAt) }}</p>
              </div>

            </div>
          </template>

        </div>
      </el-card>

      <el-card class="dashboard-section">
        <div class="section-header">
          <h3>快捷操作</h3>
        </div>
        <div class="quick-actions">
          <div class="action-card"
               @click="navigateTo('activity')">
            <div class="action-icon"
                 style="background: linear-gradient(135deg, #FF6B6B, #FFA500);">
              <el-icon>
                <Suitcase />
              </el-icon>
            </div>
            <div class="action-text">活动管理</div>
          </div>
          <div class="action-card"
               @click="navigateTo('member')">
            <div class="action-icon"
                 style="background: linear-gradient(135deg, #00C9A7, #FFD166);">
              <el-icon>
                <User />
              </el-icon>
            </div>
            <div class="action-text">成员管理</div>
          </div>
          <div class="action-card"
               @click="navigateTo('application')">
            <div class="action-icon"
                 style="background: linear-gradient(135deg, #06D6A0, #118AB2);">
              <el-icon>
                <DocumentAdd />
              </el-icon>
            </div>
            <div class="action-text">申请管理</div>
          </div>
        </div>
      </el-card>

    </div>
    <!-- 申请详情 模态框 -->
    <el-dialog title="申请详情"
               v-model="showApplication"
               width="600px">
      <el-form class="form-show">
        <el-form-item label="姓名">{{ detail?.name }}</el-form-item>
        <el-form-item label="学号">{{ detail?.studentId }}</el-form-item>
        <el-form-item label="年级">{{ detail?.grade }}</el-form-item>
        <el-form-item label="专业">{{ detail?.major }}</el-form-item>
        <el-form-item label="邮件">{{ detail?.email }}</el-form-item>
        <el-form-item label="手机号">{{ detail?.phone }}</el-form-item>
        <el-form-item label="活动兴趣偏好">{{ detail?.activityPreference }}</el-form-item>
        <el-form-item label="闲暇时间">{{ detail?.availableTime }}</el-form-item>
        <el-form-item label="申请人经历">{{ detail?.experience }}</el-form-item>
        <el-form-item label="申请理由">{{ detail?.reason }} </el-form-item>
        <div class="app-actions">
          <el-button type="success"
                     @click="handleApprove(detail?.applicationId)">同意</el-button>
          <el-button type="danger"
                     @click="handleReject(detail?.applicationId)">拒绝</el-button>
        </div>

      </el-form>

    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, onUnmounted } from "vue";
import { useDashboardStore } from "../../stores/dashboard";
import { useApplicationStore } from "../../stores/application";
import { ElMessage } from "element-plus";
import { formatIsoTime } from "../../utils/format";
import { useRouter } from "vue-router";
import DataCard from "../../components/DataCard.vue";
// 引入类型
import type {
  ApplicationItem,
  ApplicationDetail,
} from "../../types/application";

//stores
const dashboardStore = useDashboardStore();
const applicationStore = useApplicationStore();

// 路由
const router = useRouter();

// 社团名称
const club_name = ref("");
// 成员数量
const member_count = ref(0);
// 待审核申请数量
const pendingApplicationCount = ref(0);
// 活动数量
const activitiesCount = ref(0);
// 最近活动
const recentActivity = ref(0);

// 显示申请详情
const showApplication = ref(false);

let detail = ref<ApplicationDetail | null>(null);

// 1. 定义响应式数据（全局）
const pendingApplications = ref<{ list: ApplicationItem[]; total: number }>({
  list: [],
  total: 0,
}); // 所有申请列表
// 2. 定义computed（全局，依赖pendingApplications）
const filterApplications = computed(() => {
  // 判空：确保list存在
  if (!pendingApplications.value?.list) return [];
  // 筛选"待审核"的申请
  return pendingApplications.value.list.filter(
    (item) => item.status === "待审核"
  );
});

// 添加定时器引用
const pollingTimer = ref<number | null>(null);

onMounted(async () => {
  initData();
  fetchPendingList();
  // 设置定时轮询，每30秒更新一次数据
  pollingTimer.value = window.setInterval(() => {
    fetchPendingList();
    initData();
  }, 30000);
});

onUnmounted(() => {
  // 销毁定时器
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value);
  }
});
const initData = async () => {
  const response = await dashboardStore.getOverview();

  const dashboardData = response.data;
  console.log("dashboardData", dashboardData);
  // 社团名称
  club_name.value = dashboardData.clubName;
  localStorage.setItem("club_name", club_name.value);
  // 成员数量
  member_count.value = dashboardData.memberCount;
  // 待审核申请数量
  pendingApplicationCount.value = dashboardData.pendingApplicationsCount;
  // 活动数量
  activitiesCount.value = dashboardData.activitiesCount;
  // 最近活动
  recentActivity.value = dashboardData.recentActivities[0].title;
};

// 3. 异步获取数据（更新pendingApplications，computed会自动重新计算）
const fetchPendingList = async () => {
  try {
    const result = await applicationStore.getApplyList();

    pendingApplications.value = result.data; // 更新响应式数据
    console.log("所有申请列表:", result);
    // 此时computed已经自动计算，直接打印filterApplications.value即可
    console.log("筛选后的申请:", filterApplications.value);
  } catch (error) {
    console.error("获取申请列表失败:", error);
    pendingApplications.value = { list: [], total: 0 }; // 异常时重置
  }
};

// 方法
const navigateTo = (routeName: string) => {
  router.push({ name: routeName });
};

const cards = computed(() => [
  {
    title: "待审核入团申请",
    value: pendingApplicationCount.value,
  },
  {
    title: "社团成员",
    value: member_count.value,
  },
  {
    title: "活动总数",
    value: activitiesCount.value,
  },
  {
    title: "近期活动",
    value: recentActivity.value,
  },
]);

// 同意申请
const handleApprove = async (applicationId: number | undefined) => {
  try {
    await applicationStore.approveApplication(
      applicationId,
      "通过",
      "欢迎加入"
    );
    // 关键：审核成功后刷新申请列表
    await fetchPendingList();
    // 同时刷新统计数据（待审核数量会减少）
    await initData();
    showApplication.value = false;
    ElMessage.success("申请通过成功");
  } catch (error) {
    ElMessage.error("申请通过失败");
  }
};
// 拒绝申请
const handleReject = async (applicationId: number | undefined) => {
  try {
    await applicationStore.approveApplication(
      applicationId,
      "拒绝",
      "不符合条件"
    );

    // 关键：审核成功后刷新申请列表
    await fetchPendingList();
    // 同时刷新统计数据（待审核数量会减少）
    await initData();
    showApplication.value = false;
    ElMessage.success("申请拒绝成功");
  } catch (error) {
    ElMessage.error("申请拒绝失败");
  }
};

// 显示申请详情
const showApplicationDetail = async (applicationId: number) => {
  showApplication.value = true;

  detail.value = await applicationStore.getApplyDetail(applicationId);
  // console.log("申请详情:", detail);
};
</script>
<style scoped>
.dashboard-container {
  padding: 20px 40px;
}

.dashboard-header h1 {
  margin-bottom: 30px;
}

.dashboard-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.dashboard-section {
  height: 100%;
  overflow: auto;
}
.section-header {
  margin-bottom: 20px;
}

.section-header h3 {
  color: #303133;
  margin: 0;
  font-size: 18px;
}

.application-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  border-radius: 4px;
  cursor: pointer;
}
.application-item:hover {
  background-color: #f5f7fa;
}

.empty-message {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.app-name {
  color: #303133;
  margin: 0 0 5px 0;
  font-weight: 500;
}
.app-name span {
  background: linear-gradient(135deg, #ffd166, #ffa500);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 18px;
}
.app-time {
  color: #909399;
  font-size: 12px;
  margin: 0;
}

.app-actions {
  display: flex;
  gap: 10px;
}

.quick-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.action-btn {
  padding: 15px;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  width: 120px;
}

.action-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.action-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin-bottom: 12px;
}

.action-text {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.form-show {
  /* background-color: aqua; */
  padding: 10px 20px;
}
.form-show :deep(.el-form-item__label) {
  font-size: 12px;
  width: 90px;
  font-weight: bold;
  border-right: 1px solid #ebeef5;
}
.form-show :deep(.el-form-item) {
  border-bottom: 1px solid #ebeef5;
}

.app-actions {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 暗色模式 */

[data-theme="dark"] .dashboard-section {
  background-color: #303133;
  border: none;
  box-shadow: 0 2px 12px 0 #ffffff40;
}

[data-theme="dark"] .section-header h3 {
  color: #fff;
  margin: 0;
  font-size: 18px;
}
[data-theme="dark"] .application-item {
  border-bottom: 1px solid #3c3d3e;
}
[data-theme="dark"] .application-item:hover {
  background-color: #282828;
}

[data-theme="dark"] .empty-message {
  text-align: center;
  color: #fff;
  padding: 40px 0;
}

[data-theme="dark"] .app-name {
  color: #fff;
  margin: 0 0 5px 0;
  font-weight: 500;
}

[data-theme="dark"] .app-time {
  color: #fff;
  font-size: 12px;
  margin: 0;
}

[data-theme="dark"] .action-btn {
  box-shadow: 0 2px 12px 0 #ffffff40;
}

[data-theme="dark"] .action-card {
  background: #303133;
  box-shadow: 0 2px 12px 0 #ffffff40;
}

[data-theme="dark"] .action-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 2px 12px 0 #ffffff40;
}

[data-theme="dark"] .action-text {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}
</style>

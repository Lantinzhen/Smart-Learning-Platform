<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>数据分析</h1>
    </div>
    <!-- <div class="dashboard-main">
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
    <div class="action-card" @click="navigateTo('activity')">
      <div class="action-icon" style="background: linear-gradient(135deg, #FF6B6B, #FFA500);">
        <el-icon>
          <Suitcase />
        </el-icon>
      </div>
      <div class="action-text">活动管理</div>
    </div>
    <div class="action-card" @click="navigateTo('member')">
      <div class="action-icon" style="background: linear-gradient(135deg, #00C9A7, #FFD166);">
        <el-icon>
          <User />
        </el-icon>
      </div>
      <div class="action-text">成员管理</div>
    </div>
    <div class="action-card" @click="navigateTo('application')">
      <div class="action-icon" style="background: linear-gradient(135deg, #06D6A0, #118AB2);">
        <el-icon>
          <DocumentAdd />
        </el-icon>
      </div>
      <div class="action-text">申请管理</div>
    </div>
  </div>
</el-card> -->

    <!-- </div> -->
    <!-- 统计数据展示区域 -->
    <div class="statistics-section">
      <el-card class="statistics-card">
        <div class="section-header">
          <h3>社团活跃度统计</h3>
        </div>
        <div class="statistics-content">
          <div v-if="clubActivityList.length === 0" class="empty-message">
            暂无数据
          </div>
          <el-table v-else :data="clubActivityList" style="width: 100%">
            <el-table-column prop="clubName" label="社团名称" width="180" />
            <el-table-column prop="totalActivities" label="活动总数" width="120" />
            <el-table-column prop="totalParticipants" label="参与人数" width="120" />
            <el-table-column prop="avgPoints" label="平均积分" width="120">
              <template #default="scope">
                {{ scope.row.avgPoints?.toFixed(2) || 0 }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>

      <el-card class="statistics-card">
        <div class="section-header">
          <h3>学生活动参与统计</h3>
        </div>
        <div class="statistics-content">
          <div v-if="studentActivityList.length === 0" class="empty-message">
            暂无数据
          </div>
          <el-table v-else :data="studentActivityList" style="width: 100%" max-height="300">
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="studentId" label="学号" width="120" />
            <el-table-column prop="major" label="专业" width="150" />
            <el-table-column prop="grade" label="年级" width="100" />
            <el-table-column prop="totalActivities" label="总活动数" width="100" />
            <el-table-column prop="attendedActivities" label="参与活动数" width="120" />
            <el-table-column prop="totalPoints" label="总积分" width="100">
              <template #default="scope">
                {{ scope.row.totalPoints?.toFixed(1) || 0 }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>

      <el-card class="statistics-card">
        <div class="section-header">
          <h3>活动参与率统计</h3>
        </div>
        <div class="statistics-content">
          <div v-if="activityParticipationList.length === 0" class="empty-message">
            暂无数据
          </div>
          <el-table v-else :data="activityParticipationList" style="width: 100%" max-height="300">
            <el-table-column prop="title" label="活动名称" width="200" />
            <el-table-column prop="clubName" label="社团名称" width="150" />
            <el-table-column prop="maxParticipants" label="最大参与数" width="120" />
            <el-table-column prop="registeredCount" label="已报名数" width="100" />
            <el-table-column prop="participationRate" label="参与率" width="120">
              <template #default="scope">
                {{ scope.row.participationRate?.toFixed(1) || 0 }}%
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </div>
    <!-- 申请详情 模态框 -->
    <el-dialog title="申请详情" v-model="showApplication" width="600px">
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
          <el-button type="success" @click="handleApprove(detail?.applicationId)">同意</el-button>
          <el-button type="danger" @click="handleReject(detail?.applicationId)">拒绝</el-button>
        </div>

      </el-form>

    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, onUnmounted } from "vue";
import { useDashboardStore } from "../../../stores/dashboard";
import { useApplicationStore } from "../../../stores/application";
import { useAdminDashboardStore } from "../../../stores/admin/dashboard";
import { ElMessage } from "element-plus";
import { formatIsoTime } from "../../../utils/format";
import { useRouter } from "vue-router";
import DataCard from "../../../components/DataCard.vue";
// 引入类型
import type {
  ApplicationItem,
  ApplicationDetail,
} from "../../../types/application";

// 统计数据类型定义
interface ClubActivityItem {
  clubId: number;
  clubName: string;
  totalActivities: number;
  totalParticipants: number;
  avgPoints: number;
}

interface StudentActivityItem {
  studentId: string;
  name: string;
  major: string;
  grade: string;
  totalActivities: number;
  attendedActivities: number;
  totalPoints: number;
}

interface ActivityParticipationItem {
  activityId: number;
  title: string;
  clubName: string;
  maxParticipants: number;
  registeredCount: number;
  participationRate: number;
}

//stores
const dashboardStore = useDashboardStore();
const applicationStore = useApplicationStore();
const adminDashboardStore = useAdminDashboardStore();

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

// 统计数据
const clubActivityList = ref<ClubActivityItem[]>([]);
const studentActivityList = ref<StudentActivityItem[]>([]);
const activityParticipationList = ref<ActivityParticipationItem[]>([]);

// 显示申请详情
const showApplication = ref(false);

const detail = ref<ApplicationDetail | null>(null);

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
  // fetchPendingList();
  fetchStatistics();
  // 设置定时轮询，每30秒更新一次数据
  pollingTimer.value = window.setInterval(() => {
    // fetchPendingList();
    // initData();
    fetchStatistics();
  }, 30000);
});

onUnmounted(() => {
  // 销毁定时器
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value);
  }
});
const initData = async () => {
  // const response = await dashboardStore.getOverview();

  // const dashboardData = response.data;
  // console.log("dashboardData", dashboardData);
  // // 社团名称
  // club_name.value = dashboardData.clubName;
  // localStorage.setItem("club_name", club_name.value);
  // // 成员数量
  // member_count.value = dashboardData.memberCount;
  // // 待审核申请数量
  // pendingApplicationCount.value = dashboardData.pendingApplicationsCount;
  // 活动数量
  // activitiesCount.value = dashboardData.recentActivities.filter(
  //   (item: { status: string }) => item.status === "已批准"
  // ).length;
  // 最近活动
  // const approvedActivity = dashboardData.recentActivities.find(
  //   (item: { status: string }) => item.status === "已批准"
  // );
  // recentActivity.value = approvedActivity?.title || "";
};

// 3. 异步获取数据（更新pendingApplications，computed会自动重新计算）
const fetchPendingList = async () => {
  try {
    // const result = await applicationStore.getApplyList();

    // pendingApplications.value = result.data; // 更新响应式数据
    // console.log("所有申请列表:", result);
    // // 此时computed已经自动计算，直接打印filterApplications.value即可
    // console.log("筛选后的申请:", filterApplications.value);
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
  } catch {
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
  } catch {
    ElMessage.error("申请拒绝失败");
  }
};

// 显示申请详情
const showApplicationDetail = async (applicationId: number) => {
  showApplication.value = true;

  detail.value = await applicationStore.getApplyDetail(applicationId);
  // console.log("申请详情:", detail);
};

// 获取统计数据
const fetchStatistics = async () => {
  try {
    // 获取社团活跃度统计
    const clubActivityRes = await adminDashboardStore.getClubActivityStatistics();
    clubActivityList.value = clubActivityRes.data?.list || [];

    // 获取学生活动参与统计
    const studentActivityRes =
      await adminDashboardStore.getStudentActivityStatistics();
    studentActivityList.value = studentActivityRes.data?.list || [];

    // 获取活动参与率统计
    const participationRes =
      await adminDashboardStore.getActivityParticipationStatistics();
    activityParticipationList.value = participationRes.data?.list || [];
  } catch (error) {
    console.error("获取统计数据失败:", error);
    ElMessage.error("获取统计数据失败");
  }
};
</script>
<style scoped>
/* Apple Design System 字体栈 */
.dashboard-container {
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.dashboard-container {
  padding: 24px;
}

.dashboard-header h1 {
  margin-bottom: 32px;
  font-size: 28px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.5px;
  line-height: 1.2;
}

.dashboard-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.statistics-section {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  margin-top: 24px;
}

.statistics-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 16px;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06),
              0 1px 2px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.statistics-card:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08),
              0 2px 4px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
}

.statistics-content {
  max-height: 400px;
  overflow-y: auto;
}

.dashboard-section {
  height: 100%;
  overflow: auto;
}

.section-header {
  margin-bottom: 24px;
}

.section-header h3 {
  color: #1d1d1f;
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: -0.3px;
}

.application-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 8px;
}

.application-item:hover {
  background-color: rgba(0, 122, 255, 0.05);
}

.empty-message {
  text-align: center;
  color: #86868b;
  padding: 60px 0;
  font-size: 15px;
  font-weight: 400;
}

.app-name {
  color: #1d1d1f;
  margin: 0 0 8px 0;
  font-weight: 500;
  font-size: 17px;
}

.app-name span {
  color: #007AFF;
  font-weight: 600;
}

.app-time {
  color: #86868b;
  font-size: 13px;
  margin: 0;
}

.app-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: center;
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
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  width: 120px;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
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
  font-size: 15px;
  color: #1d1d1f;
  font-weight: 500;
  letter-spacing: -0.2px;
}

.form-show {
  padding: 20px 24px;
}

.form-show :deep(.el-form-item__label) {
  font-size: 15px;
  width: 90px;
  font-weight: 500;
  border-right: 0.5px solid rgba(0, 0, 0, 0.08);
  color: #1d1d1f;
}

.form-show :deep(.el-form-item) {
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.08);
}

/* 表格样式 - Apple风格 */
:deep(.el-table) {
  background: transparent;
  border-radius: 12px;
}

:deep(.el-table__header) {
  background: rgba(0, 0, 0, 0.02);
}

:deep(.el-table th) {
  background: rgba(0, 0, 0, 0.02);
  color: #1d1d1f;
  font-weight: 500;
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.08);
}

:deep(.el-table td) {
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.08);
  color: #1d1d1f;
}

:deep(.el-table tr:hover > td) {
  background: rgba(0, 122, 255, 0.05);
}

/* 暗色模式 */
[data-theme="dark"] .dashboard-container {
  background: #000000;
}

[data-theme="dark"] .dashboard-header h1 {
  color: #f5f5f7;
}

[data-theme="dark"] .dashboard-section {
  background-color: rgba(28, 28, 30, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border: 0.5px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .section-header h3 {
  color: #f5f5f7;
}

[data-theme="dark"] .application-item {
  border-bottom: 0.5px solid rgba(255, 255, 255, 0.1);
}

[data-theme="dark"] .application-item:hover {
  background-color: rgba(10, 132, 255, 0.15);
}

[data-theme="dark"] .empty-message {
  color: #86868b;
}

[data-theme="dark"] .app-name {
  color: #f5f5f7;
}

[data-theme="dark"] .app-name span {
  color: #0a84ff;
}

[data-theme="dark"] .app-time {
  color: #86868b;
}

[data-theme="dark"] .action-card {
  background: rgba(28, 28, 30, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border: 0.5px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .action-text {
  color: #f5f5f7;
}

[data-theme="dark"] .statistics-card {
  background: rgba(28, 28, 30, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border: 0.5px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .statistics-content {
  color: #f5f5f7;
}

[data-theme="dark"] :deep(.el-table__header) {
  background: rgba(255, 255, 255, 0.05);
}

[data-theme="dark"] :deep(.el-table th) {
  background: rgba(255, 255, 255, 0.05);
  color: #f5f5f7;
  border-bottom: 0.5px solid rgba(255, 255, 255, 0.1);
}

[data-theme="dark"] :deep(.el-table td) {
  border-bottom: 0.5px solid rgba(255, 255, 255, 0.1);
  color: #f5f5f7;
}

[data-theme="dark"] :deep(.el-table tr:hover > td) {
  background: rgba(10, 132, 255, 0.15);
}
</style>

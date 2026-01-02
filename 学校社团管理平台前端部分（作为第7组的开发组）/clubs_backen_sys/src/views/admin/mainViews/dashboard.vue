<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="dashboard-container">
    <upload />
    <div class="dashboard-header">
      <h1>数据分析</h1>
    </div>
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
          <div v-else class="chart-container">
            <div ref="clubActivityChartRef" class="chart-wrapper"></div>
          </div>
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
          <div v-else class="chart-container">
            <div ref="studentActivityChartRef" class="chart-wrapper"></div>
          </div>
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
          <div v-else class="chart-container">
            <div ref="participationRateChartRef" class="chart-wrapper"></div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, onUnmounted, nextTick } from "vue";
// import { useDashboardStore } from "../../../stores/dashboard";
import { useApplicationStore } from "../../../stores/application";
import { useAdminDashboardStore } from "../../../stores/admin/dashboard";
import { ElMessage } from "element-plus";
// import { formatIsoTime } from "../../../utils/format";
// import { useRouter } from "vue-router";
// import DataCard from "../../../components/DataCard.vue";
import upload from "@/components/upload.vue";
import * as echarts from "echarts";

// 引入类型
import type {
  // ApplicationItem,
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
// const dashboardStore = useDashboardStore();
const applicationStore = useApplicationStore();
const adminDashboardStore = useAdminDashboardStore();

// 路由
// const router = useRouter();

// 社团名称
// const club_name = ref("");
// 成员数量
// const member_count = ref(0);
// 待审核申请数量
// const pendingApplicationCount = ref(0);
// 活动数量
// const activitiesCount = ref(0);
// 最近活动
// const recentActivity = ref(0);

// 统计数据
const clubActivityList = ref<ClubActivityItem[]>([]);
const studentActivityList = ref<StudentActivityItem[]>([]);
const activityParticipationList = ref<ActivityParticipationItem[]>([]);

// ECharts 实例
const clubActivityChartRef = ref<HTMLDivElement | null>(null);
const studentActivityChartRef = ref<HTMLDivElement | null>(null);
const participationRateChartRef = ref<HTMLDivElement | null>(null);
let clubActivityChart: echarts.ECharts | null = null;
let studentActivityChart: echarts.ECharts | null = null;
let participationRateChart: echarts.ECharts | null = null;

// 显示申请详情
const showApplication = ref(false);

const detail = ref<ApplicationDetail | null>(null);

// 1. 定义响应式数据（全局）
// const pendingApplications = ref<{ list: ApplicationItem[]; total: number }>({
//   list: [],
//   total: 0,
// }); // 所有申请列表
// 2. 定义computed（全局，依赖pendingApplications）
// const filterApplications = computed(() => {
//   // 判空：确保list存在
//   if (!pendingApplications.value?.list) return [];
//   // 筛选"待审核"的申请
//   return pendingApplications.value.list.filter(
//     (item) => item.status === "待审核"
//   );
// });

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
  // 销毁图表实例
  if (clubActivityChart) {
    clubActivityChart.dispose();
  }
  if (studentActivityChart) {
    studentActivityChart.dispose();
  }
  if (participationRateChart) {
    participationRateChart.dispose();
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
// const fetchPendingList = async () => {
//   try {
//     // const result = await applicationStore.getApplyList();

//     // pendingApplications.value = result.data; // 更新响应式数据
//     // console.log("所有申请列表:", result);
//     // // 此时computed已经自动计算，直接打印filterApplications.value即可
//     // console.log("筛选后的申请:", filterApplications.value);
//   } catch (error) {
//     console.error("获取申请列表失败:", error);
//     // pendingApplications.value = { list: [], total: 0 }; // 异常时重置
//   }
// };

// 方法
// const navigateTo = (routeName: string) => {
//   router.push({ name: routeName });
// };

// const cards = computed(() => [
//   {
//     title: "待审核入团申请",
//     value: pendingApplicationCount.value,
//   },
//   {
//     title: "社团成员",
//     value: member_count.value,
//   },
//   {
//     title: "活动总数",
//     value: activitiesCount.value,
//   },
//   {
//     title: "近期活动",
//     value: recentActivity.value,
//   },
// ]);

// 同意申请
const handleApprove = async (applicationId: number | undefined) => {
  try {
    await applicationStore.approveApplication(
      applicationId,
      "通过",
      "欢迎加入"
    );
    // 关键：审核成功后刷新申请列表
    // await fetchPendingList();
    // 同时刷新统计数据（待审核数量会减少）
    // await initData();
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
    // await fetchPendingList();
    // 同时刷新统计数据（待审核数量会减少）
    // await initData();
    showApplication.value = false;
    ElMessage.success("申请拒绝成功");
  } catch {
    ElMessage.error("申请拒绝失败");
  }
};

// 显示申请详情
// const showApplicationDetail = async (applicationId: number) => {
//   showApplication.value = true;

//   detail.value = await applicationStore.getApplyDetail(applicationId);
//   // console.log("申请详情:", detail);
// };

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

    // 更新图表
    await nextTick();
    initClubActivityChart();
    initStudentActivityChart();
    initParticipationRateChart();
  } catch (error) {
    console.error("获取统计数据失败:", error);
    ElMessage.error("获取统计数据失败");
  }
};

// 初始化社团活跃度统计图表
const initClubActivityChart = () => {
  if (!clubActivityChartRef.value || clubActivityList.value.length === 0) return;

  if (clubActivityChart) {
    clubActivityChart.dispose();
  }

  clubActivityChart = echarts.init(clubActivityChartRef.value);
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    legend: {
      data: ["活动总数", "参与人数", "平均积分"],
      top: 10,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: clubActivityList.value.map((item) => item.clubName),
      axisLabel: {
        rotate: 45,
        interval: 0,
      },
    },
    yAxis: [
      {
        type: "value",
        name: "数量",
        position: "left",
      },
      {
        type: "value",
        name: "积分",
        position: "right",
      },
    ],
    series: [
      {
        name: "活动总数",
        type: "bar",
        data: clubActivityList.value.map((item) => item.totalActivities),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "#83bff6" },
            { offset: 0.5, color: "#188df0" },
            { offset: 1, color: "#188df0" },
          ]),
        },
      },
      {
        name: "参与人数",
        type: "bar",
        data: clubActivityList.value.map((item) => item.totalParticipants),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "#ffd93d" },
            { offset: 0.5, color: "#ff6b6b" },
            { offset: 1, color: "#ff6b6b" },
          ]),
        },
      },
      {
        name: "平均积分",
        type: "line",
        yAxisIndex: 1,
        data: clubActivityList.value.map((item) => item.avgPoints?.toFixed(2) || 0),
        itemStyle: {
          color: "#06d6a0",
        },
        lineStyle: {
          width: 3,
        },
        symbol: "circle",
        symbolSize: 8,
      },
    ],
  };
  clubActivityChart.setOption(option);
};

// 初始化学生活动参与统计图表
const initStudentActivityChart = () => {
  if (!studentActivityChartRef.value || studentActivityList.value.length === 0) return;

  if (studentActivityChart) {
    studentActivityChart.dispose();
  }

  // 只显示前10名，避免图表过于拥挤
  const displayData = studentActivityList.value.slice(0, 10);

  studentActivityChart = echarts.init(studentActivityChartRef.value);
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    legend: {
      data: ["总活动数", "参与活动数", "总积分"],
      top: 10,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: displayData.map((item) => item.name),
      axisLabel: {
        rotate: 45,
        interval: 0,
      },
    },
    yAxis: [
      {
        type: "value",
        name: "活动数",
        position: "left",
      },
      {
        type: "value",
        name: "积分",
        position: "right",
      },
    ],
    series: [
      {
        name: "总活动数",
        type: "bar",
        data: displayData.map((item) => item.totalActivities),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "#667eea" },
            { offset: 1, color: "#764ba2" },
          ]),
        },
      },
      {
        name: "参与活动数",
        type: "bar",
        data: displayData.map((item) => item.attendedActivities),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "#f093fb" },
            { offset: 1, color: "#f5576c" },
          ]),
        },
      },
      {
        name: "总积分",
        type: "line",
        yAxisIndex: 1,
        data: displayData.map((item) => item.totalPoints?.toFixed(1) || 0),
        itemStyle: {
          color: "#ffd93d",
        },
        lineStyle: {
          width: 3,
        },
        symbol: "circle",
        symbolSize: 8,
      },
    ],
  };
  studentActivityChart.setOption(option);
};

// 初始化活动参与率统计图表
const initParticipationRateChart = () => {
  if (!participationRateChartRef.value || activityParticipationList.value.length === 0) return;

  if (participationRateChart) {
    participationRateChart.dispose();
  }

  // 只显示前10个活动，避免图表过于拥挤
  const displayData = activityParticipationList.value.slice(0, 10);

  participationRateChart = echarts.init(participationRateChartRef.value);
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
      formatter: (params: unknown) => {
        const param = (params as Array<{ dataIndex: number }>)[0];
        if (!param) return "";
        const data = displayData[param.dataIndex];
        if (!data) return "";
        return `
          <div style="margin-bottom: 8px; font-weight: 600;">${data.title}</div>
          <div>社团: ${data.clubName}</div>
          <div>最大参与数: ${data.maxParticipants}</div>
          <div>已报名数: ${data.registeredCount}</div>
          <div>参与率: ${data.participationRate?.toFixed(1) || 0}%</div>
        `;
      },
    },
    legend: {
      data: ["参与率"],
      top: 10,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "15%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: displayData.map((item) => item.title.length > 15 ? item.title.substring(0, 15) + "..." : item.title),
      axisLabel: {
        rotate: 45,
        interval: 0,
      },
    },
    yAxis: {
      type: "value",
      name: "参与率 (%)",
      max: 100,
    },
    series: [
      {
        name: "参与率",
        type: "bar",
        data: displayData.map((item) => item.participationRate?.toFixed(1) || 0),
        itemStyle: {
          color: (params: { dataIndex: number }) => {
            const data = displayData[params.dataIndex];
            if (!data) return "#cccccc";
            const rate = data.participationRate;
            if (rate >= 80) {
              return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#06d6a0" },
                { offset: 1, color: "#118ab2" },
              ]);
            } else if (rate >= 50) {
              return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#ffd93d" },
                { offset: 1, color: "#ffa500" },
              ]);
            } else {
              return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#ff6b6b" },
                { offset: 1, color: "#c92a2a" },
              ]);
            }
          },
        },
        label: {
          show: true,
          position: "top",
          formatter: "{c}%",
        },
      },
    ],
  };
  participationRateChart.setOption(option);
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
  overflow: hidden;
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
  overflow-x: hidden;
}

.chart-container {
  width: 100%;
}

.chart-wrapper {
  width: 100%;
  height: 400px;
  min-height: 400px;
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

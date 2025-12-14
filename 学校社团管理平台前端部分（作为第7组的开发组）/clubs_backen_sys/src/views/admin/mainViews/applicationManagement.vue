<template>
  <div class="application-container">
    <div class="application-header">
      <h1>社团活动申请管理</h1>
    </div>

    <!-- 数据卡片统计 -->
    <DataCard :cards="cards" />

    <!-- 活动报名列表 -->
    <el-card v-for="item in activityList" :key="item.activityId">

      <div class="item-title">{{ item.title }}</div>
      <el-table v-loading="loading[item.activityId]" :data="activityPagination[item.activityId]?.list || []"
        style="width: 100%" :title="item.title">
        <el-table-column prop="registrationId" label="序号">
        </el-table-column>
        <el-table-column prop="name" label="姓名">
        </el-table-column>
        <el-table-column prop="studentId" label="学号"></el-table-column>
        <el-table-column prop="major" label="专业"></el-table-column>
        <el-table-column prop="registrationTime" label="报名时间"></el-table-column>
        <!-- <el-table-column label="操作">
          <el-button>
            审批
          </el-button>
        </el-table-column> -->

      </el-table>

      <div class="footer">
        <!-- 分页 -->
        <el-pagination :current-page="activityPagination[item.activityId]?.currentPage || 1"
          :page-size="activityPagination[item.activityId]?.pageSize || 10" :page-sizes="[1, 2, 5, 10]"
          layout="total, sizes, prev, pager, next, jumper" :total="activityPagination[item.activityId]?.total || 0"
          @size-change="(val: number) => handleSizeChange(item.activityId, val)"
          @current-change="(val: number) => handleCurrentChange(item.activityId, val)" />

        <el-button type="success" v-if="item.status === '已批准' && activityPagination[item.activityId]?.list.length"
          @click=handleApprove(item.activityId)>
          确认参加人员
        </el-button>
      </div>

    </el-card>
    <!-- <div class="send-points">
      <el-button type="success"
                 @click="getAttendance()">
        发放活动积分</el-button>
    </div> -->

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import type {
  ActivityEnrollItem,
  ActivityItem,
  ActivityPagination,
} from "../../../types/activity";
import { useActivityStore } from "../../../stores/activity";
import { ElMessage } from "element-plus";
import { pa } from "element-plus/es/locales.mjs";
import DataCard from "../../../components/DataCard.vue";

const club_name = localStorage.getItem("club_name");

// 获取store
const activityStore = useActivityStore();

// 全部申请数
const ApplyAllTotal = ref(0);
// 待处理申请数
const ApplyWaitTotal = ref(0);
// 已批准申请数
const ApplyApprovedTotal = ref(0);
// 已拒绝申请数
const ApplyRejectedTotal = ref(0);

// 统计卡片
const cards = computed(() => [
  {
    title: "全部活动申请",
    value: ApplyAllTotal.value,
  },
  {
    title: "待处理申请",
    value: ApplyWaitTotal.value,
  },
  {
    title: "已批准申请",
    value: ApplyApprovedTotal.value,
  },
  {
    title: "已拒绝申请",
    value: ApplyRejectedTotal.value,
  },
]);

// 加载中（按活动ID存储）
const loading = ref<Record<number, boolean>>({});
// 活动列表
const activityList = ref<ActivityItem[]>([]);
// 初始化每个活动的分页状态
const activityPagination = ref<Record<number, ActivityPagination>>({});

onMounted(async () => {
  loadData();
});

const loadData = async () => {
  // 重置统计值
  ApplyAllTotal.value = 0;
  ApplyWaitTotal.value = 0;
  ApplyApprovedTotal.value = 0;
  ApplyRejectedTotal.value = 0;
  // 获取活动列表
  await getActivityList();
  if (activityList.value.length > 0) {
    activityList.value.forEach(async (activity) => {
      await getActivityEnrollList(activity.activityId);
    });
  }
};

// 获取活动列表
const getActivityList = async () => {
  try {
    const result = await activityStore.getActivityList();
    activityList.value = result.data.list;

    // 设置默认状态
    if (activityList.value.length > 0) {
      activityList.value.forEach((activity) => {
        // 默认分页状态
        activityPagination.value[activity.activityId] = {
          currentPage: 1,
          pageSize: 10,
          total: 0,
          list: [],
          registrationIds: [],
        };
      });
    }
  } catch (error) {
    ElMessage.error("获取活动列表失败");
    activityList.value = [];
  }
};

// 获取活动报名列表
const getActivityEnrollList = async (activityId: number) => {
  const pagination = activityPagination.value[activityId];
  // 获取活动 默认分页状态
  if (!pagination) return;
  // 对应活动的加载状态
  loading.value[activityId] = true;
  try {
    const result = await activityStore.getActivityEnrollList(
      activityId,
      pagination.currentPage,
      pagination.pageSize
    );
    const originalList = result.data.list;
    // 获取当前活动报名列表
    pagination.list = originalList.filter((item: ActivityEnrollItem) => {
      return item.status === "已报名";
    });
    // 获取当前页面待处理的报名人数
    ApplyWaitTotal.value += pagination.list.length;
    // 获取已批准申请的报名人数
    ApplyApprovedTotal.value += originalList.filter(
      (item: ActivityEnrollItem) => {
        return item.status === "已参加";
      }
    ).length;
    // 获取已拒绝申请的报名人数
    ApplyRejectedTotal.value += originalList.filter(
      (item: ActivityEnrollItem) => {
        return item.status === "已取消";
      }
    ).length;
    pagination.registrationIds = pagination.list.map(
      (item: ActivityEnrollItem) => item.registrationId
    );
    // 获取当前活动报名总数
    pagination.total = result.data.total;
    ApplyAllTotal.value = pagination.total;
  } catch (error) {
    ElMessage.error(`获取活动${activityId}报名列表失败`);
    pagination.list = [];
    pagination.total = 0;
  } finally {
    loading.value[activityId] = false;
  }
};

// 获取活动详情
const getActivityDetail = async (activityId: number) => {
  try {
    const detail = await activityStore.getActivityDetail(activityId);
    const points = detail.data.points;
    console.log("积分", points);
    return detail.data.points;
  } catch (error) {
    ElMessage.error("获取活动详情失败");
    return 0;
  }
};

// 新增：处理每页条数变化
const handleSizeChange = (activityId: number, newPageSize: number) => {
  const pagination = activityPagination.value[activityId];
  if (!pagination) return;

  pagination.pageSize = newPageSize;
  // 切换条数后重回第一页
  pagination.currentPage = 1;
  // 重新加载该活动的列表
  getActivityEnrollList(activityId);
};

// 新增：处理页码变化
const handleCurrentChange = (activityId: number, newCurrentPage: number) => {
  const pagination = activityPagination.value[activityId];
  if (!pagination) return;

  pagination.currentPage = newCurrentPage;
  // 重新加载该活动的列表
  getActivityEnrollList(activityId);
};

// 一键审批
const handleApprove = async (activityId: number) => {
  const pagination = activityPagination.value[activityId];
  if (!pagination) return;
  try {
    await activityStore.confirmActivityEnroll(activityId, {
      registration_ids: pagination.registrationIds,
      points_earned: await getActivityDetail(activityId),
    });
    ElMessage.success("审批成功");
    // 重新加载数据
    await loadData();
  } catch (error) {
    ElMessage.error("审批失败");
  }
};
</script>
<style scoped>
/* Apple Design System 字体栈 */
.application-container {
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.application-container {
  padding: 24px;
}

.application-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.5px;
  line-height: 1.2;
  margin-bottom: 32px;
}

.footer {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
  align-items: center;
  gap: 16px;
}

/* 卡片样式 - Apple风格 */
:deep(.el-card) {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 16px;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06),
              0 1px 2px rgba(0, 0, 0, 0.04);
  padding: 24px;
  margin-bottom: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-card:hover) {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08),
              0 2px 4px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
}

.item-title {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.3px;
  margin-bottom: 20px;
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

/* 按钮样式 - Apple风格 */
:deep(.el-button) {
  border-radius: 10px;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 暗色模式 */
[data-theme="dark"] .application-container {
  background: #000000;
}

[data-theme="dark"] .application-header h1 {
  color: #f5f5f7;
}

[data-theme="dark"] .item-title {
  color: #f5f5f7;
}

[data-theme="dark"] :deep(.el-card) {
  background: rgba(28, 28, 30, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border: 0.5px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
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

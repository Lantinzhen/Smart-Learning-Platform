<template>
  <div class="activity-detail">
    <!-- 返回活动列表 按钮 -->
    <el-button type="primary"
               @click="goBack()">
      返回活动列表
    </el-button>
    <div class="detail-header">
      <h1>活动详情</h1>
    </div>

    <!-- 活动详情 内容 -->
    <div class="detail-content"
         v-if="activityDetail">
      <el-card>
        <div class="content-header">
          <!-- 活动标题 -->
          <h1> {{ activityDetail.title }}</h1>
          <el-tag :type="getStatusTagType(activityDetail.status)"
                  size="small">{{ activityDetail.status }}</el-tag>
        </div>
        <!-- 活动信息 -->
        <div class="activity-info">

          <div class="info-item">
            <div class="info-label">活动分类</div>
            <div class="info-value">{{ activityDetail.categoryName }}</div>
          </div>

          <div class="info-item">
            <div class="info-label">创建时间</div>
            <div class="info-value">{{ formatIsoTime(activityDetail.createdAt) }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">活动时间</div>
            <div class="info-value">{{ formatIsoTime(activityDetail.startTime) }} 至 {{ formatIsoTime(activityDetail.endTime) }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">报名截止</div>
            <div class="info-value">{{ formatIsoTime(activityDetail.registrationDeadline) }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">活动地点</div>
            <div class="info-value">{{ activityDetail.location }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">参与人数</div>
            <div class="info-value">{{ activityDetail.registeredCount }} / {{ activityDetail.maxParticipants }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">创建人</div>
            <div class="info-value">{{ activityDetail.createdBy }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">活动积分</div>
            <div class="info-value">{{ activityDetail.points }} 分</div>
          </div>
        </div>
      </el-card>
      <!-- 活动描述 -->
      <el-card>
        <div class="activity-description">
          <div class="info-label">活动描述</div>
          <div class="info-value">{{ activityDetail.description }}</div>
          <div class="info-label">活动海报</div>
          <!-- :src=activityDetail.posterUrl -->
          <!-- src="../../assets/logo.svg" -->
          <img :src=activityDetail.posterUrl
               alt="活动海报"
               class="info-poster">

        </div>
      </el-card>
      <!-- 活动报名列表 -->
      <el-card>
        <div class="table-name">报名列表</div>
        <el-table v-loading="loading"
                  :data="ApplyMembers"
                  style="width: 100%">
          <el-table-column prop="registrationId"
                           label="序号">
          </el-table-column>
          <el-table-column prop="name"
                           label="姓名">
          </el-table-column>
          <el-table-column prop="studentId"
                           label="学号"></el-table-column>
          <el-table-column prop="major"
                           label="专业"></el-table-column>
          <el-table-column prop="registrationTime"
                           label="报名时间"></el-table-column>

        </el-table>
        <!-- 分页 -->
        <el-pagination :current-page="currentPage"
                       :page-size="pageSize"
                       :page-sizes="[1, 2, 5, 10]"
                       layout="total, sizes, prev, pager, next, jumper"
                       :total="total"
                       @size-change="handleSizeChange"
                       @current-change="handleCurrentChange"
                       class="pagination" />

      </el-card>
      <!-- <div class="send-points"
           v-if="activityDetail.status === '已批准'">
        <el-button type="success"
                   @click="getAttendance()">
          发放活动积分</el-button>
      </div> -->

    </div>

  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useActivityStore } from "../../stores/activity";
import type { ActivityDetail, ActivityEnrollItem } from "../../types/activity";
import { getStatusTagType } from "../../utils/map";
import { formatIsoTime } from "../../utils/format";
import { ElMessage } from "element-plus";

const router = useRouter();
const route = useRoute();
const routeId = ref(route.params.id);
const activityDetail = ref<ActivityDetail>();

// 加载中
const loading = ref(false);
// 活动报名列表
const ApplyMembers = ref<ActivityEnrollItem[]>([]);

// 新增：总条数（从接口返回的总数）
const total = ref(0);
// 当前页
const currentPage = ref(1);
// 每页显示的成员数
const pageSize = ref(10);

// 统计报名的id，用来发放积分
const registrationIds = ref<number[]>([]);

// 分页后的成员列表
// const paginatedMembers = computed(() => {
//   const start = (currentPage.value - 1) * pageSize.value;
//   const end = start + pageSize.value;
//   return ApplyMembers.value.slice(start, end);
// });

// 获取活动id
const id = ref(parseInt(routeId.value as string, 10));

// 获取活动store
const activityStore = useActivityStore();

onMounted(async () => {
  // 获取活动详情
  getActivityDetail();
  // 获取活动报名列表
  getActivityEnrollList(currentPage.value, pageSize.value);
});

const getActivityDetail = async () => {
  const result = await activityStore.getActivityDetail(id.value);
  console.log("活动详情", result);

  activityDetail.value = result.data;
  // console.log("活动详情", activityDetail.value);
};

// 获取活动报名列表
const getActivityEnrollList = async (currentPage: number, pageSize: number) => {
  loading.value = true;
  const result = await activityStore.getActivityEnrollList(
    id.value,
    currentPage,
    pageSize
  );
  console.log("活动报名列表", result);
  ApplyMembers.value = result.data.list.filter((item: ActivityEnrollItem) => {
    return item.status === "已参加";
  });
  total.value = result.data.total;
  loading.value = false;
};

// // 获取报名id
// const getAttendance = async () => {
//   // 获取报名id
//   registrationIds.value = ApplyMembers.value.map((item) => item.registrationId);
//   // 获取活动积分
//   activityDetail.value?.points;

//   try {
//     // 发放积分
//     await activityStore.confirmActivityEnroll(id.value, {
//       registration_ids: registrationIds.value,
//       points_earned: activityDetail.value?.points || 0,
//     });
//     ElMessage.success("发放成功");
//   } catch (error) {
//     ElMessage.error("发放失败");
//   }
// };

// 处理每页条数变化
const handleSizeChange = (newPageSize: number) => {
  pageSize.value = newPageSize;
  // 切换每页条数后，重置到第1页并重新请求数据
  currentPage.value = 1;
  console.log("当前页,页大小", currentPage.value, pageSize.value);
  getActivityEnrollList(currentPage.value, pageSize.value);
};

// 处理页码变化
const handleCurrentChange = (newCurrentPage: number) => {
  currentPage.value = newCurrentPage;
  // 切换页码后重新请求数据
  getActivityEnrollList(currentPage.value, pageSize.value);
};

const goBack = () => {
  // router.push({ path: "/activity" });
  history.back();
};
</script>

<style scoped>
.activity-detail {
  padding: 20px;
}
.detail-header h1 {
  padding-left: 10px;
  border-left: 5px solid #01ffd5;
}
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.activity-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.info-label {
  font-size: 14px;
  color: #999;
}
.info-value {
  font-size: 16px;
  color: #333;
}
.info-poster {
  max-width: 50%;
  max-height: 50%;
}
.pagination {
  margin-top: 20px;
}
.send-points {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 暗色模式 */
[data-theme="dark"] .el-card {
  background-color: #303133;
  border: none;
  box-shadow: 0 2px 12px 0 #ffffff40;
}

[data-theme="dark"] .info-value {
  font-size: 16px;
  color: #fff;
}
[data-theme="dark"] :deep(.el-table__inner-wrapper) {
  background-color: #303133;
  border: none;
  box-shadow: 0 2px 12px 0 #ffffff40;
}
</style>


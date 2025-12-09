<template>
  <div class="activity-container">
    <div class="activity-header">
      <h1>{{ club_name }} 活动管理</h1>
    </div>
    <!-- 数据卡片统计 -->
    <DataCard :cards="cards" />

    <div class="activity-content">
      <!-- 活动列表 -->
      <el-card class="activity-list-card">
        <div class="search-filters">
          <!-- 搜索栏 -->
          <el-input v-model="searchQuery"
                    placeholder="搜索活动"
                    prefix-icon="el-icon-search"
                    class="search-input" />

          <!-- 筛选栏 -->
          <el-select v-model="statusFilter"
                     placeholder="全部状态"
                     class="status-filter">
            <el-option v-for="item in filterCards"
                       :key="item.label"
                       :label="item.label"
                       :value="item.value" />
          </el-select>
          <!-- 添加活动 -->
          <!-- <el-button @click="showCreateActivity = true">+ 添加活动</el-button> -->
          <button class="create-activity"
                  @click="showCreateActivity = true">+ 添加活动</button>
          <button class="filter-select"
                  @click="clearFilter()">清空筛选</button>
        </div>

        <div class="activity-list">
          <!-- 活动列表 -空白占位 -->
          <template v-if="filteredActivityList.length === 0">
            <div class="empty-message">暂无活动数据</div>
          </template>
          <!-- 活动列表 -数据项 -->
          <template v-else>
            <div v-for="activity in filteredActivityList"
                 :key="activity.activityId"
                 class="activity-item"
                 @click="goActivityDetail(activity.activityId)">
              <!-- 活动信息 -->
              <div class="activity-info">
                <div class="activity-header">
                  <!-- 活动名称 -->
                  <h4 class="activity-name">{{ activity.title }}</h4>
                  <!-- 活动状态 -->
                  <el-tag :type="getStatusTagType(activity.status)"
                          size="small">{{ activity.status }}</el-tag>
                </div>
                <!-- 活动信息 -时间地点 -->
                <div class="activity-meta">
                  <span class="meta-item">
                    <el-icon>
                      <Calendar />
                    </el-icon>
                    {{ formatIsoTime(activity.startTime) }}</span>
                  <span class="meta-item">
                    <el-icon>
                      <DeleteLocation />
                    </el-icon> {{ activity.location }}</span>
                  <span class="meta-item">
                    <el-icon>
                      <User />
                    </el-icon> {{ activity.registeredCount }}</span>
                </div>
              </div>
              <!-- 活动操作按钮 -->
              <div class="activity-actions">
                <el-button size="small"
                           type="warning"
                           v-if="activity.status === '草稿'|| activity.status === '已拒绝'"
                           @click.stop="handleEdit(activity.activityId)">编辑</el-button>
                <el-button size="small"
                           type="success"
                           v-if="activity.status === '草稿'|| activity.status === '已拒绝'"
                           @click.stop="handleSubmitActivity(activity.activityId)">提交审批</el-button>

              </div>
            </div>
          </template>
        </div>
      </el-card>
      <!-- 创建活动弹窗 -->
      <el-drawer v-model="showCreateActivity"
                 :title="editorActivity? '编辑活动' : '创建活动'"
                 size="70%"
                 direction="ltr"
                 :before-close="handleClose">
        <el-form ref="activityFormRef"
                 :model="activityForm"
                 :rules="activityRules"
                 label-width="120px">
          <!-- 活动名称  可编辑-->
          <el-form-item label="活动名称"
                        prop="title">
            <el-input v-model="activityForm.title"
                      placeholder="请输入活动名称" />
          </el-form-item>
          <!-- 活动类别 -->
          <el-form-item label="活动类别"
                        prop="category_id">
            <el-select v-model="activityForm.category_id"
                       :disabled="editorActivity">
              <el-option v-for="item in categoryOptions"
                         :key="item.category_id"
                         :label="item.name"
                         :value="item.category_id" />
            </el-select>
          </el-form-item>
          <!-- 活动描述 可编辑-->
          <el-form-item label="活动描述"
                        prop="description">
            <el-input v-model="activityForm.description"
                      type="textarea"
                      placeholder="请输入活动描述" />
          </el-form-item>
          <!-- 活动海报url -->
          <el-form-item label="活动海报url"
                        prop="poster_url">
            <el-input v-model="activityForm.poster_url"
                      placeholder="活动海报url"
                      :disabled="editorActivity" />
          </el-form-item>
          <!-- 活动地点 可编辑-->
          <el-form-item label="活动地点"
                        prop="location">
            <el-input v-model="activityForm.location"
                      placeholder="活动地点" />
          </el-form-item>
          <!-- 活动人数限制 -->
          <el-form-item label="人数限制"
                        prop="max_participants">
            <el-input-number v-model="activityForm.max_participants"
                             :min="1"
                             :disabled="editorActivity" />
          </el-form-item>
          <!-- 活动积分 -->
          <el-form-item label="活动积分"
                        prop="points">
            <el-input-number v-model="activityForm.points"
                             :step="0.1"
                             :min="0.1"
                             :disabled="editorActivity" />
          </el-form-item>
          <!-- 活动开始时间 -->
          <el-form-item label="开始时间"
                        prop="start_time">
            <el-date-picker v-model="activityForm.start_time"
                            type="datetime"
                            placeholder="选择活动开始时间"
                            :disabled-date="disabledStartDate"
                            :disabled="editorActivity" />
          </el-form-item>
          <!-- 活动结束时间 -->
          <el-form-item label="结束时间"
                        prop="end_time">
            <el-date-picker v-model="activityForm.end_time"
                            type="datetime"
                            placeholder="选择活动结束时间"
                            :disabled-date="disabledEndDate"
                            :disabled="editorActivity" />
          </el-form-item>
          <!-- 报名截止时间 -->

          <el-form-item label="报名截止时间"
                        prop="registration_deadline">
            <el-date-picker v-model="activityForm.registration_deadline"
                            type="datetime"
                            placeholder="选择报名截止时间"
                            :disabled-date="disabledStartDate"
                            :disabled="editorActivity" />
          </el-form-item>
          <div class="submit-activity">
            <el-button type="primary"
                       v-if="!editorActivity"
                       @click="handleCreate()">创建活动</el-button>
            <el-button type="primary"
                       v-if="editorActivity && activityForm.activity_id"
                       @click="handleSubmitEdit(activityForm.activity_id)">提交更新</el-button>
            <el-button type="danger"
                       @click="handleCancel()">取消</el-button>
          </div>
        </el-form>
      </el-drawer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useActivityStore } from "../../stores/activity";
import type { ActivityItem } from "../../types/activity";
import { formatIsoTime } from "../../utils/format";
import { getStatusTagType } from "../../utils/map";
import { User } from "@element-plus/icons";
import { useRouter } from "vue-router";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage, ElMessageBox } from "element-plus";
import type { ActivityForm, updateActivityForm } from "../../types/activity";
import DataCard from "../../components/DataCard.vue";

// 路由
const router = useRouter();

// 获取活动store
const activityStore = useActivityStore();

const club_name = localStorage.getItem("club_name");
// 搜索栏
const searchQuery = ref("");
// 筛选栏
const statusFilter = ref("全部");
// 添加活动弹窗
const showCreateActivity = ref(false);
// 绑定表单
const activityFormRef = ref<FormInstance | null>(null);

// 活动总数
const totalActivityCount = computed(() => activityList.value.length);
// 已批准活动数
const approvedActivityCount = computed(
  () =>
    activityList.value.filter((activity) => activity.status === "已批准").length
);
// 待审批活动数
const pendingActivityCount = computed(
  () =>
    activityList.value.filter((activity) => activity.status === "待审批").length
);

// 累计参与人数
const totalParticipants = computed(() =>
  activityList.value.reduce(
    (total, activity) => total + activity.registeredCount,
    0
  )
);
// 统计卡片数据
const cards = computed(() => [
  {
    title: "活动总数",
    value: totalActivityCount.value,
  },
  {
    title: "已批准",
    value: approvedActivityCount.value,
  },
  {
    title: "待审批",
    value: pendingActivityCount.value,
  },
  {
    title: "累计参与人数",
    value: totalParticipants.value,
  },
]);

// 筛选卡片数据
const filterCards = [
  { label: "全部状态", value: "全部" },
  { label: "待审批", value: "待审批" },
  { label: "已批准", value: "已批准" },
  { label: "已拒绝", value: "已拒绝" },
  { label: "草稿", value: "草稿" },
];

// 活动列表
const activityList = ref<ActivityItem[]>([]);
// 筛选后的活动列表（核心：根据搜索关键词+状态筛选）
const filteredActivityList = computed(() => {
  // 1. 先处理原始列表，确保有数据
  let list = [...activityList.value];
  // 2. 搜索关键词过滤（模糊匹配活动名称）
  if (searchQuery.value) {
    // 获取关键词
    const keyword = searchQuery.value.trim().toLowerCase();
    list = list.filter((activity) =>
      activity.title.toLowerCase().includes(keyword)
    );
  }

  // 3. 状态筛选（排除"全部状态"）
  if (statusFilter.value && statusFilter.value !== "全部") {
    list = list.filter((activity) => activity.status === statusFilter.value);
  }

  return list;
});

const clearFilter = () => {
  statusFilter.value = "全部";
  searchQuery.value = "";
};

onMounted(async () => {
  fetchActivityList();
});

const fetchActivityList = async () => {
  const result = await activityStore.getActivityList();
  activityList.value = result.data.list;
  console.log("活动列表", activityList.value);
};
// 活动表单
const activityForm = ref<ActivityForm>({
  // 活动名称
  title: "",
  // 分类ID
  category_id: 1,
  // 活动描述
  description: "",
  // 活动图片
  poster_url: "",
  // 活动地点
  location: "",
  // 活动开始时间
  start_time: "",
  // 活动结束时间
  end_time: "",
  // 活动报名人数限制
  max_participants: 100,
  // 活动积分
  points: 0.1,
  // 报名截止时间
  registration_deadline: "",
});

// 活动表单验证规则
const activityRules = ref<FormRules>({
  // 活动名称
  title: [
    { required: true, message: "请输入活动名称", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "活动名称长度应在2-50个字符之间",
      trigger: "blur",
    },
  ],
  // 分类ID
  category_id: [
    { required: true, message: "请选择活动类别", trigger: "change" },
  ],
  // 活动描述
  description: [
    { required: true, message: "请输入活动描述", trigger: "blur" },
    {
      min: 10,
      max: 500,
      message: "活动描述长度应在10-500个字符之间",
      trigger: "blur",
    },
  ],
  // 活动图片
  poster_url: [
    { required: true, message: "请上传活动海报", trigger: "change" },
    { type: "url", message: "请输入有效的URL地址", trigger: "blur" },
  ],
  // 活动地点
  location: [
    { required: true, message: "请输入活动地点", trigger: "blur" },
    {
      min: 2,
      max: 100,
      message: "活动地点长度应在2-100个字符之间",
      trigger: "blur",
    },
  ],
  // 活动开始时间
  start_time: [
    { required: true, message: "请选择活动开始时间", trigger: "change" },
  ],
  // 活动结束时间
  end_time: [
    { required: true, message: "请选择活动结束时间", trigger: "change" },
  ],
  // 活动报名人数限制
  max_participants: [
    { required: true, message: "报名人数至少为1人", trigger: "blur" },
  ],
  // 活动积分
  points: [{ required: true, message: "积分不能小于0.1", trigger: "blur" }],
  // 报名截止时间
  registration_deadline: [
    { required: true, message: "请选择报名截止时间", trigger: "change" },
  ],
});

// 关闭活动弹窗
const handleClose = () => {
  ElMessageBox.confirm("确认关闭吗？", "提示", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("关闭成功");
      showCreateActivity.value = false;
    })
    .catch(() => {
      ElMessage.info("已取消");
    });
};
// 活动类别
const categoryOptions = [
  { category_id: 1, name: "讲座论坛" },
  { category_id: 2, name: "文艺演出" },
  { category_id: 3, name: "体育竞赛" },
  { category_id: 4, name: "志愿服务" },
  { category_id: 5, name: "技能培训" },
  { category_id: 6, name: "展览展示" },
];
// 活动开始时间禁用
const disabledStartDate = (time: Date) => {
  return time.getTime() < Date.now();
};
// 活动结束时间禁用
const disabledEndDate = (time: Date) => {
  return time.getTime() < new Date(activityForm.value.start_time).getTime();
};

// 创建活动
const handleCreate = async () => {
  // 检测是否绑定
  if (!activityFormRef.value) return;
  // 验证表单
  try {
    await activityFormRef.value.validate();

    const submitData = {
      ...activityForm.value,
      start_time: formatIsoTime(activityForm.value.start_time),
      end_time: formatIsoTime(activityForm.value.end_time),
      registration_deadline: formatIsoTime(
        activityForm.value.registration_deadline
      ),
    };

    await activityStore.createActivity(submitData);
    // console.log("返回的数据", response);
    // console.log("返回的数据code", response.code);
    ElMessage.success("创建活动成功");
    showCreateActivity.value = false;
    // 重新获取活动列表
    await fetchActivityList();
  } catch (error) {
    ElMessage.error("创建活动失败");
  }
};
// 取消创建活动
const handleCancel = async () => {
  ElMessageBox.confirm("确认取消吗？你的修改将不会保存！", "提示", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("取消成功");
      showCreateActivity.value = false;
    })
    .catch(() => {
      ElMessage.info("已取消");
    });
};

// 编辑活动
const editorActivity = ref(false);
// 显示编辑弹窗
const handleEdit = async (id: number) => {
  // 激活可编辑
  // 显示创建活动弹窗
  showCreateActivity.value = true;
  editorActivity.value = true;
  const response = await activityStore.getActivityDetail(id);
  const result = response.data;
  console.log("活动详情", result);
  if (result) {
    activityForm.value = {
      activity_id: result.activityId,
      category_id: result.categoryId,
      start_time: result.startTime,
      end_time: result.endTime,
      registration_deadline: result.registrationDeadline,
      max_participants: result.maxParticipants,
      points: result.points,
      poster_url: result.posterUrl,
      location: result.location,
      title: result.title,
      description: result.description,
    };
  }
  console.log("编辑活动数据", activityForm.value);
};
// 提交更新编辑
const handleSubmitEdit = async (id: number) => {
  try {
    const submitEditorData = {
      title: activityForm.value.title,
      description: activityForm.value.description,
      location: activityForm.value.location,
    };
    console.log("提交编辑数据", submitEditorData);

    await activityStore.updateActivity(id, submitEditorData);
    ElMessage.success("更新活动成功");
    showCreateActivity.value = false;
    editorActivity.value = false;
    // 重新获取活动列表
    await fetchActivityList();
  } catch (error) {
    ElMessage.error("更新活动失败");
  }
};
const handleSubmitActivity = async (id: number) => {
  try {
    await activityStore.submitActivity(id);
    ElMessage.success("提交活动审批成功");
    // 获取活动列表
    await fetchActivityList();
  } catch (error) {
    ElMessage.error("提交活动审批失败");
  }
};

// 跳转活动详情
const goActivityDetail = (id: number) => {
  router.push({ path: `/activity/${id}` });
};
</script>
<style scoped>
.activity-container {
  padding: 20px 40px;
}
.activity-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.activity-list-card {
  flex: 1;
}

.search-filters {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}
.status-filter {
  width: 150px;
}

.create-activity {
  padding: 4px 10px;
  color: #fff;
  border-radius: 4px;
  background: linear-gradient(135deg, #00c9a7, #ffd166);
  border: none;
  box-shadow: 0 2px 8px rgba(0, 201, 167, 0.3);
  transition: all 0.3s;
  cursor: pointer;
}
.create-activity:hover {
  background: linear-gradient(135deg, #00a88c, #e6bc5c);
  box-shadow: 0 4px 12px rgba(0, 201, 167, 0.4);
}
.create-activity:active {
  background: linear-gradient(135deg, #008c6b, #e6b04c);
  box-shadow: 0 2px 8px rgba(0, 201, 167, 0.5);
}

/* 清空筛选 */
.filter-select {
  padding: 4px 10px;
  color: #fff;
  border-radius: 4px;
  background: linear-gradient(135deg, #ffd166, #ffa500);
  border: none;
  box-shadow: 0 2px 8px rgba(255, 209, 102, 0.3);
  transition: all 0.3s;
  cursor: pointer;
}
.filter-select:hover {
  background: linear-gradient(135deg, #e6bc5c, #e69500);
  box-shadow: 0 4px 12px rgba(255, 209, 102, 0.4);
}
.filter-select:active {
  background: linear-gradient(135deg, #e6bc5c, #e69500);
  box-shadow: 0 4px 12px rgba(255, 209, 102, 0.5);
}
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.empty-message {
  text-align: center;
  padding: 60px 0;
  color: #909399;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  background-color: #fafafa;
  border-radius: 8px;
  transition: all 0.3s;
  cursor: pointer;
}

.activity-item:hover {
  background-color: #f5f5f5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.activity-info {
  flex: 1;
  margin-right: 20px;
}
.activity-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.activity-name {
  color: #303133;
  margin: 0;
  font-size: 18px;
}
.activity-meta {
  display: flex;
  gap: 20px;
}
.meta-item {
  color: #909399;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}
.submit-activity {
  display: flex;
  align-items: center;
  justify-content: center;
}
.activity-actions {
  display: flex;
  gap: 10px;
}

[data-theme="dark"] .activity-list-card {
  background-color: #303133;
  border: none;
  box-shadow: 0 2px 12px 0 #ffffff40;
}
[data-theme="dark"] .activity-item {
  background-color: #303133;
}
[data-theme="dark"] .activity-item:hover {
  background-color: #282828;
}
[data-theme="dark"] .activity-name {
  color: #ffffff;
}
</style>

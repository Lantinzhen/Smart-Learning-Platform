<template>
  <div class="activity-container">
    <div class="activity-header">
      <h1>社团活动管理</h1>
    </div>

    <div class="activity-content">
      <!-- 活动列表 -->
      <el-card class="activity-list-card">
        <div class="search-filters">
          <!-- 搜索栏 -->
          <el-input v-model="searchQuery" placeholder="搜索活动" prefix-icon="el-icon-search" class="search-input" />

          <!-- 活动分类管理 -->
          <button class="create-activity" @click="showCategoryDialog = true">活动分类管理</button>
        </div>

        <div class="activity-list">
          <!-- 活动列表 -空白占位 -->
          <template v-if="activityList.length === 0">
            <div class="empty-message">暂无活动数据</div>
          </template>
          <!-- 活动列表 -数据项 -->
          <template v-else>
            <div v-for="activity in activityList" :key="activity.activityId" class="activity-item">
              <!-- 活动信息 -->
              <div class="activity-info">
                <div class="activity-header">
                  <!-- 活动名称 -->
                  <h4 class="activity-name">{{ activity.title }}</h4>
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
                  <span class="meta-item" v-if="activity.clubName">
                    <el-icon>
                      <User />
                    </el-icon> {{ activity.clubName }}</span>
                </div>
              </div>
              <!-- 活动操作按钮 -->
              <div class="activity-actions">
                <el-button size="small" type="success" @click.stop="handleApprove(activity.activityId)">批准</el-button>
                <el-button size="small" type="danger" @click.stop="handleReject(activity.activityId)">拒绝</el-button>
                <el-button size="small" type="primary"
                  @click.stop="viewActivityDetail(activity.activityId)">查看详情</el-button>
              </div>
            </div>
          </template>
        </div>
        <!-- 分页组件 -->
        <div class="pagination-container" v-if="total > 0">
          <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
            :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
        </div>
      </el-card>
      <!-- 审批弹窗 -->
      <el-dialog v-model="showApprovalDialog" title="活动审批" width="600px">
        <el-form ref="approvalFormRef" :model="approvalForm" :rules="approvalRules" label-width="100px">
          <el-form-item label="审批状态" prop="status">
            <el-radio-group v-model="approvalForm.status">
              <el-radio label="已批准">批准</el-radio>
              <el-radio label="已拒绝">拒绝</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审批意见" prop="comments">
            <el-input v-model="approvalForm.comments" type="textarea" :rows="4" placeholder="请输入审批意见" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showApprovalDialog = false">取消</el-button>
          <el-button type="primary" @click="submitApproval">确认</el-button>
        </template>
      </el-dialog>
      <!-- 活动详情弹窗 -->
      <el-dialog v-model="showDetailDialog" title="活动详情" width="800px">
        <div v-if="activityDetail" class="activity-detail">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="活动名称">{{ activityDetail.title }}</el-descriptions-item>
            <el-descriptions-item label="社团名称">{{ activityDetail.clubName }}</el-descriptions-item>
            <el-descriptions-item label="活动分类">{{ activityDetail.categoryName }}</el-descriptions-item>
            <el-descriptions-item label="活动状态">
              <el-tag :type="getStatusTagType(activityDetail.status)">{{ activityDetail.status }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="活动地点">{{ activityDetail.location }}</el-descriptions-item>
            <el-descriptions-item label="最大人数">{{ activityDetail.maxParticipants }}</el-descriptions-item>
            <el-descriptions-item label="活动积分">{{ activityDetail.points }}</el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ formatIsoTime(activityDetail.startTime) }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ formatIsoTime(activityDetail.endTime) }}</el-descriptions-item>
            <el-descriptions-item label="报名截止">{{ formatIsoTime(activityDetail.registrationDeadline)
              }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatIsoTime(activityDetail.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="活动描述" :span="2">
              <div style="white-space: pre-wrap;">{{ activityDetail.description }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="海报" :span="2" v-if="activityDetail.posterUrl">
              <el-image :src="activityDetail.posterUrl" style="max-width: 200px; max-height: 200px;" />
            </el-descriptions-item>
            <el-descriptions-item label="审批意见" :span="2" v-if="activityDetail.comments">
              {{ activityDetail.comments }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-dialog>
      <!-- 活动分类管理弹窗 -->
      <el-dialog v-model="showCategoryDialog" title="活动分类管理" width="700px" @open="handleCategoryDialogOpen">
        <div style="margin-bottom: 20px;">
          <el-button type="primary" @click="handleAddCategory">+ 添加分类</el-button>
        </div>
        <el-table :data="categoryList" border v-loading="categoryLoading" empty-text="暂无分类数据" style="width: 100%">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="category_id" label="分类ID" width="100" align="center">
            <template #default="scope">
              {{ scope.row.category_id || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="name" label="分类名称" min-width="120" show-overflow-tooltip />
          <el-table-column prop="description" label="分类描述" min-width="200" show-overflow-tooltip />
          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template #default="scope">
              <el-button size="small" type="primary" @click="editCategory(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>
      <!-- 添加/编辑分类弹窗 -->
      <el-dialog v-model="showAddCategory" :title="editingCategory ? '编辑分类' : '添加分类'" width="500px"
        @close="handleCategoryFormClose">
        <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="100px">
          <el-form-item label="分类ID" v-if="editingCategory">
            <el-input v-model="categoryForm.category_id" disabled />
          </el-form-item>
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="categoryForm.name" placeholder="请输入分类名称" maxlength="50" show-word-limit />
          </el-form-item>
          <el-form-item label="分类描述" prop="description">
            <el-input v-model="categoryForm.description" type="textarea" :rows="4" placeholder="请输入分类描述" maxlength="200"
              show-word-limit />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="handleCategoryFormClose">取消</el-button>
          <el-button type="primary" @click="submitCategory" :loading="categorySubmitting">确认</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { useAdminActivityStore } from "../../../stores/admin/activity";
import type { ActivityApprovalDetail, ActivityApprovalForm } from "../../../types/activity";
import { formatIsoTime } from "../../../utils/format";
import { getStatusTagType } from "../../../utils/map";
import { Calendar, DeleteLocation } from "@element-plus/icons";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage } from "element-plus";
import DataCard from "../../../components/DataCard.vue";

// 获取管理员活动store
const adminActivityStore = useAdminActivityStore();
// 搜索栏
const searchQuery = ref("");

// 活动总数
const totalActivityCount = computed(() => total.value);
// 已批准活动数（从当前页数据计算，实际应该从后端获取）
const approvedActivityCount = computed(
  () =>
    activityList.value.filter((activity) => activity.status === "已批准").length
);
// 待审批活动数
const pendingActivityCount = computed(
  () =>
    activityList.value.filter((activity) => activity.status === "待审批").length
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
    title: "当前页活动数",
    value: activityList.value.length,
  },
]);

// 活动列表
interface PendingActivityItem {
  activityId: number;
  clubId: number;
  title: string;
  clubName: string;
  categoryName: string;
  startTime: string;
  endTime: string;
  location: string;
  createdBy: string;
  createdAt: string;
  status: "已批准" | "待审批" | "已拒绝" | "已取消" | "草稿";
}

const activityList = ref<PendingActivityItem[]>([]);

const clearFilter = () => {
  searchQuery.value = "";
  currentPage.value = 1;
  fetchActivityList();
};

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
  fetchActivityList();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchActivityList();
};

onMounted(async () => {
  await fetchActivityList();
  await fetchCategoryList();
});

// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const fetchActivityList = async () => {
  try {
    const result = await adminActivityStore.getPendingApprovalActivities({
      page: currentPage.value,
      page_size: pageSize.value,
      keyword: searchQuery.value || undefined,
    });
    console.log("待审批活动列表 result", result);
    activityList.value = result.list || [];
    total.value = result.total || 0;
    console.log("待审批活动列表", activityList.value);
  } catch (error) {
    console.error("获取活动列表失败", error);
    ElMessage.error("获取活动列表失败");
  }
};

// 监听搜索关键词变化，延迟搜索
let searchTimer: ReturnType<typeof setTimeout> | null = null;
watch(searchQuery, () => {
  if (searchTimer) clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    currentPage.value = 1;
    fetchActivityList();
  }, 500);
});

// 活动类别
interface CategoryItem {
  category_id?: number;
  name: string;
  description: string;
}

const categoryOptions = ref<Array<{ category_id: number; name: string }>>([]);
const categoryList = ref<CategoryItem[]>([]);

// 获取活动分类列表
const fetchCategoryList = async () => {
  try {
    categoryLoading.value = true;
    const result = await adminActivityStore.getActivityCategories();
    console.log("获取活动分类结果", result);

    // 处理返回的数据结构
    const list = result.list

    categoryList.value = list;
    categoryOptions.value = list.map((item: CategoryItem) => ({
      category_id: item.category_id || 0,
      name: item.name,
    }));

    console.log("活动分类列表", categoryList.value);
    if (list.length === 0) {
      console.warn("活动分类列表为空");
    }
  } catch (error: unknown) {
    console.error("获取活动分类失败", error);
    const errorMessage = (error as { response?: { data?: { message?: string } }; message?: string })?.response?.data?.message
      || (error as { message?: string })?.message
      || "获取活动分类失败";
    ElMessage.error(errorMessage);
    // 发生错误时清空列表
    categoryList.value = [];
    categoryOptions.value = [];
  } finally {
    categoryLoading.value = false;
  }
};

// 审批相关
const showApprovalDialog = ref(false);
const approvalFormRef = ref<FormInstance | null>(null);
const currentActivityId = ref<number | null>(null);
const approvalForm = ref<ActivityApprovalForm>({
  status: "已批准",
  comments: "",
});

const approvalRules = ref<FormRules>({
  status: [{ required: true, message: "请选择审批状态", trigger: "change" }],
  comments: [{ required: true, message: "请输入审批意见", trigger: "blur" }],
});

// 批准活动
const handleApprove = (id: number) => {
  currentActivityId.value = id;
  approvalForm.value = {
    status: "已批准",
    comments: "",
  };
  showApprovalDialog.value = true;
};

// 拒绝活动
const handleReject = (id: number) => {
  currentActivityId.value = id;
  approvalForm.value = {
    status: "已拒绝",
    comments: "",
  };
  showApprovalDialog.value = true;
};

// 提交审批
const submitApproval = async () => {
  if (!approvalFormRef.value || !currentActivityId.value) return;
  try {
    await approvalFormRef.value.validate();
    await adminActivityStore.approveActivity(currentActivityId.value, {
      status: approvalForm.value.status,
      comments: approvalForm.value.comments,
    });
    ElMessage.success("审批成功");
    showApprovalDialog.value = false;
    await fetchActivityList();
  } catch (error) {
    console.error("审批失败", error);
  }
};

// 查看活动详情
const showDetailDialog = ref(false);
const activityDetail = ref<ActivityApprovalDetail | null>(null);

const viewActivityDetail = async (id: number) => {
  try {
    const result = await adminActivityStore.getActivityApprovalDetail(id);
    activityDetail.value = result;
    showDetailDialog.value = true;
  } catch (error) {
    console.error("获取活动详情失败", error);
    ElMessage.error("获取活动详情失败");
  }
};

// 活动分类管理
const showCategoryDialog = ref(false);
const showAddCategory = ref(false);
const categoryFormRef = ref<FormInstance | null>(null);
const editingCategory = ref(false);
const categoryLoading = ref(false);
const categorySubmitting = ref(false);
const categoryForm = ref({
  category_id: undefined as number | undefined,
  name: "",
  description: "",
});

const categoryRules = ref<FormRules>({
  name: [
    { required: true, message: "请输入分类名称", trigger: "blur" },
    { min: 2, max: 50, message: "分类名称长度应在2-50个字符之间", trigger: "blur" },
  ],
  description: [
    { required: true, message: "请输入分类描述", trigger: "blur" },
    { min: 5, max: 200, message: "分类描述长度应在5-200个字符之间", trigger: "blur" },
  ],
});

// 打开分类管理弹窗时刷新列表
const handleCategoryDialogOpen = async () => {
  await fetchCategoryList();
};

// 添加分类
const handleAddCategory = () => {
  editingCategory.value = false;
  resetCategoryForm();
  showAddCategory.value = true;
};

// 编辑分类
const editCategory = (row: CategoryItem) => {
  editingCategory.value = true;
  categoryForm.value = {
    category_id: row.category_id,
    name: row.name,
    description: row.description,
  };
  showAddCategory.value = true;
};

// 重置分类表单
const resetCategoryForm = () => {
  categoryForm.value = {
    category_id: undefined,
    name: "",
    description: "",
  };
  categoryFormRef.value?.clearValidate();
};

// 关闭分类表单弹窗
const handleCategoryFormClose = () => {
  showAddCategory.value = false;
  editingCategory.value = false;
  resetCategoryForm();
};

// 提交分类（添加或更新）
const submitCategory = async () => {
  if (!categoryFormRef.value) return;
  try {
    await categoryFormRef.value.validate();
    categorySubmitting.value = true;

    await adminActivityStore.updateActivityCategories({
      category_id: categoryForm.value.category_id,
      name: categoryForm.value.name.trim(),
      description: categoryForm.value.description.trim(),
    });

    ElMessage.success(editingCategory.value ? "更新分类成功" : "添加分类成功");
    handleCategoryFormClose();
    await fetchCategoryList();
  } catch (error: unknown) {
    console.error("操作分类失败", error);
    const errorMessage = (error as { response?: { data?: { message?: string } }; message?: string })?.response?.data?.message
      || (error as { message?: string })?.message
      || "操作失败，请重试";
    ElMessage.error(errorMessage);
  } finally {
    categorySubmitting.value = false;
  }
};
</script>
<style scoped>
/* Apple Design System 字体栈 */
.activity-container {
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.activity-container {
  padding: 24px;
}

.activity-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.5px;
  line-height: 1.2;
  margin-bottom: 32px;
}

.activity-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.activity-list-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 16px;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06),
              0 1px 2px rgba(0, 0, 0, 0.04);
  padding: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.activity-list-card:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08),
              0 2px 4px rgba(0, 0, 0, 0.06);
}

.search-filters {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 24px;
}

.search-input {
  width: 300px;
}

:deep(.search-input .el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid #d2d2d7;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.search-input .el-input__wrapper:hover) {
  border-color: #a1a1a6;
}

:deep(.search-input .el-input__wrapper.is-focus) {
  border-color: #007AFF;
  box-shadow: 0 0 0 4px rgba(0, 122, 255, 0.1);
}

.status-filter {
  width: 150px;
}

.create-activity {
  padding: 10px 20px;
  color: #ffffff;
  border-radius: 12px;
  background: #007AFF;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 122, 255, 0.3);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: -0.2px;
}

.create-activity:hover {
  background: #0051d5;
  box-shadow: 0 4px 16px rgba(0, 122, 255, 0.4);
  transform: translateY(-1px);
}

.create-activity:active {
  background: #0040aa;
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(0, 122, 255, 0.3);
}

.filter-select {
  padding: 10px 20px;
  color: #ffffff;
  border-radius: 12px;
  background: #007AFF;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 122, 255, 0.3);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
}

.filter-select:hover {
  background: #0051d5;
  box-shadow: 0 4px 16px rgba(0, 122, 255, 0.4);
  transform: translateY(-1px);
}

.filter-select:active {
  background: #0040aa;
  transform: translateY(0);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty-message {
  text-align: center;
  padding: 60px 0;
  color: #86868b;
  font-size: 15px;
  font-weight: 400;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.activity-item:hover {
  background-color: rgba(0, 122, 255, 0.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

.activity-info {
  flex: 1;
  margin-right: 20px;
}

.activity-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.activity-name {
  color: #1d1d1f;
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: -0.3px;
}

.activity-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  color: #86868b;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.submit-activity {
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.activity-detail {
  padding: 20px 0;
}

/* 按钮样式 - Apple风格 */
:deep(.el-button) {
  border-radius: 10px;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-button--small) {
  padding: 8px 16px;
  font-size: 14px;
}

/* 对话框样式 - Apple风格 */
:deep(.el-dialog) {
  border-radius: 16px;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

:deep(.el-dialog__header) {
  padding: 24px 24px 20px;
}

:deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.3px;
}

/* 暗色模式 */
[data-theme="dark"] .activity-container {
  background: #000000;
}

[data-theme="dark"] .activity-header h1 {
  color: #f5f5f7;
}

[data-theme="dark"] .activity-list-card {
  background: rgba(28, 28, 30, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border: 0.5px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .activity-item {
  background: rgba(28, 28, 30, 0.6);
  border: 0.5px solid rgba(255, 255, 255, 0.1);
}

[data-theme="dark"] .activity-item:hover {
  background: rgba(10, 132, 255, 0.15);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

[data-theme="dark"] .activity-name {
  color: #f5f5f7;
}

[data-theme="dark"] .meta-item {
  color: #86868b;
}

[data-theme="dark"] .empty-message {
  color: #86868b;
}

[data-theme="dark"] .create-activity {
  background: #0a84ff;
}

[data-theme="dark"] .create-activity:hover {
  background: #0051d5;
}

[data-theme="dark"] :deep(.el-dialog) {
  background: rgba(28, 28, 30, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border: 0.5px solid rgba(255, 255, 255, 0.1);
}

[data-theme="dark"] :deep(.el-dialog__title) {
  color: #f5f5f7;
}
</style>

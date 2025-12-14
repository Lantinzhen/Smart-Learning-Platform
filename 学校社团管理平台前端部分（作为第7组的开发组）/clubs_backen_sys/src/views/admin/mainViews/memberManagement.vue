<template>
  <div class="member-container">
    <div class="member-header">
      <h1>用户管理</h1>
    </div>
    <!-- 数据卡片统计 -->
    <!-- <div class="stats-cards">
      单独小卡片
      <el-card class="stat-card"
               v-for="card in cards"
               :key="card.title">
        <div class="stat-content">
          <div class="stat-icon">
            <el-avatar :size="40"
                       style="background: rgba(255,255,255,0.2)" />
          </div>
          <div class="stat-info">
            <p class="stat-label">{{card.title}}</p>
            <p class="stat-value">{{card.value}}</p>
          </div>
        </div>
      </el-card>
    </div> -->

    <!-- <DataCard :cards="cards" /> -->
    <!-- 成员列表 -->
    <el-card>
      <!-- 筛选项 -->
      <div class="search-filters">
        <!-- 搜索栏 -->
        <el-input v-model="searchQuery" placeholder="搜索学生" prefix-icon="el-icon-search" class="search-input" />

        <!-- 筛选栏 -->
        <!-- <el-select v-model="filterQuery" placeholder="全部学生" class="status-filter">
          <el-option v-for="item in filterOptions" :key="item.label" :label="item.label" :value="item.value" />
        </el-select> -->

        <el-button type="primary" @click="addStudent">添加成员</el-button>
      </div>
      <el-table v-loading="loading" :data="filteredMemberList">
        <el-table-column prop="studentId" label="学号">
          <template #default="scope">
            {{ scope.row.student_id }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名">
          <template #default="scope">
            <el-tag v-if="scope.row.role === '社长'" type="danger">
              {{ scope.row.name }}
            </el-tag>
            <el-tag v-else-if="scope.row.role === '副社长'" type="success">
              {{ scope.row.name }}
            </el-tag>
            <el-tag v-else type="primary">
              {{ scope.row.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱">
          <template #default="scope">
            {{ scope.row.email }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话">
          <template #default="scope">
            {{ scope.row.phone }}
          </template>
        </el-table-column>
        <el-table-column prop="major" label="专业">
          <template #default="scope">
            {{ scope.row.major }}
          </template>
        </el-table-column>
        <el-table-column prop="major" label="专业">
          <template #default="scope">
            {{ scope.row.major }}
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="年级">
          <template #default="scope">
            {{ scope.row.grade }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="140">
          <template #default="scope">
            <el-button type="success" @click="updateStudent(scope.row.student_id)">更新资料</el-button>
            <el-button type="danger" @click="removeMember(scope.row.student_id)">移除学生</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[1, 2, 5, 10]"
        @size-change="handleSizeChange" @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next, jumper" :total="total">

      </el-pagination>
    </el-card>
    <!-- 修改角色身份弹窗 -->
    <el-drawer v-model="showChangeRoleDialog" title="修改角色身份" size="40%" direction="rtl" :before-close="handleClose">
      <el-form ref="memberFormRef" :model="memberForm">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="memberForm.student_id" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="memberForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="memberForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="memberForm.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="memberForm.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="memberForm.grade" placeholder="请输入年级" />
        </el-form-item>
        <div class="change-actions">
          <el-button type="success" @click="confirmChangeRole()">确认更改</el-button>
          <el-button type="danger" @click="handleClose">取消</el-button>
        </div>
      </el-form>
    </el-drawer>

    <!-- 添加用户弹窗 -->
    <el-drawer v-model="showAddStudentDialog" title="添加学生资料" size="40%" direction="rtl" :before-close="handleClose">
      <el-form :model="addStudentForm">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="addStudentForm.student_id" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="addStudentForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addStudentForm.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="addStudentForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="addStudentForm.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="addStudentForm.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="addStudentForm.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="入学年份" prop="enrollment_year">
          <el-input v-model="addStudentForm.enrollment_year" placeholder="请入入学年份" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-input v-model="addStudentForm.gender" placeholder="请输入性别" />
        </el-form-item>
        <div class="change-actions">
          <el-button type="success" @click="confirmAddStudent()">确认添加</el-button>
          <el-button type="danger" @click="handleClose">取消</el-button>
        </div>
      </el-form>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useMemberStore } from "../../../stores/member";
import { useAdminMemberStore } from "@/stores/admin/member";
import { ElMessage, ElMessageBox } from "element-plus";
import type { StudentItem, StudentProfile } from "../../../types/member";
import { formatIsoTime } from "../../../utils/format";
import { pa, tr } from "element-plus/es/locales.mjs";
import DataCard from "../../../components/DataCard.vue";
import { useDashboardStore } from "../../../stores/dashboard";

// 获取store
const adminMemberStore = useAdminMemberStore();
const dashboardStore = useDashboardStore();
// 成员列表
const memberList = ref<StudentItem[]>([]);

// 单个成员详情
const memberDetail = ref<StudentItem | null>(null);
// 单个学生表单信息
const memberForm = ref<StudentItem>({
  // 学生学号
  student_id: 0,
  // 学生名称
  name: "",
  // 学生邮箱
  email: "",
  // 学生手机号
  phone: "",
  // 学生专业
  major: "",
  // 学生年级
  grade: "",
  // 状态
  status: 1,
});
const loading = ref(false);

const addStudentForm = ref<StudentProfile>({
  // 学生学号
  student_id: 0,
  // 学生名称
  name: "",
  // 密码
  password: "123456",
  // 学生邮箱
  email: "@example.com",
  // 学生手机号
  phone: "",
  // 学生专业
  major: "",
  // 学生年级
  grade: "2022",
  // 入学年份
  enrollment_year: 2022,
  // 性别
  gender: "男",
});

// 搜索条件
const searchQuery = ref("");
// 筛选条件
const filterQuery = ref("");

// 筛选选项
const filterOptions = [
  {
    label: "全部成员",
    value: "全部成员",
  },
  {
    label: "普通成员",
    value: "普通成员",
  },
  {
    label: "副社长",
    value: "副社长",
  },
  {
    label: "社长",
    value: "社长",
  },
];
// 筛选后的成员列表
const filteredMemberList = computed(() => {
  // 获取所有成员列表
  let filteredList = [...memberList.value];

  if (searchQuery.value) {
    // 获取关键词
    const keyword = searchQuery.value.trim().toLowerCase();
    filteredList = filteredList.filter((member) =>
      member.name.toLowerCase().includes(keyword)
    );
  }
  if (filterQuery.value && filterQuery.value !== "全部成员") {
    filteredList = filteredList.filter(
      (member) => member.role === filterQuery.value
    );
  }
  // 晒去status为0，即被封禁的学生
  filteredList = filteredList.filter((member) => member.status !== 0);

  return filteredList;
});

// 获取待审核成员数量
const fetchPendingMembersCount = async () => {
  const result = await dashboardStore.getOverview();
  pendingMembersCount.value = result.data.pendingApplicationsCount;
};

// 获取成员总数
const totalMemberCount = computed(() => memberList.value.length);
// 获取活跃成员总数
const activeMemberCount = computed(
  () => memberList.value.filter((item) => item.status === 1).length
);
// 添加待审核成员数量
const pendingMembersCount = ref(0);
const cards = computed(() => [
  {
    title: "成员总数",
    value: totalMemberCount.value,
  },
  {
    title: "活跃成员",
    value: activeMemberCount.value,
  },
  {
    title: "待审核成员",
    value: pendingMembersCount.value,
  },
  {
    title: "活跃度",
    value: (activeMemberCount.value / totalMemberCount.value) * 100 + "%",
  },
]);

const roleSelect = [
  { label: "普通成员", value: "普通成员" },
  { label: "副社长", value: "副社长" },
  { label: "社长", value: "社长" },
];

// 修改成员角色弹窗
const showChangeRoleDialog = ref(false);
// 添加学生弹窗
const showAddStudentDialog = ref(false)

onMounted(async () => {
  // 获取成员列表
  await fetchMemberList();
  // 获取待审核成员数量
  // await fetchPendingMembersCount();
});

// 获取成员列表
const fetchMemberList = async () => {
  try {
    loading.value = true;
    const result = await adminMemberStore.getStudentList(
      currentPage.value,
      pageSize.value
    );
    memberList.value = result.data.list;
    total.value = result.data.total;
    console.log("成员列表", memberList.value);
  } catch (error) {
    ElMessage.error("获取成员列表失败");
  } finally {
    loading.value = false;
  }
};

// 更新学生资料
const updateStudent = (id: number) => {
  console.log("改变角色", id);
  showChangeRoleDialog.value = true;
  memberDetail.value =
    memberList.value.find((member: StudentItem) => member.student_id == id) || null;
  console.log("成员详情", memberDetail.value);

  if (memberDetail.value) {
    memberForm.value = { ...memberDetail.value };
  }
  console.log("修改的成员", memberDetail.value);
};

// 添加学生资料
const addStudent = async () => {
  showAddStudentDialog.value = true;
};

// 确定更新学生资料
const confirmChangeRole = async () => {
  try {
    await adminMemberStore.updateStudent(
      memberForm.value
    );
    ElMessage.success(`修改${memberForm.value.student_id}成功`);
    showChangeRoleDialog.value = false;
    await fetchMemberList();
  } catch (error) {
    ElMessage.error("修改失败");
  }
};

// 确定添加学生资料
const confirmAddStudent = async () => {
  try {
    await adminMemberStore.updateStudent(
      addStudentForm.value
    );
    ElMessage.success(`添加${addStudentForm.value.student_id}成功`);
    showAddStudentDialog.value = false;
    await fetchMemberList();
  } catch (error) {
    ElMessage.error("添加失败");
  }
};

// 关闭活动弹窗
const handleClose = () => {
  ElMessageBox.confirm("确认关闭吗？", "提示", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("关闭成功");
      showChangeRoleDialog.value = false;
      showAddStudentDialog.value = false;
    })
    .catch(() => {
      ElMessage.info("已取消");
    })
    .finally(() => {
      loading.value = false;
    });
};

// 移除成员
const removeMember = async (id: number) => {
  try {
    await ElMessageBox.confirm("确定要移除该成员吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await adminMemberStore.deleteStudent(id);
    ElMessage.success("移除成员成功");
    // 重新获取成员列表
    await fetchMemberList();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("移除成员失败");
    }
  }
};

// 分页
// 获取当前页
const currentPage = ref(1);
// 每页数量
const pageSize = ref(10);
// 总数
const total = ref(0);

// 处理每页条数变化
const handleSizeChange = (newPageSize: number) => {
  pageSize.value = newPageSize;
  // 切换每页条数后，重置到第1页并重新请求数据
  currentPage.value = 1;
  console.log("当前页,页大小", currentPage.value, pageSize.value);
  fetchMemberList();
};

// 处理页码变化
const handleCurrentChange = (newCurrentPage: number) => {
  currentPage.value = newCurrentPage;
  // 切换页码后重新请求数据
  fetchMemberList();
};
</script>
<style scoped>
/* Apple Design System 字体栈 */
.member-container {
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.member-container {
  padding: 24px;
}

.member-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.5px;
  line-height: 1.2;
  margin-bottom: 32px;
}

.change-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
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

/* 卡片样式 - Apple风格 */
:deep(.el-card) {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 16px;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06),
              0 1px 2px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-card:hover) {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08),
              0 2px 4px rgba(0, 0, 0, 0.06);
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

/* Drawer样式 - Apple风格 */
:deep(.el-drawer) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
}

:deep(.el-drawer__header) {
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.08);
  padding: 24px;
}

:deep(.el-drawer__title) {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.3px;
}

:deep(.el-drawer__body) {
  padding: 24px;
}

/* 表单样式 - Apple风格 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #1d1d1f;
  font-size: 15px;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid #d2d2d7;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-input__wrapper:hover) {
  border-color: #a1a1a6;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #007AFF;
  box-shadow: 0 0 0 4px rgba(0, 122, 255, 0.1);
}

/* 暗色模式 */
[data-theme="dark"] .member-container {
  background: #000000;
}

[data-theme="dark"] .member-header h1 {
  color: #f5f5f7;
}

[data-theme="dark"] .dashboard-section {
  background: rgba(28, 28, 30, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border: 0.5px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .section-header h3 {
  color: #f5f5f7;
  margin: 0;
  font-size: 18px;
}

[data-theme="dark"] .application-item {
  border-bottom: 0.5px solid rgba(255, 255, 255, 0.1);
}

[data-theme="dark"] .application-item:hover {
  background: rgba(10, 132, 255, 0.15);
}

[data-theme="dark"] .empty-message {
  text-align: center;
  color: #86868b;
  padding: 40px 0;
}

[data-theme="dark"] .app-name {
  color: #f5f5f7;
  margin: 0 0 5px 0;
  font-weight: 500;
}

[data-theme="dark"] .app-time {
  color: #86868b;
  font-size: 12px;
  margin: 0;
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

[data-theme="dark"] :deep(.el-drawer) {
  background: rgba(28, 28, 30, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
}

[data-theme="dark"] :deep(.el-drawer__title) {
  color: #f5f5f7;
}

[data-theme="dark"] :deep(.el-form-item__label) {
  color: #f5f5f7;
}
</style>

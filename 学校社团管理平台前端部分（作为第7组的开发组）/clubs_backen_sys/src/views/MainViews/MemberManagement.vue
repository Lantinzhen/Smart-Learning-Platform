<template>
  <div class="member-container">
    <div class="member-header">
      <h1>{{ club_name }} 成员管理</h1>
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

    <DataCard :cards="cards" />
    <!-- 成员列表 -->
    <el-card>
      <!-- 筛选项 -->
      <div class="search-filters">
        <!-- 搜索栏 -->
        <el-input v-model="searchQuery"
                  placeholder="搜索成员"
                  prefix-icon="el-icon-search"
                  class="search-input" />

        <!-- 筛选栏 -->
        <el-select v-model="filterQuery"
                   placeholder="全部成员"
                   class="status-filter">
          <el-option v-for="item in filterOptions"
                     :key="item.label"
                     :label="item.label"
                     :value="item.value" />
        </el-select>

      </div>
      <el-table v-loading="loading"
                :data="filteredMemberList">
        <el-table-column prop="id"
                         label="序号"
                         width="100">
        </el-table-column>

        <el-table-column prop="name"
                         label="姓名">
          <template #default="scope">
            <el-tag v-if="scope.row.role === '社长'"
                    type="danger">
              {{scope.row.name}}
            </el-tag>
            <el-tag v-else-if="scope.row.role === '副社长'"
                    type="success">
              {{scope.row.name}}
            </el-tag>
            <el-tag v-else
                    type="primary">
              {{scope.row.name}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="studentId"
                         label="学号">
        </el-table-column>
        <el-table-column prop="major"
                         label="专业">
        </el-table-column>
        <el-table-column prop="joinDate"
                         label="加入时间">
          <template #default="scope">
            {{ formatIsoTime(scope.row.joinDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作"
                         fixed="right"
                         min-width="140">
          <template #default="scope">
            <el-button type="success"
                       @click="changeRole(scope.row.id)">更改角色身份</el-button>
            <el-button type="danger"
                       @click="removeMember(scope.row.id)">移除角色</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :current-page="currentPage"
                     :page-size="pageSize"
                     :page-sizes="[1, 2, 5, 10]"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"
                     layout="total, sizes, prev, pager, next, jumper"
                     :total="total">

      </el-pagination>
    </el-card>
    <!-- 修改角色身份弹窗 -->
    <el-drawer v-model="showChangeRoleDialog"
               title="修改角色身份"
               size="40%"
               direction="rtl"
               :before-close="handleClose">
      <el-form ref="memberFormRef"
               :model="memberForm">
        <el-form-item label="姓名"
                      prop="name">
          <el-input v-model="memberForm.name"
                    placeholder="请输入姓名"
                    disabled />
        </el-form-item>
        <el-form-item label="身份"
                      prop="role">
          <el-select v-model="memberForm.role"
                     placeholder="请选择身份">
            <el-option v-for="role in roleSelect"
                       :key="role.label"
                       :label="role.label"
                       :value="role.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年级"
                      prop="grade">
          <el-input v-model="memberForm.grade"
                    placeholder="请输入年级"
                    disabled />
        </el-form-item>
        <el-form-item label="学号"
                      prop="studentId">
          <el-input v-model="memberForm.studentId"
                    placeholder="请输入学号"
                    disabled />
        </el-form-item>
        <el-form-item label="专业"
                      prop="major">
          <el-input v-model="memberForm.major"
                    placeholder="请输入专业"
                    disabled />
        </el-form-item>
        <el-form-item label="加入时间"
                      prop="joinDate">
          <el-date-picker v-model="memberForm.joinDate"
                          type="date"
                          placeholder="选择加入时间"
                          style="width: 100%"
                          disabled />
        </el-form-item>
        <div class="change-actions">
          <el-button type="success"
                     @click="confirmChangeRole()">确认更改</el-button>
          <el-button type="danger"
                     @click="handleClose">取消</el-button>
        </div>
      </el-form>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useMemberStore } from "../../stores/member";
import { ElMessage, ElMessageBox } from "element-plus";
import type { MemberItem } from "../../types/member";
import { formatIsoTime } from "../../utils/format";
import { pa, tr } from "element-plus/es/locales.mjs";
import DataCard from "../../components/DataCard.vue";
import { useDashboardStore } from "../../stores/dashboard";
const club_name = localStorage.getItem("club_name");

// 获取store
const memberStore = useMemberStore();
const dashboardStore = useDashboardStore();
// 成员列表
const memberList = ref<MemberItem[]>([]);
// 单个成员详情
const memberDetail = ref<MemberItem | null>(null);
// 单个成员表单信息
const memberForm = ref<MemberItem>({
  // 成员ID
  id: 0,
  // 成员名称
  name: "",
  // 成员学号
  studentId: 0,
  // 成员年级
  grade: "",
  // 成员专业
  major: "",
  // 成员角色
  role: "",
  // 加入时间
  joinDate: "",
  // 状态
  status: 0,
});
const loading = ref(false);

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

onMounted(async () => {
  // 获取成员列表
  await fetchMemberList();
  // 获取待审核成员数量
  await fetchPendingMembersCount();
});

// 获取成员列表
const fetchMemberList = async () => {
  try {
    loading.value = true;
    const result = await memberStore.getMemberList(
      currentPage.value,
      pageSize.value
    );
    memberList.value = result.data.content;
    total.value = result.data.totalElements;
    console.log("成员列表", memberList.value);
  } catch (error) {
    ElMessage.error("获取成员列表失败");
  } finally {
    loading.value = false;
  }
};

// 改变角色
const changeRole = (id: number) => {
  console.log("改变角色", id);
  showChangeRoleDialog.value = true;
  memberDetail.value =
    memberList.value.find((member: MemberItem) => member.id == id) || null;
  console.log("成员详情", memberDetail.value);

  if (memberDetail.value) {
    memberForm.value = { ...memberDetail.value };
  }
  console.log("修改的成员", memberDetail.value);
};
// 确定修改角色
const confirmChangeRole = async () => {
  try {
    await memberStore.updateMemberRole(
      memberForm.value.id,
      memberForm.value.role
    );
    ElMessage.success(`修改${memberForm.value.id}成功`);
    showChangeRoleDialog.value = false;
    await fetchMemberList();
  } catch (error) {
    ElMessage.error("修改失败");
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

    await memberStore.deleteMember(id);
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
.member-container {
  padding: 20px 40px;
}

.change-actions {
  display: flex;
  align-items: center;
  justify-content: center;
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
</style>

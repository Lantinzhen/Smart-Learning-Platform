<template>
  <RouterView />
</template>
<script setup lang="ts">
</script>
<style scoped>
/* 通用卡片样式优化 */
:deep(.el-card) {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  /* border: 1px solid #e6e6e6; */
}

:deep(.el-card:hover) {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 彩虹渐变按钮样式 */
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #00c9a7, #ffd166);
  border: none;
  box-shadow: 0 2px 8px rgba(0, 201, 167, 0.3);
  transition: all 0.3s;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #00a88c, #e6bc5c);
  box-shadow: 0 4px 12px rgba(0, 201, 167, 0.4);
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #06d6a0, #118ab2);
  border: none;
  box-shadow: 0 2px 8px rgba(6, 214, 160, 0.3);
  transition: all 0.3s;
}

:deep(.el-button--success:hover) {
  background: linear-gradient(135deg, #05b48a, #0e7490);
  box-shadow: 0 4px 12px rgba(6, 214, 160, 0.4);
}

:deep(.el-button--warning) {
  background: linear-gradient(135deg, #ffd166, #ffa500);
  border: none;
  box-shadow: 0 2px 8px rgba(255, 209, 102, 0.3);
  transition: all 0.3s;
}

:deep(.el-button--warning:hover) {
  background: linear-gradient(135deg, #e6bc5c, #e69500);
  box-shadow: 0 4px 12px rgba(255, 209, 102, 0.4);
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #ff6b6b, #8338ec);
  border: none;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
  transition: all 0.3s;
}

:deep(.el-button--danger:hover) {
  background: linear-gradient(135deg, #e55a5a, #722ed1);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

/* 彩虹渐变统计卡片样式 */
:deep(.stat-card:nth-child(1)) {
  background: linear-gradient(135deg, #00c9a7, #06d6a0);
  color: white;
}

:deep(.stat-card:nth-child(2)) {
  background: linear-gradient(135deg, #ff6f64, #ff9d0e);
  color: white;
}

:deep(.stat-card:nth-child(3)) {
  /* #148ab8 #3587f5 */
  background: linear-gradient(135deg, #3587f5, #118ab2);
  color: white;
}

:deep(.stat-card:nth-child(4)) {
  background: linear-gradient(135deg, #8338ec, #3a86ff);
  color: white;
}

/* 表格头部渐变样式 */
:deep(.el-table__header) {
  background: linear-gradient(90deg, #fafafa, #f0f0f0);
}

/* 标签渐变样式 */
:deep(.el-tag) {
  background: linear-gradient(135deg, #00c9a7, #ffd166);
  border: none;
  color: white;
}

:deep(.el-tag--success) {
  background: linear-gradient(135deg, #06d6a0, #118ab2);
}

:deep(.el-tag--warning) {
  background: linear-gradient(135deg, #ffd166, #ffa500);
}

:deep(.el-tag--danger) {
  background: linear-gradient(135deg, #ff6b6b, #8338ec);
}

/* 按钮间距优化 */
:deep(.el-button) {
  margin-right: 12px;
  margin-bottom: 12px;
}

:deep(.el-button:last-child) {
  margin-right: 0;
}

/* 表格样式优化 */
:deep(.el-table) {
  margin-top: 16px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

:deep(.el-table__header) {
  background-color: #fafafa;
}

:deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

/* 表单样式优化 */
:deep(.el-form) {
  margin-top: 24px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

/* 统计卡片间距优化 */
:deep(.el-row > .el-col) {
  padding: 0 12px;
  margin-bottom: 24px;
}

:deep(.el-row > .el-col:first-child) {
  padding-left: 0;
}

:deep(.el-row > .el-col:last-child) {
  padding-right: 0;
}

[data-theme="dark"] :deep(.el-card) {
  box-shadow: 0 2px 12px 0 #ffffff40;
}

[data-theme="dark"] :deep(.el-drawer) {
  background-color: #303133 !important;
}
[data-theme="dark"] :root {
 --el-bg-color: #4165d7 !important;
}
</style>

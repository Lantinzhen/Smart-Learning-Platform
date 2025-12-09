<template>
  <div class="stats-cards">
    <!-- 单独小卡片 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const props = defineProps({
  cards: {
    type: Object,
    default: () => ({}),
  },
});


</script>
<style scoped>
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}
.stat-card {
  height: 100%;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border-radius: 10px;
}
.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  margin-right: 20px;
}

.stat-label {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin: 0 0 5px 0;
}
.stat-value {
  color: #ffffff;
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}
.change-actions {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 暗色模式 */
[data-theme="dark"] .stat-card {
  box-shadow: 0 2px 12px 0 #ffffff40;
}
</style>

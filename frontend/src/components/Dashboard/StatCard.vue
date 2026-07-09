<template>
  <el-row :gutter="16" class="stat-card-container">
    <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6" v-for="item in cardData" :key="item.key">
      <div class="stat-card">
        <div class="stat-card-icon" :style="{ background: item.bgColor, color: item.iconColor }">
          <menu-icon :name="item.icon" />
        </div>
        <div class="stat-card-info">
          <div class="stat-card-title">{{ item.title }}</div>
          <div class="stat-card-value">{{ item.value }}</div>
          <div class="stat-card-footer" v-if="item.footer">{{ item.footer }}</div>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { computed } from 'vue'

defineOptions({ name: 'StatCard' })

interface StatData {
  totalUsers?: number
  todayUsers?: number
  totalRoles?: number
  onlineUsers?: number
  activeUsers?: number
  totalNotices?: number
  pendingTasks?: number
  [key: string]: number | undefined
}

const props = withDefaults(defineProps<{ data?: StatData }>(), {
  data: () => ({})
})

const cardData = computed(() => [
  {
    key: 'totalUsers',
    title: '用户总数',
    value: props.data.totalUsers || 0,
    icon: 'el-icon-user',
    bgColor: '#e6fffb',
    iconColor: '#0f9f9f',
    footer: `今日新增 ${props.data.todayUsers || 0}`
  },
  {
    key: 'totalRoles',
    title: '角色数量',
    value: props.data.totalRoles || 0,
    icon: 'el-icon-s-custom',
    bgColor: '#e6f7ff',
    iconColor: '#1890ff',
    footer: `系统角色 ${props.data.totalRoles || 0}`
  },
  {
    key: 'onlineUsers',
    title: '在线用户',
    value: props.data.onlineUsers || 0,
    icon: 'el-icon-video-play',
    bgColor: '#f6ffed',
    iconColor: '#52c41a',
    footer: `活跃用户 ${props.data.activeUsers || props.data.onlineUsers || 0}`
  },
  {
    key: 'totalNotices',
    title: '通知公告',
    value: props.data.totalNotices || 0,
    icon: 'el-icon-bell',
    bgColor: '#fff7e6',
    iconColor: '#fa8c16',
    footer: `待办 ${props.data.pendingTasks || 0}`
  }
])
</script>

<style lang="scss" scoped>
.stat-card-container {
  margin-bottom: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  padding: 20px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    border-color: #cbd5e1;
    box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  }

  .stat-card-icon {
    width: 48px;
    height: 48px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    flex-shrink: 0;

    i {
      font-size: 22px;
    }
  }

  .stat-card-info {
    flex: 1;
    min-width: 0;
  }

  .stat-card-title {
    font-size: 13px;
    color: #64748b;
    margin-bottom: 4px;
  }

  .stat-card-value {
    font-size: 28px;
    font-weight: 600;
    color: #1f2937;
    line-height: 1.2;
    margin-bottom: 4px;
    font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Arial, sans-serif;
  }

  .stat-card-footer {
    font-size: 12px;
    color: #94a3b8;
  }
}

@media (max-width: 768px) {
  .stat-card {
    padding: 16px;

    .stat-card-icon {
      width: 40px;
      height: 40px;
      margin-right: 12px;

      i { font-size: 18px; }
    }

    .stat-card-value {
      font-size: 22px;
    }
  }
}
</style>

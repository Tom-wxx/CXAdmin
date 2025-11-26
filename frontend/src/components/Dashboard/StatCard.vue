<template>
  <el-row :gutter="20" class="stat-card-container">
    <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6" v-for="item in cardData" :key="item.key">
      <div class="stat-card" :class="'stat-card-' + item.key">
        <div class="stat-card-background" :style="{ background: item.gradient }"></div>
        <div class="stat-card-content">
          <div class="stat-card-icon">
            <i :class="item.icon"></i>
          </div>
          <div class="stat-card-info">
            <div class="stat-card-title">{{ item.title }}</div>
            <div class="stat-card-value">
              <span class="value-number">{{ item.value }}</span>
              <span class="value-unit" v-if="item.unit">{{ item.unit }}</span>
            </div>
            <div class="stat-card-footer" v-if="item.footer">
              <i :class="item.trendIcon" v-if="item.trendIcon"></i>
              <span>{{ item.footer }}</span>
            </div>
          </div>
        </div>
        <div class="stat-card-decoration"></div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'StatCard',
  props: {
    data: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    cardData() {
      return [
        {
          key: 'totalUsers',
          title: '用户总数',
          value: this.data.totalUsers || 0,
          icon: 'el-icon-user',
          gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          footer: `今日新增 ${this.data.todayUsers || 0}`,
          trendIcon: this.data.todayUsers > 0 ? 'el-icon-top' : ''
        },
        {
          key: 'totalRoles',
          title: '角色数量',
          value: this.data.totalRoles || 0,
          icon: 'el-icon-s-custom',
          gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
          footer: `系统角色 ${this.data.totalRoles || 0}`
        },
        {
          key: 'onlineUsers',
          title: '在线用户',
          value: this.data.onlineUsers || 0,
          icon: 'el-icon-video-play',
          gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
          footer: `活跃用户 ${this.data.activeUsers || this.data.onlineUsers || 0}`
        },
        {
          key: 'totalNotices',
          title: '通知公告',
          value: this.data.totalNotices || 0,
          icon: 'el-icon-bell',
          gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
          footer: `待办 ${this.data.pendingTasks || 0}`,
          trendIcon: this.data.pendingTasks > 0 ? 'el-icon-warning' : ''
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.stat-card-container {
  margin-bottom: 20px;
}

.stat-card {
  position: relative;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 20px;
  overflow: hidden;
  cursor: pointer;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 24px 0 rgba(0, 0, 0, 0.15);

    .stat-card-icon {
      transform: scale(1.1) rotate(5deg);
    }

    .stat-card-decoration {
      transform: scale(1.5) rotate(180deg);
      opacity: 0.15;
    }
  }

  .stat-card-background {
    position: absolute;
    top: 0;
    right: 0;
    width: 100%;
    height: 100%;
    opacity: 0.05;
    border-radius: 12px;
  }

  .stat-card-content {
    position: relative;
    z-index: 2;
    display: flex;
    align-items: flex-start;
  }

  .stat-card-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    flex-shrink: 0;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.6));
    backdrop-filter: blur(10px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;

    i {
      font-size: 28px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  // 为每个卡片定制图标颜色
  &.stat-card-totalUsers .stat-card-icon i {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  &.stat-card-totalRoles .stat-card-icon i {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  &.stat-card-onlineUsers .stat-card-icon i {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  &.stat-card-totalNotices .stat-card-icon i {
    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .stat-card-info {
    flex: 1;
    min-width: 0;
  }

  .stat-card-title {
    font-size: 13px;
    color: #909399;
    margin-bottom: 8px;
    font-weight: 500;
    letter-spacing: 0.5px;
    text-transform: uppercase;
  }

  .stat-card-value {
    display: flex;
    align-items: baseline;
    margin-bottom: 8px;

    .value-number {
      font-size: 32px;
      font-weight: 700;
      color: #303133;
      line-height: 1;
      font-family: 'Helvetica Neue', Arial, sans-serif;
    }

    .value-unit {
      font-size: 14px;
      color: #909399;
      margin-left: 4px;
    }
  }

  .stat-card-footer {
    font-size: 12px;
    color: #606266;
    display: flex;
    align-items: center;

    i {
      margin-right: 4px;
      font-size: 14px;
      color: #67C23A;
      animation: bounce 2s infinite;
    }

    i.el-icon-warning {
      color: #E6A23C;
    }
  }

  .stat-card-decoration {
    position: absolute;
    bottom: -20px;
    right: -20px;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(64, 158, 255, 0.1) 0%, transparent 70%);
    transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
    z-index: 1;
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

@media (max-width: 768px) {
  .stat-card {
    padding: 16px;

    .stat-card-icon {
      width: 48px;
      height: 48px;
      margin-right: 12px;

      i {
        font-size: 24px;
      }
    }

    .stat-card-value .value-number {
      font-size: 24px;
    }
  }
}
</style>

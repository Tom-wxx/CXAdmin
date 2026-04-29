<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h2>数据概览</h2>
      <p>系统运行统计与数据分析</p>
    </div>

    <!-- 统计卡片 -->
    <stat-card :data="statCardData" />

    <!-- 图表区域 -->
    <el-row :gutter="16">
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <chart-card
          title="用户增长趋势"
          chart-type="line"
          :chart-data="userTrendData"
          height="320px"
        />
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <chart-card
          title="登录统计"
          chart-type="bar"
          :chart-data="loginStatsData"
          height="320px"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <chart-card
          title="部门人员分布"
          chart-type="pie"
          :chart-data="deptDistributionData"
          height="320px"
        />
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="panel">
          <div class="panel-header">
            <span class="panel-title">近期操作日志</span>
          </div>
          <div class="panel-body">
            <el-table
              :data="recentLogs"
              style="width: 100%"
              :show-header="false"
              max-height="288"
              size="small"
            >
              <el-table-column prop="operContent" label="操作" min-width="150">
                <template slot-scope="scope">
                  <div class="log-item">
                    <div class="log-content">
                      <el-tag :type="getOperTypeTag(scope.row.operType)" size="mini" effect="plain">
                        {{ scope.row.operType }}
                      </el-tag>
                      <span class="log-text">{{ scope.row.operContent }}</span>
                    </div>
                    <div class="log-meta">
                      <span>{{ scope.row.operator }}</span>
                      <span class="log-time">{{ scope.row.operTime }}</span>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="status" width="60" align="right">
                <template slot-scope="scope">
                  <el-tag
                    :type="scope.row.status === '成功' ? 'success' : 'danger'"
                    size="mini"
                    effect="plain"
                  >
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="!recentLogs || recentLogs.length === 0" class="empty-logs">
              <i class="el-icon-document"></i>
              <p>暂无操作日志</p>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷访问 -->
    <div class="panel" style="margin-top: 16px;">
      <div class="panel-header">
        <span class="panel-title">快捷访问</span>
      </div>
      <div class="panel-body">
        <el-row :gutter="16">
          <el-col :xs="12" :sm="6" :md="6" :lg="6" v-for="link in quickLinks" :key="link.path">
            <router-link :to="link.path" class="quick-link">
              <i :class="link.icon"></i>
              <span>{{ link.label }}</span>
            </router-link>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import { getDashboardData } from '@/api/dashboard'
import StatCard from '@/components/Dashboard/StatCard'
import ChartCard from '@/components/Dashboard/ChartCard'

export default {
  name: 'Dashboard',
  components: {
    StatCard,
    ChartCard
  },
  data() {
    return {
      loading: false,
      statCardData: {},
      userTrendData: { labels: [], values: [] },
      loginStatsData: { labels: [], values: [] },
      deptDistributionData: { labels: [], values: [] },
      recentLogs: [],
      quickLinks: [
        { path: '/system/user', icon: 'el-icon-user', label: '用户管理' },
        { path: '/system/role', icon: 'el-icon-s-custom', label: '角色管理' },
        { path: '/monitor/operlog', icon: 'el-icon-document', label: '操作日志' },
        { path: '/monitor/online', icon: 'el-icon-video-play', label: '在线用户' }
      ]
    }
  },
  created() {
    this.loadDashboardData()
  },
  methods: {
    async loadDashboardData() {
      this.loading = true
      try {
        const response = await getDashboardData()
        if (response.code === 200 && response.data) {
          const data = response.data
          this.statCardData = data.statCard || {}
          this.userTrendData = data.userTrend || { labels: [], values: [] }
          this.loginStatsData = data.loginStats || { labels: [], values: [] }
          this.deptDistributionData = data.deptDistribution || { labels: [], values: [] }
          this.recentLogs = data.recentLogs || []
        }
      } catch (_) {
        this.$message.error('加载仪表板数据失败')
      } finally {
        this.loading = false
      }
    },
    getOperTypeTag(operType) {
      const typeMap = {
        '新增': 'success',
        '修改': 'primary',
        '删除': 'danger',
        '授权': 'warning',
        '导出': 'info',
        '导入': 'info',
        '强退': 'danger',
        '清空': 'warning',
        '其他': 'info'
      }
      return typeMap[operType] || 'info'
    }
  }
}
</script>

<style lang="scss" scoped>
$primary: #13c2c2;

.dashboard-container {
  .dashboard-header {
    margin-bottom: 16px;

    h2 {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 4px;
    }

    p {
      font-size: 13px;
      color: #8c8c8c;
      margin: 0;
    }
  }

  .panel {
    background: #fff;
    border-radius: 4px;
    border: 1px solid #e8e8e8;
    margin-bottom: 16px;

    .panel-header {
      padding: 12px 16px;
      border-bottom: 1px solid #f0f0f0;

      .panel-title {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
      }
    }

    .panel-body {
      padding: 16px;
    }
  }

  .log-item {
    padding: 8px 0;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    .log-content {
      display: flex;
      align-items: center;
      margin-bottom: 4px;

      .log-text {
        margin-left: 8px;
        font-size: 13px;
        color: #303133;
      }
    }

    .log-meta {
      font-size: 12px;
      color: #bfbfbf;
      padding-left: 4px;

      .log-time {
        margin-left: 16px;
      }
    }
  }

  .empty-logs {
    text-align: center;
    padding: 48px 0;
    color: #bfbfbf;

    i {
      font-size: 40px;
      margin-bottom: 12px;
    }

    p {
      font-size: 13px;
      margin: 0;
    }
  }

  .quick-link {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background: #fafafa;
    border-radius: 4px;
    text-decoration: none;
    margin-bottom: 12px;
    border: 1px solid transparent;
    transition: all 0.2s;

    &:hover {
      border-color: $primary;
      background: #e6fffb;

      i, span {
        color: $primary;
      }
    }

    i {
      font-size: 18px;
      color: #8c8c8c;
      margin-right: 10px;
      transition: color 0.2s;
    }

    span {
      font-size: 14px;
      color: #595959;
      transition: color 0.2s;
    }
  }
}
</style>

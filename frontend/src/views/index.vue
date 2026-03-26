<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h2>数据仪表板</h2>
      <p>系统数据概览与统计分析</p>
    </div>

    <!-- 统计卡片 -->
    <stat-card :data="statCardData" />

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <!-- 用户增长趋势 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <chart-card
          title="用户增长趋势"
          chart-type="line"
          :chart-data="userTrendData"
          height="350px"
        />
      </el-col>

      <!-- 登录统计 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <chart-card
          title="登录统计"
          chart-type="bar"
          :chart-data="loginStatsData"
          height="350px"
        />
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <!-- 部门人员分布 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <chart-card
          title="部门人员分布"
          chart-type="pie"
          :chart-data="deptDistributionData"
          height="350px"
        />
      </el-col>

      <!-- 近期操作日志 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="log-card">
          <div class="log-card-header">
            <span class="log-card-title">近期操作日志</span>
          </div>
          <div class="log-card-body">
            <el-table
              :data="recentLogs"
              style="width: 100%"
              :show-header="false"
              max-height="318"
            >
              <el-table-column prop="operContent" label="操作" min-width="150">
                <template slot-scope="scope">
                  <div class="log-item">
                    <div class="log-content">
                      <el-tag :type="getOperTypeTag(scope.row.operType)" size="mini">
                        {{ scope.row.operType }}
                      </el-tag>
                      <span class="log-text">{{ scope.row.operContent }}</span>
                    </div>
                    <div class="log-meta">
                      <span class="log-operator">{{ scope.row.operator }}</span>
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
    <el-row :gutter="20" class="quick-access-row">
      <el-col :span="24">
        <div class="quick-access-card">
          <div class="quick-access-header">
            <span class="quick-access-title">快捷访问</span>
          </div>
          <div class="quick-access-body">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <router-link to="/system/user" class="quick-link">
                  <i class="el-icon-user"></i>
                  <span>用户管理</span>
                </router-link>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <router-link to="/system/role" class="quick-link">
                  <i class="el-icon-s-custom"></i>
                  <span>角色管理</span>
                </router-link>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <router-link to="/monitor/operlog" class="quick-link">
                  <i class="el-icon-document"></i>
                  <span>操作日志</span>
                </router-link>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <router-link to="/monitor/online" class="quick-link">
                  <i class="el-icon-video-play"></i>
                  <span>在线用户</span>
                </router-link>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-col>
    </el-row>
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
      recentLogs: []
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
.dashboard-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
  min-height: calc(100vh - 84px);

  .dashboard-header {
    margin-bottom: 24px;
    padding: 20px 0;

    h2 {
      font-size: 28px;
      font-weight: 700;
      color: #303133;
      margin-bottom: 8px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    p {
      font-size: 14px;
      color: #909399;
      font-weight: 400;
    }
  }

  .log-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
    margin-bottom: 20px;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 0.12);
    }

    .log-card-header {
      padding: 20px 24px 0;
      display: flex;
      align-items: center;

      &::before {
        content: '';
        width: 4px;
        height: 18px;
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        border-radius: 2px;
        margin-right: 12px;
      }

      .log-card-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        letter-spacing: 0.3px;
      }
    }

    .log-card-body {
      padding: 20px 24px 24px;

      .log-item {
        padding: 12px 0;
        border-bottom: 1px dashed #f0f2f5;

        &:last-child {
          border-bottom: none;
        }

        .log-content {
          display: flex;
          align-items: center;
          margin-bottom: 8px;

          .log-text {
            margin-left: 8px;
            font-size: 14px;
            color: #303133;
            font-weight: 500;
          }
        }

        .log-meta {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          color: #909399;
          padding-left: 8px;

          .log-operator {
            margin-right: 10px;

            &::before {
              content: '👤 ';
            }
          }

          .log-time {
            &::before {
              content: '🕐 ';
            }
          }
        }
      }

      .empty-logs {
        text-align: center;
        padding: 60px 0;
        color: #909399;

        i {
          font-size: 56px;
          margin-bottom: 16px;
          opacity: 0.5;
        }

        p {
          font-size: 14px;
        }
      }
    }
  }

  .quick-access-row {
    margin-top: 20px;
  }

  .quick-access-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 0.12);
    }

    .quick-access-header {
      padding: 20px 24px 0;
      display: flex;
      align-items: center;

      &::before {
        content: '';
        width: 4px;
        height: 18px;
        background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        border-radius: 2px;
        margin-right: 12px;
      }

      .quick-access-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        letter-spacing: 0.3px;
      }
    }

    .quick-access-body {
      padding: 20px 24px 24px;

      .quick-link {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 28px 20px;
        background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
        border-radius: 12px;
        text-decoration: none;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        margin-bottom: 20px;
        position: relative;
        overflow: hidden;

        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          opacity: 0;
          transition: opacity 0.3s;
        }

        &:hover {
          transform: translateY(-4px) scale(1.02);
          box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);

          &::before {
            opacity: 1;
          }

          i {
            color: #fff;
            transform: scale(1.2) rotate(5deg);
          }

          span {
            color: #fff;
          }
        }

        i {
          font-size: 36px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          margin-bottom: 12px;
          transition: all 0.3s;
          position: relative;
          z-index: 1;
        }

        span {
          font-size: 14px;
          color: #303133;
          font-weight: 500;
          transition: color 0.3s;
          position: relative;
          z-index: 1;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 12px;

    .dashboard-header {
      padding: 12px 0;

      h2 {
        font-size: 22px;
      }
    }

    .log-card,
    .quick-access-card {
      .log-card-header,
      .quick-access-header {
        padding: 16px 20px 0;
      }

      .log-card-body,
      .quick-access-body {
        padding: 16px 20px 20px;
      }
    }
  }
}
</style>

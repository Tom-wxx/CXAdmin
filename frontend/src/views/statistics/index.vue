<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 系统概览卡片 -->
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <i class="el-icon-user" style="color: #409EFF;"></i>
            <div class="card-info">
              <div class="card-title">用户总数</div>
              <div class="card-value">{{ overview.totalUsers || 0 }}</div>
              <div class="card-desc">今日新增: {{ overview.todayNewUsers || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <i class="el-icon-s-custom" style="color: #67C23A;"></i>
            <div class="card-info">
              <div class="card-title">角色总数</div>
              <div class="card-value">{{ overview.totalRoles || 0 }}</div>
              <div class="card-desc">部门: {{ overview.totalDepts || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <i class="el-icon-s-promotion" style="color: #E6A23C;"></i>
            <div class="card-info">
              <div class="card-title">今日登录</div>
              <div class="card-value">{{ overview.todayLoginCount || 0 }}</div>
              <div class="card-desc">在线: {{ overview.onlineUserCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <i class="el-icon-s-operation" style="color: #F56C6C;"></i>
            <div class="card-info">
              <div class="card-title">今日操作</div>
              <div class="card-value">{{ overview.todayOperationCount || 0 }}</div>
              <div class="card-desc">未读通知: {{ overview.unreadNotifications || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 用户增长趋势 -->
      <el-col :span="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>用户增长趋势</span>
            <el-select v-model="userGrowthDays" size="small" style="float: right; width: 100px;" @change="loadUserGrowthTrend">
              <el-option label="7天" :value="7"></el-option>
              <el-option label="15天" :value="15"></el-option>
              <el-option label="30天" :value="30"></el-option>
            </el-select>
          </div>
          <div id="userGrowthChart" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 登录统计 -->
      <el-col :span="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>登录统计</span>
            <el-select v-model="loginDays" size="small" style="float: right; width: 100px;" @change="loadLoginStatistics">
              <el-option label="7天" :value="7"></el-option>
              <el-option label="15天" :value="15"></el-option>
              <el-option label="30天" :value="30"></el-option>
            </el-select>
          </div>
          <div id="loginChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 部门人员分布 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>部门人员分布</span>
          </div>
          <div id="deptDistributionChart" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 角色人员分布 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>角色人员分布</span>
          </div>
          <div id="roleDistributionChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 操作日志统计 -->
      <el-col :span="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>操作日志趋势</span>
            <el-select v-model="operationDays" size="small" style="float: right; width: 100px;" @change="loadOperationStatistics">
              <el-option label="7天" :value="7"></el-option>
              <el-option label="15天" :value="15"></el-option>
              <el-option label="30天" :value="30"></el-option>
            </el-select>
          </div>
          <div id="operationChart" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 操作类型统计 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>操作类型分布</span>
          </div>
          <div id="operationTypeChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import {
  getSystemOverview,
  getUserGrowthTrend,
  getLoginStatistics,
  getDeptUserDistribution,
  getRoleUserDistribution,
  getOperationStatistics,
  getOperationTypeStatistics
} from '@/api/statistics'

export default {
  name: 'Statistics',
  data() {
    return {
      overview: {},
      userGrowthDays: 30,
      loginDays: 30,
      operationDays: 30,
      charts: {}
    }
  },
  mounted() {
    this.loadData()
  },
  beforeDestroy() {
    // 销毁图表实例
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.dispose()
      }
    })
  },
  methods: {
    async loadData() {
      await this.loadSystemOverview()
      await this.loadUserGrowthTrend()
      await this.loadLoginStatistics()
      await this.loadDeptDistribution()
      await this.loadRoleDistribution()
      await this.loadOperationStatistics()
      await this.loadOperationTypeStatistics()
    },

    async loadSystemOverview() {
      try {
        const response = await getSystemOverview()
        this.overview = response.data
      } catch (error) {
        this.$message.error('加载系统概览失败')
      }
    },

    async loadUserGrowthTrend() {
      try {
        const response = await getUserGrowthTrend(this.userGrowthDays)
        const data = response.data

        const dates = data.map(item => item.date)
        const values = data.map(item => item.value)

        this.renderLineChart('userGrowthChart', '用户增长', dates, values, '#409EFF')
      } catch (error) {
        this.$message.error('加载用户增长趋势失败')
      }
    },

    async loadLoginStatistics() {
      try {
        const response = await getLoginStatistics(this.loginDays)
        const data = response.data

        const dates = data.map(item => item.date)
        const values = data.map(item => item.value)

        this.renderLineChart('loginChart', '登录次数', dates, values, '#67C23A')
      } catch (error) {
        this.$message.error('加载登录统计失败')
      }
    },

    async loadDeptDistribution() {
      try {
        const response = await getDeptUserDistribution()
        const data = response.data

        const chartData = data.map(item => ({
          name: item.name,
          value: item.value
        }))

        this.renderPieChart('deptDistributionChart', chartData)
      } catch (error) {
        this.$message.error('加载部门分布失败')
      }
    },

    async loadRoleDistribution() {
      try {
        const response = await getRoleUserDistribution()
        const data = response.data

        const chartData = data.map(item => ({
          name: item.name,
          value: item.value
        }))

        this.renderPieChart('roleDistributionChart', chartData)
      } catch (error) {
        this.$message.error('加载角色分布失败')
      }
    },

    async loadOperationStatistics() {
      try {
        const response = await getOperationStatistics(this.operationDays)
        const data = response.data

        const dates = data.map(item => item.date)
        const values = data.map(item => item.value)

        this.renderLineChart('operationChart', '操作次数', dates, values, '#E6A23C')
      } catch (error) {
        this.$message.error('加载操作统计失败')
      }
    },

    async loadOperationTypeStatistics() {
      try {
        const response = await getOperationTypeStatistics()
        const data = response.data

        const chartData = data.map(item => ({
          name: item.name,
          value: item.value
        }))

        this.renderPieChart('operationTypeChart', chartData)
      } catch (error) {
        this.$message.error('加载操作类型统计失败')
      }
    },

    renderLineChart(chartId, seriesName, xData, yData, color) {
      const chartDom = document.getElementById(chartId)
      if (!chartDom) return

      if (this.charts[chartId]) {
        this.charts[chartId].dispose()
      }

      const chart = echarts.init(chartDom)
      this.charts[chartId] = chart

      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xData
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: seriesName,
            type: 'line',
            smooth: true,
            data: yData,
            itemStyle: {
              color: color
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: color + '80' },
                  { offset: 1, color: color + '20' }
                ]
              }
            }
          }
        ]
      }

      chart.setOption(option)
      window.addEventListener('resize', () => chart.resize())
    },

    renderPieChart(chartId, data) {
      const chartDom = document.getElementById(chartId)
      if (!chartDom) return

      if (this.charts[chartId]) {
        this.charts[chartId].dispose()
      }

      const chart = echarts.init(chartDom)
      this.charts[chartId] = chart

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '人员分布',
            type: 'pie',
            radius: '50%',
            data: data,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

      chart.setOption(option)
      window.addEventListener('resize', () => chart.resize())
    }
  }
}
</script>

<style scoped>
.overview-card {
  margin-bottom: 20px;
}

.card-content {
  display: flex;
  align-items: center;
}

.card-content i {
  font-size: 48px;
  margin-right: 20px;
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.card-desc {
  font-size: 12px;
  color: #909399;
}
</style>

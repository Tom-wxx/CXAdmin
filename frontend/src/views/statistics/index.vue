<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 系统概览卡片 -->
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <el-icon style="color: #409EFF;"><User /></el-icon>
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
            <el-icon style="color: #67C23A;"><UserFilled /></el-icon>
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
            <el-icon style="color: #E6A23C;"><Promotion /></el-icon>
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
            <el-icon style="color: #F56C6C;"><Operation /></el-icon>
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
          <template #header><div class="clearfix">
            <span>用户增长趋势</span>
            <el-select v-model="userGrowthDays" size="small" style="float: right; width: 100px;" @change="loadUserGrowthTrend">
              <el-option label="7天" :value="7"></el-option>
              <el-option label="15天" :value="15"></el-option>
              <el-option label="30天" :value="30"></el-option>
            </el-select>
          </div></template>
          <div ref="userGrowthChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 登录统计 -->
      <el-col :span="12">
        <el-card>
          <template #header><div class="clearfix">
            <span>登录统计</span>
            <el-select v-model="loginDays" size="small" style="float: right; width: 100px;" @change="loadLoginStatistics">
              <el-option label="7天" :value="7"></el-option>
              <el-option label="15天" :value="15"></el-option>
              <el-option label="30天" :value="30"></el-option>
            </el-select>
          </div></template>
          <div ref="loginChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 部门人员分布 -->
      <el-col :span="12">
        <el-card>
          <template #header><div>
            <span>部门人员分布</span>
          </div></template>
          <div ref="deptDistributionChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 角色人员分布 -->
      <el-col :span="12">
        <el-card>
          <template #header><div>
            <span>角色人员分布</span>
          </div></template>
          <div ref="roleDistributionChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 操作日志统计 -->
      <el-col :span="12">
        <el-card>
          <template #header><div class="clearfix">
            <span>操作日志趋势</span>
            <el-select v-model="operationDays" size="small" style="float: right; width: 100px;" @change="loadOperationStatistics">
              <el-option label="7天" :value="7"></el-option>
              <el-option label="15天" :value="15"></el-option>
              <el-option label="30天" :value="30"></el-option>
            </el-select>
          </div></template>
          <div ref="operationChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 操作类型统计 -->
      <el-col :span="12">
        <el-card>
          <template #header><div>
            <span>操作类型分布</span>
          </div></template>
          <div ref="operationTypeChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { EChartsOption } from 'echarts'
import { useECharts } from '@/composables/useECharts'
import {
  getSystemOverview,
  getUserGrowthTrend,
  getLoginStatistics,
  getDeptUserDistribution,
  getRoleUserDistribution,
  getOperationStatistics,
  getOperationTypeStatistics
} from '@/api/statistics'
import type { SystemOverview, NameValue } from '@/types/statistics'

defineOptions({ name: 'Statistics' })

const overview = ref<SystemOverview>({})
const userGrowthDays = ref(30)
const loginDays = ref(30)
const operationDays = ref(30)

const userGrowthChartRef = ref<HTMLElement>()
const loginChartRef = ref<HTMLElement>()
const deptDistributionChartRef = ref<HTMLElement>()
const roleDistributionChartRef = ref<HTMLElement>()
const operationChartRef = ref<HTMLElement>()
const operationTypeChartRef = ref<HTMLElement>()

const { setOption: setUserGrowthOption } = useECharts(userGrowthChartRef)
const { setOption: setLoginOption } = useECharts(loginChartRef)
const { setOption: setDeptDistributionOption } = useECharts(deptDistributionChartRef)
const { setOption: setRoleDistributionOption } = useECharts(roleDistributionChartRef)
const { setOption: setOperationOption } = useECharts(operationChartRef)
const { setOption: setOperationTypeOption } = useECharts(operationTypeChartRef)

async function loadData() {
  await loadSystemOverview()
  await loadUserGrowthTrend()
  await loadLoginStatistics()
  await loadDeptDistribution()
  await loadRoleDistribution()
  await loadOperationStatistics()
  await loadOperationTypeStatistics()
}

async function loadSystemOverview() {
  try {
    const response = await getSystemOverview()
    overview.value = response.data
  } catch {
    ElMessage.error('加载系统概览失败')
  }
}

async function loadUserGrowthTrend() {
  try {
    const response = await getUserGrowthTrend(userGrowthDays.value)
    const data = response.data

    const dates = data.map(item => item.date ?? '')
    const values = data.map(item => item.value ?? 0)

    setUserGrowthOption(buildLineOption('用户增长', dates, values, '#409EFF'))
  } catch {
    ElMessage.error('加载用户增长趋势失败')
  }
}

async function loadLoginStatistics() {
  try {
    const response = await getLoginStatistics(loginDays.value)
    const data = response.data

    const dates = data.map(item => item.date ?? '')
    const values = data.map(item => item.value ?? 0)

    setLoginOption(buildLineOption('登录次数', dates, values, '#67C23A'))
  } catch {
    ElMessage.error('加载登录统计失败')
  }
}

async function loadDeptDistribution() {
  try {
    const response = await getDeptUserDistribution()
    setDeptDistributionOption(buildPieOption(toNameValueList(response.data)))
  } catch {
    ElMessage.error('加载部门分布失败')
  }
}

async function loadRoleDistribution() {
  try {
    const response = await getRoleUserDistribution()
    setRoleDistributionOption(buildPieOption(toNameValueList(response.data)))
  } catch {
    ElMessage.error('加载角色分布失败')
  }
}

async function loadOperationStatistics() {
  try {
    const response = await getOperationStatistics(operationDays.value)
    const data = response.data

    const dates = data.map(item => item.date ?? '')
    const values = data.map(item => item.value ?? 0)

    setOperationOption(buildLineOption('操作次数', dates, values, '#E6A23C'))
  } catch {
    ElMessage.error('加载操作统计失败')
  }
}

async function loadOperationTypeStatistics() {
  try {
    const response = await getOperationTypeStatistics()
    setOperationTypeOption(buildPieOption(toNameValueList(response.data)))
  } catch {
    ElMessage.error('加载操作类型统计失败')
  }
}

function toNameValueList(data: NameValue[]): { name: string; value: number }[] {
  return data.map(item => ({ name: item.name ?? '', value: item.value ?? 0 }))
}

function buildLineOption(seriesName: string, xData: string[], yData: number[], color: string): EChartsOption {
  return {
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
}

function buildPieOption(data: { name: string; value: number }[]): EChartsOption {
  return {
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
}

onMounted(() => {
  loadData()
})
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

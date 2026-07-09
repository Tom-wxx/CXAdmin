<template>
  <div class="dashboard-fresh">
    <!-- Hero 欢迎卡：渐变背景 + 浮动装饰图 -->
    <div class="hero fresh-anim" :style="{animationDelay: '0ms'}">
      <div class="hero-text">
        <div class="hero-greeting">{{ greeting }}，{{ userName }}</div>
        <div class="hero-sub">{{ todayStr }} · 今天系统运行平稳，{{ stats.totalUsers || '—' }} 名用户在册</div>
      </div>
      <div class="hero-deco">
        <el-icon><DataAnalysis /></el-icon>
      </div>
    </div>

    <!-- 数字 count-up 统计卡 -->
    <div class="stat-grid">
      <div
        v-for="(s, i) in statList"
        :key="s.key"
        class="stat-card-fresh fresh-anim hoverable"
        :style="{animationDelay: (80 + i * 80) + 'ms'}"
      >
        <div class="stat-icon" :style="{background: s.bg, color: s.color}">
          <menu-icon :name="s.icon" />
        </div>
        <div class="stat-info">
          <div class="stat-title">{{ s.title }}</div>
          <div class="stat-value-row">
            <span class="stat-value">{{ animatedValues[s.key] }}</span>
            <span v-if="s.trend !== undefined" :class="['stat-trend', s.trend >= 0 ? 'up' : 'down']">
              <el-icon><component :is="s.trend >= 0 ? 'Top' : 'Bottom'" /></el-icon>{{ Math.abs(s.trend) }}%
            </span>
          </div>
          <div class="stat-footer">{{ s.footer }}</div>
        </div>
      </div>
    </div>

    <!-- 图表 -->
    <div class="bento-row fresh-anim" :style="{animationDelay: '480ms'}">
      <div class="bento-card hoverable chart-wrap">
        <chart-card title="用户增长趋势" chart-type="line" :chart-data="userTrendData" height="280px" />
      </div>
      <div class="bento-card hoverable chart-wrap">
        <chart-card title="登录统计" chart-type="bar" :chart-data="loginStatsData" height="280px" />
      </div>
    </div>

    <!-- 活动 + 快捷 -->
    <div class="bento-row fresh-anim" :style="{animationDelay: '600ms'}">
      <div class="bento-card hoverable">
        <div class="card-header">
          <span class="card-title">近期操作</span>
          <span class="card-subtitle">最近 {{ recentLogs.length }} 条记录</span>
        </div>
        <div class="card-body activity-list">
          <div v-if="recentLogs.length === 0" class="empty">
            <el-icon><Document /></el-icon>
            <p>暂无操作日志</p>
          </div>
          <div v-for="log in recentLogs" :key="log.id || log.operTime" class="activity-item">
            <div class="dot" :class="log.status === '成功' ? 'ok' : 'err'"></div>
            <div class="activity-main">
              <div class="row1">
                <el-tag :type="operTagType(log.operType)" size="small" effect="plain">{{ log.operType }}</el-tag>
                <span class="activity-text">{{ log.operContent }}</span>
              </div>
              <div class="row2">{{ log.operator }} · {{ log.operTime }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-card hoverable">
        <div class="card-header">
          <span class="card-title">快捷访问</span>
        </div>
        <div class="card-body quick-grid">
          <router-link
            v-for="link in quickLinks"
            :key="link.path"
            :to="link.path"
            class="quick-tile"
            :style="{'--tile-color': link.color}"
          >
            <menu-icon :name="link.icon" />
            <span>{{ link.label }}</span>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 部门分布单独一行（视觉减压） -->
    <div class="bento-row single fresh-anim" :style="{animationDelay: '720ms'}">
      <div class="bento-card hoverable chart-wrap">
        <chart-card title="部门人员分布" chart-type="pie" :chart-data="deptDistributionData" height="320px" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getDashboardData, type DashboardStatCard, type DashboardRecentLog, type DashboardChartSeries } from '@/api/dashboard'
import { useUserStore } from '@/composables/store'
import ChartCard from '@/components/Dashboard/ChartCard.vue'

defineOptions({ name: 'Dashboard' })

type TagType = 'success' | 'primary' | 'danger' | 'warning' | 'info'

interface QuickLink {
  path: string
  icon: string
  label: string
  color: string
}

interface StatListItem {
  key: string
  title: string
  value: number
  icon: string
  bg: string
  color: string
  footer: string
  trend?: number
}

const EMPTY_CHART_SERIES: DashboardChartSeries = { labels: [], values: [] }
const OPER_TAG_TYPE_MAP: Record<string, TagType> = {
  '新增': 'success', '修改': 'primary', '删除': 'danger', '授权': 'warning',
  '导出': 'info', '导入': 'info', '强退': 'danger', '清空': 'warning', '其他': 'info'
}

const { name: userNameFromStore } = useUserStore()

const loading = ref(false)
const stats = ref<DashboardStatCard>({})
const animatedValues = reactive<Record<string, number>>({ totalUsers: 0, totalRoles: 0, onlineUsers: 0, totalNotices: 0 })
const userTrendData = ref<DashboardChartSeries>({ labels: [], values: [] })
const loginStatsData = ref<DashboardChartSeries>({ labels: [], values: [] })
const deptDistributionData = ref<DashboardChartSeries>({ labels: [], values: [] })
const recentLogs = ref<DashboardRecentLog[]>([])
const quickLinks: QuickLink[] = [
  { path: '/system/user',    icon: 'el-icon-user',         label: '用户管理', color: '#0f9f9f' },
  { path: '/system/role',    icon: 'el-icon-s-custom',     label: '角色管理', color: '#4f46e5' },
  { path: '/system/sso',     icon: 'el-icon-link',         label: '身份认证', color: '#7c3aed' },
  { path: '/monitor/operlog',icon: 'el-icon-document',     label: '操作日志', color: '#d97706' },
  { path: '/monitor/online', icon: 'el-icon-video-play',   label: '在线用户', color: '#16a34a' },
  { path: '/monitor/job',    icon: 'el-icon-time',         label: '定时任务', color: '#db2777' },
  { path: '/profile',        icon: 'el-icon-s-promotion',  label: '个人中心', color: '#dc2626' },
  { path: '/system/dict',    icon: 'el-icon-collection',   label: '数据字典', color: '#2563eb' }
]

const userName = computed(() => userNameFromStore.value || 'admin')

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 12) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const todayStr = computed(() => {
  const d = new Date()
  const w = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${w[d.getDay()]}`
})

const statList = computed<StatListItem[]>(() => {
  const s = stats.value || {}
  return [
    { key: 'totalUsers',   title: '用户总数', value: s.totalUsers   || 0, icon: 'el-icon-user',       bg: '#e6fffb', color: '#0f9f9f', footer: `今日新增 ${s.todayUsers || 0}`, trend: s.userTrend },
    { key: 'totalRoles',   title: '角色数量', value: s.totalRoles   || 0, icon: 'el-icon-s-custom',   bg: '#eef0ff', color: '#5b6ef0', footer: `已配置角色`,                     trend: undefined },
    { key: 'onlineUsers',  title: '在线用户', value: s.onlineUsers  || 0, icon: 'el-icon-video-play', bg: '#f6ffed', color: '#52c41a', footer: `活跃 ${s.activeUsers || s.onlineUsers || 0}`, trend: s.onlineTrend },
    { key: 'totalNotices', title: '通知公告', value: s.totalNotices || 0, icon: 'el-icon-bell',       bg: '#fff7e6', color: '#fa8c16', footer: `待办 ${s.pendingTasks || 0}`,   trend: undefined }
  ]
})

async function loadDashboardData() {
  loading.value = true
  try {
    const response = await getDashboardData()
    if (response.code === 200 && response.data) {
      const data = response.data
      stats.value = data.statCard || {}
      userTrendData.value = data.userTrend || EMPTY_CHART_SERIES
      loginStatsData.value = data.loginStats || EMPTY_CHART_SERIES
      deptDistributionData.value = data.deptDistribution || EMPTY_CHART_SERIES
      recentLogs.value = data.recentLogs || []
      await nextTick()
      animateNumbers()
    }
  } catch {
    ElMessage.error('加载仪表板数据失败')
  } finally {
    loading.value = false
  }
}

/** 数字 count-up 动画：1000ms，requestAnimationFrame 驱动 */
function animateNumbers() {
  const targets: Record<string, number> = {
    totalUsers:   stats.value.totalUsers   || 0,
    totalRoles:   stats.value.totalRoles   || 0,
    onlineUsers:  stats.value.onlineUsers  || 0,
    totalNotices: stats.value.totalNotices || 0
  }
  const duration = 1000
  const start = performance.now()
  const easeOutCubic = (t: number) => 1 - Math.pow(1 - t, 3)
  const tick = (now: number) => {
    const p = Math.min(1, (now - start) / duration)
    const e = easeOutCubic(p)
    Object.keys(targets).forEach(k => {
      animatedValues[k] = Math.round(targets[k] * e)
    })
    if (p < 1) requestAnimationFrame(tick)
    else Object.assign(animatedValues, targets)
  }
  requestAnimationFrame(tick)
}

function operTagType(operType: string | undefined): TagType {
  return operType ? (OPER_TAG_TYPE_MAP[operType] || 'info') : 'info'
}

loadDashboardData()
</script>

<style lang="scss" scoped>
$accent: #0f9f9f;
$accent-2: #4f46e5;
$card-border: #e5e7eb;
$text-1: #1f2937;
$text-2: #64748b;
$text-3: #94a3b8;

.dashboard-fresh {
  padding: 4px;
}

/* Hero */
.hero {
  position: relative;
  margin-bottom: 20px;
  padding: 24px 28px;
  border-radius: 8px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  color: $text-1;
  border: 1px solid $card-border;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}
.hero-greeting { font-size: 22px; font-weight: 700; }
.hero-sub      { margin-top: 8px; font-size: 14px; color: $text-2; }

.hero-deco {
  position: relative; width: 120px; height: 90px;
  i {
    position: absolute; right: 8px; bottom: 0;
    font-size: 56px; color: rgba(15, 159, 159, 0.16);
  }
}

/* Stat row */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}
.stat-card-fresh {
  padding: 20px 20px 18px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  border-radius: 8px;
}
.stat-icon {
  width: 48px; height: 48px;
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.stat-info { flex: 1; min-width: 0; }
.stat-title { font-size: 13px; color: $text-2; margin-bottom: 8px; }
.stat-value-row {
  display: flex; align-items: baseline; gap: 8px;
  margin-bottom: 6px;
}
.stat-value {
  font-size: 28px; font-weight: 700; color: $text-1;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.5px;
}
.stat-trend {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  display: inline-flex; align-items: center; gap: 2px;
  &.up   { background: #f6ffed; color: #52c41a; }
  &.down { background: #fff1f0; color: #f5222d; }
}
.stat-footer { font-size: 12px; color: $text-3; }

/* Bento rows */
.bento-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
  &.single { grid-template-columns: 1fr; }

  @media (max-width: 992px) {
    grid-template-columns: 1fr;
  }
}
.bento-card {
  padding: 20px;
  border-radius: 8px;
}
.bento-card.chart-wrap { padding: 8px; }
.bento-card.chart-wrap :deep(.chart-card),
.bento-card.chart-wrap :deep(.panel){
  border: none !important;
  box-shadow: none !important;
  margin-bottom: 0 !important;
}

.card-header {
  display: flex; align-items: baseline; justify-content: space-between;
  margin-bottom: 16px;
}
.card-title    { font-size: 16px; font-weight: 600; color: $text-1; }
.card-subtitle { font-size: 12px; color: $text-3; }

/* Activity list */
.activity-list { max-height: 320px; overflow-y: auto; }
.activity-item {
  display: flex; gap: 12px;
  padding: 12px 0;
  border-bottom: 1px dashed #f0f2f5;
  &:last-child { border-bottom: none; }
}
.dot {
  width: 8px; height: 8px; border-radius: 50%;
  margin-top: 8px; flex-shrink: 0;
  &.ok  { background: #52c41a; box-shadow: 0 0 0 4px rgba(82,196,26,0.12); }
  &.err { background: #f5222d; box-shadow: 0 0 0 4px rgba(245,34,45,0.12); }
}
.activity-main { flex: 1; min-width: 0; }
.row1 {
  display: flex; align-items: center; gap: 8px;
  font-size: 13px; color: $text-1;
  .activity-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
}
.row2 { margin-top: 4px; font-size: 12px; color: $text-3; }
.empty {
  text-align: center; padding: 40px 0; color: $text-3;
  i { font-size: 36px; }
  p { margin: 8px 0 0; font-size: 13px; }
}

/* Quick links grid */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.quick-tile {
  --tile-color: #0f9f9f;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 18px 8px;
  border-radius: 8px;
  background: #f8fafc;
  text-decoration: none;
  color: $text-1;
  transition: border-color 0.2s ease, background 0.2s ease, color 0.2s ease;
  border: 1px solid $card-border;

  i {
    font-size: 22px;
    color: var(--tile-color);
    margin-bottom: 8px;
    transition: color 0.2s ease;
  }
  span { font-size: 12px; color: $text-2; }

  &:hover {
    background: #fff;
    border-color: var(--tile-color);
    box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
    span { color: $text-1; }
  }
}
</style>

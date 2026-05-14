<template>
  <div class="dashboard-fresh">
    <!-- Hero 欢迎卡：渐变背景 + 浮动装饰图 -->
    <div class="hero fresh-anim" :style="{animationDelay: '0ms'}">
      <div class="hero-text">
        <div class="hero-greeting">{{ greeting }}，{{ userName }} 👋</div>
        <div class="hero-sub">{{ todayStr }} · 今天系统运行平稳，{{ stats.totalUsers || '—' }} 名用户在册</div>
      </div>
      <div class="hero-deco">
        <div class="deco-bubble b1"></div>
        <div class="deco-bubble b2"></div>
        <i class="el-icon-data-analysis"></i>
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
          <i :class="s.icon"></i>
        </div>
        <div class="stat-info">
          <div class="stat-title">{{ s.title }}</div>
          <div class="stat-value-row">
            <span class="stat-value">{{ animatedValues[s.key] }}</span>
            <span v-if="s.trend !== undefined" :class="['stat-trend', s.trend >= 0 ? 'up' : 'down']">
              <i :class="s.trend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>{{ Math.abs(s.trend) }}%
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
            <i class="el-icon-document"></i>
            <p>暂无操作日志</p>
          </div>
          <div v-for="log in recentLogs" :key="log.id || log.operTime" class="activity-item">
            <div class="dot" :class="log.status === '成功' ? 'ok' : 'err'"></div>
            <div class="activity-main">
              <div class="row1">
                <el-tag :type="operTagType(log.operType)" size="mini" effect="plain">{{ log.operType }}</el-tag>
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
            <i :class="link.icon"></i>
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

<script>
import { getDashboardData } from '@/api/dashboard'
import ChartCard from '@/components/Dashboard/ChartCard'

export default {
  name: 'Dashboard',
  components: { ChartCard },
  data() {
    return {
      loading: false,
      stats: {},
      animatedValues: { totalUsers: 0, totalRoles: 0, onlineUsers: 0, totalNotices: 0 },
      userTrendData: { labels: [], values: [] },
      loginStatsData: { labels: [], values: [] },
      deptDistributionData: { labels: [], values: [] },
      recentLogs: [],
      quickLinks: [
        { path: '/system/user',    icon: 'el-icon-user',         label: '用户管理', color: '#13c2c2' },
        { path: '/system/role',    icon: 'el-icon-s-custom',     label: '角色管理', color: '#5b6ef0' },
        { path: '/system/sso',     icon: 'el-icon-link',         label: '身份认证', color: '#722ed1' },
        { path: '/monitor/operlog',icon: 'el-icon-document',     label: '操作日志', color: '#fa8c16' },
        { path: '/monitor/online', icon: 'el-icon-video-play',   label: '在线用户', color: '#52c41a' },
        { path: '/monitor/job',    icon: 'el-icon-time',         label: '定时任务', color: '#eb2f96' },
        { path: '/profile',        icon: 'el-icon-s-promotion',  label: '个人中心', color: '#f5222d' },
        { path: '/system/dict',    icon: 'el-icon-collection',   label: '数据字典', color: '#1890ff' }
      ]
    }
  },
  computed: {
    userName() {
      return this.$store.state.user.name || 'admin'
    },
    greeting() {
      const h = new Date().getHours()
      if (h < 6) return '凌晨好'
      if (h < 12) return '早上好'
      if (h < 14) return '中午好'
      if (h < 18) return '下午好'
      return '晚上好'
    },
    todayStr() {
      const d = new Date()
      const w = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${w[d.getDay()]}`
    },
    statList() {
      const s = this.stats || {}
      return [
        { key: 'totalUsers',   title: '用户总数', value: s.totalUsers   || 0, icon: 'el-icon-user',       bg: '#e6fffb', color: '#13c2c2', footer: `今日新增 ${s.todayUsers || 0}`, trend: s.userTrend },
        { key: 'totalRoles',   title: '角色数量', value: s.totalRoles   || 0, icon: 'el-icon-s-custom',   bg: '#eef0ff', color: '#5b6ef0', footer: `已配置角色`,                     trend: undefined },
        { key: 'onlineUsers',  title: '在线用户', value: s.onlineUsers  || 0, icon: 'el-icon-video-play', bg: '#f6ffed', color: '#52c41a', footer: `活跃 ${s.activeUsers || s.onlineUsers || 0}`, trend: s.onlineTrend },
        { key: 'totalNotices', title: '通知公告', value: s.totalNotices || 0, icon: 'el-icon-bell',       bg: '#fff7e6', color: '#fa8c16', footer: `待办 ${s.pendingTasks || 0}`,   trend: undefined }
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
          this.stats = data.statCard || {}
          this.userTrendData = data.userTrend || { labels: [], values: [] }
          this.loginStatsData = data.loginStats || { labels: [], values: [] }
          this.deptDistributionData = data.deptDistribution || { labels: [], values: [] }
          this.recentLogs = data.recentLogs || []
          this.$nextTick(() => this.animateNumbers())
        }
      } catch (_) {
        this.$message.error('加载仪表板数据失败')
      } finally {
        this.loading = false
      }
    },
    /** 数字 count-up 动画：1000ms，requestAnimationFrame 驱动 */
    animateNumbers() {
      const targets = {
        totalUsers:   this.stats.totalUsers   || 0,
        totalRoles:   this.stats.totalRoles   || 0,
        onlineUsers:  this.stats.onlineUsers  || 0,
        totalNotices: this.stats.totalNotices || 0
      }
      const duration = 1000
      const start = performance.now()
      const easeOutCubic = t => 1 - Math.pow(1 - t, 3)
      const tick = now => {
        const p = Math.min(1, (now - start) / duration)
        const e = easeOutCubic(p)
        Object.keys(targets).forEach(k => {
          this.animatedValues[k] = Math.round(targets[k] * e)
        })
        if (p < 1) requestAnimationFrame(tick)
        else this.animatedValues = { ...targets }
      }
      requestAnimationFrame(tick)
    },
    operTagType(operType) {
      const m = { '新增': 'success', '修改': 'primary', '删除': 'danger', '授权': 'warning', '导出': 'info', '导入': 'info', '强退': 'danger', '清空': 'warning', '其他': 'info' }
      return m[operType] || 'info'
    }
  }
}
</script>

<style lang="scss" scoped>
$accent: #13c2c2;
$accent-2: #5b6ef0;
$card-border: #eef0f3;
$text-1: #1f2329;
$text-2: #646a73;
$text-3: #8f959e;

.dashboard-fresh {
  padding: 4px;
}

/* Hero */
.hero {
  position: relative;
  margin-bottom: 20px;
  padding: 28px 32px;
  border-radius: 16px;
  background: linear-gradient(135deg, #0f7474 0%, #08979c 45%, #13c2c2 100%);
  color: #fff;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 8px 24px rgba(8, 151, 156, 0.18);
}
.hero-greeting { font-size: 24px; font-weight: 700; letter-spacing: 0.5px; }
.hero-sub      { margin-top: 8px; font-size: 14px; opacity: 0.85; }

.hero-deco {
  position: relative; width: 120px; height: 90px;
  i {
    position: absolute; right: 8px; bottom: 0;
    font-size: 64px; color: rgba(255,255,255,0.4);
  }
}
.deco-bubble {
  position: absolute;
  border-radius: 50%;
  background: rgba(255,255,255,0.12);
  animation: bubble-float 6s ease-in-out infinite;
}
.deco-bubble.b1 { width: 90px; height: 90px; top: -10px; right: -10px; }
.deco-bubble.b2 { width: 50px; height: 50px; top: 40px; right: 80px; animation-delay: 2s; }
@keyframes bubble-float {
  0%, 100% { transform: translateY(0); }
  50%      { transform: translateY(-8px); }
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
}
.stat-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
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
.bento-card { padding: 20px; }
.bento-card.chart-wrap { padding: 8px; }
.bento-card.chart-wrap ::v-deep .chart-card,
.bento-card.chart-wrap ::v-deep .panel {
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
  --tile-color: #13c2c2;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 18px 8px;
  border-radius: 12px;
  background: #fafbfc;
  text-decoration: none;
  color: $text-1;
  transition: transform 0.25s ease, box-shadow 0.25s ease, background 0.25s ease;
  border: 1px solid transparent;

  i {
    font-size: 22px;
    color: var(--tile-color);
    margin-bottom: 8px;
    transition: transform 0.3s ease;
  }
  span { font-size: 12px; color: $text-2; }

  &:hover {
    transform: translateY(-3px);
    background: #fff;
    border-color: var(--tile-color);
    box-shadow: 0 6px 16px rgba(15, 23, 42, 0.08);
    i { transform: scale(1.15) rotate(-6deg); }
    span { color: $text-1; }
  }
}
</style>

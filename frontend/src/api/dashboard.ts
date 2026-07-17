import request from '@/utils/request'
import type { Result } from '@/types/api'

/** 仪表板图表系列数据（趋势/分布，labels 与 values 一一对应） */
export interface DashboardChartSeries {
  labels?: string[]
  values?: number[]
  [key: string]: unknown
}

/** 仪表板统计卡片数据 */
export interface DashboardStatCard {
  totalUsers?: number
  todayUsers?: number
  totalRoles?: number
  onlineUsers?: number
  activeUsers?: number
  totalNotices?: number
  pendingTasks?: number
  userTrend?: number
  onlineTrend?: number
  [key: string]: unknown
}

/** 仪表板近期操作日志条目 */
export interface DashboardRecentLog {
  id?: number | string
  operTime?: string
  status?: string
  operType?: string
  operContent?: string
  operator?: string
  [key: string]: unknown
}

/** 仪表板完整数据 */
export interface DashboardData {
  statCard?: DashboardStatCard
  userTrend?: DashboardChartSeries
  loginStats?: DashboardChartSeries
  deptDistribution?: DashboardChartSeries
  recentLogs?: DashboardRecentLog[]
  [key: string]: unknown
}

/**
 * 获取仪表板完整数据
 */
export function getDashboardData(): Promise<Result<DashboardData>> {
  return request({
    url: '/dashboard/data',
    method: 'get'
  })
}

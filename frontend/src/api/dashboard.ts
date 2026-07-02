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

/**
 * 获取统计卡片数据
 */
export function getStatCard(): Promise<Result<DashboardStatCard>> {
  return request({
    url: '/dashboard/stat-card',
    method: 'get'
  })
}

/**
 * 获取用户增长趋势
 */
export function getUserTrend(): Promise<Result<DashboardChartSeries>> {
  return request({
    url: '/dashboard/user-trend',
    method: 'get'
  })
}

/**
 * 获取登录统计
 */
export function getLoginStats(): Promise<Result<DashboardChartSeries>> {
  return request({
    url: '/dashboard/login-stats',
    method: 'get'
  })
}

/**
 * 获取部门人员分布
 */
export function getDeptDistribution(): Promise<Result<DashboardChartSeries>> {
  return request({
    url: '/dashboard/dept-distribution',
    method: 'get'
  })
}

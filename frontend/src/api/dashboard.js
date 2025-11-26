import request from '@/utils/request'

/**
 * 获取仪表板完整数据
 */
export function getDashboardData() {
  return request({
    url: '/dashboard/data',
    method: 'get'
  })
}

/**
 * 获取统计卡片数据
 */
export function getStatCard() {
  return request({
    url: '/dashboard/stat-card',
    method: 'get'
  })
}

/**
 * 获取用户增长趋势
 */
export function getUserTrend() {
  return request({
    url: '/dashboard/user-trend',
    method: 'get'
  })
}

/**
 * 获取登录统计
 */
export function getLoginStats() {
  return request({
    url: '/dashboard/login-stats',
    method: 'get'
  })
}

/**
 * 获取部门人员分布
 */
export function getDeptDistribution() {
  return request({
    url: '/dashboard/dept-distribution',
    method: 'get'
  })
}

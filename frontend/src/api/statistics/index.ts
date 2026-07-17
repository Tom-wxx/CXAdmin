import request from '@/utils/request'
import type { Result } from '@/types/api'
import type { SystemOverview, TrendPoint, NameValue } from '@/types/statistics'

/** 获取系统概览数据 */
export function getSystemOverview(): Promise<Result<SystemOverview>> {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

/** 获取用户增长趋势 */
export function getUserGrowthTrend(days: number): Promise<Result<TrendPoint[]>> {
  return request({
    url: '/statistics/user/growth',
    method: 'get',
    params: { days }
  })
}

/** 获取登录统计趋势 */
export function getLoginStatistics(days: number): Promise<Result<TrendPoint[]>> {
  return request({
    url: '/statistics/login/trend',
    method: 'get',
    params: { days }
  })
}


/** 获取操作日志统计 */
export function getOperationStatistics(days: number): Promise<Result<TrendPoint[]>> {
  return request({
    url: '/statistics/operation/trend',
    method: 'get',
    params: { days }
  })
}

/** 获取部门人员分布 */
export function getDeptUserDistribution(): Promise<Result<NameValue[]>> {
  return request({
    url: '/statistics/dept/distribution',
    method: 'get'
  })
}

/** 获取角色人员分布 */
export function getRoleUserDistribution(): Promise<Result<NameValue[]>> {
  return request({
    url: '/statistics/role/distribution',
    method: 'get'
  })
}

/** 获取操作类型统计 */
export function getOperationTypeStatistics(): Promise<Result<NameValue[]>> {
  return request({
    url: '/statistics/operation/type',
    method: 'get'
  })
}

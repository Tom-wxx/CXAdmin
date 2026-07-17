import request from '@/utils/request'
import type { Result } from '@/types/api'
import type { SystemOverview, TrendPoint, NameValue } from '@/types/statistics'

export function getSystemOverview(): Promise<Result<SystemOverview>> {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

export function getUserGrowthTrend(days: number): Promise<Result<TrendPoint[]>> {
  return request({
    url: '/statistics/user/growth',
    method: 'get',
    params: { days }
  })
}

export function getLoginStatistics(days: number): Promise<Result<TrendPoint[]>> {
  return request({
    url: '/statistics/login/trend',
    method: 'get',
    params: { days }
  })
}

export function getOperationStatistics(days: number): Promise<Result<TrendPoint[]>> {
  return request({
    url: '/statistics/operation/trend',
    method: 'get',
    params: { days }
  })
}

export function getDeptUserDistribution(): Promise<Result<NameValue[]>> {
  return request({
    url: '/statistics/dept/distribution',
    method: 'get'
  })
}

export function getRoleUserDistribution(): Promise<Result<NameValue[]>> {
  return request({
    url: '/statistics/role/distribution',
    method: 'get'
  })
}

export function getOperationTypeStatistics(): Promise<Result<NameValue[]>> {
  return request({
    url: '/statistics/operation/type',
    method: 'get'
  })
}

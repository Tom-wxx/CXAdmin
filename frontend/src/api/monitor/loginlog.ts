import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { LoginLog, LoginLogQuery } from '@/types/monitor/log'

/** 分页查询登录日志列表 */
export function listLoginLog(query: LoginLogQuery): Promise<Result<PageResult<LoginLog>>> {
  return request({ url: '/system/loginlog/list', method: 'get', params: query })
}

/** 查询登录日志详细信息 */
export function getLoginLog(infoId: number): Promise<Result<LoginLog>> {
  return request({ url: '/system/loginlog/' + infoId, method: 'get' })
}

/** 删除登录日志 */
export function delLoginLog(infoIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/loginlog/' + infoIds, method: 'delete' })
}

/** 清空登录日志 */
export function cleanLoginLog(): Promise<Result<void>> {
  return request({ url: '/system/loginlog/clean', method: 'delete' })
}

/** 导出登录日志（二进制流） */
export function exportLoginLog(query: LoginLogQuery): Promise<Blob> {
  return request({ url: '/system/loginlog/export', method: 'post', params: query, responseType: 'blob' }) as unknown as Promise<Blob>
}

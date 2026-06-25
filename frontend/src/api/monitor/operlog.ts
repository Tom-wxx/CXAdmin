import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { OperLog, OperLogQuery } from '@/types/monitor/log'

/** 分页查询操作日志列表 */
export function listOperLog(query: OperLogQuery): Promise<TableResponse<OperLog>> {
  return request({ url: '/system/operlog/list', method: 'get', params: query })
}

/** 查询操作日志详细信息 */
export function getOperLog(operId: number): Promise<Result<OperLog>> {
  return request({ url: '/system/operlog/' + operId, method: 'get' })
}

/** 删除操作日志 */
export function delOperLog(operIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/operlog/' + operIds, method: 'delete' })
}

/** 清空操作日志 */
export function cleanOperLog(): Promise<Result<void>> {
  return request({ url: '/system/operlog/clean', method: 'delete' })
}

/** 导出操作日志（二进制流） */
export function exportOperLog(query: OperLogQuery): Promise<Blob> {
  return request({ url: '/system/operlog/export', method: 'post', params: query, responseType: 'blob' }) as unknown as Promise<Blob>
}

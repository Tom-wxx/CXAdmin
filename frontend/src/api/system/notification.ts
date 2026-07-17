import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { Notification, NotificationQuery } from '@/types/system/notification'

/** 分页查询通知列表 */
export function listNotification(query: NotificationQuery): Promise<TableResponse<Notification>> {
  return request({ url: '/system/notification/list', method: 'get', params: query })
}

/** 查询通知详情 */
export function getNotification(id: number): Promise<Result<Notification>> {
  return request({ url: '/system/notification/' + id, method: 'get' })
}

/** 批量标记消息为已读 */
export function batchMarkAsRead(ids: number[]): Promise<Result<void>> {
  return request({ url: '/system/notification/read/batch', method: 'put', data: ids })
}

/** 全部标记为已读 */
export function markAllAsRead(): Promise<Result<void>> {
  return request({ url: '/system/notification/read/all', method: 'put' })
}

/** 删除通知 */
export function delNotification(ids: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/notification/' + ids, method: 'delete' })
}

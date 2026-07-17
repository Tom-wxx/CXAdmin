import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { Notification, NotificationQuery } from '@/types/system/notification'

export function listNotification(query: NotificationQuery): Promise<TableResponse<Notification>> {
  return request({ url: '/system/notification/list', method: 'get', params: query })
}

export function getNotification(id: number): Promise<Result<Notification>> {
  return request({ url: '/system/notification/' + id, method: 'get' })
}

export function batchMarkAsRead(ids: number[]): Promise<Result<void>> {
  return request({ url: '/system/notification/read/batch', method: 'put', data: ids })
}

export function markAllAsRead(): Promise<Result<void>> {
  return request({ url: '/system/notification/read/all', method: 'put' })
}

export function delNotification(ids: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/notification/' + ids, method: 'delete' })
}

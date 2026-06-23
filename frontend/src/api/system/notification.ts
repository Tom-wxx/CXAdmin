import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { Notification, NotificationQuery, NotificationSend } from '@/types/system/notification'

/** 分页查询通知列表 */
export function listNotification(query: NotificationQuery): Promise<Result<PageResult<Notification>>> {
  return request({ url: '/system/notification/list', method: 'get', params: query })
}

/** 查询通知详情 */
export function getNotification(id: number): Promise<Result<Notification>> {
  return request({ url: '/system/notification/' + id, method: 'get' })
}

/** 获取未读消息数量 */
export function getUnreadCount(): Promise<Result<number>> {
  return request({ url: '/system/notification/unread/count', method: 'get' })
}

/** 标记消息为已读 */
export function markAsRead(id: number): Promise<Result<void>> {
  return request({ url: '/system/notification/read/' + id, method: 'put' })
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

/** 发送通知 */
export function sendNotification(data: NotificationSend): Promise<Result<void>> {
  return request({ url: '/system/notification/send', method: 'post', data })
}

/** 批量发送通知 */
export function sendNotificationToUsers(data: NotificationSend): Promise<Result<void>> {
  return request({ url: '/system/notification/send/batch', method: 'post', data })
}

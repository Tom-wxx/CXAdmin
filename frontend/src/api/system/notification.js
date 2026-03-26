import request from '@/utils/request'

// 分页查询通知列表
export function listNotification(query) {
  return request({
    url: '/system/notification/list',
    method: 'get',
    params: query
  })
}

// 查询通知详情
export function getNotification(id) {
  return request({
    url: '/system/notification/' + id,
    method: 'get'
  })
}

// 获取未读消息数量
export function getUnreadCount() {
  return request({
    url: '/system/notification/unread/count',
    method: 'get'
  })
}

// 标记消息为已读
export function markAsRead(id) {
  return request({
    url: '/system/notification/read/' + id,
    method: 'put'
  })
}

// 批量标记消息为已读
export function batchMarkAsRead(ids) {
  return request({
    url: '/system/notification/read/batch',
    method: 'put',
    data: ids
  })
}

// 全部标记为已读
export function markAllAsRead() {
  return request({
    url: '/system/notification/read/all',
    method: 'put'
  })
}

// 删除通知
export function delNotification(ids) {
  return request({
    url: '/system/notification/' + ids,
    method: 'delete'
  })
}

// 发送通知
export function sendNotification(data) {
  return request({
    url: '/system/notification/send',
    method: 'post',
    data: data
  })
}

// 批量发送通知
export function sendNotificationToUsers(data) {
  return request({
    url: '/system/notification/send/batch',
    method: 'post',
    data: data
  })
}

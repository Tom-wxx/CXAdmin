import request from '@/utils/request'

/**
 * 分页查询消息发送日志列表
 * @param {Object} query - 查询参数
 */
export function listMessageLog(query) {
  return request({
    url: '/system/message/log/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询消息发送日志详细信息
 * @param {number} logId - 日志ID
 */
export function getMessageLog(logId) {
  return request({
    url: '/system/message/log/' + logId,
    method: 'get'
  })
}

/**
 * 删除消息发送日志
 * @param {Array|number} logIds - 日志ID数组或单个ID
 */
export function delMessageLog(logIds) {
  return request({
    url: '/system/message/log/' + logIds,
    method: 'delete'
  })
}

/**
 * 清空消息发送日志
 */
export function cleanMessageLog() {
  return request({
    url: '/system/message/log/clean',
    method: 'delete'
  })
}

import request from '@/utils/request'

/**
 * 分页查询消息模板列表
 * @param {Object} query - 查询参数
 */
export function listMessage(query) {
  return request({
    url: '/system/message/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询消息模板详细信息
 * @param {number} messageId - 消息ID
 */
export function getMessage(messageId) {
  return request({
    url: '/system/message/' + messageId,
    method: 'get'
  })
}

/**
 * 新增消息模板
 * @param {Object} data - 消息模板信息
 */
export function addMessage(data) {
  return request({
    url: '/system/message',
    method: 'post',
    data: data
  })
}

/**
 * 修改消息模板
 * @param {Object} data - 消息模板信息
 */
export function updateMessage(data) {
  return request({
    url: '/system/message',
    method: 'put',
    data: data
  })
}

/**
 * 删除消息模板
 * @param {Array|number} messageIds - 消息ID数组或单个ID
 */
export function delMessage(messageIds) {
  return request({
    url: '/system/message/' + messageIds,
    method: 'delete'
  })
}

/**
 * 修改消息模板状态
 * @param {Object} data - 包含messageId和status的对象
 */
export function changeMessageStatus(data) {
  return request({
    url: '/system/message/changeStatus',
    method: 'put',
    data: data
  })
}

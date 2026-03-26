import request from '@/utils/request'

/**
 * 分页查询消息配置列表
 * @param {Object} query - 查询参数
 */
export function listMessageConfig(query) {
  return request({
    url: '/system/message/config/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询消息配置详细信息
 * @param {number} configId - 配置ID
 */
export function getMessageConfig(configId) {
  return request({
    url: '/system/message/config/' + configId,
    method: 'get'
  })
}

/**
 * 新增消息配置
 * @param {Object} data - 消息配置信息
 */
export function addMessageConfig(data) {
  return request({
    url: '/system/message/config',
    method: 'post',
    data: data
  })
}

/**
 * 修改消息配置
 * @param {Object} data - 消息配置信息
 */
export function updateMessageConfig(data) {
  return request({
    url: '/system/message/config',
    method: 'put',
    data: data
  })
}

/**
 * 删除消息配置
 * @param {Array|number} configIds - 配置ID数组或单个ID
 */
export function delMessageConfig(configIds) {
  return request({
    url: '/system/message/config/' + configIds,
    method: 'delete'
  })
}

/**
 * 修改消息配置状态
 * @param {Object} data - 包含configId和status的对象
 */
export function changeConfigStatus(data) {
  return request({
    url: '/system/message/config/changeStatus',
    method: 'put',
    data: data
  })
}

/**
 * 设置为默认配置
 * @param {number} configId - 配置ID
 */
export function setDefaultConfig(configId) {
  return request({
    url: '/system/message/config/setDefault/' + configId,
    method: 'put'
  })
}

/**
 * 测试发送消息
 * @param {Object} data - 测试数据
 */
export function testSendMessage(data) {
  return request({
    url: '/system/message/config/test',
    method: 'post',
    data: data
  })
}

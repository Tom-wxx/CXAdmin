import request from '@/utils/request'

/**
 * 查询在线用户列表
 */
export function listOnlineUser(query) {
  return request({
    url: '/monitor/online/list',
    method: 'get',
    params: query
  })
}

/**
 * 强制退出用户
 */
export function forceLogout(tokenId) {
  return request({
    url: '/monitor/online/' + tokenId,
    method: 'delete'
  })
}

/**
 * 批量强制退出用户
 */
export function batchForceLogout(tokenIds) {
  return request({
    url: '/monitor/online/batch/' + tokenIds,
    method: 'delete'
  })
}

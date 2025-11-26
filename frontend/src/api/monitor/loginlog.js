import request from '@/utils/request'

/**
 * 分页查询登录日志列表
 */
export function listLoginLog(query) {
  return request({
    url: '/system/loginlog/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询登录日志详细信息
 */
export function getLoginLog(infoId) {
  return request({
    url: '/system/loginlog/' + infoId,
    method: 'get'
  })
}

/**
 * 删除登录日志
 */
export function delLoginLog(infoIds) {
  return request({
    url: '/system/loginlog/' + infoIds,
    method: 'delete'
  })
}

/**
 * 清空登录日志
 */
export function cleanLoginLog() {
  return request({
    url: '/system/loginlog/clean',
    method: 'delete'
  })
}

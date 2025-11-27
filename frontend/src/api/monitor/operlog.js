import request from '@/utils/request'

/**
 * 分页查询操作日志列表
 */
export function listOperLog(query) {
  return request({
    url: '/system/operlog/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询操作日志详细信息
 */
export function getOperLog(operId) {
  return request({
    url: '/system/operlog/' + operId,
    method: 'get'
  })
}

/**
 * 删除操作日志
 */
export function delOperLog(operIds) {
  return request({
    url: '/system/operlog/' + operIds,
    method: 'delete'
  })
}

/**
 * 清空操作日志
 */
export function cleanOperLog() {
  return request({
    url: '/system/operlog/clean',
    method: 'delete'
  })
}

/**
 * 导出操作日志
 */
export function exportOperLog(query) {
  return request({
    url: '/system/operlog/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

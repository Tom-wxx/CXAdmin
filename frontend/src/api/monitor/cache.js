import request from '@/utils/request'

/**
 * 获取缓存列表
 */
export function getCacheList() {
  return request({
    url: '/monitor/cache/list',
    method: 'get'
  })
}

/**
 * 获取键名列表
 */
export function getKeyList(params) {
  return request({
    url: '/monitor/cache/keys',
    method: 'get',
    params
  })
}

/**
 * 获取缓存内容
 */
export function getCacheValue(key) {
  return request({
    url: '/monitor/cache/value',
    method: 'get',
    params: { key }
  })
}

/**
 * 删除缓存
 */
export function deleteCache(key) {
  return request({
    url: `/monitor/cache/${key}`,
    method: 'delete'
  })
}

/**
 * 批量删除缓存
 */
export function batchDeleteCache(keys) {
  return request({
    url: '/monitor/cache/batch',
    method: 'delete',
    data: keys
  })
}

/**
 * 清空所有缓存
 */
export function clearAllCache() {
  return request({
    url: '/monitor/cache/clear',
    method: 'delete'
  })
}

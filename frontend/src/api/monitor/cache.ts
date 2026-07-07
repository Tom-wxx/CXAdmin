import request from '@/utils/request'
import type { Result } from '@/types/api'
import type { CacheInfo, CacheValue } from '@/types/monitor/cache'

/** 获取缓存列表 */
export function getCacheList(): Promise<Result<CacheInfo[]>> {
  return request({ url: '/monitor/cache/list', method: 'get' })
}

/** 获取键名列表 */
export function getKeyList(params: { cacheName?: string; pattern?: string }): Promise<Result<string[]>> {
  return request({ url: '/monitor/cache/keys', method: 'get', params })
}

/** 获取缓存内容 */
export function getCacheValue(key: string): Promise<Result<CacheValue>> {
  return request({ url: '/monitor/cache/value', method: 'get', params: { key } })
}

/** 删除缓存 */
export function deleteCache(key: string): Promise<Result<void>> {
  return request({ url: `/monitor/cache/${key}`, method: 'delete' })
}

/** 批量删除缓存 */
export function batchDeleteCache(keys: string[]): Promise<Result<void>> {
  return request({ url: '/monitor/cache/batch', method: 'delete', data: keys })
}

/** 清空所有缓存 */
export function clearAllCache(): Promise<Result<void>> {
  return request({ url: '/monitor/cache/clear', method: 'delete' })
}

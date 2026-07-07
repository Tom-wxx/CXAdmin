/** Redis 缓存分类信息（形状随后端，组件转换时细化） */
export interface CacheInfo {
  cacheName?: string
  cacheKey?: string
  remark?: string
  [key: string]: unknown
}

/** 缓存键值内容 */
export interface CacheValue {
  cacheName?: string
  cacheKey?: string
  cacheValue?: unknown
  [key: string]: unknown
}

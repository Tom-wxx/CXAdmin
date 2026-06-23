import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { OnlineUser, OnlineQuery } from '@/types/monitor/online'

/** 查询在线用户列表 */
export function listOnlineUser(query: OnlineQuery): Promise<Result<PageResult<OnlineUser>>> {
  return request({ url: '/monitor/online/list', method: 'get', params: query })
}

/** 强制退出用户 */
export function forceLogout(tokenId: string): Promise<Result<void>> {
  return request({ url: '/monitor/online/' + tokenId, method: 'delete' })
}

/** 批量强制退出用户 */
export function batchForceLogout(tokenIds: string | string[]): Promise<Result<void>> {
  return request({ url: '/monitor/online/batch/' + tokenIds, method: 'delete' })
}

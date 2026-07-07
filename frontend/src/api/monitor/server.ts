import request from '@/utils/request'
import type { Result } from '@/types/api'
import type { ServerInfo } from '@/types/monitor/server'

/** 获取服务器监控信息 */
export function getServerInfo(): Promise<Result<ServerInfo>> {
  return request({ url: '/monitor/server', method: 'get' })
}

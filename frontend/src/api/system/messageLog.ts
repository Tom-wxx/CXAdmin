import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { MessageLog, MessageLogQuery } from '@/types/system/message'

/** 分页查询消息发送日志列表 */
export function listMessageLog(query: MessageLogQuery): Promise<TableResponse<MessageLog>> {
  return request({ url: '/system/message/log/list', method: 'get', params: query })
}

/** 查询消息发送日志详细信息 */
export function getMessageLog(logId: number): Promise<Result<MessageLog>> {
  return request({ url: '/system/message/log/' + logId, method: 'get' })
}

/** 删除消息发送日志 */
export function delMessageLog(logIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/message/log/' + logIds, method: 'delete' })
}

/** 清空消息发送日志 */
export function cleanMessageLog(): Promise<Result<void>> {
  return request({ url: '/system/message/log/clean', method: 'delete' })
}

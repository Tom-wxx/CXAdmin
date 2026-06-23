import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { Message, MessageQuery } from '@/types/system/message'

/** 分页查询消息模板列表 */
export function listMessage(query: MessageQuery): Promise<Result<PageResult<Message>>> {
  return request({ url: '/system/message/list', method: 'get', params: query })
}

/** 查询消息模板详细信息 */
export function getMessage(messageId: number): Promise<Result<Message>> {
  return request({ url: '/system/message/' + messageId, method: 'get' })
}

/** 新增消息模板 */
export function addMessage(data: Message): Promise<Result<void>> {
  return request({ url: '/system/message', method: 'post', data })
}

/** 修改消息模板 */
export function updateMessage(data: Message): Promise<Result<void>> {
  return request({ url: '/system/message', method: 'put', data })
}

/** 删除消息模板 */
export function delMessage(messageIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/message/' + messageIds, method: 'delete' })
}

/** 修改消息模板状态 */
export function changeMessageStatus(data: { messageId: number; status: string }): Promise<Result<void>> {
  return request({ url: '/system/message/changeStatus', method: 'put', data })
}

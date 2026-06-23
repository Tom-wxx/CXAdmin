import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { MessageConfig, MessageConfigQuery, MessageTest } from '@/types/system/message'

/** 分页查询消息配置列表 */
export function listMessageConfig(query: MessageConfigQuery): Promise<Result<PageResult<MessageConfig>>> {
  return request({ url: '/system/message/config/list', method: 'get', params: query })
}

/** 查询消息配置详细信息 */
export function getMessageConfig(configId: number): Promise<Result<MessageConfig>> {
  return request({ url: '/system/message/config/' + configId, method: 'get' })
}

/** 新增消息配置 */
export function addMessageConfig(data: MessageConfig): Promise<Result<void>> {
  return request({ url: '/system/message/config', method: 'post', data })
}

/** 修改消息配置 */
export function updateMessageConfig(data: MessageConfig): Promise<Result<void>> {
  return request({ url: '/system/message/config', method: 'put', data })
}

/** 删除消息配置 */
export function delMessageConfig(configIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/message/config/' + configIds, method: 'delete' })
}

/** 修改消息配置状态 */
export function changeConfigStatus(data: { configId: number; status: string }): Promise<Result<void>> {
  return request({ url: '/system/message/config/changeStatus', method: 'put', data })
}

/** 设置为默认配置 */
export function setDefaultConfig(configId: number): Promise<Result<void>> {
  return request({ url: '/system/message/config/setDefault/' + configId, method: 'put' })
}

/** 测试发送消息 */
export function testSendMessage(data: MessageTest): Promise<Result<void>> {
  return request({ url: '/system/message/config/test', method: 'post', data })
}

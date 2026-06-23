import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { NotificationTemplate, NotificationTemplateQuery } from '@/types/system/notification'

/** 分页查询模板列表 */
export function listTemplate(query: NotificationTemplateQuery): Promise<Result<PageResult<NotificationTemplate>>> {
  return request({ url: '/system/notification/template/list', method: 'get', params: query })
}

/** 查询所有模板 */
export function getAllTemplates(): Promise<Result<NotificationTemplate[]>> {
  return request({ url: '/system/notification/template/all', method: 'get' })
}

/** 查询模板详情 */
export function getTemplate(id: number): Promise<Result<NotificationTemplate>> {
  return request({ url: '/system/notification/template/' + id, method: 'get' })
}

/** 新增模板 */
export function addTemplate(data: NotificationTemplate): Promise<Result<void>> {
  return request({ url: '/system/notification/template', method: 'post', data })
}

/** 修改模板 */
export function updateTemplate(data: NotificationTemplate): Promise<Result<void>> {
  return request({ url: '/system/notification/template', method: 'put', data })
}

/** 删除模板 */
export function delTemplate(ids: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/notification/template/' + ids, method: 'delete' })
}

/** 修改模板状态 */
export function changeTemplateStatus(id: number, status: string): Promise<Result<void>> {
  return request({ url: '/system/notification/template/status/' + id + '/' + status, method: 'put' })
}

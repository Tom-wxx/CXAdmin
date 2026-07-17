import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { NotificationTemplate, NotificationTemplateQuery } from '@/types/system/notification'

export function listTemplate(query: NotificationTemplateQuery): Promise<TableResponse<NotificationTemplate>> {
  return request({ url: '/system/notification/template/list', method: 'get', params: query })
}

export function getTemplate(id: number): Promise<Result<NotificationTemplate>> {
  return request({ url: '/system/notification/template/' + id, method: 'get' })
}

export function addTemplate(data: NotificationTemplate): Promise<Result<void>> {
  return request({ url: '/system/notification/template', method: 'post', data })
}

export function updateTemplate(data: NotificationTemplate): Promise<Result<void>> {
  return request({ url: '/system/notification/template', method: 'put', data })
}

export function delTemplate(ids: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/notification/template/' + ids, method: 'delete' })
}

export function changeTemplateStatus(id: number, status: string): Promise<Result<void>> {
  return request({ url: '/system/notification/template/status/' + id + '/' + status, method: 'put' })
}

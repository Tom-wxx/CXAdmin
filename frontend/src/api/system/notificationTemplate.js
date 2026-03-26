import request from '@/utils/request'

// 分页查询模板列表
export function listTemplate(query) {
  return request({
    url: '/system/notification/template/list',
    method: 'get',
    params: query
  })
}

// 查询所有模板
export function getAllTemplates() {
  return request({
    url: '/system/notification/template/all',
    method: 'get'
  })
}

// 查询模板详情
export function getTemplate(id) {
  return request({
    url: '/system/notification/template/' + id,
    method: 'get'
  })
}

// 新增模板
export function addTemplate(data) {
  return request({
    url: '/system/notification/template',
    method: 'post',
    data: data
  })
}

// 修改模板
export function updateTemplate(data) {
  return request({
    url: '/system/notification/template',
    method: 'put',
    data: data
  })
}

// 删除模板
export function delTemplate(ids) {
  return request({
    url: '/system/notification/template/' + ids,
    method: 'delete'
  })
}

// 修改模板状态
export function changeTemplateStatus(id, status) {
  return request({
    url: '/system/notification/template/status/' + id + '/' + status,
    method: 'put'
  })
}

import request from '@/utils/request'

// 获取流程定义列表
export function listDefinition(query) {
  return request({
    url: '/workflow/definition/list',
    method: 'get',
    params: query
  })
}

// 获取流程定义详情
export function getDefinition(id) {
  return request({
    url: `/workflow/definition/${id}`,
    method: 'get'
  })
}

// 新增流程定义
export function addDefinition(data) {
  return request({
    url: '/workflow/definition',
    method: 'post',
    data: data
  })
}

// 修改流程定义
export function updateDefinition(data) {
  return request({
    url: '/workflow/definition',
    method: 'put',
    data: data
  })
}

// 删除流程定义
export function delDefinition(id) {
  return request({
    url: `/workflow/definition/${id}`,
    method: 'delete'
  })
}

// 发布流程定义
export function publishDefinition(id) {
  return request({
    url: `/workflow/definition/publish/${id}`,
    method: 'post'
  })
}

// 停用流程定义
export function disableDefinition(id) {
  return request({
    url: `/workflow/definition/disable/${id}`,
    method: 'post'
  })
}

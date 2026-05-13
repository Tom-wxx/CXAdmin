import request from '@/utils/request'

// 登录页用：公开 IdP 列表
export function listEnabledProviders() {
  return request({ url: '/sso/providers', method: 'get' })
}

// 管理：CRUD
export function listProviders(query) {
  return request({ url: '/system/sso/list', method: 'get', params: query })
}
export function getProvider(id) {
  return request({ url: '/system/sso/' + id, method: 'get' })
}
export function addProvider(data) {
  return request({ url: '/system/sso', method: 'post', data })
}
export function updateProvider(data) {
  return request({ url: '/system/sso', method: 'put', data })
}
export function delProvider(id) {
  return request({ url: '/system/sso/' + id, method: 'delete' })
}

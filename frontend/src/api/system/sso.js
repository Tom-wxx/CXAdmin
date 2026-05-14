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

// 个人中心：当前用户的绑定
export function listMyBindings() {
  return request({ url: '/sso/bindings/me', method: 'get' })
}
export function unbindBinding(id) {
  return request({ url: '/sso/bindings/' + id, method: 'delete' })
}
// 已登录用户发起绑定，后端返回 IdP 授权 URL，前端 location 跳过去
export function startBind(code) {
  return request({ url: '/sso/bind/' + code, method: 'get' })
}

// 审计日志
export function listSsoLogs(query) {
  return request({ url: '/system/sso/log/list', method: 'get', params: query })
}

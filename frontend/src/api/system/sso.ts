import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type {
  SsoProvider,
  SsoProviderQuery,
  SsoProviderPublic,
  SsoBinding,
  SsoLoginLog,
  SsoLogQuery
} from '@/types/system/sso'

/** 登录页用：公开 IdP 列表 */
export function listEnabledProviders(): Promise<Result<SsoProviderPublic[]>> {
  return request({ url: '/sso/providers', method: 'get' })
}

/** 管理：分页查询 IdP 列表 */
export function listProviders(query: SsoProviderQuery): Promise<TableResponse<SsoProvider>> {
  return request({ url: '/system/sso/list', method: 'get', params: query })
}

/** 管理：查询 IdP 详情 */
export function getProvider(id: number): Promise<Result<SsoProvider>> {
  return request({ url: '/system/sso/' + id, method: 'get' })
}

/** 管理：新增 IdP */
export function addProvider(data: SsoProvider): Promise<Result<void>> {
  return request({ url: '/system/sso', method: 'post', data })
}

/** 管理：修改 IdP */
export function updateProvider(data: SsoProvider): Promise<Result<void>> {
  return request({ url: '/system/sso', method: 'put', data })
}

/** 管理：删除 IdP */
export function delProvider(id: number): Promise<Result<void>> {
  return request({ url: '/system/sso/' + id, method: 'delete' })
}

/** 个人中心：当前用户的绑定列表 */
export function listMyBindings(): Promise<Result<SsoBinding[]>> {
  return request({ url: '/sso/bindings/me', method: 'get' })
}

/** 个人中心：解绑 */
export function unbindBinding(id: number): Promise<Result<void>> {
  return request({ url: '/sso/bindings/' + id, method: 'delete' })
}

/** 已登录用户发起绑定，返回 IdP 授权 URL */
export function startBind(code: string): Promise<Result<string>> {
  return request({ url: '/sso/bind/' + code, method: 'get' })
}

/** 审计日志分页查询 */
export function listSsoLogs(query: SsoLogQuery): Promise<TableResponse<SsoLoginLog>> {
  return request({ url: '/system/sso/log/list', method: 'get', params: query })
}

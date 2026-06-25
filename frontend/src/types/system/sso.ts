import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** SSO 身份提供商（对应后端 sys_sso_provider / SysSsoProvider） */
export interface SsoProvider extends BaseEntity {
  id?: number
  code?: string
  name?: string
  type?: string
  icon?: string
  clientId?: string
  /** 写入时为明文，读取时为 AES-GCM 密文（一般不回显） */
  clientSecret?: string
  authorizationUri?: string
  tokenUri?: string
  userinfoUri?: string
  emailsUri?: string
  scope?: string
  /** 是否启用 PKCE(S256) 0否 1是 */
  enablePkce?: number
  userFieldMapping?: string
  defaultRoleId?: number
  defaultDeptId?: number
  /** 是否启用 0否 1是 */
  enabled?: number
  orderNum?: number
}

/** SSO 提供商查询参数 */
export interface SsoProviderQuery extends PageQuery {
  code?: string
  name?: string
  type?: string
  enabled?: number
}

/** 登录页公开展示的 IdP（对应后端 SsoProviderPublicVO） */
export interface SsoProviderPublic {
  code?: string
  name?: string
  icon?: string
  orderNum?: number
}

/** 用户 SSO 绑定记录（对应后端 SsoBindingVO） */
export interface SsoBinding {
  id?: number
  providerId?: number
  providerCode?: string
  providerName?: string
  providerIcon?: string
  externalUserId?: string
  externalUsername?: string
  email?: string
  bindTime?: string
}

/** SSO 审计日志（对应后端 sys_sso_login_log / SysSsoLoginLog） */
export interface SsoLoginLog {
  id?: number
  providerId?: number
  providerCode?: string
  userId?: number
  externalUserId?: string
  /** authorize/callback/bind/unbind */
  action?: string
  /** login/bind */
  mode?: string
  /** success/fail */
  status?: string
  ip?: string
  userAgent?: string
  errorMessage?: string
  createTime?: string
}

/** SSO 日志查询参数 */
export interface SsoLogQuery extends PageQuery {
  providerCode?: string
  action?: string
  status?: string
}

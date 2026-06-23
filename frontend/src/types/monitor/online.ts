import type { PageQuery } from '@/types/api'

/** 在线用户 */
export interface OnlineUser {
  tokenId?: string
  username?: string
  deptName?: string
  ipaddr?: string
  loginLocation?: string
  browser?: string
  os?: string
  loginTime?: number | string
}

/** 在线用户查询参数 */
export interface OnlineQuery extends PageQuery {
  ipaddr?: string
  username?: string
}

import type { PageQuery } from '@/types/api'

/** 登录日志（对应后端 sys_login_log / SysLoginLog） */
export interface LoginLog {
  infoId?: number
  username?: string
  ipaddr?: string
  loginLocation?: string
  browser?: string
  os?: string
  /** 登录状态 0成功 1失败 */
  status?: string
  msg?: string
  loginTime?: string
}

/** 登录日志查询参数 */
export interface LoginLogQuery extends PageQuery {
  username?: string
  ipaddr?: string
  status?: string
}

/** 操作日志（对应后端 sys_oper_log / SysOperLog） */
export interface OperLog {
  operId?: number
  title?: string
  /** 业务类型 0其它 1新增 2修改 3删除 */
  businessType?: number
  method?: string
  requestMethod?: string
  operatorType?: number
  operName?: string
  deptName?: string
  operUrl?: string
  operIp?: string
  operLocation?: string
  operParam?: string
  jsonResult?: string
  /** 操作状态 0正常 1异常 */
  status?: number
  errorMsg?: string
  operTime?: string
}

/** 操作日志查询参数 */
export interface OperLogQuery extends PageQuery {
  title?: string
  operName?: string
  businessType?: number
  status?: number
}

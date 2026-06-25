/**
 * 后端统一响应与分页类型
 */

/** 后端统一响应包装：{ code, message, data, timestamp } */
export interface Result<T = unknown> {
  code: number
  message: string
  data: T
  timestamp: number
}

/**
 * 列表分页响应：后端对分页接口直接在响应体顶层返回 rows/total
 * （响应拦截器把整个 body 解包返回，故调用方读 res.rows / res.total）。
 */
export interface TableResponse<T = unknown> {
  code: number
  message?: string
  rows: T[]
  total: number
}

/** 列表查询通用分页参数 */
export interface PageQuery {
  current?: number
  size?: number
}

/** 下拉树选项（部门/菜单 treeselect 等返回） */
export interface TreeOption {
  id: number
  label: string
  children?: TreeOption[]
}

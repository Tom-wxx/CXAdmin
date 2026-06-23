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

/** MyBatis-Plus 分页结果 */
export interface PageResult<T = unknown> {
  records: T[]
  total: number
  current: number
  size: number
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

import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 角色对象（对应后端 sys_role / SysRole） */
export interface Role extends BaseEntity {
  roleId?: number
  roleName?: string
  roleKey?: string
  roleSort?: number
  /** 数据范围 1所有 2自定义 3本部门 4本部门及以下 5仅本人 */
  dataScope?: string
  menuCheckStrictly?: boolean
  deptCheckStrictly?: boolean
  status?: string
  /** 非数据库字段 */
  menuIds?: number[]
  deptIds?: number[]
  flag?: boolean
}

/** 角色列表查询参数 */
export interface RoleQuery extends PageQuery {
  roleName?: string
  roleKey?: string
  status?: string
}

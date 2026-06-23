import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 用户对象（对应后端 sys_user / SysUser） */
export interface User extends BaseEntity {
  userId?: number
  deptId?: number
  username?: string
  nickname?: string
  userType?: string
  email?: string
  phonenumber?: string
  sex?: string
  avatar?: string
  /** 仅写入，响应中不返回 */
  password?: string
  status?: string
  loginIp?: string
  loginDate?: string
  /** 非数据库字段：所选岗位/角色 */
  postIds?: number[]
  roleIds?: number[]
  roleId?: number
}

/** 用户列表查询参数 */
export interface UserQuery extends PageQuery {
  username?: string
  phone?: string
  status?: string
}

/** 用户表单下拉选项（部门树 / 岗位 / 角色） */
export interface DeptOption {
  deptId: number
  deptName: string
  children?: DeptOption[]
}
export interface PostOption {
  postId: number
  postName: string
}
export interface RoleOption {
  roleId: number
  roleName: string
}
export interface UserFormOptions {
  depts: DeptOption[]
  posts: PostOption[]
  roles: RoleOption[]
}

import type { BaseEntity } from '@/types/base'

/** 部门对象（对应后端 sys_dept / SysDept） */
export interface Dept extends BaseEntity {
  deptId?: number
  parentId?: number
  ancestors?: string
  deptName?: string
  orderNum?: number
  leader?: string
  phone?: string
  email?: string
  status?: string
  /** 非数据库字段 */
  parentName?: string
  children?: Dept[]
}

/** 部门列表查询参数 */
export interface DeptQuery {
  deptName?: string
  status?: string
}

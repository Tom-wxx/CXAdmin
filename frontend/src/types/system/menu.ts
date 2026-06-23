import type { BaseEntity } from '@/types/base'

/** 菜单对象（对应后端 sys_menu / SysMenu） */
export interface Menu extends BaseEntity {
  menuId?: number
  menuName?: string
  parentId?: number
  orderNum?: number
  path?: string
  component?: string
  query?: string
  /** 是否外链（0是 1否） */
  isFrame?: number
  /** 是否缓存（0缓存 1不缓存） */
  isCache?: number
  /** 类型 M目录 C菜单 F按钮 */
  menuType?: string
  /** 显示状态 0显示 1隐藏 */
  visible?: string
  status?: string
  perms?: string
  icon?: string
  /** 非数据库字段 */
  parentName?: string
  children?: Menu[]
}

/** 菜单列表查询参数 */
export interface MenuQuery {
  menuName?: string
  status?: string
}

import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 岗位对象（对应后端 sys_post / SysPost） */
export interface Post extends BaseEntity {
  postId?: number
  postCode?: string
  postName?: string
  postSort?: number
  status?: string
}

/** 岗位列表查询参数 */
export interface PostQuery extends PageQuery {
  postCode?: string
  postName?: string
  status?: string
}

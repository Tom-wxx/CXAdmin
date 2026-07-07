import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 通知公告对象（对应后端 sys_notice / SysNotice） */
export interface Notice extends BaseEntity {
  noticeId?: number
  noticeTitle?: string
  /** 公告类型 1通知 2公告 */
  noticeType?: string
  noticeContent?: string
  status?: string
}

/** 通知公告查询参数 */
export interface NoticeQuery extends PageQuery {
  noticeTitle?: string
  noticeType?: string
  status?: string
}

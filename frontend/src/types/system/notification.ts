import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 站内通知对象（对应后端 sys_notification / SysNotification） */
export interface Notification extends BaseEntity {
  id?: number
  title?: string
  content?: string
  /** 通知类型 system/todo/approval/announce */
  type?: string
  /** 优先级 normal/important/urgent */
  priority?: string
  /** 状态 unread/read */
  status?: string
  userId?: number
  senderId?: number
  senderName?: string
  linkUrl?: string
  linkType?: string
  readTime?: string
}

/** 通知查询参数 */
export interface NotificationQuery extends PageQuery {
  title?: string
  type?: string
  status?: string
}

/** 发送通知请求体（单发 / 批量） */
export interface NotificationSend {
  title?: string
  content?: string
  type?: string
  priority?: string
  userId?: number
  userIds?: number[]
  linkUrl?: string
  linkType?: string
}

/** 通知模板对象（对应后端 sys_notification_template / SysNotificationTemplate） */
export interface NotificationTemplate extends BaseEntity {
  id?: number
  templateCode?: string
  templateName?: string
  title?: string
  content?: string
  type?: string
  priority?: string
  /** 状态 0停用 1启用 */
  status?: string
}

/** 通知模板查询参数 */
export interface NotificationTemplateQuery extends PageQuery {
  templateName?: string
  type?: string
  status?: string
}

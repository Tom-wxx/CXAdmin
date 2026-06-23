import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 消息模板对象（对应后端 sys_message / SysMessage） */
export interface Message extends BaseEntity {
  messageId?: number
  messageName?: string
  messageCode?: string
  /** 消息类型 1邮件 2短信 3站内信 4微信 */
  messageType?: string
  subject?: string
  content?: string
  variables?: string
  status?: string
}

/** 消息模板查询参数 */
export interface MessageQuery extends PageQuery {
  messageName?: string
  messageType?: string
  status?: string
}

/** 消息渠道配置对象（对应后端 sys_message_config / SysMessageConfig） */
export interface MessageConfig extends BaseEntity {
  configId?: number
  messageType?: string
  configName?: string
  /** 配置数据 JSON */
  configData?: string
  /** 是否默认 0否 1是 */
  isDefault?: string
  status?: string
}

/** 消息配置查询参数 */
export interface MessageConfigQuery extends PageQuery {
  configName?: string
  messageType?: string
  status?: string
}

/** 测试发送消息请求体 */
export interface MessageTest {
  messageType?: string
  configId?: number
  receiver?: string
  subject?: string
  content?: string
}

/** 消息发送日志对象（对应后端 sys_message_log / SysMessageLog，不含 BaseEntity 审计列） */
export interface MessageLog {
  logId?: number
  messageId?: number
  messageType?: string
  receiver?: string
  subject?: string
  content?: string
  /** 发送状态 0成功 1失败 2发送中 */
  sendStatus?: string
  sendTime?: string
  errorMsg?: string
  retryCount?: number
  sendBy?: number
  createTime?: string
}

/** 消息日志查询参数 */
export interface MessageLogQuery extends PageQuery {
  messageType?: string
  receiver?: string
  sendStatus?: string
}

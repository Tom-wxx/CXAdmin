/**
 * 系统常量定义
 * 将分散在各处的魔法字符串和硬编码值集中管理
 */

/** 通用状态 - 正常/启用 */
export const STATUS_NORMAL = '0'
/** 通用状态 - 停用/禁用 */
export const STATUS_DISABLED = '1'

/** 操作结果状态 - 成功 */
export const RESULT_SUCCESS = '0'
/** 操作结果状态 - 失败 */
export const RESULT_FAIL = '1'

/** 公告类型 - 通知 */
export const NOTICE_TYPE_NOTICE = '1'
/** 公告类型 - 公告 */
export const NOTICE_TYPE_ANNOUNCEMENT = '2'

/** 消息类型 - 邮件 */
export const MESSAGE_TYPE_EMAIL = '1'
/** 消息类型 - 短信 */
export const MESSAGE_TYPE_SMS = '2'
/** 消息类型 - 站内信 */
export const MESSAGE_TYPE_SYSTEM = '3'
/** 消息类型 - 微信 */
export const MESSAGE_TYPE_WECHAT = '4'

/** HTTP 响应码 */
export const HTTP_OK = 200
export const HTTP_UNAUTHORIZED = 401
export const HTTP_SERVER_ERROR = 500

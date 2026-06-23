/**
 * 所有实体共有的审计字段（对应后端 BaseEntity，响应中通常出现）
 */
export interface BaseEntity {
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
}

import type { PageQuery } from '@/types/api'

/** 代码生成 - 数据库表信息（字段较多，组件转换时按需细化） */
export interface GenTable {
  tableName?: string
  tableComment?: string
  className?: string
  [key: string]: unknown
}

/** 代码生成表查询参数 */
export interface GenTableQuery extends PageQuery {
  tableName?: string
  tableComment?: string
}

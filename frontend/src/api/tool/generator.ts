import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { GenTable, GenTableQuery } from '@/types/tool/generator'

/** 查询数据库表列表 */
export function listTable(query: GenTableQuery): Promise<TableResponse<GenTable>> {
  return request({ url: '/tool/generator/list', method: 'get', params: query })
}

/** 查询表详细信息 */
export function getTable(tableName: string): Promise<Result<GenTable>> {
  return request({ url: '/tool/generator/' + tableName, method: 'get' })
}

/** 生成代码（二进制流） */
export function generateCode(data: GenTable): Promise<Blob> {
  return request({ url: '/tool/generator/generate', method: 'post', data, responseType: 'blob' }) as unknown as Promise<Blob>
}

/** 根据自定义表信息生成代码（二进制流） */
export function generateCustomCode(data: GenTable): Promise<Blob> {
  return request({ url: '/tool/generator/generateCustom', method: 'post', data, responseType: 'blob' }) as unknown as Promise<Blob>
}

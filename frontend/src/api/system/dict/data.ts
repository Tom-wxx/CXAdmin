import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { DictData, DictDataQuery } from '@/types/system/dict'

/** 分页查询字典数据列表 */
export function listDictData(query: DictDataQuery): Promise<TableResponse<DictData>> {
  return request({ url: '/system/dict/data/list', method: 'get', params: query })
}

/** 根据字典类型查询字典数据 */
export function getDictDataByType(dictType: string): Promise<Result<DictData[]>> {
  return request({ url: '/system/dict/data/type/' + dictType, method: 'get' })
}

/** 查询字典数据详细信息 */
export function getDictData(dictCode: number): Promise<Result<DictData>> {
  return request({ url: '/system/dict/data/' + dictCode, method: 'get' })
}

/** 新增字典数据 */
export function addDictData(data: DictData): Promise<Result<void>> {
  return request({ url: '/system/dict/data', method: 'post', data })
}

/** 修改字典数据 */
export function updateDictData(data: DictData): Promise<Result<void>> {
  return request({ url: '/system/dict/data', method: 'put', data })
}

/** 删除字典数据 */
export function delDictData(dictCodes: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/dict/data/' + dictCodes, method: 'delete' })
}

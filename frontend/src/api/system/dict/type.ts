import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { DictType, DictTypeQuery } from '@/types/system/dict'

/** 分页查询字典类型列表 */
export function listDictType(query: DictTypeQuery): Promise<TableResponse<DictType>> {
  return request({ url: '/system/dict/type/list', method: 'get', params: query })
}

/** 查询所有字典类型 */
export function listAllDictType(): Promise<Result<DictType[]>> {
  return request({ url: '/system/dict/type/listAll', method: 'get' })
}

/** 查询字典类型详细信息 */
export function getDictType(dictId: number): Promise<Result<DictType>> {
  return request({ url: '/system/dict/type/' + dictId, method: 'get' })
}

/** 新增字典类型 */
export function addDictType(data: DictType): Promise<Result<void>> {
  return request({ url: '/system/dict/type', method: 'post', data })
}

/** 修改字典类型 */
export function updateDictType(data: DictType): Promise<Result<void>> {
  return request({ url: '/system/dict/type', method: 'put', data })
}

/** 删除字典类型 */
export function delDictType(dictIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/dict/type/' + dictIds, method: 'delete' })
}

/** 校验字典类型是否唯一 */
export function checkDictTypeUnique(dictType: string, dictId?: number): Promise<Result<boolean>> {
  return request({ url: '/system/dict/type/checkDictTypeUnique', method: 'get', params: { dictType, dictId } })
}

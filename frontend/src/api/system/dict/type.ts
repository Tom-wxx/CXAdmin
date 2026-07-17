import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { DictType, DictTypeQuery } from '@/types/system/dict'

export function listDictType(query: DictTypeQuery): Promise<TableResponse<DictType>> {
  return request({ url: '/system/dict/type/list', method: 'get', params: query })
}

export function getDictType(dictId: number): Promise<Result<DictType>> {
  return request({ url: '/system/dict/type/' + dictId, method: 'get' })
}

export function addDictType(data: DictType): Promise<Result<void>> {
  return request({ url: '/system/dict/type', method: 'post', data })
}

export function updateDictType(data: DictType): Promise<Result<void>> {
  return request({ url: '/system/dict/type', method: 'put', data })
}

export function delDictType(dictIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/dict/type/' + dictIds, method: 'delete' })
}

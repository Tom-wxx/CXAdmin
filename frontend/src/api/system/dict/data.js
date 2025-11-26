import request from '@/utils/request'

/**
 * 分页查询字典数据列表
 */
export function listDictData(query) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params: query
  })
}

/**
 * 根据字典类型查询字典数据
 */
export function getDictDataByType(dictType) {
  return request({
    url: '/system/dict/data/type/' + dictType,
    method: 'get'
  })
}

/**
 * 查询字典数据详细信息
 */
export function getDictData(dictCode) {
  return request({
    url: '/system/dict/data/' + dictCode,
    method: 'get'
  })
}

/**
 * 新增字典数据
 */
export function addDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data: data
  })
}

/**
 * 修改字典数据
 */
export function updateDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data: data
  })
}

/**
 * 删除字典数据
 */
export function delDictData(dictCodes) {
  return request({
    url: '/system/dict/data/' + dictCodes,
    method: 'delete'
  })
}

import request from '@/utils/request'

/**
 * 分页查询字典类型列表
 */
export function listDictType(query) {
  return request({
    url: '/system/dict/type/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询所有字典类型
 */
export function listAllDictType() {
  return request({
    url: '/system/dict/type/listAll',
    method: 'get'
  })
}

/**
 * 查询字典类型详细信息
 */
export function getDictType(dictId) {
  return request({
    url: '/system/dict/type/' + dictId,
    method: 'get'
  })
}

/**
 * 新增字典类型
 */
export function addDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'post',
    data: data
  })
}

/**
 * 修改字典类型
 */
export function updateDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'put',
    data: data
  })
}

/**
 * 删除字典类型
 */
export function delDictType(dictIds) {
  return request({
    url: '/system/dict/type/' + dictIds,
    method: 'delete'
  })
}

/**
 * 校验字典类型是否唯一
 */
export function checkDictTypeUnique(dictType, dictId) {
  return request({
    url: '/system/dict/type/checkDictTypeUnique',
    method: 'get',
    params: {
      dictType: dictType,
      dictId: dictId
    }
  })
}

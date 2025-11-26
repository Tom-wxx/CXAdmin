import request from '@/utils/request'

// 查询数据库表列表
export function listTable(query) {
  return request({
    url: '/tool/generator/list',
    method: 'get',
    params: query
  })
}

// 查询表详细信息
export function getTable(tableName) {
  return request({
    url: '/tool/generator/' + tableName,
    method: 'get'
  })
}

// 生成代码
export function generateCode(data) {
  return request({
    url: '/tool/generator/generate',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

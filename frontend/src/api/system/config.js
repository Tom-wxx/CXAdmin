import request from '@/utils/request'

/**
 * 分页查询参数配置列表
 */
export function listConfig(query) {
  return request({
    url: '/system/config/list',
    method: 'get',
    params: query
  })
}

/**
 * 根据参数键名查询参数值
 */
export function getConfigKey(configKey) {
  return request({
    url: '/system/config/configKey/' + configKey,
    method: 'get'
  })
}

/**
 * 查询参数配置详细信息
 */
export function getConfig(configId) {
  return request({
    url: '/system/config/' + configId,
    method: 'get'
  })
}

/**
 * 新增参数配置
 */
export function addConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    data: data
  })
}

/**
 * 修改参数配置
 */
export function updateConfig(data) {
  return request({
    url: '/system/config',
    method: 'put',
    data: data
  })
}

/**
 * 删除参数配置
 */
export function delConfig(configIds) {
  return request({
    url: '/system/config/' + configIds,
    method: 'delete'
  })
}

/**
 * 校验参数键名是否唯一
 */
export function checkConfigKeyUnique(configKey, configId) {
  return request({
    url: '/system/config/checkConfigKeyUnique',
    method: 'get',
    params: {
      configKey: configKey,
      configId: configId
    }
  })
}

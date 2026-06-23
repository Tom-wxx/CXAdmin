import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { Config, ConfigQuery } from '@/types/system/config'

/** 分页查询参数配置列表 */
export function listConfig(query: ConfigQuery): Promise<Result<PageResult<Config>>> {
  return request({ url: '/system/config/list', method: 'get', params: query })
}

/** 根据参数键名查询参数值 */
export function getConfigKey(configKey: string): Promise<Result<string>> {
  return request({ url: '/system/config/configKey/' + configKey, method: 'get' })
}

/** 查询参数配置详细信息 */
export function getConfig(configId: number): Promise<Result<Config>> {
  return request({ url: '/system/config/' + configId, method: 'get' })
}

/** 新增参数配置 */
export function addConfig(data: Config): Promise<Result<void>> {
  return request({ url: '/system/config', method: 'post', data })
}

/** 修改参数配置 */
export function updateConfig(data: Config): Promise<Result<void>> {
  return request({ url: '/system/config', method: 'put', data })
}

/** 删除参数配置 */
export function delConfig(configIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/config/' + configIds, method: 'delete' })
}

/** 校验参数键名是否唯一 */
export function checkConfigKeyUnique(configKey: string, configId?: number): Promise<Result<boolean>> {
  return request({ url: '/system/config/checkConfigKeyUnique', method: 'get', params: { configKey, configId } })
}

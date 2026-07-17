import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { LoginPetType } from '@/types/login-pet'
import type { Config, ConfigQuery, LoginPetTypeUpdateBody } from '@/types/system/config'

export function listConfig(query: ConfigQuery): Promise<TableResponse<Config>> {
  return request({ url: '/system/config/list', method: 'get', params: query })
}

export function getConfig(configId: number): Promise<Result<Config>> {
  return request({ url: '/system/config/' + configId, method: 'get' })
}

export function addConfig(data: Config): Promise<Result<void>> {
  return request({ url: '/system/config', method: 'post', data })
}

export function updateConfig(data: Config): Promise<Result<void>> {
  return request({ url: '/system/config', method: 'put', data })
}

export function delConfig(configIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/config/' + configIds, method: 'delete' })
}

/** 公开读取登录宠物类型，失败时由调用方静默回退 */
export function getLoginPetType(): Promise<Result<LoginPetType>> {
  return request({
    url: '/system/config/public/login-pet',
    method: 'get',
    silent: true
  })
}

export function updateLoginPetType(type: LoginPetType): Promise<Result<void>> {
  const data: LoginPetTypeUpdateBody = { type }
  return request({ url: '/system/config/login-pet', method: 'put', data })
}

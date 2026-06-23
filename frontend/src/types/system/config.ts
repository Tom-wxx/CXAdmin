import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 参数配置对象（对应后端 sys_config / SysConfig） */
export interface Config extends BaseEntity {
  configId?: number
  configName?: string
  configKey?: string
  configValue?: string
  /** 系统内置 Y是 N否 */
  configType?: string
}

/** 参数配置查询参数 */
export interface ConfigQuery extends PageQuery {
  configName?: string
  configKey?: string
  configType?: string
}

import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 字典类型对象（对应后端 sys_dict_type / SysDictType） */
export interface DictType extends BaseEntity {
  dictId?: number
  dictName?: string
  dictType?: string
  status?: string
}

/** 字典类型查询参数 */
export interface DictTypeQuery extends PageQuery {
  dictName?: string
  dictType?: string
  status?: string
}

/** 字典数据对象（对应后端 sys_dict_data / SysDictData） */
export interface DictData extends BaseEntity {
  dictCode?: number
  dictSort?: number
  dictLabel?: string
  dictValue?: string
  dictType?: string
  cssClass?: string
  listClass?: string
  /** 是否默认 Y是 N否 */
  isDefault?: string
  status?: string
}

/** 字典数据查询参数 */
export interface DictDataQuery extends PageQuery {
  dictType?: string
  dictLabel?: string
  status?: string
}

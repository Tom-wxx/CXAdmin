import { ref } from 'vue'
import { getDictDataByType } from '@/api/system/dict/data'
import type { DictData } from '@/types/system/dict'

/** 进程级字典缓存，避免重复请求同一字典类型 */
const cache = new Map<string, DictData[]>()

/**
 * 加载一个或多个字典类型，返回按类型分组的响应式字典数据。
 * 用法：const dict = useDict('sys_user_sex', 'sys_normal_disable')
 *       dict.value['sys_user_sex']
 */
export function useDict(...types: string[]) {
  const dict = ref<Record<string, DictData[]>>({})
  types.forEach((type) => {
    const cached = cache.get(type)
    if (cached) {
      dict.value[type] = cached
      return
    }
    getDictDataByType(type).then((res) => {
      const list = res.data || []
      cache.set(type, list)
      dict.value[type] = list
    })
  })
  return dict
}

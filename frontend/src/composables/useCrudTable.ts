import { ref, reactive, type Ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Result, PageResult, PageQuery } from '@/types/api'

export interface CrudTableOptions<T, Q extends PageQuery> {
  /** 列表查询接口（返回分页结果） */
  listApi: (query: Q) => Promise<Result<PageResult<T>>>
  /** 删除接口（可选） */
  delApi?: (ids: number | number[]) => Promise<Result<unknown>>
  /** 查询参数默认值（current/size 已内置） */
  defaultQuery?: Partial<Q>
  /** 是否在创建时立即加载，默认 true */
  immediate?: boolean
}

/**
 * CRUD 列表通用逻辑：loading / list / total / queryParams + 查询/重置/删除。
 * 用法：const { list, total, loading, queryParams, getList, handleQuery, resetQuery, handleDelete }
 *        = useCrudTable<User, UserQuery>({ listApi: listUser, delApi: delUser })
 */
export function useCrudTable<T, Q extends PageQuery = PageQuery>(options: CrudTableOptions<T, Q>) {
  const loading = ref(false)
  const list = ref<T[]>([]) as Ref<T[]>
  const total = ref(0)
  const baseQuery: Record<string, unknown> = { current: 1, size: 10, ...(options.defaultQuery || {}) }
  const queryParams = reactive({ ...baseQuery }) as unknown as Q

  async function getList(): Promise<void> {
    loading.value = true
    try {
      const res = await options.listApi(queryParams)
      list.value = res.data.records
      total.value = res.data.total
    } finally {
      loading.value = false
    }
  }

  function handleQuery(): void {
    queryParams.current = 1
    getList()
  }

  function resetQuery(): void {
    const q = queryParams as Record<string, unknown>
    Object.keys(q).forEach((k) => {
      delete q[k]
    })
    Object.assign(q, baseQuery)
    handleQuery()
  }

  function handleDelete(ids: number | number[], confirmText = '确认删除选中数据吗？'): Promise<void> {
    const del = options.delApi
    if (!del) return Promise.resolve()
    return ElMessageBox.confirm(confirmText, '警告', { type: 'warning' })
      .then(async () => {
        await del(ids)
        ElMessage.success('删除成功')
        await getList()
      })
      .catch(() => {
        /* 用户取消 */
      })
  }

  if (options.immediate !== false) {
    getList()
  }

  return { loading, list, total, queryParams, getList, handleQuery, resetQuery, handleDelete }
}

import request from '@/utils/request'
import type { Result, TreeOption } from '@/types/api'
import type { Dept, DeptQuery } from '@/types/system/dept'

export function listDept(query?: DeptQuery): Promise<Result<Dept[]>> {
  return request({ url: '/system/dept/list', method: 'get', params: query })
}

export function getDept(deptId: number): Promise<Result<Dept>> {
  return request({ url: '/system/dept/' + deptId, method: 'get' })
}

export function addDept(data: Dept): Promise<Result<void>> {
  return request({ url: '/system/dept', method: 'post', data })
}

export function updateDept(data: Dept): Promise<Result<void>> {
  return request({ url: '/system/dept', method: 'put', data })
}

export function delDept(deptId: number): Promise<Result<void>> {
  return request({ url: '/system/dept/' + deptId, method: 'delete' })
}

export function treeSelect(query?: DeptQuery): Promise<Result<TreeOption[]>> {
  return request({ url: '/system/dept/treeSelect', method: 'get', params: query })
}

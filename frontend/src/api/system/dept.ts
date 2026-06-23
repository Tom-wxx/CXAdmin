import request from '@/utils/request'
import type { Result, TreeOption } from '@/types/api'
import type { Dept, DeptQuery } from '@/types/system/dept'

/** 查询部门列表 */
export function listDept(query?: DeptQuery): Promise<Result<Dept[]>> {
  return request({ url: '/system/dept/list', method: 'get', params: query })
}

/** 查询部门列表（树形结构） */
export function listDeptTree(query?: DeptQuery): Promise<Result<Dept[]>> {
  return request({ url: '/system/dept/treeList', method: 'get', params: query })
}

/** 查询部门详细信息 */
export function getDept(deptId: number): Promise<Result<Dept>> {
  return request({ url: '/system/dept/' + deptId, method: 'get' })
}

/** 新增部门 */
export function addDept(data: Dept): Promise<Result<void>> {
  return request({ url: '/system/dept', method: 'post', data })
}

/** 修改部门 */
export function updateDept(data: Dept): Promise<Result<void>> {
  return request({ url: '/system/dept', method: 'put', data })
}

/** 删除部门 */
export function delDept(deptId: number): Promise<Result<void>> {
  return request({ url: '/system/dept/' + deptId, method: 'delete' })
}

/** 查询部门下拉树列表 */
export function treeSelect(query?: DeptQuery): Promise<Result<TreeOption[]>> {
  return request({ url: '/system/dept/treeSelect', method: 'get', params: query })
}

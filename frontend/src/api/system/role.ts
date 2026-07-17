import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { Role, RoleQuery } from '@/types/system/role'

/** 分页查询角色列表 */
export function listRole(query: RoleQuery): Promise<TableResponse<Role>> {
  return request({ url: '/system/role/list', method: 'get', params: query })
}

/** 查询所有角色列表 */
export function listAllRole(): Promise<Result<Role[]>> {
  return request({ url: '/system/role/listAll', method: 'get' })
}

/** 查询角色详细信息 */
export function getRole(roleId: number): Promise<Result<Role>> {
  return request({ url: '/system/role/' + roleId, method: 'get' })
}

/** 查询角色已分配的菜单ID列表 */
export function getMenuIds(roleId: number): Promise<Result<number[]>> {
  return request({ url: '/system/role/menuIds/' + roleId, method: 'get' })
}

/** 新增角色 */
export function addRole(data: Role): Promise<Result<void>> {
  return request({ url: '/system/role', method: 'post', data })
}

/** 修改角色 */
export function updateRole(data: Role): Promise<Result<void>> {
  return request({ url: '/system/role', method: 'put', data })
}

/** 删除角色 */
export function delRole(roleIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/role/' + roleIds, method: 'delete' })
}

/** 修改角色状态（0正常 1停用） */
export function changeRoleStatus(roleId: number, status: string): Promise<Result<void>> {
  return request({ url: '/system/role/changeStatus', method: 'put', params: { roleId, status } })
}


/** 保存角色菜单权限 */
export function saveRoleMenus(roleId: number, menuIds: number[]): Promise<Result<void>> {
  return request({ url: '/system/role/saveRoleMenus', method: 'put', params: { roleId, menuIds } })
}

/** 导出角色数据（二进制流） */
export function exportRole(query: RoleQuery): Promise<Blob> {
  return request({ url: '/system/role/export', method: 'post', params: query, responseType: 'blob' }) as unknown as Promise<Blob>
}

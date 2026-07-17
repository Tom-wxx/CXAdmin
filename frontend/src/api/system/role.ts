import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { Role, RoleQuery } from '@/types/system/role'

export function listRole(query: RoleQuery): Promise<TableResponse<Role>> {
  return request({ url: '/system/role/list', method: 'get', params: query })
}

export function listAllRole(): Promise<Result<Role[]>> {
  return request({ url: '/system/role/listAll', method: 'get' })
}

export function getRole(roleId: number): Promise<Result<Role>> {
  return request({ url: '/system/role/' + roleId, method: 'get' })
}

export function getMenuIds(roleId: number): Promise<Result<number[]>> {
  return request({ url: '/system/role/menuIds/' + roleId, method: 'get' })
}

export function addRole(data: Role): Promise<Result<void>> {
  return request({ url: '/system/role', method: 'post', data })
}

export function updateRole(data: Role): Promise<Result<void>> {
  return request({ url: '/system/role', method: 'put', data })
}

export function delRole(roleIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/role/' + roleIds, method: 'delete' })
}

/** 修改角色状态（0正常 1停用） */
export function changeRoleStatus(roleId: number, status: string): Promise<Result<void>> {
  return request({ url: '/system/role/changeStatus', method: 'put', params: { roleId, status } })
}

export function saveRoleMenus(roleId: number, menuIds: number[]): Promise<Result<void>> {
  return request({ url: '/system/role/saveRoleMenus', method: 'put', params: { roleId, menuIds } })
}

/** 导出角色数据（二进制流） */
export function exportRole(query: RoleQuery): Promise<Blob> {
  return request({ url: '/system/role/export', method: 'post', params: query, responseType: 'blob' }) as unknown as Promise<Blob>
}

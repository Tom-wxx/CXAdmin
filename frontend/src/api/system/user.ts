import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { User, UserQuery, UserFormOptions } from '@/types/system/user'

export function listUser(query: UserQuery): Promise<TableResponse<User>> {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

export function getUser(userId: number): Promise<Result<User>> {
  return request({
    url: '/system/user/' + userId,
    method: 'get'
  })
}

export function addUser(data: User): Promise<Result<void>> {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function updateUser(data: User): Promise<Result<void>> {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

/** 删除用户（单个 ID 或逗号拼接的多个 ID / 数组） */
export function delUser(userIds: number | number[]): Promise<Result<void>> {
  return request({
    url: '/system/user/' + userIds,
    method: 'delete'
  })
}

export function resetUserPwd(userId: number, newPassword: string): Promise<Result<void>> {
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    params: { userId, newPassword }
  })
}

/** 修改用户状态（0 正常 1 停用） */
export function changeUserStatus(userId: number, status: string): Promise<Result<void>> {
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    params: { userId, status }
  })
}

/** 获取用户表单选项（部门、岗位、角色） */
export function getUserFormOptions(): Promise<Result<UserFormOptions>> {
  return request({
    url: '/system/user/formOptions',
    method: 'get'
  })
}

/** 导出用户数据（二进制流） */
export function exportUser(query: UserQuery): Promise<Blob> {
  return request({
    url: '/system/user/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  }) as unknown as Promise<Blob>
}

/** 下载用户导入模板（二进制流） */
export function downloadTemplate(): Promise<Blob> {
  return request({
    url: '/system/user/importTemplate',
    method: 'get',
    responseType: 'blob'
  }) as unknown as Promise<Blob>
}

export function importUser(data: FormData): Promise<Result<void>> {
  return request({
    url: '/system/user/import',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data
  })
}

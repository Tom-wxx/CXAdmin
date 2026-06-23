import request from '@/utils/request'
import type { Result } from '@/types/api'
import type { User } from '@/types/system/user'

/** 获取用户个人信息 */
export function getUserProfile(): Promise<Result<User>> {
  return request({ url: '/system/user/profile', method: 'get' })
}

/** 修改用户个人信息 */
export function updateUserProfile(data: User): Promise<Result<void>> {
  return request({ url: '/system/user/profile', method: 'put', data })
}

/** 修改用户密码 */
export function updateUserPwd(oldPassword: string, newPassword: string): Promise<Result<void>> {
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    data: { oldPassword, newPassword }
  })
}

/** 上传用户头像 */
export function uploadAvatar(data: FormData): Promise<Result<{ imgUrl?: string }>> {
  return request({
    url: '/system/user/profile/avatar',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data
  })
}

import request from '@/utils/request'
import type { Result } from '@/types/api'
import type { LoginBody, CaptchaResult, UserInfo, RouterVo } from '@/types/auth'

/** 登录方法（token 写入 Cookie，data 通常不直接使用） */
export function login(data: LoginBody): Promise<Result<{ token?: string }>> {
  return request({ url: '/login', method: 'post', data })
}

/** 获取当前登录用户信息 */
export function getInfo(): Promise<Result<UserInfo>> {
  return request({ url: '/getInfo', method: 'get' })
}

/** 退出方法 */
export function logout(): Promise<Result<void>> {
  return request({ url: '/logout', method: 'post' })
}

/** 获取验证码 */
export function getCaptcha(): Promise<Result<CaptchaResult>> {
  return request({ url: '/captcha', method: 'get' })
}

/** 获取动态路由 */
export function getRouters(): Promise<Result<{ menus: RouterVo[] }>> {
  return request({ url: '/getRouters', method: 'get' })
}

/** 上传头像 */
export function uploadAvatar(data: FormData): Promise<Result<{ imgUrl?: string }>> {
  return request({
    url: '/profile/avatar',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data
  })
}

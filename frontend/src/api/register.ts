import request from '@/utils/request'
import type { Result } from '@/types/api'
import type { RegisterBody, ForgotPasswordBody, ResetPasswordBody } from '@/types/auth'

/** 用户注册 */
export function registerUser(data: RegisterBody): Promise<Result<void>> {
  return request({ url: '/register', method: 'post', data })
}

/** 忘记密码（发送重置邮件） */
export function forgotPassword(data: ForgotPasswordBody): Promise<Result<void>> {
  return request({ url: '/forgot-password', method: 'post', data })
}

/** 重置密码 */
export function resetPassword(data: ResetPasswordBody): Promise<Result<void>> {
  return request({ url: '/reset-password', method: 'post', data })
}

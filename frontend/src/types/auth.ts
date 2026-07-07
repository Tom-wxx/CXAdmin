import type { User } from '@/types/system/user'

/** 登录请求体 */
export interface LoginBody {
  username: string
  password: string
  code?: string
  uuid?: string
}

/** 验证码返回 */
export interface CaptchaResult {
  uuid?: string
  img?: string
  captchaEnabled?: boolean
}

/** getInfo 返回：当前登录用户信息 */
export interface UserInfo {
  user: User
  roles: string[]
  permissions: string[]
}

/** getRouters 返回的后端动态路由节点 */
export interface RouterVo {
  name?: string
  path?: string
  hidden?: boolean
  redirect?: string
  component?: string
  alwaysShow?: boolean
  meta?: {
    title?: string
    icon?: string
    noCache?: boolean
  }
  children?: RouterVo[]
}

/** 注册请求体 */
export interface RegisterBody {
  username: string
  password: string
  confirmPassword?: string
  email?: string
  code?: string
  uuid?: string
}

/** 忘记密码请求体 */
export interface ForgotPasswordBody {
  username?: string
  email: string
}

/** 重置密码请求体 */
export interface ResetPasswordBody {
  token: string
  password: string
  confirmPassword?: string
}

import request from '@/utils/request'

// 登录方法
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data: data
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

// 获取验证码
export function getCaptcha() {
  return request({
    url: '/captcha',
    method: 'get'
  })
}

// 获取路由
export function getRouters() {
  return request({
    url: '/getRouters',
    method: 'get'
  })
}

// 上传头像
export function uploadAvatar(data) {
  return request({
    url: '/profile/avatar',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: data
  })
}

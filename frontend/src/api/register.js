import request from '@/utils/request'

export function registerUser(data) {
  return request({
    url: '/register',
    method: 'post',
    data
  })
}

export function forgotPassword(data) {
  return request({
    url: '/forgot-password',
    method: 'post',
    data
  })
}

export function resetPassword(data) {
  return request({
    url: '/reset-password',
    method: 'post',
    data
  })
}

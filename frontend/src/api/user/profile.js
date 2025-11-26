import request from '@/utils/request'

/**
 * 获取用户个人信息
 */
export function getUserProfile() {
  return request({
    url: '/system/user/profile',
    method: 'get'
  })
}

/**
 * 修改用户个人信息
 */
export function updateUserProfile(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data
  })
}

/**
 * 修改用户密码
 */
export function updateUserPwd(oldPassword, newPassword) {
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    data: {
      oldPassword,
      newPassword
    }
  })
}

/**
 * 上传用户头像
 */
export function uploadAvatar(data) {
  return request({
    url: '/system/user/profile/avatar',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

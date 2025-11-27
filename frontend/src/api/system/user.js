import request from '@/utils/request'

/**
 * 分页查询用户列表
 * @param {Object} query - 查询参数
 * @param {string} query.username - 用户名
 * @param {string} query.phone - 手机号
 * @param {string} query.status - 状态
 * @param {number} query.current - 页码
 * @param {number} query.size - 每页数量
 */
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询用户详细信息
 * @param {number} userId - 用户ID
 */
export function getUser(userId) {
  return request({
    url: '/system/user/' + userId,
    method: 'get'
  })
}

/**
 * 新增用户
 * @param {Object} data - 用户信息
 */
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data: data
  })
}

/**
 * 修改用户
 * @param {Object} data - 用户信息
 */
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data: data
  })
}

/**
 * 删除用户
 * @param {number|Array} userIds - 用户ID或用户ID数组
 */
export function delUser(userIds) {
  return request({
    url: '/system/user/' + userIds,
    method: 'delete'
  })
}

/**
 * 重置用户密码
 * @param {number} userId - 用户ID
 * @param {string} newPassword - 新密码
 */
export function resetUserPwd(userId, newPassword) {
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    params: {
      userId: userId,
      newPassword: newPassword
    }
  })
}

/**
 * 修改用户状态
 * @param {number} userId - 用户ID
 * @param {string} status - 状态（0正常 1停用）
 */
export function changeUserStatus(userId, status) {
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    params: {
      userId: userId,
      status: status
    }
  })
}

/**
 * 校验用户名是否唯一
 * @param {string} username - 用户名
 * @param {number} userId - 用户ID（编辑时传入）
 */
export function checkUsernameUnique(username, userId) {
  return request({
    url: '/system/user/checkUsernameUnique',
    method: 'get',
    params: {
      username: username,
      userId: userId
    }
  })
}

/**
 * 校验手机号是否唯一
 * @param {string} phone - 手机号
 * @param {number} userId - 用户ID（编辑时传入）
 */
export function checkPhoneUnique(phone, userId) {
  return request({
    url: '/system/user/checkPhoneUnique',
    method: 'get',
    params: {
      phone: phone,
      userId: userId
    }
  })
}

/**
 * 校验邮箱是否唯一
 * @param {string} email - 邮箱
 * @param {number} userId - 用户ID（编辑时传入）
 */
export function checkEmailUnique(email, userId) {
  return request({
    url: '/system/user/checkEmailUnique',
    method: 'get',
    params: {
      email: email,
      userId: userId
    }
  })
}

/**
 * 获取用户表单选项（部门、岗位、角色）
 */
export function getUserFormOptions() {
  return request({
    url: '/system/user/formOptions',
    method: 'get'
  })
}

/**
 * 导出用户数据
 * @param {Object} query - 查询参数
 */
export function exportUser(query) {
  return request({
    url: '/system/user/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

/**
 * 下载用户导入模板
 */
export function downloadTemplate() {
  return request({
    url: '/system/user/importTemplate',
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 导入用户数据
 * @param {FormData} data - 包含文件的表单数据
 */
export function importUser(data) {
  return request({
    url: '/system/user/import',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: data
  })
}

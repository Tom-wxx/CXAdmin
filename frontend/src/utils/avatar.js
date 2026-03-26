/**
 * 头像URL处理工具函数
 * 兼容新旧URL格式，确保头像正确显示
 */

/**
 * 获取头像的完整访问URL
 * @param {string} avatar - 头像路径（可能是完整URL、相对路径、新格式或旧格式）
 * @param {string} defaultAvatar - 默认头像（可选）
 * @returns {string} 完整的头像URL
 */
export function getAvatarUrl(avatar, defaultAvatar = null) {
  // 如果没有头像，返回默认头像
  if (!avatar) {
    return defaultAvatar || require('@/assets/logo.png')
  }

  // 如果已经是完整URL（http或https开头），直接返回
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }

  // 如果已经包含 /api 前缀（新格式），直接返回
  if (avatar.startsWith('/api/')) {
    return avatar
  }

  // 如果是 /uploads 开头（旧格式），添加 /api 前缀
  if (avatar.startsWith('/uploads/')) {
    return '/api' + avatar
  }

  // 其他相对路径，拼接API地址
  const baseURL = process.env.VUE_APP_BASE_API || '/api'
  return baseURL + avatar
}

/**
 * 检查URL是否为有效的头像URL
 * @param {string} url - 待检查的URL
 * @returns {boolean}
 */
export function isValidAvatarUrl(url) {
  if (!url) return false

  // 完整URL
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return true
  }

  // API路径
  if (url.startsWith('/api/') || url.startsWith('/uploads/')) {
    return true
  }

  return false
}

export default {
  getAvatarUrl,
  isValidAvatarUrl
}

import request from '@/utils/request'

/**
 * 获取文件列表
 */
export function listFile(query) {
  return request({
    url: '/system/file/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取文件详情
 */
export function getFile(fileId) {
  return request({
    url: `/system/file/${fileId}`,
    method: 'get'
  })
}

/**
 * 上传文件
 */
export function uploadFile(data) {
  return request({
    url: '/system/file/upload',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

/**
 * 批量上传文件
 */
export function batchUploadFile(data) {
  return request({
    url: '/system/file/upload/batch',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

/**
 * 下载文件
 */
export function downloadFile(fileId) {
  return request({
    url: `/system/file/download/${fileId}`,
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 更新文件信息
 */
export function updateFile(data) {
  return request({
    url: '/system/file',
    method: 'put',
    data
  })
}

/**
 * 删除文件
 */
export function deleteFile(fileIds) {
  return request({
    url: `/system/file/${fileIds}`,
    method: 'delete'
  })
}

/**
 * 获取文件统计信息
 */
export function getFileStatistics() {
  return request({
    url: '/system/file/statistics',
    method: 'get'
  })
}

/**
 * 生成文件访问URL（兼容新旧格式）
 */
export function getFileUrl(fileUrl) {
  // 如果没有URL，返回空
  if (!fileUrl) {
    return ''
  }

  // 如果已经是完整URL（http或https开头），直接返回
  if (fileUrl.startsWith('http://') || fileUrl.startsWith('https://')) {
    return fileUrl
  }

  // 如果已经包含 /api 前缀（新格式），直接返回
  if (fileUrl.startsWith('/api/')) {
    return fileUrl
  }

  // 如果是 /uploads 开头（旧格式），添加 /api 前缀
  if (fileUrl.startsWith('/uploads/')) {
    return '/api' + fileUrl
  }

  // 其他相对路径，拼接API地址
  const baseURL = process.env.VUE_APP_BASE_API || '/api'
  return baseURL + fileUrl
}

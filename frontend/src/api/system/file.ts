import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { FileInfo, FileQuery, FileStatistics } from '@/types/system/file'

/** 获取文件列表 */
export function listFile(query: FileQuery): Promise<Result<PageResult<FileInfo>>> {
  return request({ url: '/system/file/list', method: 'get', params: query })
}

/** 获取文件详情 */
export function getFile(fileId: number): Promise<Result<FileInfo>> {
  return request({ url: `/system/file/${fileId}`, method: 'get' })
}

/** 上传文件 */
export function uploadFile(data: FormData): Promise<Result<FileInfo>> {
  return request({
    url: '/system/file/upload',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data
  })
}

/** 批量上传文件 */
export function batchUploadFile(data: FormData): Promise<Result<FileInfo[]>> {
  return request({
    url: '/system/file/upload/batch',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data
  })
}

/** 下载文件（二进制流） */
export function downloadFile(fileId: number): Promise<Blob> {
  return request({ url: `/system/file/download/${fileId}`, method: 'get', responseType: 'blob' }) as unknown as Promise<Blob>
}

/** 更新文件信息 */
export function updateFile(data: FileInfo): Promise<Result<void>> {
  return request({ url: '/system/file', method: 'put', data })
}

/** 删除文件 */
export function deleteFile(fileIds: number | number[]): Promise<Result<void>> {
  return request({ url: `/system/file/${fileIds}`, method: 'delete' })
}

/** 获取文件统计信息 */
export function getFileStatistics(): Promise<Result<FileStatistics>> {
  return request({ url: '/system/file/statistics', method: 'get' })
}

/** 生成文件访问URL（兼容新旧格式） */
export function getFileUrl(fileUrl?: string): string {
  if (!fileUrl) {
    return ''
  }
  if (fileUrl.startsWith('http://') || fileUrl.startsWith('https://')) {
    return fileUrl
  }
  if (fileUrl.startsWith('/api/')) {
    return fileUrl
  }
  if (fileUrl.startsWith('/uploads/')) {
    return '/api' + fileUrl
  }
  const baseURL = import.meta.env.VITE_APP_BASE_API || '/api'
  return baseURL + fileUrl
}

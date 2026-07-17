import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { FileInfo, FileQuery, FileStatistics } from '@/types/system/file'

export function listFile(query: FileQuery): Promise<TableResponse<FileInfo>> {
  return request({ url: '/system/file/list', method: 'get', params: query })
}

/** 下载文件（二进制流） */
export function downloadFile(fileId: number): Promise<Blob> {
  return request({ url: `/system/file/download/${fileId}`, method: 'get', responseType: 'blob' }) as unknown as Promise<Blob>
}

export function updateFile(data: FileInfo): Promise<Result<void>> {
  return request({ url: '/system/file', method: 'put', data })
}

export function deleteFile(fileIds: number | number[]): Promise<Result<void>> {
  return request({ url: `/system/file/${fileIds}`, method: 'delete' })
}

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

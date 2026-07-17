import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { Post, PostQuery } from '@/types/system/post'

export function pagePost(query: PostQuery): Promise<TableResponse<Post>> {
  return request({ url: '/system/post/page', method: 'get', params: query })
}

export function getPost(postId: number): Promise<Result<Post>> {
  return request({ url: '/system/post/' + postId, method: 'get' })
}

export function addPost(data: Post): Promise<Result<void>> {
  return request({ url: '/system/post', method: 'post', data })
}

export function updatePost(data: Post): Promise<Result<void>> {
  return request({ url: '/system/post', method: 'put', data })
}

export function delPost(postId: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/post/' + postId, method: 'delete' })
}

/** 导出岗位数据（二进制流） */
export function exportPost(query: PostQuery): Promise<Blob> {
  return request({ url: '/system/post/export', method: 'post', params: query, responseType: 'blob' }) as unknown as Promise<Blob>
}

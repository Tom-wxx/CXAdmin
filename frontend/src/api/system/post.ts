import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { Post, PostQuery } from '@/types/system/post'

/** 查询岗位列表 */
export function listPost(query?: PostQuery): Promise<Result<Post[]>> {
  return request({ url: '/system/post/list', method: 'get', params: query })
}

/** 分页查询岗位列表 */
export function pagePost(query: PostQuery): Promise<TableResponse<Post>> {
  return request({ url: '/system/post/page', method: 'get', params: query })
}

/** 查询岗位详细信息 */
export function getPost(postId: number): Promise<Result<Post>> {
  return request({ url: '/system/post/' + postId, method: 'get' })
}

/** 新增岗位 */
export function addPost(data: Post): Promise<Result<void>> {
  return request({ url: '/system/post', method: 'post', data })
}

/** 修改岗位 */
export function updatePost(data: Post): Promise<Result<void>> {
  return request({ url: '/system/post', method: 'put', data })
}

/** 删除岗位 */
export function delPost(postId: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/post/' + postId, method: 'delete' })
}

/** 校验岗位名称是否唯一 */
export function checkPostNameUnique(postName: string, postId?: number): Promise<Result<boolean>> {
  return request({ url: '/system/post/checkPostNameUnique', method: 'get', params: { postName, postId } })
}

/** 校验岗位编码是否唯一 */
export function checkPostCodeUnique(postCode: string, postId?: number): Promise<Result<boolean>> {
  return request({ url: '/system/post/checkPostCodeUnique', method: 'get', params: { postCode, postId } })
}

/** 导出岗位数据（二进制流） */
export function exportPost(query: PostQuery): Promise<Blob> {
  return request({ url: '/system/post/export', method: 'post', params: query, responseType: 'blob' }) as unknown as Promise<Blob>
}

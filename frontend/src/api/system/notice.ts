import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { Notice, NoticeQuery } from '@/types/system/notice'

/** 分页查询通知公告列表 */
export function listNotice(query: NoticeQuery): Promise<Result<PageResult<Notice>>> {
  return request({ url: '/system/notice/list', method: 'get', params: query })
}

/** 查询通知公告详细信息 */
export function getNotice(noticeId: number): Promise<Result<Notice>> {
  return request({ url: '/system/notice/' + noticeId, method: 'get' })
}

/** 新增通知公告 */
export function addNotice(data: Notice): Promise<Result<void>> {
  return request({ url: '/system/notice', method: 'post', data })
}

/** 修改通知公告 */
export function updateNotice(data: Notice): Promise<Result<void>> {
  return request({ url: '/system/notice', method: 'put', data })
}

/** 删除通知公告 */
export function delNotice(noticeIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/system/notice/' + noticeIds, method: 'delete' })
}

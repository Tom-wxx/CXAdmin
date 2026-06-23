import request from '@/utils/request'
import type { Result, PageResult } from '@/types/api'
import type { Job, JobQuery, JobLog, JobLogQuery } from '@/types/monitor/job'

/** 分页查询定时任务列表 */
export function listJob(query: JobQuery): Promise<Result<PageResult<Job>>> {
  return request({ url: '/monitor/job/list', method: 'get', params: query })
}

/** 查询定时任务详细信息 */
export function getJob(jobId: number): Promise<Result<Job>> {
  return request({ url: '/monitor/job/' + jobId, method: 'get' })
}

/** 新增定时任务 */
export function addJob(data: Job): Promise<Result<void>> {
  return request({ url: '/monitor/job', method: 'post', data })
}

/** 修改定时任务 */
export function updateJob(data: Job): Promise<Result<void>> {
  return request({ url: '/monitor/job', method: 'put', data })
}

/** 删除定时任务 */
export function delJob(jobIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/monitor/job/' + jobIds, method: 'delete' })
}

/** 修改任务状态 */
export function changeJobStatus(data: { jobId: number; status: string }): Promise<Result<void>> {
  return request({ url: '/monitor/job/changeStatus', method: 'put', data })
}

/** 立即运行任务 */
export function runJob(data: { jobId: number; jobGroup?: string }): Promise<Result<void>> {
  return request({ url: '/monitor/job/run', method: 'put', data })
}

/** 分页查询任务日志列表 */
export function listJobLog(query: JobLogQuery): Promise<Result<PageResult<JobLog>>> {
  return request({ url: '/monitor/jobLog/list', method: 'get', params: query })
}

/** 查询任务日志详细信息 */
export function getJobLog(jobLogId: number): Promise<Result<JobLog>> {
  return request({ url: '/monitor/jobLog/' + jobLogId, method: 'get' })
}

/** 删除任务日志 */
export function delJobLog(jobLogIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/monitor/jobLog/' + jobLogIds, method: 'delete' })
}

/** 清空任务日志 */
export function cleanJobLog(): Promise<Result<void>> {
  return request({ url: '/monitor/jobLog/clean', method: 'delete' })
}

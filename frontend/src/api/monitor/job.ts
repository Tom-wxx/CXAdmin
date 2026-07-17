import request from '@/utils/request'
import type { Result, TableResponse } from '@/types/api'
import type { Job, JobQuery, JobLog, JobLogQuery } from '@/types/monitor/job'

export function listJob(query: JobQuery): Promise<TableResponse<Job>> {
  return request({ url: '/monitor/job/list', method: 'get', params: query })
}

export function getJob(jobId: number): Promise<Result<Job>> {
  return request({ url: '/monitor/job/' + jobId, method: 'get' })
}

export function addJob(data: Job): Promise<Result<void>> {
  return request({ url: '/monitor/job', method: 'post', data })
}

export function updateJob(data: Job): Promise<Result<void>> {
  return request({ url: '/monitor/job', method: 'put', data })
}

export function delJob(jobIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/monitor/job/' + jobIds, method: 'delete' })
}

export function changeJobStatus(data: { jobId: number; status: string }): Promise<Result<void>> {
  return request({ url: '/monitor/job/changeStatus', method: 'put', data })
}

export function runJob(data: { jobId: number; jobGroup?: string }): Promise<Result<void>> {
  return request({ url: '/monitor/job/run', method: 'put', data })
}

export function listJobLog(query: JobLogQuery): Promise<TableResponse<JobLog>> {
  return request({ url: '/monitor/jobLog/list', method: 'get', params: query })
}

export function getJobLog(jobLogId: number): Promise<Result<JobLog>> {
  return request({ url: '/monitor/jobLog/' + jobLogId, method: 'get' })
}

export function delJobLog(jobLogIds: number | number[]): Promise<Result<void>> {
  return request({ url: '/monitor/jobLog/' + jobLogIds, method: 'delete' })
}

export function cleanJobLog(): Promise<Result<void>> {
  return request({ url: '/monitor/jobLog/clean', method: 'delete' })
}

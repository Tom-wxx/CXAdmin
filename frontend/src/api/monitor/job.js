import request from '@/utils/request'

/**
 * 分页查询定时任务列表
 */
export function listJob(query) {
  return request({
    url: '/monitor/job/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询定时任务详细信息
 */
export function getJob(jobId) {
  return request({
    url: '/monitor/job/' + jobId,
    method: 'get'
  })
}

/**
 * 新增定时任务
 */
export function addJob(data) {
  return request({
    url: '/monitor/job',
    method: 'post',
    data: data
  })
}

/**
 * 修改定时任务
 */
export function updateJob(data) {
  return request({
    url: '/monitor/job',
    method: 'put',
    data: data
  })
}

/**
 * 删除定时任务
 */
export function delJob(jobIds) {
  return request({
    url: '/monitor/job/' + jobIds,
    method: 'delete'
  })
}

/**
 * 修改任务状态
 */
export function changeJobStatus(data) {
  return request({
    url: '/monitor/job/changeStatus',
    method: 'put',
    data: data
  })
}

/**
 * 立即运行任务
 */
export function runJob(data) {
  return request({
    url: '/monitor/job/run',
    method: 'put',
    data: data
  })
}

/**
 * 分页查询任务日志列表
 */
export function listJobLog(query) {
  return request({
    url: '/monitor/jobLog/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询任务日志详细信息
 */
export function getJobLog(jobLogId) {
  return request({
    url: '/monitor/jobLog/' + jobLogId,
    method: 'get'
  })
}

/**
 * 删除任务日志
 */
export function delJobLog(jobLogIds) {
  return request({
    url: '/monitor/jobLog/' + jobLogIds,
    method: 'delete'
  })
}

/**
 * 清空任务日志
 */
export function cleanJobLog() {
  return request({
    url: '/monitor/jobLog/clean',
    method: 'delete'
  })
}

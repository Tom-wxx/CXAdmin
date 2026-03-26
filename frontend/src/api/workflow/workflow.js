import request from '@/utils/request'

// 发起审批流程
export function startProcess(data) {
  return request({
    url: '/workflow/start',
    method: 'post',
    data: data
  })
}

// 审批通过
export function approveTask(taskId, comment) {
  return request({
    url: `/workflow/approve/${taskId}`,
    method: 'post',
    params: { comment }
  })
}

// 审批驳回
export function rejectTask(taskId, comment) {
  return request({
    url: `/workflow/reject/${taskId}`,
    method: 'post',
    params: { comment }
  })
}

// 取消流程
export function cancelProcess(instanceId) {
  return request({
    url: `/workflow/cancel/${instanceId}`,
    method: 'post'
  })
}

// 获取待办任务列表
export function getPendingTasks() {
  return request({
    url: '/workflow/pending',
    method: 'get'
  })
}

// 获取已办任务列表
export function getCompletedTasks() {
  return request({
    url: '/workflow/completed',
    method: 'get'
  })
}

// 获取我发起的流程
export function getMyProcesses() {
  return request({
    url: '/workflow/my-processes',
    method: 'get'
  })
}

// 获取流程详情
export function getProcessDetail(instanceId) {
  return request({
    url: `/workflow/detail/${instanceId}`,
    method: 'get'
  })
}

import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 定时任务对象（对应后端 sys_job / SysJob） */
export interface Job extends BaseEntity {
  jobId?: number
  jobName?: string
  jobGroup?: string
  invokeTarget?: string
  cronExpression?: string
  /** 错误策略 1立即执行 2执行一次 3放弃执行 */
  misfirePolicy?: string
  /** 是否并发 0允许 1禁止 */
  concurrent?: string
  /** 状态 0正常 1暂停 */
  status?: string
}

/** 定时任务查询参数 */
export interface JobQuery extends PageQuery {
  jobName?: string
  jobGroup?: string
  status?: string
}

/** 定时任务日志（对应后端 sys_job_log / SysJobLog） */
export interface JobLog {
  jobLogId?: number
  jobName?: string
  jobGroup?: string
  invokeTarget?: string
  jobMessage?: string
  /** 执行状态 0正常 1失败 */
  status?: string
  exceptionInfo?: string
  createTime?: string
}

/** 任务日志查询参数 */
export interface JobLogQuery extends PageQuery {
  jobName?: string
  jobGroup?: string
  status?: string
}

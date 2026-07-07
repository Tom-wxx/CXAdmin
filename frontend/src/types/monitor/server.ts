/**
 * 服务器监控信息（后端基于 Oshi 返回 cpu/mem/jvm/sys/sysFiles 等，
 * 字段较多且动态，使用宽松结构，组件转换时按需细化）。
 */
export interface ServerInfo {
  cpu?: Record<string, unknown>
  mem?: Record<string, unknown>
  jvm?: Record<string, unknown>
  sys?: Record<string, unknown>
  sysFiles?: Array<Record<string, unknown>>
  [key: string]: unknown
}

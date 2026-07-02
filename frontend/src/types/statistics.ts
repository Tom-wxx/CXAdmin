/**
 * 统计模块（系统概览 / 趋势 / 分布图）响应数据的最小类型。
 * 后端返回结构比较松散，这里只声明组件实际读取的字段，其余用索引签名兜底。
 */

/** 系统概览卡片数据（/statistics/overview） */
export interface SystemOverview {
  totalUsers?: number
  todayNewUsers?: number
  totalRoles?: number
  totalDepts?: number
  todayLoginCount?: number
  onlineUserCount?: number
  todayOperationCount?: number
  unreadNotifications?: number
  [key: string]: unknown
}

/** 趋势数据点（用户增长 / 登录 / 操作日志趋势，按天返回的数组元素） */
export interface TrendPoint {
  date?: string
  value?: number
  [key: string]: unknown
}

/** 名称-数值对（部门/角色人员分布、操作类型分布等饼图数据） */
export interface NameValue {
  name?: string
  value?: number
  [key: string]: unknown
}

/** 动态路由 meta（后端菜单转换后挂载，含若干自定义字段） */
export interface AppRouteMeta {
  title?: string
  icon?: string
  noCache?: boolean
  [key: string]: unknown
}

/**
 * 应用内路由记录：在 vue-router 基础上扩展后端菜单的自定义字段
 * （hidden / alwaysShow / meta.title 等），component 解析后为组件或懒加载函数。
 */
export interface AppRouteRecord {
  path: string
  name?: string
  redirect?: string
  component?: unknown
  hidden?: boolean
  alwaysShow?: boolean
  meta?: AppRouteMeta
  children?: AppRouteRecord[]
  [key: string]: unknown
}

import type { AppRouteRecord } from '@/types/route'

export interface SidebarState {
  opened: boolean
  withoutAnimation: boolean
}

export interface AppState {
  sidebar: SidebarState
  device: string
  size: string
}

export interface UserState {
  token: string
  name: string
  avatar: string
  roles: string[]
  permissions: string[]
}

export interface PermissionState {
  routes: AppRouteRecord[]
  addRoutes: AppRouteRecord[]
}

export interface SettingsState {
  title: string
  fixedHeader: boolean
  sidebarLogo: boolean
  sidebarColor: string
  sidebarPosition: string
}

/** Vuex 根状态 */
export interface RootState {
  app: AppState
  user: UserState
  permission: PermissionState
  settings: SettingsState
}

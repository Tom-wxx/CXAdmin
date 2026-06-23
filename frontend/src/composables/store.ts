import { computed } from 'vue'
import { useStore as baseUseStore, type Store } from 'vuex'
import { key } from '@/store'
import type { RootState } from '@/types/store'
import type { LoginBody } from '@/types/auth'

/** 类型化 store 访问入口 */
export function useStore(): Store<RootState> {
  return baseUseStore(key)
}

/** 用户模块：常用 state + 动作封装 */
export function useUserStore() {
  const store = useStore()
  return {
    token: computed(() => store.state.user.token),
    name: computed(() => store.state.user.name),
    avatar: computed(() => store.state.user.avatar),
    roles: computed(() => store.state.user.roles),
    permissions: computed(() => store.state.user.permissions),
    login: (data: LoginBody): Promise<void> => store.dispatch('user/login', data),
    getInfo: () => store.dispatch('user/getInfo'),
    logout: (): Promise<void> => store.dispatch('user/logout'),
    fedLogout: (): Promise<void> => store.dispatch('user/fedLogout')
  }
}

/** 应用模块：侧边栏 / 设备 / 尺寸 */
export function useAppStore() {
  const store = useStore()
  return {
    sidebar: computed(() => store.state.app.sidebar),
    device: computed(() => store.state.app.device),
    size: computed(() => store.state.app.size),
    toggleSidebar: () => store.dispatch('app/toggleSideBar'),
    closeSidebar: (withoutAnimation: boolean) => store.dispatch('app/closeSideBar', { withoutAnimation })
  }
}

/** 设置模块：主题 / 标题 / 侧栏配色与位置 */
export function useSettingsStore() {
  const store = useStore()
  return {
    title: computed(() => store.state.settings.title),
    fixedHeader: computed(() => store.state.settings.fixedHeader),
    sidebarLogo: computed(() => store.state.settings.sidebarLogo),
    sidebarColor: computed(() => store.state.settings.sidebarColor),
    sidebarPosition: computed(() => store.state.settings.sidebarPosition),
    changeSetting: (data: { key: string; value: unknown }) => store.dispatch('settings/changeSetting', data),
    setSidebarColor: (color: string) => store.dispatch('settings/setSidebarColor', color),
    setSidebarPosition: (position: string) => store.dispatch('settings/setSidebarPosition', position)
  }
}

/** 权限模块：动态路由 */
export function usePermissionStore() {
  const store = useStore()
  return {
    routes: computed(() => store.state.permission.routes),
    addRoutes: computed(() => store.state.permission.addRoutes),
    generateRoutes: () => store.dispatch('permission/generateRoutes')
  }
}

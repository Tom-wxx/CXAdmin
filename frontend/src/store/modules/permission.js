import { constantRoutes } from '@/router'
import { getRouters } from '@/api/login'
import Layout from '@/layout'
import ParentView from '@/components/ParentView'

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({ commit }) {
    return new Promise(resolve => {
      getRouters().then(res => {
        const accessedRoutes = filterAsyncRouter(res.data.menus)
        // Vue Router 4 通配符语法：'*' 已废弃，改用 /:pathMatch(.*)*
        accessedRoutes.push({ path: '/:pathMatch(.*)*', redirect: '/404', hidden: true })
        commit('SET_ROUTES', accessedRoutes)
        resolve(accessedRoutes)
      })
    })
  }
}

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap, parent) {
  return asyncRouterMap.filter(route => {
    // 确保路径有前导斜杠（除非是子路由）
    if (route.path && !route.path.startsWith('/') && !parent) {
      route.path = '/' + route.path
    }

    // 确保每个路由都有 meta 对象，防止渲染错误
    if (!route.meta) {
      route.meta = {}
    }
    // 确保 meta.title 存在
    if (!route.meta.title) {
      route.meta.title = route.name || '未命名菜单'
    }

    if (route.component) {
      if (route.component === 'Layout') {
        route.component = Layout
      } else if (route.component === 'ParentView') {
        route.component = ParentView
      } else {
        route.component = loadView(route.component)
      }
    } else if (route.children && route.children.length > 0 && parent) {
      // 有子菜单但没有component的中间层级菜单，使用ParentView
      route.component = ParentView
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route)
    }
    return true
  })
}

// Vite：预生成 views 下所有 .vue 模块的懒加载表（key 为绝对路径 /src/views/...）
const viewModules = import.meta.glob('/src/views/**/*.vue')

export const loadView = (view) => {
  // view 形如 'system/user/index'，匹配 /src/views/system/user/index.vue
  const mod = viewModules[`/src/views/${view}.vue`]
  if (!mod) {
    // 后端 component 字段与实际文件不匹配时，路由会静默跳过导致白屏，这里给出明确告警便于排错
    console.warn(`[loadView] 未找到视图组件: /src/views/${view}.vue`)
  }
  return mod
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

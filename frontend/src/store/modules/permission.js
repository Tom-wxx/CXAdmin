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
        accessedRoutes.push({ path: '*', redirect: '/404', hidden: true })
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

export const loadView = (view) => {
  return (resolve) => require([`@/views/${view}`], resolve)
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

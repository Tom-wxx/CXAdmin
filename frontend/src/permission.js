import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login']

router.beforeEach(async (to, from, next) => {
  NProgress.start()

  console.log('路由守卫 - 目标路径:', to.path, '是否有Token:', !!getToken())

  if (getToken()) {
    to.meta.title && (document.title = to.meta.title)

    if (to.path === '/login') {
      console.log('已登录，重定向到首页')
      next({ path: '/' })
      NProgress.done()
    } else {
      const hasRoles = store.getters.roles && store.getters.roles.length > 0
      console.log('是否已有用户信息:', hasRoles, '角色:', store.getters.roles)

      if (!hasRoles) {
        // 判断当前用户是否已拉取完user_info信息
        try {
          console.log('开始获取用户信息...')
          const userInfo = await store.dispatch('user/getInfo')
          console.log('用户信息获取成功:', userInfo)

          console.log('开始生成路由...')
          const accessRoutes = await store.dispatch('permission/generateRoutes')
          console.log('路由生成成功，数量:', accessRoutes.length)

          // 根据roles权限生成可访问路由表
          router.addRoutes(accessRoutes) // 动态添加可访问路由表
          console.log('路由已添加，准备跳转')

          // 使用 replace 避免重定向警告
          // 注意：next() 不一定返回 Promise，所以不要链式调用 .catch()
          next({ ...to, replace: true })
        } catch (err) {
          console.error('!!! 获取用户信息失败 !!!', err)
          console.error('错误详情:', err.response || err.message || err)
          await store.dispatch('user/fedLogout')
          Message.error(err.message || '获取用户信息失败，请重新登录')
          next(`/login?redirect=${to.fullPath}`)
          NProgress.done()
        }
      } else {
        console.log('已有用户信息，直接放行')
        next()
      }
    }
  } else {
    console.log('没有Token')
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单，直接进入
      console.log('白名单路径，放行')
      next()
    } else {
      console.log('需要登录，重定向到登录页')
      next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

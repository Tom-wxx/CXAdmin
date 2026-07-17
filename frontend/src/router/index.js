import { createRouter, createWebHistory } from 'vue-router'

/* Layout */
import Layout from '@/layout'

/**
 * 公共路由
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/login/register'),
    hidden: true
  },
  {
    path: '/forgot-password',
    component: () => import('@/views/login/forgot-password'),
    hidden: true
  },
  {
    path: '/reset-password',
    component: () => import('@/views/login/reset-password'),
    hidden: true
  },
  {
    path: '/sso/callback',
    component: () => import('@/views/sso/callback'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: 'index',
    children: [
      {
        path: 'index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  },
  {
    path: '/monitor/cache',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/monitor/cache/index'),
        name: 'CacheMonitor',
        meta: { title: '缓存监控', icon: 'redis', activeMenu: '/monitor/server' }
      }
    ]
  },
  {
    path: '/tool/generator/edit',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/tool/generator/editTable'),
        name: 'EditTable',
        meta: { title: '编辑表', activeMenu: '/tool/generator' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior: () => ({ top: 0 }),
  routes: constantRoutes
})

export default router

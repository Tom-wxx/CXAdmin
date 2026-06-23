import { createApp } from 'vue'
import type { DirectiveBinding } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'normalize.css/normalize.css'
import '@/styles/index.scss'

import '@/permission' // 权限控制
import MenuIcon from '@/components/MenuIcon/index.vue'

const app = createApp(App)

// 全局注册所有 Element Plus 图标组件（模板中可直接以 <el-icon><Edit/></el-icon> 使用）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局菜单图标组件：把数据库存储的历史图标名解析为 Element Plus 组件
app.component('MenuIcon', MenuIcon)

// 全局错误处理 - 抑制路由重定向警告
const isNavigationFailure = (error: unknown): boolean => {
  if (!error) return false
  const e = error as { message?: string; name?: string }
  const message = e.message || ''
  const name = e.name || ''
  return name === 'NavigationDuplicated' ||
         /Avoided redundant navigation|Redirected when going from/i.test(message) ||
         /navigat/i.test(name)
}

app.config.errorHandler = (err, _vm, info) => {
  if (isNavigationFailure(err)) {
    return
  }
  // eslint-disable-next-line no-console
  console.error(err, info)
}

// 处理未捕获的 Promise 错误
window.addEventListener('unhandledrejection', event => {
  if (event.reason && isNavigationFailure(event.reason)) {
    event.preventDefault()
    return
  }
})

// 处理全局错误事件
window.addEventListener('error', event => {
  if (event.error && isNavigationFailure(event.error)) {
    event.preventDefault()
    return
  }
}, true)

app.use(router)
app.use(store)
app.use(ElementPlus, { locale: zhCn })

// 按钮级权限指令 v-hasPermi：无权限则从 DOM 移除元素（super-admin 的 '*:*:*' 放行）
app.directive('hasPermi', (el: HTMLElement, binding: DirectiveBinding<string | string[]>) => {
  const { value } = binding
  const permissions: string[] = store.getters.permissions || []
  const required = Array.isArray(value) ? value : [value]
  const hasPerm = permissions.includes('*:*:*') ||
    required.some(p => p && permissions.includes(p))
  if (!hasPerm) {
    el.parentNode?.removeChild(el)
  }
})

app.mount('#app')

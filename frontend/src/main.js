import { createApp } from 'vue'
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
import { parseTime, resetForm } from '@/utils'
import MenuIcon from '@/components/MenuIcon'

const app = createApp(App)

// 全局注册所有 Element Plus 图标组件（模板中可直接以 <el-icon><Edit/></el-icon> 使用）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局菜单图标组件：把数据库存储的历史图标名解析为 Element Plus 组件
app.component('MenuIcon', MenuIcon)

// 全局混入工具函数（保持与 Vue 2 时一致的调用方式）
app.mixin({
  methods: {
    parseTime,
    resetForm
  }
})

// 全局错误处理 - 抑制路由重定向警告
const isNavigationFailure = (error) => {
  if (!error) return false
  const message = error.message || ''
  const name = error.name || ''
  return name === 'NavigationDuplicated' ||
         /Avoided redundant navigation|Redirected when going from/i.test(message) ||
         /navigat/i.test(name)
}

app.config.errorHandler = (err, vm, info) => {
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

app.mount('#app')

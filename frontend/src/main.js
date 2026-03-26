import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import 'normalize.css/normalize.css'
import '@/styles/index.scss'

import '@/permission' // 权限控制
import { parseTime, resetForm } from '@/utils'

Vue.use(ElementUI)

// 全局混入工具函数
Vue.mixin({
  methods: {
    parseTime,
    resetForm
  }
})

Vue.config.productionTip = false

// 全局错误处理 - 抑制路由重定向警告
const isNavigationFailure = (error) => {
  if (!error) return false
  const message = error.message || ''
  const name = error.name || ''
  return name === 'NavigationDuplicated' ||
         /Avoided redundant navigation|Redirected when going from/i.test(message) ||
         /navigat/i.test(name)
}

Vue.config.errorHandler = (err, vm, info) => {
  if (isNavigationFailure(err)) {
    return
  }
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

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

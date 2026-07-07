<template>
  <div class="sso-callback">登录中，请稍候...</div>
</template>
<script setup lang="ts">
import { useRoute } from 'vue-router'
import { setToken } from '@/utils/auth'
import { useStore } from '@/composables/store'

defineOptions({ name: 'SsoCallback' })

const route = useRoute()
const store = useStore()

// 后端已通过 Set-Cookie 写入 HttpOnly token；前端只需打 user.token=1 标记
setToken()
store.commit('user/SET_TOKEN', '1')
const redirect = (route.query.redirect as string) || '/index'
window.location.href = redirect
</script>
<style scoped>
.sso-callback {
  display: flex; align-items: center; justify-content: center;
  height: 100vh; color: #606266; font-size: 14px;
}
</style>

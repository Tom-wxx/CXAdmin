<template>
  <div class="sso-callback">登录中，请稍候...</div>
</template>
<script>
import { setToken } from '@/utils/auth'
export default {
  name: 'SsoCallback',
  created() {
    // 后端已通过 Set-Cookie 写入 HttpOnly token；前端只需打 user.token=1 标记
    setToken()
    this.$store.commit('user/SET_TOKEN', '1')
    const redirect = this.$route.query.redirect || '/index'
    window.location.href = redirect
  }
}
</script>
<style scoped>
.sso-callback {
  display: flex; align-items: center; justify-content: center;
  height: 100vh; color: #606266; font-size: 14px;
}
</style>

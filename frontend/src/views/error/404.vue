<template>
  <div class="error-page">
    <h1>404</h1>
    <h3>抱歉，您访问的页面不存在</h3>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<script>
export default {
  name: 'Page404',
  methods: {
    goHome() {
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;

  h1 {
    font-size: 120px;
    margin: 0;
    color: #409EFF;
  }

  h3 {
    font-size: 24px;
    margin: 20px 0 40px;
    color: #606266;
  }
}
</style>

<template>
  <div class="error-page">
    <h1>401</h1>
    <h3>抱歉，您没有访问权限</h3>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<script>
export default {
  name: 'Page401',
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
    color: #F56C6C;
  }

  h3 {
    font-size: 24px;
    margin: 20px 0 40px;
    color: #606266;
  }
}
</style>

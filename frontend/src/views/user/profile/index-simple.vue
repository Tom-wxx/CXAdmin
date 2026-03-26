<template>
  <div class="profile-container">
    <h1>个人中心测试</h1>
    <p>如果你能看到这行文字，说明路由是正常的</p>
    <el-card>
      <h2>用户信息</h2>
      <p>用户名: {{ user.username }}</p>
      <p>昵称: {{ user.nickname }}</p>
      <el-button @click="testClick">测试按钮</el-button>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ProfileSimple',
  data() {
    return {
      user: {
        username: 'test',
        nickname: '测试用户'
      }
    }
  },
  mounted() {},
  methods: {
    testClick() {
      alert('按钮点击成功！')
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background: white;
}
</style>

<template>
  <div class="login-container">
    <div class="login-left">
      <div class="brand-area">
        <div class="brand-icon"><i class="el-icon-s-platform"></i></div>
        <h1 class="brand-title">CXAdmin</h1>
        <p class="brand-desc">简洁高效的企业级后台管理系统</p>
      </div>
    </div>
    <div class="login-right">
      <div class="login-form-wrapper">
        <h2 class="form-title">设置新密码</h2>
        <p class="form-subtitle">请输入您的新密码</p>
        <el-form ref="form" :model="form" :rules="rules" class="login-form">
          <el-form-item prop="newPassword">
            <el-input v-model="form.newPassword" type="password" placeholder="新密码（6-20个字符）" prefix-icon="el-icon-lock" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认新密码" prefix-icon="el-icon-lock" show-password @keyup.enter.native="handleReset" />
          </el-form-item>
          <el-form-item>
            <el-button :loading="loading" type="primary" class="login-button" @click.native.prevent="handleReset">
              {{ loading ? '提交中...' : '重置密码' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="form-footer">
          <el-link @click="$router.push('/login')">返回登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { resetPassword } from '@/api/register'

export default {
  name: 'ResetPassword',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.form.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: { newPassword: '', confirmPassword: '' },
      rules: {
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度6-20个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirm, trigger: 'blur' }
        ]
      },
      loading: false,
      token: ''
    }
  },
  created() {
    this.token = this.$route.query.token
    if (!this.token) {
      this.$message.error('重置链接无效')
      this.$router.push('/login')
    }
  },
  methods: {
    handleReset() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.loading = true
          resetPassword({ token: this.token, newPassword: this.form.newPassword }).then(() => {
            this.$message.success('密码重置成功，请登录')
            this.$router.push('/login')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
$primary: #13c2c2;
$primary-dark: #08979c;
$dark-bg: #001529;

.login-container { min-height: 100vh; width: 100%; display: flex; background: #f0f2f5; }

.login-left {
  flex: 1; background: $dark-bg;
  display: flex; align-items: center; justify-content: center; padding: 60px;
  .brand-area { max-width: 400px; color: #fff; }
  .brand-icon {
    width: 64px; height: 64px; background: $primary; border-radius: 16px;
    display: flex; align-items: center; justify-content: center; margin-bottom: 32px;
    i { font-size: 32px; color: #fff; }
  }
  .brand-title { font-size: 36px; font-weight: 700; margin: 0 0 12px; }
  .brand-desc { font-size: 16px; color: rgba(255,255,255,0.65); margin: 0; }
}

.login-right {
  width: 480px; display: flex; align-items: center; justify-content: center;
  padding: 60px; background: #fff;
}

.login-form-wrapper {
  width: 100%; max-width: 360px;
  .form-title { font-size: 24px; font-weight: 600; color: #303133; margin: 0 0 8px; }
  .form-subtitle { font-size: 14px; color: #8c8c8c; margin: 0 0 32px; }
  .login-form {
    ::v-deep .el-input__inner {
      height: 44px; border-radius: 4px; border-color: #d9d9d9;
      &:hover { border-color: $primary; }
      &:focus { border-color: $primary; box-shadow: 0 0 0 2px rgba(19,194,194,0.1); }
    }
    ::v-deep .el-input__prefix { color: #bfbfbf; }
    ::v-deep .el-form-item { margin-bottom: 24px; }
    .login-button {
      width: 100%; height: 44px; font-size: 15px; font-weight: 500;
      background: $primary; border-color: $primary; border-radius: 4px;
      &:hover, &:focus { background: $primary-dark; border-color: $primary-dark; }
    }
  }
  .form-footer { text-align: center; margin-top: 16px; font-size: 14px; }
}

@media screen and (max-width: 960px) {
  .login-left { display: none; }
  .login-right { width: 100%; }
}
</style>

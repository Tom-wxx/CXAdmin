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
        <template v-if="!sent">
          <h2 class="form-title">找回密码</h2>
          <p class="form-subtitle">输入注册邮箱，发送重置链接</p>
          <el-form ref="form" :model="form" :rules="rules" class="login-form">
            <el-form-item prop="email">
              <el-input v-model="form.email" placeholder="注册邮箱" prefix-icon="el-icon-message" @keyup.enter.native="handleSubmit" />
            </el-form-item>
            <el-form-item>
              <el-button :loading="loading" type="primary" class="login-button" @click.native.prevent="handleSubmit">
                {{ loading ? '发送中...' : '发送重置邮件' }}
              </el-button>
            </el-form-item>
          </el-form>
        </template>
        <template v-else>
          <div class="sent-notice">
            <i class="el-icon-circle-check success-icon"></i>
            <h3>邮件已发送</h3>
            <p>如该邮箱已注册，重置链接将发送至您的邮箱，请在 30 分钟内操作。</p>
          </div>
        </template>
        <div class="form-footer">
          <el-link @click="$router.push('/login')">返回登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { forgotPassword } from '@/api/register'

export default {
  name: 'ForgotPassword',
  data() {
    return {
      form: { email: '' },
      rules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ]
      },
      loading: false,
      sent: false
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.loading = true
          forgotPassword({ email: this.form.email }).then(() => {
            this.sent = true
          }).finally(() => {
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
    .login-button {
      width: 100%; height: 44px; font-size: 15px; font-weight: 500;
      background: $primary; border-color: $primary; border-radius: 4px;
      &:hover, &:focus { background: $primary-dark; border-color: $primary-dark; }
    }
  }
  .sent-notice {
    text-align: center; padding: 40px 0;
    .success-icon { font-size: 56px; color: #52c41a; display: block; margin-bottom: 16px; }
    h3 { font-size: 20px; color: #303133; margin: 0 0 12px; }
    p { font-size: 14px; color: #8c8c8c; line-height: 1.6; }
  }
  .form-footer { text-align: center; margin-top: 16px; font-size: 14px; }
}

@media screen and (max-width: 960px) {
  .login-left { display: none; }
  .login-right { width: 100%; }
}
</style>

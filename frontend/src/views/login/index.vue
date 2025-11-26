<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="card-header">
        <div class="logo">
          <i class="el-icon-s-platform"></i>
        </div>
        <h2 class="title">后台管理系统</h2>
        <p class="subtitle">欢迎回来，请登录您的账户</p>
      </div>

      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" autocomplete="on">
        <el-form-item prop="username">
          <div class="input-wrapper">
            <i class="el-icon-user input-icon"></i>
            <el-input
              ref="username"
              v-model="loginForm.username"
              placeholder="请输入用户名"
              name="username"
              type="text"
              tabindex="1"
              autocomplete="on"
            />
          </div>
        </el-form-item>

        <el-tooltip v-model="capsTooltip" content="大写锁定已打开" placement="right" manual>
          <el-form-item prop="password">
            <div class="input-wrapper">
              <i class="el-icon-lock input-icon"></i>
              <el-input
                :key="passwordType"
                ref="password"
                v-model="loginForm.password"
                :type="passwordType"
                placeholder="请输入密码"
                name="password"
                tabindex="2"
                autocomplete="on"
                @keyup.native="checkCapslock"
                @blur="capsTooltip = false"
                @keyup.enter.native="handleLogin"
              />
              <span class="show-pwd" @click="showPwd">
                <i :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-minus'"></i>
              </span>
            </div>
          </el-form-item>
        </el-tooltip>

        <el-form-item prop="code" v-if="captchaEnabled">
          <div class="captcha-wrapper">
            <div class="input-wrapper captcha-input">
              <i class="el-icon-picture-outline input-icon"></i>
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="请输入验证码"
                @keyup.enter.native="handleLogin"
              />
            </div>
            <div class="captcha-image" @click="getCode">
              <img :src="codeUrl" v-if="codeUrl" />
              <span class="refresh-text">点击刷新</span>
            </div>
          </div>
        </el-form-item>

        <el-button
          :loading="loading"
          type="primary"
          class="login-button"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">立即登录</span>
          <span v-else>登录中...</span>
        </el-button>
      </el-form>

      <div class="card-footer">
        <p class="tips">
          <i class="el-icon-warning-outline"></i>
          默认账号: admin / admin123
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { getCaptcha } from '@/api/login'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: 'admin',
        password: 'admin123',
        code: '',
        uuid: ''
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
        password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
        code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
      },
      passwordType: 'password',
      capsTooltip: false,
      loading: false,
      redirect: undefined,
      codeUrl: '',
      captchaEnabled: true
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
  },
  methods: {
    checkCapslock(e) {
      const { key } = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    getCode() {
      getCaptcha().then(res => {
        this.codeUrl = res.data.img
        this.loginForm.uuid = res.data.uuid
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm).then(() => {
            // 使用 location.href 强制刷新，避免路由重定向警告
            const redirectPath = this.redirect || '/index'
            window.location.href = redirectPath
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;

  // 背景动画装饰
  .background-decoration {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;
    z-index: 0;

    .circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      animation: float 20s infinite ease-in-out;

      &.circle-1 {
        width: 300px;
        height: 300px;
        top: -100px;
        left: -100px;
        animation-delay: 0s;
      }

      &.circle-2 {
        width: 200px;
        height: 200px;
        top: 50%;
        right: -50px;
        animation-delay: 5s;
      }

      &.circle-3 {
        width: 400px;
        height: 400px;
        bottom: -150px;
        left: 30%;
        animation-delay: 10s;
      }
    }
  }

  @keyframes float {
    0%, 100% {
      transform: translateY(0) rotate(0deg);
      opacity: 0.3;
    }
    50% {
      transform: translateY(-30px) rotate(180deg);
      opacity: 0.6;
    }
  }

  // 登录卡片
  .login-card {
    position: relative;
    z-index: 1;
    width: 450px;
    padding: 50px 40px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    animation: slideUp 0.6s ease-out;

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(30px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
  }

  // 卡片头部
  .card-header {
    text-align: center;
    margin-bottom: 40px;

    .logo {
      display: inline-block;
      width: 80px;
      height: 80px;
      line-height: 80px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 50%;
      margin-bottom: 20px;
      box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);

      i {
        font-size: 40px;
        color: #fff;
      }
    }

    .title {
      font-size: 28px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0 0 10px;
    }

    .subtitle {
      font-size: 14px;
      color: #7f8c8d;
      margin: 0;
    }
  }

  // 表单样式
  .login-form {
    .input-wrapper {
      position: relative;
      display: flex;
      align-items: center;
      background: #f5f7fa;
      border-radius: 12px;
      padding: 0 15px;
      transition: all 0.3s ease;

      &:hover {
        background: #eef1f6;
      }

      &:focus-within {
        background: #fff;
        box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
      }

      .input-icon {
        font-size: 18px;
        color: #909399;
        margin-right: 10px;
      }

      ::v-deep .el-input {
        flex: 1;

        .el-input__inner {
          border: none;
          background: transparent;
          height: 50px;
          line-height: 50px;
          padding: 0;
          font-size: 15px;
          color: #2c3e50;

          &::placeholder {
            color: #a0a0a0;
          }

          &:focus {
            border: none;
            box-shadow: none;
          }
        }
      }

      .show-pwd {
        position: absolute;
        right: 15px;
        font-size: 18px;
        color: #909399;
        cursor: pointer;
        user-select: none;
        transition: color 0.3s ease;

        &:hover {
          color: #667eea;
        }
      }
    }

    ::v-deep .el-form-item {
      margin-bottom: 25px;

      .el-form-item__error {
        padding-top: 5px;
      }
    }

    // 验证码样式
    .captcha-wrapper {
      display: flex;
      gap: 15px;

      .captcha-input {
        flex: 1;
      }

      .captcha-image {
        width: 150px;
        height: 50px;
        border-radius: 12px;
        overflow: hidden;
        cursor: pointer;
        position: relative;
        background: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s ease;
        border: 2px solid #e4e7ed;

        &:hover {
          transform: scale(1.08);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
          border-color: #667eea;
        }

        img {
          width: 100%;
          height: 100%;
          object-fit: contain;
          display: block;
        }

        .refresh-text {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          background: rgba(102, 126, 234, 0.9);
          color: #fff;
          font-size: 11px;
          text-align: center;
          padding: 3px 0;
          opacity: 0;
          transition: opacity 0.3s ease;
          font-weight: 500;
        }

        &:hover .refresh-text {
          opacity: 1;
        }
      }
    }

    // 登录按钮
    .login-button {
      width: 100%;
      height: 50px;
      margin-top: 10px;
      font-size: 16px;
      font-weight: 600;
      letter-spacing: 1px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 12px;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
      }

      &:active {
        transform: translateY(0);
      }
    }
  }

  // 卡片底部
  .card-footer {
    margin-top: 30px;
    text-align: center;

    .tips {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      font-size: 13px;
      color: #7f8c8d;
      margin: 0;
      padding: 15px;
      background: #f8f9fa;
      border-radius: 10px;

      i {
        font-size: 16px;
        color: #667eea;
      }
    }
  }

  // 响应式设计
  @media screen and (max-width: 768px) {
    .login-card {
      width: 90%;
      margin: 20px;
      padding: 40px 30px;
    }

    .card-header {
      .logo {
        width: 60px;
        height: 60px;
        line-height: 60px;

        i {
          font-size: 30px;
        }
      }

      .title {
        font-size: 24px;
      }
    }
  }
}
</style>

<template>
  <div class="login-container">
    <div class="login-left">
      <div class="brand-area">
        <div class="brand-icon">
          <i class="el-icon-s-platform"></i>
        </div>
        <h1 class="brand-title">CXAdmin</h1>
        <p class="brand-desc">简洁高效的企业级后台管理系统</p>
        <div class="brand-features">
          <div class="feature-item">
            <i class="el-icon-check"></i>
            <span>RBAC 权限控制</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-check"></i>
            <span>动态路由菜单</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-check"></i>
            <span>系统运行监控</span>
          </div>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="login-form-wrapper">
        <h2 class="form-title">欢迎登录</h2>
        <p class="form-subtitle">请输入您的账户信息</p>

        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input
              ref="username"
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="el-icon-user"
              name="username"
              type="text"
              tabindex="1"
              autocomplete="on"
            />
          </el-form-item>

          <el-tooltip v-model="capsTooltip" content="大写锁定已打开" placement="right" manual>
            <el-form-item prop="password">
              <el-input
                :key="passwordType"
                ref="password"
                v-model="loginForm.password"
                :type="passwordType"
                placeholder="密码"
                prefix-icon="el-icon-lock"
                name="password"
                tabindex="2"
                autocomplete="on"
                @keyup.native="checkCapslock"
                @blur="capsTooltip = false"
                @keyup.enter.native="handleLogin"
              >
                <i
                  slot="suffix"
                  :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-minus'"
                  class="show-pwd"
                  @click="showPwd"
                ></i>
              </el-input>
            </el-form-item>
          </el-tooltip>

          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="验证码"
                prefix-icon="el-icon-key"
                class="captcha-input"
                @keyup.enter.native="handleLogin"
              />
              <div class="captcha-image" @click="getCode">
                <img :src="codeUrl" v-if="codeUrl" />
              </div>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              :loading="loading"
              type="primary"
              class="login-button"
              @click.native.prevent="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <el-link @click="$router.push('/register')">注册账号</el-link>
          <el-link style="float:right" @click="$router.push('/forgot-password')">忘记密码</el-link>
        </div>

        <div v-if="providers.length" class="sso-section">
          <div class="sso-divider"><span>第三方登录</span></div>
          <div class="sso-buttons">
            <el-button
              v-for="p in providers"
              :key="p.code"
              class="sso-btn"
              plain
              @click="ssoLogin(p.code)"
            >
              <i v-if="p.icon" :class="['sso-icon', p.icon]"></i>
              {{ p.name }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCaptcha } from '@/api/login'
import { listEnabledProviders } from '@/api/system/sso'

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
      captchaEnabled: true,
      providers: []
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
    listEnabledProviders().then(res => { this.providers = res.data || [] })
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
    },
    ssoLogin(code) {
      // 走 SP 后端跳 IdP
      window.location.href = process.env.VUE_APP_BASE_API + '/sso/authorize/' + code
    }
  }
}
</script>

<style lang="scss" scoped>
$primary: #13c2c2;
$primary-dark: #08979c;
$dark-bg: #001529;

.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  background: #f0f2f5;
}

.login-left {
  flex: 1;
  background: $dark-bg;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;

  .brand-area {
    max-width: 400px;
    color: #fff;
  }

  .brand-icon {
    width: 64px;
    height: 64px;
    background: $primary;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 32px;

    i {
      font-size: 32px;
      color: #fff;
    }
  }

  .brand-title {
    font-size: 36px;
    font-weight: 700;
    margin: 0 0 12px;
    letter-spacing: 1px;
  }

  .brand-desc {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.65);
    margin: 0 0 48px;
    line-height: 1.6;
  }

  .brand-features {
    .feature-item {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      font-size: 14px;
      color: rgba(255, 255, 255, 0.85);

      i {
        width: 20px;
        height: 20px;
        background: rgba(19, 194, 194, 0.2);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        font-size: 12px;
        color: $primary;
      }
    }
  }
}

.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: #fff;
}

.login-form-wrapper {
  width: 100%;
  max-width: 360px;

  .form-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px;
  }

  .form-subtitle {
    font-size: 14px;
    color: #8c8c8c;
    margin: 0 0 40px;
  }

  .login-form {
    ::v-deep .el-input__inner {
      height: 44px;
      border-radius: 4px;
      border-color: #d9d9d9;

      &:hover {
        border-color: $primary;
      }

      &:focus {
        border-color: $primary;
        box-shadow: 0 0 0 2px rgba(19, 194, 194, 0.1);
      }
    }

    ::v-deep .el-input__prefix {
      color: #bfbfbf;
    }

    .show-pwd {
      cursor: pointer;
      color: #bfbfbf;
      line-height: 44px;
      padding-right: 4px;

      &:hover {
        color: $primary;
      }
    }

    .captcha-row {
      display: flex;
      gap: 12px;

      .captcha-input {
        flex: 1;
      }

      .captcha-image {
        width: 120px;
        height: 44px;
        border: 1px solid #d9d9d9;
        border-radius: 4px;
        cursor: pointer;
        overflow: hidden;
        flex-shrink: 0;

        &:hover {
          border-color: $primary;
        }

        img {
          width: 100%;
          height: 100%;
          display: block;
          object-fit: contain;
        }
      }
    }

    .login-button {
      width: 100%;
      height: 44px;
      font-size: 15px;
      font-weight: 500;
      letter-spacing: 4px;
      background: $primary;
      border-color: $primary;
      border-radius: 4px;

      &:hover,
      &:focus {
        background: $primary-dark;
        border-color: $primary-dark;
      }
    }

    ::v-deep .el-form-item {
      margin-bottom: 24px;
    }
  }

  .form-footer {
    margin-top: 8px;
    overflow: hidden;
    font-size: 14px;
  }

  .sso-section {
    margin-top: 24px;
  }
  .sso-divider {
    position: relative; text-align: center; color: #8c8c8c; font-size: 12px;
    margin-bottom: 16px;
    &::before, &::after {
      content: ''; position: absolute; top: 50%; width: 35%; height: 1px; background: #e8e8e8;
    }
    &::before { left: 0; }
    &::after  { right: 0; }
    span { background: #fff; padding: 0 12px; position: relative; z-index: 1; }
  }
  .sso-buttons {
    display: flex; gap: 12px; justify-content: center; flex-wrap: wrap;
    .sso-btn { flex: 1; min-width: 100px; }
    .sso-icon { margin-right: 6px; }
  }
}

@media screen and (max-width: 960px) {
  .login-left {
    display: none;
  }

  .login-right {
    width: 100%;
  }
}
</style>

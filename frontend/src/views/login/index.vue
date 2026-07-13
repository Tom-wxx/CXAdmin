<template>
  <div class="login-container">
    <div class="login-left">
      <!-- 漂浮装饰光斑 -->
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>

      <div class="brand-area">
        <LoginPet class="brand-pet" :type="loginPetType" :size="108" />
        <h1 class="brand-title">CXAdmin</h1>
        <p class="brand-desc">简洁高效的企业级后台管理系统</p>
      </div>
    </div>
    <div class="login-right">
      <div class="login-form-wrapper">
        <h2 class="form-title">欢迎登录</h2>
        <p class="form-subtitle">请输入您的账户信息</p>

        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="User"
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
                ref="passwordRef"
                v-model="loginForm.password"
                :type="passwordType"
                placeholder="密码"
                prefix-icon="Lock"
                name="password"
                tabindex="2"
                autocomplete="on"
                @keyup="checkCapslock"
                @blur="capsTooltip = false"
                @keyup.enter="handleLogin"
              >
                <template #suffix>
                  <el-icon class="show-pwd" @click="showPwd">
                    <View v-if="passwordType === 'password'" />
                    <Hide v-else />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-tooltip>

          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="验证码"
                prefix-icon="Key"
                class="captcha-input"
                @keyup.enter="handleLogin"
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
              @click.prevent="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <el-link @click="router.push('/register')">注册账号</el-link>
          <el-link style="float:right" @click="router.push('/forgot-password')">忘记密码</el-link>
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

<script setup lang="ts">
import { ref, reactive, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { getCaptcha } from '@/api/login'
import { listEnabledProviders } from '@/api/system/sso'
import type { LoginBody } from '@/types/auth'
import type { SsoProviderPublic } from '@/types/system/sso'
import LoginPet from '@/components/LoginPet/index.vue'
import { useUserStore } from '@/composables/store'
import { useLoginPetConfig } from '@/composables/useLoginPetConfig'

defineOptions({ name: 'Login' })

const router = useRouter()
const route = useRoute()
const { login } = useUserStore()
const { loginPetType, loadLoginPetType } = useLoginPetConfig()

const loginFormRef = ref<FormInstance>()
// ElInput instance — typed loosely to avoid complex import expression
const passwordRef = ref<{ focus: () => void } | null>(null)

const loginForm = reactive<LoginBody>({
  username: 'admin',
  password: 'admin123',
  code: '',
  uuid: ''
})

const loginRules: FormRules = {
  username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
}

const passwordType = ref('password')
const capsTooltip = ref(false)
const loading = ref(false)
const redirect = ref<string | undefined>(undefined)
const codeUrl = ref('')
const captchaEnabled = ref(true)
const providers = ref<SsoProviderPublic[]>([])

watch(
  () => route.query,
  (query) => {
    redirect.value = (query && query.redirect) as string | undefined
  },
  { immediate: true }
)

function checkCapslock(e: KeyboardEvent) {
  const { key } = e
  capsTooltip.value = !!(key && key.length === 1 && key >= 'A' && key <= 'Z')
}

function showPwd() {
  passwordType.value = passwordType.value === 'password' ? '' : 'password'
  nextTick(() => {
    passwordRef.value?.focus()
  })
}

function getCode() {
  getCaptcha().then(res => {
    codeUrl.value = res.data.img ?? ''
    loginForm.uuid = res.data.uuid ?? ''
    captchaEnabled.value = res.data.captchaEnabled !== false
  })
}

function handleLogin() {
  loginFormRef.value?.validate(valid => {
    if (valid) {
      loading.value = true
      login(loginForm).then(() => {
        const redirectPath = redirect.value || '/index'
        window.location.href = redirectPath
      }).catch(() => {
        loading.value = false
        if (captchaEnabled.value) {
          getCode()
        }
      })
    }
  })
}

function ssoLogin(code: string | undefined) {
  // 走 SP 后端跳 IdP
  window.location.href = import.meta.env.VITE_APP_BASE_API + '/sso/authorize/' + code
}

// setup body (replaces created)
void loadLoginPetType()
getCode()
listEnabledProviders().then(res => { providers.value = res.data || [] })
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
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  overflow: hidden;
  background: linear-gradient(125deg, #001529 0%, #003a47 30%, #08979c 70%, #0f7474 100%);
  background-size: 220% 220%;
  animation: bg-shift 18s ease-in-out infinite;

  /* 漂浮光斑 */
  .orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(50px);
    opacity: 0.55;
    pointer-events: none;
    will-change: transform;
  }
  .orb-1 {
    width: 380px; height: 380px;
    background: radial-gradient(circle at center, #13c2c2, transparent 70%);
    top: -120px; left: -120px;
    animation: orb-float-1 14s ease-in-out infinite;
  }
  .orb-2 {
    width: 300px; height: 300px;
    background: radial-gradient(circle at center, #5b6ef0, transparent 70%);
    bottom: -100px; right: -80px;
    animation: orb-float-2 16s ease-in-out infinite;
  }
  .orb-3 {
    width: 200px; height: 200px;
    background: radial-gradient(circle at center, #722ed1, transparent 70%);
    top: 50%; left: 50%;
    animation: orb-float-3 20s ease-in-out infinite;
  }

  .brand-area {
    position: relative;
    z-index: 1;
    max-width: 400px;
    color: #fff;
  }

  .brand-pet {
    display: block;
    margin-bottom: 28px;
    filter: drop-shadow(0 10px 22px rgba(19, 194, 194, 0.3));
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
    margin: 0;
    line-height: 1.6;
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
    :deep(.el-input__inner){
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

    :deep(.el-input__prefix){
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

    :deep(.el-form-item){
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
    .sso-btn {
      flex: 1; min-width: 100px;
      transition: transform 0.25s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.25s ease, border-color 0.2s ease;
      &:hover {
        transform: translateY(-2px);
        border-color: $primary;
        color: $primary;
        box-shadow: 0 6px 16px rgba(19, 194, 194, 0.18);
      }
    }
    .sso-icon { margin-right: 6px; }
  }
}

/* ===== 动效关键帧 ===== */
@keyframes bg-shift {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}
@keyframes orb-float-1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50%      { transform: translate(60px, 80px) scale(1.15); }
}
@keyframes orb-float-2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50%      { transform: translate(-50px, -60px) scale(1.1); }
}
@keyframes orb-float-3 {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50%      { transform: translate(-30%, -70%) scale(1.2); }
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

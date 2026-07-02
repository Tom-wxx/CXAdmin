<template>
  <div class="login-container">
    <div class="login-left">
      <div class="brand-area">
        <div class="brand-icon"><el-icon><Platform /></el-icon></div>
        <h1 class="brand-title">CXAdmin</h1>
        <p class="brand-desc">简洁高效的企业级后台管理系统</p>
        <div class="brand-features">
          <div class="feature-item"><el-icon><Check /></el-icon><span>RBAC 权限控制</span></div>
          <div class="feature-item"><el-icon><Check /></el-icon><span>动态路由菜单</span></div>
          <div class="feature-item"><el-icon><Check /></el-icon><span>系统运行监控</span></div>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="login-form-wrapper">
        <h2 class="form-title">注册账号</h2>
        <p class="form-subtitle">创建您的管理账号</p>
        <el-form ref="registerFormRef" :model="form" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名（2-20个字符）" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="nickname">
            <el-input v-model="form.nickname" placeholder="昵称" prefix-icon="UserFilled" />
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="邮箱" prefix-icon="Message" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码（6-20个字符）" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="code">
            <div class="captcha-row">
              <el-input v-model="form.code" placeholder="验证码" prefix-icon="Key" class="captcha-input" @keyup.enter="handleRegister" />
              <div class="captcha-image" @click="getCode">
                <img :src="codeUrl" v-if="codeUrl" />
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button :loading="loading" type="primary" class="login-button" @click.prevent="handleRegister">
              {{ loading ? '注册中...' : '注 册' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="form-footer">
          <el-link @click="router.push('/login')">已有账号，去登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getCaptcha } from '@/api/login'
import { registerUser } from '@/api/register'
import type { RegisterBody } from '@/types/auth'

defineOptions({ name: 'Register' })

const router = useRouter()

const registerFormRef = ref<FormInstance>()

interface RegisterForm extends RegisterBody {
  nickname: string
  confirmPassword: string
}

const form = reactive<RegisterForm>({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  code: '',
  uuid: ''
})

const validateConfirmPassword = (_rule: unknown, value: string, callback: (e?: Error) => void) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度2-20个字符', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'change' }]
}

const loading = ref(false)
const codeUrl = ref('')

function getCode() {
  getCaptcha().then(res => {
    codeUrl.value = res.data.img ?? ''
    form.uuid = res.data.uuid ?? ''
  })
}

function handleRegister() {
  registerFormRef.value?.validate(valid => {
    if (valid) {
      loading.value = true
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { confirmPassword, ...data } = form
      registerUser(data as RegisterBody).then(() => {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      }).catch(() => {
        loading.value = false
        getCode()
      })
    }
  })
}

// setup body (replaces created)
getCode()
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

  .brand-area { max-width: 400px; color: #fff; }

  .brand-icon {
    width: 64px; height: 64px;
    background: $primary; border-radius: 16px;
    display: flex; align-items: center; justify-content: center;
    margin-bottom: 32px;
    i { font-size: 32px; color: #fff; }
  }

  .brand-title { font-size: 36px; font-weight: 700; margin: 0 0 12px; }
  .brand-desc { font-size: 16px; color: rgba(255,255,255,0.65); margin: 0 0 48px; }

  .brand-features .feature-item {
    display: flex; align-items: center;
    margin-bottom: 16px; font-size: 14px;
    color: rgba(255,255,255,0.85);
    i {
      width: 20px; height: 20px;
      background: rgba(19,194,194,0.2); border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      margin-right: 12px; font-size: 12px; color: $primary;
    }
  }
}

.login-right {
  width: 480px;
  display: flex; align-items: center; justify-content: center;
  padding: 60px; background: #fff;
}

.login-form-wrapper {
  width: 100%; max-width: 360px;

  .form-title { font-size: 24px; font-weight: 600; color: #303133; margin: 0 0 8px; }
  .form-subtitle { font-size: 14px; color: #8c8c8c; margin: 0 0 32px; }

  .login-form {
    :deep(.el-input__inner){
      height: 44px; border-radius: 4px; border-color: #d9d9d9;
      &:hover { border-color: $primary; }
      &:focus { border-color: $primary; box-shadow: 0 0 0 2px rgba(19,194,194,0.1); }
    }
    :deep(.el-input__prefix){ color: #bfbfbf; }
    :deep(.el-form-item){ margin-bottom: 20px; }

    .captcha-row {
      display: flex; gap: 12px;
      .captcha-input { flex: 1; }
      .captcha-image {
        width: 120px; height: 44px;
        border: 1px solid #d9d9d9; border-radius: 4px;
        cursor: pointer; overflow: hidden; flex-shrink: 0;
        &:hover { border-color: $primary; }
        img { width: 100%; height: 100%; display: block; object-fit: contain; }
      }
    }

    .login-button {
      width: 100%; height: 44px; font-size: 15px; font-weight: 500;
      letter-spacing: 4px; background: $primary; border-color: $primary; border-radius: 4px;
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

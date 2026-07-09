<template>
  <div class="profile-container">
    <!-- 顶部背景装饰 -->
    <div class="header-bg"></div>

    <div class="profile-content">
      <el-row :gutter="24">
        <!-- 左侧个人信息卡片 -->
        <el-col :xs="24" :sm="24" :md="8" :lg="6">
          <div class="user-card" v-loading="loading">
            <!-- 头像区域 -->
            <div class="avatar-section">
              <div class="avatar-wrapper">
                <img v-if="user.avatar" :src="getAvatarUrl(user.avatar)" class="user-avatar" alt="avatar" />
                <div v-else class="avatar-placeholder">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <input
                  ref="avatarInputRef"
                  type="file"
                  accept="image/*"
                  style="display: none"
                  @change="handleAvatarChange"
                />
                <el-button
                  class="upload-btn"
                  type="primary"
                  icon="Camera"
                  size="small"
                  circle
                  :loading="uploadingAvatar"
                  @click="handleUploadAvatar"
                ></el-button>
              </div>
              <h2 class="user-name">{{ user.nickname || '用户' }}</h2>
              <p class="user-username">@{{ user.username || 'user' }}</p>

              <!-- 用户标签 -->
              <div class="user-tags">
                <el-tag size="small" effect="plain" type="success">在线</el-tag>
                <el-tag size="small" effect="plain" type="warning">管理员</el-tag>
              </div>
            </div>

            <!-- 分隔线 -->
            <div class="divider"></div>

            <!-- 信息列表 -->
            <div class="info-list">
              <div class="info-item">
                <div class="info-icon">
                  <el-icon style="color: #67C23A"><Phone /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-label">手机号码</div>
                  <div class="info-value">{{ user.phonenumber || '未绑定' }}</div>
                </div>
              </div>

              <div class="info-item">
                <div class="info-icon">
                  <el-icon style="color: #409EFF"><Message /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-label">电子邮箱</div>
                  <div class="info-value">{{ user.email || '未绑定' }}</div>
                </div>
              </div>

              <div class="info-item">
                <div class="info-icon">
                  <el-icon style="color: #E6A23C"><OfficeBuilding /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-label">所属部门</div>
                  <div class="info-value">{{ user.dept && user.dept.deptName ? user.dept.deptName : '未分配' }}</div>
                </div>
              </div>

              <div class="info-item">
                <div class="info-icon">
                  <el-icon style="color: #909399"><Clock /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-label">注册时间</div>
                  <div class="info-value">{{ user.createTime ? user.createTime.split('T')[0] : '-' }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 统计卡片 -->
          <div class="stats-card">
            <div class="stat-item">
              <div class="stat-icon stat-icon-document">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">128</div>
                <div class="stat-label">文章数</div>
              </div>
            </div>

            <div class="stat-item">
              <div class="stat-icon stat-icon-star">
                <el-icon><StarFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">1.2k</div>
                <div class="stat-label">获赞数</div>
              </div>
            </div>

            <div class="stat-item">
              <div class="stat-icon stat-icon-view">
                <el-icon><View /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">5.6k</div>
                <div class="stat-label">浏览量</div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 右侧内容区 -->
        <el-col :xs="24" :sm="24" :md="16" :lg="18">
          <div class="content-card">
            <el-tabs v-model="activeTab" class="profile-tabs">
              <!-- 基本资料 -->
              <el-tab-pane name="userinfo">
                <template #label><span>
                  <el-icon><User /></el-icon> 基本资料
                </span></template>

                <div class="tab-content">
                  <el-form :model="user" :rules="userRules" ref="userFormRef" label-width="100px" class="profile-form">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="用户昵称" prop="nickname">
                          <el-input
                            v-model="user.nickname"
                            placeholder="请输入用户昵称"
                            prefix-icon="User"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="用户名称" prop="username">
                          <el-input
                            v-model="user.username"
                            placeholder="用户名不可修改"
                            prefix-icon="UserFilled"
                            disabled
                          />
                        </el-form-item>
                      </el-col>
                    </el-row>

                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="手机号码" prop="phonenumber">
                          <el-input
                            v-model="user.phonenumber"
                            placeholder="请输入手机号码"
                            prefix-icon="Phone"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="邮箱地址" prop="email">
                          <el-input
                            v-model="user.email"
                            placeholder="请输入邮箱地址"
                            prefix-icon="Message"
                          />
                        </el-form-item>
                      </el-col>
                    </el-row>

                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="性别">
                          <el-radio-group v-model="user.sex">
                            <el-radio value="0">
                              <el-icon style="color: #409EFF"><Male /></el-icon> 男
                            </el-radio>
                            <el-radio value="1">
                              <el-icon style="color: #F56C6C"><Female /></el-icon> 女
                            </el-radio>
                          </el-radio-group>
                        </el-form-item>
                      </el-col>
                    </el-row>

                    <el-form-item>
                      <el-button type="primary" icon="Check" @click="handleSave">保存修改</el-button>
                      <el-button icon="Refresh" @click="handleReset">重置</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-tab-pane>

              <!-- 修改密码 -->
              <el-tab-pane name="resetPwd">
                <template #label><span>
                  <el-icon><Lock /></el-icon> 修改密码
                </span></template>

                <div class="tab-content">
                  <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px" class="profile-form">
                    <el-alert
                      title="密码强度要求"
                      type="info"
                      description="密码长度不少于6位，建议包含大小写字母、数字和特殊字符"
                      :closable="false"
                      show-icon
                      style="margin-bottom: 20px"
                    ></el-alert>

                    <el-form-item label="旧密码" prop="oldPassword">
                      <el-input
                        v-model="pwdForm.oldPassword"
                        type="password"
                        placeholder="请输入旧密码"
                        prefix-icon="Lock"
                        show-password
                        style="max-width: 400px"
                      />
                    </el-form-item>

                    <el-form-item label="新密码" prop="newPassword">
                      <el-input
                        v-model="pwdForm.newPassword"
                        type="password"
                        placeholder="请输入新密码"
                        prefix-icon="Key"
                        show-password
                        style="max-width: 400px"
                      />
                    </el-form-item>

                    <el-form-item label="确认密码" prop="confirmPassword">
                      <el-input
                        v-model="pwdForm.confirmPassword"
                        type="password"
                        placeholder="请再次输入新密码"
                        prefix-icon="Key"
                        show-password
                        style="max-width: 400px"
                      />
                    </el-form-item>

                    <el-form-item>
                      <el-button type="primary" icon="Check" @click="handleChangePwd">修改密码</el-button>
                      <el-button icon="Refresh" @click="handleResetPwd">重置</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-tab-pane>

              <!-- 账号安全 -->
              <el-tab-pane name="security">
                <template #label><span>
                  <el-icon><Tools /></el-icon> 账号安全
                </span></template>

                <div class="tab-content">
                  <div class="security-list">
                    <div class="security-item">
                      <div class="security-info">
                        <el-icon class="security-icon" style="color: #67C23A"><Lock /></el-icon>
                        <div>
                          <div class="security-title">账号密码</div>
                          <div class="security-desc">定期更换密码可以保护账号安全</div>
                        </div>
                      </div>
                      <el-button size="small" @click="activeTab='resetPwd'">修改</el-button>
                    </div>

                    <div class="security-item">
                      <div class="security-info">
                        <el-icon class="security-icon" style="color: #409EFF"><Phone /></el-icon>
                        <div>
                          <div class="security-title">密保手机</div>
                          <div class="security-desc">已绑定：{{ user.phonenumber || '未绑定' }}</div>
                        </div>
                      </div>
                      <el-button size="small" @click="handleBindPhone">修改</el-button>
                    </div>

                    <div class="security-item">
                      <div class="security-info">
                        <el-icon class="security-icon" style="color: #E6A23C"><Message /></el-icon>
                        <div>
                          <div class="security-title">密保邮箱</div>
                          <div class="security-desc">已绑定：{{ user.email || '未绑定' }}</div>
                        </div>
                      </div>
                      <el-button size="small" @click="handleBindEmail">修改</el-button>
                    </div>
                  </div>
                </div>
              </el-tab-pane>

              <!-- 账号绑定 -->
              <el-tab-pane name="bindings">
                <template #label><span>
                  <el-icon><Link /></el-icon> 账号绑定
                </span></template>

                <div class="tab-content" v-loading="bindingsLoading">
                  <h4 style="margin-top:0">已绑定的第三方账号</h4>
                  <div v-if="bindings.length === 0" class="binding-empty">
                    <el-icon style="font-size:24px; color:#909399"><Warning /></el-icon>
                    <p>暂未绑定任何第三方账号</p>
                  </div>
                  <div v-else class="binding-list">
                    <div v-for="b in bindings" :key="b.id" class="binding-item">
                      <div class="binding-info">
                        <menu-icon icon-class="binding-icon" :name="b.providerIcon || 'link'" />
                        <div>
                          <div class="binding-title">{{ b.providerName }}</div>
                          <div class="binding-desc">
                            {{ b.externalUsername || b.externalUserId }}
                            <span v-if="b.email"> · {{ b.email }}</span>
                            <span class="binding-time"> · 绑定于 {{ b.bindTime }}</span>
                          </div>
                        </div>
                      </div>
                      <el-button size="small" type="danger" plain @click="handleUnbind(b)">解绑</el-button>
                    </div>
                  </div>

                  <h4 style="margin-top:32px">可绑定的第三方账号</h4>
                  <div v-if="unboundProviders.length === 0" class="binding-empty">
                    <p>没有可绑定的 IdP（已全部绑定，或系统未启用任何 IdP）</p>
                  </div>
                  <div v-else class="binding-list">
                    <div v-for="p in unboundProviders" :key="p.code" class="binding-item">
                      <div class="binding-info">
                        <menu-icon icon-class="binding-icon" :name="p.icon || 'link'" />
                        <div>
                          <div class="binding-title">{{ p.name }}</div>
                          <div class="binding-desc">点击右侧按钮跳转到 {{ p.name }} 授权</div>
                        </div>
                      </div>
                      <el-button size="small" type="primary" plain @click="handleStartBind(p.code)">立即绑定</el-button>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { uploadAvatar } from '@/api/login'
import { listMyBindings, unbindBinding, startBind, listEnabledProviders } from '@/api/system/sso'
import { useStore, useUserStore } from '@/composables/store'
import { getAvatarUrl } from '@/utils/avatar'
import type { User } from '@/types/system/user'
import type { SsoBinding, SsoProviderPublic } from '@/types/system/sso'

defineOptions({ name: 'Profile' })

const router = useRouter()
const route = useRoute()
const store = useStore()
const { name, avatar } = useUserStore()

/** 个人中心展示用的用户对象：在通用 User 之上附加部门信息（后端 profile 接口尚未接入，当前为本地 mock） */
interface ProfileUser extends User {
  dept: { deptName: string } | null
}

const loading = ref(false)
const uploadingAvatar = ref(false)
const activeTab = ref('userinfo')
const bindings = ref<SsoBinding[]>([])
const enabledProviders = ref<SsoProviderPublic[]>([])
const bindingsLoading = ref(false)

const userFormRef = ref<FormInstance>()
const pwdFormRef = ref<FormInstance>()
const avatarInputRef = ref<HTMLInputElement>()

const user = reactive<ProfileUser>({
  username: '',
  nickname: '',
  email: '',
  phonenumber: '',
  sex: '0',
  avatar: '',
  dept: null,
  createTime: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const userRules: FormRules = {
  nickname: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phonenumber: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const pwdRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      // 跨字段校验：闭包捕获 pwdForm（setup 中无 this，不能像 Options API 那样引用 this.pwdForm）
      validator: (_rule: unknown, value: string, callback: (e?: Error) => void) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const unboundProviders = computed(() => {
  const boundCodes = new Set(bindings.value.map(b => b.providerCode))
  return enabledProviders.value.filter(p => !boundCodes.has(p.code))
})

function loadUserInfo() {
  loading.value = true
  setTimeout(() => {
    // reactive 对象不可整体重新赋值，用 Object.assign 合并
    Object.assign(user, {
      username: name.value || 'admin',
      nickname: name.value || '管理员',
      email: 'admin@example.com',
      phonenumber: '13800138000',
      sex: '0',
      avatar: avatar.value || '',
      dept: { deptName: '研发部' },
      createTime: new Date().toISOString()
    })
    loading.value = false
  }, 300)
}

function loadBindings() {
  bindingsLoading.value = true
  Promise.all([listMyBindings(), listEnabledProviders()])
    .then(([bindRes, providerRes]) => {
      bindings.value = bindRes.data || []
      enabledProviders.value = providerRes.data || []
    })
    .finally(() => { bindingsLoading.value = false })
}

function handleStartBind(code?: string) {
  if (!code) return
  startBind(code).then(res => {
    if (res && res.data) {
      window.location.href = res.data
    } else {
      ElMessage.error('发起绑定失败')
    }
  })
}

function handleUnbind(b: SsoBinding) {
  ElMessageBox.confirm(`确认解除与 ${b.providerName}（${b.externalUsername || b.externalUserId}）的绑定？`, '解绑确认', {
    confirmButtonText: '解绑', cancelButtonText: '取消', type: 'warning'
  }).then(() => unbindBinding(b.id!)) // 已加载的绑定记录必然携带 id
    .then(() => {
      ElMessage.success('已解绑')
      loadBindings()
    }).catch(() => {})
}

function handleBindReturn() {
  const { tab, bind } = route.query || {}
  if (tab === 'bindings') {
    activeTab.value = 'bindings'
    if (bind === 'ok') {
      ElMessage.success('绑定成功')
      // 清掉 query，避免刷新页面再次提示
      router.replace({ path: route.path })
    }
  }
}

function handleSave() {
  userFormRef.value?.validate((valid) => {
    if (valid) {
      ElMessage({
        message: '后端接口未实现，无法保存',
        type: 'warning',
        duration: 2000
      })
    }
  })
}

function handleReset() {
  userFormRef.value?.resetFields()
  loadUserInfo()
}

function handleChangePwd() {
  pwdFormRef.value?.validate((valid) => {
    if (valid) {
      ElMessage({
        message: '后端接口未实现，无法修改密码',
        type: 'warning',
        duration: 2000
      })
    }
  })
}

function handleResetPwd() {
  pwdFormRef.value?.resetFields()
}

function handleUploadAvatar() {
  avatarInputRef.value?.click()
}

async function handleAvatarChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return
  }

  // 验证文件大小（2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  // 上传头像
  uploadingAvatar.value = true
  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await uploadAvatar(formData)
    if (res.code === 200) {
      // 更新本地头像
      user.avatar = res.data.imgUrl ?? ''

      // 更新Vuex中的用户信息
      store.commit('user/SET_AVATAR', user.avatar)

      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(res.message || '头像上传失败')
    }
  } catch (_) {
    ElMessage.error('头像上传失败，请稍后重试')
  } finally {
    uploadingAvatar.value = false
    // 清空文件输入框
    target.value = ''
  }
}

function handleBindPhone() {
  ElMessage.info('手机绑定功能待开发')
}

function handleBindEmail() {
  ElMessage.info('邮箱绑定功能待开发')
}

// setup body（替代 created）
loadUserInfo()
loadBindings()
handleBindReturn()
</script>

<style lang="scss" scoped>
$profile-accent: #0f9f9f;
$profile-accent-dark: #087f83;
$profile-accent-soft: #e6fffb;
$profile-bg: #f5f7fb;
$profile-surface: #ffffff;
$profile-border: #e5e7eb;
$profile-text: #1f2937;
$profile-text-secondary: #64748b;
$profile-text-muted: #94a3b8;
$profile-radius: 8px;
$profile-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

.profile-container {
  min-height: calc(100vh - 50px);
  background: $profile-bg;
  position: relative;
  overflow: auto;

  .header-bg {
    display: none;
  }

  .profile-content {
    position: relative;
    padding: 24px;
    z-index: 1;
  }

  // 左侧用户卡片
  .user-card {
    background: $profile-surface;
    border-radius: $profile-radius;
    padding: 24px 20px;
    border: 1px solid $profile-border;
    box-shadow: $profile-shadow;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
    margin-bottom: 20px;

    &:hover {
      border-color: #cbd5e1;
      box-shadow: $profile-shadow;
    }

    .avatar-section {
      text-align: center;

      .avatar-wrapper {
        position: relative;
        display: inline-block;
        margin-bottom: 16px;
        cursor: pointer;

        &::after {
          content: '';
          position: absolute;
          inset: -8px;
          border-radius: 50%;
          background: radial-gradient(circle at center, rgba(15, 159, 159, 0.16), transparent 70%);
          opacity: 0;
          transition: opacity 0.3s ease, transform 0.4s ease;
          pointer-events: none;
        }
        &:hover::after { opacity: 1; transform: scale(1.15); }

        .user-avatar,
        .avatar-placeholder {
          width: 100px;
          height: 100px;
          border-radius: 50%;
          position: relative;
          z-index: 1;
          transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        &:hover .user-avatar,
        &:hover .avatar-placeholder {
          transform: scale(1.04);
        }

        .user-avatar {
          border: 4px solid #fff;
          object-fit: cover;
          box-shadow: 0 4px 12px rgba(19, 194, 194, 0.15);
        }

        .avatar-placeholder {
          background: $profile-accent;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 48px;
          border: 4px solid #fff;
          box-shadow: 0 2px 8px rgba(15, 159, 159, 0.20);
        }

        .upload-btn {
          position: absolute;
          bottom: 0;
          right: -5px;
          z-index: 2;
          background: $profile-accent !important;
          border: 3px solid white !important;
          transition: background 0.2s ease, box-shadow 0.2s ease;
          box-shadow: 0 2px 8px rgba(15, 159, 159, 0.25);

          &:hover {
            background: $profile-accent-dark !important;
            box-shadow: 0 0 0 3px rgba(15, 159, 159, 0.12);
          }
        }
      }

      .user-name {
        font-size: 22px;
        font-weight: 600;
        color: $profile-text;
        margin: 8px 0 4px;
      }

      .user-username {
        font-size: 14px;
        color: $profile-text-secondary;
        margin: 0 0 12px;
      }

      .user-tags {
        display: flex;
        gap: 8px;
        justify-content: center;
        flex-wrap: wrap;
      }
    }

    .divider {
      height: 1px;
      background: $profile-border;
      margin: 20px 0;
    }

    .info-list {
      .info-item {
        display: flex;
        align-items: center;
        padding: 12px 8px;
        border-radius: $profile-radius;
        margin-bottom: 8px;
        transition: all 0.3s ease;

        &:hover {
          background: #f5f7fa;
        }

        .info-icon {
          width: 36px;
          height: 36px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #f8fafc;
          border-radius: $profile-radius;
          margin-right: 12px;
          font-size: 18px;
        }

        .info-content {
          flex: 1;

          .info-label {
            font-size: 12px;
            color: $profile-text-muted;
            margin-bottom: 2px;
          }

          .info-value {
            font-size: 14px;
            color: $profile-text;
            font-weight: 500;
          }
        }
      }
    }
  }

  // 统计卡片
  .stats-card {
    background: $profile-surface;
    border-radius: $profile-radius;
    padding: 20px;
    border: 1px solid $profile-border;
    box-shadow: $profile-shadow;
    display: grid;
    grid-template-columns: 1fr;
    gap: 16px;

    .stat-item {
      display: flex;
      align-items: center;
      padding: 12px;
      border-radius: $profile-radius;
      background: #f8fafc;
      border: 1px solid transparent;
      transition: border-color 0.2s ease, background 0.2s ease;

      &:hover {
        background: $profile-surface;
        border-color: #cbd5e1;
      }

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: $profile-radius;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        margin-right: 12px;
      }

      .stat-icon-document {
        color: $profile-accent;
        background: $profile-accent-soft;
      }

      .stat-icon-star {
        color: #4f46e5;
        background: #eef2ff;
      }

      .stat-icon-view {
        color: #2563eb;
        background: #eff6ff;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 20px;
          font-weight: 600;
          color: $profile-text;
          margin-bottom: 2px;
        }

        .stat-label {
          font-size: 12px;
          color: $profile-text-muted;
        }
      }
    }
  }

  // 右侧内容卡片
  .content-card {
    background: $profile-surface;
    border-radius: $profile-radius;
    padding: 24px;
    border: 1px solid $profile-border;
    box-shadow: $profile-shadow;
    min-height: 600px;

    .profile-tabs {
      :deep(.el-tabs__nav-wrap::after){
        height: 1px;
        background-color: $profile-border;
      }

      :deep(.el-tabs__item){
        font-size: 15px;
        font-weight: 500;
        color: $profile-text-secondary;
        padding: 0 24px;

        i {
          margin-right: 6px;
        }

        &.is-active {
          color: $profile-accent;
        }
      }

      :deep(.el-tabs__active-bar){
        background: $profile-accent;
        height: 2px;
      }
    }

    .tab-content {
      padding: 20px 0;
    }

    .profile-form {
      :deep(.el-form-item__label){
        font-weight: 500;
        color: $profile-text-secondary;
      }

      :deep(.el-input__inner){
        border-radius: $profile-radius;
        transition: border-color 0.2s ease, box-shadow 0.2s ease;

        &:focus {
          border-color: $profile-accent;
          box-shadow: 0 0 0 2px rgba(15, 159, 159, 0.12);
        }
      }

      :deep(.el-button--primary){
        background: $profile-accent;
        border-color: $profile-accent;
        border-radius: $profile-radius;
        padding: 12px 28px;
        transition: background 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;

        &:hover {
          background: $profile-accent-dark;
          border-color: $profile-accent-dark;
          box-shadow: 0 0 0 3px rgba(15, 159, 159, 0.12);
        }
      }

      :deep(.el-button){
        border-radius: $profile-radius;
        padding: 12px 28px;
      }
    }

    // 安全设置列表
    .security-list {
      .security-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 20px;
        border-radius: $profile-radius;
        background: #f8fafc;
        border: 1px solid transparent;
        margin-bottom: 16px;
        transition: border-color 0.2s ease, background 0.2s ease;

        &:hover {
          background: $profile-surface;
          border-color: #cbd5e1;
        }

        .security-info {
          display: flex;
          align-items: center;

          .security-icon {
            width: 48px;
            height: 48px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            background: $profile-surface;
            border: 1px solid $profile-border;
            border-radius: $profile-radius;
            margin-right: 16px;
          }

          .security-title {
            font-size: 16px;
            font-weight: 600;
            color: $profile-text;
            margin-bottom: 4px;
          }

          .security-desc {
            font-size: 13px;
            color: $profile-text-secondary;
          }
        }

        :deep(.el-button){
          border-radius: $profile-radius;
        }
      }
    }

    // 第三方账号绑定列表（沿用 security-item 视觉风格）
    .binding-list {
      .binding-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 20px;
        border-radius: $profile-radius;
        background: #f8fafc;
        border: 1px solid transparent;
        margin-bottom: 16px;
        transition: border-color 0.2s ease, background 0.2s ease;

        &:hover {
          background: $profile-surface;
          border-color: #cbd5e1;
        }

        .binding-info {
          display: flex;
          align-items: center;

          .binding-icon {
            width: 48px; height: 48px;
            display: flex; align-items: center; justify-content: center;
            font-size: 22px; color: $profile-text-secondary;
            background: $profile-surface;
            border: 1px solid $profile-border;
            border-radius: $profile-radius;
            margin-right: 16px;
          }

          .binding-title { font-size: 16px; font-weight: 600; color: $profile-text; margin-bottom: 4px; }
          .binding-desc  { font-size: 13px; color: $profile-text-secondary; }
          .binding-time  { color: $profile-text-muted; }
        }
      }
    }
    .binding-empty {
      text-align: center;
      color: $profile-text-muted;
      padding: 32px 0;
      p { margin: 8px 0 0; }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-container {
    .header-bg {
      height: 120px;
    }

    .profile-content {
      padding: 16px;
    }

    .user-card,
    .stats-card,
    .content-card {
      margin-bottom: 16px;
    }
  }
}

/* 头像呼吸动画（仅占位符状态显示） */
@keyframes avatar-pulse {
  0%, 100% { box-shadow: 0 4px 12px rgba(19, 194, 194, 0.25); }
  50%      { box-shadow: 0 4px 24px rgba(19, 194, 194, 0.45); }
}
</style>

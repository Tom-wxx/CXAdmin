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
                  <i class="el-icon-user-solid"></i>
                </div>
                <input
                  ref="avatarInput"
                  type="file"
                  accept="image/*"
                  style="display: none"
                  @change="handleAvatarChange"
                />
                <el-button
                  class="upload-btn"
                  type="primary"
                  icon="el-icon-camera"
                  size="mini"
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
                  <i class="el-icon-phone" style="color: #67C23A"></i>
                </div>
                <div class="info-content">
                  <div class="info-label">手机号码</div>
                  <div class="info-value">{{ user.phonenumber || '未绑定' }}</div>
                </div>
              </div>

              <div class="info-item">
                <div class="info-icon">
                  <i class="el-icon-message" style="color: #409EFF"></i>
                </div>
                <div class="info-content">
                  <div class="info-label">电子邮箱</div>
                  <div class="info-value">{{ user.email || '未绑定' }}</div>
                </div>
              </div>

              <div class="info-item">
                <div class="info-icon">
                  <i class="el-icon-office-building" style="color: #E6A23C"></i>
                </div>
                <div class="info-content">
                  <div class="info-label">所属部门</div>
                  <div class="info-value">{{ user.dept && user.dept.deptName ? user.dept.deptName : '未分配' }}</div>
                </div>
              </div>

              <div class="info-item">
                <div class="info-icon">
                  <i class="el-icon-time" style="color: #909399"></i>
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
              <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                <i class="el-icon-document"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">128</div>
                <div class="stat-label">文章数</div>
              </div>
            </div>

            <div class="stat-item">
              <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
                <i class="el-icon-star-on"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">1.2k</div>
                <div class="stat-label">获赞数</div>
              </div>
            </div>

            <div class="stat-item">
              <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
                <i class="el-icon-view"></i>
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
                <span slot="label">
                  <i class="el-icon-user"></i> 基本资料
                </span>

                <div class="tab-content">
                  <el-form :model="user" :rules="userRules" ref="userForm" label-width="100px" class="profile-form">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="用户昵称" prop="nickname">
                          <el-input
                            v-model="user.nickname"
                            placeholder="请输入用户昵称"
                            prefix-icon="el-icon-user"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="用户名称" prop="username">
                          <el-input
                            v-model="user.username"
                            placeholder="用户名不可修改"
                            prefix-icon="el-icon-s-custom"
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
                            prefix-icon="el-icon-phone"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="邮箱地址" prop="email">
                          <el-input
                            v-model="user.email"
                            placeholder="请输入邮箱地址"
                            prefix-icon="el-icon-message"
                          />
                        </el-form-item>
                      </el-col>
                    </el-row>

                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="性别">
                          <el-radio-group v-model="user.sex">
                            <el-radio label="0">
                              <i class="el-icon-male" style="color: #409EFF"></i> 男
                            </el-radio>
                            <el-radio label="1">
                              <i class="el-icon-female" style="color: #F56C6C"></i> 女
                            </el-radio>
                          </el-radio-group>
                        </el-form-item>
                      </el-col>
                    </el-row>

                    <el-form-item>
                      <el-button type="primary" icon="el-icon-check" @click="handleSave">保存修改</el-button>
                      <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-tab-pane>

              <!-- 修改密码 -->
              <el-tab-pane name="resetPwd">
                <span slot="label">
                  <i class="el-icon-lock"></i> 修改密码
                </span>

                <div class="tab-content">
                  <el-form :model="pwdForm" :rules="pwdRules" ref="pwdForm" label-width="100px" class="profile-form">
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
                        prefix-icon="el-icon-lock"
                        show-password
                        style="max-width: 400px"
                      />
                    </el-form-item>

                    <el-form-item label="新密码" prop="newPassword">
                      <el-input
                        v-model="pwdForm.newPassword"
                        type="password"
                        placeholder="请输入新密码"
                        prefix-icon="el-icon-key"
                        show-password
                        style="max-width: 400px"
                      />
                    </el-form-item>

                    <el-form-item label="确认密码" prop="confirmPassword">
                      <el-input
                        v-model="pwdForm.confirmPassword"
                        type="password"
                        placeholder="请再次输入新密码"
                        prefix-icon="el-icon-key"
                        show-password
                        style="max-width: 400px"
                      />
                    </el-form-item>

                    <el-form-item>
                      <el-button type="primary" icon="el-icon-check" @click="handleChangePwd">修改密码</el-button>
                      <el-button icon="el-icon-refresh" @click="handleResetPwd">重置</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-tab-pane>

              <!-- 账号安全 -->
              <el-tab-pane name="security">
                <span slot="label">
                  <i class="el-icon-s-tools"></i> 账号安全
                </span>

                <div class="tab-content">
                  <div class="security-list">
                    <div class="security-item">
                      <div class="security-info">
                        <i class="el-icon-lock security-icon" style="color: #67C23A"></i>
                        <div>
                          <div class="security-title">账号密码</div>
                          <div class="security-desc">定期更换密码可以保护账号安全</div>
                        </div>
                      </div>
                      <el-button size="small" @click="activeTab='resetPwd'">修改</el-button>
                    </div>

                    <div class="security-item">
                      <div class="security-info">
                        <i class="el-icon-phone security-icon" style="color: #409EFF"></i>
                        <div>
                          <div class="security-title">密保手机</div>
                          <div class="security-desc">已绑定：{{ user.phonenumber || '未绑定' }}</div>
                        </div>
                      </div>
                      <el-button size="small" @click="handleBindPhone">修改</el-button>
                    </div>

                    <div class="security-item">
                      <div class="security-info">
                        <i class="el-icon-message security-icon" style="color: #E6A23C"></i>
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
            </el-tabs>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { uploadAvatar } from '@/api/login'

export default {
  name: 'Profile',
  data() {
    return {
      loading: false,
      uploadingAvatar: false,
      activeTab: 'userinfo',
      user: {
        username: '',
        nickname: '',
        email: '',
        phonenumber: '',
        sex: '0',
        avatar: '',
        dept: null,
        createTime: ''
      },
      pwdForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      userRules: {
        nickname: [
          { required: true, message: '请输入用户昵称', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phonenumber: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      },
      pwdRules: {
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
            validator: (rule, value, callback) => {
              if (value !== this.pwdForm.newPassword) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  created() {
    this.loadUserInfo()
  },
  methods: {
    // 获取头像完整URL
    getAvatarUrl(avatar) {
      if (!avatar) return ''
      // 如果已经是完整URL（http或https开头），直接返回
      if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
        return avatar
      }
      // 如果是相对路径，拼接API地址
      const baseURL = process.env.VUE_APP_BASE_API || '/api'
      return baseURL + avatar
    },
    loadUserInfo() {
      this.loading = true
      setTimeout(() => {
        this.user = {
          username: this.$store.state.user.name || 'admin',
          nickname: this.$store.state.user.name || '管理员',
          email: 'admin@example.com',
          phonenumber: '13800138000',
          sex: '0',
          avatar: this.$store.state.user.avatar || '',
          dept: { deptName: '研发部' },
          createTime: new Date().toISOString()
        }
        this.loading = false
      }, 300)
    },
    handleSave() {
      this.$refs.userForm.validate((valid) => {
        if (valid) {
          this.$message({
            message: '后端接口未实现，无法保存',
            type: 'warning',
            duration: 2000
          })
        }
      })
    },
    handleReset() {
      this.$refs.userForm.resetFields()
      this.loadUserInfo()
    },
    handleChangePwd() {
      this.$refs.pwdForm.validate((valid) => {
        if (valid) {
          this.$message({
            message: '后端接口未实现，无法修改密码',
            type: 'warning',
            duration: 2000
          })
        }
      })
    },
    handleResetPwd() {
      this.$refs.pwdForm.resetFields()
    },
    handleUploadAvatar() {
      this.$refs.avatarInput.click()
    },
    async handleAvatarChange(event) {
      const file = event.target.files[0]
      if (!file) return

      // 验证文件类型
      if (!file.type.startsWith('image/')) {
        this.$message.error('只能上传图片文件')
        return
      }

      // 验证文件大小（2MB）
      if (file.size > 2 * 1024 * 1024) {
        this.$message.error('图片大小不能超过2MB')
        return
      }

      // 上传头像
      this.uploadingAvatar = true
      const formData = new FormData()
      formData.append('file', file)

      try {
        const res = await uploadAvatar(formData)
        if (res.code === 200) {
          // 更新本地头像
          this.user.avatar = res.data.avatar

          // 更新Vuex中的用户信息
          this.$store.commit('user/SET_AVATAR', res.data.avatar)

          this.$message.success('头像上传成功')
        } else {
          this.$message.error(res.message || '头像上传失败')
        }
      } catch (error) {
        console.error('头像上传失败:', error)
        this.$message.error('头像上传失败，请稍后重试')
      } finally {
        this.uploadingAvatar = false
        // 清空文件输入框
        event.target.value = ''
      }
    },
    handleBindPhone() {
      this.$message.info('手机绑定功能待开发')
    },
    handleBindEmail() {
      this.$message.info('邮箱绑定功能待开发')
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: calc(100vh - 50px);
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  position: relative;
  overflow: hidden;

  // 顶部装饰背景
  .header-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 180px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 0 0 50% 50% / 0 0 80px 80px;
  }

  .profile-content {
    position: relative;
    padding: 24px;
    z-index: 1;
  }

  // 左侧用户卡片
  .user-card {
    background: white;
    border-radius: 20px;
    padding: 30px 20px;
    box-shadow: 0 2px 20px rgba(102, 126, 234, 0.1);
    transition: all 0.3s ease;
    margin-bottom: 20px;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(102, 126, 234, 0.15);
    }

    .avatar-section {
      text-align: center;

      .avatar-wrapper {
        position: relative;
        display: inline-block;
        margin-bottom: 16px;

        .user-avatar,
        .avatar-placeholder {
          width: 100px;
          height: 100px;
          border-radius: 50%;
        }

        .user-avatar {
          border: 4px solid #f0f2f5;
          object-fit: cover;
        }

        .avatar-placeholder {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 48px;
          border: 4px solid #f0f2f5;
        }

        .upload-btn {
          position: absolute;
          bottom: 0;
          right: -5px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border: 3px solid white;
          transition: all 0.3s ease;

          &:hover {
            transform: scale(1.1);
          }
        }
      }

      .user-name {
        font-size: 22px;
        font-weight: 600;
        color: #303133;
        margin: 8px 0 4px;
      }

      .user-username {
        font-size: 14px;
        color: #909399;
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
      background: linear-gradient(90deg, transparent, #e4e7ed, transparent);
      margin: 20px 0;
    }

    .info-list {
      .info-item {
        display: flex;
        align-items: center;
        padding: 12px 8px;
        border-radius: 10px;
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
          background: #f5f7fa;
          border-radius: 10px;
          margin-right: 12px;
          font-size: 18px;
        }

        .info-content {
          flex: 1;

          .info-label {
            font-size: 12px;
            color: #909399;
            margin-bottom: 2px;
          }

          .info-value {
            font-size: 14px;
            color: #303133;
            font-weight: 500;
          }
        }
      }
    }
  }

  // 统计卡片
  .stats-card {
    background: white;
    border-radius: 20px;
    padding: 20px;
    box-shadow: 0 2px 20px rgba(102, 126, 234, 0.1);
    display: grid;
    grid-template-columns: 1fr;
    gap: 16px;

    .stat-item {
      display: flex;
      align-items: center;
      padding: 12px;
      border-radius: 12px;
      background: #f5f7fa;
      transition: all 0.3s ease;

      &:hover {
        transform: translateX(5px);
        background: #fff;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      }

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 24px;
        margin-right: 12px;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 20px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 2px;
        }

        .stat-label {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  // 右侧内容卡片
  .content-card {
    background: white;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 2px 20px rgba(102, 126, 234, 0.1);
    min-height: 600px;

    .profile-tabs {
      ::v-deep .el-tabs__nav-wrap::after {
        display: none;
      }

      ::v-deep .el-tabs__item {
        font-size: 15px;
        font-weight: 500;
        padding: 0 30px;

        i {
          margin-right: 6px;
        }

        &.is-active {
          color: #667eea;
        }
      }

      ::v-deep .el-tabs__active-bar {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        height: 3px;
      }
    }

    .tab-content {
      padding: 20px 0;
    }

    .profile-form {
      ::v-deep .el-form-item__label {
        font-weight: 500;
        color: #606266;
      }

      ::v-deep .el-input__inner {
        border-radius: 8px;
        transition: all 0.3s ease;

        &:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
        }
      }

      ::v-deep .el-button--primary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        border-radius: 8px;
        padding: 12px 28px;

        &:hover {
          opacity: 0.9;
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
        }
      }

      ::v-deep .el-button {
        border-radius: 8px;
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
        border-radius: 12px;
        background: #f5f7fa;
        margin-bottom: 16px;
        transition: all 0.3s ease;

        &:hover {
          background: #fff;
          box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
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
            background: white;
            border-radius: 12px;
            margin-right: 16px;
          }

          .security-title {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 4px;
          }

          .security-desc {
            font-size: 13px;
            color: #909399;
          }
        }

        ::v-deep .el-button {
          border-radius: 8px;
        }
      }
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
</style>

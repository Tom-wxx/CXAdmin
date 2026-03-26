<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :span="6">
        <el-card class="user-card">
          <div class="user-avatar-section">
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="user.avatar" :src="user.avatar" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <div class="user-info-text">
              <h3>{{ user.nickname }}</h3>
              <p class="user-username">@{{ user.username }}</p>
            </div>
          </div>

          <ul class="user-info-list">
            <li>
              <i class="el-icon-user"></i>
              <span>用户名称</span>
              <div class="user-info-value">{{ user.nickname }}</div>
            </li>
            <li>
              <i class="el-icon-phone"></i>
              <span>手机号码</span>
              <div class="user-info-value">{{ user.phonenumber }}</div>
            </li>
            <li>
              <i class="el-icon-message"></i>
              <span>用户邮箱</span>
              <div class="user-info-value">{{ user.email }}</div>
            </li>
            <li>
              <i class="el-icon-suitcase"></i>
              <span>所属部门</span>
              <div class="user-info-value">{{ user.dept ? user.dept.deptName : '暂无' }}</div>
            </li>
            <li>
              <i class="el-icon-medal"></i>
              <span>所属角色</span>
              <div class="user-info-value">{{ getRoleNames }}</div>
            </li>
            <li>
              <i class="el-icon-time"></i>
              <span>创建时间</span>
              <div class="user-info-value">{{ parseTime(user.createTime) }}</div>
            </li>
          </ul>
        </el-card>
      </el-col>

      <!-- 右侧Tab页 -->
      <el-col :span="18">
        <el-card>
          <el-tabs v-model="activeTab">
            <!-- 基本资料 -->
            <el-tab-pane label="基本资料" name="userinfo">
              <el-form ref="userForm" :model="user" :rules="userRules" label-width="100px">
                <el-form-item label="用户昵称" prop="nickname">
                  <el-input v-model="user.nickname" placeholder="请输入用户昵称" maxlength="30" />
                </el-form-item>
                <el-form-item label="手机号码" prop="phonenumber">
                  <el-input v-model="user.phonenumber" placeholder="请输入手机号码" maxlength="11" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="user.email" placeholder="请输入邮箱" maxlength="50" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="user.sex">
                    <el-radio label="0">男</el-radio>
                    <el-radio label="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitUserInfo">保存</el-button>
                  <el-button @click="resetForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 修改密码 -->
            <el-tab-pane label="修改密码" name="resetPwd">
              <el-form ref="pwdForm" :model="pwdForm" :rules="pwdRules" label-width="100px">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input
                    v-model="pwdForm.oldPassword"
                    placeholder="请输入旧密码"
                    type="password"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="pwdForm.newPassword"
                    placeholder="请输入新密码"
                    type="password"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="pwdForm.confirmPassword"
                    placeholder="请确认新密码"
                    type="password"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitPwd">保存</el-button>
                  <el-button @click="resetPwdForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getUserProfile, updateUserProfile, updateUserPwd } from '@/api/user/profile'
import { getToken } from '@/utils/auth'

export default {
  name: 'Profile',
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        if (this.pwdForm.confirmPassword !== '') {
          this.$refs.pwdForm.validateField('confirmPassword')
        }
        callback()
      }
    }
    const validateConfirmPass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.pwdForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      activeTab: 'userinfo',
      user: {
        username: '',
        nickname: '',
        email: '',
        phonenumber: '',
        sex: '0',
        avatar: '',
        dept: {},
        roles: [],
        createTime: ''
      },
      userRules: {
        nickname: [
          { required: true, message: '用户昵称不能为空', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '邮箱地址不能为空', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        phonenumber: [
          { required: true, message: '手机号码不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      },
      pwdForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      pwdRules: {
        oldPassword: [
          { required: true, message: '旧密码不能为空', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, validator: validatePass, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPass, trigger: 'blur' }
        ]
      },
      uploadUrl: process.env.VUE_APP_BASE_API + '/system/file/upload',
      uploadHeaders: { Authorization: 'Bearer ' + getToken() }
    }
  },
  computed: {
    getRoleNames() {
      if (!this.user.roles || this.user.roles.length === 0) {
        return '暂无'
      }
      return this.user.roles.map(role => role.roleName).join('，')
    }
  },
  created() {
    this.getUserProfile()
  },
  methods: {
    /** 获取用户信息 */
    getUserProfile() {
      getUserProfile().then(response => {
        this.user = response.data
      }).catch(() => {
        // 后端接口未实现时，使用 Vuex 中的用户数据
        this.user = {
          username: this.$store.state.user.name || 'admin',
          nickname: this.$store.state.user.name || '管理员',
          email: 'admin@example.com',
          phonenumber: '13800138000',
          sex: '0',
          avatar: '',
          dept: { deptName: '研发部' },
          roles: [{ roleName: '超级管理员' }],
          createTime: new Date().toISOString()
        }
        this.$message.warning('个人中心后端接口未实现，显示临时数据')
      })
    },
    /** 提交用户信息 */
    submitUserInfo() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          updateUserProfile(this.user).then(response => {
            this.$message.success('修改成功')
            // 更新 Vuex 中的用户信息
            this.$store.dispatch('user/getInfo')
          }).catch(() => {
            this.$message.error('个人中心后端接口未实现，无法保存')
          })
        }
      })
    },
    /** 重置表单 */
    resetForm() {
      this.getUserProfile()
    },
    /** 提交修改密码 */
    submitPwd() {
      this.$refs.pwdForm.validate(valid => {
        if (valid) {
          updateUserPwd(this.pwdForm.oldPassword, this.pwdForm.newPassword).then(response => {
            this.$message.success('修改密码成功，请重新登录')
            // 退出登录
            this.$store.dispatch('user/logout').then(() => {
              this.$router.push('/login')
            })
          }).catch(() => {
            this.$message.error('个人中心后端接口未实现，无法修改密码')
          })
        }
      })
    },
    /** 重置密码表单 */
    resetPwdForm() {
      this.$refs.pwdForm.resetFields()
    },
    /** 头像上传成功 */
    handleAvatarSuccess(response, file) {
      if (response.code === 200) {
        this.user.avatar = response.data.fileUrl
        this.$message.success('上传头像成功')
        // 更新用户信息
        this.submitUserInfo()
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },
    /** 头像上传前 */
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('头像只能是图片格式!')
      }
      if (!isLt2M) {
        this.$message.error('头像大小不能超过 2MB!')
      }
      return isImage && isLt2M
    },
    /** 格式化时间 */
    parseTime(time) {
      if (!time) return ''
      return time.replace('T', ' ')
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 20px;

  .user-card {
    .user-avatar-section {
      text-align: center;
      padding: 20px 0;
      border-bottom: 1px solid #ebeef5;
      margin-bottom: 20px;

      .avatar-uploader {
        display: inline-block;
        margin-bottom: 15px;

        .avatar {
          width: 120px;
          height: 120px;
          border-radius: 50%;
          display: block;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            transform: scale(1.05);
          }
        }

        .avatar-uploader-icon {
          font-size: 28px;
          color: #8c939d;
          width: 120px;
          height: 120px;
          line-height: 120px;
          text-align: center;
          border: 2px dashed #d9d9d9;
          border-radius: 50%;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            border-color: #409EFF;
            color: #409EFF;
          }
        }
      }

      .user-info-text {
        h3 {
          font-size: 20px;
          font-weight: 600;
          color: #303133;
          margin: 10px 0 5px;
        }

        .user-username {
          font-size: 14px;
          color: #909399;
          margin: 0;
        }
      }
    }

    .user-info-list {
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        padding: 15px 10px;
        border-bottom: 1px solid #f5f7fa;
        display: flex;
        align-items: center;
        font-size: 14px;

        &:last-child {
          border-bottom: none;
        }

        i {
          font-size: 18px;
          color: #409EFF;
          margin-right: 10px;
          width: 20px;
        }

        span {
          color: #909399;
          min-width: 70px;
        }

        .user-info-value {
          flex: 1;
          color: #606266;
          text-align: right;
          word-break: break-all;
        }
      }
    }
  }

  ::v-deep .el-tabs__item {
    font-size: 16px;
  }

  ::v-deep .el-form {
    max-width: 600px;
    margin-top: 20px;
  }
}
</style>

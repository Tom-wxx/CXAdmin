<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="80px">
      <el-form-item label="配置名称" prop="configName">
        <el-input
          v-model="queryParams.configName"
          placeholder="请输入配置名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消息类型" prop="messageType">
        <el-select v-model="queryParams.messageType" placeholder="请选择消息类型" clearable size="small">
          <el-option label="邮件" value="1" />
          <el-option label="短信" value="2" />
          <el-option label="站内信" value="3" />
          <el-option label="微信" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="configList">
      <el-table-column label="配置ID" align="center" prop="configId" width="80" />
      <el-table-column label="配置名称" align="center" prop="configName" />
      <el-table-column label="消息类型" align="center" prop="messageType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.messageType === '1'" type="success">邮件</el-tag>
          <el-tag v-else-if="scope.row.messageType === '2'" type="warning">短信</el-tag>
          <el-tag v-else-if="scope.row.messageType === '3'" type="info">站内信</el-tag>
          <el-tag v-else-if="scope.row.messageType === '4'" type="primary">微信</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否默认" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isDefault === '1'" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="250">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-if="scope.row.isDefault === '0'"
            size="mini"
            type="text"
            icon="el-icon-star-off"
            @click="handleSetDefault(scope.row)"
          >设为默认</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-promotion"
            @click="handleTest(scope.row)"
          >测试</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.current"
      :limit.sync="queryParams.size"
      @pagination="getList"
    />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="消息类型" prop="messageType">
          <el-radio-group v-model="form.messageType" @change="handleTypeChange">
            <el-radio label="1">邮件</el-radio>
            <el-radio label="2">短信</el-radio>
            <el-radio label="3">站内信</el-radio>
            <el-radio label="4">微信</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 邮件配置 -->
        <template v-if="form.messageType === '1'">
          <el-form-item label="SMTP服务器">
            <el-input v-model="emailConfig.host" placeholder="如: smtp.qq.com" />
          </el-form-item>
          <el-form-item label="端口">
            <el-input v-model="emailConfig.port" placeholder="如: 465 或 587" />
          </el-form-item>
          <el-form-item label="发件邮箱">
            <el-input v-model="emailConfig.from" placeholder="如: noreply@example.com" />
          </el-form-item>
          <el-form-item label="邮箱账号">
            <el-input v-model="emailConfig.username" placeholder="登录账号" />
          </el-form-item>
          <el-form-item label="邮箱密码">
            <el-input v-model="emailConfig.password" type="password" placeholder="授权码或密码" show-password />
          </el-form-item>
          <el-form-item label="发件人名称">
            <el-input v-model="emailConfig.fromName" placeholder="如: 系统通知" />
          </el-form-item>
          <el-form-item label="SSL加密">
            <el-switch v-model="emailConfig.ssl" active-text="启用" inactive-text="关闭"></el-switch>
          </el-form-item>
        </template>

        <!-- 短信配置 -->
        <template v-if="form.messageType === '2'">
          <el-form-item label="服务商">
            <el-select v-model="smsConfig.provider" placeholder="请选择短信服务商">
              <el-option label="阿里云" value="aliyun" />
              <el-option label="腾讯云" value="tencent" />
              <el-option label="华为云" value="huawei" />
            </el-select>
          </el-form-item>
          <el-form-item label="AccessKey ID">
            <el-input v-model="smsConfig.accessKeyId" placeholder="请输入AccessKey ID" />
          </el-form-item>
          <el-form-item label="AccessKey Secret">
            <el-input v-model="smsConfig.accessKeySecret" type="password" placeholder="请输入AccessKey Secret" show-password />
          </el-form-item>
          <el-form-item label="短信签名">
            <el-input v-model="smsConfig.signName" placeholder="如: 系统通知" />
          </el-form-item>
          <el-form-item label="模板编码">
            <el-input v-model="smsConfig.templateCode" placeholder="如: SMS_123456789" />
          </el-form-item>
        </template>

        <!-- 站内信配置 -->
        <template v-if="form.messageType === '3'">
          <el-form-item label="消息有效期">
            <el-input v-model="systemConfig.expireDays" placeholder="天数，0表示永久有效">
              <template slot="append">天</template>
            </el-input>
          </el-form-item>
          <el-form-item label="启用推送">
            <el-switch v-model="systemConfig.pushEnabled" active-text="启用" inactive-text="关闭"></el-switch>
          </el-form-item>
        </template>

        <!-- 微信配置 -->
        <template v-if="form.messageType === '4'">
          <el-form-item label="AppID">
            <el-input v-model="wechatConfig.appId" placeholder="请输入微信AppID" />
          </el-form-item>
          <el-form-item label="AppSecret">
            <el-input v-model="wechatConfig.appSecret" type="password" placeholder="请输入AppSecret" show-password />
          </el-form-item>
          <el-form-item label="模板ID">
            <el-input v-model="wechatConfig.templateId" placeholder="微信模板消息ID" />
          </el-form-item>
        </template>
        <el-form-item label="是否默认" prop="isDefault">
          <el-radio-group v-model="form.isDefault">
            <el-radio label="1">是</el-radio>
            <el-radio label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 测试发送对话框 -->
    <el-dialog title="测试发送" :visible.sync="testOpen" width="500px" append-to-body >
      <el-form ref="testForm" :model="testForm" label-width="100px">
        <el-form-item label="接收者">
          <el-input v-model="testForm.receiver" placeholder="邮箱或手机号" />
        </el-form-item>
        <el-form-item label="主题">
          <el-input v-model="testForm.subject" placeholder="邮件主题（短信可为空）" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="testForm.content" type="textarea" :rows="4" placeholder="消息内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitTest">发 送</el-button>
        <el-button @click="testOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMessageConfig, getMessageConfig, addMessageConfig, updateMessageConfig, delMessageConfig, changeConfigStatus, setDefaultConfig, testSendMessage } from '@/api/system/messageConfig'
import Pagination from '@/components/Pagination'

export default {
  name: 'MessageConfig',
  components: {
    Pagination
  },
  data() {
    return {
      loading: true,
      total: 0,
      configList: [],
      title: '',
      open: false,
      testOpen: false,
      queryParams: {
        current: 1,
        size: 10,
        configName: null,
        messageType: null,
        status: null
      },
      form: {},
      testForm: {},
      // 邮件配置
      emailConfig: {
        host: '',
        port: '587',
        from: '',
        username: '',
        password: '',
        fromName: '系统通知',
        ssl: true
      },
      // 短信配置
      smsConfig: {
        provider: 'aliyun',
        accessKeyId: '',
        accessKeySecret: '',
        signName: '',
        templateCode: ''
      },
      // 站内信配置
      systemConfig: {
        expireDays: '30',
        pushEnabled: true
      },
      // 微信配置
      wechatConfig: {
        appId: '',
        appSecret: '',
        templateId: ''
      },
      rules: {
        configName: [
          { required: true, message: '配置名称不能为空', trigger: 'blur' }
        ],
        messageType: [
          { required: true, message: '消息类型不能为空', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listMessageConfig(this.queryParams).then(response => {
        this.configList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    resetForm(refName) {
      if (this.$refs[refName]) {
        this.$refs[refName].resetFields()
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        configId: null,
        configName: null,
        messageType: '1',
        configData: null,
        isDefault: '0',
        status: '0',
        remark: null
      }
      // 重置配置对象
      this.emailConfig = {
        host: '',
        port: '587',
        from: '',
        username: '',
        password: '',
        fromName: '系统通知',
        ssl: true
      }
      this.smsConfig = {
        provider: 'aliyun',
        accessKeyId: '',
        accessKeySecret: '',
        signName: '',
        templateCode: ''
      }
      this.systemConfig = {
        expireDays: '30',
        pushEnabled: true
      }
      this.wechatConfig = {
        appId: '',
        appSecret: '',
        templateId: ''
      }
      this.resetForm('form')
    },
    handleTypeChange() {
      // 切换消息类型时只重置配置对象，不重置整个表单
      this.emailConfig = {
        host: '',
        port: '587',
        from: '',
        username: '',
        password: '',
        fromName: '系统通知',
        ssl: true
      }
      this.smsConfig = {
        provider: 'aliyun',
        accessKeyId: '',
        accessKeySecret: '',
        signName: '',
        templateCode: ''
      }
      this.systemConfig = {
        expireDays: '30',
        pushEnabled: true
      }
      this.wechatConfig = {
        appId: '',
        appSecret: '',
        templateId: ''
      }
    },
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加消息配置'
    },
    handleUpdate(row) {
      this.reset()
      getMessageConfig(row.configId).then(response => {
        this.form = response.data
        // 解析配置数据
        if (this.form.configData) {
          try {
            const config = JSON.parse(this.form.configData)
            if (this.form.messageType === '1') {
              this.emailConfig = { ...this.emailConfig, ...config }
            } else if (this.form.messageType === '2') {
              this.smsConfig = { ...this.smsConfig, ...config }
            } else if (this.form.messageType === '3') {
              this.systemConfig = { ...this.systemConfig, ...config }
            } else if (this.form.messageType === '4') {
              this.wechatConfig = { ...this.wechatConfig, ...config }
            }
          } catch (_) {
            // 配置数据解析失败，使用默认值
          }
        }
        this.open = true
        this.title = '修改消息配置'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          // 根据消息类型组装配置数据
          let configData = {}
          if (this.form.messageType === '1') {
            configData = this.emailConfig
          } else if (this.form.messageType === '2') {
            configData = this.smsConfig
          } else if (this.form.messageType === '3') {
            configData = this.systemConfig
          } else if (this.form.messageType === '4') {
            configData = this.wechatConfig
          }

          // 转换为JSON字符串
          this.form.configData = JSON.stringify(configData)

          if (this.form.configId != null) {
            updateMessageConfig(this.form).then(response => {
              this.$message.success('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addMessageConfig(this.form).then(response => {
              this.$message.success('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('是否确认删除该配置？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delMessageConfig(row.configId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    handleStatusChange(row) {
      let text = row.status === '0' ? '启用' : '停用'
      this.$confirm('确认要"' + text + '""' + row.configName + '"吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return changeConfigStatus({
          configId: row.configId,
          status: row.status
        })
      }).then(() => {
        this.$message.success(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    handleSetDefault(row) {
      this.$confirm('确认要将"' + row.configName + '"设为默认配置吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        return setDefaultConfig(row.configId)
      }).then(() => {
        this.getList()
        this.$message.success('设置成功')
      })
    },
    handleTest(row) {
      this.testForm = {
        configId: row.configId,
        messageType: row.messageType,
        receiver: '',
        subject: '',
        content: '这是一条测试消息'
      }
      this.testOpen = true
    },
    submitTest() {
      testSendMessage(this.testForm).then(response => {
        this.$message.success('发送成功')
        this.testOpen = false
      }).catch(() => {
        this.$message.error('发送失败')
      })
    }
  }
}
</script>

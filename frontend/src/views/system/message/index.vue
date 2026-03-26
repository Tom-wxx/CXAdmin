<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="80px">
      <el-form-item label="消息名称" prop="messageName">
        <el-input
          v-model="queryParams.messageName"
          placeholder="请输入消息名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消息编码" prop="messageCode">
        <el-input
          v-model="queryParams.messageCode"
          placeholder="请输入消息编码"
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
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
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
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="messageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="消息ID" align="center" prop="messageId" width="80" />
      <el-table-column label="消息名称" align="center" prop="messageName" :show-overflow-tooltip="true" />
      <el-table-column label="消息编码" align="center" prop="messageCode" :show-overflow-tooltip="true" />
      <el-table-column label="消息类型" align="center" prop="messageType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.messageType === '1'" type="success">邮件</el-tag>
          <el-tag v-else-if="scope.row.messageType === '2'" type="warning">短信</el-tag>
          <el-tag v-else-if="scope.row.messageType === '3'" type="info">站内信</el-tag>
          <el-tag v-else-if="scope.row.messageType === '4'" type="primary">微信</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="消息主题" align="center" prop="subject" :show-overflow-tooltip="true" />
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
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
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
        <el-form-item label="消息名称" prop="messageName">
          <el-input v-model="form.messageName" placeholder="请输入消息名称" />
        </el-form-item>
        <el-form-item label="消息编码" prop="messageCode">
          <el-input v-model="form.messageCode" placeholder="请输入消息编码" />
        </el-form-item>
        <el-form-item label="消息类型" prop="messageType">
          <el-radio-group v-model="form.messageType">
            <el-radio label="1">邮件</el-radio>
            <el-radio label="2">短信</el-radio>
            <el-radio label="3">站内信</el-radio>
            <el-radio label="4">微信</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="消息主题" prop="subject">
          <el-input v-model="form.subject" placeholder="请输入消息主题" />
        </el-form-item>
        <el-form-item label="消息内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入消息内容模板，支持变量如：${username}" />
        </el-form-item>
        <el-form-item label="变量说明" prop="variables">
          <el-input v-model="form.variables" type="textarea" :rows="3" placeholder='请输入JSON格式的变量说明，如：{"username":"用户名"}' />
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
  </div>
</template>

<script>
import { listMessage, getMessage, addMessage, updateMessage, delMessage, changeMessageStatus } from '@/api/system/message'
import Pagination from '@/components/Pagination'

export default {
  name: 'Message',
  components: {
    Pagination
  },
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      total: 0,
      messageList: [],
      title: '',
      open: false,
      queryParams: {
        current: 1,
        size: 10,
        messageName: null,
        messageCode: null,
        messageType: null,
        status: null
      },
      form: {},
      rules: {
        messageName: [
          { required: true, message: '消息名称不能为空', trigger: 'blur' }
        ],
        messageCode: [
          { required: true, message: '消息编码不能为空', trigger: 'blur' }
        ],
        messageType: [
          { required: true, message: '消息类型不能为空', trigger: 'change' }
        ],
        content: [
          { required: true, message: '消息内容不能为空', trigger: 'blur' }
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
      listMessage(this.queryParams).then(response => {
        this.messageList = response.rows
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
        messageId: null,
        messageName: null,
        messageCode: null,
        messageType: '1',
        subject: null,
        content: null,
        variables: null,
        status: '0',
        remark: null
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.messageId)
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加消息模板'
    },
    handleUpdate(row) {
      this.reset()
      const messageId = row.messageId || this.ids
      getMessage(messageId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改消息模板'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.messageId != null) {
            updateMessage(this.form).then(response => {
              this.$message.success('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addMessage(this.form).then(response => {
              this.$message.success('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const messageIds = row.messageId || this.ids
      this.$confirm('是否确认删除所选消息模板？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delMessage(messageIds)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    handleStatusChange(row) {
      let text = row.status === '0' ? '启用' : '停用'
      this.$confirm('确认要"' + text + '""' + row.messageName + '"吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return changeMessageStatus({
          messageId: row.messageId,
          status: row.status
        })
      }).then(() => {
        this.$message.success(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    }
  }
}
</script>

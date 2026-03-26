<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="80px">
      <el-form-item label="消息类型" prop="messageType">
        <el-select v-model="queryParams.messageType" placeholder="请选择消息类型" clearable size="small">
          <el-option label="邮件" value="1" />
          <el-option label="短信" value="2" />
          <el-option label="站内信" value="3" />
          <el-option label="微信" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="接收者" prop="receiver">
        <el-input
          v-model="queryParams.receiver"
          placeholder="请输入接收者"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发送状态" prop="sendStatus">
        <el-select v-model="queryParams.sendStatus" placeholder="请选择发送状态" clearable size="small">
          <el-option label="成功" value="0" />
          <el-option label="失败" value="1" />
          <el-option label="发送中" value="2" />
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
          type="danger"
          icon="el-icon-delete"
          size="mini"
          @click="handleClean"
        >清空</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="logList">
      <el-table-column label="日志ID" align="center" prop="logId" width="80" />
      <el-table-column label="消息类型" align="center" prop="messageType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.messageType === '1'" type="success">邮件</el-tag>
          <el-tag v-else-if="scope.row.messageType === '2'" type="warning">短信</el-tag>
          <el-tag v-else-if="scope.row.messageType === '3'" type="info">站内信</el-tag>
          <el-tag v-else-if="scope.row.messageType === '4'" type="primary">微信</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="接收者" align="center" prop="receiver" :show-overflow-tooltip="true" />
      <el-table-column label="主题" align="center" prop="subject" :show-overflow-tooltip="true" />
      <el-table-column label="发送状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.sendStatus === '0'" type="success">成功</el-tag>
          <el-tag v-else-if="scope.row.sendStatus === '1'" type="danger">失败</el-tag>
          <el-tag v-else type="warning">发送中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" width="160" />
      <el-table-column label="重试次数" align="center" prop="retryCount" width="80" />
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog title="日志详情" :visible.sync="open" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ detail.logId }}</el-descriptions-item>
        <el-descriptions-item label="消息类型">
          <el-tag v-if="detail.messageType === '1'" type="success">邮件</el-tag>
          <el-tag v-else-if="detail.messageType === '2'" type="warning">短信</el-tag>
          <el-tag v-else-if="detail.messageType === '3'" type="info">站内信</el-tag>
          <el-tag v-else-if="detail.messageType === '4'" type="primary">微信</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="接收者">{{ detail.receiver }}</el-descriptions-item>
        <el-descriptions-item label="发送状态">
          <el-tag v-if="detail.sendStatus === '0'" type="success">成功</el-tag>
          <el-tag v-else-if="detail.sendStatus === '1'" type="danger">失败</el-tag>
          <el-tag v-else type="warning">发送中</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="主题" :span="2">{{ detail.subject }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">
          <div style="max-height: 200px; overflow-y: auto; white-space: pre-wrap;">{{ detail.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ detail.sendTime }}</el-descriptions-item>
        <el-descriptions-item label="重试次数">{{ detail.retryCount }}</el-descriptions-item>
        <el-descriptions-item v-if="detail.errorMsg" label="错误信息" :span="2">
          <div style="color: red;">{{ detail.errorMsg }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMessageLog, getMessageLog, delMessageLog, cleanMessageLog } from '@/api/system/messageLog'
import Pagination from '@/components/Pagination'

export default {
  name: 'MessageLog',
  components: {
    Pagination
  },
  data() {
    return {
      loading: true,
      total: 0,
      logList: [],
      open: false,
      detail: {},
      queryParams: {
        current: 1,
        size: 10,
        messageType: null,
        receiver: null,
        sendStatus: null,
        startTime: null,
        endTime: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listMessageLog(this.queryParams).then(response => {
        this.logList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    resetForm(refName) {
      if (this.$refs[refName]) {
        this.$refs[refName].resetFields()
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
    handleView(row) {
      getMessageLog(row.logId).then(response => {
        this.detail = response.data
        this.open = true
      })
    },
    handleDelete(row) {
      this.$confirm('是否确认删除该日志？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delMessageLog(row.logId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    handleClean() {
      this.$confirm('是否确认清空所有日志？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return cleanMessageLog()
      }).then(() => {
        this.getList()
        this.$message.success('清空成功')
      })
    }
  }
}
</script>

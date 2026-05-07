<template>
  <div class="app-container">
    <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />

    <TableToolbar>
      <el-col :span="1.5">
        <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleClean">清空</el-button>
      </el-col>
    </TableToolbar>

    <el-table v-loading="loading" :data="logList">
      <el-table-column label="日志ID" align="center" prop="logId" width="80" />
      <el-table-column label="消息类型" align="center" prop="messageType" width="100">
        <template slot-scope="scope">
          <DictTag :options="messageTypeOptions" :value="scope.row.messageType" />
        </template>
      </el-table-column>
      <el-table-column label="接收者" align="center" prop="receiver" :show-overflow-tooltip="true" />
      <el-table-column label="主题" align="center" prop="subject" :show-overflow-tooltip="true" />
      <el-table-column label="发送状态" align="center" width="100">
        <template slot-scope="scope">
          <DictTag :options="sendStatusOptions" :value="scope.row.sendStatus" />
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" width="160" />
      <el-table-column label="重试次数" align="center" prop="retryCount" width="80" />
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.current"
      :limit.sync="queryParams.size"
      @pagination="getList"
    />

    <el-dialog title="日志详情" :visible.sync="open" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ detail.logId }}</el-descriptions-item>
        <el-descriptions-item label="消息类型">
          <DictTag :options="messageTypeOptions" :value="detail.messageType" />
        </el-descriptions-item>
        <el-descriptions-item label="接收者">{{ detail.receiver }}</el-descriptions-item>
        <el-descriptions-item label="发送状态">
          <DictTag :options="sendStatusOptions" :value="detail.sendStatus" />
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
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'
import DictTag from '@/components/DictTag'

const MESSAGE_TYPE_OPTIONS = [
  { value: '1', label: '邮件', type: 'success' },
  { value: '2', label: '短信', type: 'warning' },
  { value: '3', label: '站内信', type: 'info' },
  { value: '4', label: '微信', type: '' }
]
const SEND_STATUS_OPTIONS = [
  { value: '0', label: '成功', type: 'success' },
  { value: '1', label: '失败', type: 'danger' },
  { value: '2', label: '发送中', type: 'warning' }
]

export default {
  name: 'MessageLog',
  components: { Pagination, SearchForm, TableToolbar, DictTag },
  data() {
    return {
      searchFields: [
        { prop: 'messageType', label: '消息类型', type: 'select', options: MESSAGE_TYPE_OPTIONS, placeholder: '请选择消息类型' },
        { prop: 'receiver', label: '接收者', type: 'input', placeholder: '请输入接收者' },
        { prop: 'sendStatus', label: '发送状态', type: 'select', options: SEND_STATUS_OPTIONS, placeholder: '请选择发送状态' }
      ],
      messageTypeOptions: MESSAGE_TYPE_OPTIONS,
      sendStatusOptions: SEND_STATUS_OPTIONS,
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
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.current = 1
      this.getList()
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

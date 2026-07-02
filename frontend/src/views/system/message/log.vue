<template>
  <div class="app-container">
    <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />

    <TableToolbar>
      <el-col :span="1.5">
        <el-button type="danger" icon="Delete" size="small" @click="handleClean">清空</el-button>
      </el-col>
    </TableToolbar>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="日志ID" align="center" prop="logId" width="80" />
      <el-table-column label="消息类型" align="center" prop="messageType" width="100">
        <template #default="scope">
          <DictTag :options="messageTypeOptions" :value="scope.row.messageType" />
        </template>
      </el-table-column>
      <el-table-column label="接收者" align="center" prop="receiver" :show-overflow-tooltip="true" />
      <el-table-column label="主题" align="center" prop="subject" :show-overflow-tooltip="true" />
      <el-table-column label="发送状态" align="center" width="100">
        <template #default="scope">
          <DictTag :options="sendStatusOptions" :value="scope.row.sendStatus" />
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" width="160" />
      <el-table-column label="重试次数" align="center" prop="retryCount" width="80" />
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button size="small" type="text" icon="View" @click="handleView(scope.row)">详情</el-button>
          <el-button size="small" type="text" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <Pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <el-dialog title="日志详情" v-model="open" width="700px" append-to-body>
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
      <template #footer><div class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMessageLog, getMessageLog, delMessageLog, cleanMessageLog } from '@/api/system/messageLog'
import { useCrudTable } from '@/composables'
import type { MessageLog, MessageLogQuery } from '@/types/system/message'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'

defineOptions({ name: 'MessageLog' })

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

const messageTypeOptions = MESSAGE_TYPE_OPTIONS
const sendStatusOptions = SEND_STATUS_OPTIONS

const searchFields = [
  { prop: 'messageType', label: '消息类型', type: 'select', options: MESSAGE_TYPE_OPTIONS, placeholder: '请选择消息类型' },
  { prop: 'receiver', label: '接收者', type: 'input', placeholder: '请输入接收者' },
  { prop: 'sendStatus', label: '发送状态', type: 'select', options: SEND_STATUS_OPTIONS, placeholder: '请选择发送状态' }
]

const { loading, list, total, queryParams, getList, handleQuery, resetQuery } =
  useCrudTable<MessageLog, MessageLogQuery>({
    listApi: listMessageLog,
    defaultQuery: { messageType: undefined, receiver: undefined, sendStatus: undefined } as Partial<MessageLogQuery>
  })

const open = ref(false)
const detail = reactive<MessageLog>({})

function handleView(row: MessageLog) {
  getMessageLog(row.logId as number).then(response => {
    Object.assign(detail, response.data)
    open.value = true
  })
}

function handleDelete(row: MessageLog) {
  ElMessageBox.confirm('是否确认删除该日志？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delMessageLog(row.logId as number)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => { /* 用户取消 */ })
}

function handleClean() {
  ElMessageBox.confirm('是否确认清空所有日志？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return cleanMessageLog()
  }).then(() => {
    getList()
    ElMessage.success('清空成功')
  }).catch(() => { /* 用户取消 */ })
}
</script>

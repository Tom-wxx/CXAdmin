<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <!-- 操作按钮 -->
    <TableToolbar show-refresh @refresh="getList">
      <el-col :span="1.5">
        <el-button type="success" plain icon="Check" size="small" :disabled="multiple" @click="handleMarkAsRead">标记已读</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Finished" size="small" @click="handleMarkAllAsRead">全部已读</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" size="small" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
    </TableToolbar>

    <!-- 通知列表 -->
    <el-table v-loading="loading" :data="notificationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="优先级" align="center" width="80">
        <template #default="scope">
          <DictTag :options="priorityOptions" :value="scope.row.priority || 'normal'" />
        </template>
      </el-table-column>
      <el-table-column label="通知标题" prop="title" :show-overflow-tooltip="true" min-width="200">
        <template #default="scope">
          <span :style="{ fontWeight: scope.row.status === 'unread' ? 'bold' : 'normal' }">
            {{ scope.row.title }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="通知类型" align="center" prop="type" width="100">
        <template #default="scope">
          <span v-if="scope.row.type === 'system'">系统通知</span>
          <span v-else-if="scope.row.type === 'todo'">待办提醒</span>
          <span v-else-if="scope.row.type === 'approval'">审批消息</span>
          <span v-else-if="scope.row.type === 'announce'">公告</span>
          <span v-else>{{ scope.row.type }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发送者" align="center" prop="senderName" width="120" :show-overflow-tooltip="true">
        <template #default="scope">
          {{ scope.row.senderName || '系统' }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template #default="scope">
          <el-button
            size="small"
            type="text"
            icon="View"
            @click="handleView(scope.row)"
          >查看</el-button>
          <el-button
            size="small"
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <!-- 查看通知对话框 -->
    <el-dialog :title="viewForm.title" v-model="viewOpen" width="600px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="通知类型">
          <span v-if="viewForm.type === 'system'">系统通知</span>
          <span v-else-if="viewForm.type === 'todo'">待办提醒</span>
          <span v-else-if="viewForm.type === 'approval'">审批消息</span>
          <span v-else-if="viewForm.type === 'announce'">公告</span>
          <span v-else>{{ viewForm.type }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <DictTag :options="priorityOptions" :value="viewForm.priority || 'normal'" />
        </el-descriptions-item>
        <el-descriptions-item label="发送者">
          {{ viewForm.senderName || '系统' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ parseTime(viewForm.createTime || '') }}
        </el-descriptions-item>
        <el-descriptions-item label="通知内容">
          <div v-html="viewForm.content" style="white-space: pre-wrap;"></div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer><div class="dialog-footer">
        <el-button @click="viewOpen = false">关闭</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listNotification,
  getNotification,
  batchMarkAsRead,
  markAllAsRead,
  delNotification
} from '@/api/system/notification'
import type { Notification, NotificationQuery } from '@/types/system/notification'
import { parseTime } from '@/utils/index.js'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'

defineOptions({ name: 'Notification' })

const TYPE_OPTIONS = [
  { value: 'system', label: '系统通知' },
  { value: 'todo', label: '待办提醒' },
  { value: 'approval', label: '审批消息' },
  { value: 'announce', label: '公告' }
]
const STATUS_OPTIONS = [
  { value: 'unread', label: '未读', type: 'danger' },
  { value: 'read', label: '已读', type: 'info' }
]
const PRIORITY_OPTIONS = [
  { value: 'urgent', label: '紧急', type: 'danger' },
  { value: 'important', label: '重要', type: 'warning' },
  { value: 'normal', label: '普通', type: 'info' }
]

const searchFields = [
  { prop: 'title', label: '通知标题', type: 'input' },
  { prop: 'type', label: '通知类型', type: 'select', options: TYPE_OPTIONS, placeholder: '通知类型' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '状态' }
]
const statusOptions = STATUS_OPTIONS
const priorityOptions = PRIORITY_OPTIONS

const loading = ref(true)
const ids = ref<number[]>([])
const multiple = ref(true)
const total = ref(0)
const notificationList = ref<Notification[]>([])
const viewOpen = ref(false)
const viewForm = ref<Notification>({})

const queryParams = reactive<NotificationQuery>({
  current: 1,
  size: 10,
  title: undefined,
  type: undefined,
  status: undefined
})

/** 查询通知列表 */
function getList() {
  loading.value = true
  listNotification(queryParams).then(res => {
    notificationList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.current = 1
  getList()
}

/** 重置按钮操作（SearchForm 已自动清字段） */
function resetQuery() {
  queryParams.current = 1
  queryParams.size = 10
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection: Notification[]) {
  ids.value = selection.map(item => item.id as number)
  multiple.value = !selection.length
}

/** 查看通知 */
function handleView(row: Notification) {
  getNotification(row.id as number).then(res => {
    viewForm.value = res.data
    viewOpen.value = true
    // 刷新列表（因为查看会自动标记为已读）
    getList()
  })
}

/** 标记已读 */
function handleMarkAsRead() {
  if (ids.value.length === 0) {
    ElMessage.warning('请选择要标记的通知')
    return
  }
  batchMarkAsRead(ids.value).then(() => {
    ElMessage.success('标记成功')
    getList()
  })
}

/** 全部标记为已读 */
function handleMarkAllAsRead() {
  ElMessageBox.confirm('确认将所有未读消息标记为已读吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => markAllAsRead())
    .then(() => {
      ElMessage.success('标记成功')
      getList()
    })
    .catch(() => { /* 用户取消 */ })
}

/** 删除按钮操作 */
function handleDelete(row?: Notification) {
  const delIds = row?.id ? [row.id] : ids.value
  if (delIds.length === 0) {
    ElMessage.warning('请选择要删除的通知')
    return
  }
  ElMessageBox.confirm('是否确认删除选中的通知?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => delNotification(delIds))
    .then(() => {
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => { /* 用户取消 */ })
}

getList()
</script>

<template>
  <div class="app-container">
    <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />

    <TableToolbar show-refresh @refresh="handleRefresh">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          size="small"
          :disabled="multiple"
          @click="handleBatchForceLogout"
        >批量强退</el-button>
      </el-col>
    </TableToolbar>

    <el-table
      v-loading="loading"
      :data="onlineList"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="60" align="center">
        <template #default="scope">
          <span>{{ (queryParams.current - 1) * queryParams.size + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会话编号" align="center" prop="tokenId" show-overflow-tooltip width="260" />
      <el-table-column label="用户名" align="center" prop="username" width="120" />
      <el-table-column label="用户昵称" align="center" prop="nickname" width="120" />
      <el-table-column label="部门名称" align="center" prop="deptName" show-overflow-tooltip />
      <el-table-column label="登录地址" align="center" prop="ipaddr" width="140" />
      <el-table-column label="登录地点" align="center" prop="loginLocation" show-overflow-tooltip />
      <el-table-column label="浏览器" align="center" prop="browser" show-overflow-tooltip />
      <el-table-column label="操作系统" align="center" prop="os" show-overflow-tooltip />
      <el-table-column label="登录时间" align="center" prop="loginTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            size="small"
            type="text"
            icon="Delete"
            @click="handleForceLogout(scope.row)"
          >强退</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0"
      v-model:current-page="queryParams.current"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="queryParams.size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 15px; text-align: right"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listOnlineUser, forceLogout, batchForceLogout } from '@/api/monitor/online'
import { parseTime } from '@/utils'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import type { OnlineUser, OnlineQuery } from '@/types/monitor/online'

defineOptions({ name: 'OnlineUser' })

const searchFields = [
  { prop: 'username', label: '用户名', type: 'input', placeholder: '请输入用户名' },
  { prop: 'ipaddr', label: '登录地址', type: 'input', placeholder: '请输入登录地址' }
]

const loading = ref(true)
const ids = ref<string[]>([])
const multiple = ref(true)
const onlineList = ref<OnlineUser[]>([])
const total = ref(0)
const queryParams = reactive<OnlineQuery & { current: number; size: number }>({
  username: undefined,
  ipaddr: undefined,
  current: 1,
  size: 10
})

function getList() {
  loading.value = true
  listOnlineUser(queryParams).then(res => {
    onlineList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.current = 1
  getList()
}

function resetQuery() {
  queryParams.current = 1
  getList()
}

function handleSizeChange(size: number) {
  queryParams.size = size
  queryParams.current = 1
  getList()
}

function handleCurrentChange(current: number) {
  queryParams.current = current
  getList()
}

function handleRefresh() {
  getList()
  ElMessage.success('刷新成功')
}

function handleSelectionChange(selection: OnlineUser[]) {
  ids.value = selection.map(item => item.tokenId as string)
  multiple.value = !selection.length
}

function handleForceLogout(row: OnlineUser) {
  ElMessageBox.confirm('是否确认强退用户"' + row.username + '"？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return forceLogout(row.tokenId as string)
  }).then(() => {
    getList()
    ElMessage.success('强制退出成功')
  })
}

function handleBatchForceLogout() {
  const tokenIds = ids.value
  if (tokenIds.length === 0) {
    ElMessage.warning('请选择要强退的用户')
    return
  }
  ElMessageBox.confirm('是否确认强退选中的' + tokenIds.length + '个用户？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return batchForceLogout(tokenIds.join(','))
  }).then(() => {
    getList()
    ElMessage.success('批量强制退出成功')
  })
}

// init
getList()
</script>

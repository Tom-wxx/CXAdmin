<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <!-- 工具栏 -->
    <TableToolbar show-export show-refresh :export-disabled="exportLoading" @export="handleExport" @refresh="getList">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" size="small" @click="handleClean">清空</el-button>
      </el-col>
    </TableToolbar>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="loginLogList" border>
      <el-table-column label="日志编号" align="center" prop="infoId" width="100" />
      <el-table-column label="用户账号" align="center" prop="username" width="120" />
      <el-table-column label="登录地址" align="center" prop="ipaddr" width="130" show-overflow-tooltip />
      <el-table-column label="登录地点" align="center" prop="loginLocation" show-overflow-tooltip />
      <el-table-column label="浏览器" align="center" prop="browser" show-overflow-tooltip />
      <el-table-column label="操作系统" align="center" prop="os" show-overflow-tooltip />
      <el-table-column label="登录状态" align="center" width="100">
        <template #default="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="提示消息" align="center" prop="msg" show-overflow-tooltip />
      <el-table-column label="登录时间" align="center" prop="loginTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            size="small"
            type="text"
            icon="View"
            @click="handleView(scope.row)"
          >详细</el-button>
          <el-button
            size="small"
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <!-- 详细信息对话框 -->
    <el-dialog title="登录日志详细" v-model="detailVisible" width="700px" append-to-body>
      <el-form :model="detailData" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="日志编号：">{{ detailData.infoId }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户账号：">{{ detailData.username }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录地址：">{{ detailData.ipaddr }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录地点：">{{ detailData.loginLocation }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="浏览器：">{{ detailData.browser }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作系统：">{{ detailData.os }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录状态：">
              <DictTag :options="statusOptions" :value="detailData.status" size="default" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录时间：">{{ parseTime(detailData.loginTime || '') }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="提示消息：">
              <div style="word-break: break-all;">
                {{ detailData.msg }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer><div>
        <el-button @click="detailVisible = false">关 闭</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listLoginLog, getLoginLog, delLoginLog, cleanLoginLog, exportLoginLog } from '@/api/monitor/loginlog'
import { parseTime } from '@/utils'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'
import type { LoginLog, LoginLogQuery } from '@/types/monitor/log'

defineOptions({ name: 'LoginLog' })

const STATUS_OPTIONS = [
  { value: '0', label: '成功', type: 'success' },
  { value: '1', label: '失败', type: 'danger' }
]

const statusOptions = STATUS_OPTIONS

const searchFields = [
  { prop: 'username', label: '用户账号', type: 'input', width: '180px' },
  { prop: 'ipaddr', label: '登录地址', type: 'input', placeholder: '请输入登录IP地址', width: '180px' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '登录状态', width: '180px' },
  { prop: 'dateRange', label: '登录时间', type: 'daterange' }
]

const loading = ref(true)
const loginLogList = ref<LoginLog[]>([])
const total = ref(0)
const queryParams = reactive<LoginLogQuery & { current: number; size: number; dateRange: string[]; beginTime?: string; endTime?: string }>({
  current: 1,
  size: 10,
  username: undefined,
  ipaddr: undefined,
  status: undefined,
  dateRange: [],
  beginTime: undefined,
  endTime: undefined
})
const detailVisible = ref(false)
const detailData = ref<LoginLog>({})
const exportLoading = ref(false)

/** 查询登录日志列表 */
function getList() {
  loading.value = true
  const range = queryParams.dateRange
  if (range && range.length === 2) {
    queryParams.beginTime = range[0]
    queryParams.endTime = range[1]
  } else {
    queryParams.beginTime = undefined
    queryParams.endTime = undefined
  }
  listLoginLog(queryParams).then(res => {
    loginLogList.value = res.rows
    total.value = res.total
  }).finally(() => {
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.current = 1
  getList()
}

/** 重置按钮操作（SearchForm 已自动清字段，这里只做分页归位） */
function resetQuery() {
  queryParams.current = 1
  queryParams.size = 10
  handleQuery()
}

/** 详细按钮操作 */
function handleView(row: LoginLog) {
  getLoginLog(row.infoId as number).then(res => {
    detailData.value = res.data
    detailVisible.value = true
  })
}

/** 删除按钮操作 */
function handleDelete(row: LoginLog) {
  ElMessageBox.confirm('是否确认删除日志编号为"' + row.infoId + '"的数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delLoginLog(row.infoId as number)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  })
}

/** 清空按钮操作 */
function handleClean() {
  ElMessageBox.confirm('是否确认清空所有登录日志数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return cleanLoginLog()
  }).then(() => {
    getList()
    ElMessage.success('清空成功')
  })
}

/** 导出按钮操作 */
function handleExport() {
  ElMessageBox.confirm('是否确认导出所有登录日志数据？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    exportLoading.value = true
    return exportLoginLog(queryParams)
  }).then(response => {
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '登录日志.xlsx'
    link.click()
    URL.revokeObjectURL(link.href)
    exportLoading.value = false
    ElMessage.success('导出成功')
  }).catch(() => {
    exportLoading.value = false
  })
}

// init
getList()
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.mb8 {
  margin-bottom: 8px;
}
</style>

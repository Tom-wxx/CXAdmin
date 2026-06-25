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
    <el-table v-loading="loading" :data="operLogList" border>
      <el-table-column label="日志编号" align="center" prop="operId" width="100" />
      <el-table-column label="系统模块" align="center" prop="title" />
      <el-table-column label="操作类型" align="center" width="100">
        <template #default="scope">
          <DictTag :options="businessTypeOptions" :value="scope.row.businessType" />
        </template>
      </el-table-column>
      <el-table-column label="操作人员" align="center" prop="operName" width="100" />
      <el-table-column label="操作地址" align="center" prop="operIp" width="130" show-overflow-tooltip />
      <el-table-column label="操作地点" align="center" prop="operLocation" show-overflow-tooltip />
      <el-table-column label="操作状态" align="center" width="100">
        <template #default="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作时间" align="center" prop="operTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.operTime) }}</span>
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
    <el-dialog title="操作日志详细" v-model="detailVisible" width="700px" append-to-body>
      <el-form :model="detailData" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="操作模块：">{{ detailData.title }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录信息：">{{ detailData.operName }} / {{ detailData.operIp }} / {{ detailData.operLocation }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求地址：">{{ detailData.operUrl }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求方式：">{{ detailData.requestMethod }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作方法：">{{ detailData.method }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求参数：">
              <div style="max-height: 200px; overflow-y: auto; word-break: break-all;">
                {{ detailData.operParam }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回参数：">
              <div style="max-height: 200px; overflow-y: auto; word-break: break-all;">
                {{ detailData.jsonResult }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作状态：">
              <DictTag :options="statusOptions" :value="detailData.status" size="default" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作时间：">{{ parseTime(detailData.operTime || '') }}</el-form-item>
          </el-col>
          <el-col :span="24" v-if="detailData.errorMsg">
            <el-form-item label="异常信息：">
              <div style="max-height: 200px; overflow-y: auto; word-break: break-all; color: #F56C6C;">
                {{ detailData.errorMsg }}
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
import { listOperLog, getOperLog, delOperLog, cleanOperLog, exportOperLog } from '@/api/monitor/operlog'
import { parseTime } from '@/utils'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'
import type { OperLog, OperLogQuery } from '@/types/monitor/log'

defineOptions({ name: 'OperLog' })

const BUSINESS_TYPE_OPTIONS = [
  { value: 0, label: '其它', type: 'info' },
  { value: 1, label: '新增', type: 'success' },
  { value: 2, label: '修改', type: 'warning' },
  { value: 3, label: '删除', type: 'danger' },
  { value: 4, label: '授权' },
  { value: 5, label: '导出' },
  { value: 6, label: '导入' },
  { value: 7, label: '强退' },
  { value: 8, label: '清空数据' }
]
const STATUS_OPTIONS = [
  { value: 0, label: '正常', type: 'success' },
  { value: 1, label: '异常', type: 'danger' }
]

const businessTypeOptions = BUSINESS_TYPE_OPTIONS
const statusOptions = STATUS_OPTIONS

const searchFields = [
  { prop: 'title', label: '系统模块', type: 'input', width: '180px' },
  { prop: 'operName', label: '操作人员', type: 'input', width: '180px' },
  { prop: 'businessType', label: '类型', type: 'select', options: BUSINESS_TYPE_OPTIONS, placeholder: '操作类型', width: '180px' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '操作状态', width: '180px' },
  { prop: 'dateRange', label: '操作时间', type: 'daterange' }
]

const loading = ref(true)
const operLogList = ref<OperLog[]>([])
const total = ref(0)
const queryParams = reactive<OperLogQuery & { current: number; size: number; dateRange: string[]; beginTime?: string; endTime?: string }>({
  current: 1,
  size: 10,
  title: undefined,
  operName: undefined,
  businessType: undefined,
  status: undefined,
  dateRange: [],
  beginTime: undefined,
  endTime: undefined
})
const detailVisible = ref(false)
const detailData = ref<OperLog>({})
const exportLoading = ref(false)

/** 查询操作日志列表 */
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
  listOperLog(queryParams).then(res => {
    operLogList.value = res.rows
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
function handleView(row: OperLog) {
  getOperLog(row.operId as number).then(res => {
    detailData.value = res.data
    detailVisible.value = true
  })
}

/** 删除按钮操作 */
function handleDelete(row: OperLog) {
  ElMessageBox.confirm('是否确认删除日志编号为"' + row.operId + '"的数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delOperLog(row.operId as number)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  })
}

/** 清空按钮操作 */
function handleClean() {
  ElMessageBox.confirm('是否确认清空所有操作日志数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return cleanOperLog()
  }).then(() => {
    getList()
    ElMessage.success('清空成功')
  })
}

/** 导出按钮操作 */
function handleExport() {
  ElMessageBox.confirm('是否确认导出所有操作日志数据？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    exportLoading.value = true
    return exportOperLog(queryParams)
  }).then(response => {
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '操作日志.xlsx'
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

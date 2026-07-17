<template>
  <div class="app-container">
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <TableToolbar show-refresh @refresh="getList">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" size="small" @click="handleClean">清空</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Back" size="small" @click="handleBack">返回</el-button>
      </el-col>
    </TableToolbar>

    <el-table v-loading="loading" :data="jobLogList" border>
      <el-table-column label="日志编号" align="center" prop="jobLogId" width="80" />
      <el-table-column label="任务名称" align="center" prop="jobName" show-overflow-tooltip />
      <el-table-column label="任务组名" align="center" prop="jobGroup" width="100" />
      <el-table-column label="调用目标" align="center" prop="invokeTarget" show-overflow-tooltip />
      <el-table-column label="日志信息" align="center" prop="jobMessage" show-overflow-tooltip />
      <el-table-column label="执行状态" align="center" width="100">
        <template #default="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="执行时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
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

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <el-dialog title="调度日志详细" v-model="viewDialogVisible" width="700px" append-to-body>
      <el-form :model="viewForm" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="日志编号：">{{ viewForm.jobLogId }}</el-form-item>
            <el-form-item label="任务名称：">{{ viewForm.jobName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务组名：">{{ viewForm.jobGroup }}</el-form-item>
            <el-form-item label="执行时间：">{{ parseTime(viewForm.createTime || '') }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="调用方法：">{{ viewForm.invokeTarget }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="日志信息：">{{ viewForm.jobMessage }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="执行状态：">
              <DictTag :options="statusOptions" :value="viewForm.status" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="viewForm.status === '1'">
            <el-form-item label="异常信息：">
              <div style="white-space: pre-wrap; max-height: 300px; overflow-y: auto; background: #f5f5f5; padding: 10px; border-radius: 4px;">
                {{ viewForm.exceptionInfo }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer><div class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关 闭</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listJobLog, getJobLog, delJobLog, cleanJobLog } from '@/api/monitor/job'
import { parseTime } from '@/utils'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'
import type { JobLog, JobLogQuery } from '@/types/monitor/job'

defineOptions({ name: 'JobLog' })

const router = useRouter()

const JOB_GROUP_OPTIONS = [
  { value: 'DEFAULT', label: '默认' },
  { value: 'SYSTEM', label: '系统' }
]
const STATUS_OPTIONS = [
  { value: '0', label: '成功', type: 'success' },
  { value: '1', label: '失败', type: 'danger' }
]

const statusOptions = STATUS_OPTIONS

const searchFields = [
  { prop: 'jobName', label: '任务名称', type: 'input' },
  { prop: 'jobGroup', label: '任务组名', type: 'select', options: JOB_GROUP_OPTIONS, placeholder: '任务组名' },
  { prop: 'status', label: '执行状态', type: 'select', options: STATUS_OPTIONS, placeholder: '执行状态' }
]

const loading = ref(true)
const jobLogList = ref<JobLog[]>([])
const total = ref(0)
const queryParams = reactive<JobLogQuery & { current: number; size: number }>({
  current: 1,
  size: 10,
  jobName: undefined,
  jobGroup: undefined,
  status: undefined
})
const viewDialogVisible = ref(false)
const viewForm = ref<JobLog>({})

function getList() {
  loading.value = true
  listJobLog(queryParams).then(res => {
    jobLogList.value = res.rows
    total.value = res.total
  }).finally(() => {
    loading.value = false
  })
}

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

function handleView(row: JobLog) {
  getJobLog(row.jobLogId as number).then(res => {
    viewForm.value = res.data
    viewDialogVisible.value = true
  })
}

function handleDelete(row: JobLog) {
  ElMessageBox.confirm('是否确认删除日志编号为"' + row.jobLogId + '"的数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delJobLog(row.jobLogId as number)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  })
}

function handleClean() {
  ElMessageBox.confirm('是否确认清空所有调度日志数据？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return cleanJobLog()
  }).then(() => {
    getList()
    ElMessage.success('清空成功')
  })
}

function handleBack() {
  router.push('/monitor/job')
}

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

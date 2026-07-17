<template>
  <div class="app-container">
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <TableToolbar show-add show-refresh @add="handleAdd" @refresh="getList">
      <el-col :span="1.5">
        <el-button type="success" plain icon="Document" size="small" @click="handleJobLog">日志</el-button>
      </el-col>
    </TableToolbar>

    <el-table v-loading="loading" :data="jobList" border>
      <el-table-column label="任务编号" align="center" prop="jobId" width="80" />
      <el-table-column label="任务名称" align="center" prop="jobName" show-overflow-tooltip />
      <el-table-column label="任务组名" align="center" prop="jobGroup" width="100" />
      <el-table-column label="调用目标" align="center" prop="invokeTarget" show-overflow-tooltip />
      <el-table-column label="Cron表达式" align="center" prop="cronExpression" show-overflow-tooltip />
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            size="small"
            type="text"
            icon="Edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="small"
            type="text"
            icon="VideoPlay"
            @click="handleRun(scope.row)"
          >执行</el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px" append-to-body>
      <el-form ref="jobFormRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="form.jobName" placeholder="请输入任务名称" maxlength="64" />
        </el-form-item>
        <el-form-item label="任务组名" prop="jobGroup">
          <el-select v-model="form.jobGroup" placeholder="请选择任务组名" style="width: 100%">
            <el-option label="默认" value="DEFAULT" />
            <el-option label="系统" value="SYSTEM" />
          </el-select>
        </el-form-item>
        <el-form-item label="调用目标" prop="invokeTarget">
          <el-input v-model="form.invokeTarget" placeholder="如：testTask.execute" maxlength="500" />
          <span class="help-text">Bean名称.方法名，如：testTask.execute</span>
        </el-form-item>
        <el-form-item label="Cron表达式" prop="cronExpression">
          <el-input v-model="form.cronExpression" placeholder="如：0/10 * * * * ?" maxlength="255" />
          <span class="help-text">每10秒：0/10 * * * * ? | 每天凌晨1点：0 0 1 * * ?</span>
        </el-form-item>
        <el-form-item label="错误策略" prop="misfirePolicy">
          <el-radio-group v-model="form.misfirePolicy">
            <el-radio value="1">立即执行</el-radio>
            <el-radio value="2">执行一次</el-radio>
            <el-radio value="3">放弃执行</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="并发执行" prop="concurrent">
          <el-radio-group v-model="form.concurrent">
            <el-radio value="0">允许</el-radio>
            <el-radio value="1">禁止</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listJob, getJob, addJob, updateJob, delJob, changeJobStatus, runJob } from '@/api/monitor/job'
import type { Job, JobQuery } from '@/types/monitor/job'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'

defineOptions({ name: 'Job' })

const router = useRouter()

const JOB_GROUP_OPTIONS = [
  { value: 'DEFAULT', label: '默认' },
  { value: 'SYSTEM', label: '系统' }
]
const STATUS_OPTIONS = [
  { value: '0', label: '正常' },
  { value: '1', label: '暂停' }
]

const searchFields = [
  { prop: 'jobName', label: '任务名称', type: 'input' },
  { prop: 'jobGroup', label: '任务组名', type: 'select', options: JOB_GROUP_OPTIONS, placeholder: '任务组名' },
  { prop: 'status', label: '任务状态', type: 'select', options: STATUS_OPTIONS, placeholder: '任务状态' }
]

const loading = ref(true)
const jobList = ref<Job[]>([])
const total = ref(0)

const queryParams = reactive<JobQuery>({
  current: 1,
  size: 10,
  jobName: undefined,
  jobGroup: undefined,
  status: undefined
})

const dialogTitle = ref('')
const dialogVisible = ref(false)
const jobFormRef = ref<FormInstance>()

const emptyForm = (): Job => ({
  jobId: undefined,
  jobName: undefined,
  jobGroup: 'DEFAULT',
  invokeTarget: undefined,
  cronExpression: undefined,
  misfirePolicy: '3',
  concurrent: '1',
  status: '1',
  remark: undefined
})

const form = ref<Job>(emptyForm())

const rules: FormRules = {
  jobName: [
    { required: true, message: '任务名称不能为空', trigger: 'blur' },
    { max: 64, message: '任务名称长度不能超过64个字符', trigger: 'blur' }
  ],
  jobGroup: [
    { required: true, message: '任务组名不能为空', trigger: 'change' }
  ],
  invokeTarget: [
    { required: true, message: '调用目标不能为空', trigger: 'blur' },
    { max: 500, message: '调用目标长度不能超过500个字符', trigger: 'blur' }
  ],
  cronExpression: [
    { required: true, message: 'Cron表达式不能为空', trigger: 'blur' }
  ]
}

function getList() {
  loading.value = true
  listJob(queryParams).then(res => {
    jobList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => {
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

function reset() {
  form.value = emptyForm()
  jobFormRef.value?.resetFields()
}

function handleAdd() {
  reset()
  dialogTitle.value = '添加定时任务'
  dialogVisible.value = true
}

function handleUpdate(row: Job) {
  reset()
  getJob(row.jobId as number).then(res => {
    form.value = res.data
    dialogTitle.value = '修改定时任务'
    dialogVisible.value = true
  })
}

function submitForm() {
  jobFormRef.value?.validate(valid => {
    if (valid) {
      if (form.value.jobId) {
        updateJob(form.value).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        })
      } else {
        addJob(form.value).then(() => {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          getList()
        })
      }
    }
  })
}

function cancel() {
  dialogVisible.value = false
  reset()
}

function handleStatusChange(row: Job) {
  const text = row.status === '0' ? '启用' : '停用'
  ElMessageBox.confirm('确认要"' + text + '""' + row.jobName + '"任务吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => changeJobStatus({ jobId: row.jobId as number, status: row.status as string }))
    .then(() => {
      ElMessage.success(text + '成功')
    })
    .catch(() => {
      row.status = row.status === '0' ? '1' : '0'
    })
}

function handleRun(row: Job) {
  ElMessageBox.confirm('确认要立即执行一次"' + row.jobName + '"任务吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => runJob({ jobId: row.jobId as number, jobGroup: row.jobGroup }))
    .then(() => {
      ElMessage.success('执行成功')
    })
    .catch(() => { /* 用户取消 */ })
}

function handleDelete(row: Job) {
  ElMessageBox.confirm('是否确认删除任务名称为"' + row.jobName + '"的数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => delJob(row.jobId as number))
    .then(() => {
      getList()
      ElMessage.success('删除成功')
    })
    .catch(() => { /* 用户取消 */ })
}

function handleJobLog() {
  router.push('/monitor/jobLog')
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
.help-text {
  font-size: 12px;
  color: #999;
  display: block;
  margin-top: 5px;
}
</style>

<template>
  <div class="app-container">
    <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />

    <TableToolbar show-add show-delete :multiple="multiple" @add="handleAdd" @delete="handleDelete" />

    <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模板编号" align="center" prop="id" width="80" />
      <el-table-column label="模板编码" align="center" prop="templateCode" :show-overflow-tooltip="true" />
      <el-table-column label="模板名称" align="center" prop="templateName" :show-overflow-tooltip="true" />
      <el-table-column label="通知类型" align="center" prop="type" width="100">
        <template #default="scope">
          <span v-if="scope.row.type === 'system'">系统通知</span>
          <span v-else-if="scope.row.type === 'todo'">待办提醒</span>
          <span v-else-if="scope.row.type === 'approval'">审批消息</span>
          <span v-else-if="scope.row.type === 'announce'">公告</span>
          <span v-else>{{ scope.row.type }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优先级" align="center" prop="priority" width="100">
        <template #default="scope">
          <DictTag :options="priorityOptions" :value="scope.row.priority || 'normal'" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="1"
            inactive-value="0"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template #default="scope">
          <el-button
            size="small"
            type="text"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:notification:template:edit']"
          >修改</el-button>
          <el-button
            size="small"
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:notification:template:remove']"
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

    <el-dialog :title="dialogTitle" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模板编码" prop="templateCode">
          <el-input v-model="form.templateCode" placeholder="请输入模板编码" />
        </el-form-item>
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择通知类型">
            <el-option label="系统通知" value="system" />
            <el-option label="待办提醒" value="todo" />
            <el-option label="审批消息" value="approval" />
            <el-option label="公告" value="announce" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" placeholder="请选择优先级">
            <el-option label="普通" value="normal" />
            <el-option label="重要" value="important" />
            <el-option label="紧急" value="urgent" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题模板" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题模板，支持变量如 {userName}" />
        </el-form-item>
        <el-form-item label="内容模板" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入内容模板，支持变量如 {userName}、{roleName}" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="1">启用</el-radio>
            <el-radio value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  listTemplate,
  getTemplate,
  addTemplate,
  updateTemplate,
  delTemplate,
  changeTemplateStatus
} from '@/api/system/notificationTemplate'
import type { NotificationTemplate, NotificationTemplateQuery } from '@/types/system/notification'
import { parseTime } from '@/utils/index.js'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'

defineOptions({ name: 'NotificationTemplate' })

const PRIORITY_OPTIONS = [
  { value: 'urgent', label: '紧急', type: 'danger' },
  { value: 'important', label: '重要', type: 'warning' },
  { value: 'normal', label: '普通', type: 'info' }
]
const STATUS_SEARCH_OPTIONS = [
  { value: '1', label: '启用' },
  { value: '0', label: '停用' }
]

const searchFields = [
  { prop: 'templateName', label: '模板名称', type: 'input', placeholder: '请输入模板名称' },
  { prop: 'templateCode', label: '模板编码', type: 'input', placeholder: '请输入模板编码' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_SEARCH_OPTIONS, placeholder: '请选择状态' }
]
const priorityOptions = PRIORITY_OPTIONS

const loading = ref(true)
const ids = ref<number[]>([])
const multiple = ref(true)
const total = ref(0)
const templateList = ref<NotificationTemplate[]>([])
const dialogTitle = ref('')
const open = ref(false)

const queryParams = reactive<NotificationTemplateQuery>({
  current: 1,
  size: 10,
  templateName: undefined,
  templateCode: undefined,
  type: undefined,
  status: undefined
})

const emptyForm = (): NotificationTemplate => ({
  id: undefined,
  templateCode: undefined,
  templateName: undefined,
  title: undefined,
  content: undefined,
  type: 'system',
  priority: 'normal',
  status: '1',
  remark: undefined
})

const form = ref<NotificationTemplate>(emptyForm())
const formRef = ref<FormInstance>()

const rules: FormRules = {
  templateCode: [{ required: true, message: '模板编码不能为空', trigger: 'blur' }],
  templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
  title: [{ required: true, message: '标题模板不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '内容模板不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '通知类型不能为空', trigger: 'change' }]
}

function getList() {
  loading.value = true
  listTemplate(queryParams).then(res => {
    templateList.value = res.rows
    total.value = res.total
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

function reset() {
  form.value = emptyForm()
  formRef.value?.resetFields()
}

function cancel() {
  open.value = false
  reset()
}

function handleSelectionChange(selection: NotificationTemplate[]) {
  ids.value = selection.map(item => item.id as number)
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  dialogTitle.value = '添加通知模板'
}

function handleUpdate(row: NotificationTemplate) {
  reset()
  const id = row.id ?? ids.value[0]
  getTemplate(id as number).then(res => {
    form.value = res.data
    open.value = true
    dialogTitle.value = '修改通知模板'
  })
}

function submitForm() {
  formRef.value?.validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateTemplate(form.value).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addTemplate(form.value).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row?: NotificationTemplate) {
  const delIds = row?.id ? [row.id as number] : ids.value
  ElMessageBox.confirm('是否确认删除选中的模板?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => delTemplate(delIds))
    .then(() => {
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => { /* 用户取消 */ })
}

function handleStatusChange(row: NotificationTemplate) {
  const text = row.status === '1' ? '启用' : '停用'
  ElMessageBox.confirm('确认要"' + text + '""' + row.templateName + '"模板吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => changeTemplateStatus(row.id as number, row.status as string))
    .then(() => {
      ElMessage.success(text + '成功')
    })
    .catch(() => {
      row.status = row.status === '0' ? '1' : '0'
    })
}

getList()
</script>

<template>
  <div class="app-container">
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <TableToolbar
      show-add
      show-delete
      show-refresh
      :multiple="multiple"
      @add="handleAdd"
      @delete="handleDelete"
      @refresh="getList"
    />

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="消息ID" align="center" prop="messageId" width="80" />
      <el-table-column label="消息名称" align="center" prop="messageName" :show-overflow-tooltip="true" />
      <el-table-column label="消息编码" align="center" prop="messageCode" :show-overflow-tooltip="true" />
      <el-table-column label="消息类型" align="center" prop="messageType" width="100">
        <template #default="scope">
          <DictTag :options="messageTypeOptions" :value="scope.row.messageType" />
        </template>
      </el-table-column>
      <el-table-column label="消息主题" align="center" prop="subject" :show-overflow-tooltip="true" />
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
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
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
            icon="Delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="消息名称" prop="messageName">
          <el-input v-model="form.messageName" placeholder="请输入消息名称" />
        </el-form-item>
        <el-form-item label="消息编码" prop="messageCode">
          <el-input v-model="form.messageCode" placeholder="请输入消息编码" />
        </el-form-item>
        <el-form-item label="消息类型" prop="messageType">
          <el-radio-group v-model="form.messageType">
            <el-radio value="1">邮件</el-radio>
            <el-radio value="2">短信</el-radio>
            <el-radio value="3">站内信</el-radio>
            <el-radio value="4">微信</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="消息主题" prop="subject">
          <el-input v-model="form.subject" placeholder="请输入消息主题" />
        </el-form-item>
        <el-form-item label="消息内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入消息内容模板，支持变量如：${username}" />
        </el-form-item>
        <el-form-item label="变量说明" prop="variables">
          <el-input v-model="form.variables" type="textarea" :rows="3" placeholder='请输入JSON格式的变量说明，如：{"username":"用户名"}' />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listMessage, getMessage, addMessage, updateMessage, delMessage, changeMessageStatus } from '@/api/system/message'
import { useCrudTable } from '@/composables'
import type { Message, MessageQuery } from '@/types/system/message'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'

defineOptions({ name: 'Message' })

const MESSAGE_TYPE_OPTIONS = [
  { value: '1', label: '邮件', type: 'success' },
  { value: '2', label: '短信', type: 'warning' },
  { value: '3', label: '站内信', type: 'info' },
  { value: '4', label: '微信', type: '' }
]
const STATUS_OPTIONS = [
  { value: '0', label: '正常' },
  { value: '1', label: '停用' }
]

const messageTypeOptions = MESSAGE_TYPE_OPTIONS

const searchFields = [
  { prop: 'messageName', label: '消息名称', type: 'input' },
  { prop: 'messageCode', label: '消息编码', type: 'input' },
  { prop: 'messageType', label: '消息类型', type: 'select', options: MESSAGE_TYPE_OPTIONS, placeholder: '请选择消息类型' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '请选择状态' }
]

const { loading, list, total, queryParams, getList, handleQuery, resetQuery } =
  useCrudTable<Message, MessageQuery>({
    listApi: listMessage,
    defaultQuery: { messageName: undefined, messageCode: undefined, messageType: undefined, status: undefined } as Partial<MessageQuery>
  })

const ids = ref<number[]>([])
const multiple = ref(true)
const title = ref('')
const open = ref(false)
const formRef = ref<FormInstance>()

const defaultForm = (): Message => ({
  messageId: undefined,
  messageName: undefined,
  messageCode: undefined,
  messageType: '1',
  subject: undefined,
  content: undefined,
  variables: undefined,
  status: '0',
  remark: undefined
})
const form = reactive<Message>(defaultForm())

const rules: FormRules = {
  messageName: [{ required: true, message: '消息名称不能为空', trigger: 'blur' }],
  messageCode: [{ required: true, message: '消息编码不能为空', trigger: 'blur' }],
  messageType: [{ required: true, message: '消息类型不能为空', trigger: 'change' }],
  content: [{ required: true, message: '消息内容不能为空', trigger: 'blur' }]
}

function reset() {
  Object.assign(form, defaultForm())
  formRef.value?.resetFields()
}

function cancel() {
  open.value = false
  reset()
}

function handleSelectionChange(selection: Message[]) {
  ids.value = selection.map(item => item.messageId as number)
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '添加消息模板'
}

function handleUpdate(row: Message) {
  reset()
  const messageId = row.messageId || ids.value
  getMessage(messageId as number).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = '修改消息模板'
  })
}

function submitForm() {
  formRef.value?.validate(valid => {
    if (valid) {
      if (form.messageId != null) {
        updateMessage(form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addMessage(form).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row?: Message) {
  const messageIds = (row?.messageId != null ? row.messageId : ids.value) as number | number[]
  ElMessageBox.confirm('是否确认删除所选消息模板？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delMessage(messageIds)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => { /* 用户取消 */ })
}

function handleStatusChange(row: Message) {
  const text = row.status === '0' ? '启用' : '停用'
  ElMessageBox.confirm('确认要"' + text + '""' + row.messageName + '"吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return changeMessageStatus({ messageId: row.messageId!, status: row.status! })
  }).then(() => {
    ElMessage.success(text + '成功')
  }).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}
</script>

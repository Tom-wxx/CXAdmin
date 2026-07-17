<template>
  <div class="app-container">
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <TableToolbar show-add show-refresh @add="handleAdd" @refresh="getList" />

    <el-table v-loading="loading" :data="list" border>
      <el-table-column label="公告编号" align="center" prop="noticeId" width="100" />
      <el-table-column label="公告标题" align="center" prop="noticeTitle" show-overflow-tooltip />
      <el-table-column label="公告类型" align="center" width="100">
        <template #default="scope">
          <DictTag :options="noticeTypeOptions" :value="scope.row.noticeType" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="820px" append-to-body :close-on-click-modal="false">
      <el-form ref="noticeFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" maxlength="50" />
        </el-form-item>
        <el-form-item label="公告类型" prop="noticeType">
          <el-radio-group v-model="form.noticeType">
            <el-radio value="1">通知</el-radio>
            <el-radio value="2">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="noticeContent">
          <QuillEditor
            v-model:content="form.noticeContent"
            content-type="html"
            :options="editorOptions"
            class="notice-editor"
          />
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

    <el-dialog title="公告详情" v-model="viewDialogVisible" width="820px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告标题">{{ viewForm.noticeTitle }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <DictTag :options="noticeTypeOptions" :value="viewForm.noticeType" />
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <DictTag :options="statusOptions" :value="viewForm.status" />
        </el-descriptions-item>
        <el-descriptions-item label="创建者">{{ viewForm.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ parseTime(viewForm.createTime || '') }}</el-descriptions-item>
        <el-descriptions-item label="公告内容" :span="2">
          <div class="ql-snow notice-view-wrapper">
            <div class="ql-editor notice-view-content" v-html="viewForm.noticeContent || '无内容'"></div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewForm.remark }}</el-descriptions-item>
      </el-descriptions>
      <template #footer><div class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关 闭</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { listNotice, getNotice, addNotice, updateNotice, delNotice } from '@/api/system/notice'
import { useCrudTable } from '@/composables'
import { parseTime } from '@/utils'
import type { Notice, NoticeQuery } from '@/types/system/notice'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'

defineOptions({ name: 'Notice' })

const NOTICE_TYPE_OPTIONS = [
  { value: '1', label: '通知', type: 'success' },
  { value: '2', label: '公告', type: 'warning' }
]
const STATUS_OPTIONS = [
  { value: '0', label: '正常', type: 'success' },
  { value: '1', label: '关闭', type: 'info' }
]

const noticeTypeOptions = NOTICE_TYPE_OPTIONS
const statusOptions = STATUS_OPTIONS

const searchFields = [
  { prop: 'noticeTitle', label: '公告标题', type: 'input' },
  { prop: 'noticeType', label: '公告类型', type: 'select', options: NOTICE_TYPE_OPTIONS, placeholder: '公告类型' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '公告状态' }
]

const { loading, list, total, queryParams, getList, handleQuery, resetQuery } =
  useCrudTable<Notice, NoticeQuery>({
    listApi: listNotice,
    defaultQuery: { noticeTitle: undefined, noticeType: undefined, status: undefined }
  })

const dialogTitle = ref('')
const dialogVisible = ref(false)
const noticeFormRef = ref<FormInstance>()

const viewDialogVisible = ref(false)
const viewForm = reactive<Notice>({})

const editorOptions = {
  theme: 'snow',
  placeholder: '请输入公告内容',
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      [{ header: 1 }, { header: 2 }],
      [{ list: 'ordered' }, { list: 'bullet' }],
      [{ color: [] }, { background: [] }],
      [{ align: [] }],
      ['blockquote', 'code-block'],
      ['link'],
      ['clean']
    ]
  }
}

const formDefaults: Notice = {
  noticeId: undefined,
  noticeTitle: undefined,
  noticeType: '1',
  noticeContent: undefined,
  status: '0',
  remark: undefined
}

const form = reactive<Notice>({ ...formDefaults })

const rules = reactive<FormRules>({
  noticeTitle: [
    { required: true, message: '公告标题不能为空', trigger: 'blur' },
    { max: 50, message: '公告标题长度不能超过50个字符', trigger: 'blur' }
  ],
  noticeType: [
    { required: true, message: '公告类型不能为空', trigger: 'change' }
  ],
  noticeContent: [
    {
      required: true,
      trigger: 'change',
      // 剥离 HTML 标签后判空，避免 quill 空状态 "<p><br></p>" 通过校验
      validator: (_rule: unknown, value: string, callback: (e?: Error) => void) => {
        const plain = (value || '').replace(/<[^>]*>/g, '').replace(/&nbsp;/g, '').trim()
        if (!plain) {
          callback(new Error('公告内容不能为空'))
        } else {
          callback()
        }
      }
    }
  ]
})

function reset() {
  Object.assign(form, formDefaults)
  noticeFormRef.value?.resetFields()
}

function handleAdd() {
  reset()
  dialogTitle.value = '添加通知公告'
  dialogVisible.value = true
}

function handleUpdate(row: Notice) {
  reset()
  getNotice(row.noticeId!).then(response => {
    Object.assign(form, response.data)
    dialogTitle.value = '修改通知公告'
    dialogVisible.value = true
  })
}

function handleView(row: Notice) {
  getNotice(row.noticeId!).then(response => {
    Object.assign(viewForm, response.data)
    viewDialogVisible.value = true
  })
}

function submitForm() {
  noticeFormRef.value?.validate(valid => {
    if (valid) {
      if (form.noticeId) {
        updateNotice(form).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        })
      } else {
        addNotice(form).then(() => {
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

function handleDelete(row: Notice) {
  ElMessageBox.confirm(
    '是否确认删除公告标题为"' + row.noticeTitle + '"的数据项？',
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    return delNotice(row.noticeId!)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.mb8 {
  margin-bottom: 8px;
}
/* Quill 编辑器内部高度（动态生成的子元素，用 :deep() 穿透 scoped 样式） */
.notice-editor :deep(.ql-container) {
  min-height: 240px;
  font-size: 14px;
}
.notice-editor :deep(.ql-editor) {
  min-height: 240px;
}
/* 查看详情：使用 ql-snow + ql-editor 的样式（颜色/对齐/列表等回显） */
.notice-view-wrapper {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fafafa;
}
.notice-view-content {
  min-height: 80px;
  padding: 12px;
}
</style>

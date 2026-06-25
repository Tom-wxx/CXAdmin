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
    <TableToolbar
      show-add
      show-delete
      show-export
      show-refresh
      :multiple="multiple"
      :export-disabled="exportLoading"
      @add="handleAdd"
      @delete="handleDeleteBatch"
      @export="handleExport"
      @refresh="getList"
    />

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="岗位ID" align="center" prop="postId" width="80" />
      <el-table-column label="岗位编码" align="center" prop="postCode" width="150" />
      <el-table-column label="岗位名称" align="center" prop="postName" />
      <el-table-column label="显示排序" align="center" prop="postSort" width="100" />
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
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

    <!-- 分页组件 -->
    <Pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <!-- 添加或修改岗位对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入岗位编码" maxlength="64" />
        </el-form-item>
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="显示排序" prop="postSort">
          <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { pagePost, getPost, delPost, addPost, updatePost, exportPost } from '@/api/system/post'
import { useCrudTable } from '@/composables'
import { parseTime } from '@/utils'
import type { Post, PostQuery } from '@/types/system/post'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'

defineOptions({ name: 'Post' })

const STATUS_OPTIONS = [
  { value: '0', label: '正常' },
  { value: '1', label: '停用' }
]

const searchFields = [
  { prop: 'postCode', label: '岗位编码', type: 'input' },
  { prop: 'postName', label: '岗位名称', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '岗位状态' }
]

const { loading, list, total, queryParams, getList, handleQuery, resetQuery } =
  useCrudTable<Post, PostQuery>({
    listApi: pagePost,
    defaultQuery: { postCode: undefined, postName: undefined, status: undefined }
  })

// 选择项
const ids = ref<number[]>([])
const single = ref(true)
const multiple = ref(true)

// 对话框
const title = ref('')
const open = ref(false)
const formRef = ref<FormInstance>()
const exportLoading = ref(false)

const formDefaults: Post = {
  postId: undefined,
  postCode: undefined,
  postName: undefined,
  postSort: 0,
  status: '0',
  remark: undefined
}

const form = reactive<Post>({ ...formDefaults })

const rules = reactive<FormRules>({
  postCode: [
    { required: true, message: '岗位编码不能为空', trigger: 'blur' }
  ],
  postName: [
    { required: true, message: '岗位名称不能为空', trigger: 'blur' }
  ],
  postSort: [
    { required: true, message: '岗位顺序不能为空', trigger: 'blur' }
  ]
})

function reset() {
  Object.assign(form, formDefaults)
  formRef.value?.resetFields()
}

function cancel() {
  open.value = false
  reset()
}

function handleSelectionChange(selection: Post[]) {
  ids.value = selection.map(item => item.postId!)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '添加岗位'
}

function handleUpdate(row: Post) {
  reset()
  const postId = row.postId ?? ids.value[0]
  getPost(postId).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = '修改岗位'
  })
}

function submitForm() {
  formRef.value?.validate(valid => {
    if (valid) {
      if (form.postId != null) {
        updatePost(form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addPost(form).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row: Post) {
  const postIds = row.postId ?? ids.value
  ElMessageBox.confirm(
    '是否确认删除岗位编号为"' + postIds + '"的数据项?',
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    return delPost(postIds as number | number[])
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

function handleDeleteBatch() {
  handleDelete({} as Post)
}

function handleStatusChange(row: Post) {
  const text = row.status === '0' ? '启用' : '停用'
  ElMessageBox.confirm(
    '确认要"' + text + '""' + row.postName + '"岗位吗?',
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    return updatePost(row)
  }).then(() => {
    ElMessage.success(text + '成功')
  }).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}

function handleExport() {
  ElMessageBox.confirm('是否确认导出所有岗位数据？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    exportLoading.value = true
    return exportPost(queryParams)
  }).then((response: Blob) => {
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '岗位数据.xlsx'
    link.click()
    URL.revokeObjectURL(link.href)
    exportLoading.value = false
    ElMessage.success('导出成功')
  }).catch(() => {
    exportLoading.value = false
  })
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>

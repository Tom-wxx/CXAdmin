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
      show-export
      show-import
      show-refresh
      @add="handleAdd"
      @export="handleExport"
      @import="handleImport"
      @refresh="getList"
    />

    <!-- 导入对话框 -->
    <el-dialog title="用户导入" v-model="importDialogVisible" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :auto-upload="false"
        :on-change="handleFileChange"
        drag
      >
        <el-icon><Upload /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip><div class="el-upload__tip">
          <el-checkbox v-model="updateSupport" />是否更新已存在的用户数据
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="handleDownloadTemplate">下载模板</el-link>
        </div></template>
      </el-upload>
      <template #footer><div class="dialog-footer">
        <el-button type="primary" @click="submitImport">确 定</el-button>
        <el-button @click="importDialogVisible = false">取 消</el-button>
      </div></template>
    </el-dialog>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="userList" border>
      <el-table-column label="用户ID" align="center" prop="userId" width="80" />
      <el-table-column label="用户名" align="center" prop="username" />
      <el-table-column label="昵称" align="center" prop="nickname" />
      <el-table-column label="部门" align="center" prop="deptName" />
      <el-table-column label="手机号" align="center" prop="phone" width="120" />
      <el-table-column label="邮箱" align="center" prop="email" width="180" />
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
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime || '') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
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
          <el-dropdown size="small" @command="(command: string) => handleCommand(command, scope.row)">
            <span class="el-dropdown-link">
              <el-icon class="el-icon--right"><DArrowRight /></el-icon>更多
            </span>
            <template #dropdown><el-dropdown-menu>
              <el-dropdown-item command="resetPwd" icon="Key">重置密码</el-dropdown-item>
            </el-dropdown-menu></template>
          </el-dropdown>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px" append-to-body>
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select
                v-model="form.deptId"
                :data="deptOptions"
                :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                value-key="deptId"
                node-key="deptId"
                check-strictly
                clearable
                placeholder="选择归属部门"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <DictSelect v-model="form.gender" :options="genderOptions" width="100%" placeholder="请选择性别" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="!form.userId">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入密码" type="password" maxlength="20" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="岗位" prop="postIds">
              <el-select v-model="form.postIds" multiple placeholder="请选择岗位" style="width: 100%">
                <el-option
                  v-for="item in postOptions"
                  :key="item.postId"
                  :label="item.postName"
                  :value="item.postId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="角色" prop="roleIds">
              <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
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
import type { FormInstance, FormRules, UploadInstance, UploadFile } from 'element-plus'
import { listUser, getUser, addUser, updateUser, delUser, resetUserPwd, changeUserStatus, getUserFormOptions, exportUser, downloadTemplate, importUser } from '@/api/system/user'
import { useCrudTable } from '@/composables'
import { parseTime } from '@/utils'
import type { User, UserQuery, DeptOption, PostOption, RoleOption } from '@/types/system/user'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictSelect from '@/components/DictSelect/index.vue'

defineOptions({ name: 'User' })

const STATUS_OPTIONS = [
  { value: '0', label: '正常' },
  { value: '1', label: '停用' }
]
const GENDER_OPTIONS = [
  { value: '0', label: '男' },
  { value: '1', label: '女' },
  { value: '2', label: '未知' }
]

/**
 * 详情/表单实际字段形状：/system/user/{id}、增改接口走的是 UserVO/UserDTO（phone/gender），
 * 与 User(对应 SysUser 实体) 类型声明的 phonenumber/sex 不同，这里在页面内按接口真实形状补充。
 */
interface UserFormData extends User {
  phone?: string
  gender?: string
}

const searchFields = [
  { prop: 'username', label: '用户名', type: 'input' },
  { prop: 'phone', label: '手机号', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '用户状态' }
]
const genderOptions = GENDER_OPTIONS

const { loading, list: userList, total, queryParams, getList, handleQuery, resetQuery } =
  useCrudTable<User, UserQuery>({
    listApi: listUser,
    defaultQuery: { username: undefined, phone: undefined, status: undefined }
  })

// 对话框
const dialogTitle = ref('')
const dialogVisible = ref(false)
const userFormRef = ref<FormInstance>()

// 下拉选项
const deptOptions = ref<DeptOption[]>([])
const postOptions = ref<PostOption[]>([])
const roleOptions = ref<RoleOption[]>([])

// 导入对话框
const importDialogVisible = ref(false)
const updateSupport = ref(false)
const importFile = ref<File | null>(null)
const uploadRef = ref<UploadInstance>()

const formDefaults: UserFormData = {
  userId: undefined,
  deptId: undefined,
  username: undefined,
  nickname: undefined,
  password: undefined,
  phone: undefined,
  email: undefined,
  gender: '2',
  status: '0',
  postIds: [],
  roleIds: [],
  remark: undefined
}

const form = reactive<UserFormData>({ ...formDefaults })

const rules = reactive<FormRules>({
  username: [
    { required: true, message: '用户名不能为空', trigger: 'blur' },
    { min: 2, max: 30, message: '用户名长度在2-30之间', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '昵称不能为空', trigger: 'blur' },
    { max: 30, message: '昵称长度不能超过30', trigger: 'blur' }
  ],
  deptId: [
    { required: true, message: '归属部门不能为空', trigger: 'blur' }
  ],
  phone: [
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20之间', trigger: 'blur' }
  ]
})

/** 获取表单选项数据 */
function getFormOptions() {
  getUserFormOptions().then(response => {
    deptOptions.value = response.data.depts || []
    postOptions.value = response.data.posts || []
    roleOptions.value = response.data.roles || []
  })
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  getFormOptions()
  dialogTitle.value = '添加用户'
  dialogVisible.value = true
}

/** 修改按钮操作 */
function handleUpdate(row: User) {
  reset()
  getFormOptions()
  const userId = row.userId! // 列表行始终带 userId
  getUser(userId).then(response => {
    // 接口类型声明为 Result<User>，但后端实际返回 { user, postIds, roleIds }，按真实形状读取
    const data = response.data as unknown as { user: UserFormData; postIds: number[]; roleIds: number[] }
    Object.assign(form, data.user)
    form.postIds = data.postIds || []
    form.roleIds = data.roleIds || []
    dialogTitle.value = '修改用户'
    dialogVisible.value = true
  })
}

/** 提交按钮 */
function submitForm() {
  userFormRef.value?.validate(valid => {
    if (valid) {
      if (form.userId) {
        updateUser(form).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        })
      } else {
        addUser(form).then(() => {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          getList()
        })
      }
    }
  })
}

/** 取消按钮 */
function cancel() {
  dialogVisible.value = false
  reset()
}

/** 表单重置 */
function reset() {
  Object.assign(form, formDefaults)
  userFormRef.value?.resetFields()
}

/** 用户状态修改 */
function handleStatusChange(row: User) {
  const text = row.status === '0' ? '启用' : '停用'
  ElMessageBox.confirm('确认要"' + text + '""' + row.username + '"用户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return changeUserStatus(row.userId!, row.status!) // 列表行始终带 userId/status
  }).then(() => {
    ElMessage.success(text + '成功')
  }).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}

/** 删除按钮操作 */
function handleDelete(row: User) {
  ElMessageBox.confirm('是否确认删除用户"' + row.username + '"？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delUser(row.userId!)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  })
}

/** 更多操作触发 */
function handleCommand(command: string, row: User) {
  switch (command) {
    case 'resetPwd':
      handleResetPwd(row)
      break
  }
}

/** 重置密码按钮操作 */
function handleResetPwd(row: User) {
  ElMessageBox.prompt('请输入"' + row.username + '"的新密码', '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^.{6,20}$/,
    inputErrorMessage: '密码长度在6-20之间'
  }).then(({ value }) => {
    resetUserPwd(row.userId!, value).then(() => {
      ElMessage.success('修改成功，新密码是：' + value)
    })
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  ElMessageBox.confirm('是否确认导出所有用户数据?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    exportUser(queryParams).then(response => {
      const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = '用户数据.xlsx'
      link.click()
      window.URL.revokeObjectURL(url)
      ElMessage.success('导出成功')
    })
  }).catch(() => {})
}

/** 导入按钮操作 */
function handleImport() {
  importDialogVisible.value = true
  updateSupport.value = false
  importFile.value = null
  uploadRef.value?.clearFiles()
}

/** 文件改变 */
function handleFileChange(file: UploadFile) {
  importFile.value = file.raw ?? null
}

/** 下载模板 */
function handleDownloadTemplate() {
  downloadTemplate().then(response => {
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '用户导入模板.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
  })
}

/** 提交导入 */
function submitImport() {
  if (!importFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }
  const formData = new FormData()
  formData.append('file', importFile.value)
  formData.append('updateSupport', String(updateSupport.value))
  importUser(formData).then(response => {
    importDialogVisible.value = false
    // 接口类型声明为 Result<void>，但后端实际返回 { successCount, failureCount }，按真实形状读取
    const data = response.data as unknown as { successCount?: number; failureCount?: number }
    let message = `成功导入 ${data.successCount ?? 0} 条数据`
    if ((data.failureCount ?? 0) > 0) {
      message += `，失败 ${data.failureCount} 条`
    }
    ElMessage.success(message)
    getList()
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
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  font-size: 12px;
}
</style>

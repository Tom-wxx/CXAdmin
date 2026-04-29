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
    <el-dialog title="用户导入" :visible.sync="importDialogVisible" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :auto-upload="false"
        :on-change="handleFileChange"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          <el-checkbox v-model="updateSupport" />是否更新已存在的用户数据
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="handleDownloadTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitImport">确 定</el-button>
        <el-button @click="importDialogVisible = false">取 消</el-button>
      </div>
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
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)">
            <span class="el-dropdown-link">
              <i class="el-icon-d-arrow-right el-icon--right"></i>更多
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="resetPwd" icon="el-icon-key">重置密码</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.current"
      :limit.sync="queryParams.size"
      @pagination="getList"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px" append-to-body>
      <el-form ref="userForm" :model="form" :rules="rules" label-width="80px">
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
              <treeselect
                v-model="form.deptId"
                :options="deptOptions"
                :normalizer="deptNormalizer"
                :show-count="true"
                placeholder="选择归属部门"
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
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
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
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUser, getUser, addUser, updateUser, delUser, resetUserPwd, changeUserStatus, getUserFormOptions, exportUser, downloadTemplate, importUser } from '@/api/system/user'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import Pagination from '@/components/Pagination'
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'
import DictSelect from '@/components/DictSelect'

const STATUS_OPTIONS = [
  { value: '0', label: '正常' },
  { value: '1', label: '停用' }
]
const GENDER_OPTIONS = [
  { value: '0', label: '男' },
  { value: '1', label: '女' },
  { value: '2', label: '未知' }
]

export default {
  name: 'User',
  components: {
    Treeselect,
    Pagination,
    SearchForm,
    TableToolbar,
    DictSelect
  },
  data() {
    return {
      // 加载状态
      loading: true,
      // 用户列表
      userList: [],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        current: 1,
        size: 10,
        username: undefined,
        phone: undefined,
        status: undefined
      },
      // 搜索表单字段配置
      searchFields: [
        { prop: 'username', label: '用户名', type: 'input' },
        { prop: 'phone', label: '手机号', type: 'input' },
        { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '用户状态' }
      ],
      // 性别选项
      genderOptions: GENDER_OPTIONS,
      // 对话框标题
      dialogTitle: '',
      // 对话框显示状态
      dialogVisible: false,
      // 表单数据
      form: {},
      // 部门选项
      deptOptions: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      // 导入对话框
      importDialogVisible: false,
      // 是否更新已存在数据
      updateSupport: false,
      // 导入文件
      importFile: null,
      // 表单校验规则
      rules: {
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
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true
      listUser(this.queryParams).then(response => {
        this.userList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /** 获取表单选项数据 */
    getFormOptions() {
      getUserFormOptions().then(response => {
        this.deptOptions = response.data.depts || []
        this.postOptions = response.data.posts || []
        this.roleOptions = response.data.roles || []
      })
    },
    /** 部门树形选择器规范化 */
    deptNormalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.deptId,
        label: node.deptName,
        children: node.children
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        current: 1,
        size: 10,
        username: undefined,
        phone: undefined,
        status: undefined
      }
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.getFormOptions()
      this.dialogTitle = '添加用户'
      this.dialogVisible = true
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.getFormOptions()
      const userId = row.userId
      getUser(userId).then(response => {
        this.form = response.data.user
        this.form.postIds = response.data.postIds || []
        this.form.roleIds = response.data.roleIds || []
        this.dialogTitle = '修改用户'
        this.dialogVisible = true
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          if (this.form.userId) {
            updateUser(this.form).then(response => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.getList()
            })
          } else {
            addUser(this.form).then(response => {
              this.$message.success('新增成功')
              this.dialogVisible = false
              this.getList()
            })
          }
        }
      })
    },
    /** 取消按钮 */
    cancel() {
      this.dialogVisible = false
      this.reset()
    },
    /** 表单重置 */
    reset() {
      this.form = {
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
      if (this.$refs.userForm) {
        this.$refs.userForm.resetFields()
      }
    },
    /** 用户状态修改 */
    handleStatusChange(row) {
      let text = row.status === '0' ? '启用' : '停用'
      this.$confirm('确认要"' + text + '""' + row.username + '"用户吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return changeUserStatus(row.userId, row.status)
      }).then(() => {
        this.$message.success(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除用户"' + row.username + '"？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delUser(row.userId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    /** 更多操作触发 */
    handleCommand(command, row) {
      switch (command) {
        case 'resetPwd':
          this.handleResetPwd(row)
          break
      }
    },
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      this.$prompt('请输入"' + row.username + '"的新密码', '重置密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^.{6,20}$/,
        inputErrorMessage: '密码长度在6-20之间'
      }).then(({ value }) => {
        resetUserPwd(row.userId, value).then(() => {
          this.$message.success('修改成功，新密码是：' + value)
        })
      }).catch(() => {})
    },
    /** 时间格式化 */
    parseTime(time) {
      if (!time) {
        return ''
      }
      const date = new Date(time)
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      const second = date.getSeconds().toString().padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$confirm('是否确认导出所有用户数据?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        exportUser(this.queryParams).then(response => {
          const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = '用户数据.xlsx'
          link.click()
          window.URL.revokeObjectURL(url)
          this.$message.success('导出成功')
        })
      }).catch(() => {})
    },
    /** 导入按钮操作 */
    handleImport() {
      this.importDialogVisible = true
      this.updateSupport = false
      this.importFile = null
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles()
      }
    },
    /** 文件改变 */
    handleFileChange(file) {
      this.importFile = file.raw
    },
    /** 下载模板 */
    handleDownloadTemplate() {
      downloadTemplate().then(response => {
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '用户导入模板.xlsx'
        link.click()
        window.URL.revokeObjectURL(url)
      })
    },
    /** 提交导入 */
    submitImport() {
      if (!this.importFile) {
        this.$message.warning('请选择要导入的文件')
        return
      }
      const formData = new FormData()
      formData.append('file', this.importFile)
      formData.append('updateSupport', this.updateSupport)
      importUser(formData).then(response => {
        this.importDialogVisible = false
        const data = response.data
        let message = `成功导入 ${data.successCount} 条数据`
        if (data.failureCount > 0) {
          message += `，失败 ${data.failureCount} 条`
        }
        this.$message.success(message)
        this.getList()
      })
    }
  }
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

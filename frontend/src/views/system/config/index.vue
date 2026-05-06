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
    <TableToolbar show-add show-refresh @add="handleAdd" @refresh="getList" />

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="configList" border>
      <el-table-column label="参数主键" align="center" prop="configId" width="100" />
      <el-table-column label="参数名称" align="center" prop="configName" show-overflow-tooltip />
      <el-table-column label="参数键名" align="center" prop="configKey" show-overflow-tooltip />
      <el-table-column label="参数键值" align="center" prop="configValue" show-overflow-tooltip />
      <el-table-column label="系统内置" align="center" width="100">
        <template slot-scope="scope">
          <DictTag :options="configTypeOptions" :value="scope.row.configType" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
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
            :disabled="scope.row.configType === 'Y'"
          >删除</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" append-to-body>
      <el-form ref="configForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入参数名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入参数键名" maxlength="100" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
          <el-radio-group v-model="form.configType">
            <el-radio label="Y">是</el-radio>
            <el-radio label="N">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listConfig, getConfig, addConfig, updateConfig, delConfig } from '@/api/system/config'
import Pagination from '@/components/Pagination'
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'
import DictTag from '@/components/DictTag'

const CONFIG_TYPE_OPTIONS = [
  { value: 'Y', label: '是', type: 'success' },
  { value: 'N', label: '否', type: 'info' }
]

export default {
  name: 'Config',
  components: {
    Pagination,
    SearchForm,
    TableToolbar,
    DictTag
  },
  data() {
    return {
      // 搜索字段配置
      searchFields: [
        { prop: 'configName', label: '参数名称', type: 'input' },
        { prop: 'configKey', label: '参数键名', type: 'input' },
        { prop: 'configType', label: '系统内置', type: 'select', options: CONFIG_TYPE_OPTIONS, placeholder: '系统内置' }
      ],
      configTypeOptions: CONFIG_TYPE_OPTIONS,
      // 加载状态
      loading: true,
      // 参数配置列表
      configList: [],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        current: 1,
        size: 10,
        configName: undefined,
        configKey: undefined,
        configType: undefined
      },
      // 对话框标题
      dialogTitle: '',
      // 对话框显示状态
      dialogVisible: false,
      // 表单数据
      form: {},
      // 表单校验规则
      rules: {
        configName: [
          { required: true, message: '参数名称不能为空', trigger: 'blur' },
          { max: 100, message: '参数名称长度不能超过100个字符', trigger: 'blur' }
        ],
        configKey: [
          { required: true, message: '参数键名不能为空', trigger: 'blur' },
          { max: 100, message: '参数键名长度不能超过100个字符', trigger: 'blur' }
        ],
        configValue: [
          { required: true, message: '参数键值不能为空', trigger: 'blur' },
          { max: 500, message: '参数键值长度不能超过500个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询参数配置列表 */
    getList() {
      this.loading = true
      listConfig(this.queryParams).then(response => {
        this.configList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    /** 重置按钮操作（SearchForm 已自动清字段） */
    resetQuery() {
      this.queryParams.current = 1
      this.queryParams.size = 10
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.dialogTitle = '添加参数配置'
      this.dialogVisible = true
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const configId = row.configId
      getConfig(configId).then(response => {
        this.form = response.data
        this.dialogTitle = '修改参数配置'
        this.dialogVisible = true
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs.configForm.validate(valid => {
        if (valid) {
          if (this.form.configId) {
            updateConfig(this.form).then(response => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.getList()
            })
          } else {
            addConfig(this.form).then(response => {
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
        configId: undefined,
        configName: undefined,
        configKey: undefined,
        configValue: undefined,
        configType: 'N',
        remark: undefined
      }
      if (this.$refs.configForm) {
        this.$refs.configForm.resetFields()
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      if (row.configType === 'Y') {
        this.$message.warning('系统内置参数不能删除')
        return
      }
      this.$confirm('是否确认删除参数名称为"' + row.configName + '"的数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delConfig(row.configId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
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
</style>

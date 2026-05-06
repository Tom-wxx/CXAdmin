<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <!-- 操作按钮 -->
    <TableToolbar show-add show-refresh @add="handleAdd" @refresh="getList" />

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="definitionList" border>
      <el-table-column label="流程KEY" prop="processKey" width="150" />
      <el-table-column label="流程名称" prop="processName" />
      <el-table-column label="流程类型" align="center" width="100">
        <template slot-scope="scope">
          <DictTag :options="processTypeOptions" :value="scope.row.processType" />
        </template>
      </el-table-column>
      <el-table-column label="审批层级" prop="approvalLevel" align="center" width="100" />
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="280" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['workflow:definition:edit']"
          >修改</el-button>
          <el-button
            v-if="scope.row.status === 'draft'"
            size="mini"
            type="text"
            @click="handlePublish(scope.row)"
            v-hasPermi="['workflow:definition:publish']"
          >发布</el-button>
          <el-button
            v-if="scope.row.status === 'published'"
            size="mini"
            type="text"
            @click="handleDisable(scope.row)"
            v-hasPermi="['workflow:definition:edit']"
          >停用</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleDelete(scope.row)"
            v-hasPermi="['workflow:definition:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="流程KEY" prop="processKey">
          <el-input v-model="form.processKey" placeholder="请输入流程KEY" />
        </el-form-item>
        <el-form-item label="流程名称" prop="processName">
          <el-input v-model="form.processName" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程类型" prop="processType">
          <el-select v-model="form.processType" placeholder="请选择">
            <el-option label="请假" value="leave" />
            <el-option label="报销" value="expense" />
            <el-option label="采购" value="purchase" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批人类型" prop="approverType">
          <el-select v-model="form.approverType" placeholder="请选择">
            <el-option label="指定人员" value="specified" />
            <el-option label="指定角色" value="role" />
            <el-option label="部门领导" value="dept_leader" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批层级" prop="approvalLevel">
          <el-input-number v-model="form.approvalLevel" :min="1" :max="5" />
        </el-form-item>
        <el-form-item label="审批人角色" prop="approverRoles" v-if="form.approverType === 'role'">
          <el-input v-model="form.approverRoles" placeholder="请输入角色ID，多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="审批人ID" prop="approverUsers" v-if="form.approverType === 'specified'">
          <el-input v-model="form.approverUsers" placeholder="请输入用户ID，多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="表单配置" prop="formConfig">
          <el-input v-model="form.formConfig" type="textarea" :rows="4" placeholder="请输入JSON格式的表单配置" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDefinition,
  getDefinition,
  addDefinition,
  updateDefinition,
  delDefinition,
  publishDefinition,
  disableDefinition
} from '@/api/workflow/definition'
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'
import DictTag from '@/components/DictTag'

const PROCESS_TYPE_OPTIONS = [
  { value: 'leave', label: '请假', type: '' },
  { value: 'expense', label: '报销', type: 'success' },
  { value: 'purchase', label: '采购', type: 'warning' }
]
const STATUS_OPTIONS = [
  { value: 'draft', label: '草稿', type: 'info' },
  { value: 'published', label: '已发布', type: 'success' },
  { value: 'disabled', label: '已停用', type: 'danger' }
]

export default {
  name: 'ProcessDefinition',
  components: { SearchForm, TableToolbar, DictTag },
  data() {
    return {
      searchFields: [
        { prop: 'processName', label: '流程名称', type: 'input' },
        { prop: 'processType', label: '流程类型', type: 'select', options: PROCESS_TYPE_OPTIONS, placeholder: '请选择' },
        { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '请选择' }
      ],
      processTypeOptions: PROCESS_TYPE_OPTIONS,
      statusOptions: STATUS_OPTIONS,
      loading: false,
      definitionList: [],
      title: '',
      open: false,
      queryParams: {
        processName: null,
        processType: null,
        status: null
      },
      form: {},
      rules: {
        processKey: [{ required: true, message: '流程KEY不能为空', trigger: 'blur' }],
        processName: [{ required: true, message: '流程名称不能为空', trigger: 'blur' }],
        processType: [{ required: true, message: '流程类型不能为空', trigger: 'change' }],
        approverType: [{ required: true, message: '审批人类型不能为空', trigger: 'change' }],
        approvalLevel: [{ required: true, message: '审批层级不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listDefinition(this.queryParams).then(response => {
        this.definitionList = response.data
        this.loading = false
      })
    },
    handleQuery() {
      this.getList()
    },
    resetQuery() {
      this.handleQuery()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加流程定义'
    },
    handleUpdate(row) {
      this.reset()
      getDefinition(row.id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改流程定义'
      })
    },
    handleDelete(row) {
      this.$confirm('是否确认删除流程定义"' + row.processName + '"?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delDefinition(row.id)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    handlePublish(row) {
      this.$confirm('是否确认发布流程定义"' + row.processName + '"?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return publishDefinition(row.id)
      }).then(() => {
        this.getList()
        this.$message.success('发布成功')
      })
    },
    handleDisable(row) {
      this.$confirm('是否确认停用流程定义"' + row.processName + '"?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return disableDefinition(row.id)
      }).then(() => {
        this.getList()
        this.$message.success('已停用')
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null,
        processKey: null,
        processName: null,
        processType: null,
        approverType: null,
        approvalLevel: 1,
        approverRoles: null,
        approverUsers: null,
        formConfig: null,
        remark: null
      }
      this.resetForm('form')
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDefinition(this.form).then(() => {
              this.$message.success('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addDefinition(this.form).then(() => {
              this.$message.success('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    }
  }
}
</script>

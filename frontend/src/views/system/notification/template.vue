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
        <template slot-scope="scope">
          <span v-if="scope.row.type === 'system'">系统通知</span>
          <span v-else-if="scope.row.type === 'todo'">待办提醒</span>
          <span v-else-if="scope.row.type === 'approval'">审批消息</span>
          <span v-else-if="scope.row.type === 'announce'">公告</span>
          <span v-else>{{ scope.row.type }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优先级" align="center" prop="priority" width="100">
        <template slot-scope="scope">
          <DictTag :options="priorityOptions" :value="scope.row.priority || 'normal'" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="1"
            inactive-value="0"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:notification:template:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:notification:template:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
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
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listTemplate,
  getTemplate,
  addTemplate,
  updateTemplate,
  delTemplate,
  changeTemplateStatus
} from '@/api/system/notificationTemplate'
import Pagination from '@/components/Pagination'
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'
import DictTag from '@/components/DictTag'

const PRIORITY_OPTIONS = [
  { value: 'urgent', label: '紧急', type: 'danger' },
  { value: 'important', label: '重要', type: 'warning' },
  { value: 'normal', label: '普通', type: 'info' }
]
const STATUS_SEARCH_OPTIONS = [
  { value: '1', label: '启用' },
  { value: '0', label: '停用' }
]

export default {
  name: 'NotificationTemplate',
  components: { Pagination, SearchForm, TableToolbar, DictTag },
  data() {
    return {
      searchFields: [
        { prop: 'templateName', label: '模板名称', type: 'input', placeholder: '请输入模板名称' },
        { prop: 'templateCode', label: '模板编码', type: 'input', placeholder: '请输入模板编码' },
        { prop: 'status', label: '状态', type: 'select', options: STATUS_SEARCH_OPTIONS, placeholder: '请选择状态' }
      ],
      priorityOptions: PRIORITY_OPTIONS,
      loading: true,
      ids: [],
      multiple: true,
      total: 0,
      templateList: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateName: undefined,
        templateCode: undefined,
        type: undefined,
        status: undefined
      },
      form: {},
      rules: {
        templateCode: [{ required: true, message: '模板编码不能为空', trigger: 'blur' }],
        templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
        title: [{ required: true, message: '标题模板不能为空', trigger: 'blur' }],
        content: [{ required: true, message: '内容模板不能为空', trigger: 'blur' }],
        type: [{ required: true, message: '通知类型不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listTemplate(this.queryParams).then(response => {
        this.templateList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: undefined,
        templateCode: undefined,
        templateName: undefined,
        title: undefined,
        content: undefined,
        type: 'system',
        priority: 'normal',
        status: '1',
        remark: undefined
      }
      this.$refs['form'] && this.$refs['form'].resetFields()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加通知模板'
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getTemplate(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改通知模板'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateTemplate(this.form).then(() => {
              this.$message.success('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addTemplate(this.form).then(() => {
              this.$message.success('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids
      this.$confirm('是否确认删除选中的模板?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delTemplate(ids)
      }).then(() => {
        this.$message.success('删除成功')
        this.getList()
      })
    },
    handleStatusChange(row) {
      let text = row.status === '1' ? '启用' : '停用'
      this.$confirm('确认要"' + text + '""' + row.templateName + '"模板吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return changeTemplateStatus(row.id, row.status)
      }).then(() => {
        this.$message.success(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    parseTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-')
    }
  }
}
</script>

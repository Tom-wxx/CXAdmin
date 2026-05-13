<template>
  <div class="app-container">
    <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />
    <TableToolbar show-add show-refresh @add="handleAdd" @refresh="getList" />

    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column label="ID" prop="id" width="60" align="center" />
      <el-table-column label="标识" prop="code" width="120" />
      <el-table-column label="名称" prop="name" width="140" />
      <el-table-column label="类型" prop="type" width="140" />
      <el-table-column label="client_id" prop="clientId" show-overflow-tooltip />
      <el-table-column label="启用" width="80" align="center">
        <template slot-scope="s">
          <el-tag :type="s.row.enabled ? 'success' : 'info'">{{ s.row.enabled ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="orderNum" width="70" align="center" />
      <el-table-column label="操作" width="180" align="center">
        <template slot-scope="s">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(s.row)">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total"
      :page.sync="queryParams.current" :limit.sync="queryParams.size"
      @pagination="getList" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="820px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="标识" prop="code"><el-input v-model="form.code" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="通用 OAuth2 / OIDC" value="OAUTH2_GENERIC" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="例如 el-icon-platform-eleme" /></el-form-item>
        <el-form-item label="client_id" prop="clientId"><el-input v-model="form.clientId" /></el-form-item>
        <el-form-item :label="form.id ? 'client_secret（留空不变更）' : 'client_secret'">
          <el-input v-model="form.clientSecret" type="password" show-password />
        </el-form-item>
        <el-form-item label="授权端点" prop="authorizationUri"><el-input v-model="form.authorizationUri" /></el-form-item>
        <el-form-item label="Token 端点" prop="tokenUri"><el-input v-model="form.tokenUri" /></el-form-item>
        <el-form-item label="UserInfo 端点" prop="userinfoUri"><el-input v-model="form.userinfoUri" /></el-form-item>
        <el-form-item label="Scope"><el-input v-model="form.scope" placeholder="空格分隔，如 read:user user:email" /></el-form-item>
        <el-form-item label="字段映射 JSON">
          <el-input v-model="form.userFieldMapping" type="textarea" :rows="3"
            placeholder='{"id":"id","username":"login","nickname":"name","email":"email","avatar":"avatar_url"}' />
        </el-form-item>
        <el-form-item label="默认角色 ID"><el-input-number v-model="form.defaultRoleId" :min="1" /></el-form-item>
        <el-form-item label="默认部门 ID"><el-input-number v-model="form.defaultDeptId" :min="1" /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.orderNum" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProviders, getProvider, addProvider, updateProvider, delProvider } from '@/api/system/sso'
import Pagination from '@/components/Pagination'
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'

export default {
  name: 'Sso',
  components: { Pagination, SearchForm, TableToolbar },
  data() {
    return {
      searchFields: [
        { prop: 'code', label: '标识', type: 'input' },
        { prop: 'name', label: '名称', type: 'input' }
      ],
      loading: true, dataList: [], total: 0,
      queryParams: { current: 1, size: 10 },
      dialogTitle: '', dialogVisible: false,
      form: this.emptyForm(),
      rules: {
        code: [{ required: true, message: '标识不能为空', trigger: 'blur' }],
        name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
        type: [{ required: true, message: '类型不能为空', trigger: 'change' }],
        clientId: [{ required: true, message: 'client_id 不能为空', trigger: 'blur' }],
        authorizationUri: [{ required: true, message: '授权端点不能为空', trigger: 'blur' }],
        tokenUri: [{ required: true, message: 'Token 端点不能为空', trigger: 'blur' }],
        userinfoUri: [{ required: true, message: 'UserInfo 端点不能为空', trigger: 'blur' }]
      }
    }
  },
  created() { this.getList() },
  methods: {
    emptyForm() {
      return { id: null, code: '', name: '', type: 'OAUTH2_GENERIC', icon: '',
               clientId: '', clientSecret: '',
               authorizationUri: '', tokenUri: '', userinfoUri: '',
               scope: '', userFieldMapping: '',
               defaultRoleId: null, defaultDeptId: null,
               enabled: 1, orderNum: 0, remark: '' }
    },
    getList() {
      this.loading = true
      listProviders(this.queryParams).then(res => {
        this.dataList = res.rows; this.total = res.total
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.current = 1; this.getList() },
    resetQuery() { this.queryParams = { current: 1, size: 10 }; this.handleQuery() },
    handleAdd() {
      this.form = this.emptyForm()
      this.dialogTitle = '新增身份认证源'
      this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      getProvider(row.id).then(res => {
        this.form = { ...this.emptyForm(), ...res.data, clientSecret: '' }
        this.dialogTitle = '修改身份认证源'
        this.dialogVisible = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const op = this.form.id ? updateProvider : addProvider
        op(this.form).then(() => {
          this.$message.success(this.form.id ? '修改成功' : '新增成功')
          this.dialogVisible = false; this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$confirm(`确认删除 "${row.name}"？删除后历史绑定不会被清除。`, '警告', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => delProvider(row.id))
        .then(() => { this.getList(); this.$message.success('删除成功') })
    }
  }
}
</script>

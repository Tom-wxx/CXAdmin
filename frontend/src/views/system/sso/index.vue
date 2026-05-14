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
        <el-form-item label="client_secret">
          <div v-if="form.id" style="display:flex; gap:8px">
            <el-input
              v-model="form.clientSecret"
              type="password"
              show-password
              :disabled="!editingSecret"
              :placeholder="editingSecret ? '输入新的 client_secret，保存后覆盖' : '已存在，点击右侧『修改』可重置'"
              style="flex:1"
            />
            <el-button v-if="!editingSecret" @click="editingSecret = true">修改</el-button>
            <el-button v-else @click="editingSecret = false; form.clientSecret = ''">取消</el-button>
          </div>
          <el-input v-else v-model="form.clientSecret" type="password" show-password />
        </el-form-item>
        <el-form-item label="授权端点" prop="authorizationUri"><el-input v-model="form.authorizationUri" /></el-form-item>
        <el-form-item label="Token 端点" prop="tokenUri"><el-input v-model="form.tokenUri" /></el-form-item>
        <el-form-item label="UserInfo 端点" prop="userinfoUri"><el-input v-model="form.userinfoUri" /></el-form-item>
        <el-form-item label="邮箱兜底端点">
          <el-input
            v-model="form.emailsUri"
            placeholder="可选：userinfo 不返回 email 时调用，GitHub 填 https://api.github.com/user/emails"
          />
        </el-form-item>
        <el-form-item label="Scope">
          <el-select
            v-model="scopeList"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入 scope，回车添加自定义"
            style="width:100%"
            @change="onScopeChange"
          >
            <el-option-group
              v-for="grp in scopePresets"
              :key="grp.label"
              :label="grp.label"
            >
              <el-option
                v-for="opt in grp.options"
                :key="opt"
                :label="opt"
                :value="opt"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="字段映射">
          <el-table :data="mappingRows" size="mini" border style="width:100%">
            <el-table-column label="标准字段" width="180">
              <template slot-scope="s">
                <el-tag size="mini" :type="s.row.required ? 'danger' : ''">{{ s.row.key }}</el-tag>
                <span class="mapping-desc">{{ s.row.desc }}</span>
              </template>
            </el-table-column>
            <el-table-column label="IdP 响应中的字段名">
              <template slot-scope="s">
                <el-input v-model="s.row.value" :placeholder="s.row.placeholder" size="mini" clearable />
              </template>
            </el-table-column>
          </el-table>
          <div class="mapping-hint">
            红色 <el-tag size="mini" type="danger">id</el-tag> 为必填项；其它字段留空则跳过该属性同步。
          </div>
        </el-form-item>
        <el-form-item label="默认角色">
          <el-select v-model="form.defaultRoleId" placeholder="新用户首次登录的默认角色" clearable style="width:100%">
            <el-option
              v-for="r in roleOptions"
              :key="r.roleId"
              :label="r.roleName"
              :value="r.roleId"
            >
              <span style="float:left">{{ r.roleName }}</span>
              <span style="float:right; color:#8492a6; font-size:12px">{{ r.roleKey }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="默认部门">
          <treeselect
            v-model="form.defaultDeptId"
            :options="deptOptions"
            :normalizer="deptNormalizer"
            :show-count="true"
            placeholder="新用户首次登录的默认部门"
          />
        </el-form-item>
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
import { listAllRole } from '@/api/system/role'
import { treeSelect } from '@/api/system/dept'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import Pagination from '@/components/Pagination'
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'

const MAPPING_KEYS = [
  { key: 'id',       desc: 'IdP 唯一用户 ID', placeholder: 'GitHub: id / Google: sub', required: true },
  { key: 'username', desc: '账号名',           placeholder: 'GitHub: login / Google: email', required: false },
  { key: 'nickname', desc: '昵称',             placeholder: 'GitHub: name / Google: name',  required: false },
  { key: 'email',    desc: '邮箱',             placeholder: 'email',                          required: false },
  { key: 'avatar',   desc: '头像 URL',         placeholder: 'GitHub: avatar_url / Google: picture', required: false }
]

export default {
  name: 'Sso',
  components: { Treeselect, Pagination, SearchForm, TableToolbar },
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
      editingSecret: false,
      scopeList: [],
      mappingRows: this.emptyMappingRows(),
      roleOptions: [],
      deptOptions: [],
      scopePresets: [
        {
          label: 'OIDC 通用',
          options: ['openid', 'profile', 'email']
        },
        {
          label: 'GitHub',
          options: ['read:user', 'user:email', 'repo', 'read:org']
        },
        {
          label: 'Google',
          options: [
            'https://www.googleapis.com/auth/userinfo.email',
            'https://www.googleapis.com/auth/userinfo.profile'
          ]
        }
      ],
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
  created() {
    this.getList()
    this.loadRoleOptions()
    this.loadDeptOptions()
  },
  methods: {
    emptyForm() {
      return { id: null, code: '', name: '', type: 'OAUTH2_GENERIC', icon: '',
               clientId: '', clientSecret: '',
               authorizationUri: '', tokenUri: '', userinfoUri: '', emailsUri: '',
               scope: '', userFieldMapping: '',
               defaultRoleId: null, defaultDeptId: null,
               enabled: 1, orderNum: 0, remark: '' }
    },
    emptyMappingRows() {
      return MAPPING_KEYS.map(k => ({ ...k, value: '' }))
    },
    loadRoleOptions() {
      listAllRole().then(res => { this.roleOptions = (res.data || []).filter(r => r.status === '0' || r.status === 0) })
    },
    loadDeptOptions() {
      treeSelect().then(res => { this.deptOptions = res.data || [] })
    },
    deptNormalizer(node) {
      if (node.children && !node.children.length) delete node.children
      return { id: node.deptId, label: node.deptName, children: node.children }
    },
    onScopeChange(val) {
      this.form.scope = (val || []).join(' ')
    },
    /** form.scope 字符串 → scopeList 数组 */
    syncScopeFromForm() {
      this.scopeList = (this.form.scope || '').split(/\s+/).filter(Boolean)
    },
    /** form.userFieldMapping JSON → mappingRows */
    syncMappingFromForm() {
      const rows = this.emptyMappingRows()
      if (this.form.userFieldMapping) {
        try {
          const obj = JSON.parse(this.form.userFieldMapping)
          rows.forEach(r => { if (obj[r.key] != null) r.value = String(obj[r.key]) })
        } catch (e) {
          this.$message.warning('原字段映射 JSON 解析失败，已重置为空')
        }
      }
      this.mappingRows = rows
    },
    /** mappingRows → form.userFieldMapping JSON */
    buildMappingJson() {
      const obj = {}
      this.mappingRows.forEach(r => {
        const v = (r.value || '').trim()
        if (v) obj[r.key] = v
      })
      return Object.keys(obj).length ? JSON.stringify(obj) : ''
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
      this.editingSecret = true
      this.scopeList = []
      this.mappingRows = this.emptyMappingRows()
      this.dialogTitle = '新增身份认证源'
      this.dialogVisible = true
      this.$nextTick(() => { if (this.$refs.form) this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      getProvider(row.id).then(res => {
        this.form = { ...this.emptyForm(), ...res.data, clientSecret: '' }
        this.editingSecret = false
        this.syncScopeFromForm()
        this.syncMappingFromForm()
        this.dialogTitle = '修改身份认证源'
        this.dialogVisible = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const idRow = this.mappingRows.find(r => r.key === 'id')
        if (!idRow.value || !idRow.value.trim()) {
          this.$message.error('字段映射中的 id 为必填项')
          return
        }
        this.form.userFieldMapping = this.buildMappingJson()
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

<style scoped>
.mapping-desc { margin-left: 8px; color: #606266; font-size: 12px; }
.mapping-hint { margin-top: 6px; color: #909399; font-size: 12px; }
</style>

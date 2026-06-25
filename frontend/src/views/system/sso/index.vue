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
        <template #default="s">
          <el-tag :type="s.row.enabled ? 'success' : 'info'">{{ s.row.enabled ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="orderNum" width="70" align="center" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="s">
          <el-button size="small" type="text" icon="Edit" @click="handleEdit(s.row)">修改</el-button>
          <el-button size="small" type="text" icon="Delete" @click="handleDelete(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total"
      v-model:page="queryParams.current" v-model:limit="queryParams.size"
      @pagination="getList" />

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="820px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="标识" prop="code"><el-input v-model="form.code" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="通用 OAuth2 / OIDC" value="OAUTH2_GENERIC" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="Element Plus 图标名，例如 Platform / Link" /></el-form-item>
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
        <el-form-item label="PKCE (S256)">
          <el-switch v-model="form.enablePkce" :active-value="1" :inactive-value="0" />
          <span class="pkce-hint">
            授权码增强保护（RFC 7636）。Google / Microsoft 支持；GitHub OAuth Apps 不支持，请保持关闭。
          </span>
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
          <el-table :data="mappingRows" size="small" border style="width:100%">
            <el-table-column label="标准字段" width="180">
              <template #default="s">
                <el-tag size="small" :type="s.row.required ? 'danger' : ''">{{ s.row.key }}</el-tag>
                <span class="mapping-desc">{{ s.row.desc }}</span>
              </template>
            </el-table-column>
            <el-table-column label="IdP 响应中的字段名">
              <template #default="s">
                <el-input v-model="s.row.value" :placeholder="s.row.placeholder" size="small" clearable />
              </template>
            </el-table-column>
          </el-table>
          <div class="mapping-hint">
            红色 <el-tag size="small" type="danger">id</el-tag> 为必填项；其它字段留空则跳过该属性同步。
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
          <el-tree-select
            v-model="form.defaultDeptId"
            :data="deptOptions"
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
            value-key="deptId"
            node-key="deptId"
            check-strictly
            clearable
            placeholder="新用户首次登录的默认部门"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.orderNum" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><div>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listProviders, getProvider, addProvider, updateProvider, delProvider } from '@/api/system/sso'
import type { SsoProvider, SsoProviderQuery } from '@/types/system/sso'
import { listAllRole } from '@/api/system/role'
import type { Role } from '@/types/system/role'
import { treeSelect } from '@/api/system/dept'
import type { TreeOption } from '@/types/api'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'

defineOptions({ name: 'Sso' })

interface MappingRow {
  key: string
  desc: string
  placeholder: string
  required: boolean
  value: string
}

const MAPPING_KEYS: Omit<MappingRow, 'value'>[] = [
  { key: 'id',       desc: 'IdP 唯一用户 ID', placeholder: 'GitHub: id / Google: sub', required: true },
  { key: 'username', desc: '账号名',           placeholder: 'GitHub: login / Google: email', required: false },
  { key: 'nickname', desc: '昵称',             placeholder: 'GitHub: name / Google: name',  required: false },
  { key: 'email',    desc: '邮箱',             placeholder: 'email',                          required: false },
  { key: 'avatar',   desc: '头像 URL',         placeholder: 'GitHub: avatar_url / Google: picture', required: false }
]

const scopePresets = [
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
]

const rules: FormRules = {
  code: [{ required: true, message: '标识不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '类型不能为空', trigger: 'change' }],
  clientId: [{ required: true, message: 'client_id 不能为空', trigger: 'blur' }],
  authorizationUri: [{ required: true, message: '授权端点不能为空', trigger: 'blur' }],
  tokenUri: [{ required: true, message: 'Token 端点不能为空', trigger: 'blur' }],
  userinfoUri: [{ required: true, message: 'UserInfo 端点不能为空', trigger: 'blur' }]
}

const searchFields = [
  { prop: 'code', label: '标识', type: 'input' },
  { prop: 'name', label: '名称', type: 'input' }
]

const loading = ref(true)
const dataList = ref<SsoProvider[]>([])
const total = ref(0)
const queryParams = reactive<SsoProviderQuery>({ current: 1, size: 10 })
const dialogTitle = ref('')
const dialogVisible = ref(false)
const editingSecret = ref(false)
const scopeList = ref<string[]>([])
const mappingRows = ref<MappingRow[]>([])
const roleOptions = ref<Role[]>([])
const deptOptions = ref<TreeOption[]>([])
const formRef = ref<FormInstance>()

function emptyForm(): SsoProvider {
  return {
    id: undefined, code: '', name: '', type: 'OAUTH2_GENERIC', icon: '',
    clientId: '', clientSecret: '',
    authorizationUri: '', tokenUri: '', userinfoUri: '', emailsUri: '',
    scope: '', enablePkce: 0, userFieldMapping: '',
    defaultRoleId: undefined, defaultDeptId: undefined,
    enabled: 1, orderNum: 0, remark: ''
  }
}

function emptyMappingRows(): MappingRow[] {
  return MAPPING_KEYS.map(k => ({ ...k, value: '' }))
}

const form = ref<SsoProvider>(emptyForm())

function loadRoleOptions() {
  listAllRole().then(res => {
    roleOptions.value = (res.data || []).filter((r: Role) => r.status === '0' || (r.status as unknown) === 0)
  })
}

function loadDeptOptions() {
  treeSelect().then(res => { deptOptions.value = res.data || [] })
}

function onScopeChange(val: string[]) {
  form.value.scope = (val || []).join(' ')
}

/** form.scope 字符串 → scopeList 数组 */
function syncScopeFromForm() {
  scopeList.value = (form.value.scope || '').split(/\s+/).filter(Boolean)
}

/** form.userFieldMapping JSON → mappingRows */
function syncMappingFromForm() {
  const rows = emptyMappingRows()
  if (form.value.userFieldMapping) {
    try {
      const obj = JSON.parse(form.value.userFieldMapping) as Record<string, unknown>
      rows.forEach(r => { if (obj[r.key] != null) r.value = String(obj[r.key]) })
    } catch (e) {
      ElMessage.warning('原字段映射 JSON 解析失败，已重置为空')
    }
  }
  mappingRows.value = rows
}

/** mappingRows → form.userFieldMapping JSON */
function buildMappingJson(): string {
  const obj: Record<string, string> = {}
  mappingRows.value.forEach(r => {
    const v = (r.value || '').trim()
    if (v) obj[r.key] = v
  })
  return Object.keys(obj).length ? JSON.stringify(obj) : ''
}

function getList() {
  loading.value = true
  listProviders(queryParams).then(res => {
    dataList.value = res.rows
    total.value = res.total
  }).finally(() => { loading.value = false })
}

function handleQuery() {
  queryParams['current'] = 1
  getList()
}

function resetQuery() {
  Object.keys(queryParams).forEach(k => delete (queryParams as Record<string, unknown>)[k])
  Object.assign(queryParams, { current: 1, size: 10 })
  handleQuery()
}

function handleAdd() {
  form.value = emptyForm()
  editingSecret.value = true
  scopeList.value = []
  mappingRows.value = emptyMappingRows()
  dialogTitle.value = '新增身份认证源'
  dialogVisible.value = true
  nextTick(() => { formRef.value?.clearValidate() })
}

function handleEdit(row: SsoProvider) {
  getProvider(row.id as number).then(res => {
    form.value = { ...emptyForm(), ...res.data, clientSecret: '' }
    editingSecret.value = false
    syncScopeFromForm()
    syncMappingFromForm()
    dialogTitle.value = '修改身份认证源'
    dialogVisible.value = true
  })
}

function submitForm() {
  formRef.value?.validate(valid => {
    if (!valid) return
    const idRow = mappingRows.value.find(r => r.key === 'id')
    if (!idRow?.value || !idRow.value.trim()) {
      ElMessage.error('字段映射中的 id 为必填项')
      return
    }
    form.value.userFieldMapping = buildMappingJson()
    const op = form.value.id ? updateProvider : addProvider
    op(form.value).then(() => {
      ElMessage.success(form.value.id ? '修改成功' : '新增成功')
      dialogVisible.value = false
      getList()
    })
  })
}

function handleDelete(row: SsoProvider) {
  ElMessageBox.confirm(`确认删除 "${row.name}"？删除后历史绑定不会被清除。`, '警告', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(() => delProvider(row.id as number))
    .then(() => { getList(); ElMessage.success('删除成功') })
    .catch(() => { /* 用户取消 */ })
}

getList()
loadRoleOptions()
loadDeptOptions()
</script>

<style scoped>
.mapping-desc { margin-left: 8px; color: #606266; font-size: 12px; }
.mapping-hint { margin-top: 6px; color: #909399; font-size: 12px; }
.pkce-hint { margin-left: 12px; color: #909399; font-size: 12px; }
</style>

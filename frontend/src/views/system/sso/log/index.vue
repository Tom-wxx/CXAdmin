<template>
  <div class="app-container">
    <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />
    <TableToolbar show-refresh @refresh="getList" />

    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column label="ID" prop="id" width="80" align="center" />
      <el-table-column label="IdP" prop="providerCode" width="100">
        <template #default="s">
          <el-tag size="small" effect="plain">{{ s.row.providerCode || '—' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="动作" prop="action" width="100" align="center">
        <template #default="s">
          <el-tag :type="actionType(s.row.action)" size="small">{{ actionLabel(s.row.action) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模式" prop="mode" width="80" align="center">
        <template #default="s">
          <span v-if="s.row.mode" :class="'mode-' + s.row.mode">{{ s.row.mode === 'login' ? '登录' : '绑定' }}</span>
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="s">
          <el-tag :type="s.row.status === 'success' ? 'success' : 'danger'" size="small">
            {{ s.row.status === 'success' ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="本地用户ID" prop="userId" width="100" align="center">
        <template #default="s">{{ s.row.userId || '—' }}</template>
      </el-table-column>
      <el-table-column label="外部用户ID" prop="externalUserId" width="140" show-overflow-tooltip>
        <template #default="s">{{ s.row.externalUserId || '—' }}</template>
      </el-table-column>
      <el-table-column label="IP" prop="ip" width="130" show-overflow-tooltip />
      <el-table-column label="User-Agent" prop="userAgent" min-width="180" show-overflow-tooltip />
      <el-table-column label="错误信息" prop="errorMessage" min-width="200" show-overflow-tooltip>
        <template #default="s">
          <span v-if="s.row.errorMessage" class="error-msg">{{ s.row.errorMessage }}</span>
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="发生时间" prop="createTime" width="160" align="center" />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { listSsoLogs, listProviders } from '@/api/system/sso'
import type { SsoLoginLog } from '@/types/system/sso'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'

defineOptions({ name: 'SsoLog' })

// 原始代码按 providerId 过滤，SsoLogQuery 类型仅定义 providerCode；
// 此处用宽松类型以与后端实际参数匹配
interface SsoLogQueryExtended {
  current: number
  size: number
  providerId: number | null
  action: string | null
  status: string | null
  userId: number | null
  from: string | null
  to: string | null
}

const loading = ref(true)
const dataList = ref<SsoLoginLog[]>([])
const total = ref(0)

const queryParams = reactive<SsoLogQueryExtended>({
  current: 1, size: 10,
  providerId: null, action: null, status: null, userId: null,
  from: null, to: null
})

const searchFields = ref([
  { prop: 'providerId', label: 'IdP', type: 'select', options: [] as { label: string; value: number }[], width: '160px' },
  { prop: 'action', label: '动作', type: 'select', width: '120px',
    options: [
      { label: '发起授权', value: 'authorize' },
      { label: '回调', value: 'callback' },
      { label: '绑定', value: 'bind' },
      { label: '解绑', value: 'unbind' }
    ] },
  { prop: 'status', label: '状态', type: 'select', width: '120px',
    options: [
      { label: '成功', value: 'success' },
      { label: '失败', value: 'fail' }
    ] },
  { prop: 'userId', label: '用户ID', type: 'input', width: '120px' }
])

function loadProviders() {
  // 拉所有 IdP（含禁用的），便于历史日志查询
  listProviders({ current: 1, size: 100 } as any).then(res => {
    const opts = (res.rows || []).map(p => ({ label: `${p.name} (${p.code})`, value: p.id as number }))
    // 把 options 注入到 searchFields[0]
    const f = searchFields.value.find(x => x.prop === 'providerId')
    if (f) f.options = opts
  })
}

function getList() {
  loading.value = true
  listSsoLogs(queryParams as any)
    .then(res => { dataList.value = res.rows; total.value = res.total })
    .finally(() => { loading.value = false })
}

function handleQuery() { queryParams.current = 1; getList() }

function resetQuery() {
  Object.assign(queryParams, { current: 1, size: 10, providerId: null, action: null, status: null, userId: null, from: null, to: null })
  handleQuery()
}

function actionType(a: string): string {
  return ({ authorize: 'info', callback: 'primary', bind: 'success', unbind: 'warning' } as Record<string, string>)[a] || ''
}

function actionLabel(a: string): string {
  return ({ authorize: '发起授权', callback: '回调', bind: '绑定', unbind: '解绑' } as Record<string, string>)[a] || a
}

loadProviders()
getList()
</script>

<style scoped>
.mode-login   { color: #13c2c2; }
.mode-bind    { color: #722ed1; }
.error-msg    { color: #f5222d; }
</style>

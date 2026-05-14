<template>
  <div class="app-container">
    <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />
    <TableToolbar show-refresh @refresh="getList" />

    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column label="ID" prop="id" width="80" align="center" />
      <el-table-column label="IdP" prop="providerCode" width="100">
        <template slot-scope="s">
          <el-tag size="mini" effect="plain">{{ s.row.providerCode || '—' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="动作" prop="action" width="100" align="center">
        <template slot-scope="s">
          <el-tag :type="actionType(s.row.action)" size="mini">{{ actionLabel(s.row.action) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模式" prop="mode" width="80" align="center">
        <template slot-scope="s">
          <span v-if="s.row.mode" :class="'mode-' + s.row.mode">{{ s.row.mode === 'login' ? '登录' : '绑定' }}</span>
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template slot-scope="s">
          <el-tag :type="s.row.status === 'success' ? 'success' : 'danger'" size="mini">
            {{ s.row.status === 'success' ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="本地用户ID" prop="userId" width="100" align="center">
        <template slot-scope="s">{{ s.row.userId || '—' }}</template>
      </el-table-column>
      <el-table-column label="外部用户ID" prop="externalUserId" width="140" show-overflow-tooltip>
        <template slot-scope="s">{{ s.row.externalUserId || '—' }}</template>
      </el-table-column>
      <el-table-column label="IP" prop="ip" width="130" show-overflow-tooltip />
      <el-table-column label="User-Agent" prop="userAgent" min-width="180" show-overflow-tooltip />
      <el-table-column label="错误信息" prop="errorMessage" min-width="200" show-overflow-tooltip>
        <template slot-scope="s">
          <span v-if="s.row.errorMessage" class="error-msg">{{ s.row.errorMessage }}</span>
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="发生时间" prop="createTime" width="160" align="center" />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.current"
      :limit.sync="queryParams.size"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listSsoLogs, listProviders } from '@/api/system/sso'
import Pagination from '@/components/Pagination'
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'

export default {
  name: 'SsoLog',
  components: { Pagination, SearchForm, TableToolbar },
  data() {
    return {
      loading: true,
      dataList: [],
      total: 0,
      queryParams: {
        current: 1, size: 10,
        providerId: null, action: null, status: null, userId: null,
        from: null, to: null
      },
      providerOptions: [],
      searchFields: [
        { prop: 'providerId', label: 'IdP', type: 'select', options: [], width: '160px' },
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
      ]
    }
  },
  created() {
    this.loadProviders()
    this.getList()
  },
  methods: {
    loadProviders() {
      // 拉所有 IdP（含禁用的），便于历史日志查询
      listProviders({ current: 1, size: 100 }).then(res => {
        const opts = (res.rows || []).map(p => ({ label: `${p.name} (${p.code})`, value: p.id }))
        this.providerOptions = opts
        // 把 options 注入到 searchFields[0]
        const f = this.searchFields.find(x => x.prop === 'providerId')
        if (f) f.options = opts
      })
    },
    getList() {
      this.loading = true
      listSsoLogs(this.queryParams)
        .then(res => { this.dataList = res.rows; this.total = res.total })
        .finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.current = 1; this.getList() },
    resetQuery() {
      this.queryParams = { current: 1, size: 10, providerId: null, action: null, status: null, userId: null, from: null, to: null }
      this.handleQuery()
    },
    actionType(a) {
      return { authorize: 'info', callback: 'primary', bind: 'success', unbind: 'warning' }[a] || ''
    },
    actionLabel(a) {
      return { authorize: '发起授权', callback: '回调', bind: '绑定', unbind: '解绑' }[a] || a
    }
  }
}
</script>

<style scoped>
.mode-login   { color: #13c2c2; }
.mode-bind    { color: #722ed1; }
.error-msg    { color: #f5222d; }
</style>

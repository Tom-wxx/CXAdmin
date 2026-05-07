<template>
  <div class="app-container">
    <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />

    <TableToolbar show-refresh @refresh="handleRefresh">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleBatchForceLogout"
        >批量强退</el-button>
      </el-col>
    </TableToolbar>

    <el-table
      v-loading="loading"
      :data="onlineList"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ (queryParams.current - 1) * queryParams.size + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会话编号" align="center" prop="tokenId" show-overflow-tooltip width="260" />
      <el-table-column label="用户名" align="center" prop="username" width="120" />
      <el-table-column label="用户昵称" align="center" prop="nickname" width="120" />
      <el-table-column label="部门名称" align="center" prop="deptName" show-overflow-tooltip />
      <el-table-column label="登录地址" align="center" prop="ipaddr" width="140" />
      <el-table-column label="登录地点" align="center" prop="loginLocation" show-overflow-tooltip />
      <el-table-column label="浏览器" align="center" prop="browser" show-overflow-tooltip />
      <el-table-column label="操作系统" align="center" prop="os" show-overflow-tooltip />
      <el-table-column label="登录时间" align="center" prop="loginTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleForceLogout(scope.row)"
          >强退</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0"
      :current-page.sync="queryParams.current"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="queryParams.size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 15px; text-align: right"
    />
  </div>
</template>

<script>
import { listOnlineUser, forceLogout, batchForceLogout } from '@/api/monitor/online'
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'

export default {
  name: 'OnlineUser',
  components: { SearchForm, TableToolbar },
  data() {
    return {
      searchFields: [
        { prop: 'username', label: '用户名', type: 'input', placeholder: '请输入用户名' },
        { prop: 'ipaddr', label: '登录地址', type: 'input', placeholder: '请输入登录地址' }
      ],
      loading: true,
      ids: [],
      multiple: true,
      onlineList: [],
      total: 0,
      queryParams: {
        username: undefined,
        ipaddr: undefined,
        current: 1,
        size: 10
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listOnlineUser(this.queryParams).then(response => {
        this.onlineList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    handleSizeChange(size) {
      this.queryParams.size = size
      this.queryParams.current = 1
      this.getList()
    },
    handleCurrentChange(current) {
      this.queryParams.current = current
      this.getList()
    },
    handleRefresh() {
      this.getList()
      this.$message.success('刷新成功')
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.tokenId)
      this.multiple = !selection.length
    },
    handleForceLogout(row) {
      this.$confirm('是否确认强退用户"' + row.username + '"？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return forceLogout(row.tokenId)
      }).then(() => {
        this.getList()
        this.$message.success('强制退出成功')
      })
    },
    handleBatchForceLogout() {
      const tokenIds = this.ids
      if (tokenIds.length === 0) {
        this.$message.warning('请选择要强退的用户')
        return
      }
      this.$confirm('是否确认强退选中的' + tokenIds.length + '个用户？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return batchForceLogout(tokenIds.join(','))
      }).then(() => {
        this.getList()
        this.$message.success('批量强制退出成功')
      })
    },
    parseTime(time) {
      if (!time) return ''
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

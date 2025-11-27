<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入用户名"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="登录地址" prop="ipaddr">
        <el-input
          v-model="queryParams.ipaddr"
          placeholder="请输入登录地址"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
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
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleRefresh"
        >刷新</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
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

    <!-- 分页 -->
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

export default {
  name: 'OnlineUser',
  data() {
    return {
      // 加载状态
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 在线用户列表
      onlineList: [],
      // 总数
      total: 0,
      // 查询参数
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
    /** 查询在线用户列表 */
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
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        username: undefined,
        ipaddr: undefined,
        current: 1,
        size: 10
      }
      this.handleQuery()
    },
    /** 每页条数改变 */
    handleSizeChange(size) {
      this.queryParams.size = size
      this.queryParams.current = 1
      this.getList()
    },
    /** 当前页改变 */
    handleCurrentChange(current) {
      this.queryParams.current = current
      this.getList()
    },
    /** 刷新按钮操作 */
    handleRefresh() {
      this.getList()
      this.$message.success('刷新成功')
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.tokenId)
      this.multiple = !selection.length
    },
    /** 强退按钮操作 */
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
    /** 批量强退按钮操作 */
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

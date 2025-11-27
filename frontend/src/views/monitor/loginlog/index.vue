<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="用户账号" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入用户账号"
          clearable
          style="width: 180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="登录地址" prop="ipaddr">
        <el-input
          v-model="queryParams.ipaddr"
          placeholder="请输入登录IP地址"
          clearable
          style="width: 180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="登录状态" clearable style="width: 180px">
          <el-option label="成功" value="0" />
          <el-option label="失败" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="登录时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
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
          @click="handleClean"
        >清空</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          :loading="exportLoading"
          @click="handleExport"
        >导出</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="loginLogList" border>
      <el-table-column label="日志编号" align="center" prop="infoId" width="100" />
      <el-table-column label="用户账号" align="center" prop="username" width="120" />
      <el-table-column label="登录地址" align="center" prop="ipaddr" width="130" show-overflow-tooltip />
      <el-table-column label="登录地点" align="center" prop="loginLocation" show-overflow-tooltip />
      <el-table-column label="浏览器" align="center" prop="browser" show-overflow-tooltip />
      <el-table-column label="操作系统" align="center" prop="os" show-overflow-tooltip />
      <el-table-column label="登录状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">成功</el-tag>
          <el-tag v-else type="danger" size="small">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="提示消息" align="center" prop="msg" show-overflow-tooltip />
      <el-table-column label="登录时间" align="center" prop="loginTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详细</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
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

    <!-- 详细信息对话框 -->
    <el-dialog title="登录日志详细" :visible.sync="detailVisible" width="700px" append-to-body>
      <el-form ref="detailForm" :model="detailData" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="日志编号：">{{ detailData.infoId }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户账号：">{{ detailData.username }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录地址：">{{ detailData.ipaddr }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录地点：">{{ detailData.loginLocation }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="浏览器：">{{ detailData.browser }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作系统：">{{ detailData.os }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录状态：">
              <el-tag v-if="detailData.status === '0'" type="success">成功</el-tag>
              <el-tag v-else type="danger">失败</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录时间：">{{ parseTime(detailData.loginTime) }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="提示消息：">
              <div style="word-break: break-all;">
                {{ detailData.msg }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="detailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listLoginLog, getLoginLog, delLoginLog, cleanLoginLog, exportLoginLog } from '@/api/monitor/loginlog'
import Pagination from '@/components/Pagination'

export default {
  name: 'LoginLog',
  components: {
    Pagination
  },
  data() {
    return {
      // 加载状态
      loading: true,
      // 登录日志列表
      loginLogList: [],
      // 总条数
      total: 0,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        current: 1,
        size: 10,
        username: undefined,
        ipaddr: undefined,
        status: undefined,
        beginTime: undefined,
        endTime: undefined
      },
      // 详细信息对话框
      detailVisible: false,
      detailData: {},
      // 导出loading
      exportLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询登录日志列表 */
    getList() {
      this.loading = true
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.beginTime = this.dateRange[0]
        this.queryParams.endTime = this.dateRange[1]
      } else {
        this.queryParams.beginTime = undefined
        this.queryParams.endTime = undefined
      }
      listLoginLog(this.queryParams).then(response => {
        this.loginLogList = response.rows
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
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.queryParams = {
        current: 1,
        size: 10,
        username: undefined,
        ipaddr: undefined,
        status: undefined,
        beginTime: undefined,
        endTime: undefined
      }
      this.handleQuery()
    },
    /** 详细按钮操作 */
    handleView(row) {
      getLoginLog(row.infoId).then(response => {
        this.detailData = response.data
        this.detailVisible = true
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除日志编号为"' + row.infoId + '"的数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delLoginLog(row.infoId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    /** 清空按钮操作 */
    handleClean() {
      this.$confirm('是否确认清空所有登录日志数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return cleanLoginLog()
      }).then(() => {
        this.getList()
        this.$message.success('清空成功')
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
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$confirm('是否确认导出所有登录日志数据？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.exportLoading = true
        return exportLoginLog(this.queryParams)
      }).then(response => {
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = '登录日志.xlsx'
        link.click()
        URL.revokeObjectURL(link.href)
        this.exportLoading = false
        this.$message.success('导出成功')
      }).catch(() => {
        this.exportLoading = false
      })
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

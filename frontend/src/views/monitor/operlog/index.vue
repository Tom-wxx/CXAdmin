<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="系统模块" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入系统模块"
          clearable
          style="width: 180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人员" prop="operName">
        <el-input
          v-model="queryParams.operName"
          placeholder="请输入操作人员"
          clearable
          style="width: 180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="businessType">
        <el-select v-model="queryParams.businessType" placeholder="操作类型" clearable style="width: 180px">
          <el-option label="其它" :value="0" />
          <el-option label="新增" :value="1" />
          <el-option label="修改" :value="2" />
          <el-option label="删除" :value="3" />
          <el-option label="授权" :value="4" />
          <el-option label="导出" :value="5" />
          <el-option label="导入" :value="6" />
          <el-option label="强退" :value="7" />
          <el-option label="清空数据" :value="8" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="操作状态" clearable style="width: 180px">
          <el-option label="正常" :value="0" />
          <el-option label="异常" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间">
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
    <el-table v-loading="loading" :data="operLogList" border>
      <el-table-column label="日志编号" align="center" prop="operId" width="100" />
      <el-table-column label="系统模块" align="center" prop="title" />
      <el-table-column label="操作类型" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.businessType === 0" type="info" size="small">其它</el-tag>
          <el-tag v-else-if="scope.row.businessType === 1" type="success" size="small">新增</el-tag>
          <el-tag v-else-if="scope.row.businessType === 2" type="warning" size="small">修改</el-tag>
          <el-tag v-else-if="scope.row.businessType === 3" type="danger" size="small">删除</el-tag>
          <el-tag v-else type="primary" size="small">其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作人员" align="center" prop="operName" width="100" />
      <el-table-column label="操作地址" align="center" prop="operIp" width="130" show-overflow-tooltip />
      <el-table-column label="操作地点" align="center" prop="operLocation" show-overflow-tooltip />
      <el-table-column label="操作状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">异常</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作时间" align="center" prop="operTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.operTime) }}</span>
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
    <el-dialog title="操作日志详细" :visible.sync="detailVisible" width="700px" append-to-body>
      <el-form ref="detailForm" :model="detailData" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="操作模块：">{{ detailData.title }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录信息：">{{ detailData.operName }} / {{ detailData.operIp }} / {{ detailData.operLocation }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求地址：">{{ detailData.operUrl }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求方式：">{{ detailData.requestMethod }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作方法：">{{ detailData.method }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求参数：">
              <div style="max-height: 200px; overflow-y: auto; word-break: break-all;">
                {{ detailData.operParam }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回参数：">
              <div style="max-height: 200px; overflow-y: auto; word-break: break-all;">
                {{ detailData.jsonResult }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作状态：">
              <el-tag v-if="detailData.status === 0" type="success">正常</el-tag>
              <el-tag v-else type="danger">异常</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作时间：">{{ parseTime(detailData.operTime) }}</el-form-item>
          </el-col>
          <el-col :span="24" v-if="detailData.errorMsg">
            <el-form-item label="异常信息：">
              <div style="max-height: 200px; overflow-y: auto; word-break: break-all; color: #F56C6C;">
                {{ detailData.errorMsg }}
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
import { listOperLog, getOperLog, delOperLog, cleanOperLog, exportOperLog } from '@/api/monitor/operlog'
import Pagination from '@/components/Pagination'

export default {
  name: 'OperLog',
  components: {
    Pagination
  },
  data() {
    return {
      // 加载状态
      loading: true,
      // 操作日志列表
      operLogList: [],
      // 总条数
      total: 0,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        current: 1,
        size: 10,
        title: undefined,
        operName: undefined,
        businessType: undefined,
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
    /** 查询操作日志列表 */
    getList() {
      this.loading = true
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.beginTime = this.dateRange[0]
        this.queryParams.endTime = this.dateRange[1]
      } else {
        this.queryParams.beginTime = undefined
        this.queryParams.endTime = undefined
      }
      listOperLog(this.queryParams).then(response => {
        this.operLogList = response.rows
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
        title: undefined,
        operName: undefined,
        businessType: undefined,
        status: undefined,
        beginTime: undefined,
        endTime: undefined
      }
      this.handleQuery()
    },
    /** 详细按钮操作 */
    handleView(row) {
      getOperLog(row.operId).then(response => {
        this.detailData = response.data
        this.detailVisible = true
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除日志编号为"' + row.operId + '"的数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delOperLog(row.operId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    /** 清空按钮操作 */
    handleClean() {
      this.$confirm('是否确认清空所有操作日志数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return cleanOperLog()
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
      this.$confirm('是否确认导出所有操作日志数据？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.exportLoading = true
        return exportOperLog(this.queryParams)
      }).then(response => {
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = '操作日志.xlsx'
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

<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <!-- 工具栏 -->
    <TableToolbar show-refresh @refresh="getList">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" @click="handleClean">清空</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-back" size="mini" @click="handleBack">返回</el-button>
      </el-col>
    </TableToolbar>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="jobLogList" border>
      <el-table-column label="日志编号" align="center" prop="jobLogId" width="80" />
      <el-table-column label="任务名称" align="center" prop="jobName" show-overflow-tooltip />
      <el-table-column label="任务组名" align="center" prop="jobGroup" width="100" />
      <el-table-column label="调用目标" align="center" prop="invokeTarget" show-overflow-tooltip />
      <el-table-column label="日志信息" align="center" prop="jobMessage" show-overflow-tooltip />
      <el-table-column label="执行状态" align="center" width="100">
        <template slot-scope="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="执行时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
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

    <!-- 查看详情对话框 -->
    <el-dialog title="调度日志详细" :visible.sync="viewDialogVisible" width="700px" append-to-body>
      <el-form ref="form" :model="viewForm" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="日志编号：">{{ viewForm.jobLogId }}</el-form-item>
            <el-form-item label="任务名称：">{{ viewForm.jobName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务组名：">{{ viewForm.jobGroup }}</el-form-item>
            <el-form-item label="执行时间：">{{ parseTime(viewForm.createTime) }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="调用方法：">{{ viewForm.invokeTarget }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="日志信息：">{{ viewForm.jobMessage }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="执行状态：">
              <DictTag :options="statusOptions" :value="viewForm.status" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="viewForm.status === '1'">
            <el-form-item label="异常信息：">
              <div style="white-space: pre-wrap; max-height: 300px; overflow-y: auto; background: #f5f5f5; padding: 10px; border-radius: 4px;">
                {{ viewForm.exceptionInfo }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listJobLog, getJobLog, delJobLog, cleanJobLog } from '@/api/monitor/job'
import Pagination from '@/components/Pagination'
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'
import DictTag from '@/components/DictTag'

const JOB_GROUP_OPTIONS = [
  { value: 'DEFAULT', label: '默认' },
  { value: 'SYSTEM', label: '系统' }
]
const STATUS_OPTIONS = [
  { value: '0', label: '成功', type: 'success' },
  { value: '1', label: '失败', type: 'danger' }
]

export default {
  name: 'JobLog',
  components: {
    Pagination,
    SearchForm,
    TableToolbar,
    DictTag
  },
  data() {
    return {
      // 搜索字段配置
      searchFields: [
        { prop: 'jobName', label: '任务名称', type: 'input' },
        { prop: 'jobGroup', label: '任务组名', type: 'select', options: JOB_GROUP_OPTIONS, placeholder: '任务组名' },
        { prop: 'status', label: '执行状态', type: 'select', options: STATUS_OPTIONS, placeholder: '执行状态' }
      ],
      statusOptions: STATUS_OPTIONS,
      // 加载状态
      loading: true,
      // 任务日志列表
      jobLogList: [],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        current: 1,
        size: 10,
        jobName: undefined,
        jobGroup: undefined,
        status: undefined
      },
      // 查看详情对话框显示状态
      viewDialogVisible: false,
      // 查看详情数据
      viewForm: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询任务日志列表 */
    getList() {
      this.loading = true
      listJobLog(this.queryParams).then(response => {
        this.jobLogList = response.rows
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
    /** 重置按钮操作（SearchForm 已自动清字段） */
    resetQuery() {
      this.queryParams.current = 1
      this.queryParams.size = 10
      this.handleQuery()
    },
    /** 查看按钮操作 */
    handleView(row) {
      const jobLogId = row.jobLogId
      getJobLog(jobLogId).then(response => {
        this.viewForm = response.data
        this.viewDialogVisible = true
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除日志编号为"' + row.jobLogId + '"的数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delJobLog(row.jobLogId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    /** 清空按钮操作 */
    handleClean() {
      this.$confirm('是否确认清空所有调度日志数据？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return cleanJobLog()
      }).then(() => {
        this.getList()
        this.$message.success('清空成功')
      })
    },
    /** 返回按钮操作 */
    handleBack() {
      this.$router.push('/monitor/job')
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

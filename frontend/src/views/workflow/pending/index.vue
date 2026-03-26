<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>待办任务</span>
      </div>

      <!-- 任务列表 -->
      <el-table
        v-loading="loading"
        :data="taskList"
        border
        style="width: 100%"
      >
        <el-table-column label="任务名称" prop="taskName" />
        <el-table-column label="流程实例" prop="processInstanceId" />
        <el-table-column label="审批层级" prop="taskLevel" align="center" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="mini"
              @click="handleApprove(scope.row)"
            >
              审批
            </el-button>
            <el-button
              size="mini"
              @click="handleDetail(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="审批意见">
          <el-input
            v-model="form.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitReject">驳回</el-button>
        <el-button type="primary" @click="submitApprove">通过</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getPendingTasks, approveTask, rejectTask, getProcessDetail } from '@/api/workflow/workflow'

export default {
  name: 'PendingTasks',
  data() {
    return {
      loading: false,
      taskList: [],
      dialogVisible: false,
      dialogTitle: '审批',
      form: {
        taskId: null,
        comment: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getPendingTasks().then(response => {
        this.taskList = response.data
        this.loading = false
      })
    },
    handleApprove(row) {
      this.form.taskId = row.id
      this.form.comment = ''
      this.dialogTitle = `审批 - ${row.taskName}`
      this.dialogVisible = true
    },
    handleDetail(row) {
      this.$router.push({
        path: '/workflow/detail',
        query: { instanceId: row.processInstanceId }
      })
    },
    submitApprove() {
      approveTask(this.form.taskId, this.form.comment).then(() => {
        this.$message.success('审批通过')
        this.dialogVisible = false
        this.getList()
      })
    },
    submitReject() {
      if (!this.form.comment) {
        this.$message.warning('请填写驳回原因')
        return
      }
      this.$confirm('确认驳回此审批?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        rejectTask(this.form.taskId, this.form.comment).then(() => {
          this.$message.success('已驳回')
          this.dialogVisible = false
          this.getList()
        })
      })
    }
  }
}
</script>

<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="任务名称" prop="jobName">
        <el-input
          v-model="queryParams.jobName"
          placeholder="请输入任务名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务组名" prop="jobGroup">
        <el-select v-model="queryParams.jobGroup" placeholder="任务组名" clearable style="width: 200px">
          <el-option label="默认" value="DEFAULT" />
          <el-option label="系统" value="SYSTEM" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="任务状态" clearable style="width: 200px">
          <el-option label="正常" value="0" />
          <el-option label="暂停" value="1" />
        </el-select>
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-document"
          size="mini"
          @click="handleJobLog"
        >日志</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="jobList" border>
      <el-table-column label="任务编号" align="center" prop="jobId" width="80" />
      <el-table-column label="任务名称" align="center" prop="jobName" show-overflow-tooltip />
      <el-table-column label="任务组名" align="center" prop="jobGroup" width="100" />
      <el-table-column label="调用目标" align="center" prop="invokeTarget" show-overflow-tooltip />
      <el-table-column label="Cron表达式" align="center" prop="cronExpression" show-overflow-tooltip />
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-video-play"
            @click="handleRun(scope.row)"
          >执行</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px" append-to-body>
      <el-form ref="jobForm" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="form.jobName" placeholder="请输入任务名称" maxlength="64" />
        </el-form-item>
        <el-form-item label="任务组名" prop="jobGroup">
          <el-select v-model="form.jobGroup" placeholder="请选择任务组名" style="width: 100%">
            <el-option label="默认" value="DEFAULT" />
            <el-option label="系统" value="SYSTEM" />
          </el-select>
        </el-form-item>
        <el-form-item label="调用目标" prop="invokeTarget">
          <el-input v-model="form.invokeTarget" placeholder="如：testTask.execute" maxlength="500" />
          <span class="help-text">Bean名称.方法名，如：testTask.execute</span>
        </el-form-item>
        <el-form-item label="Cron表达式" prop="cronExpression">
          <el-input v-model="form.cronExpression" placeholder="如：0/10 * * * * ?" maxlength="255" />
          <span class="help-text">每10秒：0/10 * * * * ? | 每天凌晨1点：0 0 1 * * ?</span>
        </el-form-item>
        <el-form-item label="错误策略" prop="misfirePolicy">
          <el-radio-group v-model="form.misfirePolicy">
            <el-radio label="1">立即执行</el-radio>
            <el-radio label="2">执行一次</el-radio>
            <el-radio label="3">放弃执行</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="并发执行" prop="concurrent">
          <el-radio-group v-model="form.concurrent">
            <el-radio label="0">允许</el-radio>
            <el-radio label="1">禁止</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listJob, getJob, addJob, updateJob, delJob, changeJobStatus, runJob } from '@/api/monitor/job'
import Pagination from '@/components/Pagination'

export default {
  name: 'Job',
  components: {
    Pagination
  },
  data() {
    return {
      // 加载状态
      loading: true,
      // 任务列表
      jobList: [],
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
      // 对话框标题
      dialogTitle: '',
      // 对话框显示状态
      dialogVisible: false,
      // 表单数据
      form: {},
      // 表单校验规则
      rules: {
        jobName: [
          { required: true, message: '任务名称不能为空', trigger: 'blur' },
          { max: 64, message: '任务名称长度不能超过64个字符', trigger: 'blur' }
        ],
        jobGroup: [
          { required: true, message: '任务组名不能为空', trigger: 'change' }
        ],
        invokeTarget: [
          { required: true, message: '调用目标不能为空', trigger: 'blur' },
          { max: 500, message: '调用目标长度不能超过500个字符', trigger: 'blur' }
        ],
        cronExpression: [
          { required: true, message: 'Cron表达式不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询定时任务列表 */
    getList() {
      this.loading = true
      listJob(this.queryParams).then(response => {
        this.jobList = response.rows
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
      this.queryParams = {
        current: 1,
        size: 10,
        jobName: undefined,
        jobGroup: undefined,
        status: undefined
      }
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.dialogTitle = '添加定时任务'
      this.dialogVisible = true
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const jobId = row.jobId
      getJob(jobId).then(response => {
        this.form = response.data
        this.dialogTitle = '修改定时任务'
        this.dialogVisible = true
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs.jobForm.validate(valid => {
        if (valid) {
          if (this.form.jobId) {
            updateJob(this.form).then(response => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.getList()
            })
          } else {
            addJob(this.form).then(response => {
              this.$message.success('新增成功')
              this.dialogVisible = false
              this.getList()
            })
          }
        }
      })
    },
    /** 取消按钮 */
    cancel() {
      this.dialogVisible = false
      this.reset()
    },
    /** 表单重置 */
    reset() {
      this.form = {
        jobId: undefined,
        jobName: undefined,
        jobGroup: 'DEFAULT',
        invokeTarget: undefined,
        cronExpression: undefined,
        misfirePolicy: '3',
        concurrent: '1',
        status: '1',
        remark: undefined
      }
      if (this.$refs.jobForm) {
        this.$refs.jobForm.resetFields()
      }
    },
    /** 任务状态修改 */
    handleStatusChange(row) {
      let text = row.status === '0' ? '启用' : '停用'
      this.$confirm('确认要"' + text + '""' + row.jobName + '"任务吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return changeJobStatus(row)
      }).then(() => {
        this.$message.success(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    /** 立即执行按钮操作 */
    handleRun(row) {
      this.$confirm('确认要立即执行一次"' + row.jobName + '"任务吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return runJob(row)
      }).then(() => {
        this.$message.success('执行成功')
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除任务名称为"' + row.jobName + '"的数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delJob(row.jobId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    /** 任务日志按钮操作 */
    handleJobLog() {
      this.$router.push('/monitor/jobLog')
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
.help-text {
  font-size: 12px;
  color: #999;
  display: block;
  margin-top: 5px;
}
</style>

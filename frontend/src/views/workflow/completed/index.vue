<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>已办任务</span>
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
        <el-table-column label="审批结果" align="center" width="100">
          <template slot-scope="scope">
            <DictTag :options="statusOptions" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="审批意见" prop="approvalComment" show-overflow-tooltip />
        <el-table-column label="审批时间" prop="approvalTime" width="180" />
        <el-table-column label="操作" align="center" width="120">
          <template slot-scope="scope">
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
  </div>
</template>

<script>
import { getCompletedTasks } from '@/api/workflow/workflow'
import DictTag from '@/components/DictTag'

const STATUS_OPTIONS = [
  { value: 'approved', label: '通过', type: 'success' },
  { value: 'rejected', label: '驳回', type: 'danger' }
]

export default {
  name: 'CompletedTasks',
  components: { DictTag },
  data() {
    return {
      statusOptions: STATUS_OPTIONS,
      loading: false,
      taskList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCompletedTasks().then(response => {
        this.taskList = response.data
        this.loading = false
      })
    },
    handleDetail(row) {
      this.$router.push({
        path: '/workflow/detail',
        query: { instanceId: row.processInstanceId }
      })
    }
  }
}
</script>

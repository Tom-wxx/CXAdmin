<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>我发起的流程</span>
      </div>

      <!-- 流程列表 -->
      <el-table
        v-loading="loading"
        :data="processList"
        border
        style="width: 100%"
      >
        <el-table-column label="流程编号" prop="instanceNo" width="180" />
        <el-table-column label="流程名称" prop="processName" />
        <el-table-column label="标题" prop="title" show-overflow-tooltip />
        <el-table-column label="当前层级" align="center" width="100">
          <template slot-scope="scope">
            {{ scope.row.currentLevel }}/{{ scope.row.totalLevel }}
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 'pending'" type="warning">审批中</el-tag>
            <el-tag v-else-if="scope.row.status === 'approved'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.status === 'rejected'" type="danger">已驳回</el-tag>
            <el-tag v-else-if="scope.row.status === 'cancelled'" type="info">已取消</el-tag>
            <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" prop="submitTime" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleDetail(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-if="scope.row.status === 'pending'"
              type="danger"
              size="mini"
              @click="handleCancel(scope.row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getMyProcesses, cancelProcess } from '@/api/workflow/workflow'

export default {
  name: 'MyProcess',
  data() {
    return {
      loading: false,
      processList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getMyProcesses().then(response => {
        this.processList = response.data
        this.loading = false
      })
    },
    handleDetail(row) {
      this.$router.push({
        path: '/workflow/detail',
        query: { instanceId: row.id }
      })
    },
    handleCancel(row) {
      this.$confirm('确认取消此流程?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        cancelProcess(row.id).then(() => {
          this.$message.success('已取消')
          this.getList()
        })
      })
    }
  }
}
</script>

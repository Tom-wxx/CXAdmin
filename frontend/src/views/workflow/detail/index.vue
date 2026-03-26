<template>
  <div class="app-container">
    <el-card v-loading="loading">
      <div slot="header">
        <span>流程详情</span>
        <el-button style="float: right" size="small" @click="goBack">返回</el-button>
      </div>

      <!-- 流程基本信息 -->
      <el-descriptions v-if="instance" :column="2" border>
        <el-descriptions-item label="流程编号">{{ instance.instanceNo }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ instance.processName }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ instance.title }}</el-descriptions-item>
        <el-descriptions-item label="发起人">{{ instance.submitterName }}</el-descriptions-item>
        <el-descriptions-item label="当前层级">
          {{ instance.currentLevel }}/{{ instance.totalLevel }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="instance.status === 'pending'" type="warning">审批中</el-tag>
          <el-tag v-else-if="instance.status === 'approved'" type="success">已通过</el-tag>
          <el-tag v-else-if="instance.status === 'rejected'" type="danger">已驳回</el-tag>
          <el-tag v-else-if="instance.status === 'cancelled'" type="info">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ instance.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">
          {{ instance.finishTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="表单数据" :span="2">
          <pre>{{ formatFormData(instance.formData) }}</pre>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 审批任务列表 -->
      <el-divider>审批任务</el-divider>
      <el-table :data="tasks" border style="width: 100%">
        <el-table-column label="层级" prop="taskLevel" width="80" align="center" />
        <el-table-column label="任务名称" prop="taskName" />
        <el-table-column label="审批人" prop="approverName" />
        <el-table-column label="状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 'pending'" type="warning">待审批</el-tag>
            <el-tag v-else-if="scope.row.status === 'approved'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.status === 'rejected'" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审批意见" prop="approvalComment" show-overflow-tooltip />
        <el-table-column label="审批时间" prop="approvalTime" width="180" />
      </el-table>

      <!-- 审批历史 -->
      <el-divider>审批历史</el-divider>
      <el-timeline>
        <el-timeline-item
          v-for="(history, index) in histories"
          :key="index"
          :timestamp="history.operationTime"
          placement="top"
        >
          <el-card>
            <h4>{{ getActionName(history.action) }}</h4>
            <p>操作人：{{ history.operatorName }}</p>
            <p v-if="history.comment">意见：{{ history.comment }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script>
import { getProcessDetail } from '@/api/workflow/workflow'

export default {
  name: 'ProcessDetail',
  data() {
    return {
      loading: false,
      instance: null,
      tasks: [],
      histories: []
    }
  },
  created() {
    const instanceId = this.$route.query.instanceId
    if (instanceId) {
      this.getDetail(instanceId)
    }
  },
  methods: {
    getDetail(instanceId) {
      this.loading = true
      getProcessDetail(instanceId).then(response => {
        this.instance = response.data.instance
        this.tasks = response.data.tasks
        this.histories = response.data.histories
        this.loading = false
      })
    },
    formatFormData(formData) {
      if (!formData) return '-'
      try {
        return JSON.stringify(JSON.parse(formData), null, 2)
      } catch (e) {
        return formData
      }
    },
    getActionName(action) {
      const actionMap = {
        'submit': '提交申请',
        'approve': '审批通过',
        'reject': '审批驳回',
        'cancel': '取消流程'
      }
      return actionMap[action] || action
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  max-height: 200px;
  overflow: auto;
}
</style>

<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="80px">
      <el-form-item label="通知标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入通知标题"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="通知类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="通知类型" clearable size="small">
          <el-option label="系统通知" value="system" />
          <el-option label="待办提醒" value="todo" />
          <el-option label="审批消息" value="approval" />
          <el-option label="公告" value="announce" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable size="small">
          <el-option label="未读" value="unread" />
          <el-option label="已读" value="read" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handleMarkAsRead"
        >标记已读</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-finished"
          size="mini"
          @click="handleMarkAllAsRead"
        >全部已读</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
    </el-row>

    <!-- 通知列表 -->
    <el-table v-loading="loading" :data="notificationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 'unread'" type="danger" size="small">未读</el-tag>
          <el-tag v-else type="info" size="small">已读</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="优先级" align="center" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.priority === 'urgent'" type="danger" size="small">紧急</el-tag>
          <el-tag v-else-if="scope.row.priority === 'important'" type="warning" size="small">重要</el-tag>
          <el-tag v-else type="info" size="small">普通</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="通知标题" prop="title" :show-overflow-tooltip="true" min-width="200">
        <template slot-scope="scope">
          <span :style="{ fontWeight: scope.row.status === 'unread' ? 'bold' : 'normal' }">
            {{ scope.row.title }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="通知类型" align="center" prop="type" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.type === 'system'">系统通知</span>
          <span v-else-if="scope.row.type === 'todo'">待办提醒</span>
          <span v-else-if="scope.row.type === 'approval'">审批消息</span>
          <span v-else-if="scope.row.type === 'announce'">公告</span>
          <span v-else>{{ scope.row.type }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发送者" align="center" prop="senderName" width="120" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          {{ scope.row.senderName || '系统' }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 查看通知对话框 -->
    <el-dialog :title="viewForm.title" :visible.sync="viewOpen" width="600px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="通知类型">
          <span v-if="viewForm.type === 'system'">系统通知</span>
          <span v-else-if="viewForm.type === 'todo'">待办提醒</span>
          <span v-else-if="viewForm.type === 'approval'">审批消息</span>
          <span v-else-if="viewForm.type === 'announce'">公告</span>
          <span v-else>{{ viewForm.type }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag v-if="viewForm.priority === 'urgent'" type="danger" size="small">紧急</el-tag>
          <el-tag v-else-if="viewForm.priority === 'important'" type="warning" size="small">重要</el-tag>
          <el-tag v-else type="info" size="small">普通</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发送者">
          {{ viewForm.senderName || '系统' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ parseTime(viewForm.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="通知内容">
          <div v-html="viewForm.content" style="white-space: pre-wrap;"></div>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listNotification,
  getNotification,
  markAsRead,
  batchMarkAsRead,
  markAllAsRead,
  delNotification
} from '@/api/system/notification'
import Pagination from '@/components/Pagination'

export default {
  name: 'Notification',
  components: {
    Pagination
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 通知列表
      notificationList: [],
      // 查看对话框
      viewOpen: false,
      // 查看表单
      viewForm: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        type: undefined,
        status: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询通知列表 */
    getList() {
      this.loading = true
      listNotification(this.queryParams).then(response => {
        this.notificationList = response.data
        this.total = response.total
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.multiple = !selection.length
    },
    /** 查看通知 */
    handleView(row) {
      const id = row.id
      getNotification(id).then(response => {
        this.viewForm = response.data
        this.viewOpen = true
        // 刷新列表（因为查看会自动标记为已读）
        this.getList()
      })
    },
    /** 标记已读 */
    handleMarkAsRead() {
      const ids = this.ids
      if (ids.length === 0) {
        this.$message.warning('请选择要标记的通知')
        return
      }
      batchMarkAsRead(ids).then(() => {
        this.$message.success('标记成功')
        this.getList()
      })
    },
    /** 全部标记为已读 */
    handleMarkAllAsRead() {
      this.$confirm('确认将所有未读消息标记为已读吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return markAllAsRead()
      }).then(() => {
        this.$message.success('标记成功')
        this.getList()
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids
      if (ids.length === 0) {
        this.$message.warning('请选择要删除的通知')
        return
      }
      this.$confirm('是否确认删除选中的通知?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delNotification(ids)
      }).then(() => {
        this.$message.success('删除成功')
        this.getList()
      })
    }
  }
}
</script>

<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <!-- 操作按钮 -->
    <TableToolbar show-refresh @refresh="getList">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-check" size="mini" :disabled="multiple" @click="handleMarkAsRead">标记已读</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-finished" size="mini" @click="handleMarkAllAsRead">全部已读</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
    </TableToolbar>

    <!-- 通知列表 -->
    <el-table v-loading="loading" :data="notificationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="优先级" align="center" width="80">
        <template slot-scope="scope">
          <DictTag :options="priorityOptions" :value="scope.row.priority || 'normal'" />
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
          <DictTag :options="priorityOptions" :value="viewForm.priority || 'normal'" />
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
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'
import DictTag from '@/components/DictTag'

const TYPE_OPTIONS = [
  { value: 'system', label: '系统通知' },
  { value: 'todo', label: '待办提醒' },
  { value: 'approval', label: '审批消息' },
  { value: 'announce', label: '公告' }
]
const STATUS_OPTIONS = [
  { value: 'unread', label: '未读', type: 'danger' },
  { value: 'read', label: '已读', type: 'info' }
]
const PRIORITY_OPTIONS = [
  { value: 'urgent', label: '紧急', type: 'danger' },
  { value: 'important', label: '重要', type: 'warning' },
  { value: 'normal', label: '普通', type: 'info' }
]

export default {
  name: 'Notification',
  components: {
    Pagination,
    SearchForm,
    TableToolbar,
    DictTag
  },
  data() {
    return {
      searchFields: [
        { prop: 'title', label: '通知标题', type: 'input' },
        { prop: 'type', label: '通知类型', type: 'select', options: TYPE_OPTIONS, placeholder: '通知类型' },
        { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '状态' }
      ],
      statusOptions: STATUS_OPTIONS,
      priorityOptions: PRIORITY_OPTIONS,
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
    /** 重置按钮操作（SearchForm 已自动清字段） */
    resetQuery() {
      this.queryParams.pageNum = 1
      this.queryParams.pageSize = 10
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

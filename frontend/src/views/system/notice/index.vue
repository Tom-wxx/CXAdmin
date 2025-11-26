<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="公告标题" prop="noticeTitle">
        <el-input
          v-model="queryParams.noticeTitle"
          placeholder="请输入公告标题"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" placeholder="公告类型" clearable style="width: 200px">
          <el-option label="通知" value="1" />
          <el-option label="公告" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="公告状态" clearable style="width: 200px">
          <el-option label="正常" value="0" />
          <el-option label="关闭" value="1" />
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
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="noticeList" border>
      <el-table-column label="公告编号" align="center" prop="noticeId" width="100" />
      <el-table-column label="公告标题" align="center" prop="noticeTitle" show-overflow-tooltip />
      <el-table-column label="公告类型" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.noticeType === '1'" type="success" size="small">通知</el-tag>
          <el-tag v-else-if="scope.row.noticeType === '2'" type="warning" size="small">公告</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="info" size="small">关闭</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
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
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
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
      <el-form ref="noticeForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" maxlength="50" />
        </el-form-item>
        <el-form-item label="公告类型" prop="noticeType">
          <el-radio-group v-model="form.noticeType">
            <el-radio label="1">通知</el-radio>
            <el-radio label="2">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="noticeContent">
          <el-input v-model="form.noticeContent" type="textarea" :rows="6" placeholder="请输入公告内容" />
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

    <!-- 查看详情对话框 -->
    <el-dialog title="公告详情" :visible.sync="viewDialogVisible" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告标题">{{ viewForm.noticeTitle }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag v-if="viewForm.noticeType === '1'" type="success" size="small">通知</el-tag>
          <el-tag v-else-if="viewForm.noticeType === '2'" type="warning" size="small">公告</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="viewForm.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="info" size="small">关闭</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建者">{{ viewForm.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ parseTime(viewForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="公告内容" :span="2">
          <div style="white-space: pre-wrap;">{{ viewForm.noticeContent }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewForm.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNotice, getNotice, addNotice, updateNotice, delNotice } from '@/api/system/notice'
import Pagination from '@/components/Pagination'

export default {
  name: 'Notice',
  components: {
    Pagination
  },
  data() {
    return {
      // 加载状态
      loading: true,
      // 通知公告列表
      noticeList: [],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        current: 1,
        size: 10,
        noticeTitle: undefined,
        noticeType: undefined,
        status: undefined
      },
      // 对话框标题
      dialogTitle: '',
      // 对话框显示状态
      dialogVisible: false,
      // 查看详情对话框显示状态
      viewDialogVisible: false,
      // 表单数据
      form: {},
      // 查看详情数据
      viewForm: {},
      // 表单校验规则
      rules: {
        noticeTitle: [
          { required: true, message: '公告标题不能为空', trigger: 'blur' },
          { max: 50, message: '公告标题长度不能超过50个字符', trigger: 'blur' }
        ],
        noticeType: [
          { required: true, message: '公告类型不能为空', trigger: 'change' }
        ],
        noticeContent: [
          { required: true, message: '公告内容不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询通知公告列表 */
    getList() {
      this.loading = true
      listNotice(this.queryParams).then(response => {
        this.noticeList = response.rows
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
        noticeTitle: undefined,
        noticeType: undefined,
        status: undefined
      }
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.dialogTitle = '添加通知公告'
      this.dialogVisible = true
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const noticeId = row.noticeId
      getNotice(noticeId).then(response => {
        this.form = response.data
        this.dialogTitle = '修改通知公告'
        this.dialogVisible = true
      })
    },
    /** 查看按钮操作 */
    handleView(row) {
      const noticeId = row.noticeId
      getNotice(noticeId).then(response => {
        this.viewForm = response.data
        this.viewDialogVisible = true
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs.noticeForm.validate(valid => {
        if (valid) {
          if (this.form.noticeId) {
            updateNotice(this.form).then(response => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.getList()
            })
          } else {
            addNotice(this.form).then(response => {
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
        noticeId: undefined,
        noticeTitle: undefined,
        noticeType: '1',
        noticeContent: undefined,
        status: '0',
        remark: undefined
      }
      if (this.$refs.noticeForm) {
        this.$refs.noticeForm.resetFields()
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除公告标题为"' + row.noticeTitle + '"的数据项？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delNotice(row.noticeId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
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

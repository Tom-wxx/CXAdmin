<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="表名称" prop="tableName">
        <el-input
          v-model="queryParams.tableName"
          placeholder="请输入表名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="tableList"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center">
        <template slot-scope="scope">
          <span>{{ (queryParams.current - 1) * queryParams.size + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="表名称" align="center" prop="tableName" show-overflow-tooltip />
      <el-table-column label="表注释" align="center" prop="tableComment" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="300" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
          >预览</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleEdit(scope.row)"
          >编辑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-download"
            @click="handleGenerate(scope.row)"
          >生成代码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      :current-page.sync="queryParams.current"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="queryParams.size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 15px; text-align: right"
    />

    <!-- 预览对话框 -->
    <el-dialog title="表结构预览" :visible.sync="previewDialogVisible" width="800px" append-to-body>
      <el-descriptions :column="2" border v-if="tableInfo">
        <el-descriptions-item label="表名称">{{ tableInfo.tableName }}</el-descriptions-item>
        <el-descriptions-item label="表注释">{{ tableInfo.tableComment }}</el-descriptions-item>
        <el-descriptions-item label="类名">{{ tableInfo.className }}</el-descriptions-item>
        <el-descriptions-item label="主键">{{ tableInfo.primaryKey ? tableInfo.primaryKey.columnName : '' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>列信息</el-divider>

      <el-table :data="tableInfo ? tableInfo.columns : []" border>
        <el-table-column label="列名" prop="columnName" />
        <el-table-column label="类型" prop="columnType" width="100" />
        <el-table-column label="注释" prop="columnComment" show-overflow-tooltip />
        <el-table-column label="Java类型" prop="javaType" width="100" />
        <el-table-column label="Java属性" prop="propertyName" />
        <el-table-column label="主键" width="80" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isPrimaryKey" type="success" size="small">是</el-tag>
            <el-tag v-else type="info" size="small">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="必填" width="80" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isRequired" type="warning" size="small">是</el-tag>
            <el-tag v-else type="info" size="small">否</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 生成配置对话框 -->
    <el-dialog title="生成配置" :visible.sync="configDialogVisible" width="600px" append-to-body>
      <el-form ref="configForm" :model="configForm" :rules="configRules" label-width="120px">
        <el-form-item label="表名" prop="tableName">
          <el-input v-model="configForm.tableName" disabled />
        </el-form-item>
        <el-form-item label="包名" prop="packageName">
          <el-input v-model="configForm.packageName" placeholder="请输入包名" />
        </el-form-item>
        <el-form-item label="模块名" prop="moduleName">
          <el-input v-model="configForm.moduleName" placeholder="请输入模块名，如：system" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="configForm.author" placeholder="请输入作者名称" />
        </el-form-item>
        <el-form-item label="表前缀" prop="tablePrefix">
          <el-input v-model="configForm.tablePrefix" placeholder="请输入表前缀，如：sys_" />
        </el-form-item>
        <el-form-item label="上级菜单">
          <el-cascader
            v-model="configForm.parentMenuId"
            :options="menuOptions"
            :props="{ checkStrictly: true, value: 'menuId', label: 'menuName', children: 'children', emitPath: false }"
            placeholder="选择上级菜单"
            clearable
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="configDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitGenerate">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTable, getTable, generateCode } from '@/api/tool/generator'
import { listMenu } from '@/api/system/menu'

export default {
  name: 'Generator',
  data() {
    return {
      // 加载状态
      loading: true,
      // 表数据列表
      tableList: [],
      // 总数
      total: 0,
      // 选中的表
      selectedTables: [],
      // 查询参数
      queryParams: {
        tableName: undefined,
        current: 1,
        size: 10
      },
      // 预览对话框显示状态
      previewDialogVisible: false,
      // 表详细信息
      tableInfo: null,
      // 配置对话框显示状态
      configDialogVisible: false,
      // 菜单树选项
      menuOptions: [],
      // 配置表单
      configForm: {
        tableName: '',
        packageName: 'com.admin.system',
        moduleName: '',
        author: 'Admin',
        tablePrefix: 'sys_',
        parentMenuId: 0
      },
      // 表单验证规则
      configRules: {
        tableName: [
          { required: true, message: '表名不能为空', trigger: 'blur' }
        ],
        packageName: [
          { required: true, message: '包名不能为空', trigger: 'blur' }
        ],
        moduleName: [
          { required: true, message: '模块名不能为空', trigger: 'blur' }
        ],
        author: [
          { required: true, message: '作者不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getMenuTree()
  },
  methods: {
    /** 查询表列表 */
    getList() {
      this.loading = true
      listTable(this.queryParams).then(response => {
        this.tableList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /** 获取菜单树 */
    getMenuTree() {
      listMenu().then(response => {
        const menus = response.data || []
        this.menuOptions = [{ menuId: 0, menuName: '主目录', children: this.buildMenuTree(menus) }]
      })
    },
    /** 构建菜单树 */
    buildMenuTree(menus) {
      const menuMap = {}
      const tree = []
      menus.forEach(menu => {
        menuMap[menu.menuId] = { ...menu, children: [] }
      })
      menus.forEach(menu => {
        if (menu.parentId === 0) {
          tree.push(menuMap[menu.menuId])
        } else if (menuMap[menu.parentId]) {
          menuMap[menu.parentId].children.push(menuMap[menu.menuId])
        }
      })
      return tree
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        tableName: undefined,
        current: 1,
        size: 10
      }
      this.handleQuery()
    },
    /** 每页条数改变 */
    handleSizeChange(size) {
      this.queryParams.size = size
      this.queryParams.current = 1
      this.getList()
    },
    /** 当前页改变 */
    handleCurrentChange(current) {
      this.queryParams.current = current
      this.getList()
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.selectedTables = selection.map(item => item.tableName)
    },
    /** 预览按钮操作 */
    handlePreview(row) {
      const tableName = row.tableName
      getTable(tableName).then(response => {
        this.tableInfo = response.data
        this.previewDialogVisible = true
      }).catch(error => {
        console.error('预览失败:', error)
        this.$message.error('获取表信息失败')
      })
    },
    /** 新增按钮操作 - 跳转到新页面 */
    handleAdd() {
      this.$router.push('/tool/generator/edit')
    },
    /** 编辑按钮操作 - 跳转到编辑页面 */
    handleEdit(row) {
      this.$router.push({
        path: '/tool/generator/edit',
        query: { tableName: row.tableName }
      })
    },
    /** 生成代码按钮操作 */
    handleGenerate(row) {
      this.configForm.tableName = row.tableName
      this.configForm.moduleName = ''
      this.configDialogVisible = true
    },
    /** 提交生成 */
    submitGenerate() {
      this.$refs['configForm'].validate(valid => {
        if (valid) {
          generateCode(this.configForm).then(response => {
            this.downloadFile(response, this.configForm.tableName + '.zip')
            this.$message.success('生成成功')
            this.configDialogVisible = false
          })
        }
      })
    },
    /** 下载文件 */
    downloadFile(data, fileName) {
      const blob = new Blob([data], { type: 'application/zip' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = fileName
      link.click()
      window.URL.revokeObjectURL(url)
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

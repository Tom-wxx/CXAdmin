<template>
  <div class="app-container">
    <SearchForm :model="queryParams" :fields="searchFields" @search="handleQuery" @reset="resetQuery" />

    <TableToolbar show-add @add="handleAdd" />

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
          <el-button size="mini" type="text" icon="el-icon-view" @click="handlePreview(scope.row)">预览</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-download" @click="handleGenerate(scope.row)">生成代码</el-button>
        </template>
      </el-table-column>
    </el-table>

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
import SearchForm from '@/components/SearchForm'
import TableToolbar from '@/components/TableToolbar'

export default {
  name: 'Generator',
  components: { SearchForm, TableToolbar },
  data() {
    return {
      searchFields: [
        { prop: 'tableName', label: '表名称', type: 'input', placeholder: '请输入表名称' }
      ],
      loading: true,
      tableList: [],
      total: 0,
      selectedTables: [],
      queryParams: {
        tableName: undefined,
        current: 1,
        size: 10
      },
      previewDialogVisible: false,
      tableInfo: null,
      configDialogVisible: false,
      menuOptions: [],
      configForm: {
        tableName: '',
        packageName: 'com.admin.system',
        moduleName: '',
        author: 'Admin',
        tablePrefix: 'sys_',
        parentMenuId: 0
      },
      configRules: {
        tableName: [{ required: true, message: '表名不能为空', trigger: 'blur' }],
        packageName: [{ required: true, message: '包名不能为空', trigger: 'blur' }],
        moduleName: [{ required: true, message: '模块名不能为空', trigger: 'blur' }],
        author: [{ required: true, message: '作者不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getMenuTree()
  },
  methods: {
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
    getMenuTree() {
      listMenu().then(response => {
        const menus = response.data || []
        this.menuOptions = [{ menuId: 0, menuName: '主目录', children: this.buildMenuTree(menus) }]
      })
    },
    buildMenuTree(menus) {
      const menuMap = {}
      const tree = []
      menus.forEach(menu => { menuMap[menu.menuId] = { ...menu, children: [] } })
      menus.forEach(menu => {
        if (menu.parentId === 0) tree.push(menuMap[menu.menuId])
        else if (menuMap[menu.parentId]) menuMap[menu.parentId].children.push(menuMap[menu.menuId])
      })
      return tree
    },
    handleQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.current = 1
      this.getList()
    },
    handleSizeChange(size) {
      this.queryParams.size = size
      this.queryParams.current = 1
      this.getList()
    },
    handleCurrentChange(current) {
      this.queryParams.current = current
      this.getList()
    },
    handleSelectionChange(selection) {
      this.selectedTables = selection.map(item => item.tableName)
    },
    handlePreview(row) {
      getTable(row.tableName).then(response => {
        this.tableInfo = response.data
        this.previewDialogVisible = true
      }).catch(() => {
        this.$message.error('获取表信息失败')
      })
    },
    handleAdd() {
      this.$router.push('/tool/generator/edit')
    },
    handleEdit(row) {
      this.$router.push({ path: '/tool/generator/edit', query: { tableName: row.tableName } })
    },
    handleGenerate(row) {
      this.configForm.tableName = row.tableName
      this.configForm.moduleName = ''
      this.configDialogVisible = true
    },
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

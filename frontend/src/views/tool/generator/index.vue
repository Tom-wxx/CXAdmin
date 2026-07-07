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
        <template #default="scope">
          <span>{{ (queryParams.current - 1) * queryParams.size + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="表名称" align="center" prop="tableName" show-overflow-tooltip />
      <el-table-column label="表注释" align="center" prop="tableComment" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="300" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="small" type="text" icon="View" @click="handlePreview(scope.row)">预览</el-button>
          <el-button size="small" type="text" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="text" icon="Download" @click="handleGenerate(scope.row)">生成代码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0"
      v-model:current-page="queryParams.current"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="queryParams.size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 15px; text-align: right"
    />

    <el-dialog title="表结构预览" v-model="previewDialogVisible" width="800px" append-to-body>
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
          <template #default="scope">
            <el-tag v-if="scope.row.isPrimaryKey" type="success" size="small">是</el-tag>
            <el-tag v-else type="info" size="small">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="必填" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.isRequired" type="warning" size="small">是</el-tag>
            <el-tag v-else type="info" size="small">否</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <template #footer><div class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关 闭</el-button>
      </div></template>
    </el-dialog>

    <el-dialog title="生成配置" v-model="configDialogVisible" width="600px" append-to-body>
      <el-form ref="configFormRef" :model="configForm" :rules="configRules" label-width="120px">
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
      <template #footer><div class="dialog-footer">
        <el-button @click="configDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitGenerate">确 定</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listTable, getTable, generateCode } from '@/api/tool/generator'
import { listMenu } from '@/api/system/menu'
import type { GenTable, GenTableQuery } from '@/types/tool/generator'
import type { Menu } from '@/types/system/menu'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'

defineOptions({ name: 'Generator' })

/** 表结构预览列（GenTable 索引签名较宽松，弹窗渲染需要的字段在此收窄） */
interface GenTablePreviewColumn {
  columnName?: string
  columnType?: string
  columnComment?: string
  javaType?: string
  propertyName?: string
  isPrimaryKey?: boolean
  isRequired?: boolean
}

/** 表结构预览详情 */
interface GenTablePreview extends GenTable {
  primaryKey?: GenTablePreviewColumn
  columns?: GenTablePreviewColumn[]
}

/** 生成配置表单 */
interface ConfigForm {
  tableName: string
  packageName: string
  moduleName: string
  author: string
  tablePrefix: string
  parentMenuId: number
}

const router = useRouter()

const searchFields = [
  { prop: 'tableName', label: '表名称', type: 'input', placeholder: '请输入表名称' }
]

const loading = ref(true)
const tableList = ref<GenTable[]>([])
const total = ref(0)
const selectedTables = ref<string[]>([])
const queryParams = reactive<GenTableQuery & { current: number; size: number }>({
  tableName: undefined,
  current: 1,
  size: 10
})

const previewDialogVisible = ref(false)
const tableInfo = ref<GenTablePreview | null>(null)

const configDialogVisible = ref(false)
const menuOptions = ref<Menu[]>([])
const configFormRef = ref<FormInstance>()
const configForm = reactive<ConfigForm>({
  tableName: '',
  packageName: 'com.admin.system',
  moduleName: '',
  author: 'Admin',
  tablePrefix: 'sys_',
  parentMenuId: 0
})

const configRules = reactive<FormRules>({
  tableName: [{ required: true, message: '表名不能为空', trigger: 'blur' }],
  packageName: [{ required: true, message: '包名不能为空', trigger: 'blur' }],
  moduleName: [{ required: true, message: '模块名不能为空', trigger: 'blur' }],
  author: [{ required: true, message: '作者不能为空', trigger: 'blur' }]
})

function getList() {
  loading.value = true
  listTable(queryParams).then(response => {
    tableList.value = response.rows
    total.value = response.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function getMenuTree() {
  listMenu().then(response => {
    const menus = response.data || []
    menuOptions.value = [{ menuId: 0, menuName: '主目录', children: buildMenuTree(menus) }]
  })
}

function buildMenuTree(menus: Menu[]): Menu[] {
  const menuMap: Record<number, Menu> = {}
  const tree: Menu[] = []
  menus.forEach(menu => {
    menuMap[menu.menuId as number] = { ...menu, children: [] } // menuId 由后端返回，保证非空
  })
  menus.forEach(menu => {
    if (menu.parentId === 0) {
      tree.push(menuMap[menu.menuId as number])
    } else if (menu.parentId !== undefined && menuMap[menu.parentId]) {
      menuMap[menu.parentId].children!.push(menuMap[menu.menuId as number])
    }
  })
  return tree
}

function handleQuery() {
  queryParams.current = 1
  getList()
}

function resetQuery() {
  queryParams.current = 1
  getList()
}

function handleSizeChange(size: number) {
  queryParams.size = size
  queryParams.current = 1
  getList()
}

function handleCurrentChange(current: number) {
  queryParams.current = current
  getList()
}

function handleSelectionChange(selection: GenTable[]) {
  selectedTables.value = selection.map(item => item.tableName as string) // 列表行 tableName 必有
}

function handlePreview(row: GenTable) {
  getTable(row.tableName as string).then(response => {
    tableInfo.value = response.data as GenTablePreview
    previewDialogVisible.value = true
  }).catch(() => {
    ElMessage.error('获取表信息失败')
  })
}

function handleAdd() {
  router.push('/tool/generator/edit')
}

function handleEdit(row: GenTable) {
  router.push({ path: '/tool/generator/edit', query: { tableName: row.tableName as string } })
}

function handleGenerate(row: GenTable) {
  configForm.tableName = row.tableName as string
  configForm.moduleName = ''
  configDialogVisible.value = true
}

function submitGenerate() {
  configFormRef.value?.validate(valid => {
    if (valid) {
      generateCode(configForm).then(response => {
        downloadFile(response, configForm.tableName + '.zip')
        ElMessage.success('生成成功')
        configDialogVisible.value = false
      })
    }
  })
}

function downloadFile(data: Blob, fileName: string) {
  const blob = new Blob([data], { type: 'application/zip' })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.click()
  window.URL.revokeObjectURL(url)
}

getList()
getMenuTree()
</script>

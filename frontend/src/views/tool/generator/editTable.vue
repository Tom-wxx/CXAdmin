<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header><div class="clearfix">
        <span>{{ isEdit ? '编辑表' : '新增表' }}</span>
        <el-button style="float: right" type="text" @click="goBack">返回</el-button>
      </div></template>

      <!-- 基本信息 -->
      <el-form ref="tableFormRef" :model="tableForm" :rules="tableRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="表名称" prop="tableName">
              <el-input v-model="tableForm.tableName" placeholder="请输入表名称，如：sys_user" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="表注释" prop="tableComment">
              <el-input v-model="tableForm.tableComment" placeholder="请输入表注释，如：用户信息" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="类名" prop="className">
              <el-input v-model="tableForm.className" placeholder="自动生成，如：SysUser" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="包名" prop="packageName">
              <el-input v-model="tableForm.packageName" placeholder="com.admin.system" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="模块名" prop="moduleName">
              <el-input v-model="tableForm.moduleName" placeholder="请输入模块名，如：system" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="作者" prop="author">
              <el-input v-model="tableForm.author" placeholder="Admin" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="表前缀" prop="tablePrefix">
              <el-input v-model="tableForm.tablePrefix" placeholder="sys_" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="上级菜单">
              <el-cascader
                v-model="tableForm.parentMenuId"
                :options="menuOptions"
                :props="{ checkStrictly: true, value: 'menuId', label: 'menuName', children: 'children', emitPath: false }"
                placeholder="选择上级菜单"
                clearable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 字段信息 -->
      <el-divider content-position="left">字段信息</el-divider>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" icon="Plus" size="small" @click="handleAddColumn">添加字段</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" icon="Plus" size="small" @click="handleAddCommonColumns">添加公共字段</el-button>
        </el-col>
      </el-row>

      <el-table :data="tableForm.columns" border style="width: 100%">
        <el-table-column label="字段名" width="140">
          <template #default="scope">
            <el-input v-model="scope.row.columnName" size="small" placeholder="user_id" />
          </template>
        </el-table-column>
        <el-table-column label="字段注释" width="120">
          <template #default="scope">
            <el-input v-model="scope.row.columnComment" size="small" placeholder="用户ID" />
          </template>
        </el-table-column>
        <el-table-column label="字段类型" width="130">
          <template #default="scope">
            <el-select v-model="scope.row.columnType" size="small" placeholder="类型" @change="handleColumnTypeChange(scope.row)" filterable>
              <el-option-group label="整数类型">
                <el-option label="bigint" value="bigint" />
                <el-option label="int" value="int" />
                <el-option label="smallint" value="smallint" />
                <el-option label="tinyint" value="tinyint" />
              </el-option-group>
              <el-option-group label="浮点类型">
                <el-option label="decimal" value="decimal" />
                <el-option label="double" value="double" />
                <el-option label="float" value="float" />
              </el-option-group>
              <el-option-group label="字符串类型">
                <el-option label="varchar" value="varchar" />
                <el-option label="char" value="char" />
                <el-option label="text" value="text" />
                <el-option label="longtext" value="longtext" />
              </el-option-group>
              <el-option-group label="日期时间类型">
                <el-option label="datetime" value="datetime" />
                <el-option label="date" value="date" />
                <el-option label="timestamp" value="timestamp" />
              </el-option-group>
              <el-option-group label="其他类型">
                <el-option label="blob" value="blob" />
                <el-option label="bit" value="bit" />
                <el-option label="json" value="json" />
              </el-option-group>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="Java类型" width="110">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.javaType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Java属性" width="120">
          <template #default="scope">
            <el-input v-model="scope.row.propertyName" size="small" placeholder="自动生成" />
          </template>
        </el-table-column>
        <el-table-column label="主键" width="50" align="center">
          <template #default="scope">
            <el-checkbox v-model="scope.row.isPrimaryKey" @change="handlePrimaryKeyChange(scope.$index)" />
          </template>
        </el-table-column>
        <el-table-column label="必填" width="50" align="center">
          <template #default="scope">
            <el-checkbox v-model="scope.row.isRequired" />
          </template>
        </el-table-column>
        <el-table-column label="列表" width="50" align="center">
          <template #default="scope">
            <el-checkbox v-model="scope.row.isList" />
          </template>
        </el-table-column>
        <el-table-column label="查询" width="50" align="center">
          <template #default="scope">
            <el-checkbox v-model="scope.row.isQuery" />
          </template>
        </el-table-column>
        <el-table-column label="查询方式" width="100">
          <template #default="scope">
            <el-select v-model="scope.row.queryType" size="small" :disabled="!scope.row.isQuery">
              <el-option label="=" value="EQ" />
              <el-option label="!=" value="NE" />
              <el-option label=">" value="GT" />
              <el-option label=">=" value="GE" />
              <el-option label="<" value="LT" />
              <el-option label="<=" value="LE" />
              <el-option label="LIKE" value="LIKE" />
              <el-option label="BETWEEN" value="BETWEEN" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="表单显示" width="50" align="center">
          <template #default="scope">
            <el-checkbox v-model="scope.row.isEdit" />
          </template>
        </el-table-column>
        <el-table-column label="显示类型" width="120">
          <template #default="scope">
            <el-select v-model="scope.row.htmlType" size="small" :disabled="!scope.row.isEdit">
              <el-option label="文本框" value="input" />
              <el-option label="文本域" value="textarea" />
              <el-option label="下拉框" value="select" />
              <el-option label="单选框" value="radio" />
              <el-option label="复选框" value="checkbox" />
              <el-option label="日期控件" value="datetime" />
              <el-option label="图片上传" value="imageUpload" />
              <el-option label="文件上传" value="fileUpload" />
              <el-option label="富文本" value="editor" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="text" icon="Delete" @click="handleDeleteColumn(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 底部按钮 -->
      <div style="margin-top: 20px; text-align: center">
        <el-button type="primary" icon="Download" @click="handleGenerate">生成代码</el-button>
        <el-button icon="Close" @click="goBack">取消</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { generateCustomCode } from '@/api/tool/generator'
import { listMenu } from '@/api/system/menu'
import type { Menu } from '@/types/system/menu'

defineOptions({ name: 'EditTable' })

/** 自定义建表 - 字段信息行 */
interface GenTableColumn {
  columnName: string
  columnComment: string
  columnType: string
  javaType: string
  propertyName: string
  isPrimaryKey: boolean
  isRequired: boolean
  isList: boolean
  isQuery: boolean
  isEdit: boolean
  queryType: string
  htmlType: string
  /** 生成代码前临时补充的首字母大写属性名 */
  capitalizedPropertyName?: string
}

/** 自定义建表 - 表单数据 */
interface TableForm {
  tableName: string
  tableComment: string
  className: string
  packageName: string
  moduleName: string
  author: string
  tablePrefix: string
  parentMenuId: number
  columns: GenTableColumn[]
}

const router = useRouter()

// 是否编辑模式
const isEdit = ref(false)
// 菜单树选项
const menuOptions = ref<Menu[]>([])
// 表单 DOM ref（与下方 tableForm 数据同名会冲突，故加 Ref 后缀）
const tableFormRef = ref<FormInstance>()
// 表单数据
const tableForm = reactive<TableForm>({
  tableName: '',
  tableComment: '',
  className: '',
  packageName: 'com.admin.system',
  moduleName: '',
  author: 'Admin',
  tablePrefix: 'sys_',
  parentMenuId: 0,
  columns: []
})
// 表单验证规则
const tableRules = reactive<FormRules>({
  tableName: [
    { required: true, message: '表名称不能为空', trigger: 'blur' }
  ],
  tableComment: [
    { required: true, message: '表注释不能为空', trigger: 'blur' }
  ],
  moduleName: [
    { required: true, message: '模块名不能为空', trigger: 'blur' }
  ]
})

watch(() => tableForm.tableName, (val) => {
  if (val) {
    tableForm.className = tableToClassName(val, tableForm.tablePrefix)
  }
})

watch(() => tableForm.tablePrefix, () => {
  if (tableForm.tableName) {
    tableForm.className = tableToClassName(tableForm.tableName, tableForm.tablePrefix)
  }
})

/** 获取菜单树 */
function getMenuTree() {
  listMenu().then(response => {
    const menus = response.data || []
    menuOptions.value = [{ menuId: 0, menuName: '主目录', children: buildMenuTree(menus) }]
  })
}

/** 构建菜单树 */
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

/** 初始化默认字段 */
function initDefaultColumns() {
  tableForm.columns = [
    createColumn('id', '主键ID', 'bigint', 'Long', true, true, true, false, false, 'EQ', false, 'input')
  ]
}

/** 创建字段对象（isFormRequired 保留原字段位以维持调用点参数顺序，当前未使用） */
function createColumn(
  columnName: string,
  columnComment: string,
  columnType: string,
  javaType: string,
  isPrimaryKey: boolean,
  isRequired: boolean,
  isList: boolean,
  isQuery: boolean,
  isEdit: boolean,
  queryType: string,
  isFormRequired: boolean,
  htmlType: string
): GenTableColumn {
  return {
    columnName: columnName,
    columnComment: columnComment,
    columnType: columnType,
    javaType: javaType,
    propertyName: columnToProperty(columnName),
    isPrimaryKey: isPrimaryKey,
    isRequired: isRequired,
    isList: isList,
    isQuery: isQuery,
    isEdit: isEdit,
    queryType: queryType,
    htmlType: htmlType
  }
}

/** 添加字段 */
function handleAddColumn() {
  tableForm.columns.push(createColumn('', '', 'varchar', 'String', false, false, true, false, true, 'EQ', false, 'input'))
}

/** 添加公共字段 */
function handleAddCommonColumns() {
  const commonColumns: GenTableColumn[] = [
    createColumn('create_by', '创建者', 'varchar', 'String', false, false, false, false, false, 'EQ', false, 'input'),
    createColumn('create_time', '创建时间', 'datetime', 'Date', false, false, true, false, false, 'EQ', false, 'datetime'),
    createColumn('update_by', '更新者', 'varchar', 'String', false, false, false, false, false, 'EQ', false, 'input'),
    createColumn('update_time', '更新时间', 'datetime', 'Date', false, false, false, false, false, 'EQ', false, 'datetime'),
    createColumn('remark', '备注', 'varchar', 'String', false, false, false, false, true, 'EQ', false, 'textarea'),
    createColumn('deleted', '删除标志', 'int', 'Integer', false, false, false, false, false, 'EQ', false, 'input')
  ]
  // 检查是否已存在
  const existingNames = tableForm.columns.map(c => c.columnName)
  commonColumns.forEach(col => {
    if (!existingNames.includes(col.columnName)) {
      tableForm.columns.push(col)
    }
  })
  ElMessage.success('已添加公共字段')
}

/** 删除字段 */
function handleDeleteColumn(index: number) {
  tableForm.columns.splice(index, 1)
}

/** 主键变更 */
function handlePrimaryKeyChange(index: number) {
  if (tableForm.columns[index].isPrimaryKey) {
    tableForm.columns.forEach((col, i) => {
      if (i !== index) {
        col.isPrimaryKey = false
      }
    })
  }
}

/** 字段类型变更 */
function handleColumnTypeChange(row: GenTableColumn) {
  const typeMap: Record<string, string> = {
    'bigint': 'Long', 'int': 'Integer', 'smallint': 'Integer', 'tinyint': 'Integer',
    'decimal': 'BigDecimal', 'double': 'Double', 'float': 'Float',
    'varchar': 'String', 'char': 'String', 'text': 'String', 'longtext': 'String',
    'datetime': 'Date', 'date': 'Date', 'timestamp': 'Date',
    'blob': 'byte[]', 'bit': 'Boolean', 'json': 'String'
  }
  row.javaType = typeMap[row.columnType] || 'String'
  // 自动设置显示类型
  if (row.columnType === 'text' || row.columnType === 'longtext') {
    row.htmlType = 'textarea'
  } else if (row.columnType === 'datetime' || row.columnType === 'date' || row.columnType === 'timestamp') {
    row.htmlType = 'datetime'
  }
}

/** 列名转属性名 */
function columnToProperty(columnName: string): string {
  if (!columnName) return ''
  const parts = columnName.toLowerCase().split('_')
  let result = ''
  for (let i = 0; i < parts.length; i++) {
    if (i === 0) {
      result += parts[i]
    } else {
      result += parts[i].charAt(0).toUpperCase() + parts[i].slice(1)
    }
  }
  return result
}

/** 表名转类名 */
function tableToClassName(tableName: string, tablePrefix: string): string {
  let name = tableName
  if (tablePrefix && name.startsWith(tablePrefix)) {
    name = name.substring(tablePrefix.length)
  }
  const parts = name.toLowerCase().split('_')
  let result = ''
  for (const part of parts) {
    result += part.charAt(0).toUpperCase() + part.slice(1)
  }
  return result
}

/** 生成代码 */
function handleGenerate() {
  tableFormRef.value?.validate(valid => {
    if (valid) {
      // 验证字段
      if (tableForm.columns.length === 0) {
        ElMessage.error('请至少添加一个字段')
        return
      }
      const hasPrimaryKey = tableForm.columns.some(col => col.isPrimaryKey)
      if (!hasPrimaryKey) {
        ElMessage.error('请设置一个主键字段')
        return
      }
      for (const col of tableForm.columns) {
        if (!col.columnName) {
          ElMessage.error('字段名不能为空')
          return
        }
        // 自动生成属性名
        if (!col.propertyName) {
          col.propertyName = columnToProperty(col.columnName)
        }
        col.capitalizedPropertyName = col.propertyName.charAt(0).toUpperCase() + col.propertyName.slice(1)
      }

      // 构建请求数据
      const tableInfo = {
        tableName: tableForm.tableName,
        tableComment: tableForm.tableComment,
        className: tableForm.className,
        classname: tableForm.className.charAt(0).toLowerCase() + tableForm.className.slice(1),
        columns: tableForm.columns,
        primaryKey: tableForm.columns.find(col => col.isPrimaryKey)
      }

      const data = {
        tableName: tableForm.tableName,
        packageName: tableForm.packageName,
        moduleName: tableForm.moduleName,
        author: tableForm.author,
        tablePrefix: tableForm.tablePrefix,
        parentMenuId: tableForm.parentMenuId,
        tableInfo: tableInfo
      }

      generateCustomCode(data).then(response => {
        downloadFile(response, tableForm.tableName + '.zip')
        ElMessage.success('生成成功')
      })
    }
  })
}

/** 下载文件 */
function downloadFile(data: Blob, fileName: string) {
  const blob = new Blob([data], { type: 'application/zip' })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.click()
  window.URL.revokeObjectURL(url)
}

/** 返回 */
function goBack() {
  router.push('/tool/generator')
}

getMenuTree()
initDefaultColumns()
</script>

<style scoped>
.box-card {
  margin: 10px;
}
.mb8 {
  margin-bottom: 8px;
}
</style>

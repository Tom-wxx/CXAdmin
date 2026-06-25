<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <!-- 工具栏 -->
    <TableToolbar show-add show-refresh @add="handleAdd" @refresh="getList" />

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="list" border>
      <el-table-column label="参数主键" align="center" prop="configId" width="100" />
      <el-table-column label="参数名称" align="center" prop="configName" show-overflow-tooltip />
      <el-table-column label="参数键名" align="center" prop="configKey" show-overflow-tooltip />
      <el-table-column label="参数键值" align="center" prop="configValue" show-overflow-tooltip />
      <el-table-column label="系统内置" align="center" width="100">
        <template #default="scope">
          <DictTag :options="configTypeOptions" :value="scope.row.configType" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            size="small"
            type="text"
            icon="Edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="small"
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
            :disabled="scope.row.configType === 'Y'"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <Pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="configFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入参数名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入参数键名" maxlength="100" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
          <el-radio-group v-model="form.configType">
            <el-radio value="Y">是</el-radio>
            <el-radio value="N">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listConfig, getConfig, addConfig, updateConfig, delConfig } from '@/api/system/config'
import { useCrudTable } from '@/composables'
import { parseTime } from '@/utils'
import type { Config, ConfigQuery } from '@/types/system/config'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'

defineOptions({ name: 'Config' })

const CONFIG_TYPE_OPTIONS = [
  { value: 'Y', label: '是', type: 'success' },
  { value: 'N', label: '否', type: 'info' }
]

const configTypeOptions = CONFIG_TYPE_OPTIONS

const searchFields = [
  { prop: 'configName', label: '参数名称', type: 'input' },
  { prop: 'configKey', label: '参数键名', type: 'input' },
  { prop: 'configType', label: '系统内置', type: 'select', options: CONFIG_TYPE_OPTIONS, placeholder: '系统内置' }
]

const { loading, list, total, queryParams, getList, handleQuery, resetQuery } =
  useCrudTable<Config, ConfigQuery>({
    listApi: listConfig,
    defaultQuery: { configName: undefined, configKey: undefined, configType: undefined }
  })

// 对话框
const dialogTitle = ref('')
const dialogVisible = ref(false)
const configFormRef = ref<FormInstance>()

const formDefaults: Config = {
  configId: undefined,
  configName: undefined,
  configKey: undefined,
  configValue: undefined,
  configType: 'N',
  remark: undefined
}

const form = reactive<Config>({ ...formDefaults })

const rules = reactive<FormRules>({
  configName: [
    { required: true, message: '参数名称不能为空', trigger: 'blur' },
    { max: 100, message: '参数名称长度不能超过100个字符', trigger: 'blur' }
  ],
  configKey: [
    { required: true, message: '参数键名不能为空', trigger: 'blur' },
    { max: 100, message: '参数键名长度不能超过100个字符', trigger: 'blur' }
  ],
  configValue: [
    { required: true, message: '参数键值不能为空', trigger: 'blur' },
    { max: 500, message: '参数键值长度不能超过500个字符', trigger: 'blur' }
  ]
})

function reset() {
  Object.assign(form, formDefaults)
  configFormRef.value?.resetFields()
}

function handleAdd() {
  reset()
  dialogTitle.value = '添加参数配置'
  dialogVisible.value = true
}

function handleUpdate(row: Config) {
  reset()
  getConfig(row.configId!).then(response => {
    Object.assign(form, response.data)
    dialogTitle.value = '修改参数配置'
    dialogVisible.value = true
  })
}

function submitForm() {
  configFormRef.value?.validate(valid => {
    if (valid) {
      if (form.configId) {
        updateConfig(form).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        })
      } else {
        addConfig(form).then(() => {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          getList()
        })
      }
    }
  })
}

function cancel() {
  dialogVisible.value = false
  reset()
}

function handleDelete(row: Config) {
  if (row.configType === 'Y') {
    ElMessage.warning('系统内置参数不能删除')
    return
  }
  ElMessageBox.confirm(
    '是否确认删除参数名称为"' + row.configName + '"的数据项？',
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    return delConfig(row.configId!)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
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

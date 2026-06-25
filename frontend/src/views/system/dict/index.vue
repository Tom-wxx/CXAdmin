<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 字典类型列表 -->
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><div>
            <span>字典类型</span>
            <el-button
              style="float: right; padding: 3px 10px"
              type="text"
              icon="Plus"
              @click="handleAddType"
            >新增</el-button>
          </div></template>
          <el-input
            v-model="typeQuery"
            placeholder="请输入字典名称"
            clearable
            size="small"
            prefix-icon="Search"
            style="margin-bottom: 10px"
            @keyup.enter="handleTypeSearch"
            @clear="handleTypeSearch"
          />
          <el-table
            v-loading="typeLoading"
            :data="typeList"
            highlight-current-row
            @current-change="handleTypeChange"
            style="width: 100%"
            max-height="500"
          >
            <el-table-column prop="dictName" label="字典名称" show-overflow-tooltip />
            <el-table-column prop="dictType" label="字典类型" show-overflow-tooltip />
            <el-table-column label="状态" width="60">
              <template #default="scope">
                <DictTag :options="statusOptions" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button
                  size="small"
                  type="text"
                  icon="Edit"
                  @click.stop="handleEditType(scope.row)"
                >修改</el-button>
                <el-button
                  size="small"
                  type="text"
                  icon="Delete"
                  @click.stop="handleDeleteType(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="typeTotal > typeQueryParams.size!"
            small
            layout="prev, pager, next"
            :total="typeTotal"
            :page-size="typeQueryParams.size"
            v-model:current-page="typeQueryParams.current"
            @current-change="handleTypePageChange"
            style="margin-top: 10px; text-align: center"
          />
        </el-card>
      </el-col>

      <!-- 字典数据列表 -->
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header><div>
            <span>字典数据</span>
            <span v-if="currentType" style="margin-left: 10px; color: #909399">
              ({{ currentType.dictName }} - {{ currentType.dictType }})
            </span>
            <el-button
              v-if="currentType"
              style="float: right; padding: 3px 10px"
              type="text"
              icon="Plus"
              @click="handleAddData"
            >新增</el-button>
          </div></template>
          <el-table
            v-loading="dataLoading"
            :data="dataList"
            style="width: 100%"
          >
            <el-table-column prop="dictLabel" label="字典标签" />
            <el-table-column prop="dictValue" label="字典键值" />
            <el-table-column prop="dictSort" label="排序" width="80" />
            <el-table-column label="状态" width="80">
              <template #default="scope">
                <DictTag :options="statusOptions" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button
                  size="small"
                  type="text"
                  icon="Edit"
                  @click="handleEditData(scope.row)"
                >修改</el-button>
                <el-button
                  size="small"
                  type="text"
                  icon="Delete"
                  @click="handleDeleteData(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="dataTotal > dataQueryParams.size!"
            small
            layout="prev, pager, next"
            :total="dataTotal"
            :page-size="dataQueryParams.size"
            v-model:current-page="dataQueryParams.current"
            @current-change="handleDataPageChange"
            style="margin-top: 10px; text-align: center"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 字典类型对话框 -->
    <el-dialog :title="typeDialogTitle" v-model="typeDialogVisible" width="600px">
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="100px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="typeForm.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="typeForm.dictType" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="typeForm.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="typeForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer><div>
        <el-button type="primary" @click="submitTypeForm">确 定</el-button>
        <el-button @click="typeDialogVisible = false">取 消</el-button>
      </div></template>
    </el-dialog>

    <!-- 字典数据对话框 -->
    <el-dialog :title="dataDialogTitle" v-model="dataDialogVisible" width="600px">
      <el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
        <el-form-item label="字典类型">
          <el-input v-model="dataForm.dictType" disabled />
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="dataForm.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典键值" prop="dictValue">
          <el-input v-model="dataForm.dictValue" placeholder="请输入字典键值" />
        </el-form-item>
        <el-form-item label="排序" prop="dictSort">
          <el-input-number v-model="dataForm.dictSort" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="样式属性" prop="cssClass">
          <el-input v-model="dataForm.cssClass" placeholder="请输入样式属性" />
        </el-form-item>
        <el-form-item label="回显样式" prop="listClass">
          <el-select v-model="dataForm.listClass" placeholder="请选择回显样式" clearable style="width: 100%">
            <el-option label="默认" value="default" />
            <el-option label="主要" value="primary" />
            <el-option label="成功" value="success" />
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="危险" value="danger" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dataForm.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dataForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer><div>
        <el-button type="primary" @click="submitDataForm">确 定</el-button>
        <el-button @click="dataDialogVisible = false">取 消</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listDictType, getDictType, addDictType, updateDictType, delDictType } from '@/api/system/dict/type'
import { listDictData, getDictData, addDictData, updateDictData, delDictData } from '@/api/system/dict/data'
import type { DictType, DictTypeQuery, DictData, DictDataQuery } from '@/types/system/dict'
import DictTag from '@/components/DictTag/index.vue'

defineOptions({ name: 'Dict' })

const STATUS_OPTIONS = [
  { value: '0', label: '正常', type: 'success' },
  { value: '1', label: '停用', type: 'danger' }
]

const statusOptions = STATUS_OPTIONS

// ─── 字典类型 ───────────────────────────────────────────────
const typeLoading = ref(false)
const typeList = ref<DictType[]>([])
const typeTotal = ref(0)
const typeQuery = ref('')
const typeQueryParams = reactive<DictTypeQuery>({ current: 1, size: 20 })
const currentType = ref<DictType | null>(null)

const typeDialogTitle = ref('')
const typeDialogVisible = ref(false)
const typeFormRef = ref<FormInstance>()

const typeFormDefaults: DictType = {
  dictId: undefined,
  dictName: undefined,
  dictType: undefined,
  status: '0',
  remark: undefined
}
const typeForm = reactive<DictType>({ ...typeFormDefaults })

const typeRules = reactive<FormRules>({
  dictName: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }],
  dictType: [{ required: true, message: '字典类型不能为空', trigger: 'blur' }]
})

function getTypeList() {
  typeLoading.value = true
  const params: DictTypeQuery = { ...typeQueryParams }
  if (typeQuery.value) {
    params.dictName = typeQuery.value
  }
  listDictType(params).then(response => {
    typeList.value = response.rows
    typeTotal.value = response.total
    typeLoading.value = false
  }).catch(() => {
    typeLoading.value = false
  })
}

function handleTypeSearch() {
  typeQueryParams.current = 1
  getTypeList()
}

function handleTypePageChange(page: number) {
  typeQueryParams.current = page
  getTypeList()
}

function handleTypeChange(row: DictType | null) {
  currentType.value = row
  if (row) {
    dataQueryParams.current = 1
    getDataList(row.dictType!)
  }
}

function handleAddType() {
  resetTypeForm()
  typeDialogTitle.value = '添加字典类型'
  typeDialogVisible.value = true
}

function handleEditType(row: DictType) {
  resetTypeForm()
  getDictType(row.dictId!).then(response => {
    Object.assign(typeForm, response.data)
    typeDialogTitle.value = '修改字典类型'
    typeDialogVisible.value = true
  })
}

function submitTypeForm() {
  typeFormRef.value?.validate(valid => {
    if (valid) {
      if (typeForm.dictId) {
        updateDictType(typeForm).then(() => {
          ElMessage.success('修改成功')
          typeDialogVisible.value = false
          getTypeList()
        })
      } else {
        addDictType(typeForm).then(() => {
          ElMessage.success('新增成功')
          typeDialogVisible.value = false
          getTypeList()
        })
      }
    }
  })
}

function handleDeleteType(row: DictType) {
  ElMessageBox.confirm(
    '是否确认删除字典类型"' + row.dictName + '"？',
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    return delDictType(row.dictId!)
  }).then(() => {
    getTypeList()
    ElMessage.success('删除成功')
    if (currentType.value && currentType.value.dictId === row.dictId) {
      currentType.value = null
      dataList.value = []
    }
  }).catch(() => {})
}

function resetTypeForm() {
  Object.assign(typeForm, typeFormDefaults)
  typeFormRef.value?.resetFields()
}

// ─── 字典数据 ───────────────────────────────────────────────
const dataLoading = ref(false)
const dataList = ref<DictData[]>([])
const dataTotal = ref(0)
const dataQueryParams = reactive<DictDataQuery>({ current: 1, size: 20, dictType: '' })

const dataDialogTitle = ref('')
const dataDialogVisible = ref(false)
const dataFormRef = ref<FormInstance>()

const dataFormDefaults: DictData = {
  dictCode: undefined,
  dictSort: 0,
  dictLabel: undefined,
  dictValue: undefined,
  dictType: undefined,
  cssClass: undefined,
  listClass: undefined,
  isDefault: 'N',
  status: '0',
  remark: undefined
}
const dataForm = reactive<DictData>({ ...dataFormDefaults })

const dataRules = reactive<FormRules>({
  dictLabel: [{ required: true, message: '字典标签不能为空', trigger: 'blur' }],
  dictValue: [{ required: true, message: '字典键值不能为空', trigger: 'blur' }]
})

function getDataList(dictType: string) {
  dataLoading.value = true
  dataQueryParams.dictType = dictType
  listDictData(dataQueryParams).then(response => {
    dataList.value = response.rows
    dataTotal.value = response.total
    dataLoading.value = false
  }).catch(() => {
    dataLoading.value = false
  })
}

function handleDataPageChange(page: number) {
  dataQueryParams.current = page
  getDataList(dataQueryParams.dictType!)
}

function handleAddData() {
  resetDataForm()
  dataForm.dictType = currentType.value!.dictType
  dataDialogTitle.value = '添加字典数据'
  dataDialogVisible.value = true
}

function handleEditData(row: DictData) {
  resetDataForm()
  getDictData(row.dictCode!).then(response => {
    Object.assign(dataForm, response.data)
    dataDialogTitle.value = '修改字典数据'
    dataDialogVisible.value = true
  })
}

function submitDataForm() {
  dataFormRef.value?.validate(valid => {
    if (valid) {
      if (dataForm.dictCode) {
        updateDictData(dataForm).then(() => {
          ElMessage.success('修改成功')
          dataDialogVisible.value = false
          getDataList(currentType.value!.dictType!)
        })
      } else {
        addDictData(dataForm).then(() => {
          ElMessage.success('新增成功')
          dataDialogVisible.value = false
          getDataList(currentType.value!.dictType!)
        })
      }
    }
  })
}

function handleDeleteData(row: DictData) {
  ElMessageBox.confirm(
    '是否确认删除字典数据"' + row.dictLabel + '"？',
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    return delDictData(row.dictCode!)
  }).then(() => {
    getDataList(currentType.value!.dictType!)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

function resetDataForm() {
  Object.assign(dataForm, dataFormDefaults)
  dataFormRef.value?.resetFields()
}

// 初始化加载字典类型
getTypeList()
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>

<template>
  <div class="app-container">
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <TableToolbar show-add show-refresh @add="handleAdd" @refresh="getList">
      <el-col :span="1.5">
        <el-button type="info" plain icon="Sort" size="small" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
    </TableToolbar>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="deptList"
      row-key="deptId"
      :default-expand-all="isExpandAll"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      border
    >
      <el-table-column prop="deptName" label="部门名称" width="260"></el-table-column>
      <el-table-column prop="orderNum" label="排序" width="80" align="center"></el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
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
            icon="Plus"
            @click="handleAdd(scope.row)"
          >新增</el-button>
          <el-button
            v-if="scope.row.parentId != 0"
            size="small"
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="deptOptions"
                :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                value-key="deptId"
                node-key="deptId"
                check-strictly
                clearable
                placeholder="选择上级部门"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门状态">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer><div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listDept, getDept, delDept, addDept, updateDept } from '@/api/system/dept'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'
import { parseTime } from '@/utils'
import type { Dept, DeptQuery } from '@/types/system/dept'

defineOptions({ name: 'Dept' })

const STATUS_OPTIONS = [
  { value: '0', label: '正常', type: 'success' },
  { value: '1', label: '停用', type: 'danger' }
]

const searchFields = [
  { prop: 'deptName', label: '部门名称', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '部门状态' }
]

const statusOptions = STATUS_OPTIONS

const loading = ref(true)
const deptList = ref<Dept[]>([])
const deptOptions = ref<Dept[]>([])
const title = ref('')
const open = ref(false)
const isExpandAll = ref(true)
const refreshTable = ref(true)
const queryParams = reactive<DeptQuery>({
  deptName: undefined,
  status: undefined
})

const formRef = ref<FormInstance>()

const formDefaults: Dept = {
  deptId: undefined,
  parentId: undefined,
  deptName: undefined,
  orderNum: 0,
  leader: undefined,
  phone: undefined,
  email: undefined,
  status: '0'
}

const form = reactive<Dept>({ ...formDefaults })

const rules = reactive<FormRules>({
  parentId: [
    { required: true, message: '上级部门不能为空', trigger: 'blur' }
  ],
  deptName: [
    { required: true, message: '部门名称不能为空', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '显示排序不能为空', trigger: 'blur' }
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  phone: [
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ]
})

function getList() {
  loading.value = true
  listDept(queryParams).then(response => {
    deptList.value = handleTree(response.data)
    loading.value = false
  })
}

function getTreeselect() {
  listDept().then(response => {
    const dept: Dept = { deptId: 0, deptName: '主类目', children: handleTree(response.data) }
    deptOptions.value = [dept]
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  Object.assign(form, formDefaults)
  formRef.value?.resetFields()
}

function handleQuery() {
  getList()
}

function resetQuery() {
  handleQuery()
}

function handleAdd(row?: Dept) {
  reset()
  getTreeselect()
  if (row != null && row.deptId) {
    form.parentId = row.deptId
  } else {
    form.parentId = 0
  }
  open.value = true
  title.value = '添加部门'
}

function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

function handleUpdate(row: Dept) {
  reset()
  getTreeselect()
  getDept(row.deptId as number).then(response => { // 表格行数据，deptId 必然存在
    Object.assign(form, response.data)
    open.value = true
    title.value = '修改部门'
  })
}

function submitForm() {
  formRef.value?.validate(valid => {
    if (valid) {
      if (form.deptId != undefined) {
        updateDept(form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addDept(form).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row: Dept) {
  ElMessageBox.confirm('是否确认删除名称为"' + row.deptName + '"的数据项?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delDept(row.deptId as number) // 表格行数据，deptId 必然存在
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

/** 转换树形结构（部门列表 → deptId/parentId 关联的树） */
function handleTree(data: Dept[]): Dept[] {
  const childrenListMap: Record<number, Dept[]> = {}
  const nodeIds: Record<number, Dept> = {}
  const tree: Dept[] = []

  data.forEach(d => {
    const parentId = d.parentId as number
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = []
    }
    nodeIds[d.deptId as number] = d
    childrenListMap[parentId].push(d)
  })

  data.forEach(d => {
    const parentId = d.parentId as number
    if (nodeIds[parentId] == null) {
      tree.push(d)
    }
  })

  function adaptToChildrenList(o: Dept) {
    const children = childrenListMap[o.deptId as number]
    if (children != null) {
      o.children = children
    }
    if (o.children) {
      o.children.forEach(c => adaptToChildrenList(c))
    }
  }
  tree.forEach(t => adaptToChildrenList(t))

  return tree
}

getList()
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>

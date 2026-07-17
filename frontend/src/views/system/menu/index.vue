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
      :data="menuList"
      row-key="menuId"
      :default-expand-all="isExpandAll"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      border
    >
      <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="200"></el-table-column>
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template #default="scope">
          <menu-icon v-if="scope.row.icon" :name="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="80"></el-table-column>
      <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <DictTag :options="statusOptions" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
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
            size="small"
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="680px" append-to-body>
      <el-form ref="menuFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                v-model="form.parentId"
                :data="menuOptions"
                :props="{ value: 'menuId', label: 'menuName', children: 'children' }"
                value-key="menuId"
                node-key="menuId"
                check-strictly
                clearable
                placeholder="选择上级菜单"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio value="M">目录</el-radio>
                <el-radio value="C">菜单</el-radio>
                <el-radio value="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.menuType != 'F'">
            <el-form-item label="菜单图标" prop="icon">
              <el-popover
                placement="bottom-start"
                width="460"
                trigger="click"
                @show="iconSelectRef?.reset()"
              >
                <IconSelect ref="iconSelectRef" @selected="selected" />
                <template #reference><el-input v-model="form.icon" placeholder="点击选择图标" readonly>
                  <template #prefix>
                    <menu-icon v-if="form.icon" :name="form.icon" />
                    <el-icon v-else><Search /></el-icon>
                  </template>
                </el-input></template>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType != 'F'">
            <el-form-item>
              <template #label><span>
                <el-tooltip content="选择是外链则路由地址需要以`http(s)://`开头" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                是否外链
              </span></template>
              <el-radio-group v-model="form.isFrame">
                <el-radio :value="0">是</el-radio>
                <el-radio :value="1">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType != 'F'">
            <el-form-item prop="path">
              <template #label><span>
                <el-tooltip content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                路由地址
              </span></template>
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType == 'C'">
            <el-form-item prop="component">
              <template #label><span>
                <el-tooltip content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                组件路径
              </span></template>
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType != 'M'">
            <el-form-item>
              <el-input v-model="form.perms" placeholder="请输入权限标识" maxlength="100" />
              <template #label><span>
                <el-tooltip content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                权限字符
              </span></template>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType == 'C'">
            <el-form-item>
              <el-input v-model="form.query" placeholder="请输入路由参数" maxlength="255" />
              <template #label><span>
                <el-tooltip content='访问路由的默认传递参数，如：`{"id": 1, "name": "ry"}`' placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                路由参数
              </span></template>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType == 'C'">
            <el-form-item>
              <template #label><span>
                <el-tooltip content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                是否缓存
              </span></template>
              <el-radio-group v-model="form.isCache">
                <el-radio :value="0">缓存</el-radio>
                <el-radio :value="1">不缓存</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType != 'F'">
            <el-form-item>
              <template #label><span>
                <el-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                显示状态
              </span></template>
              <el-radio-group v-model="form.visible">
                <el-radio value="0">显示</el-radio>
                <el-radio value="1">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <template #label><span>
                <el-tooltip content="选择停用则路由将不会出现在侧边栏，也不能被访问" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                菜单状态
              </span></template>
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
import { listMenu, getMenu, addMenu, updateMenu, delMenu, treeselect } from '@/api/system/menu'
import IconSelect from '@/components/IconSelect/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'
import DictTag from '@/components/DictTag/index.vue'
import { parseTime } from '@/utils'
import type { Menu, MenuQuery } from '@/types/system/menu'

defineOptions({ name: 'Menu' })

const STATUS_OPTIONS = [
  { value: '0', label: '正常', type: 'success' },
  { value: '1', label: '停用', type: 'danger' }
]

const searchFields = [
  { prop: 'menuName', label: '菜单名称', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '菜单状态' }
]

const statusOptions = STATUS_OPTIONS

const loading = ref(true)
const menuList = ref<Menu[]>([])
const menuOptions = ref<Menu[]>([])
const dialogTitle = ref('')
const dialogVisible = ref(false)
const isExpandAll = ref(true)
const refreshTable = ref(true)
const queryParams = reactive<MenuQuery>({
  menuName: undefined,
  status: undefined
})

const menuFormRef = ref<FormInstance>()
const iconSelectRef = ref<InstanceType<typeof IconSelect>>()

const formDefaults: Menu = {
  menuId: undefined,
  parentId: 0,
  menuName: undefined,
  icon: undefined,
  menuType: 'M',
  orderNum: 0,
  isFrame: 1,
  isCache: 0,
  visible: '0',
  status: '0'
}

const form = reactive<Menu>({ ...formDefaults })

const rules = reactive<FormRules>({
  menuName: [
    { required: true, message: '菜单名称不能为空', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '菜单顺序不能为空', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '路由地址不能为空', trigger: 'blur' }
  ]
})

function getList() {
  loading.value = true
  listMenu(queryParams).then(response => {
    menuList.value = response.data
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function getTreeselect() {
  treeselect().then(response => {
    // api 声明 treeselect() 返回 TreeOption[]（id/label），但后端 /treeselect 实际返回
    // MenuVO[]（menuId/menuName/children），与 el-tree-select 的 props 映射一致；
    // 这是既有的类型标注与运行时结构不一致问题，此处按真实运行时结构处理。
    const menu: Menu = { menuId: 0, menuName: '主类目', children: response.data as unknown as Menu[] }
    menuOptions.value = [menu]
  })
}

function selected(name: string) {
  form.icon = name
}

function handleQuery() {
  getList()
}

function resetQuery() {
  handleQuery()
}

function handleAdd(row?: Menu) {
  reset()
  getTreeselect()
  if (row != null && row.menuId) {
    form.parentId = row.menuId
  } else {
    form.parentId = 0
  }
  dialogTitle.value = '添加菜单'
  dialogVisible.value = true
}

function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

function handleUpdate(row: Menu) {
  reset()
  getTreeselect()
  getMenu(row.menuId as number).then(response => { // 表格行数据，menuId 必然存在
    Object.assign(form, response.data)
    dialogTitle.value = '修改菜单'
    dialogVisible.value = true
  })
}

function submitForm() {
  menuFormRef.value?.validate(valid => {
    if (valid) {
      if (form.menuId) {
        updateMenu(form).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        })
      } else {
        addMenu(form).then(() => {
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

function reset() {
  Object.assign(form, formDefaults)
  menuFormRef.value?.resetFields()
}

function handleDelete(row: Menu) {
  ElMessageBox.confirm('是否确认删除名称为"' + row.menuName + '"的菜单？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delMenu(row.menuId as number, false) // 表格行数据，menuId 必然存在
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch((error: unknown) => {
    if (error !== 'cancel') {
      const message = error instanceof Error ? error.message : '删除失败'
      ElMessage.error(message)
    }
  })
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

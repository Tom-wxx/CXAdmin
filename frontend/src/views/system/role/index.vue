<template>
  <div class="app-container">
    <SearchForm
      :model="queryParams"
      :fields="searchFields"
      @search="handleQuery"
      @reset="resetQuery"
    />

    <TableToolbar
      show-add
      show-export
      show-refresh
      :export-disabled="exportLoading"
      @add="handleAdd"
      @export="handleExport"
      @refresh="getList"
    />

    <el-table v-loading="loading" :data="roleList" border>
      <el-table-column label="角色ID" align="center" prop="roleId" width="80" />
      <el-table-column label="角色名称" align="center" prop="roleName" />
      <el-table-column label="权限字符" align="center" prop="roleKey" />
      <el-table-column label="显示顺序" align="center" prop="roleSort" width="100" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
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
            icon="Promotion"
            @click="handlePermission(scope.row)"
          >权限分配</el-button>
          <el-button
            size="small"
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="form.roleName" placeholder="请输入角色名称" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="权限字符" prop="roleKey">
              <el-input v-model="form.roleKey" placeholder="请输入权限字符" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="角色顺序" prop="roleSort">
              <el-input-number v-model="form.roleSort" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="数据范围">
              <el-select v-model="form.dataScope" placeholder="请选择数据范围" style="width: 100%">
                <el-option label="全部数据权限" value="1" />
                <el-option label="自定义数据权限" value="2" />
                <el-option label="本部门数据权限" value="3" />
                <el-option label="本部门及以下数据权限" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer><div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div></template>
    </el-dialog>

    <el-dialog title="权限分配" v-model="permissionDialogVisible" width="600px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="角色名称">
          <el-input v-model="currentRole.roleName" disabled />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="currentRole.roleKey" disabled />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.menuCheckStrictly">父子联动</el-checkbox>
          <el-tree
            ref="menuTreeRef"
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            node-key="menuId"
            :check-strictly="!form.menuCheckStrictly"
            empty-text="加载中，请稍候"
            :props="defaultProps"
          ></el-tree>
        </el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer">
        <el-button type="primary" @click="submitPermission">确 定</el-button>
        <el-button @click="cancelPermission">取 消</el-button>
      </div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox, ElTree } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listRole, getRole, addRole, updateRole, delRole, changeRoleStatus, getMenuIds, saveRoleMenus, exportRole } from '@/api/system/role'
import { listMenu } from '@/api/system/menu'
import { useCrudTable } from '@/composables'
import { parseTime } from '@/utils'
import type { Role, RoleQuery } from '@/types/system/role'
import type { Menu } from '@/types/system/menu'
import Pagination from '@/components/Pagination/index.vue'
import SearchForm from '@/components/SearchForm/index.vue'
import TableToolbar from '@/components/TableToolbar/index.vue'

defineOptions({ name: 'Role' })

const STATUS_OPTIONS = [
  { value: '0', label: '正常' },
  { value: '1', label: '停用' }
]

const searchFields = [
  { prop: 'roleName', label: '角色名称', type: 'input' },
  { prop: 'roleKey', label: '权限字符', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, placeholder: '角色状态' }
]

const { loading, list: roleList, total, queryParams, getList, handleQuery, resetQuery } =
  useCrudTable<Role, RoleQuery>({
    listApi: listRole,
    defaultQuery: { roleName: undefined, roleKey: undefined, status: undefined }
  })

const dialogTitle = ref('')
const dialogVisible = ref(false)
const roleFormRef = ref<FormInstance>()

const formDefaults: Role = {
  roleId: undefined,
  roleName: undefined,
  roleKey: undefined,
  roleSort: 0,
  status: '0',
  dataScope: '1',
  menuCheckStrictly: true,
  deptCheckStrictly: true,
  menuIds: [],
  deptIds: [],
  remark: undefined
}

const form = reactive<Role>({ ...formDefaults })

const rules = reactive<FormRules>({
  roleName: [
    { required: true, message: '角色名称不能为空', trigger: 'blur' },
    { max: 30, message: '角色名称长度不能超过30', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '权限字符不能为空', trigger: 'blur' },
    { max: 100, message: '权限字符长度不能超过100', trigger: 'blur' }
  ],
  roleSort: [
    { required: true, message: '角色顺序不能为空', trigger: 'blur' }
  ]
})

const permissionDialogVisible = ref(false)
const currentRole = reactive<Role>({})
const menuOptions = ref<Menu[]>([])
const defaultProps = {
  children: 'children',
  label: 'menuName'
}
const menuExpand = ref(false)
const menuNodeAll = ref(false)
const exportLoading = ref(false)
const menuTreeRef = ref<InstanceType<typeof ElTree>>()

function handleAdd() {
  reset()
  dialogTitle.value = '添加角色'
  dialogVisible.value = true
}

function handleUpdate(row: Role) {
  reset()
  const roleId = row.roleId as number // 表格行数据，roleId 必然存在
  getRole(roleId).then(response => {
    Object.assign(form, response.data)
    dialogTitle.value = '修改角色'
    dialogVisible.value = true
  })
}

function submitForm() {
  roleFormRef.value?.validate(valid => {
    if (valid) {
      if (form.roleId) {
        updateRole(form).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        })
      } else {
        addRole(form).then(() => {
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
  Object.assign(form, formDefaults, { menuIds: [], deptIds: [] })
  roleFormRef.value?.resetFields()
}

function handleStatusChange(row: Role) {
  const text = row.status === '0' ? '启用' : '停用'
  ElMessageBox.confirm('确认要"' + text + '""' + row.roleName + '"角色吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return changeRoleStatus(row.roleId as number, row.status as string) // 表格行数据，字段必然存在
  }).then(() => {
    ElMessage.success(text + '成功')
  }).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}

function handleDelete(row: Role) {
  ElMessageBox.confirm('是否确认删除角色"' + row.roleName + '"？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delRole(row.roleId as number) // 表格行数据，roleId 必然存在
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  })
}

function handlePermission(row: Role) {
  reset()
  Object.assign(currentRole, {
    roleId: row.roleId,
    roleName: row.roleName,
    roleKey: row.roleKey
  })
  getRole(row.roleId as number).then(response => {
    Object.assign(form, response.data)
    permissionDialogVisible.value = true
    getMenuTreeselect()
  })
}

function getMenuTreeselect() {
  listMenu({}).then(response => {
    menuOptions.value = buildMenuTree(response.data)
    return getMenuIds(currentRole.roleId as number) // 进入本对话框前已赋值
  }).then(response => {
    const checkedKeys = response.data
    checkedKeys.forEach(menuId => {
      nextTick(() => {
        menuTreeRef.value?.setChecked(menuId, true, false)
      })
    })
  })
}

function buildMenuTree(menus: Menu[]): Menu[] {
  const tree: Menu[] = []
  const map: Record<number, Menu> = {}

  menus.forEach(menu => {
    map[menu.menuId as number] = { ...menu, children: [] }
  })

  menus.forEach(menu => {
    const node = map[menu.menuId as number]
    if (menu.parentId === 0) {
      tree.push(node)
    } else if (menu.parentId != null && map[menu.parentId]) {
      map[menu.parentId].children!.push(node)
    }
  })

  function removeEmptyChildren(nodes: Menu[]) {
    nodes.forEach(node => {
      if (node.children && node.children.length === 0) {
        delete node.children
      } else if (node.children) {
        removeEmptyChildren(node.children)
      }
    })
  }
  removeEmptyChildren(tree)

  return tree
}

function handleCheckedTreeExpand(value: unknown) {
  const treeList = menuOptions.value
  // el-tree 的 store.nodesMap 未在公开类型中暴露，此处直接访问其内部实现
  const nodesMap = (menuTreeRef.value as any)?.store?.nodesMap
  if (!nodesMap) return
  for (let i = 0; i < treeList.length; i++) {
    nodesMap[treeList[i].menuId as number].expanded = value
  }
}

function handleCheckedTreeNodeAll(value: unknown) {
  // el-tree 类型声明要求 Node[]，但运行时按 node-key 接受原始数据对象，与其内部实现一致
  menuTreeRef.value?.setCheckedNodes((value ? menuOptions.value : []) as any)
}

function getMenuAllCheckedKeys(): number[] {
  const checkedKeys = (menuTreeRef.value?.getCheckedKeys() ?? []) as number[]
  const halfCheckedKeys = (menuTreeRef.value?.getHalfCheckedKeys() ?? []) as number[]
  checkedKeys.unshift(...halfCheckedKeys)
  return checkedKeys
}

function submitPermission() {
  const roleId = currentRole.roleId as number // 进入本对话框前已赋值
  const menuIds = getMenuAllCheckedKeys()
  saveRoleMenus(roleId, menuIds).then(() => {
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  })
}

function cancelPermission() {
  permissionDialogVisible.value = false
  reset()
}

function handleExport() {
  ElMessageBox.confirm('是否确认导出所有角色数据？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    exportLoading.value = true
    return exportRole(queryParams)
  }).then((response: Blob) => {
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '角色数据.xlsx'
    link.click()
    URL.revokeObjectURL(link.href)
    exportLoading.value = false
    ElMessage.success('导出成功')
  }).catch(() => {
    exportLoading.value = false
  })
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.mb8 {
  margin-bottom: 8px;
}
.tree-border {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #FFFFFF;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}
</style>

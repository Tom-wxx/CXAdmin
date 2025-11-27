<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="权限字符" prop="roleKey">
        <el-input
          v-model="queryParams.roleKey"
          placeholder="请输入权限字符"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="角色状态" clearable style="width: 200px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
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
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          :loading="exportLoading"
          @click="handleExport"
        >导出</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="roleList" border>
      <el-table-column label="角色ID" align="center" prop="roleId" width="80" />
      <el-table-column label="角色名称" align="center" prop="roleName" />
      <el-table-column label="权限字符" align="center" prop="roleKey" />
      <el-table-column label="显示顺序" align="center" prop="roleSort" width="100" />
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-promotion"
            @click="handlePermission(scope.row)"
          >权限分配</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" append-to-body>
      <el-form ref="roleForm" :model="form" :rules="rules" label-width="100px">
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
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
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
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog title="权限分配" :visible.sync="permissionDialogVisible" width="600px" append-to-body>
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
            ref="menuTree"
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
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPermission">确 定</el-button>
        <el-button @click="cancelPermission">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRole, getRole, addRole, updateRole, delRole, changeRoleStatus, getMenuIds, saveRoleMenus, exportRole } from '@/api/system/role'
import { listMenu } from '@/api/system/menu'
import Pagination from '@/components/Pagination'

export default {
  name: 'Role',
  components: {
    Pagination
  },
  data() {
    return {
      // 加载状态
      loading: true,
      // 角色列表
      roleList: [],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        current: 1,
        size: 10,
        roleName: undefined,
        roleKey: undefined,
        status: undefined
      },
      // 对话框标题
      dialogTitle: '',
      // 对话框显示状态
      dialogVisible: false,
      // 表单数据
      form: {},
      // 表单校验规则
      rules: {
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
      },
      // 权限分配对话框
      permissionDialogVisible: false,
      // 当前角色
      currentRole: {},
      // 菜单列表
      menuOptions: [],
      // 菜单树节点属性
      defaultProps: {
        children: 'children',
        label: 'menuName'
      },
      // 菜单展开状态
      menuExpand: false,
      // 菜单全选状态
      menuNodeAll: false,
      // 导出loading
      exportLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询角色列表 */
    getList() {
      this.loading = true
      listRole(this.queryParams).then(response => {
        this.roleList = response.rows
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
        roleName: undefined,
        roleKey: undefined,
        status: undefined
      }
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.dialogTitle = '添加角色'
      this.dialogVisible = true
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const roleId = row.roleId
      getRole(roleId).then(response => {
        this.form = response.data
        this.dialogTitle = '修改角色'
        this.dialogVisible = true
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs.roleForm.validate(valid => {
        if (valid) {
          if (this.form.roleId) {
            updateRole(this.form).then(response => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.getList()
            })
          } else {
            addRole(this.form).then(response => {
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
      if (this.$refs.roleForm) {
        this.$refs.roleForm.resetFields()
      }
    },
    /** 角色状态修改 */
    handleStatusChange(row) {
      let text = row.status === '0' ? '启用' : '停用'
      this.$confirm('确认要"' + text + '""' + row.roleName + '"角色吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return changeRoleStatus(row.roleId, row.status)
      }).then(() => {
        this.$message.success(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除角色"' + row.roleName + '"？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delRole(row.roleId)
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
    },
    /** 权限分配按钮操作 */
    handlePermission(row) {
      this.reset()
      this.currentRole = {
        roleId: row.roleId,
        roleName: row.roleName,
        roleKey: row.roleKey
      }
      // 获取角色详情
      getRole(row.roleId).then(response => {
        this.form = response.data
        this.permissionDialogVisible = true
        // 获取菜单树
        this.getMenuTreeselect()
      })
    },
    /** 查询菜单树结构 */
    getMenuTreeselect() {
      listMenu({}).then(response => {
        this.menuOptions = this.buildMenuTree(response.data)
        // 获取角色已分配的菜单ID
        return getMenuIds(this.currentRole.roleId)
      }).then(response => {
        // 设置选中的菜单节点
        const checkedKeys = response.data
        checkedKeys.forEach(menuId => {
          this.$nextTick(() => {
            this.$refs.menuTree.setChecked(menuId, true, false)
          })
        })
      })
    },
    /** 构建菜单树 */
    buildMenuTree(menus) {
      const tree = []
      const map = {}

      // 创建节点映射
      menus.forEach(menu => {
        map[menu.menuId] = { ...menu, children: [] }
      })

      // 构建树形结构
      menus.forEach(menu => {
        const node = map[menu.menuId]
        if (menu.parentId === 0) {
          tree.push(node)
        } else {
          if (map[menu.parentId]) {
            map[menu.parentId].children.push(node)
          }
        }
      })

      // 删除空children
      const removeEmptyChildren = (nodes) => {
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
    },
    /** 树权限（展开/折叠）*/
    handleCheckedTreeExpand(value) {
      let treeList = this.menuOptions
      for (let i = 0; i < treeList.length; i++) {
        this.$refs.menuTree.store.nodesMap[treeList[i].menuId].expanded = value
      }
    },
    /** 树权限（全选/全不选）*/
    handleCheckedTreeNodeAll(value) {
      this.$refs.menuTree.setCheckedNodes(value ? this.menuOptions : [])
    },
    /** 所有菜单节点数据 */
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      let checkedKeys = this.$refs.menuTree.getCheckedKeys()
      // 半选中的菜单节点
      let halfCheckedKeys = this.$refs.menuTree.getHalfCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    /** 提交权限分配 */
    submitPermission() {
      const roleId = this.currentRole.roleId
      const menuIds = this.getMenuAllCheckedKeys()
      saveRoleMenus(roleId, menuIds).then(response => {
        this.$message.success('权限分配成功')
        this.permissionDialogVisible = false
      })
    },
    /** 取消权限分配 */
    cancelPermission() {
      this.permissionDialogVisible = false
      this.reset()
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$confirm('是否确认导出所有角色数据？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.exportLoading = true
        return exportRole(this.queryParams)
      }).then(response => {
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = '角色数据.xlsx'
        link.click()
        URL.revokeObjectURL(link.href)
        this.exportLoading = false
        this.$message.success('导出成功')
      }).catch(() => {
        this.exportLoading = false
      })
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
.tree-border {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #FFFFFF;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}
</style>

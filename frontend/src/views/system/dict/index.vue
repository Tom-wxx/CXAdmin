<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 字典类型列表 -->
      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header">
            <span>字典类型</span>
            <el-button
              style="float: right; padding: 3px 10px"
              type="text"
              icon="el-icon-plus"
              @click="handleAddType"
            >新增</el-button>
          </div>
          <el-input
            v-model="typeQuery"
            placeholder="请输入字典名称"
            clearable
            size="small"
            prefix-icon="el-icon-search"
            style="margin-bottom: 10px"
            @keyup.enter.native="handleTypeSearch"
            @clear="handleTypeSearch"
          />
          <el-table
            v-loading="typeLoading"
            :data="filteredTypeList"
            highlight-current-row
            @current-change="handleTypeChange"
            style="width: 100%"
            max-height="500"
          >
            <el-table-column prop="dictName" label="字典名称" show-overflow-tooltip />
            <el-table-column prop="dictType" label="字典类型" show-overflow-tooltip />
            <el-table-column label="状态" width="60">
              <template slot-scope="scope">
                <DictTag :options="statusOptions" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click.stop="handleEditType(scope.row)"
                >修改</el-button>
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click.stop="handleDeleteType(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="typeTotal > typeQueryParams.size"
            small
            layout="prev, pager, next"
            :total="typeTotal"
            :page-size="typeQueryParams.size"
            :current-page.sync="typeQueryParams.current"
            @current-change="handleTypePageChange"
            style="margin-top: 10px; text-align: center"
          />
        </el-card>
      </el-col>

      <!-- 字典数据列表 -->
      <el-col :span="16">
        <el-card shadow="hover">
          <div slot="header">
            <span>字典数据</span>
            <span v-if="currentType" style="margin-left: 10px; color: #909399">
              ({{ currentType.dictName }} - {{ currentType.dictType }})
            </span>
            <el-button
              v-if="currentType"
              style="float: right; padding: 3px 10px"
              type="text"
              icon="el-icon-plus"
              @click="handleAddData"
            >新增</el-button>
          </div>
          <el-table
            v-loading="dataLoading"
            :data="dataList"
            style="width: 100%"
          >
            <el-table-column prop="dictLabel" label="字典标签" />
            <el-table-column prop="dictValue" label="字典键值" />
            <el-table-column prop="dictSort" label="排序" width="80" />
            <el-table-column label="状态" width="80">
              <template slot-scope="scope">
                <DictTag :options="statusOptions" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click="handleEditData(scope.row)"
                >修改</el-button>
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click="handleDeleteData(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="dataTotal > dataQueryParams.size"
            small
            layout="prev, pager, next"
            :total="dataTotal"
            :page-size="dataQueryParams.size"
            :current-page.sync="dataQueryParams.current"
            @current-change="handleDataPageChange"
            style="margin-top: 10px; text-align: center"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 字典类型对话框 -->
    <el-dialog :title="typeDialogTitle" :visible.sync="typeDialogVisible" width="600px">
      <el-form ref="typeForm" :model="typeForm" :rules="typeRules" label-width="100px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="typeForm.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="typeForm.dictType" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="typeForm.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="typeForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitTypeForm">确 定</el-button>
        <el-button @click="typeDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 字典数据对话框 -->
    <el-dialog :title="dataDialogTitle" :visible.sync="dataDialogVisible" width="600px">
      <el-form ref="dataForm" :model="dataForm" :rules="dataRules" label-width="100px">
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
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dataForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitDataForm">确 定</el-button>
        <el-button @click="dataDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDictType, getDictType, addDictType, updateDictType, delDictType } from '@/api/system/dict/type'
import { listDictData, getDictData, addDictData, updateDictData, delDictData } from '@/api/system/dict/data'
import DictTag from '@/components/DictTag'

const STATUS_OPTIONS = [
  { value: '0', label: '正常', type: 'success' },
  { value: '1', label: '停用', type: 'danger' }
]

export default {
  name: 'Dict',
  components: {
    DictTag
  },
  data() {
    return {
      statusOptions: STATUS_OPTIONS,
      // 字典类型
      typeLoading: false,
      typeList: [],
      typeTotal: 0,
      typeQuery: '',
      typeQueryParams: {
        current: 1,
        size: 20
      },
      currentType: null,
      typeDialogTitle: '',
      typeDialogVisible: false,
      typeForm: {},
      typeRules: {
        dictName: [
          { required: true, message: '字典名称不能为空', trigger: 'blur' }
        ],
        dictType: [
          { required: true, message: '字典类型不能为空', trigger: 'blur' }
        ]
      },
      // 字典数据
      dataLoading: false,
      dataList: [],
      dataTotal: 0,
      dataQueryParams: {
        current: 1,
        size: 20,
        dictType: ''
      },
      dataDialogTitle: '',
      dataDialogVisible: false,
      dataForm: {},
      dataRules: {
        dictLabel: [
          { required: true, message: '字典标签不能为空', trigger: 'blur' }
        ],
        dictValue: [
          { required: true, message: '字典键值不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    filteredTypeList() {
      return this.typeList
    }
  },
  created() {
    this.getTypeList()
  },
  methods: {
    /** 查询字典类型列表 */
    getTypeList() {
      this.typeLoading = true
      const params = { ...this.typeQueryParams }
      if (this.typeQuery) {
        params.dictName = this.typeQuery
      }
      listDictType(params).then(response => {
        this.typeList = response.rows
        this.typeTotal = response.total
        this.typeLoading = false
      }).catch(() => {
        this.typeLoading = false
      })
    },
    /** 字典类型搜索 */
    handleTypeSearch() {
      this.typeQueryParams.current = 1
      this.getTypeList()
    },
    /** 字典类型分页变化 */
    handleTypePageChange(page) {
      this.typeQueryParams.current = page
      this.getTypeList()
    },
    /** 字典类型选择变化 */
    handleTypeChange(row) {
      this.currentType = row
      if (row) {
        // 切换字典类型时重置数据分页
        this.dataQueryParams.current = 1
        this.getDataList(row.dictType)
      }
    },
    /** 查询字典数据列表 */
    getDataList(dictType) {
      this.dataLoading = true
      this.dataQueryParams.dictType = dictType
      listDictData(this.dataQueryParams).then(response => {
        this.dataList = response.rows
        this.dataTotal = response.total
        this.dataLoading = false
      }).catch(() => {
        this.dataLoading = false
      })
    },
    /** 字典数据分页变化 */
    handleDataPageChange(page) {
      this.dataQueryParams.current = page
      this.getDataList(this.dataQueryParams.dictType)
    },
    /** 新增字典类型 */
    handleAddType() {
      this.resetTypeForm()
      this.typeDialogTitle = '添加字典类型'
      this.typeDialogVisible = true
    },
    /** 修改字典类型 */
    handleEditType(row) {
      this.resetTypeForm()
      getDictType(row.dictId).then(response => {
        this.typeForm = response.data
        this.typeDialogTitle = '修改字典类型'
        this.typeDialogVisible = true
      })
    },
    /** 提交字典类型表单 */
    submitTypeForm() {
      this.$refs.typeForm.validate(valid => {
        if (valid) {
          if (this.typeForm.dictId) {
            updateDictType(this.typeForm).then(() => {
              this.$message.success('修改成功')
              this.typeDialogVisible = false
              this.getTypeList()
            })
          } else {
            addDictType(this.typeForm).then(() => {
              this.$message.success('新增成功')
              this.typeDialogVisible = false
              this.getTypeList()
            })
          }
        }
      })
    },
    /** 删除字典类型 */
    handleDeleteType(row) {
      this.$confirm('是否确认删除字典类型"' + row.dictName + '"？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delDictType(row.dictId)
      }).then(() => {
        this.getTypeList()
        this.$message.success('删除成功')
        if (this.currentType && this.currentType.dictId === row.dictId) {
          this.currentType = null
          this.dataList = []
        }
      })
    },
    /** 重置字典类型表单 */
    resetTypeForm() {
      this.typeForm = {
        dictId: undefined,
        dictName: undefined,
        dictType: undefined,
        status: '0',
        remark: undefined
      }
      if (this.$refs.typeForm) {
        this.$refs.typeForm.resetFields()
      }
    },
    /** 新增字典数据 */
    handleAddData() {
      this.resetDataForm()
      this.dataForm.dictType = this.currentType.dictType
      this.dataDialogTitle = '添加字典数据'
      this.dataDialogVisible = true
    },
    /** 修改字典数据 */
    handleEditData(row) {
      this.resetDataForm()
      getDictData(row.dictCode).then(response => {
        this.dataForm = response.data
        this.dataDialogTitle = '修改字典数据'
        this.dataDialogVisible = true
      })
    },
    /** 提交字典数据表单 */
    submitDataForm() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          if (this.dataForm.dictCode) {
            updateDictData(this.dataForm).then(() => {
              this.$message.success('修改成功')
              this.dataDialogVisible = false
              this.getDataList(this.currentType.dictType)
            })
          } else {
            addDictData(this.dataForm).then(() => {
              this.$message.success('新增成功')
              this.dataDialogVisible = false
              this.getDataList(this.currentType.dictType)
            })
          }
        }
      })
    },
    /** 删除字典数据 */
    handleDeleteData(row) {
      this.$confirm('是否确认删除字典数据"' + row.dictLabel + '"？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delDictData(row.dictCode)
      }).then(() => {
        this.getDataList(this.currentType.dictType)
        this.$message.success('删除成功')
      })
    },
    /** 重置字典数据表单 */
    resetDataForm() {
      this.dataForm = {
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
      if (this.$refs.dataForm) {
        this.$refs.dataForm.resetFields()
      }
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>

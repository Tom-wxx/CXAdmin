<template>
  <el-row :gutter="10" class="table-toolbar mb8">
    <el-col v-if="showAdd" :span="1.5">
      <el-button
        type="primary"
        plain
        icon="Plus"
        size="small"
        :disabled="addDisabled"
        @click="$emit('add')"
      >新增</el-button>
    </el-col>
    <el-col v-if="showEdit" :span="1.5">
      <el-button
        type="success"
        plain
        icon="Edit"
        size="small"
        :disabled="editDisabled || single"
        @click="$emit('edit')"
      >修改</el-button>
    </el-col>
    <el-col v-if="showDelete" :span="1.5">
      <el-button
        type="danger"
        plain
        icon="Delete"
        size="small"
        :disabled="deleteDisabled || multiple"
        @click="$emit('delete')"
      >删除</el-button>
    </el-col>
    <el-col v-if="showExport" :span="1.5">
      <el-button
        type="warning"
        plain
        icon="Download"
        size="small"
        :disabled="exportDisabled"
        @click="$emit('export')"
      >导出</el-button>
    </el-col>
    <el-col v-if="showImport" :span="1.5">
      <el-button
        type="info"
        plain
        icon="Upload"
        size="small"
        :disabled="importDisabled"
        @click="$emit('import')"
      >导入</el-button>
    </el-col>

    <slot />

    <el-col v-if="showRefresh" :span="1.5" class="table-toolbar__right">
      <el-tooltip content="刷新" placement="top">
        <el-button
          circle
          size="small"
          icon="Refresh"
          @click="$emit('refresh')"
        />
      </el-tooltip>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
defineOptions({ name: 'TableToolbar' })

withDefaults(defineProps<{
  showAdd?: boolean
  showEdit?: boolean
  showDelete?: boolean
  showExport?: boolean
  showImport?: boolean
  showRefresh?: boolean
  addDisabled?: boolean
  editDisabled?: boolean
  deleteDisabled?: boolean
  exportDisabled?: boolean
  importDisabled?: boolean
  /** 编辑按钮：未选中或多选时禁用 */
  single?: boolean
  /** 删除按钮：未选中时禁用 */
  multiple?: boolean
}>(), {
  showAdd: false,
  showEdit: false,
  showDelete: false,
  showExport: false,
  showImport: false,
  showRefresh: false,
  addDisabled: false,
  editDisabled: false,
  deleteDisabled: false,
  exportDisabled: false,
  importDisabled: false,
  single: false,
  multiple: false
})

defineEmits<{
  (e: 'add'): void
  (e: 'edit'): void
  (e: 'delete'): void
  (e: 'export'): void
  (e: 'import'): void
  (e: 'refresh'): void
}>()
</script>

<style scoped>
.table-toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}
.table-toolbar__right {
  margin-left: auto;
}
</style>

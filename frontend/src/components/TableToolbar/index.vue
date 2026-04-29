<template>
  <el-row :gutter="10" class="table-toolbar mb8">
    <el-col v-if="showAdd" :span="1.5">
      <el-button
        type="primary"
        plain
        icon="el-icon-plus"
        size="mini"
        :disabled="addDisabled"
        @click="$emit('add')"
      >新增</el-button>
    </el-col>
    <el-col v-if="showEdit" :span="1.5">
      <el-button
        type="success"
        plain
        icon="el-icon-edit"
        size="mini"
        :disabled="editDisabled || single"
        @click="$emit('edit')"
      >修改</el-button>
    </el-col>
    <el-col v-if="showDelete" :span="1.5">
      <el-button
        type="danger"
        plain
        icon="el-icon-delete"
        size="mini"
        :disabled="deleteDisabled || multiple"
        @click="$emit('delete')"
      >删除</el-button>
    </el-col>
    <el-col v-if="showExport" :span="1.5">
      <el-button
        type="warning"
        plain
        icon="el-icon-download"
        size="mini"
        :disabled="exportDisabled"
        @click="$emit('export')"
      >导出</el-button>
    </el-col>
    <el-col v-if="showImport" :span="1.5">
      <el-button
        type="info"
        plain
        icon="el-icon-upload2"
        size="mini"
        :disabled="importDisabled"
        @click="$emit('import')"
      >导入</el-button>
    </el-col>

    <slot />

    <el-col v-if="showRefresh" :span="1.5" class="table-toolbar__right">
      <el-tooltip content="刷新" placement="top">
        <el-button
          circle
          size="mini"
          icon="el-icon-refresh"
          @click="$emit('refresh')"
        />
      </el-tooltip>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'TableToolbar',
  props: {
    showAdd: { type: Boolean, default: false },
    showEdit: { type: Boolean, default: false },
    showDelete: { type: Boolean, default: false },
    showExport: { type: Boolean, default: false },
    showImport: { type: Boolean, default: false },
    showRefresh: { type: Boolean, default: false },
    addDisabled: { type: Boolean, default: false },
    editDisabled: { type: Boolean, default: false },
    deleteDisabled: { type: Boolean, default: false },
    exportDisabled: { type: Boolean, default: false },
    importDisabled: { type: Boolean, default: false },
    /** 编辑按钮：未选中或多选时禁用 */
    single: { type: Boolean, default: false },
    /** 删除按钮：未选中时禁用 */
    multiple: { type: Boolean, default: false }
  }
}
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

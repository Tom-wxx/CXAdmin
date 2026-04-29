<template>
  <el-form
    :model="model"
    :inline="true"
    size="small"
    label-width="80px"
    class="search-form"
    @submit.native.prevent
  >
    <el-form-item
      v-for="field in visibleFields"
      :key="field.prop"
      :label="field.label"
      :prop="field.prop"
    >
      <el-input
        v-if="!field.type || field.type === 'input'"
        v-model="model[field.prop]"
        :placeholder="field.placeholder || ('请输入' + field.label)"
        :clearable="field.clearable !== false"
        :style="{ width: field.width || '200px' }"
        @keyup.enter.native="handleSearch"
      />
      <el-select
        v-else-if="field.type === 'select'"
        v-model="model[field.prop]"
        :placeholder="field.placeholder || ('请选择' + field.label)"
        :clearable="field.clearable !== false"
        :multiple="!!field.multiple"
        :style="{ width: field.width || '200px' }"
      >
        <el-option
          v-for="opt in field.options || []"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
      <el-date-picker
        v-else-if="field.type === 'date'"
        v-model="model[field.prop]"
        type="date"
        :placeholder="field.placeholder || ('请选择' + field.label)"
        :clearable="field.clearable !== false"
        value-format="yyyy-MM-dd"
        :style="{ width: field.width || '200px' }"
      />
      <el-date-picker
        v-else-if="field.type === 'daterange'"
        v-model="model[field.prop]"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        :clearable="field.clearable !== false"
        value-format="yyyy-MM-dd"
        :style="{ width: field.width || '240px' }"
      />
      <el-date-picker
        v-else-if="field.type === 'datetime'"
        v-model="model[field.prop]"
        type="datetime"
        :placeholder="field.placeholder || ('请选择' + field.label)"
        :clearable="field.clearable !== false"
        value-format="yyyy-MM-dd HH:mm:ss"
        :style="{ width: field.width || '200px' }"
      />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" icon="el-icon-search" size="mini" @click="handleSearch">搜索</el-button>
      <el-button icon="el-icon-refresh" size="mini" @click="handleReset">重置</el-button>
      <el-button
        v-if="collapsible"
        type="text"
        size="mini"
        @click="collapsed = !collapsed"
      >
        {{ collapsed ? '展开' : '收起' }}
        <i :class="collapsed ? 'el-icon-arrow-down' : 'el-icon-arrow-up'" />
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: 'SearchForm',
  props: {
    model: {
      type: Object,
      required: true
    },
    fields: {
      type: Array,
      required: true
    },
    collapseAt: {
      type: Number,
      default: 4
    }
  },
  data() {
    return {
      collapsed: this.fields.length > this.collapseAt
    }
  },
  computed: {
    collapsible() {
      return this.fields.length > this.collapseAt
    },
    visibleFields() {
      if (!this.collapsible || !this.collapsed) return this.fields
      return this.fields.slice(0, this.collapseAt)
    }
  },
  methods: {
    handleSearch() {
      this.$emit('search')
    },
    handleReset() {
      this.fields.forEach(f => {
        if (f.type === 'daterange') {
          this.$set(this.model, f.prop, [])
        } else if (f.defaultValue !== undefined) {
          this.$set(this.model, f.prop, f.defaultValue)
        } else {
          this.$set(this.model, f.prop, undefined)
        }
      })
      this.$emit('reset')
    }
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 8px;
}
</style>

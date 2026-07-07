<template>
  <el-form
    :model="model"
    :inline="true"
    size="small"
    label-width="80px"
    class="search-form"
    @submit.prevent
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
        @keyup.enter="handleSearch"
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
      <el-button type="primary" icon="Search" size="small" @click="handleSearch">搜索</el-button>
      <el-button icon="Refresh" size="small" @click="handleReset">重置</el-button>
      <el-button
        v-if="collapsible"
        type="text"
        size="small"
        @click="collapsed = !collapsed"
      >
        {{ collapsed ? '展开' : '收起' }}
        <el-icon><component :is="collapsed ? 'ArrowDown' : 'ArrowUp'" /></el-icon>
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

defineOptions({ name: 'SearchForm' })

interface FieldOption {
  label: string
  value: string | number
}

interface SearchField {
  prop: string
  label: string
  /** input | select | date | daterange | datetime（宽松为 string，便于各页内联声明） */
  type?: string
  placeholder?: string
  clearable?: boolean
  width?: string
  multiple?: boolean
  options?: FieldOption[]
  defaultValue?: unknown
}

interface Props {
  // 宽松为 Record<string, any>，以便各页传入带类型的 query 对象（接口无索引签名）
  model: Record<string, any>
  fields: SearchField[]
  collapseAt?: number
}

const props = withDefaults(defineProps<Props>(), {
  collapseAt: 4
})

const emit = defineEmits<{
  (e: 'search'): void
  (e: 'reset'): void
}>()

const collapsed = ref(props.fields.length > props.collapseAt)

const collapsible = computed(() => props.fields.length > props.collapseAt)

const visibleFields = computed(() => {
  if (!collapsible.value || !collapsed.value) return props.fields
  return props.fields.slice(0, props.collapseAt)
})

function handleSearch() {
  emit('search')
}

function handleReset() {
  props.fields.forEach(f => {
    if (f.type === 'daterange') {
      props.model[f.prop] = []
    } else if (f.defaultValue !== undefined) {
      props.model[f.prop] = f.defaultValue
    } else {
      props.model[f.prop] = undefined
    }
  })
  emit('reset')
}
</script>

<style scoped>
.search-form {
  margin-bottom: 8px;
}
</style>

<template>
  <el-select
    :model-value="modelValue"
    :placeholder="placeholder"
    :clearable="clearable"
    :multiple="multiple"
    :disabled="disabled"
    :size="size"
    :style="{ width }"
    @update:model-value="onUpdate"
    @change="onChange"
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
      :disabled="item.disabled"
    />
  </el-select>
</template>

<script setup lang="ts">
import type { ComponentSize } from 'element-plus'

defineOptions({ name: 'DictSelect' })

type SelectValue = string | number | Array<string | number>

interface DictSelectOption {
  value: string | number
  label: string
  disabled?: boolean
}

withDefaults(defineProps<{
  modelValue?: SelectValue
  options?: DictSelectOption[]
  placeholder?: string
  clearable?: boolean
  multiple?: boolean
  disabled?: boolean
  size?: ComponentSize
  width?: string
}>(), {
  modelValue: '',
  options: () => [],
  placeholder: '请选择',
  clearable: true,
  multiple: false,
  disabled: false,
  size: 'small',
  width: '200px'
})

const emit = defineEmits<{
  (e: 'update:modelValue', val: SelectValue): void
  (e: 'change', val: SelectValue): void
}>()

function onUpdate(val: SelectValue) {
  emit('update:modelValue', val)
}

function onChange(val: SelectValue) {
  emit('change', val)
}
</script>

<template>
  <span class="dict-tag">
    <template v-if="Array.isArray(values) && values.length">
      <el-tag
        v-for="item in matchedItems"
        :key="item.value"
        :type="tagType(item.type)"
        :effect="effect"
        :size="size"
        class="dict-tag__item"
      >
        {{ item.label }}
      </el-tag>
    </template>
    <el-tag
      v-else-if="single"
      :type="tagType(single.type)"
      :effect="effect"
      :size="size"
    >
      {{ single.label }}
    </el-tag>
    <span v-else class="dict-tag__placeholder">{{ placeholder }}</span>
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ComponentSize } from 'element-plus'

defineOptions({ name: 'DictTag' })

type TagType = '' | 'primary' | 'success' | 'info' | 'warning' | 'danger'

interface DictTagOption {
  value: string | number
  label: string
  /** 字典 listClass 字符串；渲染时由 tagType() 收敛为 el-tag 的类型联合 */
  type?: string
}

/** 把字典里的字符串样式收敛为 el-tag 接受的类型联合 */
function tagType(t?: string): TagType {
  return (t || '') as TagType
}

const props = withDefaults(defineProps<{
  options?: DictTagOption[]
  value?: string | number | Array<string | number>
  separator?: string
  size?: ComponentSize
  effect?: 'dark' | 'light' | 'plain'
  placeholder?: string
}>(), {
  options: () => [],
  value: '',
  separator: ',',
  size: 'small',
  effect: 'light',
  placeholder: '-'
})

const values = computed<Array<string | number>>(() => {
  const v = props.value
  if (v === null || v === undefined || v === '') return []
  if (Array.isArray(v)) return v
  if (typeof v === 'string' && v.includes(props.separator)) {
    return v.split(props.separator)
  }
  return [v]
})

const matchedItems = computed<DictTagOption[]>(() =>
  values.value.map((v) => {
    const found = props.options.find((o) => String(o.value) === String(v))
    return found || { value: v, label: String(v), type: '' }
  })
)

const single = computed<DictTagOption | null>(() =>
  matchedItems.value.length === 1 ? matchedItems.value[0] : null
)
</script>

<style scoped>
.dict-tag__item + .dict-tag__item {
  margin-left: 4px;
}
.dict-tag__placeholder {
  color: #c0c4cc;
}
</style>

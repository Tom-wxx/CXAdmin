<template>
  <el-icon v-if="resolved" :class="iconClass"><component :is="resolved" /></el-icon>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { resolveIcon } from '@/utils/iconMap'

defineOptions({ name: 'MenuIcon' })

const props = withDefaults(defineProps<{
  // 数据库存储的历史图标名 / Element Plus 组件名 / el-icon-xxx 均可
  name?: string
  iconClass?: string
}>(), {
  name: '',
  iconClass: ''
})

const resolved = computed(() => resolveIcon(props.name))
</script>

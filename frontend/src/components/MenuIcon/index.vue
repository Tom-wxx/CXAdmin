<template>
  <el-icon v-if="resolved" :class="iconClass"><component :is="resolved" /></el-icon>
</template>

<script>
import { resolveIcon } from '@/utils/iconMap'

export default {
  name: 'MenuIcon',
  props: {
    // 数据库存储的历史图标名 / Element Plus 组件名 / el-icon-xxx 均可
    name: { type: String, default: '' },
    iconClass: { type: String, default: '' }
  },
  computed: {
    resolved() {
      return resolveIcon(this.name)
    }
  }
}
</script>

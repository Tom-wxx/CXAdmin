<template>
  <component :is="type" v-bind="linkProps(to)">
    <slot />
  </component>
</template>

<script setup lang="ts">
import { computed } from 'vue'

defineOptions({ name: 'SidebarLink' })

const props = defineProps<{ to: string }>()

function isExternal(path: string): boolean {
  return /^(https?:|mailto:|tel:)/.test(path)
}

const type = computed(() => (isExternal(props.to) ? 'a' : 'router-link'))

function linkProps(to: string): Record<string, string> {
  if (isExternal(to)) {
    return { href: to, target: '_blank', rel: 'noopener' }
  }
  return { to }
}
</script>

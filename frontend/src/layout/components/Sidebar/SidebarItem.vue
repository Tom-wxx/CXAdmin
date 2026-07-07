<template>
  <div v-if="!item.hidden">
    <template v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild?.children||onlyOneChild?.noShowingChildren)&&!item.alwaysShow">
      <app-link v-if="onlyOneChild && onlyOneChild.meta" :to="resolvePath(onlyOneChild.path)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)">
          <menu-icon v-if="onlyOneChild.meta.icon" :name="onlyOneChild.meta.icon" />
          <template #title><span>{{ onlyOneChild.meta.title }}</span></template>
        </el-menu-item>
      </app-link>
    </template>

    <el-sub-menu v-else ref="subMenu" :index="resolvePath(item.path)">
      <template #title>
        <menu-icon v-if="item.meta && item.meta.icon" :name="item.meta.icon" />
        <span>{{ item.meta && item.meta.title || '未命名' }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-sub-menu>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AppLink from './Link.vue'

defineOptions({ name: 'SidebarItem' })

interface RouteMeta {
  icon?: string
  title?: string
  [key: string]: unknown
}

interface RouteItem {
  path: string
  hidden?: boolean
  alwaysShow?: boolean
  children?: RouteItem[]
  noShowingChildren?: boolean
  meta?: RouteMeta
  [key: string]: unknown
}

interface Props {
  item: RouteItem
  isNest?: boolean
  basePath?: string
}

const props = withDefaults(defineProps<Props>(), {
  isNest: false,
  basePath: ''
})

// Mutated as a side-effect of hasOneShowingChild (called in template) — must be reactive ref.
const onlyOneChild = ref<RouteItem | null>(null)

function hasOneShowingChild(children: RouteItem[] = [], parent: RouteItem): boolean {
  const showingChildren = children.filter(child => {
    if (child.hidden) {
      return false
    } else {
      onlyOneChild.value = child
      return true
    }
  })

  if (showingChildren.length === 1) {
    return true
  }

  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  return false
}

function resolvePath(routePath: string): string {
  if (routePath.startsWith('/')) {
    return routePath
  }
  if (props.basePath.endsWith('/')) {
    return props.basePath + routePath
  }
  return props.basePath + '/' + routePath
}
</script>

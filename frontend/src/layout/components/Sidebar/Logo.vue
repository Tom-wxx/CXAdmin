<template>
  <div class="sidebar-logo-container" :class="{ collapse: isCollapse }" :style="logoStyle">
    <router-link to="/" class="sidebar-logo-link">
      <div class="sidebar-logo-content">
        <div class="logo-icon-wrapper">
          <el-icon class="logo-icon"><HomeFilled /></el-icon>
        </div>
        <transition name="sidebarLogoFade">
          <h1 v-if="!isCollapse" class="sidebar-title">{{ title }}</h1>
        </transition>
      </div>
    </router-link>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useSettingsStore } from '@/composables/store'

defineOptions({ name: 'SidebarLogo' })

defineProps<{ isCollapse: boolean }>()

const { title, sidebarColor } = useSettingsStore()

const logoStyle = computed(() => ({
  '--logo-title-color': isLightColor(sidebarColor.value) ? '#1f2937' : '#ffffff',
  '--logo-subtle-border': isLightColor(sidebarColor.value) ? '#e5e7eb' : 'rgba(255, 255, 255, 0.12)'
}))

function isLightColor(color?: string): boolean {
  if (!color) return true
  const hex = color.replace('#', '')
  if (hex.length !== 6) return true
  const r = parseInt(hex.slice(0, 2), 16)
  const g = parseInt(hex.slice(2, 4), 16)
  const b = parseInt(hex.slice(4, 6), 16)
  const brightness = (r * 299 + g * 587 + b * 114) / 1000
  return brightness > 155
}
</script>

<style lang="scss" scoped>
.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 48px;
  line-height: 48px;
  text-align: center;
  overflow: hidden;
  border-bottom: 1px solid var(--logo-subtle-border);

  &.collapse {
    .logo-icon-wrapper {
      margin-right: 0;
    }
  }

  .sidebar-logo-link {
    display: block;
    width: 100%;
    height: 100%;
    text-decoration: none;

    .sidebar-logo-content {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      padding: 0 12px;

      .logo-icon-wrapper {
        width: 28px;
        height: 28px;
        background: #0f9f9f;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 10px;
        flex-shrink: 0;
      }

      .logo-icon {
        font-size: 16px;
        color: #fff;
      }

      .sidebar-title {
        margin: 0;
        font-size: 15px;
        font-weight: 600;
        color: var(--logo-title-color);
        white-space: nowrap;
      }
    }
  }
}

.sidebarLogoFade-enter-active {
  transition: opacity 0.2s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}
</style>

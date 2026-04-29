<template>
  <div class="sidebar-logo-container" :class="{ collapse: isCollapse }">
    <router-link to="/" class="sidebar-logo-link">
      <div class="sidebar-logo-content">
        <div class="logo-icon-wrapper">
          <i class="el-icon-s-home logo-icon"></i>
        </div>
        <transition name="sidebarLogoFade">
          <h1 v-if="!isCollapse" class="sidebar-title">{{ title }}</h1>
        </transition>
      </div>
    </router-link>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'SidebarLogo',
  props: {
    isCollapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    ...mapState({
      title: state => state.settings.title
    })
  }
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
        background: #13c2c2;
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
        color: #fff;
        white-space: nowrap;
        letter-spacing: 0.5px;
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

<template>
  <div class="sidebar-logo-container" :class="{ collapse: isCollapse }">
    <router-link to="/" class="sidebar-logo-link">
      <div class="sidebar-logo-content">
        <i class="el-icon-s-home logo-icon"></i>
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
  height: 60px;
  line-height: 60px;
  text-align: center;
  overflow: hidden;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s;

  &.collapse {
    .sidebar-logo-link {
      .sidebar-logo-content {
        .logo-icon {
          font-size: 28px;
          margin-right: 0;
        }
      }
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
      padding: 0 15px;
      transition: all 0.3s;

      .logo-icon {
        font-size: 32px;
        margin-right: 12px;
        color: #fff;
        transition: all 0.3s;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
      }

      .sidebar-title {
        margin: 0;
        font-size: 18px;
        font-weight: 700;
        color: #fff;
        white-space: nowrap;
        letter-spacing: 1px;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
      }
    }

    &:hover {
      .sidebar-logo-content {
        .logo-icon {
          transform: scale(1.1) rotate(5deg);
        }
      }
    }
  }
}

/* 文字淡入淡出动画 */
.sidebarLogoFade-enter-active {
  transition: opacity 0.3s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}
</style>

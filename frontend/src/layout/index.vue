<template>
  <div class="app-wrapper" :class="{ 'layout-top': sidebarPosition === 'top' }">
    <!-- 顶部模式：一级菜单显示在顶部 -->
    <template v-if="sidebarPosition === 'top'">
      <div class="top-navbar" :style="{ backgroundColor: sidebarColor }">
        <div class="navbar-left">
          <sidebar-logo :is-collapse="false" style="display: inline-block; width: 180px;" />
          <el-menu
            :default-active="activeTopMenu"
            :background-color="sidebarColor"
            :text-color="textColor"
            :active-text-color="activeTextColor"
            mode="horizontal"
            class="top-menu"
            @select="handleTopMenuSelect"
          >
            <el-menu-item
              v-for="route in topMenuRoutes"
              :key="route.path"
              :index="route.path"
            >
              <i v-if="route.meta && route.meta.icon" :class="'el-icon-' + route.meta.icon"></i>
              <span>{{ route.meta ? route.meta.title : '' }}</span>
            </el-menu-item>
          </el-menu>
        </div>
        <div class="navbar-right">
          <el-tooltip content="主题设置" placement="bottom">
            <i class="el-icon-setting theme-btn" @click="showThemeSettings = true"></i>
          </el-tooltip>
          <el-dropdown class="avatar-container" trigger="click">
            <div class="avatar-wrapper">
              <img :src="getAvatarUrl(avatar)" class="user-avatar" />
              <span class="user-name">{{ name }}</span>
              <i class="el-icon-caret-bottom" />
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="goToProfile">
                <i class="el-icon-user"></i>
                <span>个人中心</span>
              </el-dropdown-item>
              <el-dropdown-item divided @click.native="logout">
                <i class="el-icon-switch-button"></i>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <!-- 左侧子菜单区域 -->
      <div
        v-if="currentTopMenuChildren.length > 0"
        class="sidebar-container sidebar-children"
        :class="{ 'sidebar-collapsed': isCollapse }"
        :style="{ backgroundColor: sidebarColor }"
      >
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :background-color="sidebarColor"
          :text-color="textColor"
          :active-text-color="activeTextColor"
          :unique-opened="false"
          :collapse-transition="false"
          mode="vertical"
        >
          <sidebar-item
            v-for="route in currentTopMenuChildren"
            :key="route.path"
            :item="route"
            :base-path="getChildBasePath(route.path)"
          />
        </el-menu>
      </div>
      <div class="main-container" :class="{ 'no-sidebar': currentTopMenuChildren.length === 0 }">
        <div class="sub-navbar">
          <div class="left-menu">
            <i
              v-if="currentTopMenuChildren.length > 0"
              :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
              @click="toggleSideBar"
            ></i>
            <span class="title">{{ title }}</span>
          </div>
        </div>
        <app-main />
      </div>
    </template>

    <!-- 左侧模式：传统左侧边栏布局 -->
    <template v-else>
      <div class="sidebar-container" :class="{ 'sidebar-collapsed': isCollapse }" :style="{ backgroundColor: sidebarColor }">
        <sidebar-logo :is-collapse="isCollapse" />
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :background-color="sidebarColor"
          :text-color="textColor"
          :active-text-color="activeTextColor"
          :unique-opened="false"
          :collapse-transition="false"
          mode="vertical"
        >
          <sidebar-item
            v-for="route in routes"
            :key="route.path"
            :item="route"
            :base-path="route.path"
          />
        </el-menu>
      </div>
      <div class="main-container">
        <div class="navbar">
          <div class="left-menu">
            <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'" @click="toggleSideBar"></i>
          </div>
          <div class="right-menu">
            <el-tooltip content="主题设置" placement="bottom">
              <i class="el-icon-setting theme-btn" @click="showThemeSettings = true"></i>
            </el-tooltip>
            <el-dropdown class="avatar-container" trigger="click">
              <div class="avatar-wrapper">
                <img :src="getAvatarUrl(avatar)" class="user-avatar" />
                <span class="user-name">{{ name }}</span>
                <i class="el-icon-caret-bottom" />
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="goToProfile">
                  <i class="el-icon-user"></i>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click.native="logout">
                  <i class="el-icon-switch-button"></i>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
        <app-main />
      </div>
    </template>

    <!-- 主题设置抽屉 -->
    <theme-settings :visible.sync="showThemeSettings" />
  </div>
</template>

<script>
import { mapState } from 'vuex'
import SidebarItem from './components/Sidebar/SidebarItem'
import SidebarLogo from './components/Sidebar/Logo'
import AppMain from './components/AppMain'
import ThemeSettings from '@/components/ThemeSettings'

export default {
  name: 'Layout',
  components: {
    SidebarItem,
    SidebarLogo,
    AppMain,
    ThemeSettings
  },
  data() {
    return {
      showThemeSettings: false,
      activeTopMenuPath: ''
    }
  },
  computed: {
    ...mapState({
      sidebar: state => state.app.sidebar,
      name: state => state.user.name,
      avatar: state => state.user.avatar,
      routes: state => state.permission.routes,
      addRoutes: state => state.permission.addRoutes,
      title: state => state.settings.title,
      sidebarColor: state => state.settings.sidebarColor,
      sidebarPosition: state => state.settings.sidebarPosition
    }),
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    isCollapse() {
      return !this.sidebar.opened
    },
    textColor() {
      return this.isLightColor(this.sidebarColor) ? '#303133' : 'rgba(255,255,255,0.65)'
    },
    activeTextColor() {
      return '#13c2c2'
    },
    topMenuRoutes() {
      return this.sidebarPosition === 'top' ? this.addRoutes : this.routes
    },
    activeTopMenu() {
      if (this.sidebarPosition !== 'top') return ''
      const currentPath = this.$route.path
      const topRoute = this.topMenuRoutes.find(route => {
        if (currentPath === route.path) return true
        if (route.children) {
          return route.children.some(child => {
            const childPath = child.path.startsWith('/') ? child.path : `${route.path}/${child.path}`
            return currentPath.startsWith(childPath)
          })
        }
        return false
      })
      return topRoute ? topRoute.path : (this.topMenuRoutes[0]?.path || '')
    },
    currentTopMenuChildren() {
      if (this.sidebarPosition !== 'top') return []
      const currentTopRoute = this.topMenuRoutes.find(route => route.path === this.activeTopMenu)
      if (!currentTopRoute || !currentTopRoute.children) return []
      return currentTopRoute.children.filter(child => !child.hidden)
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    goToProfile() {
      this.$router.push('/profile')
    },
    getAvatarUrl(avatar) {
      if (!avatar) {
        return require('@/assets/logo.png')
      }
      if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
        return avatar
      }
      if (avatar.startsWith('/api/')) {
        return avatar
      }
      if (avatar.startsWith('/uploads/')) {
        return '/api' + avatar
      }
      const baseURL = process.env.VUE_APP_BASE_API || '/api'
      return baseURL + avatar
    },
    async logout() {
      try {
        await this.$store.dispatch('user/logout')
      } catch (_) {
        await this.$store.dispatch('user/fedLogout')
      } finally {
        this.$router.push('/login').catch(() => {})
      }
    },
    isLightColor(color) {
      if (!color) return false
      const hex = color.replace('#', '')
      const r = parseInt(hex.substr(0, 2), 16)
      const g = parseInt(hex.substr(2, 2), 16)
      const b = parseInt(hex.substr(4, 2), 16)
      const brightness = (r * 299 + g * 587 + b * 114) / 1000
      return brightness > 155
    },
    getChildBasePath(childPath) {
      if (childPath.startsWith('/')) {
        return childPath
      }
      const parentPath = this.activeTopMenu
      if (parentPath.endsWith('/')) {
        return parentPath + childPath
      }
      return parentPath + '/' + childPath
    },
    handleTopMenuSelect(index) {
      const selectedRoute = this.topMenuRoutes.find(route => route.path === index)
      if (!selectedRoute) return
      if (selectedRoute.children && selectedRoute.children.length > 0) {
        const firstChild = selectedRoute.children.find(child => !child.hidden)
        if (firstChild) {
          const childPath = firstChild.path.startsWith('/')
            ? firstChild.path
            : `${selectedRoute.path}/${firstChild.path}`
          this.$router.push(childPath).catch(err => {})
          return
        }
      }
      this.$router.push(index).catch(err => {})
    }
  }
}
</script>

<style lang="scss" scoped>
$primary: #13c2c2;
$sidebar-width: 208px;
$sidebar-collapsed-width: 64px;
$navbar-height: 48px;

.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;

  /* 左侧布局模式 */
  .sidebar-container {
    transition: width 0.2s;
    width: $sidebar-width;
    height: 100%;
    position: fixed;
    font-size: 0px;
    top: 0;
    bottom: 0;
    left: 0;
    z-index: 1001;
    overflow-y: auto;
    overflow-x: hidden;

    &.sidebar-collapsed {
      width: $sidebar-collapsed-width;
    }

    &::-webkit-scrollbar {
      width: 0;
      height: 0;
    }

    ::v-deep .el-menu {
      border-right: none;
    }

    ::v-deep .el-menu-item,
    ::v-deep .el-submenu__title {
      height: 44px;
      line-height: 44px;
      font-size: 14px;

      &:hover {
        background-color: rgba(255, 255, 255, 0.08) !important;
      }
    }

    ::v-deep .el-menu-item.is-active {
      background-color: $primary !important;
      color: #fff !important;
      border-right: 3px solid #fff;
    }

    ::v-deep .el-menu-item i,
    ::v-deep .el-submenu__title i {
      color: inherit;
    }

    &.sidebar-collapsed {
      ::v-deep .el-menu--collapse {
        width: $sidebar-collapsed-width;

        .el-menu-item,
        .el-submenu__title {
          padding: 0 !important;
          text-align: center;

          span { display: none !important; }

          i {
            margin: 0 !important;
            font-size: 20px !important;
          }
        }

        .el-submenu__icon-arrow { display: none; }
      }

      ::v-deep .el-tooltip {
        padding: 0 !important;
      }
    }

    &:not(.sidebar-collapsed) {
      ::v-deep .el-menu-item i,
      ::v-deep .el-submenu__title i {
        font-size: 16px;
        margin-right: 8px;
      }
    }
  }

  .sidebar-collapsed + .main-container {
    margin-left: $sidebar-collapsed-width;
  }

  .main-container {
    min-height: 100%;
    transition: margin-left 0.2s;
    margin-left: $sidebar-width;
    position: relative;
    background: #f0f2f5;

    .navbar {
      height: $navbar-height;
      overflow: hidden;
      position: relative;
      background: #fff;
      border-bottom: 1px solid #e8e8e8;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 16px;

      .left-menu {
        display: flex;
        align-items: center;

        i {
          font-size: 20px;
          cursor: pointer;
          color: #595959;
          padding: 4px;
          border-radius: 4px;

          &:hover {
            background: #f0f2f5;
            color: $primary;
          }
        }
      }

      .right-menu {
        display: flex;
        align-items: center;
        gap: 16px;

        .theme-btn {
          font-size: 18px;
          cursor: pointer;
          color: #595959;
          padding: 4px;
          border-radius: 4px;
          transition: color 0.2s;

          &:hover {
            color: $primary;
            background: #f0f2f5;
          }
        }

        .avatar-container {
          cursor: pointer;

          .avatar-wrapper {
            display: flex;
            align-items: center;

            .user-avatar {
              width: 28px;
              height: 28px;
              border-radius: 50%;
              margin-right: 8px;
              object-fit: cover;
            }

            .user-name {
              margin-right: 4px;
              font-size: 14px;
              color: #595959;
            }

            .el-icon-caret-bottom {
              font-size: 12px;
              color: #bfbfbf;
            }
          }

          ::v-deep .el-dropdown-menu__item {
            i {
              margin-right: 8px;
              font-size: 14px;
            }

            &:hover {
              background-color: #e6fffb;
              color: $primary;
            }
          }
        }
      }
    }
  }

  /* ==================== 顶部布局模式 ==================== */
  &.layout-top {
    .top-navbar {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      height: 56px;
      z-index: 1002;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px;

      .navbar-left {
        display: flex;
        align-items: center;
        flex: 1;
        overflow: hidden;
      }

      .navbar-right {
        display: flex;
        align-items: center;
        gap: 16px;

        .theme-btn {
          font-size: 18px;
          cursor: pointer;
          color: rgba(255, 255, 255, 0.65);
          transition: color 0.2s;

          &:hover {
            color: #fff;
          }
        }

        .avatar-container {
          cursor: pointer;

          .avatar-wrapper {
            display: flex;
            align-items: center;

            .user-avatar {
              width: 28px;
              height: 28px;
              border-radius: 50%;
              margin-right: 8px;
              object-fit: cover;
            }

            .user-name {
              margin-right: 4px;
              color: rgba(255, 255, 255, 0.85);
              font-size: 14px;
            }

            .el-icon-caret-bottom {
              color: rgba(255, 255, 255, 0.45);
              font-size: 12px;
            }
          }

          ::v-deep .el-dropdown-menu__item {
            i {
              margin-right: 8px;
              font-size: 14px;
            }

            &:hover {
              background-color: #e6fffb;
              color: $primary;
            }
          }
        }
      }

      ::v-deep .el-menu--horizontal {
        border-bottom: none;
        flex: 1;

        > .el-menu-item {
          height: 56px;
          line-height: 56px;
          border-bottom: 2px solid transparent;

          &.is-active {
            border-bottom-color: $primary;
          }

          i {
            margin-right: 5px;
          }
        }
      }
    }

    .sidebar-container.sidebar-children {
      top: 56px;
      height: calc(100% - 56px);
    }

    .main-container {
      margin-left: 0;
      padding-top: 56px;

      &:not(.no-sidebar) {
        margin-left: $sidebar-width;

        .sidebar-collapsed + & {
          margin-left: $sidebar-collapsed-width;
        }
      }

      .sub-navbar {
        height: $navbar-height;
        background: #fff;
        border-bottom: 1px solid #e8e8e8;
        display: flex;
        align-items: center;
        padding: 0 16px;

        .left-menu {
          display: flex;
          align-items: center;

          i {
            font-size: 20px;
            cursor: pointer;
            margin-right: 16px;
            color: #595959;

            &:hover {
              color: $primary;
            }
          }

          .title {
            font-size: 15px;
            font-weight: 500;
            color: #303133;
          }
        }
      }
    }
  }
}
</style>

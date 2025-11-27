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
            <i class="el-icon-brush theme-btn" @click="showThemeSettings = true"></i>
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
            <span class="title">{{ title }}</span>
          </div>
          <div class="right-menu">
            <el-tooltip content="主题设置" placement="bottom">
              <i class="el-icon-brush theme-btn" @click="showThemeSettings = true"></i>
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
      activeTopMenuPath: '' // 当前激活的顶部菜单路径
    }
  },
  computed: {
    ...mapState({
      sidebar: state => state.app.sidebar,
      name: state => state.user.name,
      avatar: state => state.user.avatar,
      routes: state => state.permission.routes,
      addRoutes: state => state.permission.addRoutes, // 动态路由（后端返回的）
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
    // 根据侧边栏颜色自动计算文字颜色
    textColor() {
      return this.isLightColor(this.sidebarColor) ? '#303133' : '#bfcbd9'
    },
    activeTextColor() {
      // 浅色背景用深蓝色，深色背景用亮蓝色
      return this.isLightColor(this.sidebarColor) ? '#409EFF' : '#66b1ff'
    },
    // 顶部模式显示的路由（只显示动态路由，不显示静态路由如首页）
    topMenuRoutes() {
      return this.sidebarPosition === 'top' ? this.addRoutes : this.routes
    },
    // 顶部模式：当前激活的一级菜单
    activeTopMenu() {
      if (this.sidebarPosition !== 'top') return ''

      // 根据当前路由找到对应的一级菜单
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
    // 顶部模式：当前一级菜单的子菜单
    currentTopMenuChildren() {
      if (this.sidebarPosition !== 'top') return []

      const currentTopRoute = this.topMenuRoutes.find(route => route.path === this.activeTopMenu)
      if (!currentTopRoute || !currentTopRoute.children) return []

      // 过滤掉隐藏的子菜单
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
      const baseURL = process.env.VUE_APP_BASE_API || '/api'
      return baseURL + avatar
    },
    async logout() {
      try {
        await this.$store.dispatch('user/logout')
      } catch (error) {
        console.error('退出登录失败:', error)
        // 即使退出接口失败，也要清除本地状态
        await this.$store.dispatch('user/fedLogout')
      } finally {
        // 跳转到登录页
        this.$router.push('/login').catch(err => {
          console.log('路由跳转:', err.message)
        })
      }
    },
    // 判断颜色是否为浅色
    isLightColor(color) {
      if (!color) return false
      // 移除 # 号
      const hex = color.replace('#', '')
      // 转换为 RGB
      const r = parseInt(hex.substr(0, 2), 16)
      const g = parseInt(hex.substr(2, 2), 16)
      const b = parseInt(hex.substr(4, 2), 16)
      // 计算亮度 (使用 YIQ 公式)
      const brightness = (r * 299 + g * 587 + b * 114) / 1000
      return brightness > 155
    },
    // 获取子菜单的完整基础路径
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
    // 顶部菜单选择事件
    handleTopMenuSelect(index) {
      const selectedRoute = this.topMenuRoutes.find(route => route.path === index)
      if (!selectedRoute) return

      // 如果该菜单有子菜单，跳转到第一个子菜单
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

      // 如果没有子菜单，直接跳转
      this.$router.push(index).catch(err => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;

  /* 左侧布局模式 */
  .sidebar-container {
    transition: width 0.28s, background-color 0.3s;
    width: 200px;
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
      width: 64px;
    }

    /* 自定义滚动条样式 */
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background-color: rgba(144, 147, 153, 0.3);
      border-radius: 3px;

      &:hover {
        background-color: rgba(144, 147, 153, 0.5);
      }
    }

    &::-webkit-scrollbar-track {
      background-color: transparent;
    }

    /* 深度选择器：覆盖 Element UI 菜单项的文字颜色 */
    ::v-deep .el-menu {
      border-right: none;
    }

    ::v-deep .el-menu-item,
    ::v-deep .el-submenu__title {
      &:hover {
        background-color: rgba(0, 0, 0, 0.1) !important;
      }
    }

    ::v-deep .el-menu-item.is-active {
      background-color: rgba(64, 158, 255, 0.2) !important;
    }

    /* 确保图标颜色继承文字颜色 */
    ::v-deep .el-menu-item i,
    ::v-deep .el-submenu__title i {
      color: inherit;
    }

    /* 收缩状态下的样式优化 */
    &.sidebar-collapsed {
      ::v-deep .el-menu--collapse {
        width: 64px;

        .el-menu-item,
        .el-submenu__title {
          padding: 0 !important;
          text-align: center;

          span {
            display: none !important;
          }

          i {
            margin: 0 !important;
            font-size: 22px !important;
            transition: font-size 0.3s;
          }
        }

        /* 子菜单图标 */
        .el-submenu__icon-arrow {
          display: none;
        }
      }

      /* 隐藏所有文字，只保留图标 */
      ::v-deep .el-tooltip {
        padding: 0 !important;
      }
    }

    /* 展开状态下的图标大小 */
    &:not(.sidebar-collapsed) {
      ::v-deep .el-menu-item i,
      ::v-deep .el-submenu__title i {
        font-size: 18px;
        margin-right: 10px;
        transition: font-size 0.3s;
      }
    }
  }

  .sidebar-collapsed + .main-container {
    margin-left: 64px;
  }

  .main-container {
    min-height: 100%;
    transition: margin-left 0.28s;
    margin-left: 200px;
    position: relative;

    .navbar {
      height: 50px;
      overflow: hidden;
      position: relative;
      background: #fff;
      box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px;

      .left-menu {
        display: flex;
        align-items: center;

        i {
          font-size: 24px;
          cursor: pointer;
          margin-right: 20px;
        }

        .title {
          font-size: 18px;
          font-weight: bold;
        }
      }

      .right-menu {
        display: flex;
        align-items: center;
        gap: 20px;

        .theme-btn {
          font-size: 20px;
          cursor: pointer;
          color: #606266;
          transition: all 0.3s;

          &:hover {
            color: #409EFF;
            transform: rotate(20deg);
          }
        }

        .avatar-container {
          cursor: pointer;

          .avatar-wrapper {
            display: flex;
            align-items: center;

            .user-avatar {
              width: 32px;
              height: 32px;
              border-radius: 50%;
              margin-right: 8px;
              object-fit: cover;
            }

            .user-name {
              margin-right: 5px;
            }
          }

          ::v-deep .el-dropdown-menu__item {
            i {
              margin-right: 8px;
              font-size: 14px;
            }

            &:hover {
              background-color: #ecf5ff;
              color: #409EFF;
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
      height: 60px;
      z-index: 1002;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px;
      box-shadow: 0 2px 8px rgba(0, 21, 41, .08);

      .navbar-left {
        display: flex;
        align-items: center;
        flex: 1;
        overflow: hidden;
      }

      .navbar-right {
        display: flex;
        align-items: center;
        gap: 20px;

        .theme-btn {
          font-size: 20px;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            transform: rotate(20deg);
          }
        }

        .avatar-container {
          cursor: pointer;

          .avatar-wrapper {
            display: flex;
            align-items: center;

            .user-avatar {
              width: 32px;
              height: 32px;
              border-radius: 50%;
              margin-right: 8px;
              object-fit: cover;
            }

            .user-name {
              margin-right: 5px;
            }
          }

          ::v-deep .el-dropdown-menu__item {
            i {
              margin-right: 8px;
              font-size: 14px;
            }

            &:hover {
              background-color: #ecf5ff;
              color: #409EFF;
            }
          }
        }
      }

      ::v-deep .el-menu--horizontal {
        border-bottom: none;
        flex: 1;

        > .el-menu-item {
          height: 60px;
          line-height: 60px;
          border-bottom: 2px solid transparent;

          &.is-active {
            border-bottom-color: currentColor;
          }

          i {
            margin-right: 5px;
          }
        }
      }
    }

    .sidebar-container.sidebar-children {
      top: 60px;
      height: calc(100% - 60px);
    }

    .main-container {
      margin-left: 0;
      padding-top: 60px;

      &:not(.no-sidebar) {
        margin-left: 200px;

        .sidebar-collapsed + & {
          margin-left: 64px;
        }
      }

      .sub-navbar {
        height: 50px;
        background: #fff;
        box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
        display: flex;
        align-items: center;
        padding: 0 20px;

        .left-menu {
          display: flex;
          align-items: center;

          i {
            font-size: 24px;
            cursor: pointer;
            margin-right: 20px;
          }

          .title {
            font-size: 18px;
            font-weight: bold;
          }
        }
      }
    }
  }
}
</style>

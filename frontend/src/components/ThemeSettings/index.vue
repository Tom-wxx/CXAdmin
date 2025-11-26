<template>
  <div class="theme-settings-wrapper">
    <el-drawer
      title="主题设置"
      :visible.sync="visible"
      direction="rtl"
      size="300px"
      @close="handleClose"
    >
      <div class="theme-settings-content">
        <!-- 侧边栏位置设置 -->
        <div class="sidebar-position-section">
          <div class="section-title">侧边栏位置</div>
          <el-radio-group v-model="sidebarPosition" @change="handlePositionChange">
            <el-radio label="left">左侧</el-radio>
            <el-radio label="top">顶部</el-radio>
          </el-radio-group>
          <div class="position-description">
            <p v-if="sidebarPosition === 'left'" class="desc-text">
              <i class="el-icon-info"></i>
              传统左侧边栏布局，所有菜单显示在左侧
            </p>
            <p v-else class="desc-text">
              <i class="el-icon-info"></i>
              一级菜单显示在顶部，子菜单显示在左侧
            </p>
          </div>
        </div>

        <!-- 当前颜色显示 -->
        <div class="current-color-section">
          <div class="section-title">当前侧边栏颜色</div>
          <div class="current-color-display" :style="{ backgroundColor: currentColor }">
            <span class="color-value">{{ currentColor }}</span>
          </div>
        </div>

        <!-- 自定义颜色选择器 -->
        <div class="color-picker-section">
          <div class="section-title">自定义颜色</div>
          <el-color-picker
            v-model="selectedColor"
            show-alpha
            :predefine="predefineColors"
            @change="handleColorChange"
          />
        </div>

        <!-- 预设主题 -->
        <div class="preset-themes-section">
          <div class="section-title">预设主题</div>
          <div class="theme-list">
            <div
              v-for="theme in themes"
              :key="theme.name"
              class="theme-item"
              :class="{ active: currentColor === theme.color }"
              @click="selectTheme(theme)"
            >
              <div class="theme-color" :style="{ backgroundColor: theme.color }"></div>
              <div class="theme-name">{{ theme.name }}</div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="actions">
          <el-button type="primary" size="small" @click="applyTheme">应用</el-button>
          <el-button size="small" @click="resetTheme">重置为默认</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'ThemeSettings',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      selectedColor: '',
      sidebarPosition: 'left',
      // 预设主题
      themes: [
        { name: '经典蓝', color: '#304156' },
        { name: '深空灰', color: '#2c3e50' },
        { name: '商务黑', color: '#1f2d3d' },
        { name: '科技蓝', color: '#1e3a8a' },
        { name: '优雅紫', color: '#5b21b6' },
        { name: '森林绿', color: '#065f46' },
        { name: '浅灰白', color: '#f5f5f5' },
        { name: '天空蓝', color: '#bfdbfe' },
        { name: '薄荷绿', color: '#d1fae5' },
        { name: '柔和粉', color: '#fce7f3' },
        { name: '米白色', color: '#fef3c7' },
        { name: '淡紫色', color: '#e9d5ff' }
      ],
      // 预定义颜色（颜色选择器）
      predefineColors: [
        '#304156',
        '#2c3e50',
        '#1f2d3d',
        '#1e3a8a',
        '#5b21b6',
        '#065f46',
        '#000000',
        '#333333',
        '#f5f5f5',
        '#bfdbfe',
        '#d1fae5',
        '#fce7f3',
        '#fef3c7',
        '#e9d5ff'
      ]
    }
  },
  computed: {
    ...mapState({
      currentColor: state => state.settings.sidebarColor,
      currentPosition: state => state.settings.sidebarPosition
    })
  },
  watch: {
    currentColor: {
      immediate: true,
      handler(val) {
        this.selectedColor = val
      }
    },
    currentPosition: {
      immediate: true,
      handler(val) {
        this.sidebarPosition = val
      }
    }
  },
  methods: {
    handleClose() {
      this.$emit('update:visible', false)
    },
    handleColorChange(color) {
      // 实时预览
      this.$store.dispatch('settings/setSidebarColor', color)
    },
    handlePositionChange(position) {
      // 实时预览
      this.$store.dispatch('settings/setSidebarPosition', position)
      this.$message.success(`已切换到${position === 'left' ? '左侧' : '顶部'}布局模式`)
    },
    selectTheme(theme) {
      this.selectedColor = theme.color
      this.$store.dispatch('settings/setSidebarColor', theme.color)
    },
    applyTheme() {
      this.$store.dispatch('settings/setSidebarColor', this.selectedColor)
      this.$message.success('主题已应用')
      this.handleClose()
    },
    resetTheme() {
      const defaultColor = '#304156'
      const defaultPosition = 'left'
      this.selectedColor = defaultColor
      this.sidebarPosition = defaultPosition
      this.$store.dispatch('settings/setSidebarColor', defaultColor)
      this.$store.dispatch('settings/setSidebarPosition', defaultPosition)
      this.$message.success('已重置为默认主题')
    }
  }
}
</script>

<style lang="scss" scoped>
.theme-settings-content {
  padding: 20px;

  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }

  .sidebar-position-section {
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #ebeef5;

    .position-description {
      margin-top: 12px;

      .desc-text {
        font-size: 12px;
        color: #909399;
        margin: 0;
        display: flex;
        align-items: center;
        gap: 5px;

        i {
          font-size: 14px;
        }
      }
    }
  }

  .current-color-section {
    margin-bottom: 30px;

    .current-color-display {
      height: 80px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      transition: all 0.3s;

      .color-value {
        color: #fff;
        font-size: 16px;
        font-weight: 600;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
      }
    }
  }

  .color-picker-section {
    margin-bottom: 30px;

    ::v-deep .el-color-picker {
      .el-color-picker__trigger {
        width: 60px;
        height: 60px;
        border-radius: 8px;
      }
    }
  }

  .preset-themes-section {
    margin-bottom: 30px;

    .theme-list {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 10px;
      max-height: 400px;
      overflow-y: auto;
      padding-right: 5px;

      /* 自定义滚动条 */
      &::-webkit-scrollbar {
        width: 4px;
      }

      &::-webkit-scrollbar-thumb {
        background-color: rgba(144, 147, 153, 0.3);
        border-radius: 2px;
      }
    }

    .theme-item {
      padding: 12px;
      border: 2px solid #e4e7ed;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;

      &:hover {
        border-color: #409EFF;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
      }

      &.active {
        border-color: #409EFF;
        background-color: #ecf5ff;
      }

      .theme-color {
        width: 100%;
        height: 40px;
        border-radius: 6px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      .theme-name {
        font-size: 12px;
        color: #606266;
        font-weight: 500;
      }
    }
  }

  .actions {
    display: flex;
    gap: 10px;

    .el-button {
      flex: 1;
    }
  }
}
</style>

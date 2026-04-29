<template>
  <div class="theme-settings-wrapper">
    <el-drawer
      title="主题设置"
      :visible.sync="visible"
      direction="rtl"
      size="280px"
      @close="handleClose"
    >
      <div class="theme-settings-content">
        <!-- 侧边栏位置设置 -->
        <div class="setting-section">
          <div class="section-title">布局模式</div>
          <el-radio-group v-model="sidebarPosition" size="small" @change="handlePositionChange">
            <el-radio-button label="left">左侧</el-radio-button>
            <el-radio-button label="top">顶部</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 预设主题 -->
        <div class="setting-section">
          <div class="section-title">侧边栏配色</div>
          <div class="theme-list">
            <div
              v-for="theme in themes"
              :key="theme.name"
              class="theme-item"
              :class="{ active: currentColor === theme.color }"
              @click="selectTheme(theme)"
            >
              <div class="theme-color" :style="{ backgroundColor: theme.color }">
                <i v-if="currentColor === theme.color" class="el-icon-check"></i>
              </div>
              <div class="theme-name">{{ theme.name }}</div>
            </div>
          </div>
        </div>

        <!-- 自定义颜色 -->
        <div class="setting-section">
          <div class="section-title">自定义颜色</div>
          <el-color-picker
            v-model="selectedColor"
            :predefine="predefineColors"
            @change="handleColorChange"
          />
        </div>

        <!-- 操作 -->
        <div class="setting-actions">
          <el-button size="small" @click="resetTheme">恢复默认</el-button>
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
      themes: [
        { name: '暗夜蓝', color: '#001529' },
        { name: '经典灰', color: '#304156' },
        { name: '深灰', color: '#2c3e50' },
        { name: '深黑', color: '#1f2d3d' },
        { name: '科技蓝', color: '#1e3a8a' },
        { name: '暗紫', color: '#5b21b6' },
        { name: '墨绿', color: '#065f46' },
        { name: '纯白', color: '#ffffff' }
      ],
      predefineColors: [
        '#001529', '#304156', '#2c3e50', '#1f2d3d',
        '#1e3a8a', '#5b21b6', '#065f46', '#ffffff'
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
      this.$store.dispatch('settings/setSidebarColor', color)
    },
    handlePositionChange(position) {
      this.$store.dispatch('settings/setSidebarPosition', position)
    },
    selectTheme(theme) {
      this.selectedColor = theme.color
      this.$store.dispatch('settings/setSidebarColor', theme.color)
    },
    resetTheme() {
      this.selectedColor = '#001529'
      this.sidebarPosition = 'left'
      this.$store.dispatch('settings/setSidebarColor', '#001529')
      this.$store.dispatch('settings/setSidebarPosition', 'left')
      this.$message.success('已恢复默认')
    }
  }
}
</script>

<style lang="scss" scoped>
.theme-settings-content {
  padding: 16px 20px;

  .setting-section {
    margin-bottom: 24px;
  }

  .section-title {
    font-size: 13px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }

  .theme-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 8px;

    .theme-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;

      .theme-color {
        width: 100%;
        height: 32px;
        border-radius: 4px;
        border: 1px solid #e8e8e8;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: transform 0.2s;

        i {
          color: #fff;
          font-size: 14px;
          font-weight: bold;
        }
      }

      .theme-name {
        font-size: 11px;
        color: #8c8c8c;
        margin-top: 4px;
        text-align: center;
      }

      &.active .theme-color {
        border-color: #13c2c2;
        box-shadow: 0 0 0 1px #13c2c2;
      }

      &:hover .theme-color {
        transform: scale(1.05);
      }
    }
  }

  .setting-actions {
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;

    .el-button {
      width: 100%;
    }
  }
}
</style>

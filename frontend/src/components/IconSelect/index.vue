<template>
  <div class="icon-select">
    <el-input
      v-model="filterText"
      placeholder="搜索图标"
      clearable
      prefix-icon="Search"
      style="margin-bottom: 10px"
    />
    <div class="icon-list">
      <div
        v-for="name in filteredIcons"
        :key="name"
        class="icon-item"
        :class="{ active: selectedIcon === name }"
        @click="selectIcon(name)"
      >
        <el-icon><component :is="name" /></el-icon>
        <div class="icon-name">{{ name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import * as ElementPlusIcons from '@element-plus/icons-vue'

// Element Plus 全量图标组件名（PascalCase），均已在 main.js 全局注册
const ICON_NAMES = Object.keys(ElementPlusIcons)

export default {
  name: 'IconSelect',
  data() {
    return {
      filterText: '',
      selectedIcon: '',
      iconList: ICON_NAMES
    }
  },
  computed: {
    filteredIcons() {
      const kw = this.filterText.trim().toLowerCase()
      if (!kw) return this.iconList
      return this.iconList.filter(name => name.toLowerCase().includes(kw))
    }
  },
  methods: {
    selectIcon(name) {
      this.selectedIcon = name
      // 直接存储 Element Plus 组件名（PascalCase）
      this.$emit('selected', name)
    },
    reset() {
      this.filterText = ''
      this.selectedIcon = ''
    }
  }
}
</script>

<style scoped lang="scss">
.icon-select {
  .icon-list {
    max-height: 400px;
    overflow-y: auto;
    display: flex;
    flex-wrap: wrap;
    padding: 10px 0;
  }

  .icon-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 80px;
    height: 80px;
    margin: 5px;
    padding: 10px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
      background-color: #ecf5ff;
      transform: scale(1.05);
    }

    &.active {
      border-color: #409eff;
      background-color: #ecf5ff;
    }

    .el-icon {
      font-size: 24px;
      color: #606266;
    }

    .icon-name {
      margin-top: 8px;
      font-size: 12px;
      color: #909399;
      text-align: center;
      word-break: break-all;
      line-height: 1.2;
    }
  }
}
</style>

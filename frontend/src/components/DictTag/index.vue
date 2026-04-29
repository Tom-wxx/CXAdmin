<template>
  <span class="dict-tag">
    <template v-if="Array.isArray(values) && values.length">
      <el-tag
        v-for="item in matchedItems"
        :key="item.value"
        :type="item.type || ''"
        :effect="effect"
        :size="size"
        class="dict-tag__item"
      >
        {{ item.label }}
      </el-tag>
    </template>
    <el-tag
      v-else-if="single"
      :type="single.type || ''"
      :effect="effect"
      :size="size"
    >
      {{ single.label }}
    </el-tag>
    <span v-else class="dict-tag__placeholder">{{ placeholder }}</span>
  </span>
</template>

<script>
export default {
  name: 'DictTag',
  props: {
    options: {
      type: Array,
      default: () => []
    },
    value: {
      type: [String, Number, Array],
      default: ''
    },
    separator: {
      type: String,
      default: ','
    },
    size: {
      type: String,
      default: 'small'
    },
    effect: {
      type: String,
      default: 'light'
    },
    placeholder: {
      type: String,
      default: '-'
    }
  },
  computed: {
    values() {
      if (this.value === null || this.value === undefined || this.value === '') return []
      if (Array.isArray(this.value)) return this.value
      if (typeof this.value === 'string' && this.value.includes(this.separator)) {
        return this.value.split(this.separator)
      }
      return [this.value]
    },
    matchedItems() {
      return this.values.map(v => {
        const found = this.options.find(o => String(o.value) === String(v))
        return found || { value: v, label: v, type: '' }
      })
    },
    single() {
      return this.matchedItems.length === 1 ? this.matchedItems[0] : null
    }
  }
}
</script>

<style scoped>
.dict-tag__item + .dict-tag__item {
  margin-left: 4px;
}
.dict-tag__placeholder {
  color: #c0c4cc;
}
</style>

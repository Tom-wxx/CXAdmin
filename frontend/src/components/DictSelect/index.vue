<template>
  <el-select
    :value="value"
    :placeholder="placeholder"
    :clearable="clearable"
    :multiple="multiple"
    :disabled="disabled"
    :size="size"
    :style="{ width: width }"
    @input="onInput"
    @change="onChange"
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
      :disabled="item.disabled"
    />
  </el-select>
</template>

<script>
export default {
  name: 'DictSelect',
  props: {
    value: {
      type: [String, Number, Array],
      default: ''
    },
    options: {
      type: Array,
      default: () => []
    },
    placeholder: {
      type: String,
      default: '请选择'
    },
    clearable: {
      type: Boolean,
      default: true
    },
    multiple: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    size: {
      type: String,
      default: 'small'
    },
    width: {
      type: String,
      default: '200px'
    }
  },
  methods: {
    onInput(val) {
      this.$emit('input', val)
    },
    onChange(val) {
      this.$emit('change', val)
    }
  }
}
</script>

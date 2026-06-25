<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'App' })
</script>

<style lang="scss">
#app {
  height: 100%;
}
</style>

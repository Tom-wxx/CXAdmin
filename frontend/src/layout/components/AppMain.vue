<template>
  <section class="app-main">
    <transition name="fade" mode="out-in">
      <router-view :key="key" />
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',
  computed: {
    key() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  min-height: calc(100vh - 48px);
  width: 100%;
  position: relative;
  overflow: hidden;
  padding: 16px;
  background: #f0f2f5;
}

.fade-leave-active,
.fade-enter-active {
  transition: opacity 0.2s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>

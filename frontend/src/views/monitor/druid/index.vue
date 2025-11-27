<template>
  <div class="druid-container">
    <iframe
      :src="druidUrl"
      frameborder="0"
      class="druid-iframe"
    ></iframe>
  </div>
</template>

<script>
export default {
  name: 'Druid',
  data() {
    return {
      druidUrl: process.env.VUE_APP_BASE_API + '/druid/index.html'
    }
  }
}
</script>

<style lang="scss" scoped>
.druid-container {
  width: 100%;
  height: calc(100vh - 84px);

  .druid-iframe {
    width: 100%;
    height: 100%;
    border: none;
  }
}
</style>

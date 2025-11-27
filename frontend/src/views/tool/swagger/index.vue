<template>
  <div class="swagger-container">
    <iframe
      :src="swaggerUrl"
      frameborder="0"
      class="swagger-iframe"
    ></iframe>
  </div>
</template>

<script>
export default {
  name: 'Swagger',
  data() {
    return {
      swaggerUrl: process.env.VUE_APP_BASE_API + '/swagger-ui/index.html'
    }
  }
}
</script>

<style lang="scss" scoped>
.swagger-container {
  width: 100%;
  height: calc(100vh - 84px);

  .swagger-iframe {
    width: 100%;
    height: 100%;
    border: none;
  }
}
</style>

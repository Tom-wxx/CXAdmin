<template>
  <div class="swagger-container">
    <iframe
      :src="swaggerUrl"
      frameborder="0"
      class="swagger-iframe"
    ></iframe>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'Swagger' })

const swaggerUrl = import.meta.env.VITE_APP_BASE_API + '/swagger-ui/index.html'
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

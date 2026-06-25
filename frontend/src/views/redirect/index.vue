<template>
  <div class="redirect-container" />
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'

defineOptions({ name: 'Redirect' })

const route = useRoute()
const router = useRouter()

const { params, query } = route
const rawPath = params.path
const path = Array.isArray(rawPath) ? rawPath.join('/') : rawPath
router.replace({ path: '/' + path, query })
</script>

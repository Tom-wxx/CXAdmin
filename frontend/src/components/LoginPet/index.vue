<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { normalizeLoginPetType, type LoginPetType } from '@/types/login-pet'
import CatPet from './CatPet.vue'
import DogPet from './DogPet.vue'
import OwlPet from './OwlPet.vue'
import { calculatePetMotion, IDLE_PET_MOTION, type PetMotion } from './tracking'

const props = withDefaults(defineProps<{
  type?: LoginPetType
  size?: number
  interactive?: boolean
}>(), {
  type: 'cat',
  size: 108,
  interactive: true
})

const rootRef = ref<HTMLElement | null>(null)
const motion = ref<PetMotion>({ ...IDLE_PET_MOTION })
const reducedMotion = ref(false)
const normalizedType = computed(() => normalizeLoginPetType(props.type))
const componentMap = {
  cat: CatPet,
  dog: DogPet,
  owl: OwlPet
} as const
const petComponent = computed(() => componentMap[normalizedType.value])
const rootStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`,
  '--pet-eye-x': `${motion.value.eyeX}px`,
  '--pet-eye-y': `${motion.value.eyeY}px`
}))

let frameId: number | null = null
let latestPointer: PointerEvent | null = null
let mediaQuery: MediaQueryList | null = null
let pointerTrackingEnabled = false

function applyPointer(): void {
  frameId = null
  if (!latestPointer || !rootRef.value || document.hidden) return

  motion.value = calculatePetMotion(
    { x: latestPointer.clientX, y: latestPointer.clientY },
    rootRef.value.getBoundingClientRect(),
    reducedMotion.value ? 0.35 : 1
  )
}

function handlePointerMove(event: PointerEvent): void {
  latestPointer = event
  if (frameId === null) {
    frameId = window.requestAnimationFrame(applyPointer)
  }
}

function resetMotion(): void {
  latestPointer = null
  if (frameId !== null) {
    window.cancelAnimationFrame(frameId)
    frameId = null
  }
  motion.value = { ...IDLE_PET_MOTION }
}

function handleMediaChange(event: MediaQueryListEvent): void {
  reducedMotion.value = event.matches
  resetMotion()
}

function handleVisibilityChange(): void {
  if (document.hidden) resetMotion()
}

onMounted(() => {
  mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)')
  reducedMotion.value = mediaQuery.matches
  mediaQuery.addEventListener('change', handleMediaChange)
  document.addEventListener('visibilitychange', handleVisibilityChange)

  pointerTrackingEnabled = props.interactive && !window.matchMedia('(pointer: coarse)').matches
  if (pointerTrackingEnabled) {
    window.addEventListener('pointermove', handlePointerMove, { passive: true })
    window.addEventListener('blur', resetMotion)
  }
})

onBeforeUnmount(() => {
  if (pointerTrackingEnabled) {
    window.removeEventListener('pointermove', handlePointerMove)
    window.removeEventListener('blur', resetMotion)
  }
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  mediaQuery?.removeEventListener('change', handleMediaChange)
  if (frameId !== null) window.cancelAnimationFrame(frameId)
})
</script>

<template>
  <span
    ref="rootRef"
    class="login-pet"
    data-testid="login-pet"
    :data-pet="normalizedType"
    :style="rootStyle"
    aria-live="off"
  >
    <component
      :is="petComponent"
      :motion="motion"
      :reduced-motion="reducedMotion"
    />
  </span>
</template>

<style scoped>
.login-pet {
  display: inline-block;
  flex: 0 0 auto;
  line-height: 0;
  pointer-events: none;
  contain: layout paint;
}

.login-pet :deep(svg) {
  width: 100%;
  height: 100%;
  overflow: visible;
}
</style>

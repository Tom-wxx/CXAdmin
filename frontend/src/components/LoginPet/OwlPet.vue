<script setup lang="ts">
import { computed } from 'vue'
import type { PetMotion } from './tracking'

const props = defineProps<{
  motion: PetMotion
  reducedMotion: boolean
}>()

const headStyle = computed(() => ({
  transform: `translate(${props.motion.headX}px, ${props.motion.headY}px) rotate(${props.motion.rotate}deg)`,
  transformOrigin: '60px 65px'
}))

const eyeStyle = computed(() => ({
  transform: `translate(${props.motion.eyeX}px, ${props.motion.eyeY}px)`
}))
</script>

<template>
  <svg
    viewBox="0 0 120 120"
    role="img"
    aria-label="动态猫头鹰"
    :class="{ 'reduced-motion': reducedMotion }"
  >
    <g class="owl-wing left">
      <path d="M30 54 Q4 67 20 101 Q31 94 39 79Z" />
    </g>
    <g class="owl-wing right">
      <path d="M90 54 Q116 67 100 101 Q89 94 81 79Z" />
    </g>
    <g class="pet-head" :style="headStyle">
      <path
        class="pet-fill"
        d="M20 33 L31 14 L48 27 Q60 21 72 27 L89 14 L100 33 Q106 62 94 87 Q82 104 60 104 Q38 104 26 87 Q14 62 20 33Z"
      />
      <circle class="eye" cx="43" cy="57" r="18" />
      <circle class="eye" cx="77" cy="57" r="18" />
      <g class="pupils" :style="eyeStyle">
        <circle cx="43" cy="58" r="7" />
        <circle cx="77" cy="58" r="7" />
      </g>
      <path class="beak" d="M52 71 L68 71 L60 87Z" />
    </g>
  </svg>
</template>

<style scoped lang="scss">
.pet-fill,
.owl-wing {
  stroke: #071f2d;
  stroke-width: 5;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.pet-fill {
  fill: #73d8d0;
}

.owl-wing {
  fill: #58b9b5;
  transform-origin: center;
  animation: owl-wing-breathe 3s ease-in-out infinite;
}

.owl-wing.right {
  animation-direction: reverse;
}

.eye {
  fill: #ffffff;
  stroke: #071f2d;
  stroke-width: 4;
  transform-box: fill-box;
  transform-origin: center;
  animation: blink 6.2s ease-in-out infinite;
}

.pupils {
  fill: #071f2d;
  transition: transform 90ms linear;
}

.beak {
  fill: #f7b541;
  stroke: #071f2d;
  stroke-width: 3;
  stroke-linejoin: round;
}

.pet-head {
  transition: transform 110ms linear;
}

.reduced-motion * {
  animation: none !important;
  transition-duration: 80ms !important;
}

@keyframes blink {
  0%, 44%, 48%, 100% { transform: scaleY(1); }
  46% { transform: scaleY(0.08); }
}

@keyframes owl-wing-breathe {
  0%, 100% { transform: rotate(0); }
  50% { transform: rotate(4deg); }
}
</style>

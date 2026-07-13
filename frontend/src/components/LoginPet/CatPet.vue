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

const animationStyle = computed(() => ({
  animationPlayState: props.reducedMotion ? 'paused' : 'running'
}))
</script>

<template>
  <svg
    viewBox="0 0 120 120"
    role="img"
    aria-label="动态猫咪"
    :class="{ 'reduced-motion': reducedMotion }"
  >
    <g class="cat-tail" :style="animationStyle">
      <path d="M88 88 C116 78 116 108 94 108" />
    </g>
    <g class="pet-head" :style="headStyle">
      <path
        class="pet-fill"
        d="M25 42 L18 16 L42 30 Q60 20 78 30 L102 16 L95 43 Q101 55 96 72 Q90 96 60 98 Q30 96 24 72 Q19 55 25 42Z"
      />
      <path class="inner-ear" d="M26 31 L23 23 L36 32Z M84 32 L97 23 L94 34Z" />
      <ellipse class="eye" cx="44" cy="59" rx="12" ry="14" :style="animationStyle" />
      <ellipse class="eye" cx="76" cy="59" rx="12" ry="14" :style="animationStyle" />
      <g class="pupils" :style="eyeStyle">
        <circle cx="44" cy="60" r="5" />
        <circle cx="76" cy="60" r="5" />
      </g>
      <path class="nose" d="M55 75 Q60 70 65 75 Q60 82 55 75Z" />
      <path class="mouth" d="M60 80 Q53 88 48 82 M60 80 Q67 88 72 82" />
    </g>
  </svg>
</template>

<style scoped lang="scss">
.pet-fill,
.cat-tail {
  stroke: #071f2d;
  stroke-width: 5;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.pet-fill {
  fill: #8fe4dd;
}

.cat-tail {
  fill: none;
  transform-origin: 92px 96px;
  animation: cat-tail-wave 2.6s ease-in-out infinite;
}

.inner-ear {
  fill: #ef9ca8;
}

.eye {
  fill: #ffffff;
  stroke: #071f2d;
  stroke-width: 4;
  transform-box: fill-box;
  transform-origin: center;
  animation: blink 6s ease-in-out infinite;
}

.pupils {
  fill: #071f2d;
  transition: transform 90ms linear;
}

.nose {
  fill: #ef7f91;
  stroke: #071f2d;
  stroke-width: 3;
}

.mouth {
  fill: none;
  stroke: #071f2d;
  stroke-width: 3;
  stroke-linecap: round;
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

@keyframes cat-tail-wave {
  0%, 100% { transform: rotate(-5deg); }
  50% { transform: rotate(8deg); }
}
</style>

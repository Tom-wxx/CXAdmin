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
    aria-label="动态小狗"
    :class="{ 'reduced-motion': reducedMotion }"
  >
    <g class="dog-tail">
      <path d="M91 88 Q116 74 111 98" />
    </g>
    <g class="pet-head" :style="headStyle">
      <path class="dog-ear left" d="M31 35 Q5 31 13 67 Q18 84 34 71Z" />
      <path class="dog-ear right" d="M89 35 Q115 31 107 67 Q102 84 86 71Z" />
      <path
        class="pet-fill"
        d="M28 34 Q60 14 92 34 Q101 55 94 79 Q84 99 60 99 Q36 99 26 79 Q19 55 28 34Z"
      />
      <ellipse class="eye" cx="44" cy="58" rx="11" ry="13" />
      <ellipse class="eye" cx="76" cy="58" rx="11" ry="13" />
      <g class="pupils" :style="eyeStyle">
        <circle cx="44" cy="59" r="5" />
        <circle cx="76" cy="59" r="5" />
      </g>
      <ellipse class="muzzle" cx="60" cy="77" rx="19" ry="15" />
      <ellipse class="nose" cx="60" cy="71" rx="8" ry="6" />
      <path class="dog-tongue" d="M54 82 Q60 94 66 82 V94 Q60 104 54 94Z" />
    </g>
  </svg>
</template>

<style scoped lang="scss">
.pet-fill,
.dog-ear,
.dog-tail {
  stroke: #071f2d;
  stroke-width: 5;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.pet-fill {
  fill: #f1c67f;
}

.dog-ear {
  fill: #bc7e48;
}

.dog-tail {
  fill: none;
  stroke: #bc7e48;
  transform-origin: 92px 92px;
  animation: dog-tail-wave 2.8s ease-in-out infinite;
}

.eye,
.muzzle {
  fill: #ffffff;
  stroke: #071f2d;
  stroke-width: 4;
}

.eye {
  transform-box: fill-box;
  transform-origin: center;
  animation: blink 6.4s ease-in-out infinite;
}

.pupils {
  fill: #071f2d;
  transition: transform 90ms linear;
}

.nose {
  fill: #071f2d;
  stroke: #071f2d;
  stroke-width: 3;
}

.dog-tongue {
  fill: #ef7f91;
  stroke: #071f2d;
  stroke-width: 3;
  animation: dog-tongue-bob 2.2s ease-in-out infinite;
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

@keyframes dog-tail-wave {
  0%, 100% { transform: rotate(-4deg); }
  50% { transform: rotate(7deg); }
}

@keyframes dog-tongue-bob {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(2px); }
}
</style>

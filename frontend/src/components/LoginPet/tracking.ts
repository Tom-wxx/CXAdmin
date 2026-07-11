export interface PointerPosition {
  x: number
  y: number
}

export interface PetRect {
  left: number
  top: number
  width: number
  height: number
}

export interface PetMotion {
  eyeX: number
  eyeY: number
  headX: number
  headY: number
  rotate: number
}

export const IDLE_PET_MOTION: PetMotion = Object.freeze({
  eyeX: 0,
  eyeY: 0,
  headX: 0,
  headY: 0,
  rotate: 0
})

const clamp = (value: number, min: number, max: number): number =>
  Math.min(max, Math.max(min, value))

const round = (value: number): number => Math.round(value * 100) / 100

export function calculatePetMotion(
  pointer: PointerPosition,
  rect: PetRect,
  intensity = 1
): PetMotion {
  const values = [pointer.x, pointer.y, rect.left, rect.top, rect.width, rect.height, intensity]
  if (values.some(value => !Number.isFinite(value)) || rect.width <= 0 || rect.height <= 0) {
    return { ...IDLE_PET_MOTION }
  }

  const centerX = rect.left + rect.width / 2
  const centerY = rect.top + rect.height / 2
  const normalizedX = clamp((pointer.x - centerX) / rect.width, -1, 1)
  const normalizedY = clamp((pointer.y - centerY) / rect.height, -1, 1)
  const safeIntensity = clamp(intensity, 0, 1)

  return {
    eyeX: round(normalizedX * 6 * safeIntensity),
    eyeY: round(normalizedY * 6 * safeIntensity),
    headX: round(normalizedX * 4 * safeIntensity),
    headY: round(normalizedY * 3 * safeIntensity),
    rotate: round(normalizedX * 5 * safeIntensity)
  }
}

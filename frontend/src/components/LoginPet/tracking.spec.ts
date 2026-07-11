import { describe, expect, it } from 'vitest'
import { DEFAULT_LOGIN_PET_TYPE, normalizeLoginPetType } from '@/types/login-pet'
import { IDLE_PET_MOTION, calculatePetMotion } from './tracking'

const rect = { left: 100, top: 100, width: 100, height: 100 }

describe('normalizeLoginPetType', () => {
  it.each(['cat', 'dog', 'owl'] as const)('保留合法类型 %s', type => {
    expect(normalizeLoginPetType(type)).toBe(type)
  })

  it.each([undefined, null, '', 'fox', 1])('非法值 %s 回退猫咪', value => {
    expect(normalizeLoginPetType(value)).toBe(DEFAULT_LOGIN_PET_TYPE)
  })
})

describe('calculatePetMotion', () => {
  it('鼠标位于宠物中心时返回静止状态', () => {
    expect(calculatePetMotion({ x: 150, y: 150 }, rect)).toEqual(IDLE_PET_MOTION)
  })

  it('向右上方移动时眼睛与头部同向且旋转为正', () => {
    const motion = calculatePetMotion({ x: 250, y: 50 }, rect)
    expect(motion.eyeX).toBeGreaterThan(0)
    expect(motion.eyeY).toBeLessThan(0)
    expect(motion.headX).toBeGreaterThan(0)
    expect(motion.headY).toBeLessThan(0)
    expect(motion.rotate).toBeGreaterThan(0)
  })

  it('极远坐标被限制在设计幅度内', () => {
    expect(calculatePetMotion({ x: 10000, y: -10000 }, rect)).toEqual({
      eyeX: 6,
      eyeY: -6,
      headX: 4,
      headY: -3,
      rotate: 5
    })
  })

  it('零尺寸或非有限边界返回静止状态', () => {
    expect(calculatePetMotion({ x: 1, y: 1 }, { ...rect, width: 0 })).toEqual(IDLE_PET_MOTION)
    expect(calculatePetMotion({ x: Number.NaN, y: 1 }, rect)).toEqual(IDLE_PET_MOTION)
  })

  it('减少动态效果时按 intensity 缩小幅度', () => {
    const motion = calculatePetMotion({ x: 250, y: 50 }, rect, 0.35)
    expect(Math.abs(motion.eyeX)).toBeLessThanOrEqual(2.1)
    expect(Math.abs(motion.rotate)).toBeLessThanOrEqual(1.75)
  })
})

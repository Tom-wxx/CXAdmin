import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import LoginPet from './index.vue'

type MediaChangeListener = (event: MediaQueryListEvent) => void

describe('LoginPet', () => {
  let coarsePointer = false
  let reducedMotion = false
  let nextFrameId = 1
  let latestFrameId = 0
  let mediaChangeListeners: Set<MediaChangeListener>
  let frameCallbacks: Map<number, FrameRequestCallback>
  let mediaRemoveEventListener: ReturnType<typeof vi.fn>

  function flushLatestFrame(): void {
    const callback = frameCallbacks.get(latestFrameId)
    if (!callback) throw new Error('没有待执行的动画帧')
    frameCallbacks.delete(latestFrameId)
    callback(0)
  }

  function movePointer(clientX: number, clientY: number): void {
    window.dispatchEvent(new MouseEvent('pointermove', { clientX, clientY }))
  }

  beforeEach(() => {
    coarsePointer = false
    reducedMotion = false
    nextFrameId = 1
    latestFrameId = 0
    mediaChangeListeners = new Set()
    frameCallbacks = new Map()
    mediaRemoveEventListener = vi.fn((_type: string, listener: MediaChangeListener) => {
      mediaChangeListeners.delete(listener)
    })

    vi.spyOn(window, 'addEventListener')
    vi.spyOn(window, 'removeEventListener')
    vi.spyOn(document, 'addEventListener')
    vi.spyOn(document, 'removeEventListener')
    vi.spyOn(window, 'requestAnimationFrame').mockImplementation(callback => {
      const frameId = nextFrameId++
      latestFrameId = frameId
      frameCallbacks.set(frameId, callback)
      return frameId
    })
    vi.spyOn(window, 'cancelAnimationFrame').mockImplementation(frameId => {
      frameCallbacks.delete(frameId)
    })
    vi.stubGlobal('matchMedia', vi.fn((query: string) => ({
      matches: query === '(pointer: coarse)' ? coarsePointer : reducedMotion,
      media: query,
      onchange: null,
      addEventListener: (_type: string, listener: MediaChangeListener) => {
        if (query === '(prefers-reduced-motion: reduce)') mediaChangeListeners.add(listener)
      },
      removeEventListener: mediaRemoveEventListener,
      addListener: vi.fn(),
      removeListener: vi.fn(),
      dispatchEvent: vi.fn()
    } as unknown as MediaQueryList)))
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    vi.restoreAllMocks()
    document.body.innerHTML = ''
  })

  it.each([
    ['cat', '动态猫咪'],
    ['dog', '动态小狗'],
    ['owl', '动态猫头鹰']
  ] as const)('渲染 %s SVG 并提供中文可访问名称', (type, accessibleName) => {
    const wrapper = mount(LoginPet, { props: { type } })

    expect(wrapper.get('[data-testid="login-pet"]').attributes('data-pet')).toBe(type)
    expect(wrapper.get('svg').attributes()).toMatchObject({
      role: 'img',
      'aria-label': accessibleName,
      viewBox: '0 0 120 120'
    })

    wrapper.unmount()
  })

  it('非法运行时类型回退猫咪', () => {
    const wrapper = mount(LoginPet, { props: { type: 'fox' as never } })

    expect(wrapper.get('[data-testid="login-pet"]').attributes('data-pet')).toBe('cat')
    expect(wrapper.get('svg').attributes('aria-label')).toBe('动态猫咪')

    wrapper.unmount()
  })

  it('默认使用 108px 且允许覆盖尺寸', () => {
    const defaultWrapper = mount(LoginPet)
    const customWrapper = mount(LoginPet, { props: { size: 72 } })

    expect(defaultWrapper.get('[data-testid="login-pet"]').attributes('style')).toContain('width: 108px')
    expect(defaultWrapper.get('[data-testid="login-pet"]').attributes('style')).toContain('height: 108px')
    expect(customWrapper.get('[data-testid="login-pet"]').attributes('style')).toContain('width: 72px')
    expect(customWrapper.get('[data-testid="login-pet"]').attributes('style')).toContain('height: 72px')

    defaultWrapper.unmount()
    customWrapper.unmount()
  })

  it.each(['cat', 'dog', 'owl'] as const)('%s 的眼睛与头部消费指针运动状态', async type => {
    const wrapper = mount(LoginPet, { props: { type }, attachTo: document.body })
    vi.spyOn(wrapper.element, 'getBoundingClientRect').mockReturnValue({
      left: 100,
      top: 100,
      width: 100,
      height: 100,
      right: 200,
      bottom: 200,
      x: 100,
      y: 100,
      toJSON: () => ({})
    })

    movePointer(250, 50)
    flushLatestFrame()
    await nextTick()

    expect(wrapper.get('.pupils').attributes('style')).toContain('translate(6px, -6px)')
    expect(wrapper.get('.pet-head').attributes('style')).toContain('translate(4px, -3px) rotate(5deg)')
    wrapper.unmount()
  })

  it('同一帧内只调度一次并应用最新指针位置', async () => {
    const wrapper = mount(LoginPet, { attachTo: document.body })
    vi.spyOn(wrapper.element, 'getBoundingClientRect').mockReturnValue({
      left: 100,
      top: 100,
      width: 100,
      height: 100,
      right: 200,
      bottom: 200,
      x: 100,
      y: 100,
      toJSON: () => ({})
    })

    movePointer(50, 150)
    movePointer(250, 50)

    expect(window.requestAnimationFrame).toHaveBeenCalledTimes(1)
    flushLatestFrame()
    await nextTick()
    const root = wrapper.get<HTMLElement>('[data-testid="login-pet"]').element
    expect(root.style.getPropertyValue('--pet-eye-x')).toBe('6px')
    expect(root.style.getPropertyValue('--pet-eye-y')).toBe('-6px')
    wrapper.unmount()
  })

  it('interactive=false 不注册页面指针监听', () => {
    const wrapper = mount(LoginPet, { props: { interactive: false } })

    expect(window.addEventListener).not.toHaveBeenCalledWith(
      'pointermove',
      expect.any(Function),
      expect.anything()
    )
    wrapper.unmount()
  })

  it('粗指针设备保持正面姿态', () => {
    coarsePointer = true
    const wrapper = mount(LoginPet)

    expect(window.addEventListener).not.toHaveBeenCalledWith(
      'pointermove',
      expect.any(Function),
      expect.anything()
    )
    expect(wrapper.get<HTMLElement>('[data-testid="login-pet"]').element.style.getPropertyValue('--pet-eye-x')).toBe('0px')
    wrapper.unmount()
  })

  it('窗口失焦后回到正面', async () => {
    const wrapper = mount(LoginPet, { attachTo: document.body })
    vi.spyOn(wrapper.element, 'getBoundingClientRect').mockReturnValue({
      left: 100, top: 100, width: 100, height: 100,
      right: 200, bottom: 200, x: 100, y: 100, toJSON: () => ({})
    })
    movePointer(250, 50)
    flushLatestFrame()
    await nextTick()

    window.dispatchEvent(new Event('blur'))
    await nextTick()

    const root = wrapper.get<HTMLElement>('[data-testid="login-pet"]').element
    expect(root.style.getPropertyValue('--pet-eye-x')).toBe('0px')
    expect(root.style.getPropertyValue('--pet-eye-y')).toBe('0px')
    wrapper.unmount()
  })

  it('页面隐藏后回到正面', async () => {
    const wrapper = mount(LoginPet, { attachTo: document.body })
    vi.spyOn(wrapper.element, 'getBoundingClientRect').mockReturnValue({
      left: 100, top: 100, width: 100, height: 100,
      right: 200, bottom: 200, x: 100, y: 100, toJSON: () => ({})
    })
    movePointer(250, 50)
    flushLatestFrame()
    await nextTick()
    vi.spyOn(document, 'hidden', 'get').mockReturnValue(true)

    document.dispatchEvent(new Event('visibilitychange'))
    await nextTick()

    expect(wrapper.get<HTMLElement>('[data-testid="login-pet"]').element.style.getPropertyValue('--pet-eye-x')).toBe('0px')
    wrapper.unmount()
  })

  it('减少动态效果会停止循环动画并降低方向反馈强度', async () => {
    reducedMotion = true
    const wrapper = mount(LoginPet, { attachTo: document.body })
    vi.spyOn(wrapper.element, 'getBoundingClientRect').mockReturnValue({
      left: 100, top: 100, width: 100, height: 100,
      right: 200, bottom: 200, x: 100, y: 100, toJSON: () => ({})
    })

    movePointer(250, 50)
    flushLatestFrame()
    await nextTick()

    const root = wrapper.get<HTMLElement>('[data-testid="login-pet"]').element
    expect(wrapper.get('svg').classes()).toContain('reduced-motion')
    expect(root.style.getPropertyValue('--pet-eye-x')).toBe('2.1px')
    expect(root.style.getPropertyValue('--pet-eye-y')).toBe('-2.1px')
    wrapper.unmount()
  })

  it('媒体偏好变更后重置姿态并更新减少动态效果状态', async () => {
    const wrapper = mount(LoginPet)
    expect(mediaChangeListeners.size).toBe(1)

    mediaChangeListeners.forEach(listener => listener({ matches: true } as MediaQueryListEvent))
    await nextTick()

    expect(wrapper.get('svg').classes()).toContain('reduced-motion')
    expect(wrapper.get<HTMLElement>('[data-testid="login-pet"]').element.style.getPropertyValue('--pet-eye-x')).toBe('0px')
    wrapper.unmount()
  })

  it('卸载时清理所有监听器和待执行动画帧', () => {
    const wrapper = mount(LoginPet)
    movePointer(250, 50)
    const pendingFrameId = latestFrameId

    wrapper.unmount()

    expect(window.removeEventListener).toHaveBeenCalledWith('pointermove', expect.any(Function))
    expect(window.removeEventListener).toHaveBeenCalledWith('blur', expect.any(Function))
    expect(document.removeEventListener).toHaveBeenCalledWith('visibilitychange', expect.any(Function))
    expect(mediaRemoveEventListener).toHaveBeenCalledWith('change', expect.any(Function))
    expect(window.cancelAnimationFrame).toHaveBeenCalledWith(pendingFrameId)
  })
})

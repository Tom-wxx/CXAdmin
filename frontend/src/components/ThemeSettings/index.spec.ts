import { flushPromises, mount, type VueWrapper } from '@vue/test-utils'
import { computed, defineComponent, ref } from 'vue'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { getLoginPetType, updateLoginPetType } from '@/api/system/config'
import LoginPet from '@/components/LoginPet/index.vue'
import type { LoginPetType } from '@/types/login-pet'
import ThemeSettings from './index.vue'

const permissionState = vi.hoisted(() => ({
  set: null as ((permissions: string[]) => void) | null
}))
const message = vi.hoisted(() => ({
  success: vi.fn(),
  error: vi.fn()
}))

vi.mock('@/composables/store', () => {
  const permissions = ref<string[]>([])
  permissionState.set = (value: string[]) => {
    permissions.value = value
  }

  return {
    useSettingsStore: () => ({
      sidebarColor: computed(() => '#ffffff'),
      sidebarPosition: computed(() => 'left'),
      setSidebarColor: vi.fn(),
      setSidebarPosition: vi.fn()
    }),
    useUserStore: () => ({ permissions })
  }
})

vi.mock('@/api/system/config', () => ({
  getLoginPetType: vi.fn(),
  updateLoginPetType: vi.fn()
}))

vi.mock('element-plus', () => ({ ElMessage: message }))

const PassThrough = defineComponent({ template: '<div><slot /></div>' })
const stubs = {
  ElDrawer: defineComponent({
    props: ['modelValue'],
    emits: ['update:modelValue'],
    template: '<section><slot /></section>'
  }),
  ElRadioGroup: PassThrough,
  ElRadioButton: PassThrough,
  ElColorPicker: true,
  ElButton: defineComponent({ template: '<button><slot /></button>' }),
  ElIcon: defineComponent({ template: '<span><slot /></span>' }),
  Check: true
}

function loginPetResponse(data: unknown) {
  return {
    code: 200,
    message: 'ok',
    data: data as LoginPetType,
    timestamp: 1
  }
}

function updateResponse() {
  return {
    code: 200,
    message: 'ok',
    data: undefined,
    timestamp: 1
  }
}

function deferred<T>() {
  let resolve!: (value: T) => void
  let reject!: (reason?: unknown) => void
  const promise = new Promise<T>((resolvePromise, rejectPromise) => {
    resolve = resolvePromise
    reject = rejectPromise
  })
  return { promise, resolve, reject }
}

describe('ThemeSettings 登录宠物设置', () => {
  const wrappers: VueWrapper[] = []

  function setPermissions(value: string[]): void {
    if (!permissionState.set) throw new Error('权限 store 尚未初始化')
    permissionState.set(value)
  }

  function render(modelValue = true): VueWrapper {
    const wrapper = mount(ThemeSettings, {
      props: { modelValue },
      global: { stubs }
    })
    wrappers.push(wrapper)
    return wrapper
  }

  beforeEach(() => {
    setPermissions([])
    vi.mocked(getLoginPetType).mockReset().mockResolvedValue(loginPetResponse('cat'))
    vi.mocked(updateLoginPetType).mockReset().mockResolvedValue(updateResponse())
    message.success.mockReset()
    message.error.mockReset()
    vi.stubGlobal('matchMedia', vi.fn((query: string) => ({
      matches: false,
      media: query,
      onchange: null,
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      addListener: vi.fn(),
      removeListener: vi.fn(),
      dispatchEvent: vi.fn()
    } as MediaQueryList)))
  })

  afterEach(() => {
    wrappers.splice(0).forEach(wrapper => wrapper.unmount())
    vi.unstubAllGlobals()
  })

  it('无编辑权限时隐藏设置且打开抽屉也不请求', async () => {
    const wrapper = render()
    await flushPromises()

    expect(wrapper.find('[data-testid="login-pet-settings"]').exists()).toBe(false)
    expect(getLoginPetType).not.toHaveBeenCalled()
  })

  it.each(['system:config:edit', '*:*:*'])('%s 权限可查看设置', async permission => {
    setPermissions([permission])
    const wrapper = render(false)
    await flushPromises()

    expect(wrapper.find('[data-testid="login-pet-settings"]').exists()).toBe(true)
    expect(getLoginPetType).not.toHaveBeenCalled()
  })

  it('关闭时不加载，打开后才加载当前配置', async () => {
    setPermissions(['system:config:edit'])
    vi.mocked(getLoginPetType).mockResolvedValue(loginPetResponse('dog'))
    const wrapper = render(false)
    await flushPromises()
    expect(getLoginPetType).not.toHaveBeenCalled()

    await wrapper.setProps({ modelValue: true })
    await flushPromises()

    expect(getLoginPetType).toHaveBeenCalledTimes(1)
    expect(wrapper.get('[data-pet-option="dog"]').attributes('aria-checked')).toBe('true')
  })

  it('渲染猫、狗、猫头鹰三个不可交互的 58px 预览', async () => {
    setPermissions(['system:config:edit'])
    const wrapper = render()
    await flushPromises()

    const options = wrapper.findAll('[data-pet-option]')
    expect(options.map(option => option.attributes('data-pet-option'))).toEqual(['cat', 'dog', 'owl'])
    expect(wrapper.find('[role="radiogroup"]').exists()).toBe(true)
    expect(options.every(option => option.attributes('role') === 'radio')).toBe(true)
    expect(wrapper.findAllComponents(LoginPet).map(preview => preview.props())).toEqual([
      expect.objectContaining({ type: 'cat', size: 58, interactive: false }),
      expect.objectContaining({ type: 'dog', size: 58, interactive: false }),
      expect.objectContaining({ type: 'owl', size: 58, interactive: false })
    ])
  })

  it.each([
    ['非法响应', 'fox'],
    ['缺失响应', undefined]
  ])('%s 回退猫咪', async (_label, value) => {
    setPermissions(['system:config:edit'])
    vi.mocked(getLoginPetType).mockResolvedValue(loginPetResponse(value))
    const wrapper = render()
    await flushPromises()

    expect(wrapper.get('[data-pet-option="cat"]').attributes('aria-checked')).toBe('true')
  })

  it('加载失败回退猫咪并在下次重新打开时重试', async () => {
    setPermissions(['system:config:edit'])
    vi.mocked(getLoginPetType)
      .mockRejectedValueOnce(new Error('load failed'))
      .mockResolvedValueOnce(loginPetResponse('owl'))
    const wrapper = render()
    await flushPromises()

    expect(wrapper.get('[data-pet-option="cat"]').attributes('aria-checked')).toBe('true')
    expect(getLoginPetType).toHaveBeenCalledTimes(1)

    await wrapper.setProps({ modelValue: false })
    await wrapper.setProps({ modelValue: true })
    await flushPromises()

    expect(getLoginPetType).toHaveBeenCalledTimes(2)
    expect(wrapper.get('[data-pet-option="owl"]').attributes('aria-checked')).toBe('true')
  })

  it('加载期间禁用全部选项且点击不会提交', async () => {
    setPermissions(['system:config:edit'])
    const load = deferred<ReturnType<typeof loginPetResponse>>()
    vi.mocked(getLoginPetType).mockReturnValue(load.promise)
    const wrapper = render()
    await wrapper.vm.$nextTick()

    expect(wrapper.findAll('[data-pet-option]').every(option => option.attributes('disabled') !== undefined)).toBe(true)
    await wrapper.get('[data-pet-option="dog"]').trigger('click')
    expect(updateLoginPetType).not.toHaveBeenCalled()

    load.resolve(loginPetResponse('cat'))
    await flushPromises()
  })

  it('上一次加载未完成时重复打开也只发送一次请求', async () => {
    setPermissions(['system:config:edit'])
    const load = deferred<ReturnType<typeof loginPetResponse>>()
    vi.mocked(getLoginPetType).mockReturnValue(load.promise)
    const wrapper = render()
    await wrapper.vm.$nextTick()
    expect(getLoginPetType).toHaveBeenCalledTimes(1)

    await wrapper.setProps({ modelValue: false })
    await wrapper.setProps({ modelValue: true })

    expect(getLoginPetType).toHaveBeenCalledTimes(1)
    load.resolve(loginPetResponse('dog'))
    await flushPromises()
  })

  it('保存时先乐观选中并在成功后给出成功提示', async () => {
    setPermissions(['system:config:edit'])
    const save = deferred<ReturnType<typeof updateResponse>>()
    vi.mocked(updateLoginPetType).mockReturnValue(save.promise)
    const wrapper = render()
    await flushPromises()

    await wrapper.get('[data-pet-option="dog"]').trigger('click')

    expect(updateLoginPetType).toHaveBeenCalledWith('dog')
    expect(wrapper.get('[data-pet-option="dog"]').attributes('aria-checked')).toBe('true')
    expect(wrapper.findAll('[data-pet-option]').every(option => option.attributes('disabled') !== undefined)).toBe(true)

    save.resolve(updateResponse())
    await flushPromises()

    expect(message.success).toHaveBeenCalledWith('登录页宠物已更新')
  })

  it('保存失败回滚原选项且不重复显示错误提示', async () => {
    setPermissions(['system:config:edit'])
    vi.mocked(getLoginPetType).mockResolvedValue(loginPetResponse('owl'))
    vi.mocked(updateLoginPetType).mockRejectedValue(new Error('save failed'))
    const wrapper = render()
    await flushPromises()

    await wrapper.get('[data-pet-option="dog"]').trigger('click')
    await flushPromises()

    expect(wrapper.get('[data-pet-option="owl"]').attributes('aria-checked')).toBe('true')
    expect(message.error).not.toHaveBeenCalled()
    expect(message.success).not.toHaveBeenCalled()
  })

  it('选择当前值时不提交', async () => {
    setPermissions(['system:config:edit'])
    const wrapper = render()
    await flushPromises()

    await wrapper.get('[data-pet-option="cat"]').trigger('click')

    expect(updateLoginPetType).not.toHaveBeenCalled()
  })

  it('保存期间快速选择其他宠物也只提交第一次', async () => {
    setPermissions(['system:config:edit'])
    const save = deferred<ReturnType<typeof updateResponse>>()
    vi.mocked(updateLoginPetType).mockReturnValue(save.promise)
    const wrapper = render()
    await flushPromises()

    await wrapper.get('[data-pet-option="dog"]').trigger('click')
    await wrapper.get('[data-pet-option="owl"]').trigger('click')

    expect(updateLoginPetType).toHaveBeenCalledTimes(1)
    expect(updateLoginPetType).toHaveBeenCalledWith('dog')
    expect(wrapper.get('[data-pet-option="dog"]').attributes('aria-checked')).toBe('true')

    save.resolve(updateResponse())
    await flushPromises()
  })

  it('运行时撤销权限会立即隐藏设置且不触发新请求', async () => {
    setPermissions(['system:config:edit'])
    const wrapper = render(false)
    expect(wrapper.find('[data-testid="login-pet-settings"]').exists()).toBe(true)

    setPermissions([])
    await flushPromises()
    await wrapper.setProps({ modelValue: true })
    await flushPromises()

    expect(wrapper.find('[data-testid="login-pet-settings"]').exists()).toBe(false)
    expect(getLoginPetType).not.toHaveBeenCalled()
  })
})

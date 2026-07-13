<template>
  <div class="theme-settings-wrapper">
    <el-drawer
      title="主题设置"
      v-model="drawerVisible"
      direction="rtl"
      size="280px"
    >
      <div class="theme-settings-content">
        <div class="setting-section">
          <div class="section-title">布局模式</div>
          <el-radio-group v-model="sidebarPosition" size="small" @change="handlePositionChange">
            <el-radio-button value="left">左侧</el-radio-button>
            <el-radio-button value="top">顶部</el-radio-button>
          </el-radio-group>
        </div>

        <div class="setting-section">
          <div class="section-title">侧栏配色</div>
          <div class="theme-list">
            <div
              v-for="theme in themes"
              :key="theme.name"
              class="theme-item"
              :class="{ active: currentColor === theme.color }"
              @click="selectTheme(theme)"
            >
              <div class="theme-color" :style="{ backgroundColor: theme.color }">
                <el-icon v-if="currentColor === theme.color"><Check /></el-icon>
              </div>
              <div class="theme-name">{{ theme.name }}</div>
            </div>
          </div>
        </div>

        <div class="setting-section">
          <div class="section-title">自定义颜色</div>
          <el-color-picker
            v-model="selectedColor"
            :predefine="predefineColors"
            @change="handleColorChange"
          />
        </div>

        <div v-if="canEditLoginPet" class="setting-section" data-testid="login-pet-settings">
          <div class="section-title">登录页宠物</div>
          <div class="pet-options" role="radiogroup" aria-label="登录页宠物">
            <button
              v-for="pet in petOptions"
              :key="pet.type"
              type="button"
              class="pet-option"
              :class="{ active: currentPet === pet.type }"
              :data-pet-option="pet.type"
              :aria-checked="currentPet === pet.type"
              :disabled="petLoading || petSaving"
              role="radio"
              @click="selectPet(pet.type)"
            >
              <LoginPet :type="pet.type" :size="58" :interactive="false" />
              <span>{{ pet.name }}</span>
              <el-icon v-if="currentPet === pet.type"><Check /></el-icon>
            </button>
          </div>
        </div>

        <div class="setting-actions">
          <el-button size="small" @click="resetTheme">恢复默认</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import LoginPet from '@/components/LoginPet/index.vue'
import { getLoginPetType, updateLoginPetType } from '@/api/system/config'
import { useSettingsStore, useUserStore } from '@/composables/store'
import {
  DEFAULT_LOGIN_PET_TYPE,
  normalizeLoginPetType,
  type LoginPetType
} from '@/types/login-pet'

defineOptions({ name: 'ThemeSettings' })

interface ThemeOption {
  name: string
  color: string
}

interface PetOption {
  type: LoginPetType
  name: string
}

const props = withDefaults(defineProps<{ modelValue?: boolean }>(), {
  modelValue: false
})

const emit = defineEmits<{ (e: 'update:modelValue', v: boolean): void }>()

const { sidebarColor: currentColor, sidebarPosition: currentPosition, setSidebarColor, setSidebarPosition } = useSettingsStore()
const { permissions } = useUserStore()

const drawerVisible = computed({
  get: () => props.modelValue,
  set: (val: boolean) => emit('update:modelValue', val)
})

const selectedColor = ref('')
const sidebarPosition = ref('left')
const currentPet = ref<LoginPetType>(DEFAULT_LOGIN_PET_TYPE)
const petLoading = ref(false)
const petSaving = ref(false)
const petLoaded = ref(false)

const canEditLoginPet = computed(() =>
  permissions.value.includes('*:*:*') || permissions.value.includes('system:config:edit')
)

const petOptions: PetOption[] = [
  { type: 'cat', name: '猫咪' },
  { type: 'dog', name: '小狗' },
  { type: 'owl', name: '猫头鹰' }
]

const themes: ThemeOption[] = [
  { name: '浅白', color: '#ffffff' },
  { name: '雾灰', color: '#f8fafc' },
  { name: '冰蓝', color: '#eff6ff' },
  { name: '淡青', color: '#ecfeff' },
  { name: '夜蓝', color: '#001529' },
  { name: '经典灰', color: '#304156' },
  { name: '墨绿', color: '#065f46' },
  { name: '深紫', color: '#5b21b6' }
]

const predefineColors: string[] = [
  '#ffffff', '#f8fafc', '#eff6ff', '#ecfeff',
  '#001529', '#304156', '#065f46', '#5b21b6'
]

watch(currentColor, val => {
  selectedColor.value = val
}, { immediate: true })

watch(currentPosition, val => {
  sidebarPosition.value = val
}, { immediate: true })

watch(
  () => [drawerVisible.value, canEditLoginPet.value] as const,
  ([visible, canEdit]) => {
    if (visible && canEdit) void loadPetSetting()
  },
  { immediate: true }
)

async function loadPetSetting(): Promise<void> {
  if (!canEditLoginPet.value || petLoaded.value || petLoading.value) return

  petLoading.value = true
  try {
    const response = await getLoginPetType()
    currentPet.value = normalizeLoginPetType(response.data)
    petLoaded.value = true
  } catch {
    currentPet.value = DEFAULT_LOGIN_PET_TYPE
    petLoaded.value = false
  } finally {
    petLoading.value = false
  }
}

async function selectPet(type: LoginPetType): Promise<void> {
  if (petLoading.value || petSaving.value || type === currentPet.value) return

  const previous = currentPet.value
  currentPet.value = type
  petSaving.value = true
  try {
    await updateLoginPetType(type)
    ElMessage.success('登录页宠物已更新')
  } catch {
    currentPet.value = previous
  } finally {
    petSaving.value = false
  }
}

function handleColorChange(color: string): void {
  setSidebarColor(color)
}

function handlePositionChange(position: string): void {
  setSidebarPosition(position)
}

function selectTheme(theme: ThemeOption): void {
  selectedColor.value = theme.color
  setSidebarColor(theme.color)
}

function resetTheme(): void {
  selectedColor.value = '#ffffff'
  sidebarPosition.value = 'left'
  setSidebarColor('#ffffff')
  setSidebarPosition('left')
  ElMessage.success('已恢复默认')
}
</script>

<style lang="scss" scoped>
.theme-settings-content {
  padding: 16px 20px;

  .setting-section {
    margin-bottom: 24px;
  }

  .section-title {
    font-size: 13px;
    font-weight: 600;
    color: #1f2937;
    margin-bottom: 12px;
  }

  .theme-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 8px;

    .theme-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;

      .theme-color {
        width: 100%;
        height: 32px;
        border-radius: 8px;
        border: 1px solid #e5e7eb;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: border-color 0.2s ease, box-shadow 0.2s ease;

        .el-icon {
          color: #0f9f9f;
          font-size: 14px;
          font-weight: bold;
        }
      }

      .theme-name {
        font-size: 11px;
        color: #64748b;
        margin-top: 4px;
        text-align: center;
      }

      &.active .theme-color {
        border-color: #0f9f9f;
        box-shadow: 0 0 0 2px rgba(15, 159, 159, 0.12);
      }

      &:hover .theme-color {
        border-color: #0f9f9f;
      }
    }
  }

  .setting-actions {
    padding-top: 16px;
    border-top: 1px solid #e5e7eb;

    .el-button {
      width: 100%;
    }
  }

  .pet-options {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 8px;
  }

  .pet-option {
    position: relative;
    display: flex;
    min-width: 0;
    padding: 9px 4px 7px;
    align-items: center;
    flex-direction: column;
    gap: 5px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    color: #334155;
    background: #fff;
    cursor: pointer;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;

    &:hover,
    &:focus-visible,
    &.active {
      border-color: #0f9f9f;
      box-shadow: 0 0 0 2px rgba(15, 159, 159, 0.12);
      outline: none;
    }

    &:disabled {
      cursor: wait;
      opacity: 0.65;
    }

    .el-icon {
      position: absolute;
      top: 5px;
      right: 5px;
      color: #0f9f9f;
    }

    span {
      font-size: 11px;
    }
  }
}
</style>

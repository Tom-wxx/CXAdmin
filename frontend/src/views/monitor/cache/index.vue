<template>
  <div class="cache-monitor-container">
    <el-card class="box-card">
      <template #header><div class="header-container">
        <span class="card-title">
          <el-icon><Coin /></el-icon>
          缓存监控
        </span>
        <div class="header-actions">
          <el-button
            type="danger"
            size="small"
            icon="Delete"
            @click="handleClearAll"
          >
            清空所有
          </el-button>
          <el-button
            type="primary"
            size="small"
            icon="Refresh"
            @click="refreshAll"
          >
            刷新
          </el-button>
        </div>
      </div></template>

      <el-row :gutter="20" class="cache-content">
        <!-- 左侧：缓存列表 -->
        <el-col :span="6">
          <el-card shadow="hover" class="panel-card">
            <template #header><div class="panel-header">
              <div class="panel-header-left">
                <el-icon><FolderOpened /></el-icon>
                缓存列表
              </div>
              <el-button
                type="danger"
                size="small"
                icon="Delete"
                @click="handleClearAllCache"
              >
                清空
              </el-button>
            </div></template>
            <div class="cache-list" v-loading="cacheListLoading">
              <div
                v-for="cache in cacheList"
                :key="cache.cacheName"
                :class="['cache-item', { active: selectedCache === cache.cacheName }]"
                @click="handleCacheSelect(cache)"
              >
                <div class="cache-item-header">
                  <el-icon><Folder /></el-icon>
                  <span class="cache-name">{{ cache.cacheName }}</span>
                </div>
                <div class="cache-item-info">
                  <el-tag size="small" type="info">{{ cache.keyCount }} 个键</el-tag>
                  <span class="cache-remark">{{ cache.remark }}</span>
                </div>
              </div>
              <div v-if="cacheList.length === 0" class="empty-data">
                <el-icon><Folder /></el-icon>
                <p>暂无缓存数据</p>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 中间：键名列表 -->
        <el-col :span="6">
          <el-card shadow="hover" class="panel-card">
            <template #header><div class="panel-header">
              <div class="panel-header-left">
                <el-icon><Key /></el-icon>
                键名列表
                <el-tag v-if="selectedCache" size="small" type="primary" class="selected-tag">
                  {{ selectedCache }}
                </el-tag>
              </div>
              <el-button
                v-if="selectedCache"
                type="danger"
                size="small"
                icon="Delete"
                @click="handleClearCacheByPrefix"
              >
                清空
              </el-button>
            </div></template>
            <div class="search-box">
              <el-input
                v-model="keywordFilter"
                placeholder="搜索键名"
                size="small"
                prefix-icon="Search"
                clearable
                @input="handleKeySearch"
              />
            </div>
            <div class="key-list" v-loading="keyListLoading">
              <div
                v-for="keyItem in keyList"
                :key="keyItem.keyName"
                :class="['key-item', { active: selectedKey === keyItem.keyName }]"
                @click="handleKeySelect(keyItem)"
              >
                <div class="key-item-header">
                  <menu-icon :name="getKeyIcon(keyItem.keyType)" />
                  <span class="key-name" :title="keyItem.keyName">{{ keyItem.keyName }}</span>
                </div>
                <div class="key-item-info">
                  <el-tag size="small" :type="getKeyTypeColor(keyItem.keyType)">
                    {{ keyItem.keyType }}
                  </el-tag>
                  <span class="key-ttl">{{ keyItem.ttl }}</span>
                </div>
              </div>
              <div v-if="keyList.length === 0" class="empty-data">
                <el-icon><Key /></el-icon>
                <p>{{ selectedCache ? '暂无键名' : '请选择缓存' }}</p>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：缓存内容 -->
        <el-col :span="12">
          <el-card shadow="hover" class="panel-card content-panel">
            <template #header><div class="panel-header">
              <div class="content-header-left">
                <el-icon><Document /></el-icon>
                缓存内容
                <el-tag v-if="selectedKey" size="small" type="success" class="selected-tag">
                  {{ selectedKey }}
                </el-tag>
              </div>
              <el-button
                v-if="selectedKey"
                type="danger"
                size="small"
                icon="Delete"
                @click="handleDeleteKey"
              >
                删除
              </el-button>
            </div></template>
            <div class="cache-value" v-loading="valueLoading">
              <div v-if="cacheValue" class="value-container">
                <!-- 键信息 -->
                <div class="value-info">
                  <div class="info-row">
                    <span class="info-label">键名：</span>
                    <span class="info-value">{{ cacheValue.keyName }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">类型：</span>
                    <el-tag size="small" :type="getKeyTypeColor(cacheValue.keyType as string)">
                      {{ cacheValue.keyType }}
                    </el-tag>
                  </div>
                  <div class="info-row">
                    <span class="info-label">过期时间：</span>
                    <span class="info-value">{{ cacheValue.ttl }}</span>
                  </div>
                  <div class="info-row" v-if="cacheValue.size">
                    <span class="info-label">大小：</span>
                    <span class="info-value">{{ cacheValue.size }}</span>
                  </div>
                </div>

                <el-divider></el-divider>

                <!-- 值内容 -->
                <div class="value-content">
                  <div class="value-label">值内容：</div>

                  <!-- String 类型 -->
                  <div v-if="cacheValue.keyType === 'string'" class="string-value">
                    <pre>{{ formatValue(cacheValue.value) }}</pre>
                  </div>

                  <!-- Hash 类型 -->
                  <div v-else-if="cacheValue.keyType === 'hash'" class="hash-value">
                    <el-table :data="formatHashValue(cacheValue.value)" border size="small">
                      <el-table-column prop="key" label="字段" width="200" />
                      <el-table-column prop="value" label="值" show-overflow-tooltip />
                    </el-table>
                  </div>

                  <!-- List 类型 -->
                  <div v-else-if="cacheValue.keyType === 'list'" class="list-value">
                    <el-table :data="formatListValue(cacheValue.value)" border size="small">
                      <el-table-column prop="index" label="索引" width="100" align="center" />
                      <el-table-column prop="value" label="值" show-overflow-tooltip />
                    </el-table>
                  </div>

                  <!-- Set 类型 -->
                  <div v-else-if="cacheValue.keyType === 'set'" class="set-value">
                    <el-tag
                      v-for="(item, index) in formatSetValue(cacheValue.value)"
                      :key="index"
                      class="set-item"
                      size="small"
                    >
                      {{ item }}
                    </el-tag>
                  </div>

                  <!-- ZSet 类型 -->
                  <div v-else-if="cacheValue.keyType === 'zset'" class="zset-value">
                    <el-table :data="formatZSetValue(cacheValue.value)" border size="small">
                      <el-table-column prop="member" label="成员" show-overflow-tooltip />
                      <el-table-column prop="score" label="分数" width="150" align="center" />
                    </el-table>
                  </div>

                  <!-- 其他类型 -->
                  <div v-else class="unknown-value">
                    <pre>{{ cacheValue.value }}</pre>
                  </div>
                </div>
              </div>
              <div v-else class="empty-data">
                <el-icon><Document /></el-icon>
                <p>{{ selectedKey ? '加载中...' : '请选择键名' }}</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCacheList,
  getKeyList,
  getCacheValue,
  deleteCache,
  batchDeleteCache,
  clearAllCache
} from '@/api/monitor/cache'
import type { CacheInfo, CacheValue } from '@/types/monitor/cache'

defineOptions({ name: 'CacheMonitor' })

// ── 状态 ──────────────────────────────────────────────────────────────────────
const cacheListLoading = ref(false)
const keyListLoading = ref(false)
const valueLoading = ref(false)

const cacheList = ref<CacheInfo[]>([])

// 后端实际返回的键列表项是对象（含 keyName/keyType/ttl），
// 但 API 类型声明为 string[] ── 此处用 any[] 保留运行时字段访问
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const keyList = ref<any[]>([])

const cacheValue = ref<CacheValue | null>(null)
const selectedCache = ref<string | null>(null)
const selectedKey = ref<string | null>(null)
const keywordFilter = ref('')

// ── 数据加载 ──────────────────────────────────────────────────────────────────

/** 加载缓存列表 */
async function loadCacheList() {
  cacheListLoading.value = true
  try {
    const response = await getCacheList()
    if (response.code === 200) {
      cacheList.value = response.data || []
    }
  } catch {
    ElMessage.error('获取缓存列表失败')
  } finally {
    cacheListLoading.value = false
  }
}

/** 加载键名列表 */
async function loadKeyList(cacheName?: string | null, keyword?: string) {
  keyListLoading.value = true
  try {
    const params: Record<string, string> = {}
    if (cacheName) params.cacheName = cacheName
    if (keyword) params.keyword = keyword
    const response = await getKeyList(params)
    if (response.code === 200) {
      keyList.value = (response.data as unknown as any[]) || []
    }
  } catch {
    ElMessage.error('获取键名列表失败')
  } finally {
    keyListLoading.value = false
  }
}

/** 加载缓存内容 */
async function loadCacheValue(key: string) {
  valueLoading.value = true
  try {
    const response = await getCacheValue(key)
    if (response.code === 200) {
      cacheValue.value = response.data
    } else {
      ElMessage.error(response.message || '获取缓存内容失败')
      cacheValue.value = null
    }
  } catch {
    ElMessage.error('获取缓存内容失败')
    cacheValue.value = null
  } finally {
    valueLoading.value = false
  }
}

// ── 事件处理 ──────────────────────────────────────────────────────────────────

/** 选择缓存 */
function handleCacheSelect(cache: CacheInfo) {
  selectedCache.value = cache.cacheName ?? null
  selectedKey.value = null
  cacheValue.value = null
  keywordFilter.value = ''
  loadKeyList(cache.cacheName)
}

/** 选择键 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
function handleKeySelect(keyItem: any) {
  selectedKey.value = keyItem.keyName as string
  loadCacheValue(keyItem.keyName as string)
}

/** 搜索键名 */
function handleKeySearch() {
  loadKeyList(selectedCache.value, keywordFilter.value)
}

/** 删除键 */
function handleDeleteKey() {
  ElMessageBox.confirm('确定要删除该缓存吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteCache(selectedKey.value!)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        selectedKey.value = null
        cacheValue.value = null
        loadKeyList(selectedCache.value)
        loadCacheList()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

/** 清空所有缓存 */
function handleClearAll() {
  ElMessageBox.confirm('确定要清空所有缓存吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await clearAllCache()
      if (response.code === 200) {
        ElMessage.success('清空成功')
        selectedCache.value = null
        selectedKey.value = null
        cacheValue.value = null
        keyList.value = []
        loadCacheList()
      } else {
        ElMessage.error(response.message || '清空失败')
      }
    } catch {
      ElMessage.error('清空失败')
    }
  }).catch(() => {})
}

/** 清空所有缓存（缓存列表面板按钮） */
function handleClearAllCache() {
  handleClearAll()
}

/** 清空当前缓存分类 */
function handleClearCacheByPrefix() {
  if (!selectedCache.value) {
    ElMessage.warning('请先选择缓存分类')
    return
  }

  ElMessageBox.confirm(
    `确定要清空缓存分类"${selectedCache.value}"下的所有键吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      if (keyList.value.length === 0) {
        ElMessage.warning('当前缓存分类下没有键')
        return
      }

      const keys: string[] = keyList.value.map((item: any) => item.keyName as string)
      const response = await batchDeleteCache(keys)

      if (response.code === 200) {
        // response.data 实际含 success 字段；API 声明为 void，故用 as any
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        ElMessage.success(`已清空${(response.data as any)?.success ?? ''}个键`)
        selectedKey.value = null
        cacheValue.value = null
        keyList.value = []
        loadCacheList()
      } else {
        ElMessage.error(response.message || '清空失败')
      }
    } catch {
      ElMessage.error('清空失败')
    }
  }).catch(() => {})
}

/** 刷新所有 */
function refreshAll() {
  loadCacheList()
  if (selectedCache.value) {
    loadKeyList(selectedCache.value, keywordFilter.value)
  }
  if (selectedKey.value) {
    loadCacheValue(selectedKey.value)
  }
}

// ── 格式化工具函数 ─────────────────────────────────────────────────────────────

/** 获取键类型图标 */
function getKeyIcon(type: string): string {
  const iconMap: Record<string, string> = {
    'string': 'el-icon-document',
    'hash': 'el-icon-collection',
    'list': 'el-icon-menu',
    'set': 'el-icon-files',
    'zset': 'el-icon-data-line'
  }
  return iconMap[type] || 'el-icon-document'
}

/** 获取键类型颜色 */
function getKeyTypeColor(type: string): string {
  const colorMap: Record<string, string> = {
    'string': 'success',
    'hash': 'warning',
    'list': 'primary',
    'set': 'info',
    'zset': 'danger'
  }
  return colorMap[type] || ''
}

/** 格式化 string 值（Redis value 可为任意类型，用 unknown） */
function formatValue(value: unknown): string {
  if (typeof value === 'object' && value !== null) {
    return JSON.stringify(value, null, 2)
  }
  return String(value ?? '')
}

/** 格式化 Hash 值 → [{key, value}] */
function formatHashValue(value: unknown): Array<{ key: string; value: string }> {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return []
  return Object.entries(value as Record<string, unknown>).map(([key, val]) => ({
    key,
    value: typeof val === 'object' ? JSON.stringify(val) : String(val)
  }))
}

/** 格式化 List 值 → [{index, value}] */
function formatListValue(value: unknown): Array<{ index: number; value: string }> {
  if (!Array.isArray(value)) return []
  return value.map((item: unknown, index: number) => ({
    index,
    value: typeof item === 'object' ? JSON.stringify(item) : String(item)
  }))
}

/** 格式化 Set 值 → string[] */
function formatSetValue(value: unknown): string[] {
  if (Array.isArray(value)) return value as string[]
  if (value && typeof value === 'object') return Array.from(value as Iterable<string>)
  return []
}

/** 格式化 ZSet 值 → [{member, score}] */
function formatZSetValue(value: unknown): Array<{ member: unknown; score: number }> {
  if (!Array.isArray(value)) return []
  return (value as unknown[]).map((item: unknown) => {
    if (typeof item === 'object' && item !== null && 'member' in item) {
      return item as { member: unknown; score: number }
    }
    return { member: item, score: 0 }
  })
}

// ── 初始化（替代 created） ──────────────────────────────────────────────────────
loadCacheList()
</script>

<style lang="scss" scoped>
.cache-monitor-container {
  padding: 20px;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;

      i {
        margin-right: 8px;
        font-size: 20px;
      }
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  .cache-content {
    height: calc(100vh - 250px);
    min-height: 600px;

    .panel-card {
      height: 100%;
      display: flex;
      flex-direction: column;

      :deep(.el-card__header){
        padding: 15px 20px;
        background: #f5f7fa;
        border-bottom: 1px solid #ebeef5;
      }

      :deep(.el-card__body){
        flex: 1;
        padding: 0;
        overflow: hidden;
        display: flex;
        flex-direction: column;
      }

      .panel-header {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 8px;

        i {
          font-size: 16px;
          color: #409EFF;
        }

        .selected-tag {
          margin-left: 8px;
        }

        .panel-header-left {
          flex: 1;
          display: flex;
          align-items: center;
          gap: 8px;
        }

        .content-header-left {
          flex: 1;
          display: flex;
          align-items: center;
          gap: 8px;
        }
      }
    }

    .search-box {
      padding: 15px;
      border-bottom: 1px solid #ebeef5;
    }

    .cache-list,
    .key-list {
      flex: 1;
      overflow-y: auto;
      padding: 10px;

      .cache-item,
      .key-item {
        padding: 12px;
        margin-bottom: 8px;
        border: 1px solid #ebeef5;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #f5f7fa;
          border-color: #409EFF;
        }

        &.active {
          background: #ecf5ff;
          border-color: #409EFF;
        }

        .cache-item-header,
        .key-item-header {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;

          i {
            color: #409EFF;
            font-size: 16px;
          }

          .cache-name,
          .key-name {
            flex: 1;
            font-size: 14px;
            font-weight: 500;
            color: #303133;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        .cache-item-info,
        .key-item-info {
          display: flex;
          align-items: center;
          justify-content: space-between;
          gap: 8px;
          font-size: 12px;

          .cache-remark {
            color: #909399;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .key-ttl {
            color: #909399;
            font-size: 12px;
          }
        }
      }
    }

    .cache-value {
      flex: 1;
      overflow-y: auto;
      padding: 20px;

      .value-container {
        .value-info {
          .info-row {
            display: flex;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px dashed #ebeef5;

            &:last-child {
              border-bottom: none;
            }

            .info-label {
              width: 100px;
              font-size: 14px;
              color: #909399;
              font-weight: 500;
            }

            .info-value {
              flex: 1;
              font-size: 14px;
              color: #606266;
              word-break: break-all;
            }
          }
        }

        .value-content {
          .value-label {
            font-size: 14px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 15px;
          }

          .string-value,
          .unknown-value {
            pre {
              background: #f5f7fa;
              padding: 15px;
              border-radius: 4px;
              font-size: 13px;
              line-height: 1.6;
              color: #606266;
              overflow-x: auto;
              margin: 0;
            }
          }

          .set-value {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;

            .set-item {
              max-width: 200px;
              overflow: hidden;
              text-overflow: ellipsis;
            }
          }
        }
      }
    }

    .empty-data {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      min-height: 200px;
      color: #909399;

      i {
        font-size: 48px;
        margin-bottom: 10px;
        opacity: 0.5;
      }

      p {
        font-size: 14px;
        margin: 0;
      }
    }
  }
}
</style>

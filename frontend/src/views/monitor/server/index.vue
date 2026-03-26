<template>
  <div class="server-monitor-container">
    <el-card class="box-card">
      <div slot="header" class="header-container">
        <span class="card-title">
          <i class="el-icon-monitor"></i>
          系统监控
        </span>
        <div class="refresh-controls">
          <el-switch
            v-model="autoRefresh"
            active-text="自动刷新"
            inactive-text=""
            @change="handleAutoRefreshChange"
          />
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-refresh"
            :loading="loading"
            @click="loadServerInfo"
            style="margin-left: 10px"
          >
            刷新
          </el-button>
        </div>
      </div>

      <el-tabs v-model="activeTab">
        <!-- 服务器信息 -->
        <el-tab-pane label="服务器信息" name="server">
          <el-row :gutter="20" v-loading="loading">
            <!-- CPU信息 -->
            <el-col :span="12">
              <el-card shadow="hover" class="info-card">
                <div class="card-header-custom">
                  <i class="el-icon-cpu" style="color: #409EFF"></i>
                  <span>CPU信息</span>
                </div>
                <div class="progress-item">
                  <div class="progress-header">
                    <span>CPU使用率</span>
                    <span class="progress-value">{{ serverInfo.cpu?.used }}%</span>
                  </div>
                  <el-progress
                    :percentage="parseFloat(serverInfo.cpu?.used || 0)"
                    :color="getProgressColor(serverInfo.cpu?.used)"
                  ></el-progress>
                </div>
                <div class="info-list">
                  <div class="info-item">
                    <span class="info-label">核心数：</span>
                    <span class="info-value">{{ serverInfo.cpu?.coreNum || 0 }}核</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">系统：</span>
                    <span class="info-value">{{ serverInfo.cpu?.sys || 0 }}%</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">用户：</span>
                    <span class="info-value">{{ serverInfo.cpu?.user || 0 }}%</span>
                  </div>
                </div>
              </el-card>
            </el-col>

            <!-- 内存信息 -->
            <el-col :span="12">
              <el-card shadow="hover" class="info-card">
                <div class="card-header-custom">
                  <i class="el-icon-coin" style="color: #67C23A"></i>
                  <span>内存信息</span>
                </div>
                <div class="progress-item">
                  <div class="progress-header">
                    <span>内存使用率</span>
                    <span class="progress-value">{{ serverInfo.mem?.used }}%</span>
                  </div>
                  <el-progress
                    :percentage="parseFloat(serverInfo.mem?.used || 0)"
                    :color="getProgressColor(serverInfo.mem?.used)"
                  ></el-progress>
                </div>
                <div class="info-list">
                  <div class="info-item">
                    <span class="info-label">总内存：</span>
                    <span class="info-value">{{ serverInfo.mem?.total || 0 }} GB</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">已用：</span>
                    <span class="info-value">{{ serverInfo.mem?.usedMemory || 0 }} GB</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">空闲：</span>
                    <span class="info-value">{{ serverInfo.mem?.free || 0 }} GB</span>
                  </div>
                </div>
              </el-card>
            </el-col>

            <!-- JVM信息 -->
            <el-col :span="12">
              <el-card shadow="hover" class="info-card">
                <div class="card-header-custom">
                  <i class="el-icon-data-analysis" style="color: #E6A23C"></i>
                  <span>JVM信息</span>
                </div>
                <div class="progress-item">
                  <div class="progress-header">
                    <span>JVM使用率</span>
                    <span class="progress-value">{{ serverInfo.jvm?.used }}%</span>
                  </div>
                  <el-progress
                    :percentage="parseFloat(serverInfo.jvm?.used || 0)"
                    :color="getProgressColor(serverInfo.jvm?.used)"
                  ></el-progress>
                </div>
                <div class="info-list">
                  <div class="info-item">
                    <span class="info-label">总内存：</span>
                    <span class="info-value">{{ serverInfo.jvm?.total || 0 }} MB</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">已用：</span>
                    <span class="info-value">{{ serverInfo.jvm?.usedMemory || 0 }} MB</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">Java版本：</span>
                    <span class="info-value">{{ serverInfo.jvm?.version || '-' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">启动时间：</span>
                    <span class="info-value">{{ serverInfo.jvm?.startTime || '-' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">运行时长：</span>
                    <span class="info-value">{{ serverInfo.jvm?.runTime || '-' }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>

            <!-- 磁盘信息 -->
            <el-col :span="12">
              <el-card shadow="hover" class="info-card">
                <div class="card-header-custom">
                  <i class="el-icon-folder-opened" style="color: #F56C6C"></i>
                  <span>磁盘信息</span>
                </div>
                <div
                  v-for="(disk, index) in serverInfo.diskList"
                  :key="index"
                  class="progress-item"
                >
                  <div class="progress-header">
                    <span>{{ disk.dirName }}</span>
                    <span class="progress-value">{{ disk.used }}%</span>
                  </div>
                  <el-progress
                    :percentage="parseFloat(disk.used || 0)"
                    :color="getProgressColor(disk.used)"
                  ></el-progress>
                  <div class="disk-info">
                    <span>总计: {{ disk.total }} GB</span>
                    <span>空闲: {{ disk.free }} GB</span>
                  </div>
                </div>
                <div v-if="!serverInfo.diskList || serverInfo.diskList.length === 0" class="no-data">
                  暂无磁盘信息
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- Redis监控 -->
        <el-tab-pane label="Redis监控" name="redis">
          <div class="redis-header">
            <div></div>
            <el-button
              type="primary"
              size="small"
              icon="el-icon-s-management"
              @click="goCacheMonitor"
            >
              进入缓存监控
            </el-button>
          </div>
          <el-row :gutter="20" v-loading="loading">
            <el-col :span="8">
              <el-card shadow="hover" class="stat-card-small">
                <div class="stat-content-small">
                  <i class="el-icon-connection stat-icon-small" style="color: #409EFF"></i>
                  <div class="stat-info-small">
                    <div class="stat-value-small">{{ redisInfo.version || '-' }}</div>
                    <div class="stat-label-small">Redis版本</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="stat-card-small">
                <div class="stat-content-small">
                  <i class="el-icon-time stat-icon-small" style="color: #67C23A"></i>
                  <div class="stat-info-small">
                    <div class="stat-value-small">{{ redisInfo.uptime || 0 }}天</div>
                    <div class="stat-label-small">运行天数</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="stat-card-small">
                <div class="stat-content-small">
                  <i class="el-icon-user stat-icon-small" style="color: #E6A23C"></i>
                  <div class="stat-info-small">
                    <div class="stat-value-small">{{ redisInfo.connectedClients || 0 }}</div>
                    <div class="stat-label-small">连接数</div>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="12">
              <el-card shadow="hover" class="info-card">
                <div class="card-header-custom">
                  <i class="el-icon-coin" style="color: #409EFF"></i>
                  <span>内存使用</span>
                </div>
                <div class="progress-item">
                  <div class="progress-header">
                    <span>内存使用量</span>
                    <span class="progress-value">{{ redisInfo.usedMemoryHuman || '0M' }}</span>
                  </div>
                  <el-progress
                    :percentage="parseFloat(redisInfo.memoryUsage || 0)"
                    :color="getProgressColor(redisInfo.memoryUsage)"
                  ></el-progress>
                </div>
                <div class="info-list">
                  <div class="info-item">
                    <span class="info-label">峰值：</span>
                    <span class="info-value">{{ redisInfo.usedMemoryPeakHuman || '0M' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">碎片率：</span>
                    <span class="info-value">{{ redisInfo.memFragmentationRatio || 0 }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="12">
              <el-card shadow="hover" class="info-card">
                <div class="card-header-custom">
                  <i class="el-icon-data-analysis" style="color: #67C23A"></i>
                  <span>命令统计</span>
                </div>
                <div class="info-list">
                  <div class="info-item">
                    <span class="info-label">执行命令数：</span>
                    <span class="info-value">{{ redisInfo.totalCommandsProcessed || 0 }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">Key总数：</span>
                    <span class="info-value">{{ redisInfo.dbSize || 0 }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">网络输入：</span>
                    <span class="info-value">{{ redisInfo.totalNetInputBytes || '0B' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">网络输出：</span>
                    <span class="info-value">{{ redisInfo.totalNetOutputBytes || '0B' }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="12">
              <el-card shadow="hover" class="info-card">
                <div class="card-header-custom">
                  <i class="el-icon-document-copy" style="color: #909399"></i>
                  <span>持久化状态</span>
                </div>
                <div class="info-list">
                  <div class="info-item">
                    <span class="info-label">AOF持久化：</span>
                    <span class="info-value">
                      <el-tag
                        :type="redisInfo.aofEnabledStatus ? 'success' : 'info'"
                        size="small"
                      >
                        {{ redisInfo.aofEnabled || '未知' }}
                      </el-tag>
                    </span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">RDB快照状态：</span>
                    <span class="info-value">
                      <el-tag
                        :type="redisInfo.rdbLastSaveStatusOk ? 'success' : 'danger'"
                        size="small"
                      >
                        {{ redisInfo.rdbLastSaveStatus || '未知' }}
                      </el-tag>
                    </span>
                  </div>
                  <div class="info-item" v-if="redisInfo.rdbLastSaveTime">
                    <span class="info-label">上次RDB保存：</span>
                    <span class="info-value">{{ formatTimestamp(redisInfo.rdbLastSaveTime) }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 数据库监控 -->
        <el-tab-pane label="数据库监控" name="database">
          <el-row :gutter="20" v-loading="loading">
            <el-col :span="24">
              <el-card shadow="hover" class="info-card">
                <div class="card-header-custom">
                  <i class="el-icon-coin" style="color: #409EFF"></i>
                  <span>Druid连接池信息</span>
                </div>
                <el-table :data="[dbInfo]" border style="width: 100%">
                  <el-table-column prop="poolingCount" label="活跃连接数" width="120" align="center" />
                  <el-table-column prop="activeCount" label="当前活跃连接" width="130" align="center" />
                  <el-table-column prop="maxActive" label="最大连接数" width="120" align="center" />
                  <el-table-column prop="initialSize" label="初始连接数" width="120" align="center" />
                  <el-table-column prop="minIdle" label="最小空闲连接" width="130" align="center" />
                  <el-table-column prop="maxWait" label="最大等待时间" width="130" align="center">
                    <template slot-scope="scope">
                      {{ scope.row.maxWait }}ms
                    </template>
                  </el-table-column>
                </el-table>

                <el-divider></el-divider>

                <div class="info-list">
                  <el-row :gutter="20">
                    <el-col :span="8">
                      <div class="info-item">
                        <span class="info-label">连接获取总数：</span>
                        <span class="info-value">{{ dbInfo.connectCount || 0 }}</span>
                      </div>
                    </el-col>
                    <el-col :span="8">
                      <div class="info-item">
                        <span class="info-label">连接关闭总数：</span>
                        <span class="info-value">{{ dbInfo.closeCount || 0 }}</span>
                      </div>
                    </el-col>
                    <el-col :span="8">
                      <div class="info-item">
                        <span class="info-label">等待线程数：</span>
                        <span class="info-value">{{ dbInfo.waitThreadCount || 0 }}</span>
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { getServerInfo } from '@/api/monitor/server'

export default {
  name: 'ServerMonitor',
  data() {
    return {
      loading: false,
      activeTab: 'server',
      autoRefresh: false,
      refreshInterval: null,
      serverInfo: {
        cpu: {},
        mem: {},
        jvm: {},
        diskList: []
      },
      redisInfo: {},
      dbInfo: {}
    }
  },
  created() {
    this.loadServerInfo()
  },
  beforeDestroy() {
    this.stopAutoRefresh()
  },
  methods: {
    /** 加载服务器信息 */
    async loadServerInfo() {
      this.loading = true
      try {
        const response = await getServerInfo()
        if (response.code === 200 && response.data) {
          this.serverInfo = response.data.server || {}
          this.redisInfo = response.data.redis || {}
          this.dbInfo = response.data.database || {}
        }
      } catch (_) {
        this.$message.error('获取服务器信息失败')
      } finally {
        this.loading = false
      }
    },
    /** 获取进度条颜色 */
    getProgressColor(percentage) {
      const percent = parseFloat(percentage)
      if (percent < 60) {
        return '#67C23A'
      } else if (percent < 80) {
        return '#E6A23C'
      } else {
        return '#F56C6C'
      }
    },
    /** 自动刷新切换 */
    handleAutoRefreshChange(val) {
      if (val) {
        this.startAutoRefresh()
      } else {
        this.stopAutoRefresh()
      }
    },
    /** 开始自动刷新 */
    startAutoRefresh() {
      this.refreshInterval = setInterval(() => {
        this.loadServerInfo()
      }, 5000) // 每5秒刷新一次
    },
    /** 停止自动刷新 */
    stopAutoRefresh() {
      if (this.refreshInterval) {
        clearInterval(this.refreshInterval)
        this.refreshInterval = null
      }
    },
    /** 跳转到缓存监控 */
    goCacheMonitor() {
      this.$router.push('/monitor/cache')
    },
    /** 格式化时间戳 */
    formatTimestamp(timestamp) {
      if (!timestamp || timestamp === '0') {
        return '从未保存'
      }
      const date = new Date(parseInt(timestamp) * 1000)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style lang="scss" scoped>
.server-monitor-container {
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

    .refresh-controls {
      display: flex;
      align-items: center;
    }
  }

  .redis-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .info-card {
    margin-bottom: 20px;

    .card-header-custom {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      padding-bottom: 15px;
      border-bottom: 1px solid #ebeef5;
      font-size: 16px;
      font-weight: 600;
      color: #303133;

      i {
        font-size: 20px;
        margin-right: 8px;
      }
    }

    .progress-item {
      margin-bottom: 20px;

      .progress-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;
        font-size: 14px;
        color: #606266;

        .progress-value {
          font-weight: 600;
          color: #409EFF;
        }
      }

      .disk-info {
        display: flex;
        justify-content: space-between;
        margin-top: 5px;
        font-size: 12px;
        color: #909399;
      }
    }

    .info-list {
      .info-item {
        display: flex;
        justify-content: space-between;
        padding: 10px 0;
        font-size: 14px;
        border-bottom: 1px dashed #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        .info-label {
          color: #909399;
        }

        .info-value {
          color: #606266;
          font-weight: 500;
        }
      }
    }

    .no-data {
      text-align: center;
      padding: 40px 0;
      color: #909399;
      font-size: 14px;
    }
  }

  .stat-card-small {
    margin-bottom: 20px;

    ::v-deep .el-card__body {
      padding: 20px;
    }

    .stat-content-small {
      display: flex;
      align-items: center;
      gap: 15px;

      .stat-icon-small {
        font-size: 40px;
      }

      .stat-info-small {
        flex: 1;

        .stat-value-small {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 5px;
        }

        .stat-label-small {
          font-size: 13px;
          color: #909399;
        }
      }
    }
  }
}
</style>

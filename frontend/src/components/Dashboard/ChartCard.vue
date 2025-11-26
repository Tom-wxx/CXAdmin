<template>
  <div class="chart-card">
    <div class="chart-card-header">
      <div class="header-left">
        <div class="header-dot"></div>
        <span class="chart-card-title">{{ title }}</span>
      </div>
      <slot name="extra"></slot>
    </div>
    <div class="chart-card-body">
      <div ref="chart" class="chart-container" :style="{ height: height }"></div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'ChartCard',
  props: {
    title: {
      type: String,
      required: true
    },
    height: {
      type: String,
      default: '350px'
    },
    chartType: {
      type: String,
      required: true,
      validator: value => ['line', 'bar', 'pie'].includes(value)
    },
    chartData: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler() {
        this.renderChart()
      }
    }
  },
  mounted() {
    this.initChart()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.chart)
      this.renderChart()
    },
    renderChart() {
      if (!this.chart || !this.chartData) return

      let option = {}

      switch (this.chartType) {
        case 'line':
          option = this.getLineChartOption()
          break
        case 'bar':
          option = this.getBarChartOption()
          break
        case 'pie':
          option = this.getPieChartOption()
          break
      }

      this.chart.setOption(option, true)
    },
    getLineChartOption() {
      return {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          },
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e8e8e8',
          borderWidth: 1,
          textStyle: {
            color: '#666'
          },
          padding: [10, 15]
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.chartData.labels || [],
          axisLine: {
            lineStyle: {
              color: '#e8e8e8'
            }
          },
          axisLabel: {
            color: '#999'
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            color: '#999'
          },
          splitLine: {
            lineStyle: {
              color: '#f5f5f5',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: this.title,
            type: 'line',
            smooth: true,
            data: this.chartData.values || [],
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
              ])
            },
            itemStyle: {
              color: '#667eea'
            },
            lineStyle: {
              width: 3,
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#667eea' },
                { offset: 1, color: '#764ba2' }
              ])
            },
            emphasis: {
              itemStyle: {
                borderColor: '#fff',
                borderWidth: 3,
                shadowBlur: 10,
                shadowColor: 'rgba(102, 126, 234, 0.5)'
              }
            }
          }
        ]
      }
    },
    getBarChartOption() {
      return {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e8e8e8',
          borderWidth: 1,
          textStyle: {
            color: '#666'
          },
          padding: [10, 15]
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.chartData.labels || [],
          axisLine: {
            lineStyle: {
              color: '#e8e8e8'
            }
          },
          axisLabel: {
            interval: 0,
            rotate: this.chartData.labels && this.chartData.labels.length > 7 ? 30 : 0,
            color: '#999'
          },
          axisTick: {
            show: false
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            color: '#999'
          },
          splitLine: {
            lineStyle: {
              color: '#f5f5f5',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: this.title,
            type: 'bar',
            data: this.chartData.values || [],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#4facfe' },
                { offset: 1, color: '#00f2fe' }
              ]),
              borderRadius: [8, 8, 0, 0]
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(79, 172, 254, 0.5)'
              }
            },
            barWidth: '50%',
            barMaxWidth: 40
          }
        ]
      }
    },
    getPieChartOption() {
      const pieData = (this.chartData.labels || []).map((label, index) => ({
        name: label,
        value: (this.chartData.values || [])[index] || 0
      }))

      const colors = ['#667eea', '#f093fb', '#4facfe', '#fa709a', '#feca57', '#48dbfb']

      return {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e8e8e8',
          borderWidth: 1,
          textStyle: {
            color: '#666'
          },
          padding: [10, 15]
        },
        legend: {
          orient: 'vertical',
          right: '8%',
          top: 'center',
          data: this.chartData.labels || [],
          textStyle: {
            color: '#666',
            fontSize: 13
          },
          itemWidth: 12,
          itemHeight: 12,
          itemGap: 12
        },
        color: colors,
        series: [
          {
            name: this.title,
            type: 'pie',
            radius: ['45%', '70%'],
            center: ['40%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 3,
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.1)'
            },
            label: {
              show: false
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold',
                color: '#333'
              },
              itemStyle: {
                shadowBlur: 20,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.3)'
              }
            },
            labelLine: {
              show: false
            },
            data: pieData
          }
        ]
      }
    },
    handleResize() {
      if (this.chart) {
        this.chart.resize()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.chart-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 0.12);
  }

  .chart-card-header {
    padding: 20px 24px 0;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
    }

    .header-dot {
      width: 4px;
      height: 18px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 2px;
      margin-right: 12px;
    }

    .chart-card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      letter-spacing: 0.3px;
    }
  }

  .chart-card-body {
    padding: 20px 24px 24px;

    .chart-container {
      width: 100%;
    }
  }
}
</style>

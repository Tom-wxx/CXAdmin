<template>
  <div class="chart-card">
    <div class="chart-card-header">
      <span class="chart-card-title">{{ title }}</span>
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
      default: '320px'
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
          backgroundColor: '#fff',
          borderColor: '#e8e8e8',
          borderWidth: 1,
          textStyle: { color: '#595959', fontSize: 12 },
          padding: [8, 12]
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '8%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.chartData.labels || [],
          axisLine: { lineStyle: { color: '#e8e8e8' } },
          axisLabel: { color: '#8c8c8c', fontSize: 11 },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#8c8c8c', fontSize: 11 },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        series: [
          {
            name: this.title,
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            data: this.chartData.values || [],
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(19, 194, 194, 0.15)' },
                { offset: 1, color: 'rgba(19, 194, 194, 0.01)' }
              ])
            },
            itemStyle: { color: '#13c2c2' },
            lineStyle: { width: 2, color: '#13c2c2' }
          }
        ]
      }
    },
    getBarChartOption() {
      return {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          backgroundColor: '#fff',
          borderColor: '#e8e8e8',
          borderWidth: 1,
          textStyle: { color: '#595959', fontSize: 12 },
          padding: [8, 12]
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '8%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.chartData.labels || [],
          axisLine: { lineStyle: { color: '#e8e8e8' } },
          axisLabel: {
            interval: 0,
            rotate: this.chartData.labels && this.chartData.labels.length > 7 ? 30 : 0,
            color: '#8c8c8c',
            fontSize: 11
          },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#8c8c8c', fontSize: 11 },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        series: [
          {
            name: this.title,
            type: 'bar',
            data: this.chartData.values || [],
            itemStyle: {
              color: '#13c2c2',
              borderRadius: [2, 2, 0, 0]
            },
            barWidth: '50%',
            barMaxWidth: 36
          }
        ]
      }
    },
    getPieChartOption() {
      const pieData = (this.chartData.labels || []).map((label, index) => ({
        name: label,
        value: (this.chartData.values || [])[index] || 0
      }))

      const colors = ['#13c2c2', '#1890ff', '#52c41a', '#fa8c16', '#f5222d', '#722ed1']

      return {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)',
          backgroundColor: '#fff',
          borderColor: '#e8e8e8',
          borderWidth: 1,
          textStyle: { color: '#595959', fontSize: 12 },
          padding: [8, 12]
        },
        legend: {
          orient: 'vertical',
          right: '8%',
          top: 'center',
          data: this.chartData.labels || [],
          textStyle: { color: '#595959', fontSize: 12 },
          itemWidth: 8,
          itemHeight: 8,
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
              borderColor: '#fff',
              borderWidth: 2
            },
            label: { show: false },
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: '600',
                color: '#303133'
              }
            },
            labelLine: { show: false },
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
  border-radius: 4px;
  border: 1px solid #e8e8e8;
  margin-bottom: 16px;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 1px 6px rgba(0, 0, 0, 0.08);
  }

  .chart-card-header {
    padding: 12px 16px;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .chart-card-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
    }
  }

  .chart-card-body {
    padding: 16px;

    .chart-container {
      width: 100%;
    }
  }
}
</style>

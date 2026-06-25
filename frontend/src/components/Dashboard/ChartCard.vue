<template>
  <div class="chart-card">
    <div class="chart-card-header">
      <span class="chart-card-title">{{ title }}</span>
      <slot name="extra"></slot>
    </div>
    <div class="chart-card-body">
      <div ref="chartRef" class="chart-container" :style="{ height: height }"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import * as echarts from 'echarts'
import { useECharts } from '@/composables/useECharts'

interface ChartData {
  labels?: string[]
  values?: number[]
}

interface Props {
  title: string
  height?: string
  chartType: 'line' | 'bar' | 'pie'
  chartData?: ChartData
}

const props = withDefaults(defineProps<Props>(), {
  height: '320px',
  chartData: () => ({})
})

defineOptions({ name: 'ChartCard' })

type EChartsOption = Parameters<echarts.ECharts['setOption']>[0]

const chartRef = ref<HTMLElement>()
const { setOption } = useECharts(chartRef)

function buildOption(): EChartsOption {
  switch (props.chartType) {
    case 'line': return getLineChartOption()
    case 'bar':  return getBarChartOption()
    case 'pie':  return getPieChartOption()
    default:     return {}
  }
}

function getLineChartOption() {
  return {
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#fff',
      borderColor: '#e8e8e8',
      borderWidth: 1,
      textStyle: { color: '#595959', fontSize: 12 },
      padding: [8, 12]
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '8%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: props.chartData?.labels ?? [],
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
        name: props.title,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: props.chartData?.values ?? [],
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
}

function getBarChartOption() {
  const labels = props.chartData?.labels ?? []
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
    grid: { left: '3%', right: '4%', bottom: '3%', top: '8%', containLabel: true },
    xAxis: {
      type: 'category',
      data: labels,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: {
        interval: 0,
        rotate: labels.length > 7 ? 30 : 0,
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
        name: props.title,
        type: 'bar',
        data: props.chartData?.values ?? [],
        itemStyle: { color: '#13c2c2', borderRadius: [2, 2, 0, 0] },
        barWidth: '50%',
        barMaxWidth: 36
      }
    ]
  }
}

function getPieChartOption() {
  const labels = props.chartData?.labels ?? []
  const values = props.chartData?.values ?? []
  const pieData = labels.map((label, index) => ({ name: label, value: values[index] ?? 0 }))
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
      data: labels,
      textStyle: { color: '#595959', fontSize: 12 },
      itemWidth: 8,
      itemHeight: 8,
      itemGap: 12
    },
    color: colors,
    series: [
      {
        name: props.title,
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: { borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: '600', color: '#303133' }
        },
        labelLine: { show: false },
        data: pieData
      }
    ]
  }
}

onMounted(() => {
  setOption(buildOption())
})

watch(
  () => props.chartData,
  () => { setOption(buildOption()) },
  { deep: true }
)
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

import { onBeforeUnmount, onMounted, type Ref } from 'vue'
import * as echarts from 'echarts'

type EChartsInstance = ReturnType<typeof echarts.init>
type EChartsOption = Parameters<EChartsInstance['setOption']>[0]

/**
 * ECharts 生命周期封装：自动 init / resize 监听 / dispose 清理（修复 resize 监听泄漏）。
 * 用法：const chartRef = ref<HTMLElement>(); const { setOption } = useECharts(chartRef)
 */
export function useECharts(elRef: Ref<HTMLElement | undefined>) {
  let chart: EChartsInstance | null = null

  function init(): EChartsInstance | null {
    if (!chart && elRef.value) {
      chart = echarts.init(elRef.value)
    }
    return chart
  }

  function setOption(option: EChartsOption): void {
    if (!chart) init()
    chart?.setOption(option)
  }

  function resize(): void {
    chart?.resize()
  }

  onMounted(() => {
    window.addEventListener('resize', resize)
  })

  onBeforeUnmount(() => {
    window.removeEventListener('resize', resize)
    chart?.dispose()
    chart = null
  })

  return { init, setOption, resize, getInstance: () => chart }
}

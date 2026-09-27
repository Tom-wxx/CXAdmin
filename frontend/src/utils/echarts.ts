/**
 * ECharts 按需入口：只注册项目实际用到的图表与组件，避免 `import * as echarts from 'echarts'`
 * 把全部图表类型打进包里。新增图表类型/组件时在这里补注册，否则运行时会提示未注册。
 */
import * as echarts from 'echarts/core'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([
  LineChart,
  BarChart,
  PieChart,
  GridComponent,
  LegendComponent,
  TooltipComponent,
  CanvasRenderer
])

export default echarts

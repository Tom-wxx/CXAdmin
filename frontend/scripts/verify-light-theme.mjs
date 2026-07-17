import { readFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..')
const read = path => readFileSync(resolve(root, path), 'utf8')

const files = {
  settings: read('src/store/modules/settings.js'),
  themeSettings: read('src/components/ThemeSettings/index.vue'),
  layout: read('src/layout/index.vue'),
  appMain: read('src/layout/components/AppMain.vue'),
  indexScss: read('src/styles/index.scss'),
  refreshScss: read('src/styles/refresh.scss'),
  dashboard: read('src/views/index.vue'),
  chartCard: read('src/components/Dashboard/ChartCard.vue'),
  sidebarLogo: read('src/layout/components/Sidebar/Logo.vue'),
  iconMap: read('src/utils/iconMap.js'),
  router: read('src/router/index.js'),
  searchForm: read('src/components/SearchForm/index.vue'),
  tableToolbar: read('src/components/TableToolbar/index.vue'),
  profile: read('src/views/user/profile/index.vue')
}

const checks = [
  ['settings default sidebar is white', files.settings.includes("|| '#ffffff'")],
  ['theme reset uses white sidebar', files.themeSettings.includes("setSidebarColor('#ffffff')")],
  ['theme presets include quiet white', files.themeSettings.includes("name: '浅白'")],
  ['layout has light sidebar border token', files.layout.includes('$sidebar-border: #e5e7eb;')],
  ['app main uses quiet page background', files.appMain.includes('background: #f5f7fb;')],
  ['global scss uses 8px admin radius', files.indexScss.includes('$admin-radius: 8px;')],
  ['refresh scss uses quiet shadow token', files.refreshScss.includes('$shadow-panel: 0 1px 2px rgba(15, 23, 42, 0.04);')],
  ['refresh scss removes strong rotate hover', !files.refreshScss.includes('rotate(15deg)')],
  ['refresh scss removes large translate hover', !files.refreshScss.includes('translateY(-3px)')],
  ['dashboard hero no longer uses teal gradient', !files.dashboard.includes('linear-gradient(135deg, #0f7474')],
  ['dashboard uses light welcome panel', files.dashboard.includes('background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);')],
  ['chart card uses 8px radius', files.chartCard.includes('border-radius: 8px;')],
  ['sidebar logo title adapts to light background', files.sidebarLogo.includes('color: var(--logo-title-color);')],
  ['home route keeps dashboard icon metadata', files.router.includes("icon: 'dashboard'")],
  ['dashboard icon resolves for home menu', files.iconMap.includes("'dashboard': 'Odometer'")],
  ['search form uses default control size', files.searchForm.includes('size="default"')],
  ['search input default width is balanced', files.searchForm.includes("field.width || '220px'")],
  ['search daterange default width is balanced', files.searchForm.includes("field.width || '300px'")],
  ['search controls have consistent height token', files.refreshScss.includes('$search-control-height: 32px;')],
  ['table toolbar buttons use default control size', files.tableToolbar.includes('size="default"') && !files.tableToolbar.includes('size="small"')],
  ['table toolbar controls have consistent height token', files.refreshScss.includes('$toolbar-button-height: 32px;')],
  ['table toolbar controls use balanced horizontal padding', files.refreshScss.includes('padding: 0 14px !important;')],
  ['profile page uses quiet light tokens', files.profile.includes('$profile-accent: #0f9f9f;')],
  ['profile page removes purple hero gradient', !files.profile.includes('#667eea') && !files.profile.includes('#764ba2')],
  ['profile page uses 8px card radius', files.profile.includes('$profile-radius: 8px;')],
  ['profile page removes strong card lift', !files.profile.includes('translateY(-3px)') && !files.profile.includes('translateX(5px)')]
]

const failed = checks.filter(([, ok]) => !ok)

if (failed.length > 0) {
  console.error('Light theme verification failed:')
  failed.forEach(([name]) => console.error(`- ${name}`))
  process.exit(1)
}

console.log(`Light theme verification passed (${checks.length} checks).`)

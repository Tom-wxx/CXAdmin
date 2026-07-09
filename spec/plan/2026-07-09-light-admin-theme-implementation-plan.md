# Light Admin Theme Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将前端整体视觉调整为浅色、克制、适合高频操作的后台管理风格。

**Architecture:** 保持现有 Vue 3 + Element Plus 架构，只修改视觉层和默认主题配置。新增一个轻量静态验证脚本，用来锁定浅色默认侧栏、统一半径、低动效和首页去渐变等关键约束。

**Tech Stack:** Vue 3, TypeScript, Element Plus 2.14, Vite 6, SCSS, Node.js.

## Global Constraints

- 中文解释。
- TODO/PLAN 文件都存放在项目根目录 `spec/plan` 下。
- 不新增 UI 依赖。
- 不调整后端、路由、接口、权限、菜单数据或数据库。
- 只实现浅色主题，不做暗色模式。
- 优先沿用 Element Plus 和现有组件结构。
- 后台页面必须保持高信息密度和稳定布局，避免营销页式大动效。

---

## File Structure

- Create: `frontend/scripts/verify-light-theme.mjs`
  - 读取前端关键文件，验证浅色主题硬约束。
- Modify: `frontend/package.json`
  - 增加 `test:theme` 脚本。
- Modify: `frontend/src/store/modules/settings.js`
  - 默认侧栏色改为浅色。
- Modify: `frontend/src/components/ThemeSettings/index.vue`
  - 主题预设改为浅色优先，重置恢复浅色。
- Modify: `frontend/src/styles/index.scss`
  - 整理基础 token 和 Element Plus 全局主题覆盖。
- Modify: `frontend/src/styles/refresh.scss`
  - 降低演示感，统一面板、表格、弹窗、下拉、分页、动效。
- Modify: `frontend/src/layout/index.vue`
  - 侧栏、顶部栏、菜单选中态浅色化。
- Modify: `frontend/src/layout/components/AppMain.vue`
  - 主内容区背景和间距统一。
- Modify: `frontend/src/views/index.vue`
  - 首页 dashboard 改为轻量后台首页。
- Modify: `frontend/src/components/Dashboard/ChartCard.vue`
  - 图表卡片半径、边框和图表辅助色跟随浅色主题。
- Modify: `frontend/src/components/Dashboard/StatCard.vue`
  - 统计卡片半径、边框和 hover 降噪。

---

### Task 1: Add Theme Verification

**Files:**
- Create: `frontend/scripts/verify-light-theme.mjs`
- Modify: `frontend/package.json`

**Interfaces:**
- Produces: `npm run test:theme`

- [ ] **Step 1: Create the failing static verification script**

Create `frontend/scripts/verify-light-theme.mjs` with these checks:

```js
import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const root = resolve(import.meta.dirname, '..')
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
  statCard: read('src/components/Dashboard/StatCard.vue')
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
  ['stat card uses 8px radius', files.statCard.includes('border-radius: 8px;')]
]

const failed = checks.filter(([, ok]) => !ok)

if (failed.length > 0) {
  console.error('Light theme verification failed:')
  failed.forEach(([name]) => console.error(`- ${name}`))
  process.exit(1)
}

console.log(`Light theme verification passed (${checks.length} checks).`)
```

- [ ] **Step 2: Add npm script**

In `frontend/package.json`, add:

```json
"test:theme": "node scripts/verify-light-theme.mjs"
```

- [ ] **Step 3: Run test and verify RED**

Run: `cd frontend && npm run test:theme`

Expected: exit 1 with failures for the current dark sidebar and dashboard gradient.

---

### Task 2: Apply Global Light Theme Tokens

**Files:**
- Modify: `frontend/src/store/modules/settings.js`
- Modify: `frontend/src/components/ThemeSettings/index.vue`
- Modify: `frontend/src/styles/index.scss`
- Modify: `frontend/src/styles/refresh.scss`
- Test: `frontend/scripts/verify-light-theme.mjs`

**Interfaces:**
- Consumes: `npm run test:theme`
- Produces: global SCSS tokens `$admin-primary`, `$admin-radius`, `$shadow-panel`

- [ ] **Step 1: Change default theme state**

In `frontend/src/store/modules/settings.js`:

```js
const getSidebarColor = () => {
  return localStorage.getItem('sidebarColor') || '#ffffff'
}
```

- [ ] **Step 2: Update theme presets and reset**

In `frontend/src/components/ThemeSettings/index.vue`:

```ts
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
```

Reset must use:

```ts
setSidebarColor('#ffffff')
```

- [ ] **Step 3: Replace global SCSS foundation**

In `frontend/src/styles/index.scss`, use these tokens:

```scss
$admin-primary: #0f9f9f;
$admin-primary-dark: #087f83;
$admin-primary-light: #e6fffb;
$admin-bg: #f5f7fb;
$admin-surface: #ffffff;
$admin-text: #1f2937;
$admin-text-secondary: #64748b;
$admin-border: #e5e7eb;
$admin-radius: 8px;
```

Element Plus overrides must use `$admin-primary`, `$admin-radius`, and `$admin-border`.

- [ ] **Step 4: Replace refresh SCSS visual layer**

In `frontend/src/styles/refresh.scss`, use these tokens:

```scss
$accent: #0f9f9f;
$page-bg: #f5f7fb;
$surface: #ffffff;
$card-border: #e5e7eb;
$text-1: #1f2937;
$text-2: #64748b;
$shadow-panel: 0 1px 2px rgba(15, 23, 42, 0.04);
$shadow-popover: 0 12px 28px rgba(15, 23, 42, 0.12);
$radius: 8px;
```

Remove strong hover transforms:

```scss
transform: translateY(-2px);
transform: translateY(-3px);
transform: rotate(15deg);
```

Replace with border, background, and color transitions only.

- [ ] **Step 5: Run theme test**

Run: `cd frontend && npm run test:theme`

Expected: still fails only for layout, app main, dashboard, chart card, and stat card checks.

---

### Task 3: Apply Light Layout and Dashboard

**Files:**
- Modify: `frontend/src/layout/index.vue`
- Modify: `frontend/src/layout/components/AppMain.vue`
- Modify: `frontend/src/views/index.vue`
- Modify: `frontend/src/components/Dashboard/ChartCard.vue`
- Modify: `frontend/src/components/Dashboard/StatCard.vue`
- Test: `frontend/scripts/verify-light-theme.mjs`

**Interfaces:**
- Consumes: global SCSS light tokens from Task 2.
- Produces: shallow, quiet visual treatment across layout and dashboard.

- [ ] **Step 1: Update layout SCSS tokens**

In `frontend/src/layout/index.vue` scoped style, use:

```scss
$primary: #0f9f9f;
$sidebar-bg: #ffffff;
$sidebar-border: #e5e7eb;
$sidebar-active-bg: #e6fffb;
$page-bg: #f5f7fb;
$text-main: #1f2937;
$text-muted: #64748b;
$sidebar-width: 208px;
$sidebar-collapsed-width: 64px;
$navbar-height: 48px;
```

Menu selected state must be:

```scss
:deep(.el-menu-item.is-active) {
  background-color: $sidebar-active-bg !important;
  color: $primary !important;
  font-weight: 600;
}
```

- [ ] **Step 2: Update AppMain**

In `frontend/src/layout/components/AppMain.vue`, use:

```scss
.app-main {
  min-height: calc(100vh - 48px);
  width: 100%;
  position: relative;
  overflow: auto;
  padding: 16px;
  background: #f5f7fb;
}
```

- [ ] **Step 3: Replace dashboard hero style**

In `frontend/src/views/index.vue`, replace the hero background with:

```scss
background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
color: $text-1;
border: 1px solid $card-border;
box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
```

Remove floating bubble animation and strong decorative teal icon treatment.

- [ ] **Step 4: Update dashboard cards**

Use `border-radius: 8px;`, `border: 1px solid #e5e7eb;`, and low shadow for:

```scss
.stat-card-fresh
.bento-card
.quick-tile
.chart-card
.stat-card
```

- [ ] **Step 5: Run theme test and verify GREEN**

Run: `cd frontend && npm run test:theme`

Expected: `Light theme verification passed (13 checks).`

---

### Task 4: Build and Manual QA Readiness

**Files:**
- Existing frontend files only.

**Interfaces:**
- Consumes: completed Tasks 1-3.
- Produces: build-verified shallow light theme.

- [ ] **Step 1: Run production build**

Run: `cd frontend && npm run build`

Expected: Vite build exits 0 and emits `dist`.

- [ ] **Step 2: Run lint when possible**

Run: `cd frontend && npm run lint`

Expected: exit 0, or report exact pre-existing lint/tooling failure if blocked.

- [ ] **Step 3: Inspect changed files**

Run: `git diff -- frontend package.json spec/plan`

Expected: diff only contains the light theme plan, verification script, package script, and visual changes.

- [ ] **Step 4: Manual QA checklist**

After starting the frontend with `cd frontend && npm run dev`, manually inspect:

- Login landing redirect/default layout.
- Dashboard.
- User management table.
- Menu or department tree page.
- Theme settings drawer.
- Add/edit dialog.

Expected: all are light themed, visually consistent, and do not show obvious overlapping UI.

---

## Self-Review

- Spec coverage: all implementation sections from the design spec map to Tasks 1-4.
- Placeholder scan: no TODO/TBD/fill-later language is used.
- Type consistency: the only new interface is `npm run test:theme`, defined in Task 1 and consumed by later tasks.
- Scope check: no backend, route, API, permission, menu-data, database, or new UI dependency work is included.

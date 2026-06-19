# 前端 Vue 2 → Vue 3 迁移记录

> 状态：**已完成**。`npm run build` 0 error、`npm run lint` 0 error、`npm run dev` 编译通过且登录页冒烟无 Vue 告警。
> 分支：`feat/vue3-migration`。

## 锁定决策

| 维度 | 决策 |
|---|---|
| 构建工具 | 保留 **@vue/cli 5**（webpack），`vue.config.js`（proxy / svg-sprite-loader / `@` alias）不变 |
| 状态管理 | 升级 **Vuex 4**（`createStore`），modules / getters 不动 |
| 组件写法 | 保留 **Options API**，只改破坏性语法 |
| 执行节奏 | 一次性全量迁移 |

## 依赖变更（`frontend/package.json`）

- 升级：`vue ^3.5`、`vue-router ^4.5`、`vuex ^4.1`
- 替换：`element-ui` → `element-plus ^2.14` + `@element-plus/icons-vue`；`@riophae/vue-treeselect` → Element Plus 原生 `el-tree-select`；`vue-quill-editor` → `@vueup/vue-quill ^1.5`
- 工具链：`babel-eslint` → `@babel/eslint-parser`；`eslint-plugin-vue ^9`（vue3-essential）；新增 `@vue/compiler-sfc`
- 版本钉选注意：`vue-router`、`eslint-plugin-vue`、`@babel/eslint-parser` 的「latest」需要 ESLint 9 flat config，会与 @vue/cli 5 的 legacy eslintrc 冲突——必须钉在 v4 / v9 / v7。

## 引导层

- `main.js`：`createApp`；`app.use(ElementPlus, { locale: zhCn })`；遍历 `@element-plus/icons-vue` 全局注册全部图标；注册全局 `<menu-icon>`；`app.mixin` / `app.config.errorHandler`。
- `router/index.js`：`createRouter` + `createWebHistory`；`addRoutes` → `addRoute`；通配 `'*'` → `'/:pathMatch(.*)*'`；动态组件 `() => import()`。
- `store/index.js`：`createStore`。
- `permission.js`：`accessRoutes.forEach(r => router.addRoute(r))`；`ElMessage`。
- `layout/components/AppMain.vue`：`<router-view v-slot="{ Component }">` + `<transition><keep-alive><component :is>`。

## 模板/语法 codemod（已跑完并删除脚本）

一次性脚本 `frontend/scripts/codemod-vue3*.js`（已删除）批量处理：

- **图标**：字体图标 `el-icon-xxx` → Element Plus SVG 组件；`icon="el-icon-x"` 属性 → `icon="Pascal"`（全局注册，按钮内部 `resolveDynamicComponent` 解析）；建立 84 项「Element UI → Element Plus」改名映射。
- `size="mini"` → `small`、`size="medium"` → `default`。
- `:visible.sync` → `v-model`；`:prop.sync` → `v-model:prop`。
- 旧插槽 `slot=` / `slot-scope` → `#name` / `#default`。
- 元素级 `slot="x"` 包裹为 `<template #x>`。
- `::v-deep .foo` → `:deep(.foo)`；删除事件 `.native` 修饰符。
- `el-radio` / `el-radio-button` 的 `label`(作为值) → `value`（EP 2.6+ 废弃 label-as-value）。

## 手工处理点（非机械可替换）

- **图标解析层**：`utils/iconMap.js`（旧名 → EP 组件名解析，未知返回 null）+ 全局组件 `components/MenuIcon`。数据库存储的菜单图标历史名（`user` / `s-tools` / `time` …）经它解析；侧边栏 `SidebarItem`、顶栏、菜单表格/表单前缀、个人中心 SSO 绑定图标、仪表盘卡片、缓存/文件类型图标均改走 `<menu-icon :name="...">`。新选图标存 EP PascalCase 名。
- **IconSelect**：菜单图标选择器重做——直接列出 `@element-plus/icons-vue` 全量图标，`<el-icon><component :is="name"/></el-icon>` 渲染，emit PascalCase 名。
- **组件改名**：`el-submenu` → `el-sub-menu`（含 SCSS 类 `.el-submenu__*` → `.el-sub-menu__*`）；dropdown caret 由 `.el-icon-caret-bottom` 类改为 `<el-icon>`（CSS 选择器随之改 `.el-icon`）。
- **第三方库**：4 处 `<treeselect>`（menu/dept/user/sso）→ `<el-tree-select :data :props node-key value-key check-strictly>`，移除 normalizer；notice 富文本 `vue-quill-editor` → `@vueup/vue-quill`（`v-model:content` + `content-type="html"`）。
- **echarts**：`beforeDestroy` → `beforeUnmount`（3 个图表组件 dispose 不变）。
- **`$set` 移除**：`SearchForm` 改直接赋值（Vue 3 Proxy 响应式）。
- **`request.js`**：`element-ui` 的 `Message/MessageBox` → `element-plus` 的 `ElMessage/ElMessageBox`。
- **`ThemeSettings`**：`visible` prop + `.sync` → 标准 `modelValue` + `update:modelValue`（computed 代理 `el-drawer` 的 v-model）。
- **登录页密码可见切换**：`<i el-icon-view/minus>` → `<el-icon><View/Hide></el-icon>`。
- 删除死文件：`views/user/profile/index-backup.vue`、`index-simple.vue`、`index-backup-full.vue`。

## ESLint 规则调整（`package.json` eslintConfig.rules）

- `vue/multi-word-component-names: off` —— 视图组件按约定单词命名（与路由名/keep-alive 对应）。
- `vue/no-reserved-component-names: off` —— 允许 `Menu` 等视图名。
- `vue/no-mutating-props: [error, { shallowOnly: true }]` —— 允许 `SearchForm` 对父级传入 model 对象的**嵌套属性**就地赋值（设计如此），仍拦截整体重新赋值。
- `no-unused-vars: [error, { args: none }]` —— 忽略 `.then(response => …)` 等未用回调参数。

## 验证结果

1. `npm run build` —— **0 error** 通过。
2. `npm run lint` —— **0 error** 通过。
3. `npm run dev` —— 编译通过；Playwright 加载登录页：Element Plus 组件/图标正常渲染、路由守卫重定向到 `/login`、控制台 **0 个 Vue 告警**（仅有后端未启动导致的 `/api/captcha`、`/api/sso/providers` 500 网络错误）。

### 待人工补充的端到端冒烟（需后端 + MySQL + Redis 启动）

后端在本机未运行，以下需在后端可用后手动过一遍：

- 登录（含验证码）→ 动态路由生成不白屏。
- `system/user`：列表搜索/排序/分页、新增/编辑弹窗（`v-model` 弹窗 + 表格列插槽 + `$confirm` 删除确认）。
- `system/menu` / `system/dept`：`el-tree-select` 树选择 + 图标选择器选中入库。
- `system/notice`：`@vueup/vue-quill` 富文本编辑与回显。
- `statistics` / 首页：echarts 渲染。
- 主题抽屉、折叠侧边栏、退出登录。

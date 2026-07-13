# README 动态登录宠物更新 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 更新 README，使新装和升级用户都能发现动态登录宠物功能、权限边界和正确的数据库脚本。

**Architecture:** 仅调整根目录 `README.md` 的现有“功能模块”“快速开始”“项目结构”“配置说明”四处内容，不新增图片或发布日志。新装继续使用 `database/init.sql`，既有实例明确使用幂等升级脚本。

**Tech Stack:** GitHub Flavored Markdown、Git。

## Global Constraints

- 中文说明与现有 README 风格一致。
- 不修改与本功能无关的端口、SSO、环境或技术栈内容。
- 不修改或提交用户已有的 `application.yml`、`frontend/vite.config.ts`、`.git-rewrite/`。
- 推送目标固定为 GitHub remote `github` 的 `master` 分支，不推送 Gitee remote `origin`。

---

### Task 1: 更新 README 并推送 GitHub

**Files:**
- Modify: `README.md`

**Interfaces:**
- Consumes: `database/init.sql`、`database/upgrade/20260713_add_login_pet_config.sql`、权限 `system:config:edit`。
- Produces: 面向新装与升级用户的功能和部署说明。

- [ ] **Step 1: 在功能模块增加动态登录宠物条目**

在“个人中心”条目后增加：

```markdown
- **动态登录宠物** - 登录页支持猫咪、小狗、猫头鹰三种 108px 内联 SVG 宠物，默认猫咪；眼睛、头部和专属动作会跟随鼠标方向，并适配减少动态效果偏好
```

- [ ] **Step 2: 补充既有数据库升级命令**

在 `database/init.sql` 新装命令后增加：

````markdown
已有数据库升级时不要重新执行初始化脚本，请改为执行幂等升级 SQL：

```bash
mysql -u root -p admin_system < database/upgrade/20260713_add_login_pet_config.sql
```
````

- [ ] **Step 3: 更新数据库目录结构说明**

将数据库结构行展开为：

```text
└── database/                     # 数据库脚本
    ├── init.sql                  # 新装：完整表结构与种子数据
    └── upgrade/                  # 既有数据库：按日期执行的幂等升级脚本
```

- [ ] **Step 4: 增加登录页宠物配置说明**

在 Redis 配置后增加：

```markdown
### 登录页宠物配置

- 全局配置键：`sys.login.pet.type`
- 允许值：`cat`、`dog`、`owl`
- 默认值：`cat`（猫咪）
- 具有 `system:config:edit` 权限或超级管理员权限的用户，可在右上角 **主题设置** 抽屉中切换；登录页读取失败时会静默回退猫咪。
```

- [ ] **Step 5: 验证 README 内容与提交隔离**

Run:

```powershell
rg -n "动态登录宠物|20260713_add_login_pet_config|sys.login.pet.type|system:config:edit|database/upgrade" README.md
git diff --check
git status --short
```

Expected: 五个关键内容均命中；无 Markdown 空白错误；除 README 外仅保留用户原有三个工作区差异。

- [ ] **Step 6: 提交 README**

```powershell
git add -- README.md
git commit -m "docs: document dynamic login pets"
```

- [ ] **Step 7: 推送 GitHub master**

```powershell
git push github master
```

Expected: 推送成功，`github/master` 与本地 `master` 指向同一提交；不触碰 remote `origin`。

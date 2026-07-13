# README 动态登录宠物文档更新设计

## 目标

在不扩写为发布日志、不新增截图资源的前提下，让 README 准确说明动态登录宠物功能、权限边界和数据库部署步骤，并保持现有章节结构与中文表达风格。

## 更新范围

1. 在“功能模块”增加一项动态登录宠物说明：支持猫咪、小狗、猫头鹰，默认猫咪，108px，并随鼠标方向响应。
2. 在“初始化数据库”区分新装与既有实例：新装继续执行 `database/init.sql`；旧库执行 `database/upgrade/20260713_add_login_pet_config.sql`。
3. 在“配置说明”增加“登录页宠物配置”小节，说明全局配置键 `sys.login.pet.type`、允许值 `cat|dog|owl`、默认值 `cat`，以及只有 `system:config:edit` 或超级管理员可在右上角“主题设置”抽屉修改。
4. 在“项目结构”中标注 `database/init.sql` 与 `database/upgrade/` 的不同职责。

## 明确不做

- 不新增截图、GIF、徽章或外部图片。
- 不增加版本更新日志或重复实现细节。
- 不修改 README 中与本功能无关的端口、环境、SSO 或技术栈说明。
- 不修改或提交用户工作区中的 `application.yml`、`frontend/vite.config.ts`、`.git-rewrite/`。

## 验证与发布

- 检查目录锚点、Markdown 代码块、文件路径和权限字符串。
- 运行 `git diff --check` 并确认提交范围仅包含本设计说明与 README。
- 单独提交 README 更新，然后将当前 `master` 推送到 GitHub remote `github`。

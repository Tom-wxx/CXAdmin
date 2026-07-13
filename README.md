# CXAdmin - 企业级后台管理系统

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?logo=vuedotjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-strict-3178C6?logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-6-646CFF?logo=vite&logoColor=white)
![Element Plus](https://img.shields.io/badge/Element%20Plus-2.14-409EFF)
![License](https://img.shields.io/badge/License-MIT-blue)

基于 **Spring Boot 4 + Vue 3（TypeScript）** 的企业级后台管理系统，采用前后端分离架构，内置 RBAC 权限管理、JWT-in-Redis 认证、动态路由、OAuth2/OIDC 单点登录、Quartz 调度与系统监控等功能，开箱即用。前端全量 Composition API + 严格类型，后端为多模块 Maven 工程。

## 目录

- [技术栈](#技术栈)
- [功能模块](#功能模块)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [配置说明](#配置说明)
- [API 文档](#api-文档)

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 4.1.0（Spring Framework 7 / Jakarta EE 11 / Jackson 3） |
| 安全认证 | Spring Security 7 + JWT（jjwt 0.12）+ Redis |
| ORM | MyBatis Plus 3.5.15 |
| 数据库 | MySQL 8.0 |
| 连接池 / 监控 | Druid 1.2.28 |
| 缓存 | Redis |
| API 文档 | springdoc-openapi 3.0（OpenAPI 3.1） |
| 前端框架 | Vue 3.5（Composition API，全量 `<script setup lang="ts">`） |
| UI 组件 | Element Plus 2.14 |
| 路由 / 状态管理 | Vue Router 4 / Vuex 4 |
| 前端语言 / 构建 | TypeScript（strict） / Vite 6 |
| 后端构建 | Maven 多模块（`admin-common` ← `admin-system` ← `admin-framework` ← `admin-boot`） |

## 功能模块

- **用户管理** - 用户增删改查、角色分配、部门归属
- **角色管理** - 角色权限配置、数据权限、菜单分配
- **菜单管理** - 动态菜单路由、按钮级权限控制
- **部门管理** - 树形组织架构管理
- **岗位管理** - 用户岗位配置
- **字典管理** - 系统数据字典维护
- **参数配置** - 系统运行参数动态配置
- **通知公告** - 系统通知发布与管理
- **操作日志** - 系统操作行为记录
- **登录日志** - 用户登录行为追踪
- **在线用户** - 实时在线用户监控与强退
- **定时任务** - Quartz 定时任务动态管理
- **服务监控** - 服务器运行状态监控
- **代码生成** - 根据数据库表一键生成前后端代码
- **文件管理** - 文件上传与管理
- **消息中心** - 站内消息与通知模板
- **缓存监控** - Redis 缓存状态监控
- **数据统计** - 系统数据可视化统计
- **单点登录 (SSO)** - 通用 OAuth2 / OIDC 适配器：动态配置 IdP（GitHub / Google 等），首次登录自动注册并绑定，已登录用户可从个人中心管理第三方账号绑定。client_secret AES-GCM 加密入库
- **个人中心** - 查看 / 修改个人信息、修改密码、头像上传、SSO 账号绑定管理
- **动态登录宠物** - 登录页支持猫咪、小狗、猫头鹰三种 108px 内联 SVG 宠物，默认猫咪；眼睛、头部和专属动作会跟随鼠标方向，并适配减少动态效果偏好

## 快速开始

### 环境要求

- JDK 17+（Spring Boot 4 要求 17+）
- Maven 3.6+
- Node.js 18+（Vite 6 / TypeScript；建议 20.19+ 以便未来升级 Vite 7+）
- MySQL 8.0
- Redis

### 1. 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE admin_system DEFAULT CHARACTER SET utf8mb4;

-- 导入数据（单文件聚合所有表 + 种子数据，按依赖顺序执行）
mysql -u root -p admin_system < database/init.sql
```

已有数据库升级时不要重新执行初始化脚本，请改为执行幂等升级 SQL：

```bash
mysql -u root -p admin_system < database/upgrade/20260713_add_login_pet_config.sql
```

### 2. 启动后端

后端为多模块 Maven 工程，只有 `admin-boot` 是可执行 jar。从聚合根构建依赖并运行：

```bash
cd backend
mvn -pl admin-boot -am spring-boot:run
# 打包：mvn clean package -DskipTests  →  backend/admin-boot/target/admin-system.jar
```

后端启动地址：http://localhost:8080/api

### 3. 启动前端

```bash
cd frontend
npm install            # 如遇 peer 冲突：npm install --legacy-peer-deps
npm run dev            # Vite 开发服务器
# 可选：npm run type-check（vue-tsc 严格类型检查）、npm run build（vite 构建）
```

前端访问地址：http://localhost:8081

### 4. 登录系统

- 账号：`admin`
- 密码：`admin123`

> 登录页底部「第三方登录」按钮支持通过已配置的 IdP 进行 SSO 登录。
> 管理 IdP 配置：登录后进入 **系统管理 → 身份认证源**。

## 项目结构

```
CXAdmin/
├── backend/                      # 后端：多模块 Maven 工程（聚合父 pom，依赖 common ← system ← framework ← boot）
│   ├── admin-common/             # com.admin.common —— Result / BaseEntity / 工具类 / 安全基类（无内部依赖）
│   ├── admin-system/             # com.admin.system —— 领域层：controller/service/mapper/entity/dto/vo、SSO、Quartz、代码生成
│   ├── admin-framework/          # com.admin.framework —— 配置 / 安全 / AOP 切面 / 全局异常处理
│   └── admin-boot/               # com.admin —— 唯一可执行 jar（AdminApplication + application.yml）
├── frontend/                     # 前端：Vue 3 + TypeScript + Vite
│   └── src/
│       ├── api/                  # 接口请求（.ts，按 Result<T> / TableResponse<T> 类型化）
│       ├── types/                # 领域 / API / store 类型定义
│       ├── composables/          # 组合式逻辑（useCrudTable / useDict / useECharts / 类型化 store）
│       ├── views/                # 页面组件（<script setup lang="ts">）
│       ├── components/           # 公共组件
│       ├── router/ store/ utils/ # 路由 / Vuex / 工具
│       └── main.ts               # 入口
└── database/                     # 数据库脚本
    ├── init.sql                  # 新装：完整表结构与种子数据
    └── upgrade/                  # 既有数据库：按日期执行的幂等升级脚本
```

## 配置说明

### 数据库配置

编辑 `backend/admin-boot/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/admin_system
    username: root
    password: root
```

### Redis 配置

```yaml
spring:
  data:
    redis:           # Spring Boot 4 配置前缀为 spring.data.redis
      host: localhost
      port: 6379
      password:      # 默认无密码
```

### 登录页宠物配置

- 全局配置键：`sys.login.pet.type`
- 允许值：`cat`、`dog`、`owl`
- 默认值：`cat`（猫咪）
- 具有 `system:config:edit` 权限或超级管理员权限的用户，可在右上角 **主题设置** 抽屉中切换；登录页读取失败时会静默回退猫咪。

### 单点登录（SSO）配置

```yaml
admin:
  sso:
    # AES-256 主密钥（32 字节 Base64），用于加密 IdP 的 client_secret 入库
    # 生产环境必须通过环境变量 ADMIN_SSO_CRYPTO_KEY 覆盖，不要使用 yml 默认值
    crypto-key: ${ADMIN_SSO_CRYPTO_KEY:<your-32-byte-base64-key>}
    # IdP 回调地址（dev 走前端 8081，cookie 才会落在前端 origin）
    callback-base-url: http://localhost:8081/api
    # 前端基础地址：SSO 登录成功后跳 {front}/index
    front-base-url: http://localhost:8081
    # 出站代理（境内访问 github.com 等 IdP 时可走本地 Clash / v2rayN）
    proxy:
      host: ${ADMIN_SSO_PROXY_HOST:127.0.0.1}
      port: ${ADMIN_SSO_PROXY_PORT:7890}
```

IdP（GitHub / Google 等）的 Client ID / Secret、授权 / Token / UserInfo 端点、字段映射等参数**不写在 yml**，全部在管理后台 **系统管理 → 身份认证源** 动态配置，无需重启即生效。

GitHub OAuth App 的 Authorization callback URL 填：`http://localhost:8081/api/sso/callback/github`。

## API 文档

启动后端后访问 Swagger 文档：http://localhost:8080/api/swagger-ui/index.html

## License

MIT

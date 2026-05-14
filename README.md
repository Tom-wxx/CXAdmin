# CXAdmin - 企业级后台管理系统

基于 **Spring Boot + Vue** 的企业级后台管理系统，采用前后端分离架构，内置 RBAC 权限管理、JWT 认证、动态路由、系统监控等功能，开箱即用。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.7.18 |
| 安全认证 | Spring Security + JWT + Redis |
| ORM | MyBatis Plus 3.5.3 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis |
| 前端框架 | Vue 2.6.14 |
| UI 组件 | Element UI 2.15.13 |
| 状态管理 | Vuex 3.6.2 |
| 构建工具 | Maven / Webpack |

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

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 14+
- MySQL 8.0
- Redis

### 1. 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE admin_system DEFAULT CHARACTER SET utf8mb4;

-- 导入数据（单文件聚合所有表 + 种子数据，按依赖顺序执行）
mysql -u root -p admin_system < database/init.sql
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动地址：http://localhost:8080/api

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
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
├── backend/                # 后端 Spring Boot 项目
│   ├── src/main/java/      # Java 源码
│   │   └── com/admin/system/
│   │       ├── common/     # 公共模块（Result、BaseEntity、异常处理）
│   │       ├── config/     # 配置类（Security、Redis、MyBatis、CORS）
│   │       ├── security/   # 安全认证（JWT 过滤器、用户认证）
│   │       ├── controller/ # REST 接口层
│   │       ├── service/    # 业务逻辑层
│   │       ├── mapper/     # 数据访问层
│   │       ├── entity/     # 数据实体
│   │       ├── dto/        # 请求参数对象
│   │       └── vo/         # 响应视图对象
│   └── src/main/resources/ # 配置文件与 Mapper XML
├── frontend/               # 前端 Vue 项目
│   └── src/
│       ├── api/            # 接口请求
│       ├── views/          # 页面组件
│       ├── components/     # 公共组件
│       ├── router/         # 路由配置
│       ├── store/          # 状态管理
│       └── utils/          # 工具函数
└── database/               # 数据库脚本
```

## 配置说明

### 数据库配置

编辑 `backend/src/main/resources/application.yml`：

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
  redis:
    host: localhost
    port: 6379
    password:        # 默认无密码
```

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

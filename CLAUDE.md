# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Enterprise admin system: Spring Boot 2.7.18 backend + Vue 2.6.14 frontend, with RBAC, JWT-in-Redis authentication, Quartz scheduling, and OAuth2/OIDC single sign-on.

> **`AGENTS.md` is aspirational, not authoritative.** It describes a multi-module Maven layout (`admin-common` / `admin-framework` / ...) that does not exist — `backend/pom.xml` is a single-module jar (`com.admin:admin-system:1.0.0`).

## Tech Stack

- **Backend:** Java 17, Spring Boot 2.7.18, Spring Security + JWT, MyBatis Plus 3.5.3.1, MySQL 8, Redis, Druid, Quartz, Swagger 3, Kaptcha
- **Frontend:** Vue 2.6.14, Element UI 2.15.13, Vue Router 3 (dynamic routes), Vuex 3, Axios

## Common Commands

```bash
# Helper scripts (project root, Windows)
check-environment.bat        # verify JDK/Maven/Node/MySQL/Redis
check-services.bat           # probe ports 8080/8081/3306/6379

# Backend (run from backend/)
mvn spring-boot:run
mvn test
mvn test -Dtest=ClassName              # single class
mvn test -Dtest=ClassName#method       # single method
mvn clean package -DskipTests

# Frontend (run from frontend/)
npm install                            # use --legacy-peer-deps if conflicts
npm run dev                            # http://localhost:8081
npm run build
npm run lint

# Database (single consolidated script, 11 labeled sections)
mysql -u root -p -e "CREATE DATABASE admin_system DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p admin_system < database/init.sql
```

**Endpoints:** Backend `http://localhost:8080/api` · Swagger `/api/swagger-ui/index.html` · Druid `/api/druid/` (admin/admin) · Frontend `http://localhost:8081`

**Defaults:** DB `root/root`, app login `admin/admin123`, Redis no password.

## Backend Package Structure

```
com.admin.system/
├── annotation/    + aspect/      # @Log / @DataScope and their AOP backings
├── common/                       # BaseEntity, Result, PageResult, GlobalExceptionHandler
│   ├── constants/                # SystemConstants
│   └── exception/                # ServiceException
├── config/                       # SecurityConfig, JwtProperties, RedisConfig, MyBatisPlusConfig,
│                                 # SwaggerConfig, CorsConfig, PermissionService (the @ss bean)
├── security/                     # JwtAuthenticationFilter, LoginUser, UserDetailsServiceImpl, SecurityUtils
├── controller/ service/ mapper/  # standard layered CRUD
├── entity/ dto/ vo/              # MyBatis Plus entities (@TableName), request DTOs, response VOs
├── quartz/ + task/               # AbstractQuartzJob + concurrency markers; concrete task beans
├── monitor/                      # server/cache/online-user monitoring (Oshi)
├── generator/                    # MyBatis Plus code generator (Velocity templates)
├── sso/                          # see "SSO Module" section
└── utils/                        # JwtUtil, RedisUtil, CaptchaUtil, PageUtils, IpUtil, ExcelUtil,
                                  # FileUtil, MailService, SpringUtils
```

(The legacy `util/` package referenced in older docs has been removed — there is only `utils/`.)

## Authentication & Authorization

**Login → JWT → Redis flow:**
1. `POST /login` with credentials + CAPTCHA → BCrypt verify → mint JWT → store `LoginUser` in Redis keyed by token → return token.
2. `JwtAuthenticationFilter` runs on every request: reads `Authorization: Bearer <token>`, looks up `LoginUser` in Redis, **auto-extends TTL when ≤20 min remain**, sets Spring Security context.
3. Session policy is `STATELESS` — no server-side HttpSession; all user state lives in Redis under the JWT key.

**Permission checks:**
- Backend: `@PreAuthorize("@ss.hasPermi('system:user:add')")` on controller methods (`@ss` = `PermissionService` bean in `config/`).
- Frontend: `v-hasPermi="['system:user:add']"` directive; routes are generated dynamically from the user's menu tree returned by `getRouters`.
- Whitelisted endpoints (no auth): `/login`, `/captcha`, Swagger, and `/sso/providers` + `/sso/authorize/**` + `/sso/callback/**`.

## SSO Module (OAuth2 / OIDC)

The system is a Service Provider that delegates auth to externally configured IdPs. The design spans many files — read this section before touching `sso/`:

- **Providers are DB-driven, not yaml-driven.** All IdPs (GitHub, Google, …) are rows in `sys_sso_provider`. Add/edit/disable in the admin UI; no restart needed.
- **`client_secret` is AES/GCM-encrypted at rest.** Key comes from `admin.sso.crypto-key` in `application.yml` (do **not** commit a real key). `SsoCryptoUtil` does encrypt/decrypt; `SysSsoProviderServiceImpl` invokes it on read/write. **Rotating the key orphans every existing secret** — you must re-encrypt rows first.
- **Strategy pattern.** `SsoStrategy` interface + `GenericOAuth2Strategy` covers OAuth2 Authorization Code and OIDC userinfo flows. New protocol families (DingTalk / SAML / CAS) = new strategy + register in `SsoStrategyFactory`; never modify `GenericOAuth2Strategy`.
- **PKCE (S256) is always on** for code flow; `code_verifier` is stored in Redis under the state key and replayed on callback.
- **State/nonce in Redis** (`SsoStateData`) — opaque token issued at `/sso/authorize`, consumed once at `/sso/callback`. Prevents CSRF and replay.
- **First-login auto-provisioning.** On callback success the user is created in `sys_user` (if not bound), assigned the provider's `default_role_id`, and recorded in `sys_user_sso_binding` (many bindings per user).
- **Audit log.** Every authorize/callback/bind/unbind event is written to `sys_sso_login_log` by `SsoAuditLogServiceImpl`; surfaced via `SysSsoLogController`.
- **Frontend:** `views/sso/callback.vue` exchanges code+state with the backend then drops the JWT into the cookie. The login page fetches `/sso/providers` to render IdP buttons.
- **Design source-of-truth:** `SSO_PLAN.md` at the repo root.

## Database Conventions

- `database/init.sql` is the single consolidated init script (11 `-- Section:` headers — schema, RBAC seed, dictionaries, configs, scheduled jobs, notifications, SSO, etc.). Earlier `initData.sql` / `add_*.sql` files have been removed.
- Core RBAC tables: `sys_user`, `sys_role`, `sys_menu` (tree), `sys_dept` (tree), `sys_post`, plus join tables `sys_user_role` / `sys_role_menu` / `sys_role_dept` (`sys_role_dept` = data-scope filter, not a permission).
- Operational tables: `sys_dict_type`/`sys_dict_data`, `sys_config`, `sys_oper_log`, `sys_login_log`, `sys_notice`, `sys_job`/`sys_job_log`.
- All entities extend `BaseEntity` (`createBy`, `createTime`, `updateBy`, `updateTime`, `deleted`). `deleted=0` active, `deleted=1` logically removed — every query goes through MyBatis Plus's logical-delete handling automatically.
- PK strategy: `IdType.AUTO`. Mapper XMLs live in `backend/src/main/resources/mapper/`.

## Response & API Conventions

Every controller returns `Result<T>` (or `PageResult<T>` for lists):

```java
return Result.success(data);
return Result.fail(ResultCode.UNAUTHORIZED);
```

Response shape: `{ code, message, data, timestamp }`. Frontend `utils/request.js` (Axios interceptor) unwraps this and centrally handles 401/403.

Frontend API modules live in `frontend/src/api/<feature>/*.js` — one file per backend controller, named exports per endpoint.

## Quartz Scheduled Jobs

- `sys_job` holds cron config (target bean, method, params); `sys_job_log` holds execution history.
- Concrete task beans live in `task/`, invoked by reflection from `sys_job.invoke_target`.
- Concurrency: extend `AbstractQuartzJob`; pick `QuartzJobExecution` (concurrent OK) vs. `QuartzDisallowConcurrentExecution` (serialize).
- Jobs are managed dynamically via the UI (pause/resume/run-once) — no code change needed to add a cron.

## Adding a New Feature Module

The cleanest reference is an existing module (e.g. `system/user` end-to-end). The flow is:

1. **DB:** new table extending audit columns (`create_by/create_time/update_by/update_time/deleted/remark`). Seed `sys_menu` rows for the directory, list page, and per-action button permissions (`module:resource:action`). Bind to roles via `sys_role_menu`.
2. **Backend:** entity (`@TableName`, extends `BaseEntity`) → mapper (`extends BaseMapper<T>`) → service interface (`extends IService<T>`) → impl (`extends ServiceImpl<Mapper, T>`) → controller (`@PreAuthorize("@ss.hasPermi(...)")` on every method).
3. **Frontend:** `src/api/<module>/<feature>.js` (one named export per endpoint) → `src/views/<module>/<feature>/index.vue` (mirror an existing list+dialog page).

## Important Quirks

- **`jjwt 0.9.1` + JDK 17.** jjwt 0.9.1 references `javax.xml.bind.DatatypeConverter`, which was removed in Java 11. The pom keeps `jaxb-api 2.3.1` as a runtime shim — **do not delete it**. Upgrading jjwt to 0.11.x requires rewriting `JwtUtil` (parser API + `signWith` signature changed).
- **CORS:** dev relies on `vue.config.js` proxy (`/api` → `localhost:8080`); prod uses `CorsConfig` on the backend. CSRF is disabled (stateless JWT).
- **Logging:** colorized console + file `backend/logs/admin-system.log`; SQL logging is on via MyBatis Plus.
- **`.env.development`** sets `VUE_APP_BASE_API=/api` (proxied); production env file must point at the real backend URL.

## Troubleshooting (non-obvious only)

- **Stale `admin` BCrypt hash → login fails with correct password.** Re-apply the seed section of `database/init.sql`, or replace the hash directly with `BCrypt(admin123)`.
- **403 on a known-good endpoint.** Check the `@PreAuthorize` permission string actually exists in `sys_menu.perms` and is bound to the caller's role via `sys_role_menu`.
- **White screen after login.** Almost always means `getRouters` returned no menus — check role/menu bindings; browser console will show route-generation errors.
- **SSO callback "invalid state".** The Redis state entry expired or was consumed twice (often a browser-back replay). Restart the auth flow from `/sso/authorize`.
- **Port already in use (8080 / 8081):** `netstat -ano | findstr :<port>` then `taskkill /PID <pid> /F`.

## Testing

- `backend/src/test/java/com/admin/` exists but is **empty** — no tests currently. JUnit 5 + Spring Boot Test is on the pom; mirror production package paths when adding tests, and prioritize smoke tests around the security layer when modifying auth.
- No frontend test automation — UI changes need manual smoke (login, list page search/sort/pagination, form validation, logout).

## User Instructions (from global config)

- 中文解释 — explain in Chinese when responding to the user.
- TODO / PLAN files belong in the project root directory.

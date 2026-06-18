# Spring Boot 2.7.18 → 4.1.0 升级记录

> 分支：`feature/spring-boot-4`（从含 Vue3 迁移未提交改动的工作树切出）。
> 范围：仅后端 `backend/`，Java 保持 17。前端 Vue 2 不变。
> 状态：✅ 已完成并端到端验证通过（编译 / 155 单测 / 打包 / 启动 / 登录全链路）。

## 目标版本

| 组件 | 旧 | 新 |
|---|---|---|
| Spring Boot | 2.7.18 | **4.1.0**（Spring Framework 7.0、Spring Security 7.1） |
| Jackson | 2.x | **3.1.4**（`tools.jackson`） |
| MyBatis Plus | 3.5.3.1 (`mybatis-plus-boot-starter`) | **3.5.15**（`mybatis-plus-spring-boot4-starter` + `mybatis-plus-jsqlparser`） |
| Druid | 1.2.16 (`druid-spring-boot-starter`) | **1.2.28**（`druid-spring-boot-4-starter`） |
| API 文档 | springfox 3.0.0 | **springdoc-openapi 3.0.3**（OpenAPI 3.1） |
| JWT | jjwt 0.9.1（单包 + jaxb-api shim） | **jjwt 0.12.6**（api/impl/jackson，移除 jaxb-api） |
| MySQL 驱动 | `mysql:mysql-connector-java` | `com.mysql:mysql-connector-j`（parent 管理） |
| spring-data-redis | 2.7.x | **4.1.0** |

## 实际执行的改动（含与原计划的偏差）

1. **pom.xml**：parent 升 4.1.0；starter 重命名 `web→webmvc`、`aop→aspectj`（Boot 4 改名）；MyBatis Plus 换 boot4 starter 并**新增 `mybatis-plus-jsqlparser`**（3.5.9+ 把 `PaginationInnerInterceptor` 拆出去了）；Druid 换 **官方 `druid-spring-boot-4-starter` 1.2.28**（计划里担心的"无 Boot4 starter"已被官方解决，无需手动配置）；jjwt 拆三包并删 jaxb-api；springfox→springdoc；删除未使用的 `kaptcha`、`pagehelper`。
2. **javax → jakarta**：48 个文件批量替换 `servlet/validation/annotation/mail`（保留 `crypto/sql/imageio`）。
3. **SecurityConfig**：`WebSecurityConfigurerAdapter` → `SecurityFilterChain` Bean + Lambda DSL；`@EnableGlobalMethodSecurity`→`@EnableMethodSecurity`；`AuthenticationManager` 由 `AuthenticationConfiguration` 暴露；白名单 springdoc 化。
4. **JwtUtil**：jjwt 0.12 新 API（`claims/subject/signWith(key)` + `parser().verifyWith().build().parseSignedClaims()`），`application.yml` 的 `jwt.secret` 加长到 64 字节（HS512 强度要求）。
5. **Springfox 注解 → springdoc**：28 个文件、约 360 处（`@Api→@Tag`、`@ApiOperation→@Operation`、`@ApiParam→@Parameter`、`@ApiModel(Property)→@Schema`）；SwaggerConfig 改为 `OpenAPI` Bean。
6. **RedisConfig（Jackson 3，最关键）**：`Jackson2JsonRedisSerializer` + 手动 ObjectMapper → **`GenericJacksonJsonRedisSerializer.builder().enableUnsafeDefaultTyping().build()`**。⚠️ 默认 builder 不写 `@class`，会导致 LoginUser 反序列化成 `LinkedHashMap` 强转失败（运行时登录鉴权 403）——必须显式 `enableUnsafeDefaultTyping()`（等价旧的 `LaissezFaireSubTypeValidator + NON_FINAL`）。
7. **application.yml**：`spring.redis`→`spring.data.redis`；删 `spring.mvc.pathmatch`、`pagehelper`、`swagger`；删 `spring.jackson.serialization.write-dates-as-timestamps`（Jackson 3 已移出 `SerializationFeature`，默认即 ISO）；移除 `@EnableCaching`（项目未用 Spring Cache 抽象，Boot 4 下会缺 CacheManager 启动失败）。
8. **测试**：MP 3.5.15 给 `insert/updateById` 加了 `Collection<T>` 重载，导致 `argThat(lambda)` 类型推断歧义 → 4 个测试文件的 lambda 显式标注实体类型。

## 验证结果

- `mvn clean package`：**BUILD SUCCESS**，155 个单测全过（含 JwtUtil/JwtAuthenticationFilter/LoginService/SecurityUtils）。
- 应用以 Boot 4.1.0 启动成功（14s）。`/actuator/health`：**db UP、redis UP**。
- springdoc `/v3/api-docs`(OpenAPI 3.1.0)、`/swagger-ui`、Druid `/druid`、`/captcha` 均 200。
- **完整登录链路 200**：`/login`(签发 HS512 token) → `/getInfo`(带 token 返回权限) → `/system/user/list`(MP 分页+逻辑删除+Jackson3 日期格式) → `/getRouters`(菜单树) → `/logout` 后复用 token 403（失效正确）。

## 遗留事项（非本次回归，需关注）

- **`/actuator/health` 整体 503**：唯一 DOWN 的是 `mail`（SMTP 用占位假凭证 `your-auth-code` → `jakarta.mail.AuthenticationFailedException`）。这是既有配置问题。配置真实 SMTP 凭证即恢复；或在部署时设 `management.health.mail.enabled=false` 避免假凭证拖垮健康检查/就绪探针。
- **CLAUDE.md 版本信息已过时**（仍写 Spring Boot 2.7.18、jjwt 0.9.1 + "勿删 jaxb-api"、Swagger=springfox 等），建议同步更新；该文件当前已有他人未提交改动，未代为修改。
- **前端**：本次未动；如需，Vue 2 → Vue 3 + Element Plus 是独立迁移。
- 生产部署仍需注意 CLAUDE.md 既有提醒：清空 `admin.sso.proxy.host`。

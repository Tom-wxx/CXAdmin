# Repository Guidelines

## Project Structure & Module Organization
- Root `pom.xml` manages multi-module Maven: `admin-common` (shared utils/exceptions), `admin-framework` (boot/security/config glue), `admin-system` (core domain: user/role/menu/quartz), `admin-workflow` (workflow), `admin-admin` (admin UI glue).
- `backend/`: runnable Spring Boot service (`com.admin.system.AdminSystemApplication`), configs in `src/main/resources`, MyBatis XML under `resources/mapper`, templates in `resources/templates`, tests in `src/test/java`.
- `frontend/`: Vue 2 + Element UI app; views in `src/views`, API clients `src/api`, shared UI `src/components`, routing/store under `src/router` and `src/store`; env files `.env.development/.env.production`.
- `database/`: schema + seed SQL (`initData.sql` first, then incremental `add_*.sql`). Generated output (`target/`, `dist/`, `logs/`) stays uncommitted.

## Build, Test, and Development Commands
- `mvn clean install` (root): compile + test all modules.
- `cd backend && mvn spring-boot:run`: start API locally (banner shows `http://localhost:8080/api`).
- `cd backend && mvn clean package`: build runnable JAR in `backend/target`.
- `cd frontend && npm install` then `npm run dev` for hot reload; `npm run build` for production bundle; `npm run lint` for Vue/JS style.
- `mvn test` (root or backend) runs JUnit 5 suite.

## Coding Style & Naming Conventions
- Java 8, 4-space indent; package prefix `com.admin`; controllers end with `Controller`, services `Service`, persistence `Mapper`; keep DTO/VO/domain folders aligned with packages.
- Prefer Lombok for boilerplate; use `@Slf4j` for logging; REST paths lower-hyphen with clear verbs, returning unified response objects already in common modules.
- Vue SFCs; `views/` filenames kebab-case; APIs camelCase; colocate SCSS with components; run ESLint before commit.

## Testing Guidelines
- Backend: Spring Boot Starter Test + JUnit 5; mirror package paths in `backend/src/test/java`; mock external calls and assert security/validation.
- Cover touched utilities in `admin-common`; smoke-test auth/config when changing security.
- No formal front-end automation yet; manual verification still required.

## Frontend Testing & QA
- Minimum manual checklist: login, navigation, table search/sort/pagination, form validation errors/success, logout.
- For interactive/new views, prefer adding tests (e.g., Vitest unit or Cypress e2e under a `frontend/tests` folder); document commands/results in PR.
- Keep `.env.*` endpoints updated for local vs. prod to avoid hardcoded URLs during testing.

## Commit & Pull Request Guidelines
- History is terse (e.g., `INIT`); keep messages short/imperative. Recommended: `type(scope): summary` or `module: summary` (e.g., `feat(workflow): add pause job api`).
- Squash noisy WIP; PR description should state what/why and test commands run.
- Link issues when available; add screenshots/GIFs for UI changes and note any config/SQL touched.
- Ensure lint/tests pass before PR; exclude generated assets/logs.

## Security & Configuration Tips
- Do not commit secrets; keep overrides in local `application-local.yml` or env vars. Front-end base API lives in `.env.*`, not components.
- Database changes append new SQL in `database/`; apply in order, avoid editing applied files; note rollback path if added.
- Review `logback-spring.xml` before enabling debug in shared environments to avoid sensitive output.

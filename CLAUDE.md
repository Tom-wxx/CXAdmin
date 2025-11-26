# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Enterprise-level admin management system built with Spring Boot 2.7.18 backend and Vue 2.6.14 frontend, featuring RBAC (Role-Based Access Control), JWT authentication, and comprehensive system monitoring capabilities.

## Technology Stack

**Backend:**
- Java 8 with Spring Boot 2.7.18
- Spring Security + JWT (stateless authentication with Redis)
- MyBatis Plus 3.5.3.1 for ORM
- MySQL 8.0 database
- Redis for caching and token storage
- Druid connection pool
- Swagger 3.0 for API documentation
- Kaptcha for CAPTCHA generation

**Frontend:**
- Vue 2.6.14
- Element UI 2.15.13 component library
- Vue Router 3.5.1 with dynamic route generation
- Vuex 3.6.2 for state management
- Axios 0.27.2 for HTTP requests

## Common Development Commands

### Backend (Spring Boot)

```bash
# Navigate to backend directory
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Run specific test class
mvn test -Dtest=YourTestClassName

# Run specific test method
mvn test -Dtest=YourTestClassName#testMethodName

# Package for production
mvn clean package -DskipTests

# Compile only (faster than install)
mvn clean compile
```

**Windows-specific commands:**
```cmd
# Check if backend is running on port 8080
netstat -ano | findstr :8080

# Kill process using port 8080
taskkill /PID <process_id> /F
```

**Backend API:** http://localhost:8080/api
**Swagger Documentation:** http://localhost:8080/api/swagger-ui/index.html
**Druid Monitor:** http://localhost:8080/api/druid/ (admin/admin)

### Frontend (Vue)

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Lint code
npm run lint
```

**Windows-specific commands:**
```cmd
# Check if frontend is running on port 8081
netstat -ano | findstr :8081

# Kill process using port 8081
taskkill /PID <process_id> /F
```

**Frontend Dev Server:** http://localhost:8081

### Database Setup

```bash
# Login to MySQL
mysql -u root -p

# Execute database scripts (in order)
source database/schema.sql
source database/init-data.sql

# If login fails with default password, run:
source database/fix-password.sql
```

**Windows alternative:**
```cmd
mysql -u root -p < database\schema.sql
mysql -u root -p < database\init-data.sql
mysql -u root -p < database\fix-password.sql
```

**Database Credentials (application.yml):**
- Database: `admin_system`
- Username: `root`
- Password: `root` (change in `backend/src/main/resources/application.yml`)

**Default Application Login:**
- Username: `admin`
- Password: `admin123`

**Core Database Tables:**
- `sys_user` - User accounts
- `sys_role` - Roles
- `sys_menu` - Menus and permissions (tree structure)
- `sys_dept` - Departments (tree structure)
- `sys_post` - Posts/positions
- `sys_user_role` - User-role associations
- `sys_role_menu` - Role-menu/permission associations
- `sys_role_dept` - Role data scope (department access control)
- `sys_dict_type` / `sys_dict_data` - System dictionaries
- `sys_config` - System parameters
- `sys_oper_log` - Operation logs
- `sys_login_log` - Login logs
- `sys_notice` - System notices
- `sys_job` / `sys_job_log` - Scheduled jobs and execution logs

## Architecture Overview

### Backend Package Structure

```
com.admin.system/
├── common/              # Common utilities and base classes
│   ├── BaseEntity       # Base entity with common fields (createBy, createTime, updateBy, updateTime, deleted)
│   ├── Result           # Unified API response wrapper (code, message, data, timestamp)
│   ├── ResultCode       # Response code enums (SUCCESS, FAIL, UNAUTHORIZED, etc.)
│   ├── PageResult       # Paginated response wrapper (extends Result with pagination info)
│   ├── GlobalExceptionHandler  # Centralized exception handling (@RestControllerAdvice)
│   └── exception/       # Custom exception classes
│       └── ServiceException  # Business logic exceptions
├── config/              # Configuration classes
│   ├── SecurityConfig   # Spring Security configuration
│   ├── JwtProperties    # JWT settings from application.yml
│   ├── RedisConfig      # Redis configuration
│   ├── MyBatisPlusConfig # MyBatis Plus configuration
│   ├── SwaggerConfig    # Swagger API documentation config
│   ├── CorsConfig       # CORS policy settings
│   └── PermissionService # Custom permission validation (@ss bean)
├── security/            # Authentication & authorization
│   ├── JwtAuthenticationFilter  # JWT token validation filter
│   ├── LoginUser        # User principal with authorities
│   ├── UserDetailsServiceImpl   # Load user from database
│   └── SecurityUtils    # Security context utilities
├── controller/          # REST API endpoints
├── service/             # Business logic layer
│   └── impl/            # Service implementations
├── mapper/              # MyBatis Plus data access
├── entity/              # Database entities (with @TableName)
│   ├── SysUser          # User entity with roles and permissions
│   ├── SysRole          # Role entity
│   ├── SysMenu          # Menu/permission entity (tree structure)
│   ├── SysDept          # Department entity (tree structure)
│   ├── SysPost          # Post/position entity
│   ├── SysDictType      # Dictionary type entity
│   ├── SysDictData      # Dictionary data entity
│   ├── SysConfig        # System parameter entity
│   ├── SysOperLog       # Operation log entity
│   ├── SysLoginLog      # Login log entity
│   ├── SysNotice        # Notice/announcement entity
│   ├── SysJob           # Scheduled job entity (Quartz)
│   └── SysJobLog        # Job execution log entity
├── dto/                 # Data Transfer Objects (request)
│   └── PageQuery        # Base class for paginated queries
├── vo/                  # View Objects (response)
│   ├── RouterVo         # Router metadata for frontend
│   └── MetaVo           # Menu metadata
├── quartz/              # Quartz scheduler components
│   ├── AbstractQuartzJob  # Base class for scheduled jobs
│   ├── QuartzJobExecution # Allow concurrent job execution
│   └── QuartzDisallowConcurrentExecution # Disallow concurrent execution
├── util/                # Utility classes (note: both util/ and utils/ exist)
└── utils/               # Additional utility classes (JwtUtil, RedisUtil, CaptchaUtil, etc.)
```

### Authentication Flow

1. **Login Process:**
   - User submits credentials + CAPTCHA to `/login`
   - Backend validates credentials with BCrypt password encoder
   - JWT token generated and stored in Redis with user info
   - Token returned to client and stored in Cookie (`Admin-Token`)

2. **Request Authentication:**
   - `JwtAuthenticationFilter` intercepts all requests
   - Extracts token from `Authorization: Bearer <token>` header
   - Retrieves `LoginUser` from Redis using token as key
   - Auto-refreshes token if expiring within 20 minutes
   - Sets authentication in Spring Security context

3. **Authorization:**
   - Controllers use `@PreAuthorize` annotations for method-level security
   - Frontend uses `v-hasPermi` directive to conditionally render UI elements
   - Permissions stored in Redis with user session

### Frontend Architecture

```
src/
├── api/                 # API service modules (organized by feature)
├── assets/              # Static resources (images, icons)
├── components/          # Reusable Vue components
├── layout/              # Application layout components
├── router/              # Vue Router configuration
│   └── index.js         # Route definitions with meta (roles, permissions)
├── store/               # Vuex state management
│   └── modules/
│       ├── user.js      # User authentication state
│       └── permission.js # Dynamic route generation
├── styles/              # Global styles and theme
├── utils/               # Utility functions
│   ├── auth.js          # Token management (Cookie operations)
│   └── request.js       # Axios instance with interceptors
├── views/               # Page components
├── App.vue              # Root component
├── main.js              # Application entry point
└── permission.js        # Global route guards (auth & permission checks)
```

### Dynamic Permission System

**Route Guard Flow (`permission.js`):**
1. Check if user has token
2. If no token → redirect to `/login`
3. If token exists but no user info → fetch user info and permissions
4. Generate accessible routes based on user roles
5. Dynamically add routes using `router.addRoutes()`

**Permission Storage:**
- Token stored in Cookie via `js-cookie`
- User info, roles, and permissions stored in Vuex store
- Route access controlled by route meta fields

## Key Configuration Files

### Backend Configuration

**application.yml** (`backend/src/main/resources/application.yml`):
- Server runs on port `8080` with context path `/api`
- Database connection: `jdbc:mysql://localhost:3306/admin_system`
- Redis connection: `localhost:6379` (no password by default)
- JWT settings: 30-minute token expiry, 7-day refresh token
- MyBatis Plus: XML mappers in `classpath*:mapper/**/*Mapper.xml`
- Logging: Colorized console output, file logs in `logs/admin-system.log`

### Frontend Configuration

**vue.config.js:**
- Dev server on port `8081`, listens on `0.0.0.0` (accessible on network)
- Proxy `/api` requests to backend `http://localhost:8080`
- Webpack alias: `@` points to `src/`
- Custom SVG icon loader for `src/assets/icons`

**.env.development:**
- `VUE_APP_BASE_API = '/api'` (uses proxy in development)

**.env.production:**
- Configure production API URL here

## Development Patterns

### Backend Response Format

All API responses use the `Result<T>` wrapper:

```java
// Success responses
return Result.success(data);
return Result.success("Custom message", data);

// Error responses
return Result.fail("Error message");
return Result.fail(ResultCode.UNAUTHORIZED);
```

Response structure:
```json
{
  "code": 200,
  "message": "Success",
  "data": { ... },
  "timestamp": 1699999999999
}
```

### Frontend API Calls

Use Axios with centralized error handling:

```javascript
import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}
```

### Permission Annotations

**Backend:**
```java
@PreAuthorize("@ss.hasPermi('system:user:add')")
public Result addUser(@RequestBody User user) { ... }
```

**Frontend:**
```vue
<el-button v-hasPermi="['system:user:add']">Add User</el-button>
```

## MyBatis Plus Conventions

- Entities extend `BaseEntity` for common fields (createTime, updateTime, etc.)
- Use `@TableName` to specify table names
- Logical delete: `deleted` field (0 = active, 1 = deleted)
- Primary key strategy: `AUTO` (database auto-increment)
- Mapper XML files in `backend/src/main/resources/mapper/`
- Service layer extends `IService<T>` interface (provides basic CRUD methods)
- Service implementations extend `ServiceImpl<Mapper, Entity>`

## Scheduled Jobs (Quartz)

The system includes Quartz integration for scheduled tasks:

- **SysJob entity**: Stores job configuration (job name, cron expression, target class/method)
- **SysJobLog entity**: Stores job execution history
- **Job execution**:
  - Extend `AbstractQuartzJob` to create custom job classes
  - Use `QuartzJobExecution` for jobs that can run concurrently
  - Use `QuartzDisallowConcurrentExecution` for jobs that must run sequentially
- Jobs are dynamically managed through the UI (start, stop, execute immediately)

## Adding New Modules/Features

When adding a new feature module, follow this workflow:

### 1. Database Layer
```sql
-- Create table extending standard audit fields
CREATE TABLE sys_example (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    -- your fields here
    create_by VARCHAR(64),
    create_time DATETIME,
    update_by VARCHAR(64),
    update_time DATETIME,
    deleted INT(1) DEFAULT 0,
    remark VARCHAR(500)
);
```

### 2. Backend Layer

Create files in this order:

**Entity** (`entity/SysExample.java`):
```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_example")
public class SysExample extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
}
```

**Mapper** (`mapper/SysExampleMapper.java`):
```java
public interface SysExampleMapper extends BaseMapper<SysExample> {
    // Custom queries if needed
}
```

**Service Interface** (`service/ISysExampleService.java`):
```java
public interface ISysExampleService extends IService<SysExample> {
    // Custom methods
}
```

**Service Implementation** (`service/impl/SysExampleServiceImpl.java`):
```java
@Service
public class SysExampleServiceImpl extends ServiceImpl<SysExampleMapper, SysExample>
    implements ISysExampleService {
    // Implement custom methods
}
```

**DTO** (`dto/ExampleDTO.java`) - for request parameters
**VO** (`vo/ExampleVO.java`) - for response data

**Controller** (`controller/SysExampleController.java`):
```java
@RestController
@RequestMapping("/system/example")
@Tag(name = "Example Management")
public class SysExampleController {
    @Autowired
    private ISysExampleService exampleService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:example:list')")
    public PageResult<SysExample> list(PageQuery query) {
        // Implementation
    }
}
```

### 3. Frontend Layer

**API** (`src/api/system/example.js`):
```javascript
import request from '@/utils/request'

export function listExample(query) {
  return request({
    url: '/system/example/list',
    method: 'get',
    params: query
  })
}

export function addExample(data) {
  return request({
    url: '/system/example',
    method: 'post',
    data
  })
}
```

**Vue Component** (`src/views/system/example/index.vue`):
- Use Element UI table for list view
- Follow existing patterns from similar modules (e.g., user management)
- Include search form, data table, pagination, and dialog forms

### 4. Configure Menu & Permissions

Add menu record in database (`sys_menu` table):
- Parent menu (directory type)
- List page menu (menu type) with route path
- Button permissions (button type): add, edit, delete, export, etc.

Assign menu permissions to roles in `sys_role_menu` table.

## Testing

- Test directory structure exists at `backend/src/test/java/com/admin/` but no test files implemented yet
- When adding tests, use JUnit 5 with Spring Boot Test
- Spring Boot Test dependencies already included in pom.xml

## Important Notes

1. **Security:**
   - Passwords encrypted with BCrypt
   - JWT tokens stored in Redis with auto-refresh
   - CSRF disabled (stateless JWT authentication)
   - All endpoints except `/login`, `/captcha`, and Swagger require authentication

2. **CORS:**
   - Configured in `CorsConfig` for cross-origin requests
   - Development: Frontend proxy handles CORS
   - Production: Backend CORS config applies

3. **Session Management:**
   - Completely stateless (Spring Security session policy: STATELESS)
   - All user state stored in Redis via JWT token key

4. **Code Generation:**
   - MyBatis Plus Generator included in dependencies (Velocity templates)
   - See README.md for code generation feature usage

5. **Logging:**
   - Colorized console output enabled
   - Log files: `backend/logs/admin-system.log`
   - SQL logging enabled in MyBatis Plus configuration

6. **Development Environment:**
   - Project tested on Windows platform
   - Both util/ and utils/ packages exist in backend (legacy structure)
   - Redis password is empty by default in application.yml (set password if needed)
   - Frontend webpack dev server listens on `0.0.0.0` for network access

## Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```cmd
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (use PID from previous command)
taskkill /PID <process_id> /F
```

**Database connection errors:**
- Verify MySQL is running and accessible
- Check credentials in `backend/src/main/resources/application.yml`
- Ensure database `admin_system` exists and schema is loaded
- Default credentials: username=root, password=root

**Redis connection errors:**
- Verify Redis is running (`redis-server`)
- Check Redis host/port in application.yml (default: localhost:6379)
- Redis password is empty by default

**Build/dependency errors:**
- Clear Maven cache: `mvn clean` or delete `~/.m2/repository`
- Ensure Java 8+ is installed: `java -version`
- Ensure Maven is installed: `mvn -version`

### Frontend Issues

**Port 8081 already in use:**
```cmd
# Find process using port 8081
netstat -ano | findstr :8081

# Kill the process
taskkill /PID <process_id> /F
```

**npm install fails:**
- Try deleting `node_modules` and `package-lock.json`, then run `npm install` again
- Use `npm install --legacy-peer-deps` if peer dependency conflicts occur
- Ensure Node.js 14+ is installed: `node -v`

**Proxy/API connection errors:**
- Verify backend is running on http://localhost:8080
- Check proxy configuration in `vue.config.js`
- Check `VUE_APP_BASE_API` in `.env.development`

**Login issues:**
- Default credentials: username=admin, password=admin123
- If password doesn't work, check if `database/fix-password.sql` needs to be run
- Verify backend `/login` endpoint is accessible

### Common Runtime Issues

**403 Forbidden errors:**
- Check if user has required permissions for the endpoint
- Verify JWT token is valid and not expired
- Check `@PreAuthorize` annotations on controllers

**White screen after login:**
- Check browser console for errors
- Verify user has menu/route permissions in database
- Clear browser cache and cookies
- Check if `getRouters` API returns valid menu data

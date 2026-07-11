# 登录页动态宠物 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在登录页提供可跟随鼠标方向的猫咪、小狗、猫头鹰三种 108px 动态宠物，并允许具备 `system:config:edit` 权限的管理员在主题设置抽屉中全局切换，默认和异常回退均为猫咪。

**Architecture:** 后端复用 `sys_config` 保存 `sys.login.pet.type`，通过一个匿名只读接口和一个受权限保护的更新接口提供配置。前端以 `LoginPet` 为统一入口，将鼠标方向计算、生命周期监听和三个内联 SVG 渲染组件分离；登录页只读取配置，主题设置抽屉负责管理员预览与保存。

**Tech Stack:** Java 17、Spring Boot 4.1、Spring Security、MyBatis-Plus、JUnit 5、Mockito、Vue 3.5、TypeScript 6、Vite 6、Vitest 4、Vue Test Utils 2、Element Plus、SCSS、内联 SVG。

## Global Constraints

- 所有说明和用户可见新增文案使用中文。
- TODO、PLAN 和设计文档只存放在项目根目录 `spec/plan`。
- 代码必须可维护、可扩展、可测试。
- 登录页宠物类型只允许 `cat`、`dog`、`owl`，默认值固定为 `cat`。
- 登录页宠物尺寸固定为 108px，位置固定在 `CXAdmin` 标题上方。
- 全局配置键固定为 `sys.login.pet.type`，不写入 `localStorage`。
- 匿名接口只暴露登录宠物枚举值，不开放任意系统配置键。
- 更新接口必须由后端强制校验 `system:config:edit`。
- 960px 以下继续隐藏登录页左侧区域，不改变移动端登录表单布局。
- 本次不修改注册、忘记密码、重置密码、认证、验证码、SSO 和路由行为。
- 本次不引入 Lottie、Canvas 动画框架、远程宠物资源或自定义上传能力。
- 保留工作区已有的 `backend/admin-boot/src/main/resources/application.yml` 与 `frontend/vite.config.ts` 修改，不覆盖、不提交与本功能无关的差异。

---

## File Structure

### 后端新增

- `backend/admin-system/src/main/java/com/admin/system/enums/LoginPetType.java`：宠物白名单、默认值和归一化规则。
- `backend/admin-system/src/main/java/com/admin/system/dto/LoginPetTypeDTO.java`：管理员更新请求体及 Bean Validation 规则。
- `backend/admin-system/src/test/java/com/admin/system/service/SysConfigServiceImplTest.java`：登录宠物配置服务测试。
- `backend/admin-system/src/test/java/com/admin/system/controller/SysConfigControllerTest.java`：接口委托、响应和权限注解契约测试。

### 后端修改

- `backend/admin-system/src/main/java/com/admin/system/service/ISysConfigService.java`：增加登录宠物读取和更新接口。
- `backend/admin-system/src/main/java/com/admin/system/service/impl/SysConfigServiceImpl.java`：白名单回退、合法值更新和缺失配置处理。
- `backend/admin-system/src/main/java/com/admin/system/controller/SysConfigController.java`：增加公开读取和管理员更新端点。
- `backend/admin-framework/src/main/java/com/admin/framework/config/SecurityConfig.java`：仅允许匿名 GET 指定宠物读取路径。
- `database/init.sql`：增加内置默认配置并调整自增起点。

### 前端新增

- `frontend/vitest.config.ts`：与生产 Vite 配置隔离的 Vitest 配置。
- `frontend/src/types/login-pet.ts`：共享宠物类型、默认值和运行时归一化。
- `frontend/src/components/LoginPet/tracking.ts`：纯函数方向计算与运动状态类型。
- `frontend/src/components/LoginPet/tracking.spec.ts`：方向计算边界测试。
- `frontend/src/components/LoginPet/CatPet.vue`：猫咪 SVG 和专属动作。
- `frontend/src/components/LoginPet/DogPet.vue`：小狗 SVG 和专属动作。
- `frontend/src/components/LoginPet/OwlPet.vue`：猫头鹰 SVG 和专属动作。
- `frontend/src/components/LoginPet/index.vue`：类型路由、页面指针监听、帧合并和清理。
- `frontend/src/components/LoginPet/index.spec.ts`：渲染、跟随、清理和减少动态效果测试。
- `frontend/src/composables/useLoginPetConfig.ts`：读取全局宠物并静默回退默认值。
- `frontend/src/composables/useLoginPetConfig.spec.ts`：合法值、非法值和请求失败测试。
- `frontend/src/components/ThemeSettings/index.spec.ts`：权限显示、保存、回滚和防重复提交测试。
- `frontend/src/views/login/index.spec.ts`：登录页 108px 宠物集成与回退测试。

### 前端修改

- `frontend/package.json`：增加测试脚本和测试依赖。
- `frontend/package-lock.json`：锁定测试依赖。
- `frontend/src/types/system/config.ts`：增加登录宠物更新请求类型。
- `frontend/src/api/system/config.ts`：增加读取和更新登录宠物的 API。
- `frontend/src/utils/request.ts`：支持单次请求关闭全局错误消息，用于登录页外观静默回退。
- `frontend/src/components/ThemeSettings/index.vue`：增加管理员宠物选择区。
- `frontend/src/views/login/index.vue`：用 `LoginPet` 替换平台图标并读取全局配置。

---

### Task 1: 建立前端测试基线与宠物运动纯函数

**Files:**
- Create: `frontend/vitest.config.ts`
- Create: `frontend/src/types/login-pet.ts`
- Create: `frontend/src/components/LoginPet/tracking.ts`
- Create: `frontend/src/components/LoginPet/tracking.spec.ts`
- Modify: `frontend/package.json`
- Modify: `frontend/package-lock.json`

**Interfaces:**
- Produces: `LoginPetType`、`LOGIN_PET_TYPES`、`DEFAULT_LOGIN_PET_TYPE`、`isLoginPetType(value)`、`normalizeLoginPetType(value)`。
- Produces: `PetMotion`、`IDLE_PET_MOTION`、`calculatePetMotion(pointer, rect, intensity?)`。
- Consumed by: Tasks 4、5、6、7。

- [ ] **Step 1: 安装与当前 Vite 6、Node 20 兼容的测试依赖并添加测试脚本**

Run:

```powershell
cd frontend
npm install --save-dev vitest@^4.0.0 @vue/test-utils@^2.4.6 jsdom@^27.0.0
```

在 `frontend/package.json` 的 `scripts` 中增加：

```json
"test": "vitest run",
"test:watch": "vitest"
```

Expected: `package.json` 和 `package-lock.json` 只增加测试相关依赖；不修改 `vite.config.ts`。

- [ ] **Step 2: 创建独立 Vitest 配置**

Create `frontend/vitest.config.ts`:

```ts
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  test: {
    environment: 'jsdom',
    clearMocks: true,
    restoreMocks: true,
    css: true
  }
})
```

- [ ] **Step 3: 先写类型归一化和方向计算的失败测试**

Create `frontend/src/components/LoginPet/tracking.spec.ts`:

```ts
import { describe, expect, it } from 'vitest'
import { DEFAULT_LOGIN_PET_TYPE, normalizeLoginPetType } from '@/types/login-pet'
import { IDLE_PET_MOTION, calculatePetMotion } from './tracking'

const rect = { left: 100, top: 100, width: 100, height: 100 }

describe('normalizeLoginPetType', () => {
  it.each(['cat', 'dog', 'owl'] as const)('保留合法类型 %s', type => {
    expect(normalizeLoginPetType(type)).toBe(type)
  })

  it.each([undefined, null, '', 'fox', 1])('非法值 %s 回退猫咪', value => {
    expect(normalizeLoginPetType(value)).toBe(DEFAULT_LOGIN_PET_TYPE)
  })
})

describe('calculatePetMotion', () => {
  it('鼠标位于宠物中心时返回静止状态', () => {
    expect(calculatePetMotion({ x: 150, y: 150 }, rect)).toEqual(IDLE_PET_MOTION)
  })

  it('向右上方移动时眼睛与头部同向且旋转为正', () => {
    const motion = calculatePetMotion({ x: 250, y: 50 }, rect)
    expect(motion.eyeX).toBeGreaterThan(0)
    expect(motion.eyeY).toBeLessThan(0)
    expect(motion.headX).toBeGreaterThan(0)
    expect(motion.headY).toBeLessThan(0)
    expect(motion.rotate).toBeGreaterThan(0)
  })

  it('极远坐标被限制在设计幅度内', () => {
    expect(calculatePetMotion({ x: 10000, y: -10000 }, rect)).toEqual({
      eyeX: 6,
      eyeY: -6,
      headX: 4,
      headY: -3,
      rotate: 5
    })
  })

  it('零尺寸或非有限边界返回静止状态', () => {
    expect(calculatePetMotion({ x: 1, y: 1 }, { ...rect, width: 0 })).toEqual(IDLE_PET_MOTION)
    expect(calculatePetMotion({ x: Number.NaN, y: 1 }, rect)).toEqual(IDLE_PET_MOTION)
  })

  it('减少动态效果时按 intensity 缩小幅度', () => {
    const motion = calculatePetMotion({ x: 250, y: 50 }, rect, 0.35)
    expect(Math.abs(motion.eyeX)).toBeLessThanOrEqual(2.1)
    expect(Math.abs(motion.rotate)).toBeLessThanOrEqual(1.75)
  })
})
```

- [ ] **Step 4: 运行测试并确认因模块不存在而失败**

Run:

```powershell
cd frontend
npm run test -- src/components/LoginPet/tracking.spec.ts
```

Expected: FAIL，错误包含无法解析 `@/types/login-pet` 或 `./tracking`。

- [ ] **Step 5: 实现共享宠物类型**

Create `frontend/src/types/login-pet.ts`:

```ts
export const LOGIN_PET_TYPES = ['cat', 'dog', 'owl'] as const

export type LoginPetType = typeof LOGIN_PET_TYPES[number]

export const DEFAULT_LOGIN_PET_TYPE: LoginPetType = 'cat'

export function isLoginPetType(value: unknown): value is LoginPetType {
  return typeof value === 'string' && LOGIN_PET_TYPES.includes(value as LoginPetType)
}

export function normalizeLoginPetType(value: unknown): LoginPetType {
  return isLoginPetType(value) ? value : DEFAULT_LOGIN_PET_TYPE
}
```

- [ ] **Step 6: 实现纯函数方向计算**

Create `frontend/src/components/LoginPet/tracking.ts`:

```ts
export interface PointerPosition {
  x: number
  y: number
}

export interface PetRect {
  left: number
  top: number
  width: number
  height: number
}

export interface PetMotion {
  eyeX: number
  eyeY: number
  headX: number
  headY: number
  rotate: number
}

export const IDLE_PET_MOTION: PetMotion = Object.freeze({
  eyeX: 0,
  eyeY: 0,
  headX: 0,
  headY: 0,
  rotate: 0
})

const clamp = (value: number, min: number, max: number): number =>
  Math.min(max, Math.max(min, value))

const round = (value: number): number => Math.round(value * 100) / 100

export function calculatePetMotion(
  pointer: PointerPosition,
  rect: PetRect,
  intensity = 1
): PetMotion {
  const values = [pointer.x, pointer.y, rect.left, rect.top, rect.width, rect.height, intensity]
  if (values.some(value => !Number.isFinite(value)) || rect.width <= 0 || rect.height <= 0) {
    return { ...IDLE_PET_MOTION }
  }

  const centerX = rect.left + rect.width / 2
  const centerY = rect.top + rect.height / 2
  const normalizedX = clamp((pointer.x - centerX) / rect.width, -1, 1)
  const normalizedY = clamp((pointer.y - centerY) / rect.height, -1, 1)
  const safeIntensity = clamp(intensity, 0, 1)

  return {
    eyeX: round(normalizedX * 6 * safeIntensity),
    eyeY: round(normalizedY * 6 * safeIntensity),
    headX: round(normalizedX * 4 * safeIntensity),
    headY: round(normalizedY * 3 * safeIntensity),
    rotate: round(normalizedX * 5 * safeIntensity)
  }
}
```

- [ ] **Step 7: 运行测试并确认通过**

Run:

```powershell
cd frontend
npm run test -- src/components/LoginPet/tracking.spec.ts
```

Expected: PASS，全部方向计算和回退测试通过。

- [ ] **Step 8: 提交测试基线与纯函数**

```powershell
git add frontend/package.json frontend/package-lock.json frontend/vitest.config.ts frontend/src/types/login-pet.ts frontend/src/components/LoginPet/tracking.ts frontend/src/components/LoginPet/tracking.spec.ts
git commit -m "test(frontend): add login pet test baseline"
```

---

### Task 2: 实现后端全局宠物配置领域规则

**Files:**
- Create: `backend/admin-system/src/main/java/com/admin/system/enums/LoginPetType.java`
- Create: `backend/admin-system/src/test/java/com/admin/system/service/SysConfigServiceImplTest.java`
- Modify: `backend/admin-system/src/main/java/com/admin/system/service/ISysConfigService.java`
- Modify: `backend/admin-system/src/main/java/com/admin/system/service/impl/SysConfigServiceImpl.java`
- Modify: `database/init.sql`

**Interfaces:**
- Produces: `LoginPetType.DEFAULT_VALUE`、`LoginPetType.normalize(String)`、`LoginPetType.isValid(String)`。
- Produces: `ISysConfigService.selectLoginPetType()` 和 `ISysConfigService.updateLoginPetType(String)`。
- Consumed by: Task 3 controller endpoints。

- [ ] **Step 1: 写服务层失败测试**

Create `backend/admin-system/src/test/java/com/admin/system/service/SysConfigServiceImplTest.java`:

```java
package com.admin.system.service;

import com.admin.common.exception.ServiceException;
import com.admin.system.entity.SysConfig;
import com.admin.system.mapper.SysConfigMapper;
import com.admin.system.service.impl.SysConfigServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayName("登录页宠物配置服务测试")
@ExtendWith(MockitoExtension.class)
class SysConfigServiceImplTest {

    private static final String CONFIG_KEY = "sys.login.pet.type";

    @InjectMocks
    private SysConfigServiceImpl configService;

    @Mock
    private SysConfigMapper configMapper;

    @Test
    void selectLoginPetType_missingConfig_returnsCat() {
        when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(null);
        assertEquals("cat", configService.selectLoginPetType());
    }

    @Test
    void selectLoginPetType_invalidValue_returnsCat() {
        when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(config(4L, "fox"));
        assertEquals("cat", configService.selectLoginPetType());
    }

    @Test
    void selectLoginPetType_validValues_arePreserved() {
        for (String type : new String[]{"cat", "dog", "owl"}) {
            when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(config(4L, type));
            assertEquals(type, configService.selectLoginPetType());
        }
    }

    @Test
    void updateLoginPetType_validValue_updatesExistingConfig() {
        when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(config(4L, "cat"));
        when(configMapper.updateById(any(SysConfig.class))).thenReturn(1);

        configService.updateLoginPetType("owl");

        verify(configMapper).updateById(argThat(config ->
                config.getConfigId().equals(4L) && "owl".equals(config.getConfigValue())));
    }

    @Test
    void updateLoginPetType_invalidValue_rejectsWithoutWrite() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> configService.updateLoginPetType("fox"));
        assertEquals("不支持的登录页宠物类型", exception.getMessage());
        verify(configMapper, never()).updateById(any());
    }

    @Test
    void updateLoginPetType_missingConfig_reportsClearError() {
        when(configMapper.selectConfigByKey(CONFIG_KEY)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class,
                () -> configService.updateLoginPetType("dog"));
        assertEquals("登录页宠物配置不存在", exception.getMessage());
    }

    private SysConfig config(Long id, String value) {
        SysConfig config = new SysConfig();
        config.setConfigId(id);
        config.setConfigKey(CONFIG_KEY);
        config.setConfigValue(value);
        config.setConfigType("Y");
        return config;
    }
}
```

- [ ] **Step 2: 运行测试并确认缺少接口而失败**

Run:

```powershell
cd backend
mvn -pl admin-system -am -Dtest=SysConfigServiceImplTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: FAIL，错误包含 `selectLoginPetType` 或 `updateLoginPetType` 未定义。

- [ ] **Step 3: 实现宠物枚举**

Create `backend/admin-system/src/main/java/com/admin/system/enums/LoginPetType.java`:

```java
package com.admin.system.enums;

import java.util.Arrays;

public enum LoginPetType {
    CAT("cat"),
    DOG("dog"),
    OWL("owl");

    public static final String DEFAULT_VALUE = "cat";

    private final String value;

    LoginPetType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValid(String value) {
        return Arrays.stream(values()).anyMatch(type -> type.value.equals(value));
    }

    public static String normalize(String value) {
        return isValid(value) ? value : DEFAULT_VALUE;
    }
}
```

- [ ] **Step 4: 扩展服务接口**

在 `ISysConfigService` 中增加：

```java
String LOGIN_PET_CONFIG_KEY = "sys.login.pet.type";

String selectLoginPetType();

void updateLoginPetType(String type);
```

- [ ] **Step 5: 实现服务读取、回退和事务更新**

在 `SysConfigServiceImpl` 中增加：

```java
import com.admin.system.enums.LoginPetType;

@Override
public String selectLoginPetType() {
    return LoginPetType.normalize(selectConfigByKey(LOGIN_PET_CONFIG_KEY));
}

@Override
@Transactional(rollbackFor = Exception.class)
public void updateLoginPetType(String type) {
    if (!LoginPetType.isValid(type)) {
        throw new ServiceException("不支持的登录页宠物类型");
    }

    SysConfig config = configMapper.selectConfigByKey(LOGIN_PET_CONFIG_KEY);
    if (config == null) {
        throw new ServiceException("登录页宠物配置不存在");
    }

    config.setConfigValue(type);
    if (configMapper.updateById(config) != 1) {
        throw new ServiceException("登录页宠物配置更新失败");
    }
}
```

- [ ] **Step 6: 增加数据库默认配置**

在 `database/init.sql` 中将 `sys_config` 的 `AUTO_INCREMENT` 从 `4` 调整为 `5`，并在现有三条种子记录后增加：

```sql
INSERT INTO `sys_config` VALUES (4, '登录页宠物', 'sys.login.pet.type', 'cat', 'Y', '2026-07-10 00:00:00', 1, NULL, NULL, '允许值：cat、dog、owl；默认 cat', 0);
```

- [ ] **Step 7: 运行服务测试并确认通过**

Run:

```powershell
cd backend
mvn -pl admin-system -am -Dtest=SysConfigServiceImplTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: PASS，6 个登录宠物配置服务测试全部通过。

- [ ] **Step 8: 提交后端领域规则和数据库种子**

```powershell
git add backend/admin-system/src/main/java/com/admin/system/enums/LoginPetType.java backend/admin-system/src/main/java/com/admin/system/service/ISysConfigService.java backend/admin-system/src/main/java/com/admin/system/service/impl/SysConfigServiceImpl.java backend/admin-system/src/test/java/com/admin/system/service/SysConfigServiceImplTest.java database/init.sql
git commit -m "feat(config): add global login pet setting"
```

---

### Task 3: 暴露安全的登录宠物读取与更新接口

**Files:**
- Create: `backend/admin-system/src/main/java/com/admin/system/dto/LoginPetTypeDTO.java`
- Create: `backend/admin-system/src/test/java/com/admin/system/controller/SysConfigControllerTest.java`
- Modify: `backend/admin-system/src/main/java/com/admin/system/controller/SysConfigController.java`
- Modify: `backend/admin-framework/src/main/java/com/admin/framework/config/SecurityConfig.java`

**Interfaces:**
- Produces: `GET /system/config/public/login-pet -> Result<String>`。
- Produces: `PUT /system/config/login-pet` with `{ "type": "cat|dog|owl" } -> Result<Void>`。
- Consumed by: Task 5 frontend API。

- [ ] **Step 1: 写控制器失败测试，包括权限注解契约**

Create `backend/admin-system/src/test/java/com/admin/system/controller/SysConfigControllerTest.java`:

```java
package com.admin.system.controller;

import com.admin.common.Result;
import com.admin.system.dto.LoginPetTypeDTO;
import com.admin.system.service.ISysConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysConfigControllerTest {

    @InjectMocks
    private SysConfigController controller;

    @Mock
    private ISysConfigService configService;

    @Test
    void getLoginPet_returnsServiceValue() {
        when(configService.selectLoginPetType()).thenReturn("owl");
        Result<String> result = controller.getLoginPet();
        assertEquals("owl", result.getData());
    }

    @Test
    void updateLoginPet_delegatesValidatedType() {
        LoginPetTypeDTO body = new LoginPetTypeDTO();
        body.setType("dog");
        Result<Void> result = controller.updateLoginPet(body);
        assertEquals(200, result.getCode());
        verify(configService).updateLoginPetType("dog");
    }

    @Test
    void updateLoginPet_requiresConfigEditPermission() throws Exception {
        Method method = SysConfigController.class.getMethod("updateLoginPet", LoginPetTypeDTO.class);
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
        assertNotNull(annotation);
        assertEquals("@ss.hasPermi('system:config:edit')", annotation.value());
    }
}
```

- [ ] **Step 2: 运行控制器测试并确认缺少 DTO 和方法而失败**

Run:

```powershell
cd backend
mvn -pl admin-system -am -Dtest=SysConfigControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: FAIL，错误包含 `LoginPetTypeDTO`、`getLoginPet` 或 `updateLoginPet` 不存在。

- [ ] **Step 3: 实现受校验的请求 DTO**

Create `backend/admin-system/src/main/java/com/admin/system/dto/LoginPetTypeDTO.java`:

```java
package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginPetTypeDTO {

    @NotBlank(message = "登录页宠物类型不能为空")
    @Pattern(regexp = "cat|dog|owl", message = "登录页宠物类型仅支持 cat、dog、owl")
    private String type;
}
```

- [ ] **Step 4: 实现两个专用端点**

在 `SysConfigController` 增加：

```java
import com.admin.system.dto.LoginPetTypeDTO;

@Operation(summary = "查询登录页宠物")
@GetMapping("/public/login-pet")
public Result<String> getLoginPet() {
    return Result.success(configService.selectLoginPetType());
}

@Operation(summary = "修改登录页宠物")
@PreAuthorize("@ss.hasPermi('system:config:edit')")
@PutMapping("/login-pet")
public Result<Void> updateLoginPet(@Validated @RequestBody LoginPetTypeDTO body) {
    configService.updateLoginPetType(body.getType());
    return Result.success("登录页宠物修改成功");
}
```

- [ ] **Step 5: 只放行指定匿名 GET 路径**

在 `SecurityConfig.filterChain` 的 `authorizeHttpRequests` 中、通用静态资源规则之前增加：

```java
.requestMatchers(HttpMethod.GET, "/system/config/public/login-pet").permitAll()
```

不要放行 `/system/config/**`，也不要放行 `PUT /system/config/login-pet`。

- [ ] **Step 6: 运行控制器、服务和框架安全相关测试**

Run:

```powershell
cd backend
mvn -pl admin-system,admin-framework -am -Dtest=SysConfigControllerTest,SysConfigServiceImplTest,PermissionServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: PASS，控制器 3 个测试、服务 6 个测试以及现有权限服务测试全部通过。

- [ ] **Step 7: 提交接口与安全规则**

```powershell
git add backend/admin-system/src/main/java/com/admin/system/dto/LoginPetTypeDTO.java backend/admin-system/src/main/java/com/admin/system/controller/SysConfigController.java backend/admin-system/src/test/java/com/admin/system/controller/SysConfigControllerTest.java backend/admin-framework/src/main/java/com/admin/framework/config/SecurityConfig.java
git commit -m "feat(config): expose login pet endpoints"
```

---

### Task 4: 实现三个 SVG 宠物与页面级方向跟随

**Files:**
- Create: `frontend/src/components/LoginPet/CatPet.vue`
- Create: `frontend/src/components/LoginPet/DogPet.vue`
- Create: `frontend/src/components/LoginPet/OwlPet.vue`
- Create: `frontend/src/components/LoginPet/index.vue`
- Create: `frontend/src/components/LoginPet/index.spec.ts`

**Interfaces:**
- Consumes: `LoginPetType` and `PetMotion` from Task 1。
- Produces: `<LoginPet :type :size :interactive>`，默认 `type='cat'`、`size=108`、`interactive=true`。
- Produces: root attributes `data-testid="login-pet"` and `data-pet="cat|dog|owl"` for stable tests。
- Consumed by: Tasks 6 and 7。

- [ ] **Step 1: 写组件渲染、帧合并、清理和减少动态效果失败测试**

Create `frontend/src/components/LoginPet/index.spec.ts`:

```ts
import { mount } from '@vue/test-utils'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import LoginPet from './index.vue'

describe('LoginPet', () => {
  const listeners = new Map<string, EventListener>()
  const mediaListeners = new Set<(event: MediaQueryListEvent) => void>()

  beforeEach(() => {
    vi.spyOn(window, 'addEventListener').mockImplementation((type, listener) => {
      listeners.set(type, listener as EventListener)
    })
    vi.spyOn(window, 'removeEventListener')
    vi.spyOn(window, 'requestAnimationFrame').mockImplementation(callback => {
      callback(0)
      return 1
    })
    vi.spyOn(window, 'cancelAnimationFrame')
    vi.stubGlobal('matchMedia', vi.fn().mockReturnValue({
      matches: false,
      addEventListener: (_type: string, listener: (event: MediaQueryListEvent) => void) => mediaListeners.add(listener),
      removeEventListener: (_type: string, listener: (event: MediaQueryListEvent) => void) => mediaListeners.delete(listener)
    }))
  })

  afterEach(() => {
    listeners.clear()
    mediaListeners.clear()
    vi.unstubAllGlobals()
  })

  it.each(['cat', 'dog', 'owl'] as const)('渲染 %s SVG', type => {
    const wrapper = mount(LoginPet, { props: { type } })
    expect(wrapper.get('[data-testid="login-pet"]').attributes('data-pet')).toBe(type)
    expect(wrapper.find('svg').exists()).toBe(true)
  })

  it('非法运行时类型回退猫咪', () => {
    const wrapper = mount(LoginPet, { props: { type: 'fox' as never } })
    expect(wrapper.get('[data-testid="login-pet"]').attributes('data-pet')).toBe('cat')
  })

  it('指针移动通过 requestAnimationFrame 合并并更新 CSS 变量', async () => {
    const wrapper = mount(LoginPet, { attachTo: document.body })
    vi.spyOn(wrapper.element, 'getBoundingClientRect').mockReturnValue({
      left: 100, top: 100, width: 100, height: 100,
      right: 200, bottom: 200, x: 100, y: 100, toJSON: () => ({})
    })
    listeners.get('pointermove')?.({ clientX: 250, clientY: 50 } as PointerEvent)
    await wrapper.vm.$nextTick()
    expect(window.requestAnimationFrame).toHaveBeenCalledTimes(1)
    expect(wrapper.get('[data-testid="login-pet"]').attributes('style')).toContain('--pet-eye-x: 6px')
    wrapper.unmount()
  })

  it('interactive=false 不注册页面指针监听', () => {
    const wrapper = mount(LoginPet, { props: { interactive: false } })
    expect(window.addEventListener).not.toHaveBeenCalledWith('pointermove', expect.any(Function), expect.anything())
    wrapper.unmount()
  })

  it('粗指针设备保持正面姿态', () => {
    vi.mocked(window.matchMedia).mockImplementation(query => ({
      matches: query === '(pointer: coarse)',
      addEventListener: vi.fn(),
      removeEventListener: vi.fn()
    } as unknown as MediaQueryList))
    const wrapper = mount(LoginPet)
    expect(window.addEventListener).not.toHaveBeenCalledWith('pointermove', expect.any(Function), expect.anything())
    wrapper.unmount()
  })

  it('卸载时清理监听器和动画帧', () => {
    vi.mocked(window.requestAnimationFrame).mockReturnValueOnce(7)
    const wrapper = mount(LoginPet)
    listeners.get('pointermove')?.({ clientX: 250, clientY: 50 } as PointerEvent)
    wrapper.unmount()
    expect(window.removeEventListener).toHaveBeenCalledWith('pointermove', expect.any(Function))
    expect(window.cancelAnimationFrame).toHaveBeenCalledWith(7)
  })
})
```

- [ ] **Step 2: 运行测试并确认组件不存在而失败**

Run:

```powershell
cd frontend
npm run test -- src/components/LoginPet/index.spec.ts
```

Expected: FAIL，错误包含无法解析 `./index.vue`。

- [ ] **Step 3: 实现三个 SVG 子组件的统一输入协议**

三个组件均声明：

```ts
import type { PetMotion } from './tracking'

const props = defineProps<{
  motion: PetMotion
  reducedMotion: boolean
}>()
```

`CatPet.vue` 使用以下稳定分组，测试和样式依赖这些类名：

```vue
<template>
  <svg viewBox="0 0 120 120" role="img" aria-label="动态猫咪">
    <g class="cat-tail"><path d="M88 88 C116 78 116 108 94 108" /></g>
    <g class="pet-head" :style="headStyle">
      <path class="pet-fill" d="M25 42 L18 16 L42 30 Q60 20 78 30 L102 16 L95 43 Q101 55 96 72 Q90 96 60 98 Q30 96 24 72 Q19 55 25 42Z" />
      <ellipse class="eye" cx="44" cy="59" rx="12" ry="14" />
      <ellipse class="eye" cx="76" cy="59" rx="12" ry="14" />
      <g class="pupils" :style="eyeStyle"><circle cx="44" cy="60" r="5" /><circle cx="76" cy="60" r="5" /></g>
      <path class="nose" d="M55 75 Q60 70 65 75 Q60 82 55 75Z" />
      <path class="mouth" d="M60 80 Q53 88 48 82 M60 80 Q67 88 72 82" />
    </g>
  </svg>
</template>
```

`DogPet.vue` 使用暖色耳朵、口鼻和尾巴：

```vue
<template>
  <svg viewBox="0 0 120 120" role="img" aria-label="动态小狗">
    <g class="dog-tail"><path d="M91 88 Q116 74 111 98" /></g>
    <g class="pet-head" :style="headStyle">
      <path class="dog-ear left" d="M31 35 Q5 31 13 67 Q18 84 34 71Z" />
      <path class="dog-ear right" d="M89 35 Q115 31 107 67 Q102 84 86 71Z" />
      <path class="pet-fill" d="M28 34 Q60 14 92 34 Q101 55 94 79 Q84 99 60 99 Q36 99 26 79 Q19 55 28 34Z" />
      <ellipse class="eye" cx="44" cy="58" rx="11" ry="13" />
      <ellipse class="eye" cx="76" cy="58" rx="11" ry="13" />
      <g class="pupils" :style="eyeStyle"><circle cx="44" cy="59" r="5" /><circle cx="76" cy="59" r="5" /></g>
      <ellipse class="muzzle" cx="60" cy="77" rx="19" ry="15" />
      <ellipse class="nose" cx="60" cy="71" rx="8" ry="6" />
      <path class="dog-tongue" d="M54 82 Q60 94 66 82 V94 Q60 104 54 94Z" />
    </g>
  </svg>
</template>
```

`OwlPet.vue` 使用大眼睛和两侧翅膀：

```vue
<template>
  <svg viewBox="0 0 120 120" role="img" aria-label="动态猫头鹰">
    <g class="owl-wing left"><path d="M30 54 Q4 67 20 101 Q31 94 39 79Z" /></g>
    <g class="owl-wing right"><path d="M90 54 Q116 67 100 101 Q89 94 81 79Z" /></g>
    <g class="pet-head" :style="headStyle">
      <path class="pet-fill" d="M20 33 L31 14 L48 27 Q60 21 72 27 L89 14 L100 33 Q106 62 94 87 Q82 104 60 104 Q38 104 26 87 Q14 62 20 33Z" />
      <circle class="eye" cx="43" cy="57" r="18" />
      <circle class="eye" cx="77" cy="57" r="18" />
      <g class="pupils" :style="eyeStyle"><circle cx="43" cy="58" r="7" /><circle cx="77" cy="58" r="7" /></g>
      <path class="beak" d="M52 71 L68 71 L60 87Z" />
    </g>
  </svg>
</template>
```

每个组件内使用计算样式：

```ts
const headStyle = computed(() => ({
  transform: `translate(${props.motion.headX}px, ${props.motion.headY}px) rotate(${props.motion.rotate}deg)`,
  transformOrigin: '60px 65px'
}))

const eyeStyle = computed(() => ({
  transform: `translate(${props.motion.eyeX}px, ${props.motion.eyeY}px)`
}))
```

三个组件的根 `svg` 使用 `:class="{ 'reduced-motion': reducedMotion }"`。每个组件 scoped 样式必须明确写入深色轮廓、角色填充色、低频眨眼和专属行为；基础样式使用：

```scss
.pet-fill,
.dog-ear,
.owl-wing,
.cat-tail,
.dog-tail {
  stroke: #071f2d;
  stroke-width: 5;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.pet-fill { fill: #8fe4dd; }
.eye,
.muzzle { fill: #ffffff; stroke: #071f2d; stroke-width: 4; }
.pupils { fill: #071f2d; transition: transform 90ms linear; }
.nose { fill: #ef7f91; stroke: #071f2d; stroke-width: 3; }
.mouth { fill: none; stroke: #071f2d; stroke-width: 3; stroke-linecap: round; }
.pet-head { transition: transform 110ms linear; }

@keyframes blink {
  0%, 44%, 48%, 100% { transform: scaleY(1); }
  46% { transform: scaleY(0.08); }
}

.eye { transform-box: fill-box; transform-origin: center; animation: blink 6s ease-in-out infinite; }

.reduced-motion * {
  animation: none !important;
  transition-duration: 80ms !important;
}
```

角色专属样式分别使用以下确定值：

```scss
/* CatPet.vue */
.cat-tail { fill: none; animation: cat-tail-wave 2.6s ease-in-out infinite; transform-origin: 92px 96px; }
@keyframes cat-tail-wave { 0%, 100% { transform: rotate(-5deg); } 50% { transform: rotate(8deg); } }

/* DogPet.vue */
.pet-fill { fill: #f1c67f; }
.dog-ear,
.dog-tail { fill: #bc7e48; }
.dog-tongue { fill: #ef7f91; stroke: #071f2d; stroke-width: 3; animation: dog-tongue-bob 2.2s ease-in-out infinite; }
@keyframes dog-tongue-bob { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(2px); } }

/* OwlPet.vue */
.pet-fill { fill: #73d8d0; }
.owl-wing { fill: #58b9b5; animation: owl-wing-breathe 3s ease-in-out infinite; transform-origin: center; }
.beak { fill: #f7b541; stroke: #071f2d; stroke-width: 3; }
@keyframes owl-wing-breathe { 0%, 100% { transform: rotate(0); } 50% { transform: rotate(4deg); } }
```

- [ ] **Step 4: 实现统一 `LoginPet` 容器**

Create `frontend/src/components/LoginPet/index.vue`，核心脚本必须完整实现以下逻辑：

```vue
<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import CatPet from './CatPet.vue'
import DogPet from './DogPet.vue'
import OwlPet from './OwlPet.vue'
import { calculatePetMotion, IDLE_PET_MOTION, type PetMotion } from './tracking'
import { normalizeLoginPetType, type LoginPetType } from '@/types/login-pet'

const props = withDefaults(defineProps<{
  type?: LoginPetType
  size?: number
  interactive?: boolean
}>(), {
  type: 'cat',
  size: 108,
  interactive: true
})

const rootRef = ref<HTMLElement>()
const motion = ref<PetMotion>({ ...IDLE_PET_MOTION })
const reducedMotion = ref(false)
const normalizedType = computed(() => normalizeLoginPetType(props.type))
const componentMap = { cat: CatPet, dog: DogPet, owl: OwlPet }
const petComponent = computed(() => componentMap[normalizedType.value])
const rootStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`,
  '--pet-eye-x': `${motion.value.eyeX}px`,
  '--pet-eye-y': `${motion.value.eyeY}px`
}))

let frameId: number | null = null
let latestPointer: PointerEvent | null = null
let mediaQuery: MediaQueryList | null = null
let pointerTrackingEnabled = false

function applyPointer(): void {
  frameId = null
  if (!latestPointer || !rootRef.value || document.hidden) return
  const rect = rootRef.value.getBoundingClientRect()
  motion.value = calculatePetMotion(
    { x: latestPointer.clientX, y: latestPointer.clientY },
    rect,
    reducedMotion.value ? 0.35 : 1
  )
}

function handlePointerMove(event: PointerEvent): void {
  latestPointer = event
  if (frameId === null) frameId = window.requestAnimationFrame(applyPointer)
}

function resetMotion(): void {
  latestPointer = null
  motion.value = { ...IDLE_PET_MOTION }
}

function handleMediaChange(event: MediaQueryListEvent): void {
  reducedMotion.value = event.matches
  resetMotion()
}

function handleVisibilityChange(): void {
  if (document.hidden) resetMotion()
}

onMounted(() => {
  mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)')
  reducedMotion.value = mediaQuery.matches
  mediaQuery.addEventListener('change', handleMediaChange)
  document.addEventListener('visibilitychange', handleVisibilityChange)
  pointerTrackingEnabled = props.interactive && !window.matchMedia('(pointer: coarse)').matches
  if (pointerTrackingEnabled) {
    window.addEventListener('pointermove', handlePointerMove, { passive: true })
    window.addEventListener('blur', resetMotion)
  }
})

onBeforeUnmount(() => {
  if (pointerTrackingEnabled) {
    window.removeEventListener('pointermove', handlePointerMove)
    window.removeEventListener('blur', resetMotion)
  }
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  mediaQuery?.removeEventListener('change', handleMediaChange)
  if (frameId !== null) window.cancelAnimationFrame(frameId)
})
</script>

<template>
  <span
    ref="rootRef"
    class="login-pet"
    data-testid="login-pet"
    :data-pet="normalizedType"
    :style="rootStyle"
    aria-live="off"
  >
    <component
      :is="petComponent"
      :motion="motion"
      :reduced-motion="reducedMotion"
    />
  </span>
</template>

<style scoped>
.login-pet {
  display: inline-block;
  flex: 0 0 auto;
  line-height: 0;
  pointer-events: none;
  contain: layout paint;
}
.login-pet :deep(svg) {
  width: 100%;
  height: 100%;
  overflow: visible;
}
</style>
```

- [ ] **Step 5: 运行组件测试并确认通过**

Run:

```powershell
cd frontend
npm run test -- src/components/LoginPet/index.spec.ts src/components/LoginPet/tracking.spec.ts
```

Expected: PASS，三种宠物、回退、帧合并、非交互模式和清理测试全部通过。

- [ ] **Step 6: 提交动态宠物组件**

```powershell
git add frontend/src/components/LoginPet
git commit -m "feat(login): add animated svg pets"
```

---

### Task 5: 实现前端配置 API 与静默回退组合函数

**Files:**
- Create: `frontend/src/composables/useLoginPetConfig.ts`
- Create: `frontend/src/composables/useLoginPetConfig.spec.ts`
- Modify: `frontend/src/types/system/config.ts`
- Modify: `frontend/src/api/system/config.ts`
- Modify: `frontend/src/utils/request.ts`

**Interfaces:**
- Consumes: Task 3 HTTP endpoints and Task 1 `LoginPetType` normalization。
- Produces: `getLoginPetType()`、`updateLoginPetType(type)`。
- Produces: `useLoginPetConfig()` returning `{ loginPetType, loading, loadLoginPetType }`。
- Consumed by: Tasks 6 and 7。

- [ ] **Step 1: 写组合函数失败测试**

Create `frontend/src/composables/useLoginPetConfig.spec.ts`:

```ts
import { describe, expect, it, vi } from 'vitest'
import { getLoginPetType } from '@/api/system/config'
import { useLoginPetConfig } from './useLoginPetConfig'

vi.mock('@/api/system/config', () => ({
  getLoginPetType: vi.fn()
}))

describe('useLoginPetConfig', () => {
  it('初始值为猫咪，合法响应后更新', async () => {
    vi.mocked(getLoginPetType).mockResolvedValue({ code: 200, message: 'ok', data: 'owl', timestamp: 1 })
    const config = useLoginPetConfig()
    expect(config.loginPetType.value).toBe('cat')
    await config.loadLoginPetType()
    expect(config.loginPetType.value).toBe('owl')
  })

  it('非法响应回退猫咪', async () => {
    vi.mocked(getLoginPetType).mockResolvedValue({ code: 200, message: 'ok', data: 'fox' as never, timestamp: 1 })
    const config = useLoginPetConfig()
    await config.loadLoginPetType()
    expect(config.loginPetType.value).toBe('cat')
  })

  it('请求失败不抛出并保留猫咪', async () => {
    vi.mocked(getLoginPetType).mockRejectedValue(new Error('offline'))
    const config = useLoginPetConfig()
    await expect(config.loadLoginPetType()).resolves.toBeUndefined()
    expect(config.loginPetType.value).toBe('cat')
  })
})
```

- [ ] **Step 2: 运行测试并确认组合函数不存在而失败**

Run:

```powershell
cd frontend
npm run test -- src/composables/useLoginPetConfig.spec.ts
```

Expected: FAIL，无法解析 `./useLoginPetConfig` 或 API 函数。

- [ ] **Step 3: 扩展 API 类型与方法**

在 `frontend/src/types/system/config.ts` 增加：

```ts
import type { LoginPetType } from '@/types/login-pet'

export interface LoginPetTypeUpdateBody {
  type: LoginPetType
}
```

在 `frontend/src/api/system/config.ts` 增加：

```ts
import type { LoginPetType } from '@/types/login-pet'
import type { LoginPetTypeUpdateBody } from '@/types/system/config'

export function getLoginPetType(): Promise<Result<LoginPetType>> {
  return request({
    url: '/system/config/public/login-pet',
    method: 'get',
    silent: true
  })
}

export function updateLoginPetType(type: LoginPetType): Promise<Result<void>> {
  const data: LoginPetTypeUpdateBody = { type }
  return request({ url: '/system/config/login-pet', method: 'put', data })
}
```

- [ ] **Step 4: 为单次请求增加静默错误选项**

在 `frontend/src/utils/request.ts` 增加配置类型：

```ts
export interface AppRequestConfig extends AxiosRequestConfig {
  silent?: boolean
}

function isSilent(config?: AxiosRequestConfig): boolean {
  return Boolean((config as AppRequestConfig | undefined)?.silent)
}
```

将 `request` 签名改为：

```ts
export function request<T = unknown>(config: AppRequestConfig): Promise<T> {
  return service(config) as unknown as Promise<T>
}
```

将现有响应拦截器替换为下面的等价实现；非静默请求保持当前行为，静默请求只拒绝 Promise，不弹出消息或登录过期确认框：

```ts
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    const code = res.code || HTTP_OK
    const silent = isSilent(response.config)

    if (code === HTTP_UNAUTHORIZED) {
      if (!silent) {
        ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/logout').then(() => {
            location.href = '/login'
          })
        })
      }
      return Promise.reject(new Error('无效的会话，或者会话已过期，请重新登录。'))
    }

    if (code === HTTP_SERVER_ERROR || code !== HTTP_OK) {
      if (!silent) {
        ElMessage({
          message: res.message || 'Error',
          type: 'error',
          duration: 5 * 1000
        })
      }
      return Promise.reject(new Error(res.message || 'Error'))
    }

    return res
  },
  (error: AxiosError) => {
    let message = error.message || ''
    if (message === 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
      message = '系统接口' + message.substr(message.length - 3) + '异常'
    }
    if (!isSilent(error.config)) {
      ElMessage({ message, type: 'error', duration: 5 * 1000 })
    }
    return Promise.reject(error)
  }
)
```

- [ ] **Step 5: 实现登录宠物读取组合函数**

Create `frontend/src/composables/useLoginPetConfig.ts`:

```ts
import { ref } from 'vue'
import { getLoginPetType } from '@/api/system/config'
import {
  DEFAULT_LOGIN_PET_TYPE,
  normalizeLoginPetType,
  type LoginPetType
} from '@/types/login-pet'

export function useLoginPetConfig() {
  const loginPetType = ref<LoginPetType>(DEFAULT_LOGIN_PET_TYPE)
  const loading = ref(false)

  async function loadLoginPetType(): Promise<void> {
    loading.value = true
    try {
      const response = await getLoginPetType()
      loginPetType.value = normalizeLoginPetType(response.data)
    } catch {
      loginPetType.value = DEFAULT_LOGIN_PET_TYPE
    } finally {
      loading.value = false
    }
  }

  return { loginPetType, loading, loadLoginPetType }
}
```

- [ ] **Step 6: 运行组合函数和类型测试**

Run:

```powershell
cd frontend
npm run test -- src/composables/useLoginPetConfig.spec.ts src/components/LoginPet/tracking.spec.ts
npm run type-check
```

Expected: PASS；类型检查无新增错误。

- [ ] **Step 7: 提交配置客户端与回退逻辑**

```powershell
git add frontend/src/types/system/config.ts frontend/src/api/system/config.ts frontend/src/utils/request.ts frontend/src/composables/useLoginPetConfig.ts frontend/src/composables/useLoginPetConfig.spec.ts
git commit -m "feat(login): load global pet config"
```

---

### Task 6: 在主题设置抽屉增加管理员宠物选择器

**Files:**
- Create: `frontend/src/components/ThemeSettings/index.spec.ts`
- Modify: `frontend/src/components/ThemeSettings/index.vue`

**Interfaces:**
- Consumes: Task 4 `LoginPet` in non-interactive mode。
- Consumes: Task 5 `getLoginPetType` and `updateLoginPetType`。
- Reads: `useUserStore().permissions` and treats `*:*:*` as super-admin permission。
- Produces: Theme drawer section `data-testid="login-pet-settings"` and option buttons `data-pet-option`。

- [ ] **Step 1: 写权限显示、保存成功、保存失败和防重复提交测试**

Create `frontend/src/components/ThemeSettings/index.spec.ts`:

```ts
import { shallowMount } from '@vue/test-utils'
import { computed, nextTick } from 'vue'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import ThemeSettings from './index.vue'
import { getLoginPetType, updateLoginPetType } from '@/api/system/config'

const permissions = vi.hoisted(() => ({ value: [] as string[] }))

vi.mock('@/composables/store', () => ({
  useSettingsStore: () => ({
    sidebarColor: computed(() => '#ffffff'),
    sidebarPosition: computed(() => 'left'),
    setSidebarColor: vi.fn(),
    setSidebarPosition: vi.fn()
  }),
  useUserStore: () => ({ permissions: computed(() => permissions.value) })
}))

vi.mock('@/api/system/config', () => ({
  getLoginPetType: vi.fn(),
  updateLoginPetType: vi.fn()
}))

const stubs = {
  ElDrawer: { template: '<section><slot /></section>' },
  ElRadioGroup: { template: '<div><slot /></div>' },
  ElRadioButton: { template: '<button><slot /></button>' },
  ElColorPicker: true,
  ElButton: { template: '<button><slot /></button>' },
  ElIcon: { template: '<span><slot /></span>' },
  Check: true,
  LoginPet: { props: ['type'], template: '<span :data-preview="type" />' }
}

describe('ThemeSettings 登录宠物设置', () => {
  beforeEach(() => {
    permissions.value = []
    vi.mocked(getLoginPetType).mockResolvedValue({ code: 200, message: 'ok', data: 'cat', timestamp: 1 })
    vi.mocked(updateLoginPetType).mockResolvedValue({ code: 200, message: 'ok', data: undefined, timestamp: 1 })
  })

  it('无 system:config:edit 权限时不渲染', () => {
    const wrapper = shallowMount(ThemeSettings, { props: { modelValue: true }, global: { stubs } })
    expect(wrapper.find('[data-testid="login-pet-settings"]').exists()).toBe(false)
  })

  it('管理员打开抽屉时加载并渲染三个选项', async () => {
    permissions.value = ['system:config:edit']
    const wrapper = shallowMount(ThemeSettings, { props: { modelValue: true }, global: { stubs } })
    await nextTick()
    expect(getLoginPetType).toHaveBeenCalledTimes(1)
    expect(wrapper.findAll('[data-pet-option]')).toHaveLength(3)
  })

  it('保存成功后更新选中态', async () => {
    permissions.value = ['system:config:edit']
    const wrapper = shallowMount(ThemeSettings, { props: { modelValue: true }, global: { stubs } })
    await nextTick()
    await wrapper.get('[data-pet-option="owl"]').trigger('click')
    expect(updateLoginPetType).toHaveBeenCalledWith('owl')
    expect(wrapper.get('[data-pet-option="owl"]').classes()).toContain('active')
  })

  it('保存失败后恢复原选项', async () => {
    permissions.value = ['system:config:edit']
    vi.mocked(updateLoginPetType).mockRejectedValue(new Error('save failed'))
    const wrapper = shallowMount(ThemeSettings, { props: { modelValue: true }, global: { stubs } })
    await nextTick()
    await wrapper.get('[data-pet-option="dog"]').trigger('click')
    expect(wrapper.get('[data-pet-option="cat"]').classes()).toContain('active')
  })
})
```

- [ ] **Step 2: 运行测试并确认设置区不存在而失败**

Run:

```powershell
cd frontend
npm run test -- src/components/ThemeSettings/index.spec.ts
```

Expected: FAIL，无法找到 `login-pet-settings` 或宠物选项。

- [ ] **Step 3: 在主题设置脚本中实现权限与保存状态**

在 `ThemeSettings/index.vue` 导入：

```ts
import LoginPet from '@/components/LoginPet/index.vue'
import { getLoginPetType, updateLoginPetType } from '@/api/system/config'
import {
  DEFAULT_LOGIN_PET_TYPE,
  normalizeLoginPetType,
  type LoginPetType
} from '@/types/login-pet'
import { useSettingsStore, useUserStore } from '@/composables/store'
```

增加状态和方法：

```ts
interface PetOption {
  type: LoginPetType
  name: string
}

const { permissions } = useUserStore()
const canEditLoginPet = computed(() =>
  permissions.value.includes('*:*:*') || permissions.value.includes('system:config:edit')
)
const petOptions: PetOption[] = [
  { type: 'cat', name: '猫咪' },
  { type: 'dog', name: '小狗' },
  { type: 'owl', name: '猫头鹰' }
]
const currentPet = ref<LoginPetType>(DEFAULT_LOGIN_PET_TYPE)
const petSaving = ref(false)
const petLoaded = ref(false)

async function loadPetSetting(): Promise<void> {
  if (!canEditLoginPet.value || petLoaded.value) return
  try {
    const response = await getLoginPetType()
    currentPet.value = normalizeLoginPetType(response.data)
    petLoaded.value = true
  } catch {
    currentPet.value = DEFAULT_LOGIN_PET_TYPE
    petLoaded.value = false
  }
}

async function selectPet(type: LoginPetType): Promise<void> {
  if (petSaving.value || type === currentPet.value) return
  const previous = currentPet.value
  currentPet.value = type
  petSaving.value = true
  try {
    await updateLoginPetType(type)
    ElMessage.success('登录页宠物已更新')
  } catch {
    currentPet.value = previous
    ElMessage.error('登录页宠物更新失败')
  } finally {
    petSaving.value = false
  }
}

watch(
  () => [drawerVisible.value, canEditLoginPet.value] as const,
  ([visible, canEdit]) => {
    if (visible && canEdit) void loadPetSetting()
  },
  { immediate: true }
)
```

上述 `loadPetSetting` 在读取异常时保留 `cat`，同时将 `petLoaded` 保持为 `false`，允许下次打开抽屉重试。

- [ ] **Step 4: 在抽屉中增加语义化选择区**

在自定义颜色区块之后、恢复默认按钮之前增加：

```vue
<div v-if="canEditLoginPet" class="setting-section" data-testid="login-pet-settings">
  <div class="section-title">登录页宠物</div>
  <div class="pet-options" role="radiogroup" aria-label="登录页宠物">
    <button
      v-for="pet in petOptions"
      :key="pet.type"
      type="button"
      class="pet-option"
      :class="{ active: currentPet === pet.type }"
      :data-pet-option="pet.type"
      :aria-checked="currentPet === pet.type"
      :disabled="petSaving"
      role="radio"
      @click="selectPet(pet.type)"
    >
      <LoginPet :type="pet.type" :size="58" :interactive="false" />
      <span>{{ pet.name }}</span>
      <el-icon v-if="currentPet === pet.type"><Check /></el-icon>
    </button>
  </div>
</div>
```

增加 scoped 样式：

```scss
.pet-options {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
}

.pet-option {
  position: relative;
  display: flex;
  min-width: 0;
  padding: 9px 4px 7px;
  align-items: center;
  flex-direction: column;
  gap: 5px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  color: #64748b;
  background: #fff;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;

  &:hover,
  &:focus-visible,
  &.active {
    border-color: #0f9f9f;
    box-shadow: 0 0 0 2px rgba(15, 159, 159, 0.12);
    outline: none;
  }

  &:disabled { cursor: wait; opacity: 0.65; }
  .el-icon { position: absolute; top: 5px; right: 5px; color: #0f9f9f; }
  span { font-size: 11px; }
}
```

- [ ] **Step 5: 运行主题设置测试、类型检查和 Lint**

Run:

```powershell
cd frontend
npm run test -- src/components/ThemeSettings/index.spec.ts
npm run type-check
npm run lint
```

Expected: PASS；无新增 TypeScript 和 ESLint 错误。

- [ ] **Step 6: 提交管理员宠物选择器**

```powershell
git add frontend/src/components/ThemeSettings/index.vue frontend/src/components/ThemeSettings/index.spec.ts
git commit -m "feat(settings): select global login pet"
```

---

### Task 7: 将动态宠物接入登录页

**Files:**
- Create: `frontend/src/views/login/index.spec.ts`
- Modify: `frontend/src/views/login/index.vue`

**Interfaces:**
- Consumes: Task 4 `<LoginPet>` and Task 5 `useLoginPetConfig()`。
- Produces: 登录页左侧 `data-testid="login-pet"`，尺寸 108px，配置失败时保持猫咪。

- [ ] **Step 1: 写登录页集成失败测试**

Create `frontend/src/views/login/index.spec.ts`:

```ts
import { shallowMount } from '@vue/test-utils'
import { ref } from 'vue'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import LoginView from './index.vue'

const petType = ref<'cat' | 'dog' | 'owl'>('cat')
const loadLoginPetType = vi.fn()

vi.mock('@/composables/useLoginPetConfig', () => ({
  useLoginPetConfig: () => ({
    loginPetType: petType,
    loading: ref(false),
    loadLoginPetType
  })
}))

vi.mock('@/composables/store', () => ({
  useUserStore: () => ({ login: vi.fn() })
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: vi.fn() }),
  useRoute: () => ({ query: {} })
}))

vi.mock('@/api/login', () => ({
  getCaptcha: vi.fn().mockResolvedValue({ data: { captchaEnabled: false } })
}))

vi.mock('@/api/system/sso', () => ({
  listEnabledProviders: vi.fn().mockResolvedValue({ data: [] })
}))

describe('登录页动态宠物集成', () => {
  beforeEach(() => {
    vi.stubGlobal('matchMedia', vi.fn().mockReturnValue({
      matches: false,
      addEventListener: vi.fn(),
      removeEventListener: vi.fn()
    }))
    vi.spyOn(window, 'requestAnimationFrame').mockReturnValue(1)
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('渲染 108px 全局宠物并触发配置加载', () => {
    petType.value = 'owl'
    const wrapper = shallowMount(LoginView, {
      global: {
        stubs: {
          LoginPet: false
        }
      }
    })

    const pet = wrapper.get('[data-testid="login-pet"]')
    expect(pet.attributes('data-pet')).toBe('owl')
    expect(pet.attributes('style')).toContain('width: 108px')
    expect(pet.attributes('style')).toContain('height: 108px')
    expect(loadLoginPetType).toHaveBeenCalled()
  })
})
```

- [ ] **Step 2: 运行测试并确认仍为 Platform 图标而失败**

Run:

```powershell
cd frontend
npm run test -- src/views/login/index.spec.ts
```

Expected: FAIL，找不到 `data-testid="login-pet"`。

- [ ] **Step 3: 替换登录页品牌图标**

在 `frontend/src/views/login/index.vue`：

1. 将模板中的：

```vue
<div class="brand-icon">
  <el-icon><Platform /></el-icon>
</div>
```

替换为：

```vue
<LoginPet class="brand-pet" :type="loginPetType" :size="108" />
```

2. 在脚本中增加：

```ts
import LoginPet from '@/components/LoginPet/index.vue'
import { useLoginPetConfig } from '@/composables/useLoginPetConfig'

const { loginPetType, loadLoginPetType } = useLoginPetConfig()
```

3. 在现有初始化调用旁增加：

```ts
void loadLoginPetType()
```

4. 删除 `.brand-icon` 样式块，增加：

```scss
.brand-pet {
  display: block;
  margin-bottom: 28px;
  filter: drop-shadow(0 10px 22px rgba(19, 194, 194, 0.3));
}
```

保持 `.brand-title`、`.brand-desc`、左侧背景、光斑和 960px 媒体查询不变。

- [ ] **Step 4: 运行登录页集成测试和全部前端测试**

Run:

```powershell
cd frontend
npm run test -- src/views/login/index.spec.ts
npm run test
```

Expected: PASS，登录页测试确认类型和 108px 尺寸；全部前端测试通过。

- [ ] **Step 5: 运行前端静态验证与生产构建**

Run:

```powershell
cd frontend
npm run type-check
npm run lint
npm run build
```

Expected: 全部退出码为 0，`dist` 构建成功，无新增警告或错误。

- [ ] **Step 6: 提交登录页集成**

```powershell
git add frontend/src/views/login/index.vue frontend/src/views/login/index.spec.ts
git commit -m "feat(login): show global animated pet"
```

---

### Task 8: 端到端验证、安全验证与收尾

**Files:**
- Verify only unless a failing check requires a focused fix.

**Interfaces:**
- Verifies all outputs from Tasks 1 through 7 as one user-visible feature。

- [ ] **Step 1: 运行完整后端测试**

Run:

```powershell
cd backend
mvn test
```

Expected: BUILD SUCCESS，全部模块测试通过。

- [ ] **Step 2: 运行完整前端验证**

Run:

```powershell
cd frontend
npm run test
npm run type-check
npm run lint
npm run build
```

Expected: 全部退出码为 0。

- [ ] **Step 3: 启动本地服务并验证匿名读取与受保护更新**

启动后端：

```powershell
cd backend
mvn -pl admin-boot -am spring-boot:run
```

在另一个终端验证匿名读取：

```powershell
Invoke-RestMethod -Method Get -Uri 'http://localhost:8180/api/system/config/public/login-pet'
```

Expected: HTTP 200，`data` 为 `cat`、`dog` 或 `owl`。

验证未登录更新被拒绝：

```powershell
try {
  Invoke-RestMethod -Method Put -Uri 'http://localhost:8180/api/system/config/login-pet' -ContentType 'application/json' -Body '{"type":"dog"}'
  throw '未认证更新不应成功'
} catch {
  if ($_.Exception.Response.StatusCode.value__ -notin @(401, 403)) { throw }
}
```

Expected: HTTP 401 或 403；数据库值不改变。

- [ ] **Step 4: 启动前端并执行浏览器手动检查**

Run:

```powershell
cd frontend
npm run dev
```

使用浏览器检查：

1. 登录页默认显示 108px 猫咪，`CXAdmin` 和说明文字位置稳定。
2. 鼠标移动到页面四个方向时，眼睛、头部和专属部位同向响应。
3. 鼠标离开或页面失焦后，宠物回到正面。
4. 猫咪尾巴、小狗尾巴或舌头、猫头鹰翅膀存在克制的低频动作。
5. 管理员打开主题设置时看到三个选项；普通用户看不到该区块。
6. 管理员依次选择小狗、猫头鹰、猫咪，刷新登录页后对应宠物生效。
7. 模拟 `prefers-reduced-motion: reduce` 后持续动作停止，方向反馈幅度降低。
8. 视口缩小到 960px 以下后左侧隐藏，登录表单仍完整可用。

- [ ] **Step 5: 检查本功能差异与无关修改隔离**

Run:

```powershell
git status --short
git diff --check
git log --oneline -8
```

Expected:

- 无空白错误。
- 本功能提交按任务拆分且可独立审阅。
- 原有 `application.yml`、`vite.config.ts` 和 `.git-rewrite/` 修改仍保持用户原状，没有被功能提交误带。

- [ ] **Step 6: 如验证产生修复，单独提交修复**

仅在 Step 1-5 发现并修复本功能问题时执行：

```powershell
git add -- database/init.sql backend/admin-system/src/main/java/com/admin/system/enums/LoginPetType.java backend/admin-system/src/main/java/com/admin/system/dto/LoginPetTypeDTO.java backend/admin-system/src/main/java/com/admin/system/service/ISysConfigService.java backend/admin-system/src/main/java/com/admin/system/service/impl/SysConfigServiceImpl.java backend/admin-system/src/main/java/com/admin/system/controller/SysConfigController.java backend/admin-system/src/test/java/com/admin/system/service/SysConfigServiceImplTest.java backend/admin-system/src/test/java/com/admin/system/controller/SysConfigControllerTest.java backend/admin-framework/src/main/java/com/admin/framework/config/SecurityConfig.java frontend/package.json frontend/package-lock.json frontend/vitest.config.ts frontend/src/types/login-pet.ts frontend/src/types/system/config.ts frontend/src/api/system/config.ts frontend/src/utils/request.ts frontend/src/composables/useLoginPetConfig.ts frontend/src/composables/useLoginPetConfig.spec.ts frontend/src/components/LoginPet frontend/src/components/ThemeSettings/index.vue frontend/src/components/ThemeSettings/index.spec.ts frontend/src/views/login/index.vue frontend/src/views/login/index.spec.ts
git commit -m "fix(login): stabilize dynamic pet"
```

若所有验证直接通过，不创建空提交。

---

## Completion Checklist

- [ ] `sys.login.pet.type` 默认种子为 `cat`。
- [ ] 匿名读取仅限 `GET /system/config/public/login-pet`。
- [ ] 更新端点强制校验 `system:config:edit` 和 `cat|dog|owl` 白名单。
- [ ] 登录页配置失败、缺失、非法时均显示猫咪且不阻塞登录。
- [ ] 三个 SVG 宠物在 108px 下清晰并跟随鼠标。
- [ ] 事件通过 `requestAnimationFrame` 合并，卸载时清理。
- [ ] 减少动态效果模式生效。
- [ ] 主题设置仅对授权管理员显示，保存失败可回滚。
- [ ] 960px 以下响应式行为不变。
- [ ] 前端测试、类型检查、Lint、构建全部通过。
- [ ] 后端完整测试通过。
- [ ] 浏览器与接口手动验收通过。

package com.admin.framework.aspect;

import com.admin.common.annotation.DataScope;
import com.admin.common.BaseEntity;
import com.admin.common.constants.SystemConstants;
import com.admin.system.entity.SysRole;
import com.admin.system.security.LoginUser;
import com.admin.system.security.SecurityUtils;
import com.admin.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据权限切面
 *
 * <p>拦截 {@link DataScope} 标注的方法，按当前用户角色的 {@code data_scope} 生成数据范围过滤
 * SQL 片段，注入入参 {@link BaseEntity} 子类对象的 {@code params.dataScope}。</p>
 *
 * <p><b>注入安全：</b>拼入的 dept_id / user_id 均来自已认证主体与角色配置中的数值型 {@code Long}，
 * 非外部输入，故 {@code ${...}} 插值不存在 SQL 注入风险；进入切面前会先清除入参中可能存在的
 * {@code dataScope} 键，杜绝客户端伪造。</p>
 *
 * @author Admin
 */
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {

    /** params 中承载过滤片段的键名 */
    public static final String DATA_SCOPE_PARAM = "dataScope";

    // ===== 数据权限取值（与 sys_role.data_scope 对应）=====
    /** 全部数据权限 */
    private static final String SCOPE_ALL = "1";
    /** 自定数据权限（sys_role_dept 指定部门） */
    private static final String SCOPE_CUSTOM = "2";
    /** 本部门数据权限 */
    private static final String SCOPE_DEPT = "3";
    /** 本部门及以下数据权限 */
    private static final String SCOPE_DEPT_AND_CHILD = "4";
    /** 仅本人数据权限 */
    private static final String SCOPE_SELF = "5";

    private final ISysRoleService roleService;

    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) {
        handleDataScope(point.getArgs(), dataScope.deptAlias(), dataScope.userAlias());
    }

    /**
     * 计算并注入数据范围过滤片段（包级可见，便于单测直接调用）。
     */
    void handleDataScope(Object[] args, String deptAlias, String userAlias) {
        BaseEntity carrier = firstBaseEntity(args);
        if (carrier == null) {
            return;
        }
        // 先清除，防止客户端通过 params 伪造注入
        carrier.getParams().remove(DATA_SCOPE_PARAM);
        carrier.getParams().put(DATA_SCOPE_PARAM, buildFilter(deptAlias, userAlias));
    }

    /**
     * 构造数据范围过滤片段：无限制返回空串，否则返回形如 {@code " AND (cond1 OR cond2)"}。
     */
    String buildFilter(String deptAlias, String userAlias) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 未登录（理论上列表接口已被鉴权拦截）或超级管理员：不附加任何限制
        if (loginUser == null || isSuperAdmin(loginUser)) {
            return "";
        }

        List<SysRole> roles = roleService.selectRolesByUserId(loginUser.getUserId());
        if (CollectionUtils.isEmpty(roles)) {
            // 无角色 → 看不到任何数据
            return " AND (1 = 0)";
        }

        Long deptId = loginUser.getDeptId();
        Long userId = loginUser.getUserId();
        // 用 Set 去重，避免多个角色相同范围产生重复片段
        Set<String> conditions = new LinkedHashSet<>();

        for (SysRole role : roles) {
            String scope = role.getDataScope();
            if (SCOPE_ALL.equals(scope)) {
                // 任一角色拥有全部数据权限 → 整体放行
                return "";
            } else if (SCOPE_CUSTOM.equals(scope)) {
                conditions.add(deptAlias + ".dept_id IN (SELECT dept_id FROM sys_role_dept WHERE role_id = "
                        + role.getRoleId() + ")");
            } else if (SCOPE_DEPT.equals(scope)) {
                if (deptId != null) {
                    conditions.add(deptAlias + ".dept_id = " + deptId);
                }
            } else if (SCOPE_DEPT_AND_CHILD.equals(scope)) {
                if (deptId != null) {
                    conditions.add(deptAlias + ".dept_id IN (SELECT dept_id FROM sys_dept WHERE dept_id = "
                            + deptId + " OR FIND_IN_SET(" + deptId + ", ancestors))");
                }
            } else if (SCOPE_SELF.equals(scope)) {
                if (userAlias != null && !userAlias.isEmpty()) {
                    if (userId != null) {
                        conditions.add(userAlias + ".user_id = " + userId);
                    }
                } else {
                    // 非用户表（如部门列表）没有"仅本人"维度 → 该角色看不到任何数据
                    conditions.add(deptAlias + ".dept_id = 0");
                }
            }
        }

        if (conditions.isEmpty()) {
            // 角色都是部门/自定义类型但数据缺失 → 看不到任何数据
            return " AND (1 = 0)";
        }
        return " AND (" + String.join(" OR ", conditions) + ")";
    }

    private BaseEntity firstBaseEntity(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof BaseEntity be) {
                return be;
            }
        }
        return null;
    }

    private boolean isSuperAdmin(LoginUser loginUser) {
        boolean isAdminId = loginUser.getUserId() != null && loginUser.getUserId() == SystemConstants.SUPER_ADMIN_ID;
        boolean isAdminName = SystemConstants.SUPER_ADMIN_USERNAME.equalsIgnoreCase(loginUser.getUsername());
        return isAdminId || isAdminName;
    }
}

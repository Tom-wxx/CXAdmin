package com.admin.system.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 *
 * <p>标注在 Service 方法上，由 {@code DataScopeAspect} 拦截：根据当前登录用户的角色
 * {@code data_scope} 配置，动态拼装数据范围过滤 SQL 片段，写入入参中 {@code BaseEntity}
 * 子类对象的 {@code params.dataScope}，再由对应 Mapper XML 以
 * {@code ${query.params.dataScope}} 拼接到查询。</p>
 *
 * <p>扩展到其它列表查询：在 Service 方法加本注解 + 让入参携带 {@code BaseEntity} 子类对象
 * + 在 Mapper XML 末尾追加 {@code ${...params.dataScope}} 即可。</p>
 *
 * @author Admin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门表别名（用于 dept_id 维度过滤）
     */
    String deptAlias() default "";

    /**
     * 用户表别名（用于"仅本人"维度过滤 user_id）
     */
    String userAlias() default "";
}

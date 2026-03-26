package com.admin.system.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Admin
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块名称
     */
    String title() default "";

    /**
     * 业务类型（0其它 1新增 2修改 3删除 4查询 5导出 6导入）
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 是否保存请求参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应参数
     */
    boolean isSaveResponseData() default true;

    /**
     * 业务类型枚举
     */
    enum BusinessType {
        /**
         * 其它
         */
        OTHER,

        /**
         * 新增
         */
        INSERT,

        /**
         * 修改
         */
        UPDATE,

        /**
         * 删除
         */
        DELETE,

        /**
         * 查询
         */
        SELECT,

        /**
         * 导出
         */
        EXPORT,

        /**
         * 导入
         */
        IMPORT,

        /**
         * 清空
         */
        CLEAN
    }
}

package com.admin.system.generator;

import lombok.Data;

/**
 * 代码生成配置
 *
 * @author Admin
 */
@Data
public class GenConfig {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 包名
     */
    private String packageName = "com.admin.system";

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 作者
     */
    private String author = "Admin";

    /**
     * 表前缀（生成时会去掉）
     */
    private String tablePrefix = "sys_";

    /**
     * 父菜单ID（用于生成菜单SQL）
     */
    private Long parentMenuId = 0L;

    /**
     * 自定义表信息（用于新增表生成代码）
     */
    private TableInfo tableInfo;
}

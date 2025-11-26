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
}

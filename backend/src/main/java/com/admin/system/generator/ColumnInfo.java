package com.admin.system.generator;

import lombok.Data;

/**
 * 数据库列信息
 *
 * @author Admin
 */
@Data
public class ColumnInfo {

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * 列注释
     */
    private String columnComment;

    /**
     * 是否主键
     */
    private Boolean isPrimaryKey;

    /**
     * 是否必填
     */
    private Boolean isRequired;

    /**
     * Java属性名（小驼峰）
     */
    private String propertyName;

    /**
     * Java类型
     */
    private String javaType;
}

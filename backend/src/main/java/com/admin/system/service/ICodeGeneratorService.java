package com.admin.system.service;

import com.admin.system.common.PageResult;
import com.admin.system.generator.GenConfig;
import com.admin.system.generator.TableInfo;

import java.util.List;

/**
 * 代码生成器Service接口
 *
 * @author Admin
 */
public interface ICodeGeneratorService {

    /**
     * 查询数据库表列表
     *
     * @param tableName 表名
     * @return 表信息列表
     */
    List<TableInfo> getTableList(String tableName);

    /**
     * 查询数据库表列表（分页）
     *
     * @param tableName 表名
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<TableInfo> getTableListPage(String tableName, Integer current, Integer size);

    /**
     * 根据表名查询表信息
     *
     * @param tableName 表名
     * @return 表信息
     */
    TableInfo getTableInfo(String tableName);

    /**
     * 生成代码（返回ZIP字节数组）
     *
     * @param tableName 表名
     * @param config 生成配置
     * @return ZIP字节数组
     */
    byte[] generateCode(String tableName, GenConfig config);

    /**
     * 根据自定义表信息生成代码
     *
     * @param tableInfo 自定义表信息
     * @param config 生成配置
     * @return ZIP字节数组
     */
    byte[] generateCustomCode(TableInfo tableInfo, GenConfig config);
}

package com.admin.system.service.impl;

import com.admin.system.common.PageResult;
import com.admin.system.generator.CodeGeneratorUtil;
import com.admin.system.generator.ColumnInfo;
import com.admin.system.generator.GenConfig;
import com.admin.system.generator.TableInfo;
import com.admin.system.service.ICodeGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器Service实现类
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class CodeGeneratorServiceImpl implements ICodeGeneratorService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TableInfo> getTableList(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT table_name, table_comment, create_time, update_time ");
        sql.append("FROM information_schema.tables ");
        sql.append("WHERE table_schema = (SELECT DATABASE()) ");
        sql.append("AND table_name NOT LIKE 'qrtz_%' ");

        List<Object> params = new ArrayList<>();
        if (StringUtils.hasText(tableName)) {
            sql.append("AND table_name LIKE ? ");
            params.add("%" + tableName + "%");
        }

        sql.append("ORDER BY create_time DESC");

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        List<TableInfo> tableList = new ArrayList<>();

        for (Map<String, Object> row : results) {
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName((String) row.get("table_name"));
            tableInfo.setTableComment((String) row.get("table_comment"));
            tableList.add(tableInfo);
        }

        return tableList;
    }

    @Override
    public PageResult<TableInfo> getTableListPage(String tableName, Integer current, Integer size) {
        // 构建查询条件
        StringBuilder whereSql = new StringBuilder();
        whereSql.append("WHERE table_schema = (SELECT DATABASE()) ");
        whereSql.append("AND table_name NOT LIKE 'qrtz_%' ");

        List<Object> countParams = new ArrayList<>();
        if (StringUtils.hasText(tableName)) {
            whereSql.append("AND table_name LIKE ? ");
            countParams.add("%" + tableName + "%");
        }

        // 查询总数
        String countSql = "SELECT COUNT(*) FROM information_schema.tables " + whereSql;
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, countParams.toArray());

        // 分页查询
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT table_name, table_comment, create_time, update_time ");
        sql.append("FROM information_schema.tables ");
        sql.append(whereSql);
        sql.append("ORDER BY create_time DESC ");
        sql.append("LIMIT ? OFFSET ?");

        int offset = (current - 1) * size;
        List<Object> queryParams = new ArrayList<>(countParams);
        queryParams.add(size);
        queryParams.add(offset);

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        List<TableInfo> tableList = new ArrayList<>();

        for (Map<String, Object> row : results) {
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName((String) row.get("table_name"));
            tableInfo.setTableComment((String) row.get("table_comment"));
            tableList.add(tableInfo);
        }

        return PageResult.build(tableList, total, current, size);
    }

    @Override
    public TableInfo getTableInfo(String tableName) {
        // 查询表信息
        String tableSql = "SELECT table_name, table_comment FROM information_schema.tables " +
                         "WHERE table_schema = (SELECT DATABASE()) AND table_name = ?";
        Map<String, Object> tableMap = jdbcTemplate.queryForMap(tableSql, tableName);

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setTableComment((String) tableMap.get("table_comment"));

        // 查询列信息
        String columnSql = "SELECT column_name, data_type, column_comment, column_key, is_nullable " +
                          "FROM information_schema.columns " +
                          "WHERE table_schema = (SELECT DATABASE()) AND table_name = ? " +
                          "ORDER BY ordinal_position";
        List<Map<String, Object>> columnList = jdbcTemplate.queryForList(columnSql, tableName);

        List<ColumnInfo> columns = new ArrayList<>();
        for (Map<String, Object> columnMap : columnList) {
            ColumnInfo column = new ColumnInfo();
            String columnName = (String) columnMap.get("column_name");
            String dataType = (String) columnMap.get("data_type");
            String columnKey = (String) columnMap.get("column_key");
            String isNullable = (String) columnMap.get("is_nullable");

            column.setColumnName(columnName);
            column.setColumnType(dataType);
            column.setColumnComment((String) columnMap.get("column_comment"));
            column.setIsPrimaryKey("PRI".equals(columnKey));
            column.setIsRequired("NO".equals(isNullable));
            String propName = CodeGeneratorUtil.columnToProperty(columnName);
            column.setPropertyName(propName);
            column.setCapitalizedPropertyName(propName.substring(0, 1).toUpperCase() + propName.substring(1));
            column.setJavaType(CodeGeneratorUtil.mysqlTypeToJavaType(dataType));

            columns.add(column);

            // 设置主键
            if (column.getIsPrimaryKey()) {
                tableInfo.setPrimaryKey(column);
            }
        }

        tableInfo.setColumns(columns);

        return tableInfo;
    }

    @Override
    public byte[] generateCode(String tableName, GenConfig config) {
        try {
            // 获取表信息
            TableInfo tableInfo = getTableInfo(tableName);

            // 生成类名
            String className = CodeGeneratorUtil.tableToClassName(tableName, config.getTablePrefix());
            tableInfo.setClassName(className);
            tableInfo.setClassname(className.substring(0, 1).toLowerCase() + className.substring(1));

            // 生成代码
            return CodeGeneratorUtil.generateCode(tableInfo, config);
        } catch (IOException e) {
            throw new RuntimeException("代码生成失败", e);
        }
    }

    @Override
    public byte[] generateCustomCode(TableInfo tableInfo, GenConfig config) {
        try {
            // 生成类名
            String className = CodeGeneratorUtil.tableToClassName(tableInfo.getTableName(), config.getTablePrefix());
            tableInfo.setClassName(className);
            tableInfo.setClassname(className.substring(0, 1).toLowerCase() + className.substring(1));

            // 生成代码
            return CodeGeneratorUtil.generateCode(tableInfo, config);
        } catch (IOException e) {
            throw new RuntimeException("代码生成失败", e);
        }
    }
}

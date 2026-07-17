package com.admin.system.generator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成工具类
 *
 * @author Admin
 */
public class CodeGeneratorUtil {

    /**
     * 生成代码（返回ZIP字节数组）
     */
    public static byte[] generateCode(TableInfo tableInfo, GenConfig config) throws IOException {
        Properties prop = new Properties();
        prop.put("resource.loader", "class");
        prop.put("resource.loader.class.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        Map<String, Object> map = prepareContext(tableInfo, config);
        VelocityContext context = new VelocityContext(map);

        List<String> templates = getTemplates();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for (String templateName : templates) {
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(templateName, "UTF-8");
            tpl.merge(context, sw);

            String fileName = getFileName(templateName, tableInfo.getClassName(), config.getModuleName());
            zip.putNextEntry(new ZipEntry(fileName));
            zip.write(sw.toString().getBytes("UTF-8"));
            zip.closeEntry();
        }

        zip.close();
        return outputStream.toByteArray();
    }

    /**
     * 准备模板上下文
     */
    private static Map<String, Object> prepareContext(TableInfo tableInfo, GenConfig config) {
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableInfo.getTableName());
        map.put("tableComment", tableInfo.getTableComment() != null ? tableInfo.getTableComment() : tableInfo.getClassName());
        map.put("className", tableInfo.getClassName());
        map.put("classname", tableInfo.getClassname());
        map.put("columns", tableInfo.getColumns());
        map.put("primaryKey", tableInfo.getPrimaryKey());
        map.put("packageName", config.getPackageName());
        map.put("moduleName", config.getModuleName());
        map.put("author", config.getAuthor());
        map.put("datetime", new Date());
        // 生成菜单ID (基于时间戳后6位)
        map.put("menuId", System.currentTimeMillis() % 1000000);
        map.put("parentMenuId", config.getParentMenuId() != null ? config.getParentMenuId() : 0);
        return map;
    }

    /**
     * 获取模板列表
     */
    private static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("templates/Entity.java.vm");
        templates.add("templates/Mapper.java.vm");
        templates.add("templates/Mapper.xml.vm");
        templates.add("templates/Service.java.vm");
        templates.add("templates/ServiceImpl.java.vm");
        templates.add("templates/Controller.java.vm");
        templates.add("templates/vue-index.vue.vm");
        templates.add("templates/api.js.vm");
        templates.add("templates/sql.vm");
        return templates;
    }

    /**
     * 获取文件名
     */
    private static String getFileName(String template, String className, String moduleName) {
        String packagePath = "backend/src/main/java/com/admin/system/";
        String resourcesPath = "backend/src/main/resources/";
        String vuePath = "frontend/src/views/" + moduleName + "/";
        String apiPath = "frontend/src/api/" + moduleName + "/";
        String classname = className.substring(0, 1).toLowerCase() + className.substring(1);

        if (template.contains("Entity.java.vm")) {
            return packagePath + "entity/" + className + ".java";
        }

        if (template.contains("Mapper.java.vm")) {
            return packagePath + "mapper/" + className + "Mapper.java";
        }

        if (template.contains("Mapper.xml.vm")) {
            return resourcesPath + "mapper/" + className + "Mapper.xml";
        }

        if (template.contains("Service.java.vm")) {
            return packagePath + "service/I" + className + "Service.java";
        }

        if (template.contains("ServiceImpl.java.vm")) {
            return packagePath + "service/impl/" + className + "ServiceImpl.java";
        }

        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller/" + className + "Controller.java";
        }

        if (template.contains("vue-index.vue.vm")) {
            return vuePath + classname + "/index.vue";
        }

        if (template.contains("api.js.vm")) {
            return apiPath + classname + ".js";
        }

        if (template.contains("sql.vm")) {
            return "sql/" + classname + "_menu.sql";
        }

        return null;
    }

    /**
     * 列名转换成Java属性名（驼峰命名）
     */
    public static String columnToProperty(String columnName) {
        StringBuilder result = new StringBuilder();
        String[] parts = columnName.toLowerCase().split("_");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                result.append(parts[i]);
            } else {
                result.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
            }
        }
        return result.toString();
    }

    /**
     * 表名转换成类名
     */
    public static String tableToClassName(String tableName, String tablePrefix) {
        if (tableName.startsWith(tablePrefix)) {
            tableName = tableName.substring(tablePrefix.length());
        }
        StringBuilder result = new StringBuilder();
        String[] parts = tableName.toLowerCase().split("_");
        for (String part : parts) {
            result.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
        }
        return result.toString();
    }

    /**
     * MySQL类型转Java类型
     */
    public static String mysqlTypeToJavaType(String mysqlType) {
        String type = mysqlType.toLowerCase();
        if (type.contains("bigint")) {
            return "Long";
        } else if (type.contains("int")) {
            return "Integer";
        }
        else if (type.contains("decimal") || type.contains("numeric")) {
            return "BigDecimal";
        } else if (type.contains("double")) {
            return "Double";
        } else if (type.contains("float")) {
            return "Float";
        }
        else if (type.contains("varchar") || type.contains("text") || type.contains("char") || type.contains("json")) {
            return "String";
        }
        else if (type.contains("datetime") || type.contains("timestamp") || type.contains("date") || type.contains("time")) {
            return "Date";
        }
        else if (type.contains("blob") || type.contains("binary")) {
            return "byte[]";
        }
        else if (type.contains("bit") || type.contains("bool")) {
            return "Boolean";
        }
        else {
            return "String";
        }
    }
}

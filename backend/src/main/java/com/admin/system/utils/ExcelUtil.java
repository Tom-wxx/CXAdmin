package com.admin.system.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Excel工具类
 *
 * @author Admin
 */
public class ExcelUtil {

    /**
     * 导出Excel
     *
     * @param response 响应对象
     * @param fileName 文件名
     * @param headers  表头
     * @param fields   字段名
     * @param data     数据列表
     * @param <T>      数据类型
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName,
                                        String[] headers, String[] fields, List<T> data) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // 创建表头样式
        CellStyle headerStyle = createHeaderStyle(workbook);

        // 创建数据样式
        CellStyle dataStyle = createDataStyle(workbook);

        // 创建表头行
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 20 * 256);
        }

        // 创建数据行
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            T item = data.get(i);

            for (int j = 0; j < fields.length; j++) {
                Cell cell = row.createCell(j);
                Object value = getFieldValue(item, fields[j]);

                if (value == null) {
                    cell.setCellValue("");
                } else if (value instanceof Date) {
                    cell.setCellValue(sdf.format((Date) value));
                } else if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    cell.setCellValue(value.toString());
                }
                cell.setCellStyle(dataStyle);
            }
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName + ".xlsx");

        // 写入响应流
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // 设置背景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 设置居中
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 设置居中
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * 获取对象字段值
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            // 支持嵌套属性，如 "dept.deptName"
            String[] fields = fieldName.split("\\.");
            Object value = obj;

            for (String field : fields) {
                if (value == null) {
                    return null;
                }
                Field f = getField(value.getClass(), field);
                if (f != null) {
                    f.setAccessible(true);
                    value = f.get(value);
                } else {
                    return null;
                }
            }
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取字段（包括父类）
     */
    private static Field getField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 生成导入模板
     */
    public static void exportTemplate(HttpServletResponse response, String fileName,
                                       String[] headers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // 创建表头样式
        CellStyle headerStyle = createHeaderStyle(workbook);

        // 创建表头行
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 20 * 256);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName + ".xlsx");

        // 写入响应流
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
    }
}

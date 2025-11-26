package com.admin.system.utils;

import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件工具类
 *
 * @author Admin
 */
public class FileUtil {

    /**
     * 文件类型映射
     */
    private static final Map<String, String> FILE_CATEGORY_MAP = new HashMap<>();

    static {
        // 图片
        FILE_CATEGORY_MAP.put("jpg", "image");
        FILE_CATEGORY_MAP.put("jpeg", "image");
        FILE_CATEGORY_MAP.put("png", "image");
        FILE_CATEGORY_MAP.put("gif", "image");
        FILE_CATEGORY_MAP.put("bmp", "image");
        FILE_CATEGORY_MAP.put("webp", "image");
        FILE_CATEGORY_MAP.put("svg", "image");

        // 文档
        FILE_CATEGORY_MAP.put("doc", "document");
        FILE_CATEGORY_MAP.put("docx", "document");
        FILE_CATEGORY_MAP.put("xls", "document");
        FILE_CATEGORY_MAP.put("xlsx", "document");
        FILE_CATEGORY_MAP.put("ppt", "document");
        FILE_CATEGORY_MAP.put("pptx", "document");
        FILE_CATEGORY_MAP.put("pdf", "document");
        FILE_CATEGORY_MAP.put("txt", "document");

        // 视频
        FILE_CATEGORY_MAP.put("mp4", "video");
        FILE_CATEGORY_MAP.put("avi", "video");
        FILE_CATEGORY_MAP.put("mov", "video");
        FILE_CATEGORY_MAP.put("wmv", "video");
        FILE_CATEGORY_MAP.put("flv", "video");
        FILE_CATEGORY_MAP.put("mkv", "video");

        // 音频
        FILE_CATEGORY_MAP.put("mp3", "audio");
        FILE_CATEGORY_MAP.put("wav", "audio");
        FILE_CATEGORY_MAP.put("flac", "audio");
        FILE_CATEGORY_MAP.put("aac", "audio");
        FILE_CATEGORY_MAP.put("wma", "audio");

        // 压缩文件
        FILE_CATEGORY_MAP.put("zip", "archive");
        FILE_CATEGORY_MAP.put("rar", "archive");
        FILE_CATEGORY_MAP.put("7z", "archive");
        FILE_CATEGORY_MAP.put("tar", "archive");
        FILE_CATEGORY_MAP.put("gz", "archive");
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 根据文件扩展名获取文件分类
     */
    public static String getFileCategory(String fileExt) {
        return FILE_CATEGORY_MAP.getOrDefault(fileExt.toLowerCase(), "other");
    }

    /**
     * 生成唯一文件名
     */
    public static String generateFileName(String originalFileName) {
        String ext = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return ext.isEmpty() ? uuid : uuid + "." + ext;
    }

    /**
     * 生成按日期分类的文件路径（用于文件系统）
     * Windows: 2025\\11\\26\\uuid.jpg
     * Linux: 2025/11/26/uuid.jpg
     */
    public static String generateDatePathForFile(String fileName) {
        LocalDate now = LocalDate.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy" + File.separator + "MM" + File.separator + "dd"));
        return datePath + File.separator + fileName;
    }

    /**
     * 生成按日期分类的URL路径（用于URL访问）
     * 所有系统统一使用: 2025/11/26/uuid.jpg
     */
    public static String generateDatePathForUrl(String fileName) {
        LocalDate now = LocalDate.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return datePath + "/" + fileName;
    }

    /**
     * 计算文件MD5
     */
    public static String calculateMD5(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return DigestUtils.md5DigestAsHex(fis);
        }
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 创建目录（如果不存在）
     */
    public static boolean createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 检查文件是否存在
     */
    public static boolean fileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
}

package com.admin.system.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件上传配置
 *
 * @author Admin
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig implements WebMvcConfigurer {

    /**
     * 文件上传路径
     */
    private String path = "uploads";

    /**
     * 文件访问前缀
     */
    private String prefix = "/uploads";

    /**
     * 服务器 context-path
     */
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 最大文件大小（MB）
     */
    private Integer maxSize = 100;

    /**
     * 允许的文件类型（逗号分隔）
     */
    private String allowedTypes = "jpg,jpeg,png,gif,bmp,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,zip,rar";

    /**
     * 配置静态资源访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件访问路径映射（Spring资源路径使用URL格式）
        registry.addResourceHandler(prefix + "/**")
                .addResourceLocations("file:" + getAbsolutePathForUrl() + "/");
    }

    /**
     * 获取上传文件的绝对路径（文件系统路径，使用系统分隔符）
     * Windows: D:\\uploads
     * Linux: /var/uploads
     */
    public String getAbsolutePath() {
        String absolutePath;
        // 如果是相对路径，转换为绝对路径
        if (!path.startsWith("/") && !path.contains(":")) {
            absolutePath = System.getProperty("user.dir") + java.io.File.separator + path;
        } else {
            absolutePath = path;
        }
        // 标准化路径分隔符为系统分隔符
        return absolutePath.replace("/", java.io.File.separator)
                           .replace("\\", java.io.File.separator);
    }

    /**
     * 获取上传文件的绝对路径（URL格式，统一使用 /）
     * 用于Spring资源映射
     */
    public String getAbsolutePathForUrl() {
        String absolutePath;
        if (!path.startsWith("/") && !path.contains(":")) {
            absolutePath = System.getProperty("user.dir") + "/" + path;
        } else {
            absolutePath = path;
        }
        // URL格式统一使用正斜杠
        return absolutePath.replace("\\", "/");
    }

    /**
     * 检查文件类型是否允许
     */
    public boolean isAllowedType(String fileExt) {
        if (allowedTypes == null || allowedTypes.isEmpty() || "*".equals(allowedTypes)) {
            return true;
        }
        String[] types = allowedTypes.split(",");
        for (String type : types) {
            if (type.trim().equalsIgnoreCase(fileExt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查文件大小是否超限
     */
    public boolean isAllowedSize(long fileSize) {
        long maxSizeBytes = maxSize * 1024L * 1024L;
        return fileSize <= maxSizeBytes;
    }

    /**
     * 获取完整的访问URL前缀（包含context-path）
     * 用于生成可访问的文件URL
     */
    public String getFullPrefix() {
        if (contextPath == null || contextPath.isEmpty()) {
            return prefix;
        }
        // 确保 context-path 不以 / 结尾，prefix 以 / 开头
        String ctx = contextPath.endsWith("/") ? contextPath.substring(0, contextPath.length() - 1) : contextPath;
        return ctx + prefix;
    }
}

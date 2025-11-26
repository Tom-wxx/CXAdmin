package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysFile;
import com.admin.system.service.ISysFileService;
import com.admin.system.utils.FileUtil;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理控制器
 *
 * @author Admin
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/system/file")
public class SysFileController {

    @Autowired
    private ISysFileService fileService;

    /**
     * 分页查询文件列表
     */
    @ApiOperation("分页查询文件列表")
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/list")
    public PageResult<SysFile> list(
            PageQuery pageQuery,
            @ApiParam("文件名") @RequestParam(required = false) String fileName,
            @ApiParam("文件分类") @RequestParam(required = false) String category,
            @ApiParam("文件类型") @RequestParam(required = false) String fileExt
    ) {
        // 使用 PageQuery 快速构建分页对象
        Page<SysFile> page = pageQuery.build();

        // 构建查询条件
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(fileName != null && !fileName.isEmpty(), SysFile::getOriginalName, fileName)
                .eq(category != null && !category.isEmpty(), SysFile::getCategory, category)
                .eq(fileExt != null && !fileExt.isEmpty(), SysFile::getFileExt, fileExt)
                .orderByDesc(SysFile::getCreateTime);

        // 执行分页查询
        Page<SysFile> result = fileService.page(page, wrapper);

        // 使用工具类转换结果
        return PageUtils.buildPageResult(result);
    }

    /**
     * 获取文件详情
     */
    @ApiOperation("获取文件详情")
    @PreAuthorize("@ss.hasPermi('system:file:query')")
    @GetMapping("/{fileId}")
    public Result<SysFile> getInfo(@PathVariable Long fileId) {
        SysFile sysFile = fileService.getById(fileId);
        return Result.success(sysFile);
    }

    /**
     * 上传文件
     */
    @ApiOperation("上传文件")
    @PreAuthorize("@ss.hasPermi('system:file:upload')")
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(
            @ApiParam("文件") @RequestParam("file") MultipartFile file
    ) {
        try {
            SysFile sysFile = fileService.uploadFile(file);

            Map<String, Object> result = new HashMap<>();
            result.put("fileId", sysFile.getFileId());
            result.put("fileName", sysFile.getFileName());
            result.put("originalName", sysFile.getOriginalName());
            result.put("fileUrl", sysFile.getFileUrl());
            result.put("fileSize", sysFile.getFileSize());
            result.put("fileSizeText", FileUtil.formatFileSize(sysFile.getFileSize()));
            result.put("fileType", sysFile.getFileType());
            result.put("category", sysFile.getCategory());

            return Result.success("文件上传成功", result);
        } catch (Exception e) {
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 批量上传文件
     */
    @ApiOperation("批量上传文件")
    @PreAuthorize("@ss.hasPermi('system:file:upload')")
    @PostMapping("/upload/batch")
    public Result<Map<String, Object>> batchUpload(
            @ApiParam("文件列表") @RequestParam("files") MultipartFile[] files
    ) {
        try {
            int successCount = 0;
            int failCount = 0;
            Map<String, Object> result = new HashMap<>();

            for (MultipartFile file : files) {
                try {
                    fileService.uploadFile(file);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                }
            }

            result.put("total", files.length);
            result.put("successCount", successCount);
            result.put("failCount", failCount);

            return Result.success("批量上传完成", result);
        } catch (Exception e) {
            return Result.fail("批量上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    @ApiOperation("下载文件")
    @PreAuthorize("@ss.hasPermi('system:file:download')")
    @GetMapping("/download/{fileId}")
    public void download(@PathVariable Long fileId, HttpServletResponse response) {
        try {
            fileService.downloadFile(fileId, response);
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @ApiOperation("删除文件")
    @PreAuthorize("@ss.hasPermi('system:file:remove')")
    @DeleteMapping("/{fileIds}")
    public Result<Void> delete(@PathVariable Long[] fileIds) {
        boolean success = fileService.batchDeleteFiles(fileIds);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * 获取文件统计信息
     */
    @ApiOperation("获取文件统计信息")
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = fileService.getFileStatistics();

        // 格式化文件大小
        formatSize(statistics, "total_size");
        formatSize(statistics, "image_size");
        formatSize(statistics, "document_size");
        formatSize(statistics, "video_size");
        formatSize(statistics, "audio_size");
        formatSize(statistics, "other_size");

        return Result.success(statistics);
    }

    /**
     * 格式化文件大小
     */
    private void formatSize(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value != null) {
            long size = ((Number) value).longValue();
            map.put(key + "_text", FileUtil.formatFileSize(size));
        }
    }
}

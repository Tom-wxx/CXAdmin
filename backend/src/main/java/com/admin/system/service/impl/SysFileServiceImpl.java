package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.config.FileUploadConfig;
import com.admin.system.entity.SysFile;
import com.admin.system.mapper.SysFileMapper;
import com.admin.system.security.SecurityUtils;
import com.admin.system.service.ISysFileService;
import com.admin.system.utils.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 文件服务实现
 *
 * @author Admin
 */
@Slf4j
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile uploadFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("文件不能为空");
        }

        // 获取原始文件名
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new ServiceException("文件名不能为空");
        }

        // 获取文件扩展名
        String fileExt = FileUtil.getFileExtension(originalFileName);

        // 检查文件类型是否允许
        if (!fileUploadConfig.isAllowedType(fileExt)) {
            throw new ServiceException("不支持的文件类型: " + fileExt);
        }

        // 检查文件大小
        if (!fileUploadConfig.isAllowedSize(file.getSize())) {
            throw new ServiceException("文件大小超过限制: " +
                    FileUtil.formatFileSize(fileUploadConfig.getMaxSize() * 1024L * 1024L));
        }

        // 生成新文件名
        String newFileName = FileUtil.generateFileName(originalFileName);

        // 生成文件系统路径（按日期分类，使用系统分隔符）
        String relativeFilePath = FileUtil.generateDatePathForFile(newFileName);

        // 生成URL路径（按日期分类，使用 / 分隔符）
        String relativeUrlPath = FileUtil.generateDatePathForUrl(newFileName);

        // 完整的文件系统路径
        String uploadPath = fileUploadConfig.getAbsolutePath();
        String fullPath = uploadPath + File.separator + relativeFilePath;

        // 输出日志，帮助调试
        log.info("=== 文件上传路径信息 ===");
        log.info("配置的上传路径: {}", fileUploadConfig.getPath());
        log.info("绝对上传路径: {}", uploadPath);
        log.info("相对文件路径: {}", relativeFilePath);
        log.info("相对URL路径: {}", relativeUrlPath);
        log.info("完整文件路径: {}", fullPath);

        // 创建目录
        File destFile = new File(fullPath);
        log.info("目标文件对象: {}", destFile.getAbsolutePath());
        log.info("父目录是否存在: {}", destFile.getParentFile().exists());

        if (!destFile.getParentFile().exists()) {
            boolean created = destFile.getParentFile().mkdirs();
            log.info("创建父目录: {}, 结果: {}", destFile.getParentFile().getAbsolutePath(), created);
        }

        // 保存文件
        log.info("开始保存文件到: {}", destFile.getAbsolutePath());
        file.transferTo(destFile);
        log.info("文件保存成功，文件大小: {} bytes", destFile.length());

        // 计算MD5
        String md5 = FileUtil.calculateMD5(destFile);

        // 检查是否已存在相同MD5的文件
        SysFile existingFile = lambdaQuery()
                .eq(SysFile::getMd5, md5)
                .one();

        if (existingFile != null) {
            // 检查旧文件的物理文件是否真实存在
            boolean oldFileExists = FileUtil.fileExists(existingFile.getFilePath());

            if (oldFileExists) {
                // 旧文件存在，删除新上传的重复文件
                log.info("文件已存在且物理文件存在，使用已有文件: {}", existingFile.getFileName());
                FileUtil.deleteFile(fullPath);
                return existingFile;
            } else {
                // 旧文件记录存在但物理文件已丢失，删除旧记录，使用新文件
                log.warn("数据库中存在相同MD5的记录，但物理文件不存在，删除旧记录: {}", existingFile.getFileName());
                removeById(existingFile.getFileId());
                // 继续保存新文件记录
            }
        }

        // 创建文件记录
        SysFile sysFile = new SysFile();
        sysFile.setFileName(newFileName);
        sysFile.setOriginalName(originalFileName);
        sysFile.setFilePath(fullPath);
        sysFile.setFileUrl(fileUploadConfig.getPrefix() + "/" + relativeUrlPath);
        sysFile.setFileSize(file.getSize());
        sysFile.setFileType(file.getContentType());
        sysFile.setFileExt(fileExt);
        sysFile.setCategory(FileUtil.getFileCategory(fileExt));
        sysFile.setStorageType("local");
        sysFile.setMd5(md5);
        sysFile.setDownloadCount(0);
        sysFile.setStatus("0");
        sysFile.setCreateBy(SecurityUtils.getUsername());

        // 保存到数据库
        save(sysFile);

        log.info("文件上传成功: {}, 大小: {}", originalFileName, FileUtil.formatFileSize(file.getSize()));

        return sysFile;
    }

    @Override
    public void downloadFile(Long fileId, HttpServletResponse response) throws Exception {
        // 查询文件信息
        SysFile sysFile = getById(fileId);
        if (sysFile == null) {
            throw new ServiceException("文件不存在");
        }

        // 检查文件是否存在
        File file = new File(sysFile.getFilePath());
        if (!file.exists()) {
            throw new ServiceException("文件已被删除");
        }

        // 更新下载次数
        lambdaUpdate()
                .eq(SysFile::getFileId, fileId)
                .setSql("download_count = download_count + 1")
                .update();

        // 设置响应头
        response.setContentType(sysFile.getFileType());
        response.setContentLengthLong(sysFile.getFileSize());

        // 设置文件名（支持中文）
        String encodedFileName = URLEncoder.encode(sysFile.getOriginalName(), "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);

        // 输出文件流
        try (InputStream is = Files.newInputStream(file.toPath());
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        }

        log.info("文件下载: {}", sysFile.getOriginalName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId) {
        SysFile sysFile = getById(fileId);
        if (sysFile == null) {
            return false;
        }

        // 删除物理文件
        boolean fileDeleted = FileUtil.deleteFile(sysFile.getFilePath());
        if (!fileDeleted) {
            log.warn("物理文件删除失败: {}", sysFile.getFilePath());
        }

        // 删除数据库记录（逻辑删除）
        boolean dbDeleted = removeById(fileId);

        log.info("文件删除: {}, 物理删除: {}, 数据库删除: {}",
                sysFile.getFileName(), fileDeleted, dbDeleted);

        return dbDeleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteFiles(Long[] fileIds) {
        if (fileIds == null || fileIds.length == 0) {
            return false;
        }

        for (Long fileId : fileIds) {
            deleteFile(fileId);
        }

        return true;
    }

    @Override
    public Map<String, Object> getFileStatistics() {
        return baseMapper.getFileStatistics();
    }
}

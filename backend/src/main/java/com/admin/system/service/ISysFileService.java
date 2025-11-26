package com.admin.system.service;

import com.admin.system.entity.SysFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 文件服务接口
 *
 * @author Admin
 */
public interface ISysFileService extends IService<SysFile> {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    SysFile uploadFile(MultipartFile file) throws Exception;

    /**
     * 下载文件
     *
     * @param fileId   文件ID
     * @param response HTTP响应
     */
    void downloadFile(Long fileId, HttpServletResponse response) throws Exception;

    /**
     * 删除文件（包括物理文件）
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    boolean deleteFile(Long fileId);

    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID数组
     * @return 是否成功
     */
    boolean batchDeleteFiles(Long[] fileIds);

    /**
     * 获取文件统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getFileStatistics();
}

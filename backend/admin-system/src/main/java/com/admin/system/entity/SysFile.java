package com.admin.system.entity;

import com.admin.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件信息实体
 *
 * @author Admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    private String fileName;

    private String originalName;

    private String filePath;

    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME类型）
     */
    private String fileType;

    private String fileExt;

    /**
     * 文件分类（image/document/video/audio/other）
     */
    private String category;

    /**
     * 存储类型（local/oss/cos等）
     */
    private String storageType;

    private String md5;

    private Integer downloadCount;

    /**
     * 状态（0正常 1禁用）
     */
    private String status;
}

package com.admin.system.mapper;

import com.admin.system.entity.SysFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 文件信息 Mapper
 *
 * @author Admin
 */
@Mapper
public interface SysFileMapper extends BaseMapper<SysFile> {

    /**
     * 统计各类型文件数量和大小
     */
    @Select("SELECT " +
            "COUNT(*) as total_files, " +
            "COALESCE(SUM(file_size), 0) as total_size, " +
            "SUM(CASE WHEN category = 'image' THEN 1 ELSE 0 END) as image_count, " +
            "SUM(CASE WHEN category = 'image' THEN file_size ELSE 0 END) as image_size, " +
            "SUM(CASE WHEN category = 'document' THEN 1 ELSE 0 END) as document_count, " +
            "SUM(CASE WHEN category = 'document' THEN file_size ELSE 0 END) as document_size, " +
            "SUM(CASE WHEN category = 'video' THEN 1 ELSE 0 END) as video_count, " +
            "SUM(CASE WHEN category = 'video' THEN file_size ELSE 0 END) as video_size, " +
            "SUM(CASE WHEN category = 'audio' THEN 1 ELSE 0 END) as audio_count, " +
            "SUM(CASE WHEN category = 'audio' THEN file_size ELSE 0 END) as audio_size, " +
            "SUM(CASE WHEN category NOT IN ('image', 'document', 'video', 'audio') THEN 1 ELSE 0 END) as other_count, " +
            "SUM(CASE WHEN category NOT IN ('image', 'document', 'video', 'audio') THEN file_size ELSE 0 END) as other_size " +
            "FROM sys_file WHERE deleted = 0")
    Map<String, Object> getFileStatistics();
}

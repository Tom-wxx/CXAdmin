package com.admin.system.mapper;

import com.admin.system.entity.SysConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 参数配置表 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 分页查询参数配置列表
     */
    Page<SysConfig> selectConfigPage(Page<SysConfig> page,
                                      @Param("configName") String configName,
                                      @Param("configKey") String configKey,
                                      @Param("configType") String configType);

    /**
     * 根据参数键名查询参数配置
     */
    SysConfig selectConfigByKey(@Param("configKey") String configKey);

    /**
     * 校验参数键名是否唯一
     */
    SysConfig checkConfigKeyUnique(@Param("configKey") String configKey);
}

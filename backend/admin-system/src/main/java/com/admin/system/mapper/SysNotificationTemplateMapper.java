package com.admin.system.mapper;

import com.admin.system.entity.SysNotificationTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 通知模板 Mapper 接口
 *
 * @author Admin
 */
public interface SysNotificationTemplateMapper extends BaseMapper<SysNotificationTemplate> {

    /**
     * 根据模板编码查询模板
     *
     * @param templateCode 模板编码
     * @return 模板信息
     */
    SysNotificationTemplate selectByTemplateCode(@Param("templateCode") String templateCode);

}

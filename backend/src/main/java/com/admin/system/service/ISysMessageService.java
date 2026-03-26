package com.admin.system.service;

import com.admin.system.entity.SysMessage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 消息模板服务接口
 *
 * @author Admin
 */
public interface ISysMessageService extends IService<SysMessage> {

    /**
     * 根据消息编码查询消息模板
     *
     * @param messageCode 消息编码
     * @return 消息模板
     */
    SysMessage getByMessageCode(String messageCode);
}

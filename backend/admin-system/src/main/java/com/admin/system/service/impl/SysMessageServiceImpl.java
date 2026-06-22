package com.admin.system.service.impl;

import com.admin.system.entity.SysMessage;
import com.admin.system.mapper.SysMessageMapper;
import com.admin.system.service.ISysMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 消息模板服务实现
 *
 * @author Admin
 */
@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

    @Override
    public SysMessage getByMessageCode(String messageCode) {
        LambdaQueryWrapper<SysMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMessage::getMessageCode, messageCode);
        queryWrapper.eq(SysMessage::getDeleted, 0);
        return this.getOne(queryWrapper);
    }
}

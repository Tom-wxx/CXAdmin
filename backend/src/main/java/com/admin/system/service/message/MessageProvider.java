package com.admin.system.service.message;

import java.util.Map;

/**
 * 消息发送提供者接口
 *
 * @author Admin
 */
public interface MessageProvider {

    /**
     * 发送消息
     *
     * @param receiver 接收者（邮箱/手机号/用户ID）
     * @param subject  主题/标题
     * @param content  消息内容
     * @param config   配置信息
     * @return 是否发送成功
     * @throws Exception 发送异常
     */
    boolean send(String receiver, String subject, String content, Map<String, Object> config) throws Exception;

    /**
     * 获取消息类型
     *
     * @return 消息类型（1邮件 2短信 3站内信 4微信）
     */
    String getMessageType();
}

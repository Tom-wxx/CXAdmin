package com.admin.system.service;

import java.util.Map;

/**
 * 消息发送服务接口
 *
 * @author Admin
 */
public interface IMessageSendService {

    /**
     * 根据消息模板发送消息
     *
     * @param messageCode 消息编码
     * @param receiver    接收者（邮箱/手机号/用户ID）
     * @param variables   模板变量值
     * @return 是否发送成功
     */
    boolean sendByTemplate(String messageCode, String receiver, Map<String, Object> variables);

    /**
     * 直接发送消息（不使用模板）
     *
     * @param messageType 消息类型（1邮件 2短信 3站内信 4微信）
     * @param receiver    接收者（邮箱/手机号/用户ID）
     * @param subject     主题/标题
     * @param content     消息内容
     * @return 是否发送成功
     */
    boolean sendDirect(String messageType, String receiver, String subject, String content);

    /**
     * 测试发送消息
     *
     * @param messageType 消息类型（1邮件 2短信 3站内信 4微信）
     * @param receiver    接收者（邮箱/手机号/用户ID）
     * @param subject     主题/标题
     * @param content     消息内容
     * @param configId    配置ID
     * @return 是否发送成功
     */
    boolean testSend(String messageType, String receiver, String subject, String content, Long configId);
}

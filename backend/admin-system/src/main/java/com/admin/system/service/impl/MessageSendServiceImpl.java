package com.admin.system.service.impl;

import com.admin.system.entity.SysMessage;
import com.admin.system.entity.SysMessageConfig;
import com.admin.system.entity.SysMessageLog;
import com.admin.system.service.IMessageSendService;
import com.admin.system.service.ISysMessageConfigService;
import com.admin.system.service.ISysMessageLogService;
import com.admin.system.service.ISysMessageService;
import com.admin.system.service.message.MessageProvider;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息发送服务实现
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class MessageSendServiceImpl implements IMessageSendService {

    private static final Logger log = LoggerFactory.getLogger(MessageSendServiceImpl.class);

    private final ISysMessageService messageService;
    private final ISysMessageConfigService messageConfigService;
    private final ISysMessageLogService messageLogService;
    private final List<MessageProvider> messageProviders;

    @Override
    public boolean sendByTemplate(String messageCode, String receiver, Map<String, Object> variables) {
        // 查询消息模板
        SysMessage message = messageService.getByMessageCode(messageCode);
        if (message == null) {
            log.error("消息模板不存在：{}", messageCode);
            return false;
        }

        if (!"0".equals(message.getStatus())) {
            log.error("消息模板已停用：{}", messageCode);
            return false;
        }

        // 替换模板变量
        String subject = replaceVariables(message.getSubject(), variables);
        String content = replaceVariables(message.getContent(), variables);

        // 发送消息
        return sendDirect(message.getMessageType(), receiver, subject, content, message.getMessageId());
    }

    @Override
    public boolean sendDirect(String messageType, String receiver, String subject, String content) {
        return sendDirect(messageType, receiver, subject, content, null);
    }

    @Override
    public boolean testSend(String messageType, String receiver, String subject, String content, Long configId) {
        // 获取指定配置
        SysMessageConfig config = null;
        if (configId != null) {
            config = messageConfigService.getById(configId);
        }
        if (config == null) {
            config = messageConfigService.getDefaultConfig(messageType);
        }

        if (config == null) {
            log.error("消息配置不存在，类型：{}", messageType);
            return false;
        }

        // 发送消息（测试发送不记录日志）
        return doSend(messageType, receiver, subject, content, config, null);
    }

    /**
     * 直接发送消息（内部方法，支持指定消息ID）
     *
     * @param messageType 消息类型
     * @param receiver    接收者
     * @param subject     主题
     * @param content     内容
     * @param messageId   消息模板ID（可为空）
     * @return 是否发送成功
     */
    private boolean sendDirect(String messageType, String receiver, String subject, String content, Long messageId) {
        // 获取默认配置
        SysMessageConfig config = messageConfigService.getDefaultConfig(messageType);
        if (config == null) {
            log.error("消息配置不存在，类型：{}", messageType);
            return false;
        }

        if (!"0".equals(config.getStatus())) {
            log.error("消息配置已停用，类型：{}", messageType);
            return false;
        }

        // 发送消息
        return doSend(messageType, receiver, subject, content, config, messageId);
    }

    /**
     * 执行消息发送
     *
     * @param messageType 消息类型
     * @param receiver    接收者
     * @param subject     主题
     * @param content     内容
     * @param config      配置信息
     * @param messageId   消息模板ID
     * @return 是否发送成功
     */
    private boolean doSend(String messageType, String receiver, String subject, String content,
                          SysMessageConfig config, Long messageId) {
        // 创建发送日志
        SysMessageLog messageLog = new SysMessageLog();
        messageLog.setMessageId(messageId);
        messageLog.setMessageType(messageType);
        messageLog.setReceiver(receiver);
        messageLog.setSubject(subject);
        messageLog.setContent(content);
        messageLog.setCreateTime(new Date());

        try {
            // 获取对应的消息发送提供者
            MessageProvider provider = getProvider(messageType);
            if (provider == null) {
                throw new RuntimeException("不支持的消息类型：" + messageType);
            }

            // 解析配置数据
            Map<String, Object> configMap = JSON.parseObject(config.getConfigData(), Map.class);

            // 发送消息
            boolean success = provider.send(receiver, subject, content, configMap);

            // 记录日志
            if (messageId != null) {
                messageLog.setSendStatus(success ? "0" : "1");
                messageLog.setSendTime(new Date());
                if (!success) {
                    messageLog.setErrorMsg("发送失败");
                }
                messageLogService.recordLog(messageLog);
            }

            return success;
        } catch (Exception e) {
            log.error("消息发送失败，类型：{}, 接收者：{}, 错误：{}", messageType, receiver, e.getMessage(), e);

            // 记录错误日志
            if (messageId != null) {
                messageLog.setSendStatus("1");
                messageLog.setSendTime(new Date());
                messageLog.setErrorMsg(e.getMessage());
                messageLogService.recordLog(messageLog);
            }

            return false;
        }
    }

    /**
     * 获取消息发送提供者
     *
     * @param messageType 消息类型
     * @return 消息发送提供者
     */
    private MessageProvider getProvider(String messageType) {
        for (MessageProvider provider : messageProviders) {
            if (provider.getMessageType().equals(messageType)) {
                return provider;
            }
        }
        return null;
    }

    /**
     * 替换模板变量
     *
     * @param template  模板内容
     * @param variables 变量值
     * @return 替换后的内容
     */
    private String replaceVariables(String template, Map<String, Object> variables) {
        if (!StringUtils.hasText(template) || variables == null || variables.isEmpty()) {
            return template;
        }

        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = variables.get(key);
            matcher.appendReplacement(result, value != null ? value.toString() : "");
        }
        matcher.appendTail(result);

        return result.toString();
    }
}

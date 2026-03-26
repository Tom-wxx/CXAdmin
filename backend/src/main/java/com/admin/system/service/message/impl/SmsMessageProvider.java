package com.admin.system.service.message.impl;

import com.admin.system.service.message.MessageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信消息发送提供者（模拟实现）
 *
 * 实际使用时需要集成阿里云、腾讯云等短信服务商的SDK
 *
 * @author Admin
 */
@Component
public class SmsMessageProvider implements MessageProvider {

    private static final Logger log = LoggerFactory.getLogger(SmsMessageProvider.class);

    @Override
    public boolean send(String receiver, String subject, String content, Map<String, Object> config) throws Exception {
        log.info("开始发送短信，接收者：{}, 内容：{}", receiver, content);

        try {
            // 获取配置信息
            String provider = (String) config.getOrDefault("provider", "aliyun");
            String accessKeyId = (String) config.get("accessKeyId");
            String accessKeySecret = (String) config.get("accessKeySecret");
            String signName = (String) config.get("signName");
            String templateCode = (String) config.get("templateCode");

            // 模拟短信发送
            // 实际使用时需要调用短信服务商的API
            // 例如：阿里云短信、腾讯云短信等

            log.info("【模拟】短信发送配置：provider={}, signName={}, templateCode={}",
                    provider, signName, templateCode);
            log.info("【模拟】短信发送内容：{}", content);

            // 模拟发送延迟
            Thread.sleep(100);

            log.info("【模拟】短信发送成功，接收者：{}", receiver);
            return true;
        } catch (Exception e) {
            log.error("短信发送失败，接收者：{}, 错误：{}", receiver, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String getMessageType() {
        return "2"; // 2表示短信
    }
}

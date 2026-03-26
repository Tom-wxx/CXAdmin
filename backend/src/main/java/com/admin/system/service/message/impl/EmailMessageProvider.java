package com.admin.system.service.message.impl;

import com.admin.system.service.message.MessageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件消息发送提供者
 *
 * @author Admin
 */
@Component
public class EmailMessageProvider implements MessageProvider {

    private static final Logger log = LoggerFactory.getLogger(EmailMessageProvider.class);

    @Override
    public boolean send(String receiver, String subject, String content, Map<String, Object> config) throws Exception {
        log.info("开始发送邮件，接收者：{}, 主题：{}", receiver, subject);

        try {
            // 获取配置信息
            String host = (String) config.get("host");
            String port = (String) config.get("port");
            String username = (String) config.get("username");
            String password = (String) config.get("password");
            String from = (String) config.get("from");
            String fromName = (String) config.get("fromName");
            Boolean ssl = (Boolean) config.getOrDefault("ssl", true);

            // 配置邮件服务器属性
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");

            if (ssl) {
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            } else {
                props.put("mail.smtp.starttls.enable", "true");
            }

            // 创建会话
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // 创建邮件消息
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, fromName, "UTF-8"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            message.setSubject(subject, "UTF-8");
            message.setContent(content, "text/html;charset=UTF-8");

            // 发送邮件
            Transport.send(message);

            log.info("邮件发送成功，接收者：{}", receiver);
            return true;
        } catch (Exception e) {
            log.error("邮件发送失败，接收者：{}, 错误：{}", receiver, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String getMessageType() {
        return "1"; // 1表示邮件
    }
}

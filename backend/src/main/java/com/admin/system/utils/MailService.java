package com.admin.system.utils;

import com.admin.system.common.constants.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Value("${app.frontend-url:http://localhost:8081}")
    private String frontendUrl;

    public void sendResetPasswordEmail(String to, String token) {
        if (mailSender == null) {
            log.warn("邮件服务未配置，跳过发送重置密码邮件至: {}", to);
            return;
        }
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("CXAdmin 密码重置");
        message.setText("您好，\n\n请点击以下链接重置密码（链接 "
                + SystemConstants.RESET_PWD_EXPIRATION + " 分钟内有效）：\n\n"
                + resetLink + "\n\n如非本人操作，请忽略此邮件。");
        mailSender.send(message);
    }
}

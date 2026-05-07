package com.moodnote.common.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.moodnote.common.constant.MessageConstant;
import com.moodnote.common.exception.BusinessException;
import com.moodnote.common.exception.ErrorCode;

@Slf4j
@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    /**
     * 发送验证码邮件
     * @param to 收件人邮箱
     * @param code 验证码
     * @param subject 邮件主题
     * @return 是否发送成功
     */
    public boolean sendCodeEmail(String to, String code, String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            
            String content = "<html><body>" +
                    "<h3>您好！</h3>" +
                    "<p>您的验证码是：<strong style='color: #4CAF50; font-size: 24px;'>" + code + "</strong></p>" +
                    "<p>该验证码有效期为5分钟，请及时使用。</p>" +
                    "<p>如果不是您本人操作，请忽略此邮件。</p>" +
                    "</body></html>";
            helper.setText(content, true);
            
            mailSender.send(message);
            log.info(MessageConstant.EMAIL_SEND_SUCCESS + "收件人：{}", to);
            return true;
        } catch (MessagingException e) {
            log.error(MessageConstant.EMAIL_SEND_FAILED, e);
            throw new BusinessException(ErrorCode.EMAIL_SERVICE_ERROR, MessageConstant.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 发送注册验证码
     */
    public boolean sendRegisterCode(String email, String code) {
        return sendCodeEmail(email, code, "【晚风记事】注册验证码");
    }

    /**
     * 发送登录验证码
     */
    public boolean sendLoginCode(String email, String code) {
        return sendCodeEmail(email, code, "【晚风记事】登录验证码");
    }

    /**
     * 发送找回密码验证码
     */
    public boolean sendResetCode(String email, String code) {
        return sendCodeEmail(email, code, "【晚风记事】找回密码验证码");
    }
}
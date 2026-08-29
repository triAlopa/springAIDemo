package com.chen.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OfferMailRetryService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    public OfferMailRetryService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Retryable(
            retryFor = {MailException.class, MessagingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2.0)
    )
    public boolean sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(to);
        mimeMessage.setSubject(subject);
        mimeMessageHelper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
        return true;
    }

    @Recover
    public boolean recover(Exception e, String to, String subject, String htmlContent) {
        log.error("offer邮件发送失败，已达到最大重试次数, to={}, subject={}", to, subject, e);
        return false;
    }
}

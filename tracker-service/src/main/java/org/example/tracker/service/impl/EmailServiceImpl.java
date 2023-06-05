package org.example.tracker.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.service.EmailService;
import org.example.tracker.service.property.EmailProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final EmailProperties properties;

    public EmailServiceImpl(JavaMailSender mailSender, EmailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
        log.info("mail properties: {}", properties);
    }

    @Override
    public boolean send(EmailDetails details) {
        if (!properties.getEnable()) {
            return true;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            String to = details.getTo();
            String text = details.getText();
            String subject = details.getSubject();

            if (properties.getTest().getEnable()) {
                text = String.format("%s\n%s", details.getTo(), details.getText());
                to = properties.getTest().getTo();
            }

            helper.setTo(to);
            helper.setText(text);
            helper.setSubject(subject);

            mailSender.send(message);
            log.info("message send to email {}", to);
            return true;

        } catch (MessagingException | MailException ex) {
            log.error(ex.getMessage());
        }

        return false;
    }
}

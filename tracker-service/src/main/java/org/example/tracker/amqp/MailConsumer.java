package org.example.tracker.amqp;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.smtp.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = "${custom.amqp.mail-queue}")
    public void process(EmailService.EmailDetails msg) throws MessagingException {
        log.info("receive mail queue {}", msg);
        emailService.send(msg);
    }
}

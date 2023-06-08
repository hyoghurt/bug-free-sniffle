package org.example.tracker.amqp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.property.AmqpProperties;
import org.example.tracker.smtp.EmailService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailProducer {
    private final RabbitTemplate rabbitTemplate;
    private final AmqpProperties properties;

    public void sendMail(EmailService.EmailDetails msg) {
        log.info("send mail queue {}", msg);
        rabbitTemplate.convertAndSend(properties.getMailExchange(), properties.getMailQueue(), msg);
    }
}

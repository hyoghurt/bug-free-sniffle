package org.example.tracker.amqp.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.amqp.message.NewTaskMsg;
import org.example.tracker.amqp.property.AmqpProperties;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewTaskProducer {
    private final RabbitTemplate rabbitTemplate;
    private final AmqpProperties properties;

    public void send(NewTaskMsg msg) {
        try {
            log.info("send mail queue {}", msg);
            rabbitTemplate.convertAndSend(properties.getNewTaskExchange(), properties.getNewTaskQueue(), msg);
        } catch (AmqpException e) {
            log.warn("amqp send error {}", e.getMessage());
        }
    }
}

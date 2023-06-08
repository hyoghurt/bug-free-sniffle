package org.example.tracker.amqp;

import lombok.RequiredArgsConstructor;
import org.example.tracker.property.AmqpProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmqpConfig {
    private final AmqpProperties properties;

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    Queue mailQueue() {
        return QueueBuilder.durable(properties.getMailQueue())
                .withArgument("x-dead-letter-exchange", properties.getMailDlExchange())
                .build();
    }

    @Bean
    Queue mailDeadLetterQueue() {
        return QueueBuilder.durable(properties.getMailDlQueue()).build();
    }

    @Bean
    DirectExchange mailExchange() {
        return new DirectExchange(properties.getMailExchange());
    }

    @Bean
    FanoutExchange mailDeadLetterExchange() {
        return new FanoutExchange(properties.getMailDlExchange());
    }

    @Bean
    Binding mailBinding() {
        return BindingBuilder
                .bind(mailQueue())
                .to(mailExchange())
                .with(properties.getMailQueue());
    }

    @Bean
    Binding mailDeadLetterBinding() {
        return BindingBuilder
                .bind(mailDeadLetterQueue())
                .to(mailDeadLetterExchange());
    }
}

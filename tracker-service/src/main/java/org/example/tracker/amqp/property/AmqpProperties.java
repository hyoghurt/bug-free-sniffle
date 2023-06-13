package org.example.tracker.amqp.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "custom.amqp")
public class AmqpProperties {
    private String newTaskExchange;
    private String newTaskDlExchange;
    private String newTaskQueue;
    private String newTaskDlQueue;
}

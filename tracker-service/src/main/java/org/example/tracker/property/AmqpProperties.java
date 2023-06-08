package org.example.tracker.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "custom.amqp")
public class AmqpProperties {
    private String mailExchange;
    private String mailDlExchange;
    private String mailQueue;
    private String mailDlQueue;
}

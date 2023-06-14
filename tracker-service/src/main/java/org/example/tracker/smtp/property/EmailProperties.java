package org.example.tracker.smtp.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "custom.mail")
public class EmailProperties {
    private Boolean enable = false;
    private Test test;

    @Data
    @ConfigurationProperties(prefix = "test")
    public static class Test {
        private Boolean enable = true;
        private String to;
    }
}

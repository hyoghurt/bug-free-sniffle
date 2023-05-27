package org.example.tracker.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "custom.security")
public class CustomSecurityProperties {
    private String[] permitAll;
}

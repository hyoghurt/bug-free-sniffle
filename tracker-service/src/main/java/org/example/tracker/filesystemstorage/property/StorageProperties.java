package org.example.tracker.filesystemstorage.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "custom.storage")
public class StorageProperties {
    private String location;
}

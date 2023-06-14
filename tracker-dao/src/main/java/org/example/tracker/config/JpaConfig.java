package org.example.tracker.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "org.example.tracker.repository")
@EntityScan("org.example.tracker.entity")
public class JpaConfig {
}

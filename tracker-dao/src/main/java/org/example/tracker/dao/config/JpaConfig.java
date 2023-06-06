package org.example.tracker.dao.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "org.example.tracker.dao")
@EntityScan("org.example.tracker.dao")
public class JpaConfig {
}

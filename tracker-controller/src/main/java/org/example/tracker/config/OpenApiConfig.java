package org.example.tracker.config;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Value("${springdoc.version}")
    private String appVersion;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicScheme",
                        new SecurityScheme()
                                .scheme("basic")
                                .type(SecurityScheme.Type.HTTP)
                                .in(SecurityScheme.In.HEADER))
                )
                .info(new Info().title("Tracker API")
                        .description("API для управления проектами")
                        .version(appVersion)
                );
    }
}

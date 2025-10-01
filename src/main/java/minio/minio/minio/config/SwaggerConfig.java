package minio.minio.minio.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("minio-file-service")
                .pathsToMatch("/api/**")
                .packagesToScan("minio.minio.minio.controller")
                .build();
    }
}

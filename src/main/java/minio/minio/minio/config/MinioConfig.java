package minio.minio.minio.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    private final MinioProperties minioProperties;

    public MinioConfig(MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
        log.info("MinIO Configuration:");
        log.info("  - Endpoint: {}", minioProperties.getEndpoint());
        log.info("  - Access Key: {}", minioProperties.getAccessKey() != null ? "[PROVIDED]" : "[MISSING]");
        log.info("  - Secret Key: {}", minioProperties.getSecretKey() != null ? "[PROVIDED]" : "[MISSING]");
        log.info("  - Bucket Configuration:");
        log.info("    - Songs: {}", minioProperties.getBucket().getSongs());
        log.info("    - Images: {}", minioProperties.getBucket().getImages());
        log.info("    - Videos: {}", minioProperties.getBucket().getVideos());
        log.info("    - Photos: {}", minioProperties.getBucket().getPhotos());
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    @Bean
    public String minioBaseUrl() {
        return minioProperties.getUrl();
    }
}

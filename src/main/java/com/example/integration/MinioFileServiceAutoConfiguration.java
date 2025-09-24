package com.example.integration;

// import com.fileservice.client.MinioFileClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * OPTION 3: Spring Boot Starter Auto-Configuration
 * Les backends ajoutent juste une dépendance et configurent les propriétés
 */

@Configuration
@ConfigurationProperties(prefix = "minio.file.service")
public class MinioFileServiceAutoConfiguration {
    
    private String baseUrl = "http://localhost:8080";
    private int timeoutSeconds = 30;
    private boolean enableMetrics = true;
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // Configuration du timeout, etc.
        return restTemplate;
    }
    
    // Note: MinioFileClient bean would be created when the SDK module is properly integrated
    
    // Getters/Setters pour les propriétés
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    
    public boolean isEnableMetrics() { return enableMetrics; }
    public void setEnableMetrics(boolean enableMetrics) { this.enableMetrics = enableMetrics; }
}

/**
 * Comment les backends l'utilisent:
 * 
 * 1. Ajouter dans pom.xml:
 * <dependency>
 *     <groupId>com.fileservice</groupId>
 *     <artifactId>minio-file-service-starter</artifactId>
 *     <version>1.0.0</version>
 * </dependency>
 * 
 * 2. Configurer dans application.yml:
 * minio:
 *   file:
 *     service:
 *       base-url: http://minio-service:8080
 *       timeout-seconds: 60
 *       enable-metrics: true
 * 
 * 3. Utiliser directement:
 * @Service
 * public class MyService {
 *     
 *     @Autowired
 *     private MinioFileClient fileClient;
 *     
 *     public String uploadPhoto(MultipartFile photo) {
 *         return fileClient.uploadFile(photo, FileType.PHOTO).getFileUrl();
 *     }
 * }
 */

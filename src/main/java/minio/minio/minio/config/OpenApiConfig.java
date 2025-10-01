package minio.minio.minio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MinIO File Service API")
                        .description("API REST pour la gestion de fichiers avec MinIO - Service de stockage cloud")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DevNestor")
                                .email("devnestor@example.com")
                                .url("https://github.com/devnestor"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("https://minio-file-service-6s7p.onrender.com")
                                .description("Production Server (Render)"),
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server")
                ));
    }
}

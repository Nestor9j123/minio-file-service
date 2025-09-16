package minio.minio.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String url;
    private String accessKey;
    private String secretKey;
    private Bucket bucket = new Bucket();

    @Data
    public static class Bucket {
        private String songs = "file-service-songs";
        private String images = "file-service-images";
        private String videos = "file-service-videos";
        private String photos = "file-service-photos";
        private String documents = "file-service-documents";
        private String archives = "file-service-archives";
        private String files = "file-service-files";
        
        public String getSongs() {
            return songs;
        }
        
        public String getImages() {
            return images;
        }
        
        public String getVideos() {
            return videos;
        }
        
        public String getPhotos() {
            return photos;
        }
        
        public String getDocuments() {
            return documents;
        }
        
        public String getArchives() {
            return archives;
        }
        
        public String getFiles() {
            return files;
        }
    }

    public String getEndpoint() {
        return url;
    }

    public String getAccessKey() {
        return accessKey != null ? accessKey : "minioadmin";
    }

    public String getSecretKey() {
        return secretKey != null ? secretKey : "minioadmin123";
    }
    
    public Bucket getBucket() {
        return bucket;
    }

    public String getUrl() {
        return url != null ? url : "http://localhost:9000";
    }
}

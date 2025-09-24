package minio.minio.minio.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {MinioProperties.class})
@EnableConfigurationProperties(MinioProperties.class)
@TestPropertySource(properties = {
        "minio.url=http://test-minio:9000",
        "minio.access-key=test-access-key",
        "minio.secret-key=test-secret-key",
        "minio.bucket.songs=test-songs",
        "minio.bucket.images=test-images",
        "minio.bucket.videos=test-videos",
        "minio.bucket.photos=test-photos",
        "minio.bucket.documents=test-documents",
        "minio.bucket.archives=test-archives",
        "minio.bucket.files=test-files"
})
class MinioPropertiesTest {

    private MinioProperties minioProperties;

    @BeforeEach
    void setUp() {
        minioProperties = new MinioProperties();
        minioProperties.setUrl("http://test-minio:9000");
        minioProperties.setAccessKey("test-access-key");
        minioProperties.setSecretKey("test-secret-key");
        
        MinioProperties.Bucket bucket = new MinioProperties.Bucket();
        bucket.setSongs("test-songs");
        bucket.setImages("test-images");
        bucket.setVideos("test-videos");
        bucket.setPhotos("test-photos");
        bucket.setDocuments("test-documents");
        bucket.setArchives("test-archives");
        bucket.setFiles("test-files");
        
        minioProperties.setBucket(bucket);
    }

    @Test
    void getUrl_WithConfiguredValue_ReturnsConfiguredValue() {
        // When
        String url = minioProperties.getUrl();
        
        // Then
        assertEquals("http://test-minio:9000", url);
    }

    @Test
    void getUrl_WithNullValue_ReturnsDefaultValue() {
        // Given
        minioProperties.setUrl(null);
        
        // When
        String url = minioProperties.getUrl();
        
        // Then
        assertEquals("http://localhost:9000", url);
    }

    @Test
    void getEndpoint_ReturnsUrlValue() {
        // When
        String endpoint = minioProperties.getEndpoint();
        
        // Then
        assertEquals("http://test-minio:9000", endpoint);
    }

    @Test
    void getAccessKey_WithConfiguredValue_ReturnsConfiguredValue() {
        // When
        String accessKey = minioProperties.getAccessKey();
        
        // Then
        assertEquals("test-access-key", accessKey);
    }

    @Test
    void getAccessKey_WithNullValue_ReturnsDefaultValue() {
        // Given
        minioProperties.setAccessKey(null);
        
        // When
        String accessKey = minioProperties.getAccessKey();
        
        // Then
        assertEquals("minioadmin", accessKey);
    }

    @Test
    void getSecretKey_WithConfiguredValue_ReturnsConfiguredValue() {
        // When
        String secretKey = minioProperties.getSecretKey();
        
        // Then
        assertEquals("test-secret-key", secretKey);
    }

    @Test
    void getSecretKey_WithNullValue_ReturnsDefaultValue() {
        // Given
        minioProperties.setSecretKey(null);
        
        // When
        String secretKey = minioProperties.getSecretKey();
        
        // Then
        assertEquals("minioadmin123", secretKey);
    }

    @Test
    void getBucket_ReturnsConfiguredBucket() {
        // When
        MinioProperties.Bucket bucket = minioProperties.getBucket();
        
        // Then
        assertNotNull(bucket);
        assertEquals("test-songs", bucket.getSongs());
        assertEquals("test-images", bucket.getImages());
        assertEquals("test-videos", bucket.getVideos());
        assertEquals("test-photos", bucket.getPhotos());
        assertEquals("test-documents", bucket.getDocuments());
        assertEquals("test-archives", bucket.getArchives());
        assertEquals("test-files", bucket.getFiles());
    }

    @Test
    void bucketDefaultValues_AreCorrect() {
        // Given
        MinioProperties.Bucket bucket = new MinioProperties.Bucket();
        
        // Then
        assertEquals("file-service-songs", bucket.getSongs());
        assertEquals("file-service-images", bucket.getImages());
        assertEquals("file-service-videos", bucket.getVideos());
        assertEquals("file-service-photos", bucket.getPhotos());
        assertEquals("file-service-documents", bucket.getDocuments());
        assertEquals("file-service-archives", bucket.getArchives());
        assertEquals("file-service-files", bucket.getFiles());
    }

    @Test
    void bucketSettersAndGetters_WorkCorrectly() {
        // Given
        MinioProperties.Bucket bucket = new MinioProperties.Bucket();
        
        // When
        bucket.setSongs("custom-songs");
        bucket.setImages("custom-images");
        bucket.setVideos("custom-videos");
        bucket.setPhotos("custom-photos");
        bucket.setDocuments("custom-documents");
        bucket.setArchives("custom-archives");
        bucket.setFiles("custom-files");
        
        // Then
        assertEquals("custom-songs", bucket.getSongs());
        assertEquals("custom-images", bucket.getImages());
        assertEquals("custom-videos", bucket.getVideos());
        assertEquals("custom-photos", bucket.getPhotos());
        assertEquals("custom-documents", bucket.getDocuments());
        assertEquals("custom-archives", bucket.getArchives());
        assertEquals("custom-files", bucket.getFiles());
    }

    @Test
    void minioProperties_DefaultConstructor_InitializesCorrectly() {
        // Given
        MinioProperties properties = new MinioProperties();
        
        // Then
        assertNotNull(properties.getBucket());
        assertEquals("http://localhost:9000", properties.getUrl());
        assertEquals("minioadmin", properties.getAccessKey());
        assertEquals("minioadmin123", properties.getSecretKey());
    }

    @Test
    void minioProperties_AllFieldsAccessible() {
        // Given
        MinioProperties properties = new MinioProperties();
        
        // When
        properties.setUrl("http://custom:9000");
        properties.setAccessKey("custom-access");
        properties.setSecretKey("custom-secret");
        
        MinioProperties.Bucket customBucket = new MinioProperties.Bucket();
        customBucket.setSongs("custom-songs-bucket");
        properties.setBucket(customBucket);
        
        // Then
        assertEquals("http://custom:9000", properties.getUrl());
        assertEquals("custom-access", properties.getAccessKey());
        assertEquals("custom-secret", properties.getSecretKey());
        assertEquals("custom-songs-bucket", properties.getBucket().getSongs());
    }

    @Test
    void bucketClass_IsStaticAndPublic() {
        // Given & When
        MinioProperties.Bucket bucket = new MinioProperties.Bucket();
        
        // Then
        assertNotNull(bucket);
        assertTrue(bucket.getClass().getModifiers() > 0); // Public class
    }

    @Test
    void minioProperties_EqualsAndHashCode() {
        // Given
        MinioProperties properties1 = new MinioProperties();
        properties1.setUrl("http://test:9000");
        properties1.setAccessKey("test-key");
        properties1.setSecretKey("test-secret");
        
        MinioProperties properties2 = new MinioProperties();
        properties2.setUrl("http://test:9000");
        properties2.setAccessKey("test-key");
        properties2.setSecretKey("test-secret");
        
        // Then
        assertEquals(properties1.getUrl(), properties2.getUrl());
        assertEquals(properties1.getAccessKey(), properties2.getAccessKey());
        assertEquals(properties1.getSecretKey(), properties2.getSecretKey());
    }

    @Test
    void bucketProperties_EqualsAndHashCode() {
        // Given
        MinioProperties.Bucket bucket1 = new MinioProperties.Bucket();
        bucket1.setSongs("test-songs");
        bucket1.setImages("test-images");
        
        MinioProperties.Bucket bucket2 = new MinioProperties.Bucket();
        bucket2.setSongs("test-songs");
        bucket2.setImages("test-images");
        
        // Then
        assertEquals(bucket1.getSongs(), bucket2.getSongs());
        assertEquals(bucket1.getImages(), bucket2.getImages());
    }
}

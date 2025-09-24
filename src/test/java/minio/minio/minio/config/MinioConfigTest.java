package minio.minio.minio.config;

import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class MinioConfigTest {

    @Mock
    private MinioProperties minioProperties;

    @Mock
    private MinioProperties.Bucket bucket;

    private MinioConfig minioConfig;

    @BeforeEach
    void setUp() {
        when(minioProperties.getEndpoint()).thenReturn("http://localhost:9000");
        when(minioProperties.getAccessKey()).thenReturn("minioadmin");
        when(minioProperties.getSecretKey()).thenReturn("minioadmin123");
        when(minioProperties.getUrl()).thenReturn("http://localhost:9000");
        when(minioProperties.getBucket()).thenReturn(bucket);
        
        lenient().when(bucket.getSongs()).thenReturn("file-service-songs");
        lenient().when(bucket.getImages()).thenReturn("file-service-images");
        lenient().when(bucket.getVideos()).thenReturn("file-service-videos");
        lenient().when(bucket.getPhotos()).thenReturn("file-service-photos");
        
        minioConfig = new MinioConfig(minioProperties);
    }

    @Test
    void constructor_LogsConfiguration() {
        // Given & When
        MinioConfig config = new MinioConfig(minioProperties);
        
        // Then
        assertNotNull(config);
        verify(minioProperties).getEndpoint();
        verify(minioProperties).getAccessKey();
        verify(minioProperties).getSecretKey();
        verify(minioProperties).getBucket();
    }

    @Test
    void minioClient_CreatesClientWithCorrectConfiguration() {
        // When
        MinioClient client = minioConfig.minioClient();
        
        // Then
        assertNotNull(client);
        verify(minioProperties).getEndpoint();
        verify(minioProperties).getAccessKey();
        verify(minioProperties).getSecretKey();
    }

    @Test
    void minioBaseUrl_ReturnsCorrectUrl() {
        // When
        String baseUrl = minioConfig.minioBaseUrl();
        
        // Then
        assertEquals("http://localhost:9000", baseUrl);
        verify(minioProperties).getUrl();
    }

    @Test
    void minioClient_WithCustomEndpoint_CreatesClient() {
        // Given
        when(minioProperties.getEndpoint()).thenReturn("http://custom-minio:9000");
        when(minioProperties.getAccessKey()).thenReturn("custom-access");
        when(minioProperties.getSecretKey()).thenReturn("custom-secret");
        
        MinioConfig customConfig = new MinioConfig(minioProperties);
        
        // When
        MinioClient client = customConfig.minioClient();
        
        // Then
        assertNotNull(client);
    }

    @Test
    void minioBaseUrl_WithCustomUrl_ReturnsCustomUrl() {
        // Given
        String customUrl = "http://custom-minio:9000";
        when(minioProperties.getUrl()).thenReturn(customUrl);
        
        MinioConfig customConfig = new MinioConfig(minioProperties);
        
        // When
        String baseUrl = customConfig.minioBaseUrl();
        
        // Then
        assertEquals(customUrl, baseUrl);
    }

    @Test
    void constructor_WithNullProperties_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            new MinioConfig(null));
    }

    @Test
    void minioClient_IsNotNull() {
        // When
        MinioClient client = minioConfig.minioClient();
        
        // Then
        assertNotNull(client);
    }

    @Test
    void minioBaseUrl_IsNotNull() {
        // When
        String baseUrl = minioConfig.minioBaseUrl();
        
        // Then
        assertNotNull(baseUrl);
    }

    @Test
    void constructor_CallsAllRequiredMethods() {
        // Given
        reset(minioProperties, bucket);
        when(minioProperties.getEndpoint()).thenReturn("http://test:9000");
        when(minioProperties.getAccessKey()).thenReturn("test-access");
        when(minioProperties.getSecretKey()).thenReturn("test-secret");
        when(minioProperties.getBucket()).thenReturn(bucket);
        when(bucket.getSongs()).thenReturn("test-songs");
        when(bucket.getImages()).thenReturn("test-images");
        when(bucket.getVideos()).thenReturn("test-videos");
        when(bucket.getPhotos()).thenReturn("test-photos");
        
        // When
        new MinioConfig(minioProperties);
        
        // Then
        verify(minioProperties).getEndpoint();
        verify(minioProperties).getAccessKey();
        verify(minioProperties).getSecretKey();
        verify(minioProperties).getBucket();
        verify(bucket).getSongs();
        verify(bucket).getImages();
        verify(bucket).getVideos();
        verify(bucket).getPhotos();
    }

    @Test
    void minioClient_WithSecureEndpoint_CreatesClient() {
        // Given
        when(minioProperties.getEndpoint()).thenReturn("https://secure-minio:9000");
        
        MinioConfig secureConfig = new MinioConfig(minioProperties);
        
        // When
        MinioClient client = secureConfig.minioClient();
        
        // Then
        assertNotNull(client);
    }

    @Test
    void minioClient_MultipleCalls_ReturnsDifferentInstances() {
        // When
        MinioClient client1 = minioConfig.minioClient();
        MinioClient client2 = minioConfig.minioClient();
        
        // Then
        assertNotNull(client1);
        assertNotNull(client2);
        assertNotSame(client1, client2); // Different instances each time
    }

    @Test
    void minioBaseUrl_MultipleCalls_ReturnsSameValue() {
        // When
        String url1 = minioConfig.minioBaseUrl();
        String url2 = minioConfig.minioBaseUrl();
        
        // Then
        assertEquals(url1, url2);
    }
}

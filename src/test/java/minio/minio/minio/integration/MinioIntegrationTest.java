package minio.minio.minio.integration;

import minio.minio.minio.dto.FileUploadResponse;
import minio.minio.minio.enums.FileType;
import minio.minio.minio.service.MinioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class MinioIntegrationTest {

    @Container
    static MinIOContainer minioContainer = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z")
            .withUserName("testuser")
            .withPassword("testpassword");

    @Autowired
    private MinioService minioService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.url", minioContainer::getS3URL);
        registry.add("minio.access-key", minioContainer::getUserName);
        registry.add("minio.secret-key", minioContainer::getPassword);
    }

    @Test
    void uploadAndDownloadFile_Integration_Success() {
        // Given
        // Use a minimal JPEG header that Tika will recognize as image/jpeg
        byte[] jpegContent = new byte[]{
            (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00, (byte)0xFF, (byte)0xD9
        };
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-integration.jpg", "image/jpeg", jpegContent);

        // When - Upload
        FileUploadResponse uploadResponse = minioService.uploadFile(file, FileType.IMAGE);

        // Then - Verify upload
        assertNotNull(uploadResponse);
        assertNotNull(uploadResponse.getFileName());
        assertNotNull(uploadResponse.getFileUrl());
        assertEquals("test-integration.jpg", uploadResponse.getOriginalFileName());
        assertEquals("image/jpeg", uploadResponse.getContentType());

        // When - Check file exists
        boolean exists = minioService.fileExists(uploadResponse.getFileName(), FileType.IMAGE);

        // Then - Verify existence
        assertTrue(exists);

        // When - Get file content
        byte[] content = minioService.getFileContentAsBytes(uploadResponse.getFileName(), FileType.IMAGE);

        // Then - Verify content
        assertNotNull(content);
        assertArrayEquals(jpegContent, content);

        // When - Delete file
        boolean deleted = minioService.deleteFile(uploadResponse.getFileName(), FileType.IMAGE);

        // Then - Verify deletion
        assertTrue(deleted);

        // When - Check file no longer exists
        boolean existsAfterDelete = minioService.fileExists(uploadResponse.getFileName(), FileType.IMAGE);

        // Then - Verify file is gone
        assertFalse(existsAfterDelete);
    }

    @Test
    void uploadMultipleFiles_Integration_Success() {
        // Given
        // Use JPEG content for both files
        byte[] jpegContent1 = new byte[]{
            (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00, (byte)0xFF, (byte)0xD9
        };
        MockMultipartFile file1 = new MockMultipartFile(
                "file1", "test1.jpg", "image/jpeg", jpegContent1);
        MockMultipartFile file2 = new MockMultipartFile(
                "file2", "test2.jpg", "image/jpeg", jpegContent1);

        // When
        var responses = minioService.uploadMultipleFiles(java.util.List.of(file1, file2), FileType.IMAGE);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        // Cleanup
        responses.forEach(response -> 
            minioService.deleteFile(response.getFileName(), FileType.IMAGE));
    }

    @Test
    void getPresignedUrl_Integration_Success() {
        // Given
        // Use JPEG content
        byte[] jpegContent = new byte[]{
            (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00, (byte)0xFF, (byte)0xD9
        };
        MockMultipartFile file = new MockMultipartFile(
                "file", "presigned-test.jpg", "image/jpeg", jpegContent);

        // When - Upload file first
        FileUploadResponse uploadResponse = minioService.uploadFile(file, FileType.IMAGE);

        // When - Get presigned URL
        String presignedUrl = minioService.getPresignedUrl(uploadResponse.getFileName(), FileType.IMAGE, 60);

        // Then
        assertNotNull(presignedUrl);
        assertTrue(presignedUrl.contains(uploadResponse.getFileName()));
        assertTrue(presignedUrl.contains("X-Amz-Algorithm"));
        assertTrue(presignedUrl.contains("X-Amz-Credential"));

        // Cleanup
        minioService.deleteFile(uploadResponse.getFileName(), FileType.IMAGE);
    }

    @Test
    void listFiles_Integration_Success() {
        // Given
        // Use JPEG content
        byte[] jpegContent = new byte[]{
            (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00, (byte)0xFF, (byte)0xD9
        };
        MockMultipartFile file = new MockMultipartFile(
                "file", "list-test.jpg", "image/jpeg", jpegContent);

        // When - Upload file
        FileUploadResponse uploadResponse = minioService.uploadFile(file, FileType.IMAGE);

        // When - List files
        var files = minioService.listFiles(FileType.IMAGE);

        // Then
        assertNotNull(files);
        assertTrue(files.stream().anyMatch(f -> f.getFileName().equals(uploadResponse.getFileName())));

        // Cleanup
        minioService.deleteFile(uploadResponse.getFileName(), FileType.IMAGE);
    }

    @Test
    void getFileMetadata_Integration_Success() {
        // Given
        // Use JPEG content
        byte[] jpegContent = new byte[]{
            (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00, (byte)0xFF, (byte)0xD9
        };
        MockMultipartFile file = new MockMultipartFile(
                "file", "metadata-test.jpg", "image/jpeg", jpegContent);

        // When - Upload file
        FileUploadResponse uploadResponse = minioService.uploadFile(file, FileType.IMAGE);

        // When - Get metadata
        var metadata = minioService.getFileMetadata(uploadResponse.getFileName(), FileType.IMAGE);

        // Then
        assertNotNull(metadata);
        assertEquals(uploadResponse.getFileName(), metadata.getFileName());
        assertEquals("image/jpeg", metadata.getContentType());
        assertTrue(metadata.getFileSize() > 0);
        assertNotNull(metadata.getCreatedAt());
        assertNotNull(metadata.getEtag());

        // Cleanup
        minioService.deleteFile(uploadResponse.getFileName(), FileType.IMAGE);
    }

    @Test
    void containerIsRunning() {
        assertTrue(minioContainer.isRunning());
        assertNotNull(minioContainer.getS3URL());
        assertEquals("testuser", minioContainer.getUserName());
        assertEquals("testpassword", minioContainer.getPassword());
    }
}

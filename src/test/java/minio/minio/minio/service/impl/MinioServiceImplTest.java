package minio.minio.minio.service.impl;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import minio.minio.minio.config.MinioProperties;
import minio.minio.minio.dto.FileDownloadResponse;
import minio.minio.minio.dto.FileMetadata;
import minio.minio.minio.dto.FileUploadResponse;
import minio.minio.minio.enums.FileType;
import minio.minio.minio.exception.FileNotFoundException;
import minio.minio.minio.exception.InvalidFileTypeException;
import minio.minio.minio.exception.MinioException;
import minio.minio.minio.service.PdfProcessingService;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class MinioServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @Mock
    private PdfProcessingService pdfProcessingService;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Tika tika;

    @InjectMocks
    private MinioServiceImpl minioService;

    private MinioProperties.Bucket bucket;

    @BeforeEach
    void setUp() {
        bucket = new MinioProperties.Bucket();
        lenient().when(minioProperties.getBucket()).thenReturn(bucket);
        lenient().when(minioProperties.getUrl()).thenReturn("http://localhost:9000");
    }

    @Test
    void uploadFile_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        // Use a minimal JPEG header that Tika will recognize as image/jpeg
        byte[] fileContent = new byte[]{
            (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00, (byte)0xFF, (byte)0xD9
        };
        
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn((long) fileContent.length);
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));
        lenient().when(multipartFile.getContentType()).thenReturn(contentType);
        
        // Mock Tika to return the expected content type (lenient as Tika detection might not be called)
        lenient().when(tika.detect(any(InputStream.class), eq(fileName))).thenReturn(contentType);
        
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        
        ObjectWriteResponse writeResponse = mock(ObjectWriteResponse.class);
        when(writeResponse.etag()).thenReturn("test-etag");
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(writeResponse);

        // When
        FileUploadResponse response = minioService.uploadFile(multipartFile, FileType.IMAGE);

        // Then
        assertNotNull(response);
        assertEquals(fileName, response.getOriginalFileName());
        assertEquals(contentType, response.getContentType());
        assertEquals((long) fileContent.length, response.getFileSize());
        assertNotNull(response.getFileUrl());
        assertNotNull(response.getUploadedAt());
        
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void uploadFile_EmptyFile_ThrowsException() {
        // Given
        when(multipartFile.isEmpty()).thenReturn(true);

        // When & Then
        assertThrows(MinioException.class, () -> 
            minioService.uploadFile(multipartFile, FileType.IMAGE));
    }

    @Test
    void uploadFile_FileSizeExceeded_ThrowsException() {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(FileType.IMAGE.getMaxFileSize() + 1);

        // When & Then
        assertThrows(MinioException.class, () -> 
            minioService.uploadFile(multipartFile, FileType.IMAGE));
    }

    @Test
    void uploadFile_InvalidMimeType_ThrowsException() throws Exception {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1000L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");

        // When & Then
        assertThrows(InvalidFileTypeException.class, () -> 
            minioService.uploadFile(multipartFile, FileType.IMAGE));
    }

    @Test
    void downloadFile_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        FileType fileType = FileType.IMAGE;
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
        
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(mock(StatObjectResponse.class));
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(mockResponse.transferTo(any())).thenAnswer(invocation -> {
            java.io.OutputStream os = invocation.getArgument(0);
            os.write("test content".getBytes());
            return "test content".getBytes().length;
        });
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);
        
        StatObjectResponse statResponse = mock(StatObjectResponse.class);
        when(statResponse.size()).thenReturn(100L);
        when(statResponse.contentType()).thenReturn("image/jpeg");
        when(statResponse.lastModified()).thenReturn(ZonedDateTime.now());
        when(statResponse.etag()).thenReturn("test-etag");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(statResponse);

        // When
        FileDownloadResponse response = minioService.downloadFile(fileName, fileType);

        // Then
        assertNotNull(response);
        assertEquals(fileName, response.getFileName());
        assertEquals("image/jpeg", response.getContentType());
        assertEquals(100L, response.getFileSize());
        assertNotNull(response.getInputStream());
    }

    @Test
    void downloadFile_FileNotFound_ThrowsException() throws Exception {
        // Given
        String fileName = "nonexistent.jpg";
        FileType fileType = FileType.IMAGE;
        
        ErrorResponseException errorResponse = mock(ErrorResponseException.class);
        when(errorResponse.errorResponse()).thenReturn(mock(io.minio.messages.ErrorResponse.class));
        when(errorResponse.errorResponse().code()).thenReturn("NoSuchKey");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(errorResponse);

        // When & Then
        assertThrows(FileNotFoundException.class, () -> 
            minioService.downloadFile(fileName, fileType));
    }

    @Test
    void deleteFile_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        FileType fileType = FileType.IMAGE;
        
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // When
        boolean result = minioService.deleteFile(fileName, fileType);

        // Then
        assertTrue(result);
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void deleteFile_Exception_ReturnsFalse() throws Exception {
        // Given
        String fileName = "test.jpg";
        FileType fileType = FileType.IMAGE;
        
        doThrow(new RuntimeException("Delete failed")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // When
        boolean result = minioService.deleteFile(fileName, fileType);

        // Then
        assertFalse(result);
    }

    @Test
    void getFileMetadata_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        FileType fileType = FileType.IMAGE;
        
        StatObjectResponse statResponse = mock(StatObjectResponse.class);
        when(statResponse.size()).thenReturn(1000L);
        when(statResponse.contentType()).thenReturn("image/jpeg");
        when(statResponse.lastModified()).thenReturn(ZonedDateTime.now());
        when(statResponse.etag()).thenReturn("test-etag");
        
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(statResponse);

        // When
        FileMetadata metadata = minioService.getFileMetadata(fileName, fileType);

        // Then
        assertNotNull(metadata);
        assertEquals(fileName, metadata.getFileName());
        assertEquals("image/jpeg", metadata.getContentType());
        assertEquals(1000L, metadata.getFileSize());
        assertEquals("test-etag", metadata.getEtag());
    }

    @Test
    void listFiles_Success() throws Exception {
        // Given
        FileType fileType = FileType.IMAGE;
        
        List<Result<Item>> mockResults = new ArrayList<>();
        Item mockItem = mock(Item.class);
        when(mockItem.objectName()).thenReturn("test.jpg");
        when(mockItem.size()).thenReturn(1000L);
        when(mockItem.lastModified()).thenReturn(ZonedDateTime.now());
        when(mockItem.etag()).thenReturn("test-etag");
        
        Result<Item> mockResult = mock(Result.class);
        when(mockResult.get()).thenReturn(mockItem);
        mockResults.add(mockResult);
        
        when(minioClient.listObjects(any(ListObjectsArgs.class))).thenReturn(mockResults);

        // When
        List<FileMetadata> files = minioService.listFiles(fileType);

        // Then
        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("test.jpg", files.get(0).getFileName());
    }

    @Test
    void fileExists_True() throws Exception {
        // Given
        String fileName = "test.jpg";
        FileType fileType = FileType.IMAGE;
        
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(mock(StatObjectResponse.class));

        // When
        boolean exists = minioService.fileExists(fileName, fileType);

        // Then
        assertTrue(exists);
    }

    @Test
    void fileExists_False() throws Exception {
        // Given
        String fileName = "nonexistent.jpg";
        FileType fileType = FileType.IMAGE;
        
        ErrorResponseException errorResponse = mock(ErrorResponseException.class);
        when(errorResponse.errorResponse()).thenReturn(mock(io.minio.messages.ErrorResponse.class));
        when(errorResponse.errorResponse().code()).thenReturn("NoSuchKey");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(errorResponse);

        // When
        boolean exists = minioService.fileExists(fileName, fileType);

        // Then
        assertFalse(exists);
    }

    @Test
    void getFileUrl_Success() {
        // Given
        String fileName = "test.jpg";
        FileType fileType = FileType.IMAGE;
        String expectedUrl = "http://localhost:9000/file-service-images/test.jpg";

        // When
        String url = minioService.getFileUrl(fileName, fileType);

        // Then
        assertEquals(expectedUrl, url);
    }

    @Test
    void getPresignedUrl_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        FileType fileType = FileType.IMAGE;
        int expiryMinutes = 60;
        String expectedUrl = "http://localhost:9000/presigned-url";
        
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(mock(StatObjectResponse.class));
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn(expectedUrl);

        // When
        String url = minioService.getPresignedUrl(fileName, fileType, expiryMinutes);

        // Then
        assertEquals(expectedUrl, url);
    }

    @Test
    void getPresignedUrl_FileNotFound_ThrowsException() throws Exception {
        // Given
        String fileName = "nonexistent.jpg";
        FileType fileType = FileType.IMAGE;
        int expiryMinutes = 60;
        
        ErrorResponseException errorResponse = mock(ErrorResponseException.class);
        when(errorResponse.errorResponse()).thenReturn(mock(io.minio.messages.ErrorResponse.class));
        when(errorResponse.errorResponse().code()).thenReturn("NoSuchKey");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(errorResponse);

        // When & Then
        assertThrows(FileNotFoundException.class, () -> 
            minioService.getPresignedUrl(fileName, fileType, expiryMinutes));
    }

    @Test
    void uploadMultipleFiles_Success() throws Exception {
        // Given
        List<MultipartFile> files = List.of(multipartFile);
        FileType fileType = FileType.IMAGE;
        
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1000L);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        
        ObjectWriteResponse writeResponse = mock(ObjectWriteResponse.class);
        when(writeResponse.etag()).thenReturn("test-etag");
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(writeResponse);

        // When
        List<FileUploadResponse> responses = minioService.uploadMultipleFiles(files, fileType);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertNotNull(responses.get(0).getFileUrl());
    }

    @Test
    void uploadMultipleFiles_EmptyList_ThrowsException() {
        // Given
        List<MultipartFile> files = new ArrayList<>();
        FileType fileType = FileType.IMAGE;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            minioService.uploadMultipleFiles(files, fileType));
    }

    @Test
    void getFileContentAsBytes_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        FileType fileType = FileType.IMAGE;
        byte[] expectedContent = "test content".getBytes();
        
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(mock(StatObjectResponse.class));
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(mockResponse.transferTo(any())).thenAnswer(invocation -> {
            java.io.OutputStream os = invocation.getArgument(0);
            os.write(expectedContent);
            return expectedContent.length;
        });
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);

        // When
        byte[] content = minioService.getFileContentAsBytes(fileName, fileType);

        // Then
        assertArrayEquals(expectedContent, content);
    }

    @Test
    void extractFileNameFromUrl_Success() {
        // Given
        String url = "http://localhost:9000/titan-images/test-file.jpg";
        String expectedFileName = "test-file.jpg";

        // When
        String fileName = minioService.extractFileNameFromUrl(url);

        // Then
        assertEquals(expectedFileName, fileName);
    }

    @Test
    void extractFileNameFromUrl_NullUrl_ReturnsNull() {
        // When
        String fileName = minioService.extractFileNameFromUrl(null);

        // Then
        assertNull(fileName);
    }

    @Test
    void extractFileNameFromUrl_EmptyUrl_ReturnsNull() {
        // When
        String fileName = minioService.extractFileNameFromUrl("");

        // Then
        assertNull(fileName);
    }
}

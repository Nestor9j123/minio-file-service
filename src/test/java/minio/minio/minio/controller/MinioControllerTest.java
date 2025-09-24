package minio.minio.minio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import minio.minio.minio.dto.FileDownloadResponse;
import minio.minio.minio.dto.FileMetadata;
import minio.minio.minio.dto.FileUploadResponse;
import minio.minio.minio.enums.FileType;
import minio.minio.minio.exception.FileNotFoundException;
import minio.minio.minio.exception.MinioException;
import minio.minio.minio.service.MinioService;
import minio.minio.minio.service.PdfProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MinioController.class)
class MinioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MinioService minioService;

    @MockBean
    private PdfProcessingService pdfProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    private FileUploadResponse mockUploadResponse;
    private FileDownloadResponse mockDownloadResponse;
    private FileMetadata mockMetadata;

    @BeforeEach
    void setUp() {
        mockUploadResponse = FileUploadResponse.builder()
                .fileName("test-file.jpg")
                .originalFileName("test.jpg")
                .fileUrl("http://localhost:9000/file-service-images/test-file.jpg")
                .bucketName("file-service-images")
                .fileSize(1000L)
                .contentType("image/jpeg")
                .uploadedAt(LocalDateTime.now())
                .fileId("test-etag")
                .build();

        mockDownloadResponse = FileDownloadResponse.builder()
                .inputStream(new ByteArrayInputStream("test content".getBytes()))
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .fileSize(1000L)
                .build();

        mockMetadata = FileMetadata.builder()
                .fileName("test.jpg")
                .originalFileName("test.jpg")
                .bucketName("file-service-images")
                .fileSize(1000L)
                .contentType("image/jpeg")
                .createdAt(LocalDateTime.now())
                .lastModified(LocalDateTime.now())
                .etag("test-etag")
                .build();
    }

    @Test
    void uploadFile_Success() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test content".getBytes());
        
        when(minioService.uploadFile(any(), eq(FileType.IMAGE), isNull()))
                .thenReturn(mockUploadResponse);

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/IMAGE")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test-file.jpg"))
                .andExpect(jsonPath("$.originalFileName").value("test.jpg"))
                .andExpect(jsonPath("$.fileUrl").value("http://localhost:9000/file-service-images/test-file.jpg"))
                .andExpect(jsonPath("$.contentType").value("image/jpeg"))
                .andExpect(jsonPath("$.fileSize").value(1000));

        verify(minioService).uploadFile(any(), eq(FileType.IMAGE), isNull());
    }

    @Test
    void uploadFile_WithCustomFileName_Success() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test content".getBytes());
        String customFileName = "custom-name.jpg";
        
        when(minioService.uploadFile(any(), eq(FileType.IMAGE), eq(customFileName)))
                .thenReturn(mockUploadResponse);

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/IMAGE")
                        .file(file)
                        .param("customFileName", customFileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test-file.jpg"));

        verify(minioService).uploadFile(any(), eq(FileType.IMAGE), eq(customFileName));
    }

    @Test
    void uploadFile_InvalidFileType_ThrowsException() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test content".getBytes());
        
        when(minioService.uploadFile(any(), eq(FileType.IMAGE), isNull()))
                .thenThrow(new MinioException("Invalid file type"));

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/IMAGE")
                        .file(file))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void uploadMultipleFiles_Success() throws Exception {
        // Given
        MockMultipartFile file1 = new MockMultipartFile(
                "files", "test1.jpg", "image/jpeg", "test content 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "test2.jpg", "image/jpeg", "test content 2".getBytes());
        
        List<FileUploadResponse> responses = Arrays.asList(mockUploadResponse, mockUploadResponse);
        when(minioService.uploadMultipleFiles(any(), eq(FileType.IMAGE)))
                .thenReturn(responses);

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/multiple/IMAGE")
                        .file(file1)
                        .file(file2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(minioService).uploadMultipleFiles(any(), eq(FileType.IMAGE));
    }

    @Test
    void downloadFile_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        when(minioService.downloadFile(fileName, FileType.IMAGE))
                .thenReturn(mockDownloadResponse);

        // When & Then
        mockMvc.perform(get("/api/files/download/IMAGE/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.jpg\""));

        verify(minioService).downloadFile(fileName, FileType.IMAGE);
    }

    @Test
    void downloadFile_FileNotFound_ReturnsNotFound() throws Exception {
        // Given
        String fileName = "nonexistent.jpg";
        when(minioService.downloadFile(fileName, FileType.IMAGE))
                .thenThrow(new FileNotFoundException("File not found"));

        // When & Then
        mockMvc.perform(get("/api/files/download/IMAGE/{fileName}", fileName))
                .andExpect(status().isNotFound());
    }

    @Test
    void streamFile_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        when(minioService.downloadFile(fileName, FileType.IMAGE))
                .thenReturn(mockDownloadResponse);

        // When & Then
        mockMvc.perform(get("/api/files/stream/IMAGE/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"test.jpg\""));

        verify(minioService).downloadFile(fileName, FileType.IMAGE);
    }

    @Test
    void deleteFile_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        when(minioService.deleteFile(fileName, FileType.IMAGE)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/files/IMAGE/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(content().string("Fichier supprimé avec succès"));

        verify(minioService).deleteFile(fileName, FileType.IMAGE);
    }

    @Test
    void deleteFile_FileNotFound_ReturnsNotFound() throws Exception {
        // Given
        String fileName = "nonexistent.jpg";
        when(minioService.deleteFile(fileName, FileType.IMAGE)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/files/IMAGE/{fileName}", fileName))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFileMetadata_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        when(minioService.getFileMetadata(fileName, FileType.IMAGE))
                .thenReturn(mockMetadata);

        // When & Then
        mockMvc.perform(get("/api/files/metadata/IMAGE/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.jpg"))
                .andExpect(jsonPath("$.contentType").value("image/jpeg"))
                .andExpect(jsonPath("$.fileSize").value(1000));

        verify(minioService).getFileMetadata(fileName, FileType.IMAGE);
    }

    @Test
    void listFiles_Success() throws Exception {
        // Given
        List<FileMetadata> files = Arrays.asList(mockMetadata);
        when(minioService.listFiles(FileType.IMAGE)).thenReturn(files);

        // When & Then
        mockMvc.perform(get("/api/files/list/IMAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fileName").value("test.jpg"));

        verify(minioService).listFiles(FileType.IMAGE);
    }

    @Test
    void fileExists_True() throws Exception {
        // Given
        String fileName = "test.jpg";
        when(minioService.fileExists(fileName, FileType.IMAGE)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/files/exists/IMAGE/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(minioService).fileExists(fileName, FileType.IMAGE);
    }

    @Test
    void fileExists_False() throws Exception {
        // Given
        String fileName = "nonexistent.jpg";
        when(minioService.fileExists(fileName, FileType.IMAGE)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/files/exists/IMAGE/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void getFileUrl_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        String expectedUrl = "http://localhost:9000/file-service-images/test.jpg";
        when(minioService.getFileUrl(fileName, FileType.IMAGE)).thenReturn(expectedUrl);

        // When & Then
        mockMvc.perform(get("/api/files/url/IMAGE/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUrl));

        verify(minioService).getFileUrl(fileName, FileType.IMAGE);
    }

    @Test
    void getPresignedUrl_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        int expiryMinutes = 120;
        String expectedUrl = "http://localhost:9000/presigned-url";
        when(minioService.getPresignedUrl(fileName, FileType.IMAGE, expiryMinutes))
                .thenReturn(expectedUrl);

        // When & Then
        mockMvc.perform(get("/api/files/presigned-url/IMAGE/{fileName}", fileName)
                        .param("expiryMinutes", String.valueOf(expiryMinutes)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUrl));

        verify(minioService).getPresignedUrl(fileName, FileType.IMAGE, expiryMinutes);
    }

    @Test
    void getPresignedUrl_DefaultExpiry_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        String expectedUrl = "http://localhost:9000/presigned-url";
        when(minioService.getPresignedUrl(fileName, FileType.IMAGE, 60))
                .thenReturn(expectedUrl);

        // When & Then
        mockMvc.perform(get("/api/files/presigned-url/IMAGE/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUrl));

        verify(minioService).getPresignedUrl(fileName, FileType.IMAGE, 60);
    }

    @Test
    void generatePdfThumbnail_Success() throws Exception {
        // Given
        String fileName = "test.pdf";
        byte[] thumbnailData = "thumbnail data".getBytes();
        byte[] fileContent = "pdf content".getBytes();
        
        when(minioService.getFileContentAsBytes(fileName, FileType.PDF))
                .thenReturn(fileContent);
        when(pdfProcessingService.generatePdfThumbnail(any(), eq(200), eq(200)))
                .thenReturn(thumbnailData);

        // When & Then
        mockMvc.perform(get("/api/files/pdf/thumbnail/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"test.pdf_thumbnail.png\""));

        verify(minioService).getFileContentAsBytes(fileName, FileType.PDF);
        verify(pdfProcessingService).generatePdfThumbnail(any(), eq(200), eq(200));
    }

    @Test
    void generatePdfThumbnail_WithCustomSize_Success() throws Exception {
        // Given
        String fileName = "test.pdf";
        int width = 300;
        int height = 400;
        byte[] thumbnailData = "thumbnail data".getBytes();
        byte[] fileContent = "pdf content".getBytes();
        
        when(minioService.getFileContentAsBytes(fileName, FileType.PDF))
                .thenReturn(fileContent);
        when(pdfProcessingService.generatePdfThumbnail(any(), eq(width), eq(height)))
                .thenReturn(thumbnailData);

        // When & Then
        mockMvc.perform(get("/api/files/pdf/thumbnail/{fileName}", fileName)
                        .param("width", String.valueOf(width))
                        .param("height", String.valueOf(height)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));

        verify(pdfProcessingService).generatePdfThumbnail(any(), eq(width), eq(height));
    }

    @Test
    void generatePdfThumbnail_FileNotFound_ReturnsBadRequest() throws Exception {
        // Given
        String fileName = "nonexistent.pdf";
        when(minioService.getFileContentAsBytes(fileName, FileType.PDF))
                .thenThrow(new FileNotFoundException("File not found"));

        // When & Then
        mockMvc.perform(get("/api/files/pdf/thumbnail/{fileName}", fileName))
                .andExpect(status().isBadRequest());
    }

    @Test
    void extractPdfText_Success() throws Exception {
        // Given
        String fileName = "test.pdf";
        String extractedText = "This is extracted text from PDF";
        byte[] fileContent = "pdf content".getBytes();
        
        when(minioService.getFileContentAsBytes(fileName, FileType.PDF))
                .thenReturn(fileContent);
        when(pdfProcessingService.extractTextFromPdf(any()))
                .thenReturn(extractedText);

        // When & Then
        mockMvc.perform(get("/api/files/pdf/text/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain;charset=UTF-8"))
                .andExpect(content().string(extractedText));

        verify(minioService).getFileContentAsBytes(fileName, FileType.PDF);
        verify(pdfProcessingService).extractTextFromPdf(any());
    }

    @Test
    void extractPdfText_FileNotFound_ReturnsBadRequest() throws Exception {
        // Given
        String fileName = "nonexistent.pdf";
        when(minioService.getFileContentAsBytes(fileName, FileType.PDF))
                .thenThrow(new FileNotFoundException("File not found"));

        // When & Then
        mockMvc.perform(get("/api/files/pdf/text/{fileName}", fileName))
                .andExpect(status().isBadRequest());
    }

    @Test
    void extractPdfText_ProcessingError_ReturnsBadRequest() throws Exception {
        // Given
        String fileName = "corrupted.pdf";
        byte[] fileContent = "corrupted pdf content".getBytes();
        
        when(minioService.getFileContentAsBytes(fileName, FileType.PDF))
                .thenReturn(fileContent);
        when(pdfProcessingService.extractTextFromPdf(any()))
                .thenThrow(new RuntimeException("PDF processing error"));

        // When & Then
        mockMvc.perform(get("/api/files/pdf/text/{fileName}", fileName))
                .andExpect(status().isBadRequest());
    }
}

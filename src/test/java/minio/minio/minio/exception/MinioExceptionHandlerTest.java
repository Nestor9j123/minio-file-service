package minio.minio.minio.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import minio.minio.minio.controller.MinioController;
import minio.minio.minio.service.MinioService;
import minio.minio.minio.service.PdfProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.FileNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {MinioController.class, MinioExceptionHandler.class})
class MinioExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MinioService minioService;

    @MockBean
    private PdfProcessingService pdfProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleFileNotFoundException_ReturnsNotFound() throws Exception {
        // Given
        String fileName = "nonexistent.jpg";
        when(minioService.downloadFile(eq(fileName), any()))
                .thenThrow(new FileNotFoundException("File not found: " + fileName));

        // When & Then
        mockMvc.perform(get("/api/files/download/IMAGE/{fileName}", fileName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.errorCode").value("FILE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("File not found: " + fileName))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleInvalidFileTypeException_ReturnsBadRequest() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test content".getBytes());
        
        when(minioService.uploadFile(any(), any(), any()))
                .thenThrow(new InvalidFileTypeException("text/plain"));

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/IMAGE")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_FILE_TYPE"))
                .andExpect(jsonPath("$.message").value("Invalid file type: text/plain"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleMinioException_ReturnsInternalServerError() throws Exception {
        // Given
        String fileName = "test.jpg";
        when(minioService.getFileMetadata(eq(fileName), any()))
                .thenThrow(new MinioException("MinIO connection failed"));

        // When & Then
        mockMvc.perform(get("/api/files/metadata/IMAGE/{fileName}", fileName))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.errorCode").value("MINIO_ERROR"))
                .andExpect(jsonPath("$.message").value("MinIO connection failed"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleMaxUploadSizeExceededException_ReturnsBadRequest() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.jpg", "image/jpeg", new byte[1024]);
        
        when(minioService.uploadFile(any(), any(), any()))
                .thenThrow(new MaxUploadSizeExceededException(1024));

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/IMAGE")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errorCode").value("FILE_SIZE_EXCEEDED"))
                .andExpect(jsonPath("$.message").value("La taille du fichier dépasse la limite autorisée"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleIllegalArgumentException_ReturnsBadRequest() throws Exception {
        // Given
        when(minioService.uploadMultipleFiles(any(), any()))
                .thenThrow(new IllegalArgumentException("Aucun fichier fourni pour l'upload"));

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/multiple/IMAGE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message").value("Aucun fichier fourni pour l'upload"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleMinioException_WithCause_ReturnsInternalServerError() throws Exception {
        // Given
        String fileName = "test.jpg";
        MinioException exception = new MinioException("Failed to upload file", 
                new RuntimeException("Connection timeout"));
        
        when(minioService.getFileMetadata(eq(fileName), any()))
                .thenThrow(exception);

        // When & Then
        mockMvc.perform(get("/api/files/metadata/IMAGE/{fileName}", fileName))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.errorCode").value("MINIO_ERROR"))
                .andExpect(jsonPath("$.message").value("Failed to upload file"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleInvalidFileTypeException_WithSpecificMimeType_ReturnsBadRequest() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "pdf content".getBytes());
        
        when(minioService.uploadFile(any(), any(), any()))
                .thenThrow(new InvalidFileTypeException("application/pdf"));

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/IMAGE")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_FILE_TYPE"))
                .andExpect(jsonPath("$.message").value("Invalid file type: application/pdf"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleFileNotFoundException_WithCustomMessage_ReturnsNotFound() throws Exception {
        // Given
        String fileName = "missing.jpg";
        String customMessage = "The requested file could not be found in the storage";
        
        when(minioService.downloadFile(eq(fileName), any()))
                .thenThrow(new FileNotFoundException(customMessage));

        // When & Then
        mockMvc.perform(get("/api/files/download/IMAGE/{fileName}", fileName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.errorCode").value("FILE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(customMessage))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void errorResponseStructure_ContainsAllRequiredFields() throws Exception {
        // Given
        String fileName = "test.jpg";
        when(minioService.downloadFile(eq(fileName), any()))
                .thenThrow(new FileNotFoundException("File not found"));

        // When & Then
        mockMvc.perform(get("/api/files/download/IMAGE/{fileName}", fileName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.errorCode").isString())
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void handleMinioException_EmptyMessage_ReturnsInternalServerError() throws Exception {
        // Given
        String fileName = "test.jpg";
        when(minioService.getFileMetadata(eq(fileName), any()))
                .thenThrow(new MinioException(""));

        // When & Then
        mockMvc.perform(get("/api/files/metadata/IMAGE/{fileName}", fileName))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.errorCode").value("MINIO_ERROR"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}

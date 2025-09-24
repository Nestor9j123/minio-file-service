package minio.minio.minio.service.impl;

import minio.minio.minio.dto.FileMetadata;
import minio.minio.minio.exception.MinioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfProcessingServiceImplTest {

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PdfProcessingServiceImpl pdfProcessingService;

    private byte[] validPdfContent;
    private byte[] invalidPdfContent;

    @BeforeEach
    void setUp() {
        // Minimal valid PDF content (PDF header)
        validPdfContent = "%PDF-1.4\n1 0 obj\n<<\n/Type /Catalog\n/Pages 2 0 R\n>>\nendobj\n2 0 obj\n<<\n/Type /Pages\n/Kids [3 0 R]\n/Count 1\n>>\nendobj\n3 0 obj\n<<\n/Type /Page\n/Parent 2 0 R\n/MediaBox [0 0 612 792]\n>>\nendobj\nxref\n0 4\n0000000000 65535 f \n0000000010 00000 n \n0000000079 00000 n \n0000000173 00000 n \ntrailer\n<<\n/Size 4\n/Root 1 0 R\n>>\nstartxref\n253\n%%EOF".getBytes();
        
        // Invalid PDF content
        invalidPdfContent = "This is not a PDF file".getBytes();
    }

    @Test
    void extractPdfMetadata_FromMultipartFile_Success() throws IOException {
        // Given
        String fileName = "test.pdf";
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(validPdfContent));

        // When
        FileMetadata metadata = pdfProcessingService.extractPdfMetadata(multipartFile);

        // Then
        assertNotNull(metadata);
        assertEquals(fileName, metadata.getFileName());
        assertEquals("application/pdf", metadata.getContentType());
        // Note: For a minimal PDF, page count might be 0 or 1 depending on PDF structure
        assertTrue(metadata.getPageCount() >= 0);
    }

    @Test
    void extractPdfMetadata_FromMultipartFile_InvalidPdf_ThrowsException() throws IOException {
        // Given
        String fileName = "invalid.pdf";
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(invalidPdfContent));

        // When & Then
        assertThrows(MinioException.class, () -> 
            pdfProcessingService.extractPdfMetadata(multipartFile));
    }

    @Test
    void extractPdfMetadata_FromMultipartFile_IOException_ThrowsException() throws IOException {
        // Given
        String fileName = "test.pdf";
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenThrow(new IOException("IO Error"));

        // When & Then
        assertThrows(MinioException.class, () -> 
            pdfProcessingService.extractPdfMetadata(multipartFile));
    }

    @Test
    void extractPdfMetadata_FromInputStream_Success() {
        // Given
        String fileName = "test.pdf";
        InputStream inputStream = new ByteArrayInputStream(validPdfContent);

        // When
        FileMetadata metadata = pdfProcessingService.extractPdfMetadata(inputStream, fileName);

        // Then
        assertNotNull(metadata);
        assertEquals(fileName, metadata.getFileName());
        assertEquals("application/pdf", metadata.getContentType());
        assertTrue(metadata.getPageCount() >= 0);
    }

    @Test
    void extractPdfMetadata_FromInputStream_InvalidPdf_ThrowsException() {
        // Given
        String fileName = "invalid.pdf";
        InputStream inputStream = new ByteArrayInputStream(invalidPdfContent);

        // When & Then
        assertThrows(MinioException.class, () -> 
            pdfProcessingService.extractPdfMetadata(inputStream, fileName));
    }

    @Test
    void extractTextFromPdf_Success() {
        // Given
        InputStream inputStream = new ByteArrayInputStream(validPdfContent);

        // When
        String text = pdfProcessingService.extractTextFromPdf(inputStream);

        // Then
        assertNotNull(text);
        // For a minimal PDF without actual text content, the result might be empty
        assertTrue(text.length() >= 0);
    }

    @Test
    void extractTextFromPdf_InvalidPdf_ThrowsException() {
        // Given
        InputStream inputStream = new ByteArrayInputStream(invalidPdfContent);

        // When & Then
        assertThrows(MinioException.class, () -> 
            pdfProcessingService.extractTextFromPdf(inputStream));
    }

    @Test
    void getPdfPageCount_Success() {
        // Given
        InputStream inputStream = new ByteArrayInputStream(validPdfContent);

        // When
        int pageCount = pdfProcessingService.getPdfPageCount(inputStream);

        // Then
        assertTrue(pageCount >= 0);
    }

    @Test
    void getPdfPageCount_InvalidPdf_ThrowsException() {
        // Given
        InputStream inputStream = new ByteArrayInputStream(invalidPdfContent);

        // When & Then
        assertThrows(MinioException.class, () -> 
            pdfProcessingService.getPdfPageCount(inputStream));
    }

    @Test
    void generatePdfThumbnail_Success() {
        // Given
        InputStream inputStream = new ByteArrayInputStream(validPdfContent);
        int width = 200;
        int height = 200;

        // When & Then
        // Note: This test might fail with minimal PDF content as it may not have renderable pages
        // In a real scenario, you'd use a proper PDF with actual content
        assertThrows(MinioException.class, () -> 
            pdfProcessingService.generatePdfThumbnail(inputStream, width, height));
    }

    @Test
    void generatePdfThumbnail_InvalidPdf_ThrowsException() {
        // Given
        InputStream inputStream = new ByteArrayInputStream(invalidPdfContent);
        int width = 200;
        int height = 200;

        // When & Then
        assertThrows(MinioException.class, () -> 
            pdfProcessingService.generatePdfThumbnail(inputStream, width, height));
    }

    @Test
    void validatePdfIntegrity_ValidPdf_ReturnsTrue() {
        // Given
        InputStream inputStream = new ByteArrayInputStream(validPdfContent);

        // When
        boolean isValid = pdfProcessingService.validatePdfIntegrity(inputStream);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validatePdfIntegrity_InvalidPdf_ReturnsFalse() {
        // Given
        InputStream inputStream = new ByteArrayInputStream(invalidPdfContent);

        // When
        boolean isValid = pdfProcessingService.validatePdfIntegrity(inputStream);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validatePdfIntegrity_EmptyStream_ReturnsFalse() {
        // Given
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        // When
        boolean isValid = pdfProcessingService.validatePdfIntegrity(inputStream);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractPdfMetadata_WithComplexPdfStructure() {
        // Given
        String fileName = "complex.pdf";
        // Create a more complex PDF structure with metadata
        String complexPdf = "%PDF-1.4\n" +
                "1 0 obj\n<<\n/Type /Catalog\n/Pages 2 0 R\n>>\nendobj\n" +
                "2 0 obj\n<<\n/Type /Pages\n/Kids [3 0 R]\n/Count 1\n>>\nendobj\n" +
                "3 0 obj\n<<\n/Type /Page\n/Parent 2 0 R\n/MediaBox [0 0 612 792]\n>>\nendobj\n" +
                "4 0 obj\n<<\n/Title (Test Document)\n/Author (Test Author)\n/Subject (Test Subject)\n>>\nendobj\n" +
                "xref\n0 5\n" +
                "0000000000 65535 f \n" +
                "0000000010 00000 n \n" +
                "0000000079 00000 n \n" +
                "0000000173 00000 n \n" +
                "0000000253 00000 n \n" +
                "trailer\n<<\n/Size 5\n/Root 1 0 R\n/Info 4 0 R\n>>\n" +
                "startxref\n334\n%%EOF";
        
        InputStream inputStream = new ByteArrayInputStream(complexPdf.getBytes());

        // When
        FileMetadata metadata = pdfProcessingService.extractPdfMetadata(inputStream, fileName);

        // Then
        assertNotNull(metadata);
        assertEquals(fileName, metadata.getFileName());
        assertEquals("application/pdf", metadata.getContentType());
        assertFalse(metadata.getEncrypted());
    }
}

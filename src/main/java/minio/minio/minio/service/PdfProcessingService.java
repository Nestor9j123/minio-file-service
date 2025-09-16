package minio.minio.minio.service;

import minio.minio.minio.dto.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface PdfProcessingService {
    
    /**
     * Extract metadata from PDF file
     */
    FileMetadata extractPdfMetadata(MultipartFile file);
    
    /**
     * Extract metadata from PDF input stream
     */
    FileMetadata extractPdfMetadata(InputStream inputStream, String fileName);
    
    /**
     * Extract text content from PDF
     */
    String extractTextFromPdf(InputStream inputStream);
    
    /**
     * Get number of pages in PDF
     */
    int getPdfPageCount(InputStream inputStream);
    
    /**
     * Generate thumbnail from first page of PDF
     */
    byte[] generatePdfThumbnail(InputStream inputStream, int width, int height);
    
    /**
     * Validate PDF file integrity
     */
    boolean validatePdfIntegrity(InputStream inputStream);
}

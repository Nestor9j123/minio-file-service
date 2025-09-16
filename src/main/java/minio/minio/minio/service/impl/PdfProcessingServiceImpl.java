package minio.minio.minio.service.impl;

import lombok.extern.slf4j.Slf4j;
import minio.minio.minio.dto.FileMetadata;
import minio.minio.minio.exception.MinioException;
import minio.minio.minio.service.PdfProcessingService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
public class PdfProcessingServiceImpl implements PdfProcessingService {

    private final Tika tika = new Tika();

    @Override
    public FileMetadata extractPdfMetadata(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return extractPdfMetadata(inputStream, file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Error extracting PDF metadata from MultipartFile: {}", e.getMessage(), e);
            throw new MinioException("Failed to extract PDF metadata: " + e.getMessage(), e);
        }
    }

    @Override
    public FileMetadata extractPdfMetadata(InputStream inputStream, String fileName) {
        try {
            byte[] pdfBytes = inputStream.readAllBytes();
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDDocumentInformation info = document.getDocumentInformation();
                
                return FileMetadata.builder()
                        .fileName(fileName)
                        .contentType("application/pdf")
                        .pageCount(document.getNumberOfPages())
                        .title(info.getTitle())
                        .author(info.getAuthor())
                        .subject(info.getSubject())
                        .creator(info.getCreator())
                        .producer(info.getProducer())
                        .keywords(info.getKeywords())
                        .createdAt(info.getCreationDate() != null ? 
                            LocalDateTime.ofInstant(info.getCreationDate().toInstant(), ZoneId.systemDefault()) : null)
                        .lastModified(info.getModificationDate() != null ? 
                            LocalDateTime.ofInstant(info.getModificationDate().toInstant(), ZoneId.systemDefault()) : null)
                        .encrypted(document.isEncrypted())
                        .build();
            }
        } catch (IOException e) {
            log.error("Error extracting PDF metadata: {}", e.getMessage(), e);
            throw new MinioException("Failed to extract PDF metadata: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractTextFromPdf(InputStream inputStream) {
        try {
            byte[] pdfBytes = inputStream.readAllBytes();
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDFTextStripper textStripper = new PDFTextStripper();
                return textStripper.getText(document);
            }
        } catch (IOException e) {
            log.error("Error extracting text from PDF: {}", e.getMessage(), e);
            throw new MinioException("Failed to extract text from PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public int getPdfPageCount(InputStream inputStream) {
        try {
            byte[] pdfBytes = inputStream.readAllBytes();
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                return document.getNumberOfPages();
            }
        } catch (IOException e) {
            log.error("Error getting PDF page count: {}", e.getMessage(), e);
            throw new MinioException("Failed to get PDF page count: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generatePdfThumbnail(InputStream inputStream, int width, int height) {
        try {
            byte[] pdfBytes = inputStream.readAllBytes();
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                if (document.getNumberOfPages() == 0) {
                    throw new MinioException("PDF document has no pages");
                }
                
                PDFRenderer renderer = new PDFRenderer(document);
                BufferedImage image = renderer.renderImageWithDPI(0, 150); // First page at 150 DPI
                
                // Resize image to specified dimensions
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                resizedImage.getGraphics().drawImage(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
                
                // Convert to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "PNG", baos);
                return baos.toByteArray();
            }
        } catch (IOException e) {
            log.error("Error generating PDF thumbnail: {}", e.getMessage(), e);
            throw new MinioException("Failed to generate PDF thumbnail: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validatePdfIntegrity(InputStream inputStream) {
        try {
            byte[] pdfBytes = inputStream.readAllBytes();
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                // Try to access basic document properties
                document.getNumberOfPages();
                document.getDocumentInformation();
                
                // Try to render first page if it exists
                if (document.getNumberOfPages() > 0) {
                    PDFRenderer renderer = new PDFRenderer(document);
                    renderer.renderImageWithDPI(0, 72); // Low DPI for quick validation
                }
                
                return true;
            }
        } catch (Exception e) {
            log.warn("PDF validation failed: {}", e.getMessage());
            return false;
        }
    }
}

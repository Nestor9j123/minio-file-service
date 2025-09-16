package com.example.integration;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * Exemples d'intégration REST pour les backends
 * Service MinIO File Service
 */
public class RestExamples {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String MINIO_SERVICE_URL = "http://minio-service:8080";
    
    /**
     * EXEMPLE 1: Upload d'une photo utilisateur
     */
    public String uploadUserPhoto(Long userId, MultipartFile photo) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", photo.getResource());
            body.add("customFileName", "user_" + userId + "_photo.jpg");
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // POST /files/upload/PHOTO
            ResponseEntity<FileUploadResponse> response = restTemplate.postForEntity(
                MINIO_SERVICE_URL + "/files/upload/PHOTO", 
                requestEntity, 
                FileUploadResponse.class
            );
            
            return response.getBody().getFileUrl();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload photo: " + e.getMessage());
        }
    }
    
    /**
     * EXEMPLE 2: Upload d'un document PDF
     */
    public String uploadDocument(MultipartFile pdfFile, String documentName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", pdfFile.getResource());
            body.add("customFileName", documentName + ".pdf");
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // POST /files/upload/PDF
            ResponseEntity<FileUploadResponse> response = restTemplate.postForEntity(
                MINIO_SERVICE_URL + "/files/upload/PDF", 
                requestEntity, 
                FileUploadResponse.class
            );
            
            return response.getBody().getFileUrl();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload PDF: " + e.getMessage());
        }
    }
    
    /**
     * EXEMPLE 3: Récupérer les métadonnées d'un PDF
     */
    public FileMetadata getPdfMetadata(String fileName) {
        try {
            // GET /files/metadata/PDF/{fileName}
            ResponseEntity<FileMetadata> response = restTemplate.getForEntity(
                MINIO_SERVICE_URL + "/files/metadata/PDF/" + fileName,
                FileMetadata.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur métadonnées PDF: " + e.getMessage());
        }
    }
    
    /**
     * EXEMPLE 4: Générer un thumbnail PDF
     */
    public byte[] generatePdfThumbnail(String fileName) {
        try {
            // GET /files/pdf/thumbnail/{fileName}?width=200&height=200
            ResponseEntity<byte[]> response = restTemplate.getForEntity(
                MINIO_SERVICE_URL + "/files/pdf/thumbnail/" + fileName + "?width=200&height=200",
                byte[].class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur thumbnail PDF: " + e.getMessage());
        }
    }
    
    /**
     * EXEMPLE 5: Obtenir une URL présignée (accès temporaire)
     */
    public String getPresignedUrl(String fileName, String fileType, int expiryMinutes) {
        try {
            // GET /files/presigned-url/{fileType}/{fileName}?expiryMinutes=60
            ResponseEntity<String> response = restTemplate.getForEntity(
                MINIO_SERVICE_URL + "/files/presigned-url/" + fileType + "/" + fileName + "?expiryMinutes=" + expiryMinutes,
                String.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur URL présignée: " + e.getMessage());
        }
    }
    
    /**
     * EXEMPLE 6: Supprimer un fichier
     */
    public boolean deleteFile(String fileName, String fileType) {
        try {
            // DELETE /files/{fileType}/{fileName}
            restTemplate.delete(MINIO_SERVICE_URL + "/files/" + fileType + "/" + fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

// DTOs nécessaires
class FileUploadResponse {
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String contentType;
    // ... getters/setters
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}

class FileMetadata {
    private String fileName;
    private Integer pageCount;
    private String title;
    private String author;
    // ... getters/setters
    
    public Integer getPageCount() { return pageCount; }
    public String getTitle() { return title; }
}

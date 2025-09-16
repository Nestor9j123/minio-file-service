package com.fileservice.client;

import com.fileservice.client.dto.FileUploadResponse;
import com.fileservice.client.dto.FileMetadata;
import com.fileservice.client.enums.FileType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Client SDK pour interagir avec le service MinIO File Service
 * 
 * Usage:
 * MinioFileClient client = new MinioFileClient("http://minio-service:8080");
 * FileUploadResponse response = client.uploadFile(file, FileType.PHOTO);
 */
public class MinioFileClient {
    
    private final RestTemplate restTemplate;
    private final String baseUrl;
    
    public MinioFileClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.restTemplate = new RestTemplate();
    }
    
    public MinioFileClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.restTemplate = restTemplate;
    }
    
    /**
     * Upload un fichier à partir d'un byte array
     */
    public FileUploadResponse uploadFile(byte[] fileContent, String fileName, FileType fileType) {
        return uploadFile(fileContent, fileName, fileType, null);
    }
    
    /**
     * Upload un fichier à partir d'un InputStream
     */
    public FileUploadResponse uploadFile(InputStream inputStream, String fileName, FileType fileType) {
        try {
            byte[] fileContent = inputStream.readAllBytes();
            return uploadFile(fileContent, fileName, fileType, null);
        } catch (IOException e) {
            throw new MinioClientException("Failed to read input stream", e);
        }
    }
    
    /**
     * Upload un fichier avec nom personnalisé
     */
    public FileUploadResponse uploadFile(byte[] fileContent, String fileName, FileType fileType, String customFileName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // Créer une Resource à partir du byte array
            Resource fileResource = new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            
            body.add("file", fileResource);
            if (customFileName != null) {
                body.add("customFileName", customFileName);
            }
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            ResponseEntity<FileUploadResponse> response = restTemplate.postForEntity(
                baseUrl + "/files/upload/" + fileType.name(),
                requestEntity,
                FileUploadResponse.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new MinioClientException("Failed to upload file", e);
        }
    }
    
    /**
     * Télécharger un fichier
     */
    public InputStreamResource downloadFile(String fileName, FileType fileType) {
        try {
            ResponseEntity<InputStreamResource> response = restTemplate.getForEntity(
                baseUrl + "/files/download/" + fileType.name() + "/" + fileName,
                InputStreamResource.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new MinioClientException("Failed to download file", e);
        }
    }
    
    /**
     * Obtenir les métadonnées d'un fichier
     */
    public FileMetadata getFileMetadata(String fileName, FileType fileType) {
        try {
            ResponseEntity<FileMetadata> response = restTemplate.getForEntity(
                baseUrl + "/files/metadata/" + fileType.name() + "/" + fileName,
                FileMetadata.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new MinioClientException("Failed to get file metadata", e);
        }
    }
    
    /**
     * Supprimer un fichier
     */
    public boolean deleteFile(String fileName, FileType fileType) {
        try {
            restTemplate.delete(baseUrl + "/files/" + fileType.name() + "/" + fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtenir l'URL d'un fichier
     */
    public String getFileUrl(String fileName, FileType fileType) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/files/url/" + fileType.name() + "/" + fileName,
                String.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new MinioClientException("Failed to get file URL", e);
        }
    }
    
    /**
     * Obtenir une URL présignée
     */
    public String getPresignedUrl(String fileName, FileType fileType, int expiryMinutes) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/files/presigned-url/" + fileType.name() + "/" + fileName + "?expiryMinutes=" + expiryMinutes,
                String.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new MinioClientException("Failed to get presigned URL", e);
        }
    }
    
    /**
     * Lister les fichiers d'un type
     */
    @SuppressWarnings("unchecked")
    public List<FileMetadata> listFiles(FileType fileType) {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/files/list/" + fileType.name(),
                List.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new MinioClientException("Failed to list files", e);
        }
    }
    
    /**
     * Vérifier si un fichier existe
     */
    public boolean fileExists(String fileName, FileType fileType) {
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                baseUrl + "/files/exists/" + fileType.name() + "/" + fileName,
                Boolean.class
            );
            
            return Boolean.TRUE.equals(response.getBody());
            
        } catch (Exception e) {
            return false;
        }
    }
    
    // Méthodes spécifiques PDF
    
    /**
     * Générer un thumbnail PDF
     */
    public byte[] generatePdfThumbnail(String fileName, int width, int height) {
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(
                baseUrl + "/files/pdf/thumbnail/" + fileName + "?width=" + width + "&height=" + height,
                byte[].class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new MinioClientException("Failed to generate PDF thumbnail", e);
        }
    }
    
    /**
     * Extraire le texte d'un PDF
     */
    public String extractPdfText(String fileName) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/files/pdf/text/" + fileName,
                String.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            throw new MinioClientException("Failed to extract PDF text", e);
        }
    }
}

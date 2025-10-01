# 🚀 MinIO File Service - Guide d'Intégration Développeur

## 📋 Vue d'ensemble

Ce service MinIO File Management est une API REST complète pour la gestion de fichiers avec support multi-formats, métadonnées automatiques, et traitement PDF avancé.

### 🎯 Fonctionnalités Principales

- ✅ **Upload/Download** de fichiers multi-formats
- ✅ **7 Types de buckets** automatiquement créés
- ✅ **Métadonnées automatiques** (taille, type, date, etc.)
- ✅ **Traitement PDF** (thumbnails, extraction de texte)
- ✅ **URLs présignées** pour accès temporaire
- ✅ **Streaming** de fichiers volumineux
- ✅ **API REST** complète avec documentation

## 🌐 URLs de Production

| Service | URL | Description |
|---------|-----|-------------|
| **API Base** | `https://minio-file-service-6s7p.onrender.com` | URL principale du service |
| **Health Check** | `https://minio-file-service-6s7p.onrender.com/actuator/health` | Vérification de l'état |
| **API Documentation** | `https://minio-file-service-6s7p.onrender.com/api-docs` | Documentation OpenAPI |
| **Swagger UI** | `https://minio-file-service-6s7p.onrender.com/swagger-ui.html` | Interface interactive |

## 📁 Types de Fichiers Supportés

### 🎵 SONG (Bucket: minio-songs)
- **Formats** : mp3, wav, flac, ogg, aac, m4a
- **Taille max** : 50MB
- **Endpoint** : `/api/files/upload/SONG`

### 🖼️ IMAGE (Bucket: minio-images)
- **Formats** : jpg, jpeg, png, gif, webp, bmp, svg
- **Taille max** : 10MB
- **Endpoint** : `/api/files/upload/IMAGE`

### 📹 VIDEO (Bucket: minio-videos)
- **Formats** : mp4, avi, mkv, mov, wmv, webm, flv
- **Taille max** : 500MB
- **Endpoint** : `/api/files/upload/VIDEO`

### 📸 PHOTO (Bucket: minio-photos)
- **Formats** : jpg, jpeg, png, raw, tiff, heic, heif
- **Taille max** : 10MB
- **Endpoint** : `/api/files/upload/PHOTO`

### 📄 DOCUMENT (Bucket: minio-documents)
- **Formats** : pdf, doc, docx, xls, xlsx, ppt, pptx, txt
- **Taille max** : 100MB
- **Endpoint** : `/api/files/upload/DOCUMENT`

### 📦 ARCHIVE (Bucket: minio-archives)
- **Formats** : zip, rar, 7z, tar, gz, bz2
- **Taille max** : 200MB
- **Endpoint** : `/api/files/upload/ARCHIVE`

### 📁 FILE (Bucket: minio-files)
- **Formats** : Tous types non spécifiés
- **Taille max** : 100MB
- **Endpoint** : `/api/files/upload/FILE`

## 🔧 Intégration API - TOUS LES ENDPOINTS DISPONIBLES

### 📋 **Liste Complète des 15 Endpoints API**

| Endpoint | Méthode | Description | Exemple |
|----------|---------|-------------|---------|
| `/upload/{fileType}` | POST | Upload un fichier | Upload d'image |
| `/upload/multiple/{fileType}` | POST | Upload plusieurs fichiers | Upload multiple |
| `/download/{fileType}/{fileName}` | GET | Télécharger un fichier | Download avec attachment |
| `/stream/{fileType}/{fileName}` | GET | Streamer un fichier | Stream pour lecture directe |
| `/{fileType}/{fileName}` | DELETE | Supprimer un fichier | Suppression définitive |
| `/metadata/{fileType}/{fileName}` | GET | Métadonnées d'un fichier | Infos détaillées |
| `/list/{fileType}` | GET | Lister tous les fichiers | Liste par type |
| `/exists/{fileType}/{fileName}` | GET | Vérifier l'existence | Check si fichier existe |
| `/url/{fileType}/{fileName}` | GET | URL publique du fichier | URL directe |
| `/presigned-url/{fileType}/{fileName}` | GET | URL présignée temporaire | Accès sécurisé temporaire |
| `/pdf/thumbnail/{fileName}` | GET | Thumbnail PDF | Image de la 1ère page |
| `/pdf/text/{fileName}` | GET | Extraire texte PDF | Contenu textuel |

### 1. 📤 **Upload de Fichier Simple**

```bash
curl -X POST "https://minio-file-service-6s7p.onrender.com/api/files/upload/IMAGE" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@mon-image.jpg" \
  -F "customFileName=ma-belle-image"
```

**Réponse :**
```json
{
  "fileName": "uuid-generated-name.jpg",
  "originalFileName": "mon-image.jpg",
  "fileUrl": "https://play.min.io/minio-images/uuid-generated-name.jpg",
  "bucketName": "minio-images",
  "fileSize": 1024567,
  "contentType": "image/jpeg",
  "uploadedAt": "2025-10-01T21:00:00.000Z",
  "fileId": "unique-file-id"
}
```

### 2. 📤 **Upload Multiple de Fichiers**

```bash
curl -X POST "https://minio-file-service-6s7p.onrender.com/api/files/upload/multiple/IMAGE" \
  -F "files=@image1.jpg" \
  -F "files=@image2.png" \
  -F "files=@image3.gif"
```

**Réponse :**
```json
[
  {
    "fileName": "uuid1.jpg",
    "originalFileName": "image1.jpg",
    "fileUrl": "https://play.min.io/minio-images/uuid1.jpg",
    "bucketName": "minio-images",
    "fileSize": 1024567,
    "contentType": "image/jpeg",
    "uploadedAt": "2025-10-01T21:00:00.000Z",
    "fileId": "unique-file-id-1"
  },
  {
    "fileName": "uuid2.png",
    "originalFileName": "image2.png",
    "fileUrl": "https://play.min.io/minio-images/uuid2.png",
    "bucketName": "minio-images",
    "fileSize": 2048567,
    "contentType": "image/png",
    "uploadedAt": "2025-10-01T21:00:01.000Z",
    "fileId": "unique-file-id-2"
  }
]
```

### 3. 📥 **Téléchargement de Fichier (Download)**

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/download/IMAGE/uuid-generated-name.jpg" \
  -o mon-fichier-telecharge.jpg
```

### 4. 🎵 **Streaming de Fichier (pour Audio/Vidéo)**

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/stream/SONG/ma-chanson.mp3" \
  --output - | mpv -
```

### 5. 🗑️ **Suppression de Fichier**

```bash
curl -X DELETE "https://minio-file-service-6s7p.onrender.com/api/files/IMAGE/uuid-generated-name.jpg"
```

**Réponse :**
```text
Fichier supprimé avec succès
```

### 6. 📊 **Métadonnées de Fichier**

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/metadata/IMAGE/uuid-generated-name.jpg"
```

**Réponse :**
```json
{
  "fileName": "uuid-generated-name.jpg",
  "originalFileName": "mon-image.jpg",
  "bucketName": "minio-images",
  "fileSize": 1024567,
  "contentType": "image/jpeg",
  "createdAt": "2025-10-01T21:00:00.000",
  "lastModified": "2025-10-01T21:00:00.000",
  "etag": "\"file-etag-hash\"",
  "width": 1920,
  "height": 1080,
  "colorSpace": "RGB",
  "description": "Belle image de paysage",
  "title": "Paysage montagnard"
}
```

### 7. 📋 **Liste des Fichiers par Type**

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/list/IMAGE"
```

**Réponse :**
```json
[
  {
    "fileName": "uuid1.jpg",
    "originalFileName": "image1.jpg",
    "bucketName": "minio-images",
    "fileSize": 1024567,
    "contentType": "image/jpeg",
    "createdAt": "2025-10-01T21:00:00.000",
    "lastModified": "2025-10-01T21:00:00.000",
    "etag": "\"hash1\"",
    "width": 1920,
    "height": 1080
  },
  {
    "fileName": "uuid2.png",
    "originalFileName": "image2.png",
    "bucketName": "minio-images",
    "fileSize": 2048567,
    "contentType": "image/png",
    "createdAt": "2025-10-01T21:01:00.000",
    "lastModified": "2025-10-01T21:01:00.000",
    "etag": "\"hash2\"",
    "width": 1280,
    "height": 720
  }
]
```

### 8. ✅ **Vérifier l'Existence d'un Fichier**

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/exists/IMAGE/uuid-generated-name.jpg"
```

**Réponse :**
```json
true
```

### 9. 🔗 **URL Publique du Fichier**

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/url/IMAGE/uuid-generated-name.jpg"
```

**Réponse :**
```text
https://play.min.io/minio-images/uuid-generated-name.jpg
```

### 10. 🔐 **URL Présignée (Accès Temporaire Sécurisé)**

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/presigned-url/IMAGE/uuid-generated-name.jpg?expiryMinutes=120"
```

**Réponse :**
```text
https://play.min.io/minio-images/uuid-generated-name.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...&X-Amz-Date=...&X-Amz-Expires=7200&X-Amz-SignedHeaders=host&X-Amz-Signature=...
```

### 11. 🖼️ **Génération de Thumbnail PDF**

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/pdf/thumbnail/mon-document.pdf?width=300&height=400" \
  -o thumbnail.png
```

### 12. 📄 **Extraction de Texte PDF**

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/pdf/text/mon-document.pdf"
```

**Réponse :**
```text
Ceci est le contenu textuel extrait du fichier PDF.
Il peut contenir plusieurs paragraphes et du formatage.
Toutes les pages du PDF sont traitées.
```

## 🏗️ **EXEMPLE COMPLET DE CONTRÔLEUR SPRING BOOT**

### 📝 **Contrôleur MinIO Complet avec Toutes les Fonctionnalités**

```java
package com.example.minio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Management", description = "API pour la gestion des fichiers multimédias avec MinIO")
public class MinioController {

    private final MinioService minioService;
    private final PdfProcessingService pdfProcessingService;

    // ========== UPLOAD ENDPOINTS ==========
    
    @PostMapping(value = "/upload/{fileType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload un fichier", description = "Upload un fichier vers MinIO selon le type spécifié")
    @ApiResponse(responseCode = "200", description = "Fichier uploadé avec succès")
    @ApiResponse(responseCode = "400", description = "Type de fichier invalide ou fichier corrompu")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @Parameter(description = "Type de fichier (SONG, IMAGE, VIDEO, PHOTO, DOCUMENT, ARCHIVE, FILE)", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Fichier à uploader", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Nom personnalisé pour le fichier (optionnel)")
            @RequestParam(value = "customFileName", required = false) String customFileName) {
        
        log.info("Uploading file: {} of type: {}", file.getOriginalFilename(), fileType);
        FileUploadResponse response = minioService.uploadFile(file, fileType, customFileName);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "/upload/multiple/{fileType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload plusieurs fichiers", description = "Upload plusieurs fichiers vers MinIO selon le type spécifié")
    @ApiResponse(responseCode = "200", description = "Fichiers uploadés avec succès")
    @ApiResponse(responseCode = "400", description = "Type de fichier invalide ou fichiers corrompus")
    public ResponseEntity<List<FileUploadResponse>> uploadMultipleFiles(
            @Parameter(description = "Type de fichier (SONG, IMAGE, VIDEO, PHOTO, DOCUMENT, ARCHIVE, FILE)", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Liste des fichiers à uploader", required = true)
            @RequestParam("files") List<MultipartFile> files) {
        
        log.info("Uploading {} files of type: {}", files.size(), fileType);
        List<FileUploadResponse> responses = minioService.uploadMultipleFiles(files, fileType);
        return ResponseEntity.ok(responses);
    }

    // ========== DOWNLOAD & STREAM ENDPOINTS ==========

    @GetMapping("/download/{fileType}/{fileName}")
    @Operation(summary = "Télécharger un fichier", description = "Télécharge un fichier depuis MinIO")
    @ApiResponse(responseCode = "200", description = "Fichier téléchargé avec succès")
    @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    public ResponseEntity<InputStreamResource> downloadFile(
            @Parameter(description = "Type de fichier", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String fileName) {
        
        log.info("Downloading file: {} of type: {}", fileName, fileType);
        FileDownloadResponse response = minioService.downloadFile(fileName, fileType);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .contentLength(response.getFileSize())
                .body(new InputStreamResource(response.getInputStream()));
    }

    @GetMapping("/stream/{fileType}/{fileName}")
    @Operation(summary = "Streamer un fichier", description = "Streame un fichier depuis MinIO pour lecture directe")
    @ApiResponse(responseCode = "200", description = "Fichier streamé avec succès")
    @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    public ResponseEntity<InputStreamResource> streamFile(
            @Parameter(description = "Type de fichier", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String fileName) {
        
        log.info("Streaming file: {} of type: {}", fileName, fileType);
        FileDownloadResponse response = minioService.downloadFile(fileName, fileType);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + response.getFileName() + "\"")
                .contentLength(response.getFileSize())
                .body(new InputStreamResource(response.getInputStream()));
    }

    // ========== FILE MANAGEMENT ENDPOINTS ==========

    @DeleteMapping("/{fileType}/{fileName}")
    @Operation(summary = "Supprimer un fichier", description = "Supprime un fichier de MinIO")
    @ApiResponse(responseCode = "200", description = "Fichier supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    public ResponseEntity<String> deleteFile(
            @Parameter(description = "Type de fichier", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String fileName) {
        
        log.info("Deleting file: {} of type: {}", fileName, fileType);
        boolean deleted = minioService.deleteFile(fileName, fileType);
        
        if (deleted) {
            return ResponseEntity.ok("Fichier supprimé avec succès");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/metadata/{fileType}/{fileName}")
    @Operation(summary = "Obtenir les métadonnées d'un fichier", description = "Récupère les métadonnées d'un fichier")
    @ApiResponse(responseCode = "200", description = "Métadonnées récupérées avec succès")
    @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    public ResponseEntity<FileMetadata> getFileMetadata(
            @Parameter(description = "Type de fichier", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String fileName) {
        
        log.info("Getting metadata for file: {} of type: {}", fileName, fileType);
        FileMetadata metadata = minioService.getFileMetadata(fileName, fileType);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/list/{fileType}")
    @Operation(summary = "Lister les fichiers", description = "Liste tous les fichiers d'un type donné")
    @ApiResponse(responseCode = "200", description = "Liste des fichiers récupérée avec succès")
    public ResponseEntity<List<FileMetadata>> listFiles(
            @Parameter(description = "Type de fichier", required = true)
            @PathVariable FileType fileType) {
        
        log.info("Listing files of type: {}", fileType);
        List<FileMetadata> files = minioService.listFiles(fileType);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/exists/{fileType}/{fileName}")
    @Operation(summary = "Vérifier l'existence d'un fichier", description = "Vérifie si un fichier existe dans MinIO")
    @ApiResponse(responseCode = "200", description = "Vérification effectuée avec succès")
    public ResponseEntity<Boolean> fileExists(
            @Parameter(description = "Type de fichier", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String fileName) {
        
        log.info("Checking existence of file: {} of type: {}", fileName, fileType);
        boolean exists = minioService.fileExists(fileName, fileType);
        return ResponseEntity.ok(exists);
    }

    // ========== URL ENDPOINTS ==========

    @GetMapping("/url/{fileType}/{fileName}")
    @Operation(summary = "Obtenir l'URL d'un fichier", description = "Récupère l'URL publique d'un fichier")
    @ApiResponse(responseCode = "200", description = "URL récupérée avec succès")
    @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    public ResponseEntity<String> getFileUrl(
            @Parameter(description = "Type de fichier", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String fileName) {
        
        log.info("Getting URL for file: {} of type: {}", fileName, fileType);
        String url = minioService.getFileUrl(fileName, fileType);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/presigned-url/{fileType}/{fileName}")
    @Operation(summary = "Générer une URL pré-signée", description = "Génère une URL pré-signée temporaire pour accéder au fichier")
    @ApiResponse(responseCode = "200", description = "URL pré-signée générée avec succès")
    @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    public ResponseEntity<String> getPresignedUrl(
            @Parameter(description = "Type de fichier", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String fileName,
            @Parameter(description = "Durée d'expiration en minutes")
            @RequestParam(value = "expiryMinutes", defaultValue = "60") int expiryMinutes) {
        
        log.info("Generating presigned URL for file: {} of type: {}, expiry: {} minutes", fileName, fileType, expiryMinutes);
        String presignedUrl = minioService.getPresignedUrl(fileName, fileType, expiryMinutes);
        return ResponseEntity.ok(presignedUrl);
    }

    // ========== PDF PROCESSING ENDPOINTS ==========

    @GetMapping("/pdf/thumbnail/{fileName}")
    @Operation(summary = "Générer un thumbnail PDF", description = "Génère une image thumbnail de la première page d'un PDF")
    @ApiResponse(responseCode = "200", description = "Thumbnail généré avec succès")
    @ApiResponse(responseCode = "404", description = "Fichier PDF non trouvé")
    @ApiResponse(responseCode = "400", description = "Le fichier n'est pas un PDF valide")
    public ResponseEntity<byte[]> generatePdfThumbnail(
            @Parameter(description = "Nom du fichier PDF", required = true)
            @PathVariable String fileName,
            @Parameter(description = "Largeur du thumbnail")
            @RequestParam(value = "width", defaultValue = "200") int width,
            @Parameter(description = "Hauteur du thumbnail")
            @RequestParam(value = "height", defaultValue = "200") int height) {
        
        log.info("Generating PDF thumbnail for file: {}, size: {}x{}", fileName, width, height);
        
        try {
            byte[] fileContent = minioService.getFileContentAsBytes(fileName, FileType.DOCUMENT);
            byte[] thumbnail = pdfProcessingService.generatePdfThumbnail(
                new java.io.ByteArrayInputStream(fileContent), width, height);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "_thumbnail.png\"")
                    .body(thumbnail);
                    
        } catch (Exception e) {
            log.error("Error generating PDF thumbnail for {}: {}", fileName, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pdf/text/{fileName}")
    @Operation(summary = "Extraire le texte d'un PDF", description = "Extrait tout le texte contenu dans un fichier PDF")
    @ApiResponse(responseCode = "200", description = "Texte extrait avec succès")
    @ApiResponse(responseCode = "404", description = "Fichier PDF non trouvé")
    @ApiResponse(responseCode = "400", description = "Le fichier n'est pas un PDF valide")
    public ResponseEntity<String> extractPdfText(
            @Parameter(description = "Nom du fichier PDF", required = true)
            @PathVariable String fileName) {
        
        log.info("Extracting text from PDF file: {}", fileName);
        
        try {
            byte[] fileContent = minioService.getFileContentAsBytes(fileName, FileType.DOCUMENT);
            String text = pdfProcessingService.extractTextFromPdf(
                new java.io.ByteArrayInputStream(fileContent));
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(text);
                    
        } catch (Exception e) {
            log.error("Error extracting text from PDF {}: {}", fileName, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
```

### 🔧 **Configuration Spring Boot Requise**

#### 1. **Dependencies Maven (pom.xml)**

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MinIO Client -->
    <dependency>
        <groupId>io.minio</groupId>
        <artifactId>minio</artifactId>
        <version>8.5.7</version>
    </dependency>
    
    <!-- Swagger/OpenAPI Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.0.4</version>
    </dependency>
    
    <!-- Lombok pour réduire le boilerplate -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- PDF Processing -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.0</version>
    </dependency>
</dependencies>
```

#### 2. **Configuration Application (application.yml)**

```yaml
# Configuration MinIO
minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
  access-key: ${MINIO_ACCESS_KEY:minioadmin}
  secret-key: ${MINIO_SECRET_KEY:minioadmin123}
  buckets:
    songs: ${MINIO_BUCKET_SONGS:minio-songs}
    images: ${MINIO_BUCKET_IMAGES:minio-images}
    videos: ${MINIO_BUCKET_VIDEOS:minio-videos}
    photos: ${MINIO_BUCKET_PHOTOS:minio-photos}
    documents: ${MINIO_BUCKET_DOCUMENTS:minio-documents}
    archives: ${MINIO_BUCKET_ARCHIVES:minio-archives}
    files: ${MINIO_BUCKET_FILES:minio-files}

# Configuration Spring Boot
spring:
  servlet:
    multipart:
      max-file-size: 200MB
      max-request-size: 200MB
  application:
    name: minio-file-service

# Configuration Swagger/OpenAPI
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
```

#### 3. **Enum FileType**

```java
package com.example.minio.enums;

public enum FileType {
    SONG,       // Fichiers audio (mp3, wav, flac, etc.)
    IMAGE,      // Images (jpg, png, gif, etc.)
    VIDEO,      // Vidéos (mp4, avi, mkv, etc.)
    PHOTO,      // Photos (même format que IMAGE mais bucket séparé)
    DOCUMENT,   // Documents (pdf, doc, txt, etc.)
    ARCHIVE,    // Archives (zip, rar, 7z, etc.)
    FILE        // Fichiers génériques
}
```

#### 4. **DTOs (Data Transfer Objects)**

```java
// FileUploadResponse.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String bucketName;
    private long fileSize;
    private String contentType;
    private LocalDateTime uploadedAt;
    private String fileId;
}

// FileMetadata.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String fileName;
    private String originalFileName;
    private String bucketName;
    private long fileSize;
    private String contentType;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String etag;
    private Integer width;
    private Integer height;
    private String colorSpace;
    private String description;
    private String title;
}

// FileDownloadResponse.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadResponse {
    private String fileName;
    private String contentType;
    private long fileSize;
    private InputStream inputStream;
}
```

## 🚀 **COMMENT UTILISER L'API MINIO FILE SERVICE**

> **✅ Service déjà déployé sur :** `https://minio-file-service-6s7p.onrender.com`

### 📋 **Méthodes Disponibles - Guide Rapide**

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| **Upload** | `POST /api/files/upload/{fileType}` | Upload un fichier |
| **Upload Multiple** | `POST /api/files/upload/multiple/{fileType}` | Upload plusieurs fichiers |
| **Download** | `GET /api/files/download/{fileType}/{fileName}` | Télécharger un fichier |
| **Stream** | `GET /api/files/stream/{fileType}/{fileName}` | Streamer un fichier |
| **Delete** | `DELETE /api/files/{fileType}/{fileName}` | Supprimer un fichier |
| **List** | `GET /api/files/list/{fileType}` | Lister les fichiers |
| **Metadata** | `GET /api/files/metadata/{fileType}/{fileName}` | Métadonnées d'un fichier |
| **Exists** | `GET /api/files/exists/{fileType}/{fileName}` | Vérifier l'existence |
| **URL** | `GET /api/files/url/{fileType}/{fileName}` | URL publique |
| **Presigned URL** | `GET /api/files/presigned-url/{fileType}/{fileName}` | URL temporaire sécurisée |
| **PDF Thumbnail** | `GET /api/files/pdf/thumbnail/{fileName}` | Thumbnail PDF |
| **PDF Text** | `GET /api/files/pdf/text/{fileName}` | Extraire texte PDF |

### 🎯 **Intégration Spring Boot Simple**

```java
@RestController
@RequestMapping("/api/mon-app")
public class MonController {

    private final String API_BASE = "https://minio-file-service-6s7p.onrender.com/api/files";
    private final RestTemplate restTemplate = new RestTemplate();

    // ========== UPLOAD ==========
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        return restTemplate.postForEntity(API_BASE + "/upload/IMAGE", requestEntity, Object.class);
    }

    // ========== LIST FILES ==========
    
    @GetMapping("/images")
    public ResponseEntity<?> listImages() {
        return restTemplate.getForEntity(API_BASE + "/list/IMAGE", Object.class);
    }

    // ========== DELETE ==========
    
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        restTemplate.delete(API_BASE + "/IMAGE/" + fileName);
        return ResponseEntity.ok("Fichier supprimé");
    }

    // ========== GET URL ==========
    
    @GetMapping("/url/{fileName}")
    public ResponseEntity<String> getFileUrl(@PathVariable String fileName) {
        return restTemplate.getForEntity(API_BASE + "/url/IMAGE/" + fileName, String.class);
    }
}
```

### 🌐 **Intégration JavaScript/React Simple**

```javascript
// Configuration de base
const API_BASE = 'https://minio-file-service-6s7p.onrender.com/api/files';

// ========== UPLOAD ==========
async function uploadFile(file, fileType = 'IMAGE') {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch(`${API_BASE}/upload/${fileType}`, {
    method: 'POST',
    body: formData
  });
  
  return await response.json();
}

// ========== LIST FILES ==========
async function listFiles(fileType = 'IMAGE') {
  const response = await fetch(`${API_BASE}/list/${fileType}`);
  return await response.json();
}

// ========== DELETE ==========
async function deleteFile(fileName, fileType = 'IMAGE') {
  const response = await fetch(`${API_BASE}/${fileType}/${fileName}`, {
    method: 'DELETE'
  });
  return response.ok;
}

// ========== GET URL ==========
async function getFileUrl(fileName, fileType = 'IMAGE') {
  const response = await fetch(`${API_BASE}/url/${fileType}/${fileName}`);
  return await response.text();
}

// ========== PRESIGNED URL ==========
async function getPresignedUrl(fileName, fileType = 'IMAGE', expiryMinutes = 60) {
  const response = await fetch(`${API_BASE}/presigned-url/${fileType}/${fileName}?expiryMinutes=${expiryMinutes}`);
  return await response.text();
}
```

### ⚡ **Exemple React Component**

```jsx
import React, { useState, useEffect } from 'react';

const FileManager = () => {
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);

  // Charger la liste des fichiers
  useEffect(() => {
    loadFiles();
  }, []);

  const loadFiles = async () => {
    try {
      const fileList = await listFiles('IMAGE');
      setFiles(fileList);
    } catch (error) {
      console.error('Erreur chargement fichiers:', error);
    }
  };

  const handleUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    setUploading(true);
    try {
      const result = await uploadFile(file, 'IMAGE');
      console.log('Upload réussi:', result);
      loadFiles(); // Recharger la liste
    } catch (error) {
      console.error('Erreur upload:', error);
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (fileName) => {
    try {
      await deleteFile(fileName, 'IMAGE');
      loadFiles(); // Recharger la liste
    } catch (error) {
      console.error('Erreur suppression:', error);
    }
  };

  return (
    <div>
      <h2>Gestionnaire de Fichiers</h2>
      
      {/* Upload */}
      <input 
        type="file" 
        onChange={handleUpload} 
        disabled={uploading}
        accept="image/*"
      />
      {uploading && <p>Upload en cours...</p>}
      
      {/* Liste des fichiers */}
      <div>
        <h3>Fichiers ({files.length})</h3>
        {files.map(file => (
          <div key={file.fileName} style={{border: '1px solid #ccc', margin: '10px', padding: '10px'}}>
            <p><strong>{file.fileName}</strong></p>
            <p>Taille: {(file.fileSize / 1024).toFixed(2)} KB</p>
            <p>Créé: {new Date(file.createdAt).toLocaleDateString()}</p>
            <button onClick={() => handleDelete(file.fileName)}>
              Supprimer
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default FileManager;
```

## 🎯 **Types de Fichiers Supportés**

```javascript
const FILE_TYPES = {
  SONG: 'SONG',       // Audio: mp3, wav, flac
  IMAGE: 'IMAGE',     // Images: jpg, png, gif
  VIDEO: 'VIDEO',     // Vidéos: mp4, avi, mkv
  PHOTO: 'PHOTO',     // Photos (bucket séparé)
  DOCUMENT: 'DOCUMENT', // Documents: pdf, doc, txt
  ARCHIVE: 'ARCHIVE', // Archives: zip, rar, 7z
  FILE: 'FILE'        // Fichiers génériques
};

// Exemple d'usage
await uploadFile(monFichier, FILE_TYPES.VIDEO);
await listFiles(FILE_TYPES.DOCUMENT);
```

## ✅ **Résumé - Ce dont vous avez besoin**

### 🔗 **URL de l'API déployée**
```
https://minio-file-service-6s7p.onrender.com
```

### 📋 **Endpoints principaux**
- `POST /api/files/upload/{fileType}` - Upload
- `GET /api/files/list/{fileType}` - Liste
- `DELETE /api/files/{fileType}/{fileName}` - Suppression
- `GET /api/files/url/{fileType}/{fileName}` - URL publique

### 🎯 **Types de fichiers**
`SONG`, `IMAGE`, `VIDEO`, `PHOTO`, `DOCUMENT`, `ARCHIVE`, `FILE`

### 💡 **Exemple minimal**
```javascript
// Upload
const formData = new FormData();
formData.append('file', monFichier);
fetch('https://minio-file-service-6s7p.onrender.com/api/files/upload/IMAGE', {
  method: 'POST',
  body: formData
});

// Liste
fetch('https://minio-file-service-6s7p.onrender.com/api/files/list/IMAGE')
  .then(r => r.json())
  .then(files => console.log(files));
```

**🚀 C'est tout ! Le service est prêt à l'emploi !**
  const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/upload/${fileType}`, {
    method: 'POST',
    body: formData
  });
  
  return await response.json();
}

// Liste des fichiers
async function listFiles(fileType) {
  const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/list/${fileType}`);
  return await response.json();
}

// Exemple d'utilisation
const fileInput = document.getElementById('fileInput');
const file = fileInput.files[0];

uploadFile(file, 'IMAGE').then(result => {
  console.log('Fichier uploadé:', result);
});
```

### React Hook

```jsx
import { useState } from 'react';

export const useMinioUpload = () => {
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState(null);

  const uploadFile = async (file, fileType) => {
    setUploading(true);
    setError(null);
    
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/upload/${fileType}`, {
        method: 'POST',
        body: formData
      });
      
      if (!response.ok) {
        throw new Error('Upload failed');
      }
      
      const result = await response.json();
      setUploading(false);
      return result;
    } catch (err) {
      setError(err.message);
      setUploading(false);
      throw err;
    }
  };

  return { uploadFile, uploading, error };
};
```

## 🐍 Intégration Python

```python
import requests
import json

class MinioFileService:
    def __init__(self):
        self.base_url = "https://minio-file-service-6s7p.onrender.com"
    
    def upload_file(self, file_path, file_type):
        """Upload un fichier vers le service MinIO"""
        url = f"{self.base_url}/api/files/upload/{file_type}"
        
        with open(file_path, 'rb') as file:
            files = {'file': file}
            response = requests.post(url, files=files)
            
        if response.status_code == 200:
            return response.json()
        else:
            raise Exception(f"Upload failed: {response.status_code}")
    
    def list_files(self, file_type):
        """Liste les fichiers d'un type donné"""
        url = f"{self.base_url}/api/files/list/{file_type}"
        response = requests.get(url)
        
        if response.status_code == 200:
            return response.json()
        else:
            raise Exception(f"List failed: {response.status_code}")
    
    def download_file(self, file_type, file_name, save_path):
        """Télécharge un fichier"""
        url = f"{self.base_url}/api/files/download/{file_type}/{file_name}"
        response = requests.get(url)
        
        if response.status_code == 200:
            with open(save_path, 'wb') as file:
                file.write(response.content)
            return True
        else:
            raise Exception(f"Download failed: {response.status_code}")

# Exemple d'utilisation
service = MinioFileService()

# Upload
result = service.upload_file("mon-image.jpg", "IMAGE")
print(f"Fichier uploadé: {result['fileName']}")

# Liste
files = service.list_files("IMAGE")
print(f"Nombre de fichiers: {len(files)}")
```

## ☕ Intégration Java/Spring Boot

```java
@Service
public class MinioFileServiceClient {
    
    private final RestTemplate restTemplate;
    private final String baseUrl = "https://minio-file-service-6s7p.onrender.com";
    
    public MinioFileServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public FileUploadResponse uploadFile(MultipartFile file, String fileType) {
        String url = baseUrl + "/api/files/upload/" + fileType;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = 
            new HttpEntity<>(body, headers);
        
        return restTemplate.postForObject(url, requestEntity, FileUploadResponse.class);
    }
    
    public List<FileMetadata> listFiles(String fileType) {
        String url = baseUrl + "/api/files/list/" + fileType;
        
        ResponseEntity<FileMetadata[]> response = 
            restTemplate.getForEntity(url, FileMetadata[].class);
        
        return Arrays.asList(response.getBody());
    }
}
```

## 📱 Intégration Mobile

### Flutter/Dart

```dart
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:io';

class MinioFileService {
  static const String baseUrl = 'https://minio-file-service-6s7p.onrender.com';
  
  static Future<Map<String, dynamic>> uploadFile(File file, String fileType) async {
    var uri = Uri.parse('$baseUrl/api/files/upload/$fileType');
    var request = http.MultipartRequest('POST', uri);
    
    request.files.add(await http.MultipartFile.fromPath('file', file.path));
    
    var response = await request.send();
    var responseBody = await response.stream.bytesToString();
    
    if (response.statusCode == 200) {
      return json.decode(responseBody);
    } else {
      throw Exception('Upload failed: ${response.statusCode}');
    }
  }
  
  static Future<List<dynamic>> listFiles(String fileType) async {
    var response = await http.get(Uri.parse('$baseUrl/api/files/list/$fileType'));
    
    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('List failed: ${response.statusCode}');
    }
  }
}
```

## 🔧 Fonctionnalités Avancées

### 1. 📄 Traitement PDF

```bash
# Génération de thumbnail PDF
curl "https://minio-file-service-6s7p.onrender.com/api/files/pdf/thumbnail/DOCUMENT/mon-document.pdf"

# Extraction de texte PDF
curl "https://minio-file-service-6s7p.onrender.com/api/files/pdf/text/DOCUMENT/mon-document.pdf"
```

### 2. 🎵 Streaming Audio/Vidéo

```bash
# Stream d'un fichier audio
curl "https://minio-file-service-6s7p.onrender.com/api/files/stream/SONG/ma-chanson.mp3"

# Stream d'un fichier vidéo
curl "https://minio-file-service-6s7p.onrender.com/api/files/stream/VIDEO/ma-video.mp4"
```

### 3. 📊 Upload Multiple

```bash
curl -X POST "https://minio-file-service-6s7p.onrender.com/api/files/upload-multiple/IMAGE" \
  -F "files=@image1.jpg" \
  -F "files=@image2.png" \
  -F "files=@image3.gif"
```

## 🚨 Gestion d'Erreurs

### Codes de Réponse HTTP

| Code | Description | Action |
|------|-------------|--------|
| **200** | Succès | Traitement normal |
| **400** | Requête invalide | Vérifier les paramètres |
| **404** | Fichier non trouvé | Vérifier le nom/type de fichier |
| **413** | Fichier trop volumineux | Réduire la taille |
| **415** | Type de fichier non supporté | Utiliser un format supporté |
| **500** | Erreur serveur | Réessayer plus tard |

### Exemple de Gestion d'Erreur

```javascript
async function uploadFileWithErrorHandling(file, fileType) {
  try {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/upload/${fileType}`, {
      method: 'POST',
      body: formData
    });
    
    if (!response.ok) {
      switch (response.status) {
        case 400:
          throw new Error('Requête invalide - Vérifiez le fichier et le type');
        case 413:
          throw new Error('Fichier trop volumineux');
        case 415:
          throw new Error('Type de fichier non supporté');
        case 500:
          throw new Error('Erreur serveur - Réessayez plus tard');
        default:
          throw new Error(`Erreur ${response.status}: ${response.statusText}`);
      }
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur upload:', error.message);
    throw error;
  }
}
```

## 🔒 Sécurité et Bonnes Pratiques

### 1. Validation Côté Client

```javascript
function validateFile(file, maxSize, allowedTypes) {
  // Vérifier la taille
  if (file.size > maxSize) {
    throw new Error(`Fichier trop volumineux. Max: ${maxSize / 1024 / 1024}MB`);
  }
  
  // Vérifier le type
  if (!allowedTypes.includes(file.type)) {
    throw new Error(`Type de fichier non autorisé: ${file.type}`);
  }
  
  return true;
}

// Exemple d'utilisation
const imageFile = document.getElementById('imageInput').files[0];
const maxSize = 10 * 1024 * 1024; // 10MB
const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];

try {
  validateFile(imageFile, maxSize, allowedTypes);
  // Procéder à l'upload
} catch (error) {
  console.error('Validation échouée:', error.message);
}
```

### 2. Retry Logic

```javascript
async function uploadWithRetry(file, fileType, maxRetries = 3) {
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      return await uploadFile(file, fileType);
    } catch (error) {
      if (attempt === maxRetries) {
        throw error;
      }
      
      // Attendre avant de réessayer
      await new Promise(resolve => setTimeout(resolve, 1000 * attempt));
    }
  }
}
```

## 📈 Monitoring et Logs

### Health Check

```bash
# Vérifier l'état du service
curl "https://minio-file-service-6s7p.onrender.com/actuator/health"
```

**Réponse attendue :**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## 🎯 Exemples Complets

### Application Web Simple

```html
<!DOCTYPE html>
<html>
<head>
    <title>MinIO File Upload</title>
</head>
<body>
    <h1>Upload de Fichier</h1>
    
    <input type="file" id="fileInput" multiple>
    <select id="fileType">
        <option value="IMAGE">Image</option>
        <option value="DOCUMENT">Document</option>
        <option value="VIDEO">Vidéo</option>
        <option value="SONG">Audio</option>
    </select>
    <button onclick="uploadFiles()">Upload</button>
    
    <div id="results"></div>
    
    <script>
        async function uploadFiles() {
            const fileInput = document.getElementById('fileInput');
            const fileType = document.getElementById('fileType').value;
            const results = document.getElementById('results');
            
            results.innerHTML = 'Upload en cours...';
            
            try {
                for (const file of fileInput.files) {
                    const formData = new FormData();
                    formData.append('file', file);
                    
                    const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/upload/${fileType}`, {
                        method: 'POST',
                        body: formData
                    });
                    
                    const result = await response.json();
                    results.innerHTML += `<p>✅ ${file.name} → ${result.fileName}</p>`;
                }
            } catch (error) {
                results.innerHTML = `❌ Erreur: ${error.message}`;
            }
        }
    </script>
</body>
</html>
```

## 🚀 Déploiement et Configuration

### Variables d'Environnement

Si vous déployez votre propre instance, voici les variables requises :

```env
# Configuration MinIO
MINIO_URL=https://play.min.io
MINIO_ACCESS_KEY=Q3AM3UQ867SPQQA43P2F
MINIO_SECRET_KEY=zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG

# Configuration Spring
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Configuration des buckets
MINIO_BUCKET_SONGS=minio-songs
MINIO_BUCKET_IMAGES=minio-images
MINIO_BUCKET_VIDEOS=minio-videos
MINIO_BUCKET_PHOTOS=minio-photos
MINIO_BUCKET_DOCUMENTS=minio-documents
MINIO_BUCKET_ARCHIVES=minio-archives
MINIO_BUCKET_FILES=minio-files
```

## 📞 Support et Contact

- **Service en Production** : `https://minio-file-service-6s7p.onrender.com`
- **Documentation API** : `https://minio-file-service-6s7p.onrender.com/api-docs`
- **Health Check** : `https://minio-file-service-6s7p.onrender.com/actuator/health`

## 🎉 Conclusion

Ce service MinIO File Management offre une solution complète et robuste pour la gestion de fichiers avec :

- ✅ **API REST** simple et intuitive
- ✅ **Support multi-formats** avec validation automatique
- ✅ **Métadonnées riches** pour tous les types de fichiers
- ✅ **Traitement PDF** avancé
- ✅ **URLs présignées** pour la sécurité
- ✅ **Streaming** pour les gros fichiers
- ✅ **Documentation complète** avec Swagger

**Prêt à intégrer dans vos projets !** 🚀

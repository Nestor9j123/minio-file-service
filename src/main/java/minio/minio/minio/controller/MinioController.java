package minio.minio.minio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import minio.minio.minio.dto.FileDownloadResponse;
import minio.minio.minio.dto.FileMetadata;
import minio.minio.minio.dto.FileUploadResponse;
import minio.minio.minio.enums.FileType;
import minio.minio.minio.service.MinioService;
import minio.minio.minio.service.PdfProcessingService;


import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Management", description = "API pour la gestion des fichiers multimédias avec MinIO")
public class MinioController {

    private final MinioService minioService;
    private final PdfProcessingService pdfProcessingService;


    @PostMapping(value = "/upload/{fileType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload un fichier", description = "Upload un fichier vers MinIO selon le type spécifié")
    @ApiResponse(responseCode = "200", description = "Fichier uploadé avec succès")
    @ApiResponse(responseCode = "400", description = "Type de fichier invalide ou fichier corrompu")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @Parameter(description = "Type de fichier (SONG, IMAGE, VIDEO, PHOTO)", required = true)
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
            @Parameter(description = "Type de fichier (SONG, IMAGE, VIDEO, PHOTO)", required = true)
            @PathVariable FileType fileType,
            @Parameter(description = "Liste des fichiers à uploader", required = true)
            @RequestParam("files") List<MultipartFile> files) {
        
        log.info("Uploading {} files of type: {}", files.size(), fileType);
        List<FileUploadResponse> responses = minioService.uploadMultipleFiles(files, fileType);
        return ResponseEntity.ok(responses);
    }

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
            byte[] fileContent = minioService.getFileContentAsBytes(fileName, FileType.PDF);
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
            byte[] fileContent = minioService.getFileContentAsBytes(fileName, FileType.PDF);
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

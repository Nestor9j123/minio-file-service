package minio.minio.minio.service.impl;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import minio.minio.minio.dto.FileDownloadResponse;
import minio.minio.minio.dto.FileMetadata;
import minio.minio.minio.dto.FileUploadResponse;
import minio.minio.minio.enums.FileType;
import minio.minio.minio.exception.FileNotFoundException;
import minio.minio.minio.exception.InvalidFileTypeException;
import minio.minio.minio.exception.MinioException;
import minio.minio.minio.service.MinioService;
import minio.minio.minio.service.PdfProcessingService;
import minio.minio.minio.config.MinioProperties;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final PdfProcessingService pdfProcessingService;
    private final Tika tika = new Tika();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void initializeBuckets() {
        log.info("Initializing ALL MinIO buckets and configuring public access...");
        try {
            // Vérifier la connectivité MinIO d'abord
            if (!isMinioAvailable()) {
                log.warn("MinIO server is not available at startup. Buckets will be created on first use.");
                return;
            }

            // Créer TOUS les buckets configurés
            MinioProperties.Bucket bucket = minioProperties.getBucket();
            String[] allBuckets = {
                bucket.getSongs(),
                bucket.getImages(), 
                bucket.getVideos(),
                bucket.getPhotos(),
                bucket.getDocuments(),
                bucket.getArchives(),
                bucket.getFiles()
            };

            // Créer chaque bucket et configurer l'accès public pour images/photos
            for (String bucketName : allBuckets) {
                ensureBucketExists(bucketName);
                
                // Configurer l'accès public pour les buckets d'images et photos
                if (bucketName.equals(bucket.getImages()) || bucketName.equals(bucket.getPhotos())) {
                    setBucketPublicReadAccess(bucketName);
                }
            }

            log.info("Successfully initialized {} buckets: {}", allBuckets.length, String.join(", ", allBuckets));
        } catch (Exception e) {
            log.warn("MinIO buckets initialization failed: {}. Buckets will be created on first use.", e.getMessage());
        }
    }

    /**
     * Vérifie si le serveur MinIO est disponible
     */
    private boolean isMinioAvailable() {
        try {
            // Test simple de connectivité avec listBuckets
            minioClient.listBuckets();
            return true;
        } catch (Exception e) {
            log.debug("MinIO connectivity check failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile file, FileType fileType) {
        return uploadFile(file, fileType, null);
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile file, FileType fileType, String customFileName) {
        try {
            validateFile(file, fileType);
            
            String bucketName = getBucketName(fileType);
            ensureBucketExists(bucketName);
            
            String fileName = customFileName != null ? customFileName : generateFileName(file.getOriginalFilename());
            String contentType = detectContentType(file);
            
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build();
            
            ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
            
            return FileUploadResponse.builder()
                    .fileName(fileName)
                    .originalFileName(file.getOriginalFilename())
                    .fileUrl(getFileUrl(fileName, fileType))
                    .bucketName(bucketName)
                    .fileSize(file.getSize())
                    .contentType(contentType)
                    .uploadedAt(LocalDateTime.now())
                    .fileId(response.etag())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new MinioException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public FileDownloadResponse downloadFile(String fileName, FileType fileType) {
        try {
            String bucketName = getBucketName(fileType);
            
            if (!fileExists(fileName, fileType)) {
                throw new FileNotFoundException(fileName);
            }
            
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            FileMetadata metadata = getFileMetadata(fileName, fileType);

            return FileDownloadResponse.builder()
                    .inputStream(inputStream)
                    .fileName(fileName)
                    .contentType(metadata.getContentType())
                    .fileSize(metadata.getFileSize())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage(), e);
            throw new MinioException("Failed to download file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String fileName, FileType fileType) {
        try {
            String bucketName = getBucketName(fileType);
            
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            
            minioClient.removeObject(removeObjectArgs);
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public FileMetadata getFileMetadata(String fileName, FileType fileType) {
        try {
            String bucketName = getBucketName(fileType);
            
            StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            
            StatObjectResponse stat = minioClient.statObject(statObjectArgs);
            
            FileMetadata.FileMetadataBuilder metadataBuilder = FileMetadata.builder()
                    .fileName(fileName)
                    .bucketName(bucketName)
                    .fileSize(stat.size())
                    .contentType(stat.contentType())
                    .createdAt(LocalDateTime.ofInstant(stat.lastModified().toInstant(), ZoneId.systemDefault()))
                    .lastModified(LocalDateTime.ofInstant(stat.lastModified().toInstant(), ZoneId.systemDefault()))
                    .etag(stat.etag());
            
            // Extract additional metadata for PDF files
            if (fileType == FileType.PDF && "application/pdf".equals(stat.contentType())) {
                try (InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder().bucket(bucketName).object(fileName).build())) {
                    FileMetadata pdfMetadata = pdfProcessingService.extractPdfMetadata(inputStream, fileName);
                    metadataBuilder
                            .pageCount(pdfMetadata.getPageCount())
                            .title(pdfMetadata.getTitle())
                            .author(pdfMetadata.getAuthor())
                            .subject(pdfMetadata.getSubject())
                            .creator(pdfMetadata.getCreator())
                            .producer(pdfMetadata.getProducer())
                            .keywords(pdfMetadata.getKeywords())
                            .encrypted(pdfMetadata.getEncrypted());
                } catch (Exception pdfError) {
                    log.warn("Could not extract PDF metadata for {}: {}", fileName, pdfError.getMessage());
                }
            }
            
            return metadataBuilder.build();
                    
        } catch (Exception e) {
            log.error("Error getting file metadata: {}", e.getMessage(), e);
            throw new MinioException("Failed to get file metadata: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FileMetadata> listFiles(FileType fileType) {
        try {
            String bucketName = getBucketName(fileType);
            List<FileMetadata> files = new ArrayList<>();
            
            ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build();
            
            Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
            
            for (Result<Item> result : results) {
                Item item = result.get();
                FileMetadata metadata = FileMetadata.builder()
                        .fileName(item.objectName())
                        .bucketName(bucketName)
                        .fileSize(item.size())
                        .createdAt(LocalDateTime.ofInstant(item.lastModified().toInstant(), ZoneId.systemDefault()))
                        .lastModified(LocalDateTime.ofInstant(item.lastModified().toInstant(), ZoneId.systemDefault()))
                        .etag(item.etag())
                        .build();
                files.add(metadata);
            }
            
            return files;
            
        } catch (Exception e) {
            log.error("Error listing files: {}", e.getMessage(), e);
            throw new MinioException("Failed to list files: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String fileName, FileType fileType) {
        try {
            String bucketName = getBucketName(fileType);
            
            StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            
            minioClient.statObject(statObjectArgs);
            return true;
            
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new MinioException("Error checking file existence: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new MinioException("Error checking file existence: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFileUrl(String fileName, FileType fileType) {
        String bucketName = getBucketName(fileType);
        String baseUrl = minioProperties.getUrl();
        // Remove trailing slash if present
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return String.format("%s/%s/%s", baseUrl, bucketName, fileName);
    }

    @Override
    public String getPresignedUrl(String fileName, FileType fileType, int expiryInMinutes) {
        try {
            String bucketName = getBucketName(fileType);
            
            // Vérifier si le fichier existe
            if (!fileExists(fileName, fileType)) {
                throw new FileNotFoundException("File not found: " + fileName);
            }
            
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(expiryInMinutes, TimeUnit.MINUTES)
                            .build());
            
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            throw new MinioException("Error generating presigned URL" + e.getMessage(), e);
        }
    }
    
    @Override
    public List<FileUploadResponse> uploadMultipleFiles(List<MultipartFile> files, FileType fileType) {
        List<FileUploadResponse> responses = new ArrayList<>();
        
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni pour l'upload");
        }
        
        for (MultipartFile file : files) {
            try {
                FileUploadResponse response = uploadFile(file, fileType);
                responses.add(response);
            } catch (Exception e) {
                log.error("Erreur lors de l'upload du fichier {}: {}", 
                        file.getOriginalFilename(), e.getMessage(), e);
                // On continue avec les autres fichiers même si un échoue
                responses.add(FileUploadResponse.builder()
                    .fileName(file.getOriginalFilename())
                    .originalFileName(file.getOriginalFilename())
                    .fileUrl(null)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .uploadedAt(LocalDateTime.now())
                    .build());
            }
        }
        
        return responses;
    }

    private void validateFile(MultipartFile file, FileType fileType) throws IOException, MinioException {
        if (file.isEmpty()) {
            throw new MinioException("File is empty");
        }
        
        // Validate file size
        long maxSize = fileType.getMaxFileSize();
        if (file.getSize() > maxSize) {
            throw new MinioException(String.format("File size (%d bytes) exceeds maximum allowed size (%d bytes) for type %s", 
                    file.getSize(), maxSize, fileType));
        }
        
        String contentType = detectContentType(file);
        if (!fileType.isValidMimeType(contentType)) {
            throw new InvalidFileTypeException(contentType);
        }
    }

    private String detectContentType(MultipartFile file) throws IOException {
        String contentType = tika.detect(file.getInputStream(), file.getOriginalFilename());
        return contentType != null ? contentType : file.getContentType();
    }

    private String getBucketName(FileType fileType) {
        MinioProperties.Bucket bucket = minioProperties.getBucket();
        switch (fileType) {
            case SONG:
                return bucket.getSongs();
            case IMAGE:
                return bucket.getImages();
            case VIDEO:
                return bucket.getVideos();
            case PHOTO:
                return bucket.getPhotos();
            case PDF:
            case DOCUMENT:
                return bucket.getDocuments();
            case ARCHIVE:
                return bucket.getArchives();
            case FILE:
                return bucket.getFiles();
            default:
                throw new IllegalArgumentException("Unknown file type: " + fileType);
        }
    }

    private void ensureBucketExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Created bucket: {}", bucketName);

                // Configure public access for image and photo buckets
                if (bucketName.equals(minioProperties.getBucket().getImages()) ||
                    bucketName.equals(minioProperties.getBucket().getPhotos())) {
                    setBucketPublicReadAccess(bucketName);
                    log.info("Configured public read access for bucket: {}", bucketName);
                }
            }
        } catch (Exception e) {
            log.error("Error ensuring bucket exists: {}", e.getMessage(), e);
            throw new MinioException("Failed to ensure bucket exists: " + e.getMessage(), e);
        }
    }

    private void setBucketPublicReadAccess(String bucketName) {
        try {
            // Policy JSON pour permettre l'accès public en lecture
            String policy = String.format("""
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": "*",
                      "Action": "s3:GetObject",
                      "Resource": "arn:aws:s3:::%s/*"
                    }
                  ]
                }
                """, bucketName);

            SetBucketPolicyArgs policyArgs = SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build();

            minioClient.setBucketPolicy(policyArgs);
            log.info("Successfully set public read policy for bucket: {}", bucketName);

        } catch (Exception e) {
            log.warn("Could not set public policy for bucket {}: {}. Images may not be directly accessible.",
                    bucketName, e.getMessage());
            // Ne pas lancer d'exception car le bucket fonctionne quand même
        }
    }

    @Override
    public byte[] getFileContentAsBytes(String fileName, FileType fileType) {
        try {
            String bucketName = getBucketName(fileType);
            
            if (!fileExists(fileName, fileType)) {
                throw new FileNotFoundException("File not found: " + fileName);
            }
            
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            
            try (InputStream inputStream = minioClient.getObject(getObjectArgs)) {
                return inputStream.readAllBytes();
            }
            
        } catch (Exception e) {
            log.error("Error getting file content as bytes for file {}: {}", fileName, e.getMessage(), e);
            throw new MinioException("Failed to get file content: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        try {
            // Extraire le nom du fichier depuis l'URL MinIO
            // Format: http://localhost:9000/titan-images/filename.ext
            String[] parts = url.split("/");
            return parts[parts.length - 1]; // Dernier élément = nom du fichier
        } catch (Exception e) {
            log.error("Error extracting filename from URL: {}", url, e);
            return null;
        }
    }

    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}

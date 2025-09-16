package minio.minio.minio.service;

import minio.minio.minio.dto.FileDownloadResponse;
import minio.minio.minio.dto.FileMetadata;
import minio.minio.minio.dto.FileUploadResponse;
import minio.minio.minio.enums.FileType;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MinioService {
    
    /**
     * Upload a file to MinIO
     */
    FileUploadResponse uploadFile(MultipartFile file, FileType fileType);
    
    /**
     * Upload a file to MinIO with custom filename
     */
    FileUploadResponse uploadFile(MultipartFile file, FileType fileType, String customFileName);
    
    /**
     * Download a file from MinIO
     */
    FileDownloadResponse downloadFile(String fileName, FileType fileType);
    
    /**
     * Delete a file from MinIO
     */
    boolean deleteFile(String fileName, FileType fileType);
    
    /**
     * Get file metadata
     */
    FileMetadata getFileMetadata(String fileName, FileType fileType);
    
    /**
     * List all files in a bucket
     */
    List<FileMetadata> listFiles(FileType fileType);
    
    /**
     * Check if file exists
     */
    boolean fileExists(String fileName, FileType fileType);
    
    /**
     * Get file URL for direct access
     */
    String getFileUrl(String fileName, FileType fileType);
    
    /**
     * Upload multiple files to MinIO
     */
    List<FileUploadResponse> uploadMultipleFiles(List<MultipartFile> files, FileType fileType);
    
    /**
     * Get presigned URL for temporary access
     */
    String getPresignedUrl(String fileName, FileType fileType, int expiryInMinutes);
    
    /**
     * Get file content as byte array
     */
    byte[] getFileContentAsBytes(String fileName, FileType fileType);
    
    /**
     * Extract filename from MinIO URL
     */
    String extractFileNameFromUrl(String url);
}

package com.fileservice.client.dto;

import java.time.LocalDateTime;

public class FileUploadResponse {
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String bucketName;
    private Long fileSize;
    private String contentType;
    private LocalDateTime uploadedAt;
    private String fileId;

    // Constructors
    public FileUploadResponse() {}

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
}

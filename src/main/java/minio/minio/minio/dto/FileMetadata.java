package minio.minio.minio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String fileName;
    private String originalFileName;
    private String bucketName;
    private Long fileSize;
    private String contentType;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String etag;
    
    // PDF and document specific metadata
    private Integer pageCount;
    private String title;
    private String author;
    private String subject;
    private String creator;
    private String producer;
    private String keywords;
    private Boolean encrypted;
    
    // Image specific metadata
    private Integer width;
    private Integer height;
    private String colorSpace;
    
    // General metadata
    private String description;
    private String language;
    private Long duration; // For video/audio files in milliseconds
}

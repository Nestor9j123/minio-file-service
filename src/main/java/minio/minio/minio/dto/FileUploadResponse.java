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
public class FileUploadResponse {
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String bucketName;
    private Long fileSize;
    private String contentType;
    private LocalDateTime uploadedAt;
    private String fileId;
}

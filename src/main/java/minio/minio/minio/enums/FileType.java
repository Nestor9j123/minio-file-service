package minio.minio.minio.enums;

import lombok.Getter;

@Getter
public enum FileType {
    // Audio files
    SONG("songs", new String[]{"audio/mpeg", "audio/mp3", "audio/wav", "audio/flac", "audio/ogg", "audio/aac", "audio/m4a"}),
    
    // Image files
    IMAGE("images", new String[]{"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp", "image/svg+xml"}),
    PHOTO("photos", new String[]{"image/jpeg", "image/jpg", "image/png", "image/raw", "image/tiff", "image/heic", "image/heif"}),
    
    // Video files
    VIDEO("videos", new String[]{"video/mp4", "video/avi", "video/mkv", "video/mov", "video/wmv", "video/webm", "video/flv", "video/3gp"}),
    
    // Document files
    PDF("documents", new String[]{"application/pdf"}),
    DOCUMENT("documents", new String[]{
        "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain",
        "text/csv",
        "application/rtf"
    }),
    
    // Archive files
    ARCHIVE("archives", new String[]{
        "application/zip", 
        "application/x-rar-compressed", 
        "application/x-7z-compressed",
        "application/x-tar",
        "application/gzip"
    }),
    
    // General files (fallback)
    FILE("files", new String[]{"*/*"});

    private final String bucketSuffix;
    private final String[] allowedMimeTypes;

    FileType(String bucketSuffix, String[] allowedMimeTypes) {
        this.bucketSuffix = bucketSuffix;
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public static FileType fromMimeType(String mimeType) {
        for (FileType type : values()) {
            for (String allowedType : type.allowedMimeTypes) {
                if (allowedType.equalsIgnoreCase(mimeType)) {
                    return type;
                }
            }
        }
        return null;
    }

    public boolean isValidMimeType(String mimeType) {
        // FILE type accepts any mime type
        if (this == FILE) {
            return true;
        }
        
        for (String allowedType : allowedMimeTypes) {
            if (allowedType.equalsIgnoreCase(mimeType) || allowedType.equals("*/*")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the maximum file size allowed for this file type (in bytes)
     */
    public long getMaxFileSize() {
        switch (this) {
            case SONG:
                return 50L * 1024 * 1024; // 50MB
            case IMAGE:
            case PHOTO:
                return 10L * 1024 * 1024; // 10MB
            case VIDEO:
                return 500L * 1024 * 1024; // 500MB
            case PDF:
            case DOCUMENT:
                return 100L * 1024 * 1024; // 100MB
            case ARCHIVE:
                return 200L * 1024 * 1024; // 200MB
            case FILE:
            default:
                return 100L * 1024 * 1024; // 100MB default
        }
    }
}

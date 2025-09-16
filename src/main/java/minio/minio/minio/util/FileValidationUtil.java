package minio.minio.minio.util;

import minio.minio.minio.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidationUtil {

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final long MAX_SONG_SIZE = 50 * 1024 * 1024;  // 50MB
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VIDEO_SIZE = 500 * 1024 * 1024; // 500MB

    private static final List<String> DANGEROUS_EXTENSIONS = Arrays.asList(
        ".exe", ".bat", ".cmd", ".scr", ".pif", ".jar", ".js", ".vbs", ".sh"
    );

    public static boolean isValidFileSize(MultipartFile file, FileType fileType) {
        long fileSize = file.getSize();
        
        switch (fileType) {
            case SONG:
                return fileSize <= MAX_SONG_SIZE;
            case IMAGE:
            case PHOTO:
                return fileSize <= MAX_IMAGE_SIZE;
            case VIDEO:
                return fileSize <= MAX_VIDEO_SIZE;
            default:
                return fileSize <= MAX_FILE_SIZE;
        }
    }

    public static boolean isSafeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        // Vérifier les extensions dangereuses
        String lowerFileName = fileName.toLowerCase();
        for (String dangerousExt : DANGEROUS_EXTENSIONS) {
            if (lowerFileName.endsWith(dangerousExt)) {
                return false;
            }
        }

        // Vérifier les caractères interdits
        return !fileName.matches(".*[<>:\"/\\\\|?*].*");
    }

    public static String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unnamed_file";
        }

        // Remplacer les caractères interdits
        String sanitized = fileName.replaceAll("[<>:\"/\\\\|?*]", "_");
        
        // Limiter la longueur
        if (sanitized.length() > 255) {
            String extension = "";
            int dotIndex = sanitized.lastIndexOf(".");
            if (dotIndex > 0) {
                extension = sanitized.substring(dotIndex);
                sanitized = sanitized.substring(0, Math.min(255 - extension.length(), dotIndex)) + extension;
            } else {
                sanitized = sanitized.substring(0, 255);
            }
        }

        return sanitized;
    }

    public static boolean isValidImageFormat(String contentType) {
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/jpg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif") ||
            contentType.equals("image/webp") ||
            contentType.equals("image/bmp")
        );
    }

    public static boolean isValidAudioFormat(String contentType) {
        return contentType != null && (
            contentType.equals("audio/mpeg") ||
            contentType.equals("audio/mp3") ||
            contentType.equals("audio/wav") ||
            contentType.equals("audio/flac") ||
            contentType.equals("audio/ogg") ||
            contentType.equals("audio/aac")
        );
    }

    public static boolean isValidVideoFormat(String contentType) {
        return contentType != null && (
            contentType.equals("video/mp4") ||
            contentType.equals("video/avi") ||
            contentType.equals("video/mkv") ||
            contentType.equals("video/mov") ||
            contentType.equals("video/wmv") ||
            contentType.equals("video/webm")
        );
    }
}

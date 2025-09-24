package minio.minio.minio.util;

import minio.minio.minio.enums.FileType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class FileValidationUtilTest {

    @Test
    void isValidFileSize_ImageFile_ValidSize_ReturnsTrue() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[1024]); // 1KB
        
        // When
        boolean isValid = FileValidationUtil.isValidFileSize(file, FileType.IMAGE);
        
        // Then
        assertTrue(isValid);
    }

    @Test
    void isValidFileSize_ImageFile_ExceedsLimit_ReturnsFalse() {
        // Given
        byte[] largeContent = new byte[(int) (FileType.IMAGE.getMaxFileSize() + 1)];
        MultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", largeContent);
        
        // When
        boolean isValid = FileValidationUtil.isValidFileSize(file, FileType.IMAGE);
        
        // Then
        assertFalse(isValid);
    }

    @Test
    void isValidFileSize_SongFile_ValidSize_ReturnsTrue() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", new byte[1024 * 1024]); // 1MB
        
        // When
        boolean isValid = FileValidationUtil.isValidFileSize(file, FileType.SONG);
        
        // Then
        assertTrue(isValid);
    }

    @Test
    void isValidFileSize_SongFile_ExceedsLimit_ReturnsFalse() {
        // Given
        byte[] largeContent = new byte[(int) (FileType.SONG.getMaxFileSize() + 1)];
        MultipartFile file = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", largeContent);
        
        // When
        boolean isValid = FileValidationUtil.isValidFileSize(file, FileType.SONG);
        
        // Then
        assertFalse(isValid);
    }

    @Test
    void isValidFileSize_VideoFile_ValidSize_ReturnsTrue() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file", "test.mp4", "video/mp4", new byte[1024 * 1024 * 10]); // 10MB
        
        // When
        boolean isValid = FileValidationUtil.isValidFileSize(file, FileType.VIDEO);
        
        // Then
        assertTrue(isValid);
    }

    @Test
    void isValidFileSize_VideoFile_ExceedsLimit_ReturnsFalse() {
        // Given
        byte[] largeContent = new byte[(int) (FileType.VIDEO.getMaxFileSize() + 1)];
        MultipartFile file = new MockMultipartFile(
                "file", "test.mp4", "video/mp4", largeContent);
        
        // When
        boolean isValid = FileValidationUtil.isValidFileSize(file, FileType.VIDEO);
        
        // Then
        assertFalse(isValid);
    }

    @Test
    void isValidFileSize_DefaultFileType_ValidSize_ReturnsTrue() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", new byte[1024 * 1024]); // 1MB
        
        // When
        boolean isValid = FileValidationUtil.isValidFileSize(file, FileType.PDF);
        
        // Then
        assertTrue(isValid);
    }

    @Test
    void isSafeFileName_ValidFileName_ReturnsTrue() {
        // Given
        String fileName = "test-file_123.jpg";
        
        // When
        boolean isSafe = FileValidationUtil.isSafeFileName(fileName);
        
        // Then
        assertTrue(isSafe);
    }

    @Test
    void isSafeFileName_NullFileName_ReturnsFalse() {
        // When
        boolean isSafe = FileValidationUtil.isSafeFileName(null);
        
        // Then
        assertFalse(isSafe);
    }

    @Test
    void isSafeFileName_EmptyFileName_ReturnsFalse() {
        // When
        boolean isSafe = FileValidationUtil.isSafeFileName("");
        
        // Then
        assertFalse(isSafe);
    }

    @Test
    void isSafeFileName_WhitespaceOnlyFileName_ReturnsFalse() {
        // When
        boolean isSafe = FileValidationUtil.isSafeFileName("   ");
        
        // Then
        assertFalse(isSafe);
    }

    @Test
    void isSafeFileName_DangerousExtension_ReturnsFalse() {
        // Given
        String[] dangerousFiles = {
                "malware.exe", "script.bat", "command.cmd", "screen.scr",
                "program.pif", "archive.jar", "script.js", "virus.vbs", "shell.sh"
        };
        
        // When & Then
        for (String fileName : dangerousFiles) {
            boolean isSafe = FileValidationUtil.isSafeFileName(fileName);
            assertFalse(isSafe, "File " + fileName + " should be considered unsafe");
        }
    }

    @Test
    void isSafeFileName_InvalidCharacters_ReturnsFalse() {
        // Given
        String[] invalidFiles = {
                "file<name.jpg", "file>name.jpg", "file:name.jpg", "file\"name.jpg",
                "file/name.jpg", "file\\name.jpg", "file|name.jpg", "file?name.jpg", "file*name.jpg"
        };
        
        // When & Then
        for (String fileName : invalidFiles) {
            boolean isSafe = FileValidationUtil.isSafeFileName(fileName);
            assertFalse(isSafe, "File " + fileName + " should be considered unsafe");
        }
    }

    @Test
    void sanitizeFileName_ValidFileName_ReturnsUnchanged() {
        // Given
        String fileName = "valid-file_123.jpg";
        
        // When
        String sanitized = FileValidationUtil.sanitizeFileName(fileName);
        
        // Then
        assertEquals(fileName, sanitized);
    }

    @Test
    void sanitizeFileName_NullFileName_ReturnsDefault() {
        // When
        String sanitized = FileValidationUtil.sanitizeFileName(null);
        
        // Then
        assertEquals("unnamed_file", sanitized);
    }

    @Test
    void sanitizeFileName_InvalidCharacters_ReplacesWithUnderscore() {
        // Given
        String fileName = "file<>:\"/\\|?*name.jpg";
        String expected = "file_________name.jpg";
        
        // When
        String sanitized = FileValidationUtil.sanitizeFileName(fileName);
        
        // Then
        assertEquals(expected, sanitized);
    }

    @Test
    void sanitizeFileName_TooLongFileName_TruncatesCorrectly() {
        // Given
        String longName = "a".repeat(300);
        String extension = ".jpg";
        String fileName = longName + extension;
        
        // When
        String sanitized = FileValidationUtil.sanitizeFileName(fileName);
        
        // Then
        assertTrue(sanitized.length() <= 255);
        assertTrue(sanitized.endsWith(extension));
    }

    @Test
    void sanitizeFileName_TooLongFileNameWithoutExtension_TruncatesTo255() {
        // Given
        String fileName = "a".repeat(300);
        
        // When
        String sanitized = FileValidationUtil.sanitizeFileName(fileName);
        
        // Then
        assertEquals(255, sanitized.length());
    }

    @Test
    void isValidImageFormat_ValidImageTypes_ReturnsTrue() {
        // Given
        String[] validTypes = {
                "image/jpeg", "image/jpg", "image/png", "image/gif", 
                "image/webp", "image/bmp"
        };
        
        // When & Then
        for (String contentType : validTypes) {
            boolean isValid = FileValidationUtil.isValidImageFormat(contentType);
            assertTrue(isValid, "Content type " + contentType + " should be valid");
        }
    }

    @Test
    void isValidImageFormat_InvalidImageTypes_ReturnsFalse() {
        // Given
        String[] invalidTypes = {
                "text/plain", "application/pdf", "video/mp4", "audio/mpeg", null
        };
        
        // When & Then
        for (String contentType : invalidTypes) {
            boolean isValid = FileValidationUtil.isValidImageFormat(contentType);
            assertFalse(isValid, "Content type " + contentType + " should be invalid");
        }
    }

    @Test
    void isValidAudioFormat_ValidAudioTypes_ReturnsTrue() {
        // Given
        String[] validTypes = {
                "audio/mpeg", "audio/mp3", "audio/wav", "audio/flac", 
                "audio/ogg", "audio/aac"
        };
        
        // When & Then
        for (String contentType : validTypes) {
            boolean isValid = FileValidationUtil.isValidAudioFormat(contentType);
            assertTrue(isValid, "Content type " + contentType + " should be valid");
        }
    }

    @Test
    void isValidAudioFormat_InvalidAudioTypes_ReturnsFalse() {
        // Given
        String[] invalidTypes = {
                "text/plain", "application/pdf", "video/mp4", "image/jpeg", null
        };
        
        // When & Then
        for (String contentType : invalidTypes) {
            boolean isValid = FileValidationUtil.isValidAudioFormat(contentType);
            assertFalse(isValid, "Content type " + contentType + " should be invalid");
        }
    }

    @Test
    void isValidVideoFormat_ValidVideoTypes_ReturnsTrue() {
        // Given
        String[] validTypes = {
                "video/mp4", "video/avi", "video/mkv", "video/mov", 
                "video/wmv", "video/webm"
        };
        
        // When & Then
        for (String contentType : validTypes) {
            boolean isValid = FileValidationUtil.isValidVideoFormat(contentType);
            assertTrue(isValid, "Content type " + contentType + " should be valid");
        }
    }

    @Test
    void isValidVideoFormat_InvalidVideoTypes_ReturnsFalse() {
        // Given
        String[] invalidTypes = {
                "text/plain", "application/pdf", "audio/mpeg", "image/jpeg", null
        };
        
        // When & Then
        for (String contentType : invalidTypes) {
            boolean isValid = FileValidationUtil.isValidVideoFormat(contentType);
            assertFalse(isValid, "Content type " + contentType + " should be invalid");
        }
    }

    @Test
    void sanitizeFileName_ComplexScenario() {
        // Given
        String fileName = "My<Document>:With/Invalid\\Characters|And?Asterisk*.pdf";
        String expected = "My_Document__With_Invalid_Characters_And_Asterisk_.pdf";
        
        // When
        String sanitized = FileValidationUtil.sanitizeFileName(fileName);
        
        // Then
        assertEquals(expected, sanitized);
    }

    @Test
    void sanitizeFileName_PreservesValidSpecialCharacters() {
        // Given
        String fileName = "document-with_underscores.and.dots(parentheses)[brackets].pdf";
        
        // When
        String sanitized = FileValidationUtil.sanitizeFileName(fileName);
        
        // Then
        assertEquals(fileName, sanitized);
    }

    @Test
    void isSafeFileName_CaseInsensitiveDangerousExtensions() {
        // Given
        String[] dangerousFiles = {
                "MALWARE.EXE", "Script.BAT", "Command.CMD", "Screen.SCR",
                "Program.PIF", "Archive.JAR", "Script.JS", "Virus.VBS", "Shell.SH"
        };
        
        // When & Then
        for (String fileName : dangerousFiles) {
            boolean isSafe = FileValidationUtil.isSafeFileName(fileName);
            assertFalse(isSafe, "File " + fileName + " should be considered unsafe (case insensitive)");
        }
    }
}

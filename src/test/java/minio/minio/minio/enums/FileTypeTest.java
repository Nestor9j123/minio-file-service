package minio.minio.minio.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FileTypeTest {

    @Test
    void getBucketSuffix_ReturnsCorrectValues() {
        assertEquals("songs", FileType.SONG.getBucketSuffix());
        assertEquals("images", FileType.IMAGE.getBucketSuffix());
        assertEquals("photos", FileType.PHOTO.getBucketSuffix());
        assertEquals("videos", FileType.VIDEO.getBucketSuffix());
        assertEquals("documents", FileType.PDF.getBucketSuffix());
        assertEquals("documents", FileType.DOCUMENT.getBucketSuffix());
        assertEquals("archives", FileType.ARCHIVE.getBucketSuffix());
        assertEquals("files", FileType.FILE.getBucketSuffix());
    }

    @Test
    void getAllowedMimeTypes_ReturnsCorrectArrays() {
        // Audio
        String[] songMimeTypes = FileType.SONG.getAllowedMimeTypes();
        assertTrue(containsMimeType(songMimeTypes, "audio/mpeg"));
        assertTrue(containsMimeType(songMimeTypes, "audio/mp3"));
        assertTrue(containsMimeType(songMimeTypes, "audio/wav"));

        // Images
        String[] imageMimeTypes = FileType.IMAGE.getAllowedMimeTypes();
        assertTrue(containsMimeType(imageMimeTypes, "image/jpeg"));
        assertTrue(containsMimeType(imageMimeTypes, "image/png"));
        assertTrue(containsMimeType(imageMimeTypes, "image/gif"));

        // Videos
        String[] videoMimeTypes = FileType.VIDEO.getAllowedMimeTypes();
        assertTrue(containsMimeType(videoMimeTypes, "video/mp4"));
        assertTrue(containsMimeType(videoMimeTypes, "video/avi"));

        // PDF
        String[] pdfMimeTypes = FileType.PDF.getAllowedMimeTypes();
        assertEquals(1, pdfMimeTypes.length);
        assertEquals("application/pdf", pdfMimeTypes[0]);

        // Archives
        String[] archiveMimeTypes = FileType.ARCHIVE.getAllowedMimeTypes();
        assertTrue(containsMimeType(archiveMimeTypes, "application/zip"));
        assertTrue(containsMimeType(archiveMimeTypes, "application/x-rar-compressed"));

        // Files
        String[] fileMimeTypes = FileType.FILE.getAllowedMimeTypes();
        assertEquals(1, fileMimeTypes.length);
        assertEquals("*/*", fileMimeTypes[0]);
    }

    @ParameterizedTest
    @CsvSource({
        "audio/mpeg, SONG",
        "audio/mp3, SONG",
        "audio/wav, SONG",
        "image/jpeg, IMAGE",
        "image/png, IMAGE",
        "video/mp4, VIDEO",
        "application/pdf, PDF",
        "application/zip, ARCHIVE"
    })
    void fromMimeType_ValidMimeTypes_ReturnsCorrectFileType(String mimeType, String expectedType) {
        FileType result = FileType.fromMimeType(mimeType);
        assertEquals(FileType.valueOf(expectedType), result);
    }

    @Test
    void fromMimeType_InvalidMimeType_ReturnsNull() {
        FileType result = FileType.fromMimeType("invalid/mimetype");
        assertNull(result);
    }

    @Test
    void fromMimeType_NullMimeType_ReturnsNull() {
        FileType result = FileType.fromMimeType(null);
        assertNull(result);
    }

    @Test
    void fromMimeType_EmptyMimeType_ReturnsNull() {
        FileType result = FileType.fromMimeType("");
        assertNull(result);
    }

    @Test
    void fromMimeType_CaseInsensitive_ReturnsCorrectFileType() {
        FileType result1 = FileType.fromMimeType("IMAGE/JPEG");
        FileType result2 = FileType.fromMimeType("image/jpeg");
        FileType result3 = FileType.fromMimeType("Image/Jpeg");
        
        assertEquals(FileType.IMAGE, result1);
        assertEquals(FileType.IMAGE, result2);
        assertEquals(FileType.IMAGE, result3);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "audio/mpeg", "audio/mp3", "audio/wav", "audio/flac", "audio/ogg", "audio/aac", "audio/m4a"
    })
    void isValidMimeType_SongType_ValidMimeTypes_ReturnsTrue(String mimeType) {
        assertTrue(FileType.SONG.isValidMimeType(mimeType));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp", "image/svg+xml"
    })
    void isValidMimeType_ImageType_ValidMimeTypes_ReturnsTrue(String mimeType) {
        assertTrue(FileType.IMAGE.isValidMimeType(mimeType));
    }

    @Test
    void isValidMimeType_FileType_AcceptsAnyMimeType() {
        assertTrue(FileType.FILE.isValidMimeType("any/mimetype"));
        assertTrue(FileType.FILE.isValidMimeType("application/unknown"));
        assertTrue(FileType.FILE.isValidMimeType("text/plain"));
        assertTrue(FileType.FILE.isValidMimeType("*/*"));
    }

    @Test
    void isValidMimeType_InvalidMimeType_ReturnsFalse() {
        assertFalse(FileType.SONG.isValidMimeType("image/jpeg"));
        assertFalse(FileType.IMAGE.isValidMimeType("audio/mpeg"));
        assertFalse(FileType.VIDEO.isValidMimeType("application/pdf"));
    }

    @Test
    void isValidMimeType_NullMimeType_ReturnsFalse() {
        assertFalse(FileType.SONG.isValidMimeType(null));
        assertFalse(FileType.IMAGE.isValidMimeType(null));
        // FILE type should still return true for null (accepts everything)
        assertTrue(FileType.FILE.isValidMimeType(null));
    }

    @Test
    void getMaxFileSize_ReturnsCorrectSizes() {
        assertEquals(50L * 1024 * 1024, FileType.SONG.getMaxFileSize()); // 50MB
        assertEquals(10L * 1024 * 1024, FileType.IMAGE.getMaxFileSize()); // 10MB
        assertEquals(10L * 1024 * 1024, FileType.PHOTO.getMaxFileSize()); // 10MB
        assertEquals(500L * 1024 * 1024, FileType.VIDEO.getMaxFileSize()); // 500MB
        assertEquals(100L * 1024 * 1024, FileType.PDF.getMaxFileSize()); // 100MB
        assertEquals(100L * 1024 * 1024, FileType.DOCUMENT.getMaxFileSize()); // 100MB
        assertEquals(200L * 1024 * 1024, FileType.ARCHIVE.getMaxFileSize()); // 200MB
        assertEquals(100L * 1024 * 1024, FileType.FILE.getMaxFileSize()); // 100MB
    }

    @Test
    void values_ReturnsAllFileTypes() {
        FileType[] values = FileType.values();
        assertEquals(8, values.length);
        
        assertTrue(containsFileType(values, FileType.SONG));
        assertTrue(containsFileType(values, FileType.IMAGE));
        assertTrue(containsFileType(values, FileType.PHOTO));
        assertTrue(containsFileType(values, FileType.VIDEO));
        assertTrue(containsFileType(values, FileType.PDF));
        assertTrue(containsFileType(values, FileType.DOCUMENT));
        assertTrue(containsFileType(values, FileType.ARCHIVE));
        assertTrue(containsFileType(values, FileType.FILE));
    }

    @Test
    void valueOf_ValidName_ReturnsCorrectFileType() {
        assertEquals(FileType.SONG, FileType.valueOf("SONG"));
        assertEquals(FileType.IMAGE, FileType.valueOf("IMAGE"));
        assertEquals(FileType.VIDEO, FileType.valueOf("VIDEO"));
    }

    @Test
    void valueOf_InvalidName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> FileType.valueOf("INVALID"));
    }

    @Test
    void documentAndPdfTypes_ShareSameBucket() {
        assertEquals(FileType.PDF.getBucketSuffix(), FileType.DOCUMENT.getBucketSuffix());
    }

    @Test
    void photoMimeTypes_IncludeRawFormats() {
        String[] photoMimeTypes = FileType.PHOTO.getAllowedMimeTypes();
        assertTrue(containsMimeType(photoMimeTypes, "image/raw"));
        assertTrue(containsMimeType(photoMimeTypes, "image/tiff"));
        assertTrue(containsMimeType(photoMimeTypes, "image/heic"));
        assertTrue(containsMimeType(photoMimeTypes, "image/heif"));
    }

    @Test
    void documentMimeTypes_IncludeOfficeFormats() {
        String[] docMimeTypes = FileType.DOCUMENT.getAllowedMimeTypes();
        assertTrue(containsMimeType(docMimeTypes, "application/msword"));
        assertTrue(containsMimeType(docMimeTypes, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        assertTrue(containsMimeType(docMimeTypes, "application/vnd.ms-excel"));
        assertTrue(containsMimeType(docMimeTypes, "text/plain"));
        assertTrue(containsMimeType(docMimeTypes, "text/csv"));
    }

    @Test
    void archiveMimeTypes_IncludeCommonFormats() {
        String[] archiveMimeTypes = FileType.ARCHIVE.getAllowedMimeTypes();
        assertTrue(containsMimeType(archiveMimeTypes, "application/zip"));
        assertTrue(containsMimeType(archiveMimeTypes, "application/x-rar-compressed"));
        assertTrue(containsMimeType(archiveMimeTypes, "application/x-7z-compressed"));
        assertTrue(containsMimeType(archiveMimeTypes, "application/x-tar"));
        assertTrue(containsMimeType(archiveMimeTypes, "application/gzip"));
    }

    // Helper methods
    private boolean containsMimeType(String[] mimeTypes, String target) {
        for (String mimeType : mimeTypes) {
            if (mimeType.equals(target)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsFileType(FileType[] fileTypes, FileType target) {
        for (FileType fileType : fileTypes) {
            if (fileType == target) {
                return true;
            }
        }
        return false;
    }
}

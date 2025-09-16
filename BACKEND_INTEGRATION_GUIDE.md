# 🚀 Guide d'Intégration MinIO File Service pour les Backends

## 📋 **RÉSUMÉ RAPIDE**

Votre service MinIO est déployé en ligne et accessible via :
- **🌐 Service URL** : `https://votre-service-minio.com`
- **📚 Documentation Swagger** : `https://votre-service-minio.com/swagger-ui.html`
- **📦 SDK Client** : Dépendance Maven disponible

---

## 🎯 **2 FAÇONS D'UTILISER LE SERVICE**

### **🏆 OPTION 1 : SDK CLIENT JAVA (RECOMMANDÉE)**

#### **1. Ajouter la dépendance Maven**
```xml
<dependency>
    <groupId>com.fileservice</groupId>
    <artifactId>minio-client-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### **2. Utiliser dans votre code**
```java
@Service
public class UserService {
    
    private final MinioFileClient fileClient;
    
    public UserService() {
        // URL de votre service MinIO déployé
        this.fileClient = new MinioFileClient("https://votre-service-minio.com");
    }
    
    // Upload photo utilisateur
    public String uploadUserPhoto(Long userId, byte[] photoData, String fileName) {
        FileUploadResponse response = fileClient.uploadFile(
            photoData, 
            fileName, 
            FileType.PHOTO, 
            "user_" + userId + "_photo.jpg"
        );
        
        // Sauvegarder l'URL en base
        User user = userRepository.findById(userId);
        user.setPhotoUrl(response.getFileUrl());
        userRepository.save(user);
        
        return response.getFileUrl();
    }
    
    // Upload document PDF
    public String uploadDocument(byte[] pdfData, String fileName) {
        FileUploadResponse response = fileClient.uploadFile(
            pdfData, 
            fileName, 
            FileType.PDF
        );
        
        return response.getFileUrl();
    }
    
    // Télécharger un fichier
    public InputStreamResource downloadFile(String fileName, FileType fileType) {
        return fileClient.downloadFile(fileName, fileType);
    }
    
    // Obtenir métadonnées PDF
    public FileMetadata getPdfInfo(String fileName) {
        return fileClient.getFileMetadata(fileName, FileType.PDF);
    }
    
    // Générer thumbnail PDF
    public byte[] getPdfThumbnail(String fileName) {
        return fileClient.generatePdfThumbnail(fileName, 200, 200);
    }
}
```

#### **✅ Avantages du SDK Client**
- ✅ **Super simple** : Une seule classe à utiliser
- ✅ **Type-safe** : Enums pour les types de fichiers
- ✅ **Gestion d'erreurs** intégrée
- ✅ **Pas besoin de connaître les URLs** REST

---

### **🌐 OPTION 2 : API REST DIRECTE**

#### **Consulter la documentation Swagger**
Allez sur : `https://votre-service-minio.com/swagger-ui.html`

#### **Endpoints principaux**
```bash
# Upload fichier
POST https://votre-service-minio.com/files/upload/{fileType}
Content-Type: multipart/form-data
Body: file=@photo.jpg&customFileName=user_123_photo.jpg

# Télécharger fichier
GET https://votre-service-minio.com/files/download/{fileType}/{fileName}

# Métadonnées
GET https://votre-service-minio.com/files/metadata/{fileType}/{fileName}

# Thumbnail PDF
GET https://votre-service-minio.com/files/pdf/thumbnail/{fileName}?width=200&height=200

# URL présignée
GET https://votre-service-minio.com/files/presigned-url/{fileType}/{fileName}?expiryMinutes=60
```

#### **Exemple avec RestTemplate**
```java
@Service
public class FileService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String MINIO_SERVICE_URL = "https://votre-service-minio.com";
    
    public String uploadPhoto(byte[] photoData, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(photoData) {
            @Override
            public String getFilename() { return fileName; }
        });
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<FileUploadResponse> response = restTemplate.postForEntity(
            MINIO_SERVICE_URL + "/files/upload/PHOTO", 
            requestEntity, 
            FileUploadResponse.class
        );
        
        return response.getBody().getFileUrl();
    }
}
```

---

## 📋 **TYPES DE FICHIERS SUPPORTÉS**

```java
public enum FileType {
    SONG,       // Fichiers audio (MP3, WAV, etc.)
    IMAGE,      // Images générales (JPEG, PNG, GIF, etc.)
    VIDEO,      // Vidéos (MP4, AVI, etc.)
    PHOTO,      // Photos utilisateurs (JPEG, PNG, etc.)
    PDF,        // Documents PDF avec processing
    DOCUMENT,   // Documents Office (Word, Excel, etc.)
    ARCHIVE,    // Archives (ZIP, RAR, etc.)
    FILE        // Fichiers génériques
}
```

---

## 🔧 **CONFIGURATION RECOMMANDÉE**

### **Dans votre `application.yml`**
```yaml
# Configuration pour le SDK Client
minio:
  file:
    service:
      base-url: https://votre-service-minio.com
      timeout-seconds: 60
      
# Configuration pour RestTemplate (si vous utilisez l'API REST)
spring:
  web:
    client:
      timeout:
        connect: 30s
        read: 60s
```

---

## 🎯 **EXEMPLES D'USAGE COURANTS**

### **1. Upload photo de profil utilisateur**
```java
public String uploadUserProfilePhoto(Long userId, MultipartFile photo) {
    try {
        byte[] photoData = photo.getBytes();
        String customFileName = "user_" + userId + "_profile.jpg";
        
        FileUploadResponse response = fileClient.uploadFile(
            photoData, 
            photo.getOriginalFilename(), 
            FileType.PHOTO, 
            customFileName
        );
        
        return response.getFileUrl();
    } catch (Exception e) {
        throw new RuntimeException("Erreur upload photo: " + e.getMessage());
    }
}
```

### **2. Upload et traitement de document PDF**
```java
public DocumentInfo uploadAndProcessPdf(MultipartFile pdfFile) {
    try {
        byte[] pdfData = pdfFile.getBytes();
        
        // Upload du PDF
        FileUploadResponse uploadResponse = fileClient.uploadFile(
            pdfData, 
            pdfFile.getOriginalFilename(), 
            FileType.PDF
        );
        
        // Obtenir les métadonnées
        FileMetadata metadata = fileClient.getFileMetadata(
            uploadResponse.getFileName(), 
            FileType.PDF
        );
        
        // Générer un thumbnail
        byte[] thumbnail = fileClient.generatePdfThumbnail(
            uploadResponse.getFileName(), 
            200, 
            200
        );
        
        return DocumentInfo.builder()
            .fileUrl(uploadResponse.getFileUrl())
            .pageCount(metadata.getPageCount())
            .title(metadata.getTitle())
            .author(metadata.getAuthor())
            .thumbnail(thumbnail)
            .build();
            
    } catch (Exception e) {
        throw new RuntimeException("Erreur traitement PDF: " + e.getMessage());
    }
}
```

### **3. Téléchargement sécurisé avec URL présignée**
```java
public String getSecureDownloadUrl(String fileName, FileType fileType) {
    // URL valide pendant 1 heure
    return fileClient.getPresignedUrl(fileName, fileType, 60);
}
```

---

## 🚀 **DÉMARRAGE RAPIDE**

### **Pour commencer rapidement :**

1. **Ajoutez la dépendance** dans votre `pom.xml`
2. **Créez un service** avec `MinioFileClient`
3. **Testez avec un upload simple** :

```java
@RestController
public class TestController {
    
    private final MinioFileClient fileClient = new MinioFileClient("https://votre-service-minio.com");
    
    @PostMapping("/test-upload")
    public String testUpload(@RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponse response = fileClient.uploadFile(
                file.getBytes(), 
                file.getOriginalFilename(), 
                FileType.IMAGE
            );
            return "Upload réussi ! URL: " + response.getFileUrl();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }
}
```

---

## 📞 **SUPPORT ET RESSOURCES**

- **📚 Documentation Swagger** : `https://votre-service-minio.com/swagger-ui.html`
- **🔍 Health Check** : `https://votre-service-minio.com/actuator/health`
- **📊 Métriques** : `https://votre-service-minio.com/actuator/metrics`

---

## 🎉 **RÉSUMÉ**

**Vous avez 2 choix simples :**

1. **🏆 SDK Client** (recommandé) → Ajoutez la dépendance Maven, utilisez `MinioFileClient`
2. **🌐 API REST** → Consultez Swagger, appelez les endpoints HTTP

**Mon conseil : Commencez par l'Option 1 (SDK Client) pour une intégration rapide et robuste !**

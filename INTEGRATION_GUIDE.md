# 🚀 Guide d'Intégration MinIO File Service

## 📋 **3 OPTIONS POUR LES ÉQUIPES BACKEND**

Voici les 3 solutions que vous pouvez proposer aux équipes backend pour utiliser votre service MinIO :

---

## 🎯 **OPTION 1 : SDK CLIENT JAVA (RECOMMANDÉE)**

### **✅ Avantages**
- **Super simple** : Une seule classe à utiliser
- **Type-safe** : Enums pour les types de fichiers
- **Gestion d'erreurs** intégrée
- **Pas besoin de connaître les URLs** REST

### **📦 Ce que vous donnez aux backends**
```
minio-client-sdk/
├── pom.xml
└── src/main/java/com/fileservice/client/
    ├── MinioFileClient.java          # Client principal
    ├── MinioClientException.java     # Gestion d'erreurs
    ├── dto/
    │   ├── FileUploadResponse.java
    │   └── FileMetadata.java
    └── enums/
        └── FileType.java
```

### **🔧 Comment les backends l'utilisent**

**1. Ajouter la dépendance Maven :**
```xml
<dependency>
    <groupId>com.fileservice</groupId>
    <artifactId>minio-client-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

**2. Utiliser dans leur code :**
```java
@Service
public class UserService {
    
    private final MinioFileClient fileClient;
    
    public UserService() {
        this.fileClient = new MinioFileClient("http://minio-service:8080");
    }
    
    // Upload photo utilisateur
    public String uploadUserPhoto(Long userId, MultipartFile photo) {
        String customFileName = "user_" + userId + "_photo.jpg";
        FileUploadResponse response = fileClient.uploadFile(photo, FileType.PHOTO, customFileName);
        
        // Sauvegarder l'URL en base
        User user = userRepository.findById(userId);
        user.setPhotoUrl(response.getFileUrl());
        userRepository.save(user);
        
        return response.getFileUrl();
    }
    
    // Télécharger une photo
    public InputStreamResource getUserPhoto(String fileName) {
        return fileClient.downloadFile(fileName, FileType.PHOTO);
    }
    
    // Upload document PDF
    public String uploadDocument(MultipartFile pdf, String docName) {
        FileUploadResponse response = fileClient.uploadFile(pdf, FileType.PDF, docName);
        return response.getFileUrl();
    }
    
    // Générer thumbnail PDF
    public byte[] getPdfThumbnail(String fileName) {
        return fileClient.generatePdfThumbnail(fileName, 200, 200);
    }
}
```

---

## 🌐 **OPTION 2 : API REST + DOCUMENTATION**

### **✅ Avantages**
- **Flexible** : Utilisable avec n'importe quel langage
- **Documentation Swagger** complète
- **Exemples concrets** fournis

### **📚 Ce que vous donnez aux backends**
- **Documentation Swagger** : `http://votre-service:8080/swagger-ui.html`
- **Collection Postman** avec tous les exemples
- **Fichier d'exemples** : `integration-examples/RestExamples.java`

### **🔧 Endpoints principaux**
```bash
# Upload fichier
POST /files/upload/{fileType}
Content-Type: multipart/form-data
Body: file=@photo.jpg&customFileName=user_123_photo.jpg

# Télécharger fichier
GET /files/download/{fileType}/{fileName}

# Métadonnées
GET /files/metadata/{fileType}/{fileName}

# URL présignée
GET /files/presigned-url/{fileType}/{fileName}?expiryMinutes=60

# Thumbnail PDF
GET /files/pdf/thumbnail/{fileName}?width=200&height=200

# Supprimer
DELETE /files/{fileType}/{fileName}
```

### **💡 Exemple d'utilisation**
```java
// Upload avec RestTemplate
MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
body.add("file", photo.getResource());
body.add("customFileName", "user_123_photo.jpg");

ResponseEntity<FileUploadResponse> response = restTemplate.postForEntity(
    "http://minio-service:8080/files/upload/PHOTO", 
    new HttpEntity<>(body, headers), 
    FileUploadResponse.class
);

String fileUrl = response.getBody().getFileUrl();
```

---

## ⚡ **OPTION 3 : SPRING BOOT STARTER**

### **✅ Avantages**
- **Auto-configuration** Spring Boot
- **Configuration par propriétés**
- **Injection automatique**

### **📦 Ce que vous donnez aux backends**
- Dépendance Spring Boot Starter
- Auto-configuration classe

### **🔧 Comment les backends l'utilisent**

**1. Ajouter la dépendance :**
```xml
<dependency>
    <groupId>com.fileservice</groupId>
    <artifactId>minio-file-service-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**2. Configurer dans `application.yml` :**
```yaml
minio:
  file:
    service:
      base-url: http://minio-service:8080
      timeout-seconds: 60
      enable-metrics: true
```

**3. Utiliser directement :**
```java
@Service
public class MyService {
    
    @Autowired
    private MinioFileClient fileClient;  // Auto-injecté !
    
    public String uploadPhoto(MultipartFile photo) {
        return fileClient.uploadFile(photo, FileType.PHOTO).getFileUrl();
    }
}
```

---

## 🎯 **MA RECOMMANDATION**

### **Pour la plupart des backends → OPTION 1 (SDK Client)**

**Pourquoi ?**
- ✅ **Simple** : Une seule classe à comprendre
- ✅ **Robuste** : Gestion d'erreurs intégrée
- ✅ **Type-safe** : Pas d'erreurs de typage
- ✅ **Maintenance** : Centralisée dans le SDK

### **Cas d'usage par option :**

| Cas d'usage | Option recommandée |
|-------------|-------------------|
| Backend Spring Boot standard | **Option 1** (SDK Client) |
| Backend non-Java | **Option 2** (REST API) |
| Microservices avec beaucoup de backends | **Option 3** (Starter) |
| Intégration rapide/prototype | **Option 2** (REST API) |

---

## 🚀 **DÉPLOIEMENT ET DISTRIBUTION**

### **Comment distribuer le SDK :**

1. **Repository Maven privé** (recommandé)
2. **JAR dans un repository partagé**
3. **Git submodule**

### **Exemple Docker Compose pour dev :**
```yaml
version: '3.8'
services:
  minio:
    image: minio/minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    command: server /data --console-address ":9001"

  minio-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      MINIO_URL: http://minio:9000
      MINIO_ACCESS_KEY: minioadmin
      MINIO_SECRET_KEY: minioadmin
    depends_on:
      - minio

  backend-app:
    build: ./backend
    ports:
      - "8081:8081"
    environment:
      MINIO_FILE_SERVICE_URL: http://minio-service:8080
    depends_on:
      - minio-service
```

---

## 📞 **SUPPORT ET DOCUMENTATION**

- **Swagger UI** : `http://service:8080/swagger-ui.html`
- **Health Check** : `http://service:8080/actuator/health`
- **Metrics** : `http://service:8080/actuator/metrics`

---

## 🎉 **RÉSUMÉ POUR LES ÉQUIPES**

**Vous avez 3 choix :**

1. **🎯 SDK Client** (le plus simple) → Ajoutez une dépendance, utilisez `MinioFileClient`
2. **🌐 API REST** (le plus flexible) → Appelez les endpoints HTTP
3. **⚡ Spring Starter** (le plus intégré) → Auto-configuration Spring Boot

**Mon conseil : Commencez par l'Option 1 (SDK Client) !**

# MinIO File Management Service

Un service complet de gestion de fichiers utilisant MinIO pour le stockage, conçu pour être facilement intégré dans d'autres projets Spring Boot.

## 🚀 Fonctionnalités

- **Gestion complète des fichiers** : Upload, download, streaming, suppression
- **Support multi-formats** : PDF, documents Office, images, vidéos, audio, archives
- **Traitement PDF avancé** : Extraction de métadonnées, génération de thumbnails, validation
- **API REST complète** : Endpoints documentés avec Swagger/OpenAPI
- **Déploiement cloud-ready** : Configuration pour Fly.io et autres plateformes
- **Validation et sécurité** : Validation des types de fichiers et tailles
- **Métadonnées enrichies** : Extraction automatique pour PDF et autres formats

## 📋 Types de fichiers supportés

| Type | Extensions | Taille max | Bucket |
|------|------------|------------|--------|
| **Audio (SONG)** | mp3, wav, flac, ogg, aac, m4a | 50MB | file-service-songs |
| **Images** | jpg, png, gif, webp, bmp, svg | 10MB | file-service-images |
| **Photos** | jpg, png, raw, tiff, heic, heif | 10MB | file-service-photos |
| **Vidéos** | mp4, avi, mkv, mov, wmv, webm, flv | 500MB | file-service-videos |
| **PDF** | pdf | 100MB | file-service-documents |
| **Documents** | doc, docx, xls, xlsx, ppt, pptx, txt, csv | 100MB | file-service-documents |
| **Archives** | zip, rar, 7z, tar, gz | 200MB | file-service-archives |
| **Fichiers génériques** | tous types | 100MB | file-service-files |

## 🛠️ Installation et Configuration

### 1. Dépendances Maven

Ajoutez les dépendances suivantes à votre `pom.xml` :

```xml
<dependencies>
    <!-- MinIO Client -->
    <dependency>
        <groupId>io.minio</groupId>
        <artifactId>minio</artifactId>
        <version>8.5.7</version>
    </dependency>
    
    <!-- Apache Tika pour détection de types -->
    <dependency>
        <groupId>org.apache.tika</groupId>
        <artifactId>tika-core</artifactId>
        <version>2.9.1</version>
    </dependency>
    
    <!-- PDFBox pour traitement PDF -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.1</version>
    </dependency>
    
    <!-- Swagger pour documentation API -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>
</dependencies>
```

### 2. Configuration Application

Ajoutez à votre `application.yml` :

```yaml
# MinIO Configuration
minio:
  url: ${MINIO_URL:http://localhost:9000}
  access-key: ${MINIO_ACCESS_KEY:minioadmin}
  secret-key: ${MINIO_SECRET_KEY:minioadmin}
  bucket:
    songs: ${MINIO_BUCKET_SONGS:your-app-songs}
    images: ${MINIO_BUCKET_IMAGES:your-app-images}
    videos: ${MINIO_BUCKET_VIDEOS:your-app-videos}
    photos: ${MINIO_BUCKET_PHOTOS:your-app-photos}
    documents: ${MINIO_BUCKET_DOCUMENTS:your-app-documents}
    archives: ${MINIO_BUCKET_ARCHIVES:your-app-archives}
    files: ${MINIO_BUCKET_FILES:your-app-files}

spring:
  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 500MB
```

### 3. Intégration dans votre projet

Copiez le package `nitchcorp.backend.titan.shared.minio` dans votre projet et adaptez le nom du package selon vos besoins.

## 📖 Utilisation

### Service Java

```java
@Autowired
private MinioService minioService;

@Autowired
private PdfProcessingService pdfProcessingService;

// Upload d'un fichier
@PostMapping("/upload")
public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
    FileUploadResponse response = minioService.uploadFile(file, FileType.PDF);
    return ResponseEntity.ok(response);
}

// Download d'un fichier
@GetMapping("/download/{fileName}")
public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
    FileDownloadResponse response = minioService.downloadFile(fileName, FileType.PDF);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(response.getContentType()))
        .body(new InputStreamResource(response.getInputStream()));
}

// Métadonnées PDF
@GetMapping("/pdf/metadata/{fileName}")
public ResponseEntity<FileMetadata> getPdfMetadata(@PathVariable String fileName) {
    FileMetadata metadata = minioService.getFileMetadata(fileName, FileType.PDF);
    return ResponseEntity.ok(metadata);
}
```

### API REST

#### Upload de fichier
```bash
POST /files/upload/{fileType}
Content-Type: multipart/form-data

curl -X POST "http://localhost:8080/files/upload/PDF" \
  -F "file=@document.pdf" \
  -F "customFileName=mon-document.pdf"
```

#### Download de fichier
```bash
GET /files/download/{fileType}/{fileName}

curl "http://localhost:8080/files/download/PDF/mon-document.pdf" \
  --output document.pdf
```

#### Streaming de fichier
```bash
GET /files/stream/{fileType}/{fileName}

curl "http://localhost:8080/files/stream/PDF/mon-document.pdf"
```

#### Métadonnées
```bash
GET /files/metadata/{fileType}/{fileName}

curl "http://localhost:8080/files/metadata/PDF/mon-document.pdf"
```

#### Liste des fichiers
```bash
GET /files/list/{fileType}

curl "http://localhost:8080/files/list/PDF"
```

#### URL présignée
```bash
GET /files/presigned-url/{fileType}/{fileName}?expiryMinutes=60

curl "http://localhost:8080/files/presigned-url/PDF/mon-document.pdf?expiryMinutes=120"
```

## 🚀 Déploiement

### Déploiement local avec Docker

```bash
# Build de l'image
docker build -t minio-file-service .

# Run du container
docker run -p 8080:8080 \
  -e MINIO_URL=http://your-minio-server:9000 \
  -e MINIO_ACCESS_KEY=your-access-key \
  -e MINIO_SECRET_KEY=your-secret-key \
  minio-file-service
```

### Déploiement sur Fly.io

1. **Installation de Fly CLI** :
```bash
curl -L https://fly.io/install.sh | sh
```

2. **Login et initialisation** :
```bash
fly auth login
fly launch
```

3. **Configuration des secrets** :
```bash
fly secrets set MINIO_URL=https://your-minio-server.com
fly secrets set MINIO_ACCESS_KEY=your-access-key
fly secrets set MINIO_SECRET_KEY=your-secret-key
```

4. **Déploiement** :
```bash
fly deploy
```

### Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|---------|
| `MINIO_URL` | URL du serveur MinIO | `http://localhost:9000` |
| `MINIO_ACCESS_KEY` | Clé d'accès MinIO | `minioadmin` |
| `MINIO_SECRET_KEY` | Clé secrète MinIO | `minioadmin` |
| `SPRING_PROFILES_ACTIVE` | Profil Spring actif | `dev` |
| `PORT` | Port du serveur | `8080` |
| `MANAGEMENT_PORT` | Port des endpoints de management | `8081` |

## 📚 Documentation API

Une fois l'application démarrée, la documentation Swagger est disponible à :
- **Interface Swagger UI** : `http://localhost:8080/swagger-ui.html`
- **Spécification OpenAPI** : `http://localhost:8080/api-docs`

## 🔧 Endpoints de monitoring

- **Health Check** : `GET /actuator/health`
- **Informations** : `GET /actuator/info`
- **Métriques** : `GET /actuator/metrics`

## 🏗️ Architecture

```
src/main/java/nitchcorp/backend/titan/shared/minio/
├── config/
│   ├── MinioConfig.java              # Configuration MinIO
│   └── MinioProperties.java          # Propriétés de configuration
├── controller/
│   └── MinioController.java          # API REST endpoints
├── dto/
│   ├── FileUploadResponse.java       # Réponse d'upload
│   ├── FileDownloadResponse.java     # Réponse de download
│   └── FileMetadata.java             # Métadonnées de fichier
├── enums/
│   └── FileType.java                 # Types de fichiers supportés
├── exception/
│   ├── MinioException.java           # Exception générale
│   ├── FileNotFoundException.java    # Fichier non trouvé
│   ├── InvalidFileTypeException.java # Type invalide
│   └── MinioExceptionHandler.java    # Gestionnaire d'exceptions
├── service/
│   ├── MinioService.java             # Interface service principal
│   ├── PdfProcessingService.java     # Interface traitement PDF
│   └── impl/
│       ├── MinioServiceImpl.java     # Implémentation service principal
│       └── PdfProcessingServiceImpl.java # Implémentation PDF
└── util/
    └── FileValidationUtil.java       # Utilitaires de validation
```

## 🔒 Sécurité

- **Validation des types de fichiers** : Utilisation d'Apache Tika pour la détection
- **Limitation de taille** : Tailles maximales configurables par type
- **Sanitisation des noms** : Nettoyage automatique des noms de fichiers
- **URLs présignées** : Accès temporaire sécurisé
- **Validation d'intégrité** : Vérification des fichiers PDF

## 🤝 Intégration dans vos projets

### 1. Copier le package
Copiez le package `minio` dans votre projet et adaptez le nom du package.

### 2. Ajouter les dépendances
Ajoutez les dépendances Maven listées ci-dessus.

### 3. Configuration
Adaptez la configuration MinIO selon vos besoins.

### 4. Utilisation
Injectez `MinioService` dans vos services et contrôleurs.

## 📞 Support

Pour toute question ou problème :
- **Email** : support@fileservice.com
- **Documentation** : Consultez la documentation Swagger
- **Logs** : Vérifiez les logs de l'application pour le debug

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

# Package MinIO pour Titan Tunes

Ce package fournit une intégration complète avec MinIO pour la gestion des fichiers multimédias dans l'application Titan Tunes.

## Fonctionnalités

- **Upload de fichiers** : Songs, Images, Vidéos, Photos
- **Download et streaming** : Téléchargement direct ou streaming
- **Gestion des métadonnées** : Informations complètes sur les fichiers
- **URLs présignées** : Accès temporaire sécurisé
- **Validation des fichiers** : Type, taille et sécurité
- **Gestion d'erreurs** : Exceptions personnalisées et gestion globale

## Types de fichiers supportés

### Songs (Audio)
- MP3, WAV, FLAC, OGG, AAC
- Taille max : 50MB
- Bucket : `titan-songs`

### Images
- JPEG, PNG, GIF, WebP, BMP
- Taille max : 10MB
- Bucket : `titan-images`

### Videos
- MP4, AVI, MKV, MOV, WMV, WebM
- Taille max : 500MB
- Bucket : `titan-videos`

### Photos
- JPEG, PNG, RAW, TIFF
- Taille max : 10MB
- Bucket : `titan-photos`

## Configuration

### Application Properties
```properties
# MinIO Configuration
minio.url=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket.songs=titan-songs
minio.bucket.images=titan-images
minio.bucket.videos=titan-videos
minio.bucket.photos=titan-photos
```

## API Endpoints

### Upload de fichiers
```
POST /api/files/upload/{fileType}
- fileType: SONG, IMAGE, VIDEO, PHOTO
- file: MultipartFile
- customFileName: String (optionnel)
```

### Download de fichiers
```
GET /api/files/download/{fileType}/{fileName}
GET /api/files/stream/{fileType}/{fileName}
```

### Gestion des fichiers
```
DELETE /api/files/{fileType}/{fileName}
GET /api/files/metadata/{fileType}/{fileName}
GET /api/files/list/{fileType}
GET /api/files/exists/{fileType}/{fileName}
```

### URLs
```
GET /api/files/url/{fileType}/{fileName}
GET /api/files/presigned-url/{fileType}/{fileName}?expiryMinutes=60
```

### Upload multiple
```
POST /api/files/upload-multiple/{fileType}
- files: MultipartFile[]
```

## Utilisation du Service

```java
@Autowired
private MinioService minioService;

// Upload
FileUploadResponse response = minioService.uploadFile(file, FileType.SONG);

// Download
FileDownloadResponse download = minioService.downloadFile("song.mp3", FileType.SONG);

// Vérifier existence
boolean exists = minioService.fileExists("song.mp3", FileType.SONG);

// Obtenir métadonnées
FileMetadata metadata = minioService.getFileMetadata("song.mp3", FileType.SONG);
```

## Sécurité

- Validation des types de fichiers
- Vérification de la taille des fichiers
- Sanitisation des noms de fichiers
- Protection contre les extensions dangereuses
- URLs présignées avec expiration

## Gestion d'erreurs

- `FileNotFoundException` : Fichier non trouvé
- `InvalidFileTypeException` : Type de fichier non supporté
- `MinioException` : Erreurs générales MinIO
- `MaxUploadSizeExceededException` : Taille de fichier dépassée

## Architecture

```
minio/
├── config/
│   ├── MinioConfig.java
│   └── MinioAutoConfiguration.java
├── controller/
│   └── MinioController.java
├── dto/
│   ├── FileUploadResponse.java
│   ├── FileDownloadResponse.java
│   └── FileMetadata.java
├── enums/
│   └── FileType.java
├── exception/
│   ├── MinioException.java
│   ├── FileNotFoundException.java
│   ├── InvalidFileTypeException.java
│   └── MinioExceptionHandler.java
├── service/
│   ├── MinioService.java
│   └── impl/MinioServiceImpl.java
└── util/
    └── FileValidationUtil.java
```

## Prérequis

1. MinIO Server en cours d'exécution
2. Dépendances Maven ajoutées
3. Configuration des propriétés

## Installation MinIO

```bash
# Docker
docker run -p 9000:9000 -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  minio/minio server /data --console-address ":9001"
```

Accès console : http://localhost:9001

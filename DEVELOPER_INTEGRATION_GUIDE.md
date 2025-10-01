# 🚀 MinIO File Service - Guide d'Intégration Développeur

## 📋 Vue d'ensemble

Ce service MinIO File Management est une API REST complète pour la gestion de fichiers avec support multi-formats, métadonnées automatiques, et traitement PDF avancé.

### 🎯 Fonctionnalités Principales

- ✅ **Upload/Download** de fichiers multi-formats
- ✅ **7 Types de buckets** automatiquement créés
- ✅ **Métadonnées automatiques** (taille, type, date, etc.)
- ✅ **Traitement PDF** (thumbnails, extraction de texte)
- ✅ **URLs présignées** pour accès temporaire
- ✅ **Streaming** de fichiers volumineux
- ✅ **API REST** complète avec documentation

## 🌐 URLs de Production

| Service | URL | Description |
|---------|-----|-------------|
| **API Base** | `https://minio-file-service-6s7p.onrender.com` | URL principale du service |
| **Health Check** | `https://minio-file-service-6s7p.onrender.com/actuator/health` | Vérification de l'état |
| **API Documentation** | `https://minio-file-service-6s7p.onrender.com/api-docs` | Documentation OpenAPI |
| **Swagger UI** | `https://minio-file-service-6s7p.onrender.com/swagger-ui.html` | Interface interactive |

## 📁 Types de Fichiers Supportés

### 🎵 SONG (Bucket: minio-songs)
- **Formats** : mp3, wav, flac, ogg, aac, m4a
- **Taille max** : 50MB
- **Endpoint** : `/api/files/upload/SONG`

### 🖼️ IMAGE (Bucket: minio-images)
- **Formats** : jpg, jpeg, png, gif, webp, bmp, svg
- **Taille max** : 10MB
- **Endpoint** : `/api/files/upload/IMAGE`

### 📹 VIDEO (Bucket: minio-videos)
- **Formats** : mp4, avi, mkv, mov, wmv, webm, flv
- **Taille max** : 500MB
- **Endpoint** : `/api/files/upload/VIDEO`

### 📸 PHOTO (Bucket: minio-photos)
- **Formats** : jpg, jpeg, png, raw, tiff, heic, heif
- **Taille max** : 10MB
- **Endpoint** : `/api/files/upload/PHOTO`

### 📄 DOCUMENT (Bucket: minio-documents)
- **Formats** : pdf, doc, docx, xls, xlsx, ppt, pptx, txt
- **Taille max** : 100MB
- **Endpoint** : `/api/files/upload/DOCUMENT`

### 📦 ARCHIVE (Bucket: minio-archives)
- **Formats** : zip, rar, 7z, tar, gz, bz2
- **Taille max** : 200MB
- **Endpoint** : `/api/files/upload/ARCHIVE`

### 📁 FILE (Bucket: minio-files)
- **Formats** : Tous types non spécifiés
- **Taille max** : 100MB
- **Endpoint** : `/api/files/upload/FILE`

## 🔧 Intégration API

### 1. 📤 Upload de Fichier

```bash
curl -X POST "https://minio-file-service-6s7p.onrender.com/api/files/upload/IMAGE" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@mon-image.jpg"
```

**Réponse :**
```json
{
  "fileName": "uuid-generated-name.jpg",
  "originalFileName": "mon-image.jpg",
  "fileUrl": "https://play.min.io/minio-images/uuid-generated-name.jpg",
  "bucketName": "minio-images",
  "fileSize": 1024567,
  "contentType": "image/jpeg",
  "uploadedAt": "2025-10-01T21:00:00.000Z",
  "fileId": "unique-file-id"
}
```

### 2. 📋 Liste des Fichiers

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/list/IMAGE"
```

**Réponse :**
```json
[
  {
    "fileName": "uuid-generated-name.jpg",
    "originalFileName": null,
    "bucketName": "minio-images",
    "fileSize": 1024567,
    "contentType": null,
    "createdAt": "2025-10-01T21:00:00.000",
    "lastModified": "2025-10-01T21:00:00.000",
    "etag": "\"file-etag-hash\"",
    "width": 1920,
    "height": 1080,
    "colorSpace": "RGB"
  }
]
```

### 3. 📥 Téléchargement de Fichier

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/download/IMAGE/uuid-generated-name.jpg" \
  -o mon-fichier-telecharge.jpg
```

### 4. 🔗 URL Présignée (Accès Temporaire)

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/presigned-url/IMAGE/uuid-generated-name.jpg?expiry=3600"
```

**Réponse :**
```json
{
  "presignedUrl": "https://play.min.io/minio-images/uuid-generated-name.jpg?X-Amz-Algorithm=...",
  "expiryTime": "2025-10-01T22:00:00.000Z"
}
```

### 5. 📊 Métadonnées de Fichier

```bash
curl "https://minio-file-service-6s7p.onrender.com/api/files/metadata/IMAGE/uuid-generated-name.jpg"
```

### 6. 🗑️ Suppression de Fichier

```bash
curl -X DELETE "https://minio-file-service-6s7p.onrender.com/api/files/delete/IMAGE/uuid-generated-name.jpg"
```

## 🎨 Intégration Frontend

### JavaScript/TypeScript

```javascript
// Upload de fichier
async function uploadFile(file, fileType) {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/upload/${fileType}`, {
    method: 'POST',
    body: formData
  });
  
  return await response.json();
}

// Liste des fichiers
async function listFiles(fileType) {
  const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/list/${fileType}`);
  return await response.json();
}

// Exemple d'utilisation
const fileInput = document.getElementById('fileInput');
const file = fileInput.files[0];

uploadFile(file, 'IMAGE').then(result => {
  console.log('Fichier uploadé:', result);
});
```

### React Hook

```jsx
import { useState } from 'react';

export const useMinioUpload = () => {
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState(null);

  const uploadFile = async (file, fileType) => {
    setUploading(true);
    setError(null);
    
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/upload/${fileType}`, {
        method: 'POST',
        body: formData
      });
      
      if (!response.ok) {
        throw new Error('Upload failed');
      }
      
      const result = await response.json();
      setUploading(false);
      return result;
    } catch (err) {
      setError(err.message);
      setUploading(false);
      throw err;
    }
  };

  return { uploadFile, uploading, error };
};
```

## 🐍 Intégration Python

```python
import requests
import json

class MinioFileService:
    def __init__(self):
        self.base_url = "https://minio-file-service-6s7p.onrender.com"
    
    def upload_file(self, file_path, file_type):
        """Upload un fichier vers le service MinIO"""
        url = f"{self.base_url}/api/files/upload/{file_type}"
        
        with open(file_path, 'rb') as file:
            files = {'file': file}
            response = requests.post(url, files=files)
            
        if response.status_code == 200:
            return response.json()
        else:
            raise Exception(f"Upload failed: {response.status_code}")
    
    def list_files(self, file_type):
        """Liste les fichiers d'un type donné"""
        url = f"{self.base_url}/api/files/list/{file_type}"
        response = requests.get(url)
        
        if response.status_code == 200:
            return response.json()
        else:
            raise Exception(f"List failed: {response.status_code}")
    
    def download_file(self, file_type, file_name, save_path):
        """Télécharge un fichier"""
        url = f"{self.base_url}/api/files/download/{file_type}/{file_name}"
        response = requests.get(url)
        
        if response.status_code == 200:
            with open(save_path, 'wb') as file:
                file.write(response.content)
            return True
        else:
            raise Exception(f"Download failed: {response.status_code}")

# Exemple d'utilisation
service = MinioFileService()

# Upload
result = service.upload_file("mon-image.jpg", "IMAGE")
print(f"Fichier uploadé: {result['fileName']}")

# Liste
files = service.list_files("IMAGE")
print(f"Nombre de fichiers: {len(files)}")
```

## ☕ Intégration Java/Spring Boot

```java
@Service
public class MinioFileServiceClient {
    
    private final RestTemplate restTemplate;
    private final String baseUrl = "https://minio-file-service-6s7p.onrender.com";
    
    public MinioFileServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public FileUploadResponse uploadFile(MultipartFile file, String fileType) {
        String url = baseUrl + "/api/files/upload/" + fileType;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = 
            new HttpEntity<>(body, headers);
        
        return restTemplate.postForObject(url, requestEntity, FileUploadResponse.class);
    }
    
    public List<FileMetadata> listFiles(String fileType) {
        String url = baseUrl + "/api/files/list/" + fileType;
        
        ResponseEntity<FileMetadata[]> response = 
            restTemplate.getForEntity(url, FileMetadata[].class);
        
        return Arrays.asList(response.getBody());
    }
}
```

## 📱 Intégration Mobile

### Flutter/Dart

```dart
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:io';

class MinioFileService {
  static const String baseUrl = 'https://minio-file-service-6s7p.onrender.com';
  
  static Future<Map<String, dynamic>> uploadFile(File file, String fileType) async {
    var uri = Uri.parse('$baseUrl/api/files/upload/$fileType');
    var request = http.MultipartRequest('POST', uri);
    
    request.files.add(await http.MultipartFile.fromPath('file', file.path));
    
    var response = await request.send();
    var responseBody = await response.stream.bytesToString();
    
    if (response.statusCode == 200) {
      return json.decode(responseBody);
    } else {
      throw Exception('Upload failed: ${response.statusCode}');
    }
  }
  
  static Future<List<dynamic>> listFiles(String fileType) async {
    var response = await http.get(Uri.parse('$baseUrl/api/files/list/$fileType'));
    
    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('List failed: ${response.statusCode}');
    }
  }
}
```

## 🔧 Fonctionnalités Avancées

### 1. 📄 Traitement PDF

```bash
# Génération de thumbnail PDF
curl "https://minio-file-service-6s7p.onrender.com/api/files/pdf/thumbnail/DOCUMENT/mon-document.pdf"

# Extraction de texte PDF
curl "https://minio-file-service-6s7p.onrender.com/api/files/pdf/text/DOCUMENT/mon-document.pdf"
```

### 2. 🎵 Streaming Audio/Vidéo

```bash
# Stream d'un fichier audio
curl "https://minio-file-service-6s7p.onrender.com/api/files/stream/SONG/ma-chanson.mp3"

# Stream d'un fichier vidéo
curl "https://minio-file-service-6s7p.onrender.com/api/files/stream/VIDEO/ma-video.mp4"
```

### 3. 📊 Upload Multiple

```bash
curl -X POST "https://minio-file-service-6s7p.onrender.com/api/files/upload-multiple/IMAGE" \
  -F "files=@image1.jpg" \
  -F "files=@image2.png" \
  -F "files=@image3.gif"
```

## 🚨 Gestion d'Erreurs

### Codes de Réponse HTTP

| Code | Description | Action |
|------|-------------|--------|
| **200** | Succès | Traitement normal |
| **400** | Requête invalide | Vérifier les paramètres |
| **404** | Fichier non trouvé | Vérifier le nom/type de fichier |
| **413** | Fichier trop volumineux | Réduire la taille |
| **415** | Type de fichier non supporté | Utiliser un format supporté |
| **500** | Erreur serveur | Réessayer plus tard |

### Exemple de Gestion d'Erreur

```javascript
async function uploadFileWithErrorHandling(file, fileType) {
  try {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/upload/${fileType}`, {
      method: 'POST',
      body: formData
    });
    
    if (!response.ok) {
      switch (response.status) {
        case 400:
          throw new Error('Requête invalide - Vérifiez le fichier et le type');
        case 413:
          throw new Error('Fichier trop volumineux');
        case 415:
          throw new Error('Type de fichier non supporté');
        case 500:
          throw new Error('Erreur serveur - Réessayez plus tard');
        default:
          throw new Error(`Erreur ${response.status}: ${response.statusText}`);
      }
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur upload:', error.message);
    throw error;
  }
}
```

## 🔒 Sécurité et Bonnes Pratiques

### 1. Validation Côté Client

```javascript
function validateFile(file, maxSize, allowedTypes) {
  // Vérifier la taille
  if (file.size > maxSize) {
    throw new Error(`Fichier trop volumineux. Max: ${maxSize / 1024 / 1024}MB`);
  }
  
  // Vérifier le type
  if (!allowedTypes.includes(file.type)) {
    throw new Error(`Type de fichier non autorisé: ${file.type}`);
  }
  
  return true;
}

// Exemple d'utilisation
const imageFile = document.getElementById('imageInput').files[0];
const maxSize = 10 * 1024 * 1024; // 10MB
const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];

try {
  validateFile(imageFile, maxSize, allowedTypes);
  // Procéder à l'upload
} catch (error) {
  console.error('Validation échouée:', error.message);
}
```

### 2. Retry Logic

```javascript
async function uploadWithRetry(file, fileType, maxRetries = 3) {
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      return await uploadFile(file, fileType);
    } catch (error) {
      if (attempt === maxRetries) {
        throw error;
      }
      
      // Attendre avant de réessayer
      await new Promise(resolve => setTimeout(resolve, 1000 * attempt));
    }
  }
}
```

## 📈 Monitoring et Logs

### Health Check

```bash
# Vérifier l'état du service
curl "https://minio-file-service-6s7p.onrender.com/actuator/health"
```

**Réponse attendue :**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## 🎯 Exemples Complets

### Application Web Simple

```html
<!DOCTYPE html>
<html>
<head>
    <title>MinIO File Upload</title>
</head>
<body>
    <h1>Upload de Fichier</h1>
    
    <input type="file" id="fileInput" multiple>
    <select id="fileType">
        <option value="IMAGE">Image</option>
        <option value="DOCUMENT">Document</option>
        <option value="VIDEO">Vidéo</option>
        <option value="SONG">Audio</option>
    </select>
    <button onclick="uploadFiles()">Upload</button>
    
    <div id="results"></div>
    
    <script>
        async function uploadFiles() {
            const fileInput = document.getElementById('fileInput');
            const fileType = document.getElementById('fileType').value;
            const results = document.getElementById('results');
            
            results.innerHTML = 'Upload en cours...';
            
            try {
                for (const file of fileInput.files) {
                    const formData = new FormData();
                    formData.append('file', file);
                    
                    const response = await fetch(`https://minio-file-service-6s7p.onrender.com/api/files/upload/${fileType}`, {
                        method: 'POST',
                        body: formData
                    });
                    
                    const result = await response.json();
                    results.innerHTML += `<p>✅ ${file.name} → ${result.fileName}</p>`;
                }
            } catch (error) {
                results.innerHTML = `❌ Erreur: ${error.message}`;
            }
        }
    </script>
</body>
</html>
```

## 🚀 Déploiement et Configuration

### Variables d'Environnement

Si vous déployez votre propre instance, voici les variables requises :

```env
# Configuration MinIO
MINIO_URL=https://play.min.io
MINIO_ACCESS_KEY=Q3AM3UQ867SPQQA43P2F
MINIO_SECRET_KEY=zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG

# Configuration Spring
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Configuration des buckets
MINIO_BUCKET_SONGS=minio-songs
MINIO_BUCKET_IMAGES=minio-images
MINIO_BUCKET_VIDEOS=minio-videos
MINIO_BUCKET_PHOTOS=minio-photos
MINIO_BUCKET_DOCUMENTS=minio-documents
MINIO_BUCKET_ARCHIVES=minio-archives
MINIO_BUCKET_FILES=minio-files
```

## 📞 Support et Contact

- **Service en Production** : `https://minio-file-service-6s7p.onrender.com`
- **Documentation API** : `https://minio-file-service-6s7p.onrender.com/api-docs`
- **Health Check** : `https://minio-file-service-6s7p.onrender.com/actuator/health`

## 🎉 Conclusion

Ce service MinIO File Management offre une solution complète et robuste pour la gestion de fichiers avec :

- ✅ **API REST** simple et intuitive
- ✅ **Support multi-formats** avec validation automatique
- ✅ **Métadonnées riches** pour tous les types de fichiers
- ✅ **Traitement PDF** avancé
- ✅ **URLs présignées** pour la sécurité
- ✅ **Streaming** pour les gros fichiers
- ✅ **Documentation complète** avec Swagger

**Prêt à intégrer dans vos projets !** 🚀

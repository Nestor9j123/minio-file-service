# 🚀 Guide de Déploiement Railway - Service MinIO

## 🎯 **ÉTAPES ULTRA-RAPIDES POUR RAILWAY**

### **📋 Prérequis**
- ✅ Compte GitHub (pour connecter le repo)
- ✅ Tous les fichiers sont prêts (`Dockerfile`, `railway.json`)

---

## 🚀 **ÉTAPE 1 : PUSH SUR GITHUB**

### **1. Initialiser Git (si pas déjà fait)**
```bash
git init
git add .
git commit -m "Ready for Railway deployment - MinIO File Service"
```

### **2. Créer un repo GitHub**
1. Allez sur [github.com](https://github.com)
2. Cliquez sur "New repository"
3. Nom : `minio-file-service`
4. Public ou Private (votre choix)
5. Cliquez "Create repository"

### **3. Push le code**
```bash
git remote add origin https://github.com/VOTRE-USERNAME/minio-file-service.git
git branch -M main
git push -u origin main
```

---

## 🚀 **ÉTAPE 2 : DÉPLOYER SUR RAILWAY**

### **1. Aller sur Railway**
- Allez sur [railway.app](https://railway.app)
- Cliquez sur "Login"
- Connectez-vous avec GitHub

### **2. Créer un nouveau projet**
1. Cliquez sur "New Project"
2. Sélectionnez "Deploy from GitHub repo"
3. Autorisez Railway à accéder à vos repos GitHub
4. Sélectionnez votre repo `minio-file-service`

### **3. Railway détecte automatiquement**
- ✅ Railway voit le `Dockerfile`
- ✅ Railway voit le `railway.json`
- ✅ Le build commence automatiquement !

---

## 🚀 **ÉTAPE 3 : CONFIGURER LES VARIABLES D'ENVIRONNEMENT**

### **Dans Railway Dashboard :**

1. Cliquez sur votre projet
2. Allez dans l'onglet "Variables"
3. Ajoutez ces variables :

```bash
MINIO_URL=https://play.min.io
MINIO_ACCESS_KEY=Q3AM3UQ867SPQQA43P2F
MINIO_SECRET_KEY=zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG
SPRING_PROFILES_ACTIVE=prod
```

### **4. Redéployer (optionnel)**
- Cliquez sur "Deploy" pour redéployer avec les nouvelles variables

---

## 🎉 **ÉTAPE 4 : VOTRE SERVICE EST DÉPLOYÉ !**

### **URLs finales :**
- **Service** : `https://votre-projet.up.railway.app`
- **Swagger** : `https://votre-projet.up.railway.app/swagger-ui.html`
- **Health Check** : `https://votre-projet.up.railway.app/actuator/health`

### **Railway vous donne automatiquement :**
- ✅ **Domaine HTTPS** gratuit
- ✅ **SSL/TLS** automatique
- ✅ **Logs** en temps réel
- ✅ **Métriques** de performance
- ✅ **Auto-redéploiement** sur push GitHub

---

## 🎯 **CE QUE VOUS DONNEZ AUX BACKENDS**

### **1. URL du service déployé**
```
https://votre-projet.up.railway.app
```

### **2. Documentation Swagger**
```
https://votre-projet.up.railway.app/swagger-ui.html
```

### **3. SDK Client Maven**
```xml
<dependency>
    <groupId>com.fileservice</groupId>
    <artifactId>minio-client-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### **4. Exemple d'utilisation**
```java
@Service
public class FileService {
    private final MinioFileClient fileClient = new MinioFileClient("https://votre-projet.up.railway.app");
    
    public String uploadPhoto(byte[] photoData, String fileName) {
        FileUploadResponse response = fileClient.uploadFile(
            photoData, fileName, FileType.PHOTO
        );
        return response.getFileUrl();
    }
}
```

---

## 🔧 **AVANTAGES DE RAILWAY**

- ✅ **500 heures gratuites/mois** + $5 de crédit
- ✅ **Déploiement automatique** depuis GitHub
- ✅ **Pas de mise en veille** (contrairement à Heroku)
- ✅ **Domaine HTTPS gratuit**
- ✅ **Interface simple** et intuitive
- ✅ **Logs en temps réel**
- ✅ **Scaling automatique**

---

## 🎉 **RÉSUMÉ**

**En 3 étapes simples :**

1. **Push sur GitHub** → Votre code est en ligne
2. **Connecter à Railway** → Déploiement automatique
3. **Configurer les variables** → Service opérationnel

**Total temps : 5-10 minutes !** ⚡

**Votre service MinIO sera accessible 24/7 gratuitement !** 🎉

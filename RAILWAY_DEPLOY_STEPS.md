# 🚀 Guide de Déploiement Railway - MinIO File Service

## **Étape 1 : Préparer le Code**

✅ **Votre projet est déjà prêt !** Tous les fichiers nécessaires sont configurés.

## **Étape 2 : Pousser sur GitHub**

```bash
# Si ce n'est pas déjà fait, initialisez Git
git init
git add .
git commit -m "Initial commit - MinIO File Service ready for Railway"

# Créez un repository sur GitHub et poussez le code
git remote add origin https://github.com/VOTRE_USERNAME/minio-file-service.git
git branch -M main
git push -u origin main
```

## **Étape 3 : Créer un Compte Railway**

1. Allez sur **[railway.app](https://railway.app)**
2. Cliquez sur **"Start a New Project"**
3. Connectez-vous avec **GitHub**
4. Autorisez Railway à accéder à vos repositories

## **Étape 4 : Déployer le Projet**

1. Dans Railway Dashboard, cliquez **"New Project"**
2. Sélectionnez **"Deploy from GitHub repo"**
3. Choisissez votre repository **`minio-file-service`**
4. Railway détecte automatiquement le **Dockerfile** ✅
5. Le build commence automatiquement !

## **Étape 5 : Configuration MinIO Cloud (GRATUIT)**

### **Option A : MinIO Cloud (Recommandé)**
1. Allez sur **[min.io](https://min.io)** 
2. Créez un compte gratuit (**10 GB gratuit**)
3. Créez un nouveau projet
4. Notez vos credentials :
   - **Endpoint URL**
   - **Access Key**
   - **Secret Key**

### **Option B : Supabase Storage (Alternative)**
1. Allez sur **[supabase.com](https://supabase.com)**
2. Créez un projet gratuit (**1 GB gratuit**)
3. Activez le Storage
4. Utilisez l'API S3-compatible

## **Étape 6 : Variables d'Environnement Railway**

Dans Railway Dashboard → Votre projet → **Variables** :

```bash
# MinIO Configuration
MINIO_URL=https://your-minio-endpoint.min.io
MINIO_ACCESS_KEY=your-access-key
MINIO_SECRET_KEY=your-secret-key

# Spring Configuration
SPRING_PROFILES_ACTIVE=prod
PORT=8080

# Buckets (optionnel - utilise les défauts si non spécifié)
MINIO_BUCKET_SONGS=minio-songs
MINIO_BUCKET_IMAGES=minio-images
MINIO_BUCKET_VIDEOS=minio-videos
MINIO_BUCKET_PHOTOS=minio-photos
MINIO_BUCKET_DOCUMENTS=minio-documents
MINIO_BUCKET_ARCHIVES=minio-archives
MINIO_BUCKET_FILES=minio-files
```

## **Étape 7 : Redéployer avec les Variables**

1. Après avoir ajouté les variables, cliquez **"Deploy"**
2. Railway redéploie automatiquement avec la nouvelle configuration
3. Attendez que le status passe à **"Active"** ✅

## **Étape 8 : Tester le Déploiement**

Votre service sera disponible sur :
```
https://votre-app-name.up.railway.app
```

### **URLs de Test :**
- **Health Check** : `https://votre-app.up.railway.app/actuator/health`
- **Swagger UI** : `https://votre-app.up.railway.app/swagger-ui.html`
- **API Docs** : `https://votre-app.up.railway.app/api-docs`

### **Test d'Upload :**
```bash
curl -X POST "https://votre-app.up.railway.app/api/files/upload/IMAGE" \
  -F "file=@test-image.jpg"
```

## **🎉 Félicitations !**

Votre service MinIO est maintenant déployé gratuitement sur Railway !

### **Ressources Gratuites :**
- **Railway** : 500 heures/mois + $5 crédit
- **MinIO Cloud** : 10 GB gratuit
- **Total** : 100% GRATUIT ! 🎉

### **URLs Importantes :**
- **Service** : `https://votre-app.up.railway.app`
- **Documentation** : `https://votre-app.up.railway.app/swagger-ui.html`
- **Health** : `https://votre-app.up.railway.app/actuator/health`

## **🔧 Dépannage**

### **Si le build échoue :**
1. Vérifiez les logs dans Railway Dashboard
2. Assurez-vous que le Dockerfile est à la racine
3. Vérifiez que `railway.json` est configuré

### **Si le service ne démarre pas :**
1. Vérifiez les variables d'environnement MinIO
2. Testez la connexion MinIO avec les credentials
3. Vérifiez les logs d'application

### **Support :**
- **Railway Docs** : [docs.railway.app](https://docs.railway.app)
- **MinIO Docs** : [min.io/docs](https://min.io/docs)

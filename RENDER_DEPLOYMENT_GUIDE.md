# 🚀 Guide de Déploiement Render - MinIO File Service

## 📋 Vue d'ensemble

Ce guide vous explique comment déployer votre MinIO File Service sur Render avec **persistance des données** en utilisant **Alpine Linux** pour une image optimisée.

### 🎯 Avantages de cette configuration

- ✅ **Image Alpine Linux** : Taille réduite (~150MB vs ~500MB)
- ✅ **Persistance complète** : Données MinIO et métadonnées sauvegardées
- ✅ **Multi-stage build** : Optimisation maximale
- ✅ **Sécurité renforcée** : Utilisateur non-root
- ✅ **Monitoring intégré** : Health checks et métriques
- ✅ **Configuration production** : Logs, compression, HTTP/2

## 🏗️ Architecture de Déploiement

```
┌─────────────────────────────────────────────────────────────┐
│                        RENDER CLOUD                        │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │
│  │  MinIO Service  │  │  MinIO Storage  │  │ PostgreSQL  │  │
│  │   (Alpine)      │  │   (Persistent)  │  │    (DB)     │  │
│  │                 │  │                 │  │             │  │
│  │ Port: 8080      │  │ Port: 9000/9001 │  │ Port: 5432  │  │
│  │ Disk: /app/data │  │ Disk: /data     │  │ Disk: 5GB   │  │
│  │ Size: 10GB      │  │ Size: 20GB+     │  │             │  │
│  └─────────────────┘  └─────────────────┘  └─────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Étapes de Déploiement

### 1. Préparation du Code

```bash
# Cloner et préparer le projet
git clone your-repo
cd minio-file-service

# Vérifier les fichiers de déploiement
ls -la Dockerfile.alpine render.yaml
```

### 2. Configuration des Secrets

Sur Render Dashboard, configurez ces variables d'environnement :

#### 🔐 Secrets Obligatoires
```env
MINIO_ACCESS_KEY=your_secure_access_key
MINIO_SECRET_KEY=your_secure_secret_key_min_8_chars
MINIO_ROOT_USER=admin_user
MINIO_ROOT_PASSWORD=secure_password_min_8_chars
```

#### 🌐 Configuration MinIO
```env
MINIO_URL=https://your-minio-storage.onrender.com
MINIO_BUCKET_IMAGES=prod-images
MINIO_BUCKET_DOCUMENTS=prod-documents
MINIO_BUCKET_VIDEOS=prod-videos
# ... autres buckets
```

#### 🗄️ Base de Données (optionnel)
```env
DATABASE_URL=postgresql://user:pass@host:5432/dbname
```

### 3. Déploiement Automatique

#### Option A : Utiliser render.yaml (Recommandé)

1. **Pousser le code :**
```bash
git add .
git commit -m "Add Render deployment with Alpine Linux"
git push origin main
```

2. **Sur Render Dashboard :**
   - Créer un nouveau service
   - Connecter votre repository
   - Render détectera automatiquement `render.yaml`
   - Configurer les secrets dans l'interface

#### Option B : Configuration Manuelle

1. **Créer un Web Service :**
   - Environment: `Docker`
   - Dockerfile Path: `./Dockerfile.alpine`
   - Build Command: (laisser vide)
   - Start Command: (laisser vide)

2. **Configurer les disques persistants :**
   - Nom: `minio-data`
   - Mount Path: `/app/data`
   - Taille: `10GB` (ajustable)

### 4. Configuration MinIO Storage Séparé

1. **Créer un second Web Service pour MinIO :**
```yaml
name: minio-storage
environment: docker
dockerCommand: |
  minio/minio:RELEASE.2023-09-04T19-57-37Z server /data --console-address ":9001"
```

2. **Configurer le disque persistant :**
   - Mount Path: `/data`
   - Taille: `20GB+` selon vos besoins

## 🔧 Configuration Avancée

### Health Checks
```yaml
healthCheckPath: /actuator/health
```

### Monitoring
- **Métriques** : `/actuator/metrics`
- **Prometheus** : `/actuator/prometheus`
- **Logs** : Disponibles dans Render Dashboard

### Optimisations Performance

#### Variables JVM
```env
JAVA_OPTS=-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport
```

#### Configuration Spring
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 500MB
```

## 📊 Surveillance et Maintenance

### 1. Monitoring
- **Health Check** : `https://your-app.onrender.com/actuator/health`
- **Métriques** : `https://your-app.onrender.com/actuator/metrics`
- **Logs** : Render Dashboard → Service → Logs

### 2. Gestion des Données
```bash
# Vérifier l'espace disque
curl https://your-app.onrender.com/actuator/health

# Monitoring des buckets MinIO
curl https://your-minio.onrender.com/minio/health/live
```

### 3. Sauvegarde
- **Automatique** : Render sauvegarde les disques persistants
- **Manuelle** : Exporter les données via API MinIO

## 🛡️ Sécurité

### 1. Bonnes Pratiques
- ✅ Utilisateur non-root dans le conteneur
- ✅ Secrets gérés par Render
- ✅ HTTPS automatique
- ✅ Validation des fichiers uploadés

### 2. Configuration Réseau
```yaml
# Render configure automatiquement :
- HTTPS/TLS
- Load balancing
- DDoS protection
```

## 🚨 Dépannage

### Problèmes Courants

#### 1. Erreur de Démarrage
```bash
# Vérifier les logs
# Render Dashboard → Service → Logs

# Causes communes :
- Variables d'environnement manquantes
- Problème de connexion MinIO
- Manque de mémoire
```

#### 2. Problème de Persistance
```bash
# Vérifier le montage du disque
# Dans les logs, chercher : "Disk space health check"

# Solutions :
- Vérifier la configuration du disque
- Augmenter la taille si nécessaire
```

#### 3. Problème MinIO
```bash
# Tester la connexion MinIO
curl -f https://your-minio.onrender.com/minio/health/live

# Vérifier les credentials
# Variables : MINIO_ACCESS_KEY, MINIO_SECRET_KEY
```

## 💰 Coûts Estimés

### Configuration Recommandée
- **Web Service** (Starter) : $7/mois
- **MinIO Storage** (Starter) : $7/mois  
- **PostgreSQL** (Starter) : $7/mois
- **Disques Persistants** : $0.25/GB/mois

**Total estimé** : ~$25-35/mois pour une configuration complète

### Optimisations Coût
- Commencer avec des plans Starter
- Ajuster les tailles de disque selon l'usage
- Utiliser une seule instance pour dev/test

## 🎯 Checklist de Déploiement

- [ ] Code poussé sur Git
- [ ] Dockerfile.alpine testé localement
- [ ] Variables d'environnement configurées
- [ ] Secrets Render créés
- [ ] Disques persistants configurés
- [ ] Health checks fonctionnels
- [ ] Tests de upload/download
- [ ] Monitoring activé
- [ ] Sauvegarde configurée

## 📞 Support

### Ressources
- [Documentation Render](https://render.com/docs)
- [MinIO Documentation](https://docs.min.io/)
- [Spring Boot on Render](https://render.com/docs/deploy-spring-boot)

### Logs Utiles
```bash
# Application logs
tail -f /app/logs/minio-file-service.log

# Health check
curl https://your-app.onrender.com/actuator/health

# MinIO status
curl https://your-minio.onrender.com/minio/health/ready
```

---

🎉 **Félicitations !** Votre MinIO File Service est maintenant déployé sur Render avec persistance complète et optimisation Alpine Linux !

La configuration est prête pour la production avec monitoring, sécurité et performance optimisés. 🚀

# 🆓 Guide de Déploiement Gratuit - Service MinIO

## 🏆 **OPTION 1 : RAILWAY (RECOMMANDÉE)**

### **Pourquoi Railway ?**
- ✅ **500 heures gratuites/mois** + $5 de crédit
- ✅ **Déploiement ultra-simple** depuis GitHub
- ✅ **Base de données gratuite** incluse
- ✅ **Domaine gratuit** : `votre-app.up.railway.app`
- ✅ **Logs et monitoring** intégrés

### **Étapes de déploiement :**

#### **1. Préparer le projet pour Railway**
```bash
# Créer un fichier railway.json
echo '{
  "build": {
    "builder": "DOCKERFILE"
  },
  "deploy": {
    "startCommand": "java -jar target/minio-0.0.1-SNAPSHOT.jar"
  }
}' > railway.json
```

#### **2. Optimiser le Dockerfile pour Railway**
```dockerfile
# Dockerfile optimisé pour Railway
FROM openjdk:21-jdk-slim

# Variables d'environnement pour Railway
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Copier le JAR
COPY target/minio-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port
EXPOSE $PORT

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "/app.jar", "--server.port=${PORT}"]
```

#### **3. Déployer sur Railway**
1. Allez sur [railway.app](https://railway.app)
2. Connectez votre compte GitHub
3. Cliquez sur "New Project" → "Deploy from GitHub repo"
4. Sélectionnez votre repository MinIO
5. Railway détecte automatiquement le Dockerfile
6. **C'est tout !** 🎉

#### **4. Configuration des variables d'environnement**
Dans Railway Dashboard → Variables :
```bash
MINIO_URL=https://your-minio-instance.com
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123
SPRING_PROFILES_ACTIVE=prod
PORT=8080
```

---

## 🚀 **OPTION 2 : RENDER**

### **Étapes de déploiement :**

#### **1. Créer un render.yaml**
```yaml
services:
  - type: web
    name: minio-service
    env: java
    buildCommand: mvn clean package
    startCommand: java -jar target/minio-0.0.1-SNAPSHOT.jar
    envVars:
      - key: MINIO_URL
        value: https://your-minio-instance.com
      - key: MINIO_ACCESS_KEY
        value: minioadmin
      - key: MINIO_SECRET_KEY
        value: minioadmin123
      - key: SPRING_PROFILES_ACTIVE
        value: prod
```

#### **2. Déployer sur Render**
1. Allez sur [render.com](https://render.com)
2. Connectez GitHub
3. New → Web Service
4. Sélectionnez votre repo
5. Render détecte automatiquement Java
6. **Déployé !** 🎉

---

## ☁️ **OPTION 3 : FLY.IO (Déjà configuré !)**

Vous avez déjà le `fly.toml` configuré ! Il suffit de :

```bash
# Installer Fly CLI
curl -L https://fly.io/install.sh | sh

# Se connecter
flyctl auth login

# Déployer (GRATUIT pour 3 apps)
flyctl deploy
```

---

## 🐙 **OPTION 4 : HEROKU**

### **Créer un Procfile**
```
web: java -jar target/minio-0.0.1-SNAPSHOT.jar --server.port=$PORT
```

### **Déployer**
```bash
# Installer Heroku CLI
# Puis :
heroku create votre-minio-service
git push heroku main
```

---

## 🎯 **MA RECOMMANDATION FINALE**

### **Pour débuter rapidement : RAILWAY**

**Pourquoi ?**
- ✅ **Le plus généreux** en version gratuite
- ✅ **Le plus simple** à configurer
- ✅ **Pas de mise en veille** automatique
- ✅ **Domaine HTTPS** automatique

### **Étapes ultra-rapides :**

1. **Push votre code sur GitHub**
2. **Allez sur railway.app**
3. **"Deploy from GitHub"**
4. **Sélectionnez votre repo**
5. **Ajoutez les variables d'environnement**
6. **C'est déployé !** 🎉

### **URL finale :**
Votre service sera accessible sur :
`https://votre-minio-service.up.railway.app`

Et la documentation Swagger sur :
`https://votre-minio-service.up.railway.app/swagger-ui.html`

---

## 💡 **CONSEIL POUR LE STOCKAGE MINIO**

Pour MinIO lui-même (le stockage), vous pouvez utiliser :

### **Option 1 : MinIO Cloud (Gratuit)**
- **10 GB gratuits** sur MinIO Cloud
- **URL** : [min.io](https://min.io)

### **Option 2 : AWS S3 Free Tier**
- **5 GB gratuits** pendant 12 mois
- Compatible avec MinIO client

### **Option 3 : Supabase Storage**
- **1 GB gratuit** permanent
- API compatible S3

---

## 🎉 **RÉSUMÉ**

**Pour déployer GRATUITEMENT votre service MinIO :**

1. **Service MinIO** → **Railway** (recommandé)
2. **Stockage** → **MinIO Cloud** (10 GB gratuit)
3. **URL finale** → `https://votre-service.up.railway.app`
4. **Swagger** → `https://votre-service.up.railway.app/swagger-ui.html`

**Total : 100% GRATUIT !** 🎉

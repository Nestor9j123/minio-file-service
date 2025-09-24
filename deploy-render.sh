#!/bin/bash

# Script de déploiement pour Render
# Usage: ./deploy-render.sh

set -e

echo "🚀 Déploiement MinIO File Service sur Render avec Alpine Linux"
echo "============================================================="

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "pom.xml" ]; then
    log_error "Erreur: pom.xml non trouvé. Exécutez ce script depuis la racine du projet."
    exit 1
fi

# Vérifier la présence des fichiers nécessaires
log_info "Vérification des fichiers de déploiement..."

if [ ! -f "Dockerfile.alpine" ]; then
    log_error "Dockerfile.alpine manquant"
    exit 1
fi

if [ ! -f "render.yaml" ]; then
    log_error "render.yaml manquant"
    exit 1
fi

log_success "Tous les fichiers de déploiement sont présents"

# Construire l'image Docker localement pour tester
log_info "Construction de l'image Docker Alpine..."
docker build -f Dockerfile.alpine -t minio-file-service:alpine .

if [ $? -eq 0 ]; then
    log_success "Image Docker construite avec succès"
else
    log_error "Échec de la construction de l'image Docker"
    exit 1
fi

# Vérifier la taille de l'image
log_info "Vérification de la taille de l'image..."
IMAGE_SIZE=$(docker images minio-file-service:alpine --format "table {{.Size}}" | tail -n 1)
log_info "Taille de l'image Alpine: $IMAGE_SIZE"

# Test local de l'image (optionnel)
read -p "Voulez-vous tester l'image localement ? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    log_info "Démarrage du conteneur de test..."
    docker run -d --name minio-test -p 8080:8080 \
        -e SPRING_PROFILES_ACTIVE=dev \
        minio-file-service:alpine
    
    log_info "Attente du démarrage de l'application..."
    sleep 30
    
    # Test de santé
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        log_success "Application démarrée avec succès"
    else
        log_warning "L'application pourrait avoir des problèmes de démarrage"
    fi
    
    # Nettoyer le conteneur de test
    docker stop minio-test && docker rm minio-test
fi

# Instructions de déploiement
echo
log_info "Instructions de déploiement sur Render:"
echo "========================================"
echo
echo "1. 📁 Pousser votre code sur GitHub/GitLab"
echo "   git add ."
echo "   git commit -m 'Add Render deployment configuration'"
echo "   git push origin main"
echo
echo "2. 🌐 Aller sur https://render.com et créer un nouveau service"
echo
echo "3. 🔗 Connecter votre repository Git"
echo
echo "4. 📋 Configurer les variables d'environnement suivantes:"
echo "   MINIO_ACCESS_KEY=votre_access_key"
echo "   MINIO_SECRET_KEY=votre_secret_key"
echo "   MINIO_URL=https://votre-instance-minio.com"
echo "   MINIO_ROOT_USER=votre_root_user"
echo "   MINIO_ROOT_PASSWORD=votre_root_password"
echo
echo "5. 🐳 Utiliser le fichier render.yaml pour la configuration automatique"
echo
echo "6. 💾 Configurer les disques persistants:"
echo "   - Service principal: /app/data (10GB)"
echo "   - MinIO storage: /data (20GB+)"
echo
echo "7. 🚀 Déployer!"

# Afficher les informations importantes
echo
log_warning "IMPORTANT - Configuration de persistance:"
echo "=========================================="
echo "• Les données MinIO seront stockées dans /data avec persistance"
echo "• Les logs de l'application dans /app/logs"
echo "• Base de données PostgreSQL séparée pour les métadonnées"
echo "• Health checks configurés sur /actuator/health"
echo "• Monitoring disponible via /actuator/metrics"
echo
log_warning "SÉCURITÉ:"
echo "• Changez les mots de passe par défaut"
echo "• Utilisez des secrets Render pour les clés sensibles"
echo "• L'application s'exécute avec un utilisateur non-root"
echo
log_success "Configuration de déploiement Render prête!"
echo "Taille optimisée avec Alpine Linux pour des déploiements rapides 🏔️"

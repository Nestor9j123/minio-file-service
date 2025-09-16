#!/bin/bash

# Script de déploiement pour MinIO File Service
# Usage: ./deploy.sh [environment]
# Environments: local, fly, docker

set -e

ENVIRONMENT=${1:-local}
APP_NAME="minio-file-service"

echo "🚀 Déploiement de $APP_NAME en mode: $ENVIRONMENT"

# Fonction pour vérifier les prérequis
check_prerequisites() {
    echo "📋 Vérification des prérequis..."
    
    if ! command -v java &> /dev/null; then
        echo "❌ Java n'est pas installé"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven n'est pas installé"
        exit 1
    fi
    
    echo "✅ Prérequis vérifiés"
}

# Fonction pour compiler le projet
build_project() {
    echo "🔨 Compilation du projet..."
    mvn clean package -DskipTests
    echo "✅ Compilation terminée"
}

# Fonction pour déploiement local
deploy_local() {
    echo "🏠 Déploiement local..."
    
    # Vérifier si MinIO est en cours d'exécution
    if ! curl -f http://localhost:9000/minio/health/live &> /dev/null; then
        echo "⚠️  MinIO ne semble pas être en cours d'exécution sur localhost:9000"
        echo "💡 Démarrez MinIO avec Docker:"
        echo "   docker run -p 9000:9000 -p 9001:9001 \\"
        echo "     -e \"MINIO_ROOT_USER=minioadmin\" \\"
        echo "     -e \"MINIO_ROOT_PASSWORD=minioadmin\" \\"
        echo "     minio/minio server /data --console-address \":9001\""
        echo ""
    fi
    
    # Démarrer l'application
    echo "🚀 Démarrage de l'application..."
    java -jar target/*.jar
}

# Fonction pour déploiement Docker
deploy_docker() {
    echo "🐳 Déploiement Docker..."
    
    # Build de l'image Docker
    echo "📦 Construction de l'image Docker..."
    docker build -t $APP_NAME .
    
    # Démarrage du container
    echo "🚀 Démarrage du container..."
    docker run -d \
        --name $APP_NAME \
        -p 8080:8080 \
        -p 8081:8081 \
        -e MINIO_URL=${MINIO_URL:-http://host.docker.internal:9000} \
        -e MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY:-minioadmin} \
        -e MINIO_SECRET_KEY=${MINIO_SECRET_KEY:-minioadmin} \
        $APP_NAME
    
    echo "✅ Application déployée sur http://localhost:8080"
    echo "📚 Documentation API: http://localhost:8080/swagger-ui.html"
}

# Fonction pour déploiement Fly.io
deploy_fly() {
    echo "✈️  Déploiement sur Fly.io..."
    
    if ! command -v fly &> /dev/null; then
        echo "❌ Fly CLI n'est pas installé"
        echo "💡 Installez avec: curl -L https://fly.io/install.sh | sh"
        exit 1
    fi
    
    # Vérifier la connexion
    if ! fly auth whoami &> /dev/null; then
        echo "❌ Vous n'êtes pas connecté à Fly.io"
        echo "💡 Connectez-vous avec: fly auth login"
        exit 1
    fi
    
    # Déploiement
    echo "🚀 Déploiement sur Fly.io..."
    fly deploy
    
    echo "✅ Application déployée sur Fly.io"
    echo "🌐 URL: https://$APP_NAME.fly.dev"
    echo "📚 Documentation API: https://$APP_NAME.fly.dev/swagger-ui.html"
}

# Fonction pour afficher l'aide
show_help() {
    echo "Usage: $0 [environment]"
    echo ""
    echo "Environments disponibles:"
    echo "  local   - Déploiement local (défaut)"
    echo "  docker  - Déploiement avec Docker"
    echo "  fly     - Déploiement sur Fly.io"
    echo ""
    echo "Variables d'environnement (optionnelles):"
    echo "  MINIO_URL        - URL du serveur MinIO"
    echo "  MINIO_ACCESS_KEY - Clé d'accès MinIO"
    echo "  MINIO_SECRET_KEY - Clé secrète MinIO"
    echo ""
    echo "Exemples:"
    echo "  $0 local"
    echo "  $0 docker"
    echo "  $0 fly"
}

# Fonction principale
main() {
    case $ENVIRONMENT in
        "local")
            check_prerequisites
            build_project
            deploy_local
            ;;
        "docker")
            check_prerequisites
            build_project
            deploy_docker
            ;;
        "fly")
            check_prerequisites
            build_project
            deploy_fly
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        *)
            echo "❌ Environnement non reconnu: $ENVIRONMENT"
            show_help
            exit 1
            ;;
    esac
}

# Exécution du script
main

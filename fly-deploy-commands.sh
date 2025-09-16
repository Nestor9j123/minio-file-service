#!/bin/bash

# Script de déploiement Fly.io pour le service MinIO
echo "🚀 Déploiement du service MinIO sur Fly.io"

# Ajouter le PATH pour flyctl
export PATH="/home/devnestor/.fly/bin:$PATH"

echo "📋 Configuration des variables d'environnement..."

# Configurer les variables d'environnement pour Fly.io
flyctl secrets set \
  MINIO_URL="https://play.min.io" \
  MINIO_ACCESS_KEY="Q3AM3UQ867SPQQA43P2F" \
  MINIO_SECRET_KEY="zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG" \
  SPRING_PROFILES_ACTIVE="prod"

echo " Variables d'environnement configurées !"

echo " Déploiement de l'application..."

# Déployer l'application
flyctl deploy

echo "✅ Déploiement Fly.io terminé !"
echo "🌐 Votre service sera accessible sur : https://minio-file-service.fly.dev"
echo "📚 Documentation Swagger : https://minio-file-service.fly.dev/swagger-ui.html"

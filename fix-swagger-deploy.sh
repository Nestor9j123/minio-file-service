#!/bin/bash

# Script pour corriger et redéployer le service MinIO avec Swagger fixé
echo "🔧 Correction de la configuration Swagger et redéploiement..."

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "pom.xml" ]; then
    echo "❌ Erreur: Ce script doit être exécuté depuis la racine du projet MinIO"
    exit 1
fi

echo "✅ Répertoire du projet vérifié"

# Compiler le projet pour vérifier qu'il n'y a pas d'erreurs
echo "🔨 Compilation du projet..."
./mvnw clean compile -q

if [ $? -eq 0 ]; then
    echo "✅ Compilation réussie"
else
    echo "❌ Erreur de compilation"
    exit 1
fi

# Créer le JAR
echo "📦 Création du JAR..."
./mvnw clean package -DskipTests -q

if [ $? -eq 0 ]; then
    echo "✅ JAR créé avec succès"
else
    echo "❌ Erreur lors de la création du JAR"
    exit 1
fi

echo ""
echo "🎉 Corrections appliquées avec succès !"
echo ""
echo "📋 Résumé des corrections :"
echo "  ✅ Suppression du groupe 'actuator' dans SwaggerConfig"
echo "  ✅ Correction de l'URL du serveur de production"
echo "  ✅ Configuration Swagger simplifiée"
echo ""
echo "🚀 Prochaines étapes :"
echo "  1. Commitez les changements : git add . && git commit -m 'Fix Swagger configuration'"
echo "  2. Poussez vers votre repo : git push"
echo "  3. Render redéploiera automatiquement"
echo "  4. Testez Swagger UI : https://minio-file-service-6s7p.onrender.com/swagger-ui.html"
echo ""
echo "⏱️  Le redéploiement prendra environ 2-3 minutes"

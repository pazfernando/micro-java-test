#!/bin/bash

# Variables (reemplaza con tus propios valores)
AWS_REGION="us-east-1"
AWS_ACCOUNT_ID="123456789012" # Reemplaza con tu ID de cuenta
ECR_REPOSITORY_NAME="db-connector"

# Construir la aplicación con Maven
echo "🔨 Construyendo la aplicación con Maven..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "❌ Error al construir con Maven"
    exit 1
fi

# Construir la imagen Docker
echo "🐳 Construyendo la imagen Docker..."
docker build -t ${ECR_REPOSITORY_NAME}:latest .

if [ $? -ne 0 ]; then
    echo "❌ Error al construir la imagen Docker"
    exit 1
fi

# Autenticar con ECR
echo "🔑 Autenticando con Amazon ECR..."
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

if [ $? -ne 0 ]; then
    echo "❌ Error al autenticar con ECR"
    exit 1
fi

# Verificar si el repositorio ya existe, si no, crearlo
aws ecr describe-repositories --repository-names ${ECR_REPOSITORY_NAME} --region ${AWS_REGION} > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "📦 Creando repositorio en ECR..."
    aws ecr create-repository --repository-name ${ECR_REPOSITORY_NAME} --region ${AWS_REGION}
fi

# Etiquetar la imagen para ECR
REMOTE_IMAGE="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY_NAME}:latest"
echo "🏷️ Etiquetando la imagen: ${REMOTE_IMAGE}"
docker tag ${ECR_REPOSITORY_NAME}:latest ${REMOTE_IMAGE}

# Subir la imagen a ECR
echo "⬆️ Subiendo imagen a ECR..."
docker push ${REMOTE_IMAGE}

if [ $? -eq 0 ]; then
    echo "✅ Imagen subida correctamente a ECR: ${REMOTE_IMAGE}"
else
    echo "❌ Error al subir la imagen a ECR"
    exit 1
fi

echo "🚀 Proceso completado con éxito!"
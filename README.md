# DB Connector - Spring Boot Application

Esta aplicación Spring Boot permite probar conexiones a bases de datos PostgreSQL y DocumentDB.

## Requisitos

- Java 11+
- Maven 3.6+
- Docker (para construir y ejecutar el contenedor)

## Construcción de la aplicación

Para construir la aplicación con Maven:

```bash
mvn clean package
```

## Construcción de la imagen Docker

Para construir la imagen Docker:

```bash
docker build -t hello-world .
```

## Ejecución local

Para ejecutar la aplicación localmente:

```bash
docker run -p 8090:8080 \
  -e PG_HOST=your-postgres-host \
  -e PG_PORT=5432 \
  -e PG_DBNAME=postgres \
  -e PG_USER=postgres \
  -e PG_PASSWORD=your-password \
  -e PG_PROXY_ENDPOINT=your-proxy-endpoint \
  -e DOCDB_HOST=your-docdb-host \
  -e DOCDB_PORT=27017 \
  -e DOCDB_DBNAME=admin \
  -e DOCDB_USER=docdb-user \
  -e DOCDB_PASSWORD=your-docdb-password \
  hello-world
```

## Subir a Amazon ECR

1. Autenticarse en ECR:
```bash
aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <aws-account-id>.dkr.ecr.<region>.amazonaws.com
```

2. Crear un repositorio en ECR (si no existe):
```bash
aws ecr create-repository --repository-name hello-world
```

3. Etiquetar la imagen:
```bash
docker tag hello-world:latest <aws-account-id>.dkr.ecr.<region>.amazonaws.com/hello-world:latest
```

4. Enviar la imagen a ECR:
```bash
docker push <aws-account-id>.dkr.ecr.<region>.amazonaws.com/hello-world:latest
```

## API Endpoints

- `GET /api/db-test?dbType=all` - Probar todas las conexiones de bases de datos
- `GET /api/db-test?dbType=postgres` - Probar solo la conexión PostgreSQL
- `GET /api/db-test?dbType=postgres-proxy` - Probar solo la conexión PostgreSQL Proxy
- `GET /api/db-test?dbType=documentdb` - Probar solo la conexión DocumentDB
- `GET /api/db-test?dbType=none` - No ejecutar ninguna prueba

## Variables de entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|------------------|
| PG_HOST | Host de PostgreSQL | localhost |
| PG_PORT | Puerto de PostgreSQL | 5432 |
| PG_DBNAME | Nombre de la base de datos PostgreSQL | postgres |
| PG_USER | Usuario de PostgreSQL | postgres |
| PG_PASSWORD | Contraseña de PostgreSQL | postgres |
| PG_PROXY_ENDPOINT | Endpoint del proxy PostgreSQL | localhost |
| DOCDB_HOST | Host de DocumentDB | localhost |
| DOCDB_PORT | Puerto de DocumentDB | 27017 |
| DOCDB_DBNAME | Nombre de la base de datos DocumentDB | admin |
| DOCDB_USER | Usuario de DocumentDB | root |
| DOCDB_PASSWORD | Contraseña de DocumentDB | password |
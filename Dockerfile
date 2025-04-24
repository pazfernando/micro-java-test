FROM amazoncorretto:11-alpine

# Configura timezone
RUN apk add --no-cache tzdata
ENV TZ=America/Mexico_City

WORKDIR /app

# Copia el archivo JAR construido a la imagen
COPY target/db-connector-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto por la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]